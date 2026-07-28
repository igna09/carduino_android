package com.example.carduino.shared.models.trip;

import com.example.carduino.shared.models.trip.tripvalue.TripValue;
import com.example.carduino.shared.models.trip.tripvalue.TripValueEnum;

import java.util.Date;
import java.util.HashMap;
import java.util.Map;
import java.util.UUID;

public class Trip {
    private String id;
    private Boolean started;
    private Date startDate;
    private Date endDate;
    private Date lastUpdate;
    private Map<TripValueEnum, TripValue> tripValues;

    public Trip() {
        id = UUID.randomUUID().toString();
        started = false;
        startDate = null;
        endDate = null;
        tripValues = new HashMap<>();
    }

    public Boolean isStarted() { return started; }
    public String getId() { return id; }
    public Date getBeginDate() { return startDate; }
    public Date getEndDate() { return endDate; }

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

    public Date getLastUpdate() {
        return lastUpdate;
    }

    public void setLastUpdate(Date lastUpdate) {
        this.lastUpdate = lastUpdate;
    }

    public void touch() {
        this.lastUpdate = new Date();
    }

    public void startTrip() {
        this.startDate = new Date();
        this.lastUpdate = new Date();
        this.started = true;
    }

    // Overload per chiudere il trip specificando una data precisa (es. lastUpdate)
    public void stopTrip(Date end) {
        this.endDate = end;
        this.started = false;
    }

    public void stopTrip() {
        stopTrip(new Date());
    }
}
