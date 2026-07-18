package com.example.carduino.shared.models.carstatus.propertychangelisteners;

import com.example.carduino.shared.models.carstatus.CarStatusFactory;
import com.example.carduino.shared.models.carstatus.values.Rpm;
import com.example.carduino.shared.models.carstatus.values.Value;
import com.example.carduino.shared.singletons.AppSwitchSingleton;
import com.example.carduino.shared.singletons.ArduinoSingleton;
import com.example.carduino.shared.singletons.CarStatusSingleton;
import com.example.carduino.shared.singletons.ContextsSingleton;
import com.example.carduino.shared.singletons.LoggerSingleton;
import com.example.carduino.shared.singletons.SettingsSingleton;
import com.example.carduino.shared.singletons.SharedDataSingleton;
import com.example.carduino.shared.singletons.TripSingleton;
import com.example.carduino.shared.utilities.DialogUtilities;
import com.example.carduino.shared.utilities.LoggerUtilities;

public class RpmCarStatusPropertyChangeListener extends PropertyChangeListener<Rpm> {
    @Override
    public void onPropertyChange(String propertyName, Rpm oldValue, Rpm newValue) {
        //LoggerUtilities.logMessage("RpmCarStatusPropertyChangeListener", "oldValue: " + (oldValue.getValue() != null ? oldValue.getValue().toString() : "null") + ", newValue: " + (newValue.getValue() != null ? newValue.getValue() : "null") + ", tripStarted: " + TripSingleton.getInstance().getTrip().isStarted());
        // CARSTATUS
        if((oldValue.getValue() == null || oldValue.getValue() == 0) && newValue.getValue() > 0) { //Engine turned on
            Value value = CarStatusFactory.getCarStatusValue("ENGINE_STARTED", "TRUE");
            if (value != null) {
                CarStatusSingleton.getInstance().getCarStatus().putValue(value);
            }
        }
        if((oldValue.getValue() != null && oldValue.getValue() > 0) && (newValue.getValue() == 0 || newValue.getValue() == null)) { //Engine turned off
            //LoggerUtilities.logMessage("RpmCarStatusPropertyChangeListener", "engine turned off");
            Value value = CarStatusFactory.getCarStatusValue("ENGINE_STARTED", "FALSE");
            if (value != null) {
                CarStatusSingleton.getInstance().getCarStatus().putValue(value);
            }
            if(TripSingleton.getInstance().getTrip().isStarted()) {
                TripSingleton.getInstance().stopTrip();
            }
        }
        // TRIP
        if(!TripSingleton.getInstance().getTrip().isStarted() && newValue.getValue() > 0) {
            if(TripSingleton.getInstance().tripBackupAvailable()) {
                if(ContextsSingleton.getInstance().getApplicationContext().isShowingApplication()) {
                    if(!DialogUtilities.isShowingDialog()) {
                        DialogUtilities.openContinueLastTrip();
                    }
                } else { // application not in foreground continuing last trip
                    try {
                        TripSingleton.getInstance().restoreTrip();
                        TripSingleton.getInstance().startTrip();
                    } catch (Exception e) {
                        throw new RuntimeException(e);
                    }
                }
            } else {
                TripSingleton.getInstance().startTrip();
            }
        }

//        if(TripSingleton.getInstance().getTrip().isStarted() && newValue.getValue() == 0) { //car turned off
//            TripSingleton.getInstance().stopTrip();
//            TripSingleton.invalidate();
//        }
    }
}
