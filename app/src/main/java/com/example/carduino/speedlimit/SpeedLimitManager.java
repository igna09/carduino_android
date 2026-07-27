package com.example.carduino.speedlimit;

import com.graphhopper.GraphHopper;
import com.graphhopper.routing.ev.DecimalEncodedValue;
import com.graphhopper.routing.ev.MaxSpeed;
import com.graphhopper.routing.util.EdgeFilter;
import com.graphhopper.routing.util.EncodingManager;
import com.graphhopper.storage.index.LocationIndex;
import com.graphhopper.storage.index.Snap;
import com.graphhopper.util.EdgeIteratorState;

import java.io.File;

public class SpeedLimitManager {

    private GraphHopper hopper;
    private LocationIndex locationIndex;
    private DecimalEncodedValue maxSpeedEnc;
    private boolean isLoaded = false;

    /**
     * Inizializza GraphHopper caricando il grafo dalla cartella interna dell'app/SD.
     * Va eseguito in background.
     */
    public void initGraphHopper(File graphDir, Runnable onReadyCallback) {
        new Thread(() -> {
            try {
                hopper = new GraphHopper();

                // 1. Imposta la cartella del grafo
                hopper.setGraphHopperLocation(graphDir.getAbsolutePath());

                // 2. Carica il grafo senza argomenti
                boolean loaded = hopper.load();

                if (loaded) {
                    locationIndex = hopper.getLocationIndex();
                    EncodingManager encodingManager = hopper.getEncodingManager();

                    if (encodingManager.hasEncodedValue(MaxSpeed.KEY)) {
                        maxSpeedEnc = encodingManager.getDecimalEncodedValue(MaxSpeed.KEY);
                    }

                    isLoaded = true;

                    if (onReadyCallback != null) {
                        onReadyCallback.run();
                    }
                }
            } catch (Exception e) {
                e.printStackTrace();
            }
        }).start();
    }

    /**
     * Restituisce il limite di velocità in km/h per le coordinate indicate.
     */
    public Integer getCurrentRoadSpeedLimit(double lat, double lon) {
        if (!isLoaded || locationIndex == null || maxSpeedEnc == null) {
            return null;
        }

        // Snap-to-road della posizione GPS
        Snap snap = locationIndex.findClosest(lat, lon, EdgeFilter.ALL_EDGES);

        if (snap.isValid()) {
            EdgeIteratorState edge = snap.getClosestEdge();
            if (edge != null) {
                double speed = edge.get(maxSpeedEnc);

                if (!Double.isInfinite(speed) && !Double.isNaN(speed) && speed > 0) {
                    return (int) Math.round(speed); // Es: 50, 90, 130
                }
            }
        }

        return null;
    }
}