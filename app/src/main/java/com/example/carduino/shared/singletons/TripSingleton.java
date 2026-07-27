package com.example.carduino.shared.singletons;

import com.example.carduino.shared.models.trip.Trip;
import com.example.carduino.shared.models.trip.tripvalue.TripValue;
import com.example.carduino.shared.models.trip.tripvalue.TripValueEnum;
import com.google.gson.Gson;
import com.google.gson.GsonBuilder;
import com.google.gson.JsonDeserializationContext;
import com.google.gson.JsonDeserializer;
import com.google.gson.JsonElement;
import com.google.gson.JsonParseException;

import java.io.BufferedReader;
import java.io.File;
import java.io.FileInputStream;
import java.io.IOException;
import java.io.InputStream;
import java.io.InputStreamReader;
import java.lang.reflect.Type;
import java.util.Date;

public class TripSingleton {
    public class TripValueDeserializer implements JsonDeserializer<TripValue> {
        @Override
        public TripValue deserialize(JsonElement json, Type typeOfT, JsonDeserializationContext context) throws JsonParseException {
            JsonElement jsonElement = json.getAsJsonObject().get("tripValueEnum");
            if(jsonElement != null) {
                String stringType = jsonElement.getAsString();
                TripValueEnum type;
                try {
                    type = TripValueEnum.valueOf(stringType);
                } catch (IllegalArgumentException e) {
                    type = null;
                }
                if(type == null) {
                    throw new IllegalArgumentException("No TripValuEnum exists for" + stringType);
                } else {
                    return context.deserialize(json, type.getClazz());
                }
            } else {
                return new TripValue() {
                    @Override
                    public void addValue(Object value) {
                        super.addValue(value);
                    }
                };
            }
        }
    }
    private static TripSingleton instance;

    private Trip trip;

    private final FileSystemSingleton fileSystemSingleton;
    private static final String TRIP_FILE_NAME = "last_trip.json";
    private final Thread backupThread;

    private TripSingleton() {
        trip = new Trip();
        fileSystemSingleton = FileSystemSingleton.getInstance();

        backupThread = new Thread(new Runnable() {
            @Override
            public void run() {
                try {
                    while(backupThread.isAlive() && !backupThread.isInterrupted()) {
                        Thread.sleep(15 * 1000);
                        backupTrip();
                    }
                } catch (Exception e) {
                    throw new RuntimeException(e);
                }
            }
        });
    }

    public void startBackupThread() {
        if(!backupThread.isAlive()) {
            backupThread.start();
        }
    }

    public void startTrip() {
        startBackupThread();
        trip.startTrip();
    }

    public void stopTrip() {
        trip.stopTrip();
        stopBackupThread();
    }

    public void stopBackupThread() {
        backupThread.interrupt();
    }

    public static TripSingleton getInstance() {
        if(instance == null) {
            instance = new TripSingleton();
        }
        return instance;
    }

    public Trip getTrip() {
        return trip;
    }

    public void backupTrip() throws IOException {
        File tripFile = getTripFile();

        Gson gson = new GsonBuilder().setPrettyPrinting().create();
        String json = gson.toJson(trip);

        if(tripFile != null) {
            fileSystemSingleton.writeToFile(tripFile, json, false);
        }
    }

    private File getTripFile() throws IOException {
        return fileSystemSingleton.createOrGetFile(fileSystemSingleton.getCarduinoRootFolder(), TRIP_FILE_NAME);
    }

    public Boolean tripBackupAvailable() {
        return fileSystemSingleton.existsInRoot(TRIP_FILE_NAME);
    }

    public Boolean restoreTrip() throws Exception {
        if(tripBackupAvailable()) {
            File tripFile = getTripFile();

            FileInputStream fin = new FileInputStream(tripFile);
            String json = convertStreamToString(fin);
            fin.close();

            Gson gson = new GsonBuilder()
                    .registerTypeAdapter(TripValue.class, new TripValueDeserializer())
                    .create();
            trip = gson.fromJson(json, Trip.class);

            if(trip.getTripValues().containsKey(TripValueEnum.DISTANCE)) {
                trip.getTripValues().get(TripValueEnum.DISTANCE).setLastReading(new Date());
            }

            return true;
        } else {
            return false;
        }
    }

    public void resetTrip() throws IOException {
        trip = new Trip();
        backupTrip();
    }

    public static String convertStreamToString(InputStream is) throws Exception {
        BufferedReader reader = new BufferedReader(new InputStreamReader(is));
        StringBuilder sb = new StringBuilder();
        String line = null;
        while ((line = reader.readLine()) != null) {
            sb.append(line).append("\n");
        }
        reader.close();
        return sb.toString();
    }

    public static void invalidate() {
        instance.backupThread.interrupt();
        instance = null;
    }
}
