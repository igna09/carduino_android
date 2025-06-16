package com.example.carduino.shared.models;

import com.example.carduino.shared.BaseEnum;

import java.util.Arrays;

public enum Event implements BaseEnum {
    RESET_WEBAPP(0x0C),
    BLE_PAIRING_CODE(0x0D),
    RESTART(0x0E),
    SWC_PAIR(0x0F),
    GET_SETTINGS(0x10);

    private Integer id;

    Event(Integer id) {
        this.id = id;
    }

    public Integer getId() {
        return id;
    }

    public static BaseEnum getEnumById(Integer id) {
        return Arrays.stream(Event.values()).filter(mediaControlEnum -> mediaControlEnum.getId() != null && mediaControlEnum.getId().equals(id)).findFirst().orElse(null);
    }

    public static BaseEnum getEnumByName(String name) {
        return Event.valueOf(name);
    }
}
