package com.example.carduino.speedlimit;

import java.io.File;
import java.io.IOException;
import java.io.RandomAccessFile;
import java.nio.ByteOrder;
import java.nio.MappedByteBuffer;
import java.nio.channels.FileChannel;
import java.util.HashMap;
import java.util.Map;

/**
 * Lookup del limite di velocità corrente + disambiguazione cavalcavia,
 * a partire dal file "speedlimits.bin" generato da SpeedLimitExporter.java (PC-side).
 *
 * Versione ottimizzata per grafi grandi (centinaia di migliaia di segmenti):
 * - Nessun oggetto Segment per elemento: tutto in array primitivi paralleli.
 * - Grid index in formato CSR (counting sort): una sola entry di HashMap per
 *   cella, non una ArrayList<Integer> per cella con Integer boxati.
 * - Lettura file tramite memory-mapping, niente readFloat() chiamato milioni di volte.
 *
 * Nessuna dipendenza da GraphHopper: niente Janino, niente incompatibilità Android.
 */
public class SpeedLimitManager {

    private static final double CELL_SIZE_DEG = 0.01;
    private static final double BASE_SEARCH_RADIUS_M = 30.0; // margine minimo per errore di mappatura/curve
    private static final double MAX_SEARCH_RADIUS_M = 80.0;  // tetto di sicurezza: oltre non ha senso, rischi strade parallele
    private static final double MAX_BEARING_DIFF_DEG = 45.0;

    // --- Dati segmenti, array primitivi paralleli (indice comune) ---
    private float[] lat1, lon1, lat2, lon2, maxSpeed, bearing;
    private byte[] flags;
    private int segmentCount;

    // --- Grid in formato CSR ---
    private Map<Long, Integer> cellKeyToBucket; // chiave cella -> indice bucket (0..numBuckets-1)
    private int[] bucketStart;      // dimensione numBuckets+1
    private int[] cellSegmentIdx;   // dimensione totale voci (ogni segmento può comparire in 1-2 celle)

    private boolean isLoaded = false;
    private int lastMatchedIndex = -1;

    // --- Debounce per evitare match spuri alle intersezioni/rampe ---
    // Un cambio di segmento viene accettato solo se il nuovo candidato
    // rimane il migliore per più letture consecutive.
    private static final int REQUIRED_CONSECUTIVE_HITS = 3;

    private int pendingCandidate = -1;
    private int pendingCount = 0;

    public void load(File binFile, Runnable onReadyCallback) {
        new Thread(() -> {
            try {
                loadInternal(binFile);
                isLoaded = true;
                if (onReadyCallback != null) onReadyCallback.run();
            } catch (Exception e) {
                e.printStackTrace();
            }
        }).start();
    }

    private void loadInternal(File binFile) throws IOException {
        long startMs = System.currentTimeMillis();

        try (RandomAccessFile raf = new RandomAccessFile(binFile, "r");
             FileChannel channel = raf.getChannel()) {

            MappedByteBuffer buf = channel.map(FileChannel.MapMode.READ_ONLY, 0, channel.size());
            buf.order(ByteOrder.BIG_ENDIAN); // DataOutputStream scrive big-endian

            segmentCount = buf.getInt();

            lat1 = new float[segmentCount];
            lon1 = new float[segmentCount];
            lat2 = new float[segmentCount];
            lon2 = new float[segmentCount];
            maxSpeed = new float[segmentCount];
            flags = new byte[segmentCount];
            bearing = new float[segmentCount];

            for (int i = 0; i < segmentCount; i++) {
                lat1[i] = buf.getFloat();
                lon1[i] = buf.getFloat();
                lat2[i] = buf.getFloat();
                lon2[i] = buf.getFloat();
                maxSpeed[i] = buf.getFloat();
                flags[i] = buf.get();
                bearing[i] = (float) bearingDeg(lat1[i], lon1[i], lat2[i], lon2[i]);
            }
        }

        buildGridCsr();

        long elapsed = System.currentTimeMillis() - startMs;
        System.out.println("SpeedLimitManager: caricati " + segmentCount + " segmenti in " + elapsed + " ms");
    }

    /**
     * Costruisce la griglia con un counting sort in 2 passate:
     * 1) conta quante entry (segmento,cella) per ogni cella unica
     * 2) riempie l'array cellSegmentIdx nella posizione corretta
     * Evita ArrayList<Integer> per cella: solo array primitivi + una mappa piccola
     * (una entry per cella unica, non per segmento).
     */
    private void buildGridCsr() {
        cellKeyToBucket = new HashMap<>();
        int[] cellA = new int[segmentCount];
        int[] cellB = new int[segmentCount]; // -1 se coincide con cellA

        int nextBucket = 0;
        for (int i = 0; i < segmentCount; i++) {
            long keyA = cellKey(lat1[i], lon1[i]);
            long keyB = cellKey(lat2[i], lon2[i]);

            Integer bA = cellKeyToBucket.get(keyA);
            if (bA == null) {
                bA = nextBucket++;
                cellKeyToBucket.put(keyA, bA);
            }
            cellA[i] = bA;

            if (keyB != keyA) {
                Integer bB = cellKeyToBucket.get(keyB);
                if (bB == null) {
                    bB = nextBucket++;
                    cellKeyToBucket.put(keyB, bB);
                }
                cellB[i] = bB;
            } else {
                cellB[i] = -1;
            }
        }

        int numBuckets = nextBucket;
        int[] count = new int[numBuckets + 1];

        for (int i = 0; i < segmentCount; i++) {
            count[cellA[i]]++;
            if (cellB[i] >= 0) count[cellB[i]]++;
        }

        bucketStart = new int[numBuckets + 1];
        int acc = 0;
        for (int b = 0; b < numBuckets; b++) {
            bucketStart[b] = acc;
            acc += count[b];
        }
        bucketStart[numBuckets] = acc;

        int[] cursor = new int[numBuckets];
        System.arraycopy(bucketStart, 0, cursor, 0, numBuckets);

        cellSegmentIdx = new int[acc];
        for (int i = 0; i < segmentCount; i++) {
            int ba = cellA[i];
            cellSegmentIdx[cursor[ba]++] = i;
            if (cellB[i] >= 0) {
                int bb = cellB[i];
                cellSegmentIdx[cursor[bb]++] = i;
            }
        }
    }

    private long cellKey(double lat, double lon) {
        long cx = (long) Math.floor(lon / CELL_SIZE_DEG);
        long cy = (long) Math.floor(lat / CELL_SIZE_DEG);
        return (cx << 32) ^ (cy & 0xFFFFFFFFL);
    }

    /**
     * @param lat         latitudine corrente
     * @param lon         longitudine corrente
     * @param headingDeg  direzione di marcia (0-360), -1 se non disponibile
     * @param gpsAccuracyM accuratezza del fix GPS in metri (Location.getAccuracy()).
     *                     Passa 0 se non disponibile: verrà usato solo il raggio base.
     * @return limite di velocità in km/h, o null se nessun match plausibile
     */
    public Integer getCurrentSpeedLimit(double lat, double lon, double headingDeg, double gpsAccuracyM) {
        if (!isLoaded) return null;

        // Raggio adattivo: più il fix GPS è impreciso, più allarghiamo la ricerca,
        // ma con un tetto per non finire a matchare la carreggiata opposta o
        // strade di servizio parallele su autostrade molto larghe.
        double effectiveRadiusM = Math.min(BASE_SEARCH_RADIUS_M + Math.max(0, gpsAccuracyM), MAX_SEARCH_RADIUS_M);

        boolean haveHeading = headingDeg >= 0;

        int best = -1;
        double bestScore = Double.MAX_VALUE;
        int bestNoAngleFilter = -1;
        double bestNoAngleDist = Double.MAX_VALUE;

        // Distanza dell'ultimo segmento "ufficiale" (se ancora nelle vicinanze)
        double lastMatchedDist = Double.MAX_VALUE;
        boolean lastMatchedStillPlausible = false;

        long cx = (long) Math.floor(lon / CELL_SIZE_DEG);
        long cy = (long) Math.floor(lat / CELL_SIZE_DEG);

        for (long dx = -1; dx <= 1; dx++) {
            for (long dy = -1; dy <= 1; dy++) {
                long key = ((cx + dx) << 32) ^ ((cy + dy) & 0xFFFFFFFFL);
                Integer bucket = cellKeyToBucket.get(key);
                if (bucket == null) continue;

                int from = bucketStart[bucket];
                int to = bucketStart[bucket + 1];

                for (int k = from; k < to; k++) {
                    int idx = cellSegmentIdx[k];

                    double dist = pointToSegmentDistanceM(lat, lon, lat1[idx], lon1[idx], lat2[idx], lon2[idx]);
                    if (dist > effectiveRadiusM) continue;

                    if (dist < bestNoAngleDist) {
                        bestNoAngleDist = dist;
                        bestNoAngleFilter = idx;
                    }

                    if (idx == lastMatchedIndex) {
                        lastMatchedDist = dist;
                        if (haveHeading) {
                            lastMatchedStillPlausible = angleDiff(headingDeg, bearing[idx]) <= MAX_BEARING_DIFF_DEG;
                        } else {
                            lastMatchedStillPlausible = true;
                        }
                    }

                    if (haveHeading) {
                        double diff = angleDiff(headingDeg, bearing[idx]);
                        if (diff > MAX_BEARING_DIFF_DEG) continue;

                        double score = dist;
                        if (score < bestScore) {
                            bestScore = score;
                            best = idx;
                        }
                    }
                }
            }
        }

        int instantBest = (best != -1) ? best : bestNoAngleFilter;
        if (instantBest == -1) return null;

        // Primo match in assoluto: accettalo subito, non c'è niente da debounciare
        if (lastMatchedIndex == -1) {
            lastMatchedIndex = instantBest;
            pendingCandidate = -1;
            pendingCount = 0;
            return Math.round(maxSpeed[instantBest]);
        }

        // Se il match istantaneo coincide con quello già confermato, tutto bene
        if (instantBest == lastMatchedIndex) {
            pendingCandidate = -1;
            pendingCount = 0;
            return Math.round(maxSpeed[lastMatchedIndex]);
        }

        // Il candidato è diverso da quello confermato: prima di cambiare,
        // verifica se quello vecchio è ancora una spiegazione plausibile
        // (siamo ancora abbastanza vicini e con bearing compatibile) -
        // tipico il caso di un incrocio/rampa che per un istante è più vicino
        // della strada principale su cui stiamo effettivamente viaggiando.
        boolean oldStillSticky = lastMatchedStillPlausible && lastMatchedDist <= (effectiveRadiusM * 0.6);

        if (instantBest == pendingCandidate) {
            pendingCount++;
        } else {
            pendingCandidate = instantBest;
            pendingCount = 1;
        }

        if (pendingCount >= REQUIRED_CONSECUTIVE_HITS || !oldStillSticky) {
            // Il nuovo candidato è persistente abbastanza, oppure il vecchio
            // segmento non è più raggiungibile/plausibile: accetta il cambio.
            lastMatchedIndex = instantBest;
            pendingCandidate = -1;
            pendingCount = 0;
            return Math.round(maxSpeed[instantBest]);
        }

        // Non ancora abbastanza persistente: resta sul segmento precedente
        return Math.round(maxSpeed[lastMatchedIndex]);
    }

    // --- Geometria di supporto ---

    private static double bearingDeg(double lat1, double lon1, double lat2, double lon2) {
        double phi1 = Math.toRadians(lat1);
        double phi2 = Math.toRadians(lat2);
        double deltaLambda = Math.toRadians(lon2 - lon1);

        double y = Math.sin(deltaLambda) * Math.cos(phi2);
        double x = Math.cos(phi1) * Math.sin(phi2) - Math.sin(phi1) * Math.cos(phi2) * Math.cos(deltaLambda);
        double theta = Math.atan2(y, x);
        return (Math.toDegrees(theta) + 360) % 360;
    }

    private static double angleDiff(double a, double b) {
        double diff = Math.abs(a - b) % 360;
        if (diff > 180) diff = 360 - diff;
        return Math.min(diff, Math.abs(180 - diff));
    }

    private static double pointToSegmentDistanceM(double lat, double lon, double lat1, double lon1, double lat2, double lon2) {
        double mLat = 111320.0;
        double mLon = 111320.0 * Math.cos(Math.toRadians(lat));

        double px = (lon - lon1) * mLon;
        double py = (lat - lat1) * mLat;
        double dx = (lon2 - lon1) * mLon;
        double dy = (lat2 - lat1) * mLat;

        double lenSq = dx * dx + dy * dy;
        double t = (lenSq == 0) ? 0 : Math.max(0, Math.min(1, (px * dx + py * dy) / lenSq));

        double projX = t * dx;
        double projY = t * dy;

        double ddx = px - projX;
        double ddy = py - projY;
        return Math.sqrt(ddx * ddx + ddy * ddy);
    }
}