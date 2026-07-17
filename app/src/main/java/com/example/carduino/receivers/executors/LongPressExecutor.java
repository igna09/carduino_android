package com.example.carduino.receivers.executors;

import com.example.carduino.receivers.ArduinoMessageExecutorInterface;
import com.example.carduino.shared.models.ArduinoMessage;
import com.example.carduino.shared.models.carstatus.CarStatusEnum;
import com.example.carduino.shared.models.carstatus.CarStatusFactory;
import com.example.carduino.shared.models.carstatus.values.Value;
import com.example.carduino.shared.singletons.AppSwitchSingleton;
import com.example.carduino.shared.singletons.CarStatusSingleton;

public class LongPressExecutor implements ArduinoMessageExecutorInterface {

    @Override
    public void execute(ArduinoMessage message) {
        AppSwitchSingleton.getInstance().openNextApplication();
    }
}