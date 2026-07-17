package com.example.carduino.receivers.executors;

import com.example.carduino.receivers.ArduinoMessageExecutorInterface;
import com.example.carduino.settings.SettingsEnum;
import com.example.carduino.settings.settingfactory.SettingsFactory;
import com.example.carduino.shared.models.ArduinoMessage;
import com.example.carduino.settings.settingfactory.Setting;
import com.example.carduino.shared.singletons.SettingsSingleton;
import com.example.carduino.shared.utilities.LoggerUtilities;

public class ReadSettingExecutor implements ArduinoMessageExecutorInterface {

    @Override
    public void execute(ArduinoMessage message) {
        Integer settingId = message.getValueAt(0); // Ritorna l'Integer (UINT8)
        Object settingValue = message.getValueAt(1);

        if (settingId == null || settingValue == null) {
            return;
        }

        // 1. Recuperiamo l'enum specifico facendo il cast da BaseEnum a SettingsEnum
        SettingsEnum settingsEnum = (SettingsEnum) SettingsEnum.getEnumById(settingId);

        // 2. Se l'ID corrisponde a un'impostazione valida, procediamo
        if (settingsEnum != null) {
            // Usiamo settingsEnum.name() per passare la String (es. "VOLUME_LEVEL") alla factory
            Setting setting = SettingsFactory.getSetting(
                    settingsEnum.name(),
                    String.valueOf(settingValue)
            );

            if (setting != null) {
                SettingsSingleton.getInstance().addSetting(setting);
            }
        }
    }
}