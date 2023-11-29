package com.example.carduino.shared.models.carstatus.values;

public class Rpm extends ValueInteger {
    public Rpm(String id, Integer rpm) {
        super(id, rpm, "rpm");
    }

    public Rpm() {
        super(null, null, "rpm");
    }
}
