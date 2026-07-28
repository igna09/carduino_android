package com.example.carduino.carstatus;

import com.example.carduino.carstatus.transformators.CardTransformator;
import com.example.carduino.carstatus.transformators.FloatTransformator;
import com.example.carduino.carstatus.transformators.TurboTransformator;
import com.example.carduino.shared.models.carstatus.CarStatusEnum;

public enum CarstatusCardEnum {
    SPEED("SPEED", CarStatusEnum.SPEED, 0, 0),
    RPM("RPM", CarStatusEnum.ENGINE_RPM, 0, 1),
    TURBO_PRESSURE("TURBO PRESSURE", CarStatusEnum.ENGINE_INTAKE_MANIFOLD_PRESSURE, 0, 2, new TurboTransformator()),
    COOLING_WATER("COOLING WATER", CarStatusEnum.ENGINE_WATER_COOLING_TEMPERATURE, 0, 3, new FloatTransformator("0.0")),
    FUEL_CONSUMPTION("FUEL CONSUMPTION", CarStatusEnum.FUEL_CONSUMPTION, 1, 0, new FloatTransformator("0.0")),
//    EXTERNAL_TEMPERATURE("EXTERNAL TEMPERATURE", CarStatusEnum.EXTERNAL_TEMPERATURE, 1, 2, new FloatTransformator("0.0")),
//    EXTERNAL_TEMPERATURE("BATTERY VOLTAGE", CarStatusEnum.BATTERY_VOLTAGE, 1, 2, new FloatTransformator("0.0")),
    BATTERY_VOLTAGE("BATTERY VOLTAGE", CarStatusEnum.BATTERY_VOLTAGE, 1, 1, new FloatTransformator("0.0")),
    INTERNAL_TEMPERATURE("INTERNAL TEMPERATURE", CarStatusEnum.INTERNAL_TEMPERATURE, 1, 3, new FloatTransformator("0.0")),
    SPEED_LIMIT("SPEED LIMIT", CarStatusEnum.SPEED_LIMIT, 1, 2),
    CRUISE_STATUS("CRUISE STATUS", CarStatusEnum.CRUISE_STATUS, 3, 0);

    String label;
    Integer row;
    Integer column;
    CarStatusEnum carstatusEnum;
    CardTransformator transformator;

    CarstatusCardEnum(String label, CarStatusEnum carstatusEnum, Integer row, Integer column) {
        this.label = label;
        this.row = row;
        this.column = column;
        this.carstatusEnum = carstatusEnum;
    }

    CarstatusCardEnum(String label, CarStatusEnum carstatusEnum, Integer row, Integer column, CardTransformator transformator) {
        this.label = label;
        this.row = row;
        this.column = column;
        this.carstatusEnum = carstatusEnum;
        this.transformator = transformator;
    }
}
