package com.example.carduino.shared;

import com.example.carduino.shared.models.trip.tripvalue.TripValue;
import com.example.carduino.shared.models.trip.tripvalue.TripValueEnum;
import com.google.gson.JsonDeserializationContext;
import com.google.gson.JsonDeserializer;
import com.google.gson.JsonElement;
import com.google.gson.JsonParseException;

import java.lang.reflect.Type;

public class TripValueDeserializer implements JsonDeserializer<TripValue> {
    @Override
    public TripValue deserialize(JsonElement json, Type typeOfT, JsonDeserializationContext context) throws JsonParseException {
        JsonElement jsonElement = json.getAsJsonObject().get("tripValueEnum");
        if (jsonElement != null) {
            String stringType = jsonElement.getAsString();
            TripValueEnum type;
            try {
                type = TripValueEnum.valueOf(stringType);
            } catch (IllegalArgumentException e) {
                type = null;
            }
            if (type == null) {
                throw new IllegalArgumentException("No TripValueEnum exists for " + stringType);
            }
            return context.deserialize(json, type.getClazz());
        } else {
            return new TripValue() {
                @Override
                public void addValue(Object value) {
                    super.addValue(value);
                }
                @Override
                public void mergeFrom(TripValue other) {}
            };
        }
    }
}