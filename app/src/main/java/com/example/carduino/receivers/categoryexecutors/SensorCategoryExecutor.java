package com.example.carduino.receivers.categoryexecutors;

import com.example.carduino.receivers.ArduinoMessageExecutorInterface;
import com.example.carduino.shared.EventExecutor;
import com.example.carduino.shared.models.ArduinoMessage;
import com.example.carduino.shared.models.Event;
import com.example.carduino.shared.models.carstatus.CarStatusFactory;
import com.example.carduino.shared.models.carstatus.values.Value;
import com.example.carduino.shared.singletons.CarStatusSingleton;

public class SensorCategoryExecutor implements ArduinoMessageExecutorInterface {

    @Override
    public void execute(ArduinoMessage message) {
        Event event = message.getEvent(); //
        if (event == null) {
            return;
        }

        // Recuperiamo il valore già convertito nel suo tipo nativo (Float, Integer, etc.)
        Object rawValue = message.getValueAt(0);

        if (rawValue != null) {
            // Adesso passiamo direttamente l'istanza di Event (come chiave) e l'Object già tipizzato
            Value value = CarStatusFactory.getCarStatusValue(event.name(), rawValue.toString());

            if (value != null) {
                CarStatusSingleton.getInstance().getCarStatus().putValue(value);
            }
        }
    }
}
