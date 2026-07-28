package com.example.carduino.shared.singletons;

import com.example.carduino.shared.TripValueDeserializer;
import com.example.carduino.shared.models.trip.Trip;
import com.example.carduino.shared.models.trip.tripvalue.TripValue;
import com.example.carduino.shared.models.trip.tripvalue.TripValueEnum;
import com.example.carduino.shared.utilities.LoggerUtilities;
import com.google.gson.Gson;
import com.google.gson.GsonBuilder;
import com.google.gson.reflect.TypeToken;

import java.io.File;
import java.io.FileInputStream;
import java.io.IOException;
import java.lang.reflect.Type;
import java.util.ArrayList;
import java.util.Collections;
import java.util.List;

public class TripHistorySingleton {
    private static TripHistorySingleton instance;

    private List<Trip> trips;       // storico completo, ultimo elemento = trip corrente
    private final FileSystemSingleton fileSystemSingleton;
    private static final String TRIPS_FILE_NAME = "trips.json";
    private final Thread backupThread;

    private TripHistorySingleton() {
        trips = new ArrayList<>();
        fileSystemSingleton = FileSystemSingleton.getInstance();
        loadTrips();

        backupThread = new Thread(() -> {
            try {
                while (!Thread.currentThread().isInterrupted()) {
                    Thread.sleep(15 * 1000);
                    backupTrips();
                }
            } catch (InterruptedException ignored) {} catch (IOException e) {
                throw new RuntimeException(e);
            }
        });
    }

    public static TripHistorySingleton getInstance() {
        if (instance == null) instance = new TripHistorySingleton();
        return instance;
    }

    public void startBackupThread() { if (!backupThread.isAlive()) backupThread.start(); }
    public void stopBackupThread() { backupThread.interrupt(); }

    /** Chiamato quando si rileva RPM > 0: chiude il trip corrente (se aperto) e ne apre uno nuovo. */
    public synchronized Trip startNewTrip() {
        if (!trips.isEmpty() && getCurrentTrip().isStarted()) {
            getCurrentTrip().stopTrip();
        }
        Trip t = new Trip();
        t.startTrip();
        trips.add(t);
        startBackupThread();
        try { backupTrips(); } catch (IOException e) { LoggerUtilities.logMessage("TripHistory", "save failed"); }
        return t;
    }

    public Trip getCurrentTrip() {
        return trips.isEmpty() ? null : trips.get(trips.size() - 1);
    }

    public boolean isCurrentTripStarted() {
        Trip t = getCurrentTrip();
        return t != null && t.isStarted();
    }

    /** Trip passati, dal più recente (esclude quello corrente). */
    public List<Trip> getPastTrips() {
        if (trips.size() <= 1) return Collections.emptyList();
        List<Trip> past = new ArrayList<>(trips.subList(0, trips.size() - 1));
        Collections.reverse(past);
        return past;
    }

    public List<Trip> getAllTripsRecentFirst() {
        List<Trip> all = new ArrayList<>(trips);
        Collections.reverse(all);
        return all;
    }

    /** Aggregato lifetime per singolo TripValueEnum, ricalcolato su richiesta da tutti i trip. */
    @SuppressWarnings("unchecked")
    public TripValue getLifetimeValue(TripValueEnum type) {
        TripValue aggregate = null;
        for (Trip t : trips) {
            TripValue v = t.getTripValues().get(type);
            if (v == null) continue;
            if (aggregate == null) {
                try {
                    aggregate = (TripValue) type.getClazz().newInstance(); // parte da zero, pulito
                } catch (Exception e) { throw new RuntimeException(e); }
            }
            aggregate.mergeFrom(v);
        }
        return aggregate;
    }

    public void resetAllTrips() throws IOException {
        trips.clear();
        File f = getTripsFile();
        if (f != null && f.exists()) f.delete();
    }

    public void backupTrips() throws IOException {
        File f = getTripsFile();
        Gson gson = new GsonBuilder().setPrettyPrinting().create();
        String json = gson.toJson(trips);
        if (f != null) fileSystemSingleton.writeToFile(f, json, false);
    }

    public void loadTrips() {
        try {
            File f = getTripsFile();
            if (f == null || !f.exists()) return;
            FileInputStream fin = new FileInputStream(f);
            String json = TripHistorySingleton.convertStreamToString(fin);
            fin.close();
            Gson gson = new GsonBuilder()
                    .registerTypeAdapter(TripValue.class, new TripValueDeserializer())
                    .create();
            Type listType = new TypeToken<ArrayList<Trip>>(){}.getType();
            trips = gson.fromJson(json, listType);
            if (trips == null) trips = new ArrayList<>();
        } catch (Exception e) {
            LoggerUtilities.logMessage("TripHistory", "load failed: " + e.getMessage());
            trips = new ArrayList<>();
        }
    }

    private File getTripsFile() throws IOException {
        return fileSystemSingleton.createOrGetFile(fileSystemSingleton.getCarduinoRootFolder(), TRIPS_FILE_NAME);
    }

    public static void invalidate() {
        instance = null;
    }

    public synchronized void stopTrip() {
        Trip current = getCurrentTrip();
        if (current != null && current.isStarted()) {
            current.stopTrip();
            try {
                backupTrips();
            } catch (IOException e) {
                LoggerUtilities.logMessage("TripHistory", "save failed on stopTrip");
            }
        }
    }

    public boolean tripBackupAvailable() {
        try {
            File f = getTripsFile();
            return f != null && f.exists() && f.length() > 0;
        } catch (IOException e) {
            return false;
        }
    }

    public static String convertStreamToString(FileInputStream fis) throws IOException {
        java.io.ByteArrayOutputStream baos = new java.io.ByteArrayOutputStream();
        byte[] buffer = new byte[1024];
        int len;
        while ((len = fis.read(buffer)) != -1) {
            baos.write(buffer, 0, len);
        }
        return baos.toString("UTF-8");
    }
}
