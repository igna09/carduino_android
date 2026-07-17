package com.example.carduino.receivers.executors;

import com.example.carduino.receivers.ArduinoMessageExecutorInterface;
import com.example.carduino.settings.SettingsEnum;
import com.example.carduino.settings.settingfactory.Setting;
import com.example.carduino.settings.settingfactory.SettingsFactory;
import com.example.carduino.shared.models.ArduinoMessage;
import com.example.carduino.shared.models.carstatus.CarStatusEnum;
import com.example.carduino.shared.models.carstatus.CarStatusFactory;
import com.example.carduino.shared.models.carstatus.values.Value;
import com.example.carduino.shared.singletons.CarStatusSingleton;
import com.example.carduino.shared.singletons.SettingsSingleton;
import com.example.carduino.shared.utilities.DialogUtilities;

public class SpeedLimitExecutor implements ArduinoMessageExecutorInterface {

    @Override
    public void execute(ArduinoMessage message) {
        Integer speed = message.getValueAt(0); // Ritorna l'Integer (UINT8)

        DialogUtilities.openDialogSpeedLimitSet(speed.toString());

        Value value = CarStatusFactory.getCarStatusValue(CarStatusEnum.SPEED_LIMIT.name(), speed.toString());
        CarStatusSingleton.getInstance().getCarStatus().putValue(value);
    }
}