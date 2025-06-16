package com.example.carduino.receivers.canbus.factory;

import com.example.carduino.receivers.canbus.factory.actions.CarStatusAction;
import com.example.carduino.receivers.canbus.factory.actions.EventAction;
import com.example.carduino.receivers.canbus.factory.actions.LogAction;
import com.example.carduino.receivers.canbus.factory.actions.MediaControlAction;
import com.example.carduino.receivers.canbus.factory.actions.SettingAction;
import com.example.carduino.settings.SettingsEnum;
import com.example.carduino.shared.BaseEnum;
import com.example.carduino.shared.models.MediaControl;
import com.example.carduino.shared.models.carstatus.CarStatusEnum;
import com.example.carduino.shared.models.Event;

import java.util.Arrays;
import java.util.function.Function;

public enum CanbusActions implements BaseEnum {
    CAR_STATUS(0x00, CarStatusAction.class, CarStatusEnum::getEnumById, CarStatusEnum::getEnumByName),
    READ_SETTING(0x01, SettingAction.class, SettingsEnum::getEnumById, SettingsEnum::getEnumByName),
    MEDIA_CONTROL(0x02, MediaControlAction.class, MediaControl::getEnumById, MediaControl::getEnumByName),
    WRITE_SETTING(0x03),
    LOG(0x04, LogAction.class, null, null),
//    ERROR(0x05),
    EVENT(0x06, EventAction.class, Event::getEnumById, Event::getEnumByName);
    // GET_SETTINGS(0x07);

    private Class clazz;
    private Integer id;
    Function<Integer, BaseEnum> actionEnumFromId;
    Function<String, BaseEnum> actionEnumFromName;

    CanbusActions(Integer id, Class clazz, Function<Integer, BaseEnum> actionEnumFromId, Function<String, BaseEnum> actionEnumFromName) {
        this.clazz = clazz;
        this.id = id;
        this.actionEnumFromId = actionEnumFromId;
        this.actionEnumFromName = actionEnumFromName;
    }

    CanbusActions(Integer id) {
        this(id, null, null, null);
    }

    public Class getClazz() {
        return clazz;
    }

    public void setClazz(Class clazz) {
        this.clazz = clazz;
    }

    public Integer getId() {
        return id;
    }

    public Function<Integer, BaseEnum> getActionEnumById() {
        return actionEnumFromId;
    }

    public Function<String, BaseEnum> getActionEnumFromName() {
        return actionEnumFromName;
    }

    public static BaseEnum getEnumById(Integer id) {
        return Arrays.stream(CanbusActions.values()).filter(canbusActions -> canbusActions.getId() != null && canbusActions.getId().equals(id)).findFirst().orElse(null);
    }
}
