package com.example.carduino.shared.models.carstatus;

import com.example.carduino.shared.BaseEnum;
import com.example.carduino.shared.models.carstatus.propertychangelisteners.FuelConsumptionCarStatusPropertyChangeListener;
import com.example.carduino.shared.models.carstatus.propertychangelisteners.InternalLuminanceCarStatusPropertyChangeListener;
import com.example.carduino.shared.models.carstatus.propertychangelisteners.RpmCarStatusPropertyChangeListener;
import com.example.carduino.shared.models.carstatus.propertychangelisteners.SpeedCarStatusPropertyChangeListener;
import com.example.carduino.shared.models.carstatus.values.CelsiusTemperature;
import com.example.carduino.shared.models.carstatus.values.CmDistance;
import com.example.carduino.shared.models.carstatus.values.FuelConsumptionKmL;
import com.example.carduino.shared.models.carstatus.values.InjectedQuantity;
import com.example.carduino.shared.models.carstatus.values.KmDistance;
import com.example.carduino.shared.models.carstatus.values.KmhSpeed;
import com.example.carduino.shared.models.carstatus.values.BarPressure;
import com.example.carduino.shared.models.carstatus.values.LuxLuminance;
import com.example.carduino.shared.models.carstatus.values.Rpm;
import com.example.carduino.shared.models.carstatus.values.ValueBoolean;
import com.example.carduino.shared.models.carstatus.values.ValueFloat;
import com.example.carduino.shared.models.carstatus.values.ValueInteger;
import com.example.carduino.shared.models.carstatus.values.ValueString;
import com.example.carduino.shared.models.carstatus.values.Voltage;
import com.example.carduino.shared.models.trip.tripvalue.FloatTripValue;
import com.example.carduino.shared.models.trip.tripvalue.IntegerTripValue;

import java.util.Arrays;

public enum CarStatusEnum implements BaseEnum {
    EXTERNAL_TEMPERATURE(0x00, Category.CAR, CelsiusTemperature.class),
    INTERNAL_TEMPERATURE(0x01, Category.CAR, CelsiusTemperature.class),
    SPEED(0x02, Category.CAR, KmhSpeed.class, SpeedCarStatusPropertyChangeListener.class, IntegerTripValue.class),
    INTERNAL_LUMINANCE(0x03, Category.CAR, LuxLuminance.class, InternalLuminanceCarStatusPropertyChangeListener.class),
    FRONT_DISTANCE(0x04, Category.CAR, CmDistance.class),
    ENGINE_WATER_COOLING_TEMPERATURE(0x05, Category.ENGINE, CelsiusTemperature.class/*, EngineWaterCoolingTemperatureCarStatusPropertyChangeListener.class*/),
    ENGINE_OIL_TEMPERATURE(0x06, Category.ENGINE, CelsiusTemperature.class),
    ENGINE_INTAKE_MANIFOLD_PRESSURE(0x07, Category.ENGINE, BarPressure.class),
    ENGINE_RPM(0x08, Category.ENGINE, Rpm.class, RpmCarStatusPropertyChangeListener.class),
    //TRIP_DURATION(0x09, Category.TRIP, Duration.class),
    TRIP_AVERAGE_SPEED(0x0A, Category.TRIP, KmhSpeed.class),
    TRIP_MAX_SPEED(0x0B, Category.TRIP, KmhSpeed.class),
    INJECTED_QUANTITY(0x0C, Category.CAR, InjectedQuantity.class),
    FUEL_CONSUMPTION(0x0D, Category.CAR, FuelConsumptionKmL.class, FuelConsumptionCarStatusPropertyChangeListener.class, FloatTripValue.class),
    ENGINE_STARTED(null, Category.CAR, ValueBoolean.class),
    BATTERY_VOLTAGE(0x0E, Category.CAR, Voltage.class),
    DISTANCE(null, Category.CAR, KmDistance.class, null, FloatTripValue.class),
    IS_REVERSE(0x10, Category.CAR, ValueBoolean.class),
    IS_KEY_ON(0x11, Category.CAR, ValueBoolean.class),
    SPEED_LIMIT(0x12, Category.CAR, KmhSpeed.class),
    CRUISE_STATUS(0x13, Category.CAR, ValueString.class);

    private enum Category {
        ENGINE,
        TRIP,
        CAR;
    }

    private Integer id;
    private Category category;
    private Class type;
    private Class propertyChangeListener;
    private Class tripType;

    CarStatusEnum(Integer id, Category category, Class type, Class propertyChangeListener, Class tripType) {
        this.id = id;
        this.category = category;
        this.type = type;
        this.propertyChangeListener = propertyChangeListener;
        this.tripType = tripType;
    }

    CarStatusEnum(Integer id, Category category, Class type, Class propertyChangeListener) {
        this.id = id;
        this.category = category;
        this.type = type;
        this.propertyChangeListener = propertyChangeListener;
        this.tripType = null;
    }

    CarStatusEnum(Integer id, Category category, Class type) {
        this.id = id;
        this.category = category;
        this.type = type;
        this.propertyChangeListener = null;
        this.tripType = null;
    }

    public Integer getId() {
        return id;
    }

    public Category getCategory() {
        return category;
    }

    public Class getType() {
        return type;
    }

    public Class getPropertyChangeListener() {
        return propertyChangeListener;
    }

    public static BaseEnum getEnumById(Integer id) {
        return Arrays.stream(CarStatusEnum.values()).filter(carStatusEnum -> carStatusEnum.getId() != null && carStatusEnum.getId().equals(id)).findFirst().orElse(null);
    }

    public static BaseEnum getEnumByName(String name) {
        return CarStatusEnum.valueOf(name);
    }

    public Class getTripType() {
        return tripType;
    }
}
