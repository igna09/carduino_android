package com.example.carduino.settings.factory;

public class SettingsFactory {
    public static Setting getSetting(String id, String settingType, Object value, String unit) {
        Setting setting;

        switch (SettingsEnum.valueOf(settingType)) {
            case BOOLEAN:
                Boolean booleanSetting;
                    if(value instanceof String) {
                        booleanSetting = ((String) value).equals("true");
                    } else {
                    booleanSetting = (Boolean) value;
                }
                return new BooleanSetting(id, booleanSetting, unit);
            case INTEGER:
                Integer integerValue;
                if(value instanceof String) {
                    integerValue = Integer.valueOf((String) value);
                } else {
                    integerValue = (Integer) value;
                }
                return new IntegerSetting(id, integerValue, unit);
            case FLOAT:
                Float floatValue;
                if(value instanceof String) {
                    floatValue = Float.valueOf((String) value);
                } else {
                    floatValue = (Float) value;
                }
                return new FloatSetting(id, floatValue, unit);
        }

        return null;
    }

    public static Setting getSetting(String id, String settingType, Object value) {
        return getSetting(id, settingType, value, null);
    }
}
