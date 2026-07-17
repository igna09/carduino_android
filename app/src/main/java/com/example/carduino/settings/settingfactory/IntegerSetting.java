package com.example.carduino.settings.settingfactory;

public class IntegerSetting extends Setting<Integer> {
    public IntegerSetting() {
    }

    public IntegerSetting(String id, String label) {
        super(id, label);
    }

    @Override
    public void setValueFromString(String value) {
        if(value == null) return;
        try {
            float f = Float.parseFloat(value);
            this.setValue((Integer) Math.round(f)); // 150.0 -> 150
        } catch (NumberFormatException e) {
            this.setValue(null);
            throw new IllegalArgumentException("Valore non numerico: " + value, e);
        }
    }
}
