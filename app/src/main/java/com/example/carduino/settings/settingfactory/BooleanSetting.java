package com.example.carduino.settings.settingfactory;

public class BooleanSetting extends Setting<Boolean> {
    public BooleanSetting() {
    }

    public BooleanSetting(String id, String label) {
        super(id, label);
    }

    @Override
    public void setValueFromString(String value) {
        if(value != null) {
            this.setValue(value.equalsIgnoreCase("TRUE"));
        } else {
            this.setValue(false);
        }
    }
}
