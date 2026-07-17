package com.example.carduino.settings.settingfactory;

public class FloatSetting extends Setting<Float> {
    public FloatSetting() {
    }

    public FloatSetting(String id, String label) {
        super(id, label);
    }

    @Override
    public void setValueFromString(String value) {
        if(value == null) return;
        try {
            float f = Float.parseFloat(value);
            this.setValue(f);
        } catch (NumberFormatException e) {
            this.setValue(null);
            throw new IllegalArgumentException("Valore non numerico: " + value, e);
        }
    }
}
