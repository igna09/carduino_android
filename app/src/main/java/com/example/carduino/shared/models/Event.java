package com.example.carduino.shared.models;

import com.example.carduino.receivers.ArduinoMessageExecutorInterface;
import com.example.carduino.receivers.categoryexecutors.SensorCategoryExecutor;
import com.example.carduino.receivers.executors.LongPressExecutor;
import com.example.carduino.receivers.executors.ReadSettingExecutor;
import com.example.carduino.receivers.executors.SpeedLimitExecutor;
import com.example.carduino.shared.BaseEnum;
import com.example.carduino.shared.EventCategory;
import com.example.carduino.shared.EventExecutor;
import com.example.carduino.shared.PayloadType;

import java.util.Arrays;
import java.util.List;

public enum Event implements BaseEnum {
    // Eventi esistenti con la loro categoria (e opzionalmente un executor specifico)
    GET_SETTINGS(0x10, EventCategory.CONTROL),
    SWC_PAIR(0x0F, EventCategory.CONTROL),
    READ_SETTING(0x2D, EventCategory.CONTROL, ReadSettingExecutor.class, PayloadType.UINT8, PayloadType.FLOAT),

    // Nuovi eventi SENSOR (useranno l'executor di categoria "SENSOR")
    INTERNAL_TEMPERATURE(0x15, EventCategory.SENSOR, SensorCategoryExecutor.class, PayloadType.FLOAT),
    SPEED(0x16, EventCategory.SENSOR, SensorCategoryExecutor.class, PayloadType.UINT8),
    INTERNAL_LUMINANCE(0x17, EventCategory.SENSOR, SensorCategoryExecutor.class, PayloadType.UINT16),
    ENGINE_WATER_COOLING_TEMPERATURE(0x19, EventCategory.SENSOR, SensorCategoryExecutor.class, PayloadType.FLOAT),
    ENGINE_INTAKE_MANIFOLD_PRESSURE(0x1B, EventCategory.SENSOR, SensorCategoryExecutor.class, PayloadType.FLOAT),
    INTERNAL_PRESSURE(0x2E, EventCategory.SENSOR, SensorCategoryExecutor.class, PayloadType.UINT16),
    ENGINE_RPM(0x1C, EventCategory.SENSOR, SensorCategoryExecutor.class, PayloadType.UINT16),
    INJECTED_QUANTITY(0x20, EventCategory.SENSOR, SensorCategoryExecutor.class, PayloadType.FLOAT),
    FUEL_CONSUMPTION(0x21, EventCategory.SENSOR, SensorCategoryExecutor.class, PayloadType.FLOAT),
    BATTERY_VOLTAGE(0x22, EventCategory.SENSOR, SensorCategoryExecutor.class, PayloadType.FLOAT),
    SPEED_LIMIT_SET(0x2F, EventCategory.CONTROL, SpeedLimitExecutor.class, PayloadType.UINT8),
    LONG_PRESS(0x29, EventCategory.CONTROL, LongPressExecutor.class);

    private final int id;
    private final EventCategory category;
    private final Class<? extends ArduinoMessageExecutorInterface> specificExecutorClass;
    private final List<PayloadType> payloadTypes;

    Event(Integer id, EventCategory category, PayloadType... payloadTypes) {
        this(id, category, null, payloadTypes);
    }

    // Costruttore completo per eventi che richiedono un executor dedicato
    Event(Integer id, EventCategory category, Class<? extends ArduinoMessageExecutorInterface> specificExecutorClass, PayloadType... payloadTypes) {
        this.id = id;
        this.category = category;
        this.specificExecutorClass = specificExecutorClass;
        this.payloadTypes = Arrays.asList(payloadTypes);
    }

    @Override
    public Integer getId() { return id; }

    public EventCategory getCategory() { return category; }

    @Override
    public List<PayloadType> getPayloadTypes() { return payloadTypes; }

    public Class<? extends ArduinoMessageExecutorInterface> getExecutorClass() {
        return specificExecutorClass;
    }

    public static Event getEnumById(Integer id) {
        return Arrays.stream(Event.values())
                .filter(e -> e.getId() != null && e.getId().equals(id))
                .findFirst()
                .orElse(null);
    }

    public static Event getEnumByName(String name) {
        if (name == null) return null;
        String normalized = name.trim().toUpperCase();
        try {
            return Event.valueOf(normalized);
        } catch (IllegalArgumentException e) {
            return Arrays.stream(Event.values())
                    .filter(ev -> ev.name().equalsIgnoreCase(normalized))
                    .findFirst()
                    .orElse(null);
        }
    }
}