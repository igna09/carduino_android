package com.example.carduino.shared.models.carstatus.values;

public class ValueString extends Value<String> {
    public ValueString(String id, String value, String unit) {
        super(id, value, unit);
    }

    public ValueString() {super();}

    @Override
    public String parseValueFromString(String value) {
        return value;
    }
}
