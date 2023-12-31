package com.example.carduino.settings.settingfactory;

import com.example.carduino.settings.SettingsEnum;

public class SettingsFactory {
    public static Setting getSetting(String id, String value) {
        Setting setting;
        try {
            setting =  (Setting) SettingsEnum.valueOf(id).getSettingType().newInstance();
        } catch (IllegalAccessException | InstantiationException e) {
            throw new RuntimeException(e);
        }

        setting.setId(id);
        setting.setLabel(SettingsEnum.valueOf(id).getLabel());
        setting.setValueFromString(value);
//        setting.generateView();

        return setting;
    }
}
