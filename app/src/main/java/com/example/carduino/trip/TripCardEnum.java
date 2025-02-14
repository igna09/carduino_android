package com.example.carduino.trip;

import com.example.carduino.shared.Transformator;
import com.example.carduino.shared.models.carstatus.CarStatusEnum;
import com.example.carduino.shared.models.trip.tripvalue.TripValueEnum;
import com.example.carduino.trip.cards.AvgMaxTripCard;
import com.example.carduino.trip.cards.ButtonsTripCard;
import com.example.carduino.trip.cards.DistanceTripCard;
import com.example.carduino.trip.cards.RoadTripCard;
import com.example.carduino.trip.transformators.CardTransformator;
import com.example.carduino.trip.transformators.FloatTransformator;

public enum TripCardEnum {
    SPEED("SPEED", TripValueEnum.SPEED, 0, 0, AvgMaxTripCard.class, new FloatTransformator("0.0")),
    FUEL_CONSUMPTION("FUEL CONSUMPTION", TripValueEnum.FUEL_CONSUMPTION, 0, 1, AvgMaxTripCard.class, new FloatTransformator("0.0")),
    DISTANCE("DISTANCE", TripValueEnum.DISTANCE, 0, 2, DistanceTripCard.class, new FloatTransformator("0.0")),
    BUTTONS(null, null, 0, 3, ButtonsTripCard.class, null),
    ROAD("ROAD", null, 1, 0, RoadTripCard.class, null);

    String label;
    Integer row;
    Integer column;
    TripValueEnum tripValueEnum;
    Class cardTripClass;
    CardTransformator transformator;

    TripCardEnum(String label, TripValueEnum carstatusEnum, Integer row, Integer column, Class cardTripClass, CardTransformator transformator) {
        this.label = label;
        this.row = row;
        this.column = column;
        this.tripValueEnum = carstatusEnum;
        this.cardTripClass = cardTripClass;
        this.transformator = transformator;
    }
}
