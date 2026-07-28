package com.example.carduino.shared.models.trip;

import com.example.carduino.shared.models.carstatus.CarStatusEnum;
import com.example.carduino.shared.models.trip.tripvalue.FloatTripValue;
import com.example.carduino.shared.models.trip.tripvalue.IntegerTripValue;
import com.example.carduino.shared.models.trip.tripvalue.TripValue;
import com.example.carduino.shared.models.trip.tripvalue.TripValueEnum;

import java.util.Date;
import java.util.HashMap;
import java.util.Map;
import java.util.UUID;

public class Trip {
    private String id;
    private Boolean started;
    private Date begin;
    private Date end;
    private Map<TripValueEnum, TripValue> tripValues;

    public Trip() {
        id = UUID.randomUUID().toString();
        started = false;
        begin = null;
        end = null;
        tripValues = new HashMap<>();
    }

    public void startTrip() { started = true; begin = new Date(); }
    public void stopTrip()  { started = false; end = new Date(); }

    public Boolean isStarted() { return started; }
    public String getId() { return id; }
    public Date getBegin() { return begin; }
    public Date getEnd() { return end; }

    public void addTripValue(TripValueEnum tripValueEnum, Object value) {
        if(!tripValues.containsKey(tripValueEnum)) {
            try {
                TripValue tv = (TripValue) tripValueEnum.getClazz().newInstance();
                tv.setTripValueEnum(tripValueEnum);
                tripValues.put(tripValueEnum, tv);
            } catch (IllegalAccessException | InstantiationException e) { throw new RuntimeException(e); }
        }
        tripValues.get(tripValueEnum).addValue(value);
    }

    public Map<TripValueEnum, TripValue> getTripValues() { return tripValues; }
}
