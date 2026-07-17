package com.example.carduino.shared;

import com.example.carduino.receivers.ArduinoMessageExecutorInterface;
import com.example.carduino.receivers.categoryexecutors.SensorCategoryExecutor;

public enum EventCategory {
    // Gli executor vengono istanziati qui staticamente (una sola volta all'avvio)
    SENSOR(new SensorCategoryExecutor()),
    CONTROL(),
    SETTINGS();

    private final ArduinoMessageExecutorInterface executor;

    EventCategory(ArduinoMessageExecutorInterface executor) {
        this.executor = executor;
    }

    EventCategory() {
        this.executor = null;
    }

    public ArduinoMessageExecutorInterface getExecutor() {
        return this.executor;
    }
}
