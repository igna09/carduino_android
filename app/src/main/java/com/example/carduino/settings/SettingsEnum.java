package com.example.carduino.settings;

import com.example.carduino.settings.settingfactory.BooleanSetting;
import com.example.carduino.settings.settingfactory.IntegerSetting;
import com.example.carduino.settings.settingviewfactory.BooleanSettingViewWrapper;
import com.example.carduino.settings.settingviewfactory.ButtonSettingViewWrapper;
import com.example.carduino.settings.settingviewfactory.IntegerSettingViewWrapper;
import com.example.carduino.shared.BaseEnum;
import com.example.carduino.shared.models.ArduinoMessage;
import com.example.carduino.shared.models.Event;
import com.example.carduino.shared.singletons.ArduinoSingleton;
import com.example.carduino.shared.singletons.SharedDataSingleton;
import com.example.carduino.shared.utilities.ArduinoMessageUtilities;

import java.util.Arrays;

public enum SettingsEnum implements BaseEnum {
    AUTO_BRIGHTNESS(null, "Auto brightness", BooleanSetting.class, SettingType.APP, BooleanSettingViewWrapper.class),
    MAX_BRIGHTNESS(null, "Max brightness", IntegerSetting.class, SettingType.APP, IntegerSettingViewWrapper.class),
    /*ON_REVERSE_LOWER_MIRRORS(0x04, "Lower mirrors on reverse", BooleanSetting.class, SettingType.ARDUINO, BooleanSettingViewWrapper.class, (value) -> {
        ArduinoMessageUtilities.sendArduinoMessage(new ArduinoMessage(CanbusActions.WRITE_SETTING, SettingsEnum.valueOf("ON_REVERSE_LOWER_MIRRORS").getId().toString(), (Boolean) value ? "TRUE" : "FALSE"));
    }),*/
    SPD_LMT_LRM(0x02, "Speed limit alarm", BooleanSetting.class, SettingType.ARDUINO, BooleanSettingViewWrapper.class, (value) -> {
        ArduinoMessageUtilities.sendArduinoMessage(new ArduinoMessage(Event.WRITE_SETTING, SettingsEnum.valueOf("SPD_LMT_LRM").getId(), toSettingFloat(value)));
    }),
    /*AUTO_CLOSE_REARVIEW_MIRRORS(0x00, "Auto close mirrors on turn off", BooleanSetting.class, SettingType.ARDUINO, BooleanSettingViewWrapper.class, (value) -> {
        ArduinoMessageUtilities.sendArduinoMessage(new ArduinoMessage(CanbusActions.WRITE_SETTING, SettingsEnum.valueOf("AUTO_CLOSE_REARVIEW_MIRRORS").getId().toString(), (Boolean) value ? "TRUE" : "FALSE"));
    }),*/
    ADVANCED_MODE(null, "Advanced mode", BooleanSetting.class, SettingType.APP, BooleanSettingViewWrapper.class, (value) -> {
        SharedDataSingleton.getInstance().setAdvancedMode((Boolean) value);
    }),
    SWC_PAIR(null, "SWC pairing", BooleanSetting.class, SettingType.APP, ButtonSettingViewWrapper.class, (value) -> {
        ArduinoMessageUtilities.sendArduinoMessage(new ArduinoMessage(Event.SWC_PAIR));
    }),
    /*BLE_PAIRING(0x05, "Enter BLE pairing mode", BooleanSetting.class, SettingType.ARDUINO, BooleanSettingViewWrapper.class, (value) -> {
        ArduinoMessageUtilities.sendArduinoMessage(new ArduinoMessage(CanbusActions.WRITE_SETTING, SettingsEnum.valueOf("BLE_PAIRING").getId().toString(), (Boolean) value ? "TRUE" : "FALSE"));
    }),*/
    RESTART(0x02, "Restart all nodes", BooleanSetting.class, SettingType.APP, ButtonSettingViewWrapper.class, (value) -> {
        ArduinoMessageUtilities.sendArduinoMessage(new ArduinoMessage(Event.RESTART));
    }),
    TMP_SWC_PRESS_T(0x00, "SWC press time", IntegerSetting.class, SettingType.ARDUINO, IntegerSettingViewWrapper.class, (value) -> {
        ArduinoMessageUtilities.sendArduinoMessage(new ArduinoMessage(Event.WRITE_SETTING, SettingsEnum.valueOf("TMP_SWC_PRESS_T").getId(), toSettingFloat(value)));
    }),
    HANDLE_KLINE(0x01, "Send kline messages to head unit", BooleanSetting.class, SettingType.ARDUINO, BooleanSettingViewWrapper.class, (value) -> {
        ArduinoMessageUtilities.sendArduinoMessage(new ArduinoMessage(Event.WRITE_SETTING, SettingsEnum.valueOf("HANDLE_KLINE").getId(), toSettingFloat(value)));
    }),
    LOG_SND_RCV_MSG(0x03, "Log received and sent messages", BooleanSetting.class, SettingType.ARDUINO, BooleanSettingViewWrapper.class, (value) -> {
        ArduinoMessageUtilities.sendArduinoMessage(new ArduinoMessage(Event.WRITE_SETTING, SettingsEnum.valueOf("LOG_SND_RCV_MSG").getId(), toSettingFloat(value)));
    });

    public enum SettingType {
        APP,
        ARDUINO
    }

    private String label;
    private Class settingValueType;
    private Class settingViewType;
    private SettingCallback settingCallback;
    private SettingType settingType;
    private Integer id;

    SettingsEnum(Integer id, String label, Class settingValueType, SettingType settingType, Class settingViewType, SettingCallback settingCallback) {
        this.id = id;
        this.settingValueType = settingValueType;
        this.label = label;
        this.settingViewType = settingViewType;
        this.settingCallback = settingCallback;
        this.settingType = settingType;
    }

    SettingsEnum(Integer id, String label, Class settingValueType, SettingType settingType) {
        this(id, label, settingValueType, settingType, null, null);
    }

    SettingsEnum(Integer id, String label, Class settingValueType, SettingType settingType, Class settingViewType) {
        this(id, label, settingValueType, settingType, settingViewType, null);
    }

    public String getLabel() {
        return label;
    }

    public Class getSettingValueType() {
        return settingValueType;
    }

    public Class getSettingViewType() {
        return settingViewType;
    }

    public SettingCallback getSettingCallback() {
        return settingCallback;
    }

    public SettingType getSettingType() {
        return settingType;
    }

    public Integer getId() {
        return id;
    }

    public void setId(Integer id) {
        this.id = id;
    }

    public static BaseEnum getEnumById(Integer id) {
        return Arrays.stream(SettingsEnum.values()).filter(carStatusEnum -> carStatusEnum.getId() != null && carStatusEnum.getId().equals(id)).findFirst().orElse(null);
    }

    public static BaseEnum getEnumByName(String name) {
        return SettingsEnum.valueOf(name);
    }

    private static float toSettingFloat(Object v) {
        if (v instanceof Float) return (Float) v;
        if (v instanceof Boolean) return ((Boolean) v) ? 1.0f : 0.0f;
        if (v instanceof Number) return ((Number) v).floatValue();
        throw new IllegalArgumentException("Tipo non convertibile in float per SETTINGS: " + v.getClass());
    }
}
