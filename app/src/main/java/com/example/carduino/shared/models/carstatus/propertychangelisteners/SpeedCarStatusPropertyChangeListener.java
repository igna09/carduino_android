package com.example.carduino.shared.models.carstatus.propertychangelisteners;

import android.media.AudioManager;
import android.media.ToneGenerator;

import com.example.carduino.shared.models.carstatus.values.KmhSpeed;
import com.example.carduino.shared.models.trip.tripvalue.TripValueEnum;
import com.example.carduino.shared.singletons.SharedDataSingleton;
import com.example.carduino.shared.singletons.TripSingleton;
import com.example.carduino.shared.utilities.LoggerUtilities;

import java.util.Date;

public class SpeedCarStatusPropertyChangeListener extends PropertyChangeListener<KmhSpeed> {
    @Override
    public void onPropertyChange(String propertyName, KmhSpeed oldValue, KmhSpeed newValue) {
        if(TripSingleton.getInstance().getTrip().isStarted()) {
            if (TripSingleton.getInstance().getTrip().getTripValues().get(TripValueEnum.DISTANCE) == null) { //first insertion
                TripSingleton.getInstance().getTrip().addTripValue(TripValueEnum.DISTANCE, Float.valueOf(0));
            }
            if (TripSingleton.getInstance().getTrip().getTripValues().get(TripValueEnum.DISTANCE).getLastReading() == null) {
                TripSingleton.getInstance().getTrip().getTripValues().get(TripValueEnum.DISTANCE).setLastReading(new Date());
            }

            if(newValue.getValue() != null && newValue.getValue() > 0) {
                // SPEED TRIP
                TripSingleton.getInstance().getTrip().addTripValue(TripValueEnum.SPEED, newValue.getValue());

                // DISTANCE TRIP
                Float avgSpeed = new Float((oldValue.getValue() + newValue.getValue()) / 2.0);
                Date now = new Date();
                Float deltaT = ((Double) (Math.abs(now.getTime() - TripSingleton.getInstance().getTrip().getTripValues().get(TripValueEnum.DISTANCE).getLastReading().getTime()) / 1000.0)).floatValue();
                Float distance = (((avgSpeed * deltaT) / 3600) * 0.988F); //102.3 / 101.1 = 0.988
                /*LoggerUtilities.logMessage(
                        "SpeedCarStatusPropertyChangeListener",
                        "oldValue " + oldValue.getValue()
                                + ", newValue " + newValue.getValue()
                                + ", avgSpeed " + avgSpeed
                                + ", lastReading " + TripSingleton.getInstance().getTrip().getTripValues().get(TripValueEnum.DISTANCE).getLastReading().getTime()
                                + ", nowTime " + now.getTime()
                                + ", deltaT " + deltaT
                                + ", distance((avgSpeed * deltaT) / 3600) " + distance
                );*/
                TripSingleton.getInstance().getTrip().addTripValue(TripValueEnum.DISTANCE, distance);
                TripSingleton.getInstance().getTrip().getTripValues().get(TripValueEnum.DISTANCE).setLastReading(now);
            } else {
                TripSingleton.getInstance().getTrip().getTripValues().get(TripValueEnum.DISTANCE).setLastReading(new Date());
            }
        }

        if(SharedDataSingleton.getInstance().getRoadLimit() > newValue.getValue()) {
            LoggerUtilities.logMessage("limit of speed reached, speed: " + newValue.getValue() + ", limit: " + SharedDataSingleton.getInstance().getRoadLimit());
            (new ToneGenerator(AudioManager.STREAM_NOTIFICATION, 100)).startTone(ToneGenerator.TONE_CDMA_ALERT_CALL_GUARD, 500);
        }
    }
}
