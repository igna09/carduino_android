package com.example.carduino.shared.models.trip.tripvalue;

import java.util.Date;

public class IntegerTripValue extends TripValue<Integer> {
    public IntegerTripValue() {
        setAverage(0);
        setMax(0);
        setSum(0);
        setReadings(0);
    }

    @Override
    public void addValue(Integer value) {
        super.addValue(value);
        if(value > getMax()) {
            setMax(value);
        }
        setReadings(getReadings() + 1);
        setSum(getSum() + value);
        setAverage(getSum() / getReadings());
    }

    @Override
    public void mergeFrom(TripValue<Integer> other) {
        if (other.getMax() > getMax()) setMax(other.getMax());
        setReadings(getReadings() + other.getReadings());
        setSum(getSum() + other.getSum());
        setAverage(getReadings() > 0 ? getSum() / getReadings() : 0);
    }
}
