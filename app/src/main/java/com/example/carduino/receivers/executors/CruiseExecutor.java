package com.example.carduino.receivers.executors;

import com.example.carduino.receivers.ArduinoMessageExecutorInterface;
import com.example.carduino.shared.models.ArduinoMessage;
import com.example.carduino.shared.models.carstatus.CarStatusEnum;
import com.example.carduino.shared.models.carstatus.CarStatusFactory;
import com.example.carduino.shared.models.carstatus.values.Value;
import com.example.carduino.shared.singletons.CarStatusSingleton;

enum CruiseState {
    OFF(0),
    READY(1),
    ACTIVE(2),
    BRAKE(3),
    CLUTCH(4),
    PAUSED(5);

    private final int code;

    CruiseState(int code) {
        this.code = code;
    }

    public int getCode() {
        return code;
    }

    public static CruiseState fromCode(int code) {
        for (CruiseState state : values()) {
            if (state.code == code) {
                return state;
            }
        }
        return OFF;
    }
}

public class CruiseExecutor implements ArduinoMessageExecutorInterface {

    @Override
    public void execute(ArduinoMessage message) {
        Integer rawCode = message.getValueAt(0);
        int code = (rawCode != null) ? rawCode.intValue() : 0;

        CruiseState cruiseState = CruiseState.fromCode(code);

        // Manda alla UI la stringa esatta dell'enum ("OFF", "READY", "ACTIVE", "BRAKE", "CLUTCH", "PAUSED")
        Value value = CarStatusFactory.getCarStatusValue(
                CarStatusEnum.CRUISE_STATUS.name(),
                cruiseState.name()
        );
        CarStatusSingleton.getInstance().getCarStatus().putValue(value);
    }
}