package com.example.carduino.shared.models.carstatus.propertychangelisteners;

import android.media.AudioManager;
import android.media.ToneGenerator;

import com.example.carduino.settings.SettingsEnum;
import com.example.carduino.shared.models.carstatus.values.KmhSpeed;
import com.example.carduino.shared.models.trip.tripvalue.TripValueEnum;
import com.example.carduino.shared.singletons.SettingsSingleton;
import com.example.carduino.shared.singletons.SharedDataSingleton;
import com.example.carduino.shared.singletons.TripHistorySingleton;
import com.example.carduino.shared.utilities.LoggerUtilities;

import java.util.Date;

public class SpeedCarStatusPropertyChangeListener extends PropertyChangeListener<KmhSpeed> {
    @Override
    public void onPropertyChange(String propertyName, KmhSpeed oldValue, KmhSpeed newValue) {
        if(TripHistorySingleton.getInstance().isCurrentTripStarted()) {
            if (TripHistorySingleton.getInstance().getCurrentTrip().getTripValues().get(TripValueEnum.DISTANCE) == null) { //first insertion
                TripHistorySingleton.getInstance().getCurrentTrip().addTripValue(TripValueEnum.DISTANCE, Float.valueOf(0));
            }
            if (TripHistorySingleton.getInstance().getCurrentTrip().getTripValues().get(TripValueEnum.DISTANCE).getLastReading() == null) {
                TripHistorySingleton.getInstance().getCurrentTrip().getTripValues().get(TripValueEnum.DISTANCE).setLastReading(new Date());
            }

            if(newValue.getValue() != null && newValue.getValue() > 0) {
                // SPEED TRIP
                TripHistorySingleton.getInstance().getCurrentTrip().addTripValue(TripValueEnum.SPEED, newValue.getValue());

                // DISTANCE TRIP
                Float avgSpeed = new Float((oldValue.getValue() + newValue.getValue()) / 2.0);
                Date now = new Date();
                Float deltaT = ((Double) (Math.abs(now.getTime() - TripHistorySingleton.getInstance().getCurrentTrip().getTripValues().get(TripValueEnum.DISTANCE).getLastReading().getTime()) / 1000.0)).floatValue();
                Float distance = (((avgSpeed * deltaT) / 3600) * 0.988F); //102.3 / 101.1 = 0.988
                /*LoggerUtilities.logMessage(
                        "SpeedCarStatusPropertyChangeListener",
                        "oldValue " + oldValue.getValue()
                                + ", newValue " + newValue.getValue()
                                + ", avgSpeed " + avgSpeed
                                + ", lastReading " + TripHistorySingleton.getInstance().getCurrentTrip().getTripValues().get(TripValueEnum.DISTANCE).getLastReading().getTime()
                                + ", nowTime " + now.getTime()
                                + ", deltaT " + deltaT
                                + ", distance((avgSpeed * deltaT) / 3600) " + distance
                );*/
                TripHistorySingleton.getInstance().getCurrentTrip().addTripValue(TripValueEnum.DISTANCE, distance);
                TripHistorySingleton.getInstance().getCurrentTrip().getTripValues().get(TripValueEnum.DISTANCE).setLastReading(now);
            } else {
                TripHistorySingleton.getInstance().getCurrentTrip().getTripValues().get(TripValueEnum.DISTANCE).setLastReading(new Date());
            }
        }

        /*if(
            (Boolean) SettingsSingleton.getInstance().getSettings().get(SettingsEnum.SPD_LMT_LRM.name()).getValue()
            && SharedDataSingleton.getInstance().getRoadInfo().getLimit() != null
            && newValue.getValue() != null
            && newValue.getValue() > SharedDataSingleton.getInstance().getRoadInfo().getLimit()
        ) {
            LoggerUtilities.logMessage("limit of speed reached, speed: " + newValue.getValue() + ", limit: " + SharedDataSingleton.getInstance().getRoadInfo().getLimit());
            (new ToneGenerator(AudioManager.STREAM_NOTIFICATION, 100)).startTone(ToneGenerator.TONE_CDMA_ALERT_CALL_GUARD, 500);
        }*/
    }
}
