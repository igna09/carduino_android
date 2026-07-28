package com.example.carduino.receivers.executors;

import com.example.carduino.receivers.ArduinoMessageExecutorInterface;
import com.example.carduino.shared.models.ArduinoMessage;
import com.example.carduino.shared.models.carstatus.CarStatusEnum;
import com.example.carduino.shared.models.carstatus.CarStatusFactory;
import com.example.carduino.shared.models.carstatus.values.Value;
import com.example.carduino.shared.singletons.AppSwitchSingleton;
import com.example.carduino.shared.singletons.CarStatusSingleton;

public class CruiseExecutor implements ArduinoMessageExecutorInterface {

    @Override
    public void execute(ArduinoMessage message) {
        Float cruiseStatus = message.getValueAt(0);

        String status = "";

        if (cruiseStatus == 0) {
            status = "DISARMED";
        } else if (cruiseStatus == 1) {
            status = "ARMED";
        } else {
            status = "ENABLED";
        }

        Value value = CarStatusFactory.getCarStatusValue(CarStatusEnum.CRUISE_STATUS.name(), status);
        CarStatusSingleton.getInstance().getCarStatus().putValue(value);
    }
}