package com.example.carduino.shared.models.carstatus.propertychangelisteners;

import com.example.carduino.shared.models.carstatus.values.FuelConsumptionKmL;
import com.example.carduino.shared.models.trip.tripvalue.TripValueEnum;
import com.example.carduino.shared.singletons.TripHistorySingleton;

public class FuelConsumptionCarStatusPropertyChangeListener extends PropertyChangeListener<FuelConsumptionKmL> {
    @Override
    public void onPropertyChange(String propertyName, FuelConsumptionKmL oldValue, FuelConsumptionKmL newValue) {
        if(TripHistorySingleton.getInstance().isCurrentTripStarted() && newValue.getValue() > 0 && newValue.getValue() < 50) { // avoiding abnormal values
            TripHistorySingleton.getInstance().getCurrentTrip().addTripValue(TripValueEnum.FUEL_CONSUMPTION, newValue.getValue());
        }
    }
}
