package com.example.carduino.shared.models.carstatus.propertychangelisteners;

import com.example.carduino.shared.models.carstatus.CarStatusFactory;
import com.example.carduino.shared.models.carstatus.values.Rpm;
import com.example.carduino.shared.models.carstatus.values.Value;
import com.example.carduino.shared.singletons.CarStatusSingleton;
import com.example.carduino.shared.singletons.TripHistorySingleton;

public class RpmCarStatusPropertyChangeListener extends PropertyChangeListener<Rpm> {
    @Override
    public void onPropertyChange(String propertyName, Rpm oldValue, Rpm newValue) {
        // CARSTATUS
        if ((oldValue.getValue() == null || oldValue.getValue() == 0) && newValue.getValue() > 0) { //Engine turned on
            Value value = CarStatusFactory.getCarStatusValue("ENGINE_STARTED", "TRUE");
            if (value != null) {
                CarStatusSingleton.getInstance().getCarStatus().putValue(value);
            }
        }
        if ((oldValue.getValue() != null && oldValue.getValue() > 0) && (newValue.getValue() == 0 || newValue.getValue() == null)) { //Engine turned off
            Value value = CarStatusFactory.getCarStatusValue("ENGINE_STARTED", "FALSE");
            if (value != null) {
                CarStatusSingleton.getInstance().getCarStatus().putValue(value);
            }
            if (TripHistorySingleton.getInstance().isCurrentTripStarted()) {
                TripHistorySingleton.getInstance().stopTrip();
            }
        }
        // TRIP: auto-start, nessun resume prompt
        if (!TripHistorySingleton.getInstance().isCurrentTripStarted() && newValue.getValue() > 0) {
            TripHistorySingleton.getInstance().startNewTrip();
        }
    }
}