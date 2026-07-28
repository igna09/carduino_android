package com.example.carduino.trip.cards;

import android.content.Context;
import android.view.LayoutInflater;
import android.widget.TextView;

import androidx.cardview.widget.CardView;

import com.example.carduino.R;
import com.example.carduino.shared.models.carstatus.CarStatusEnum;
import com.example.carduino.shared.models.carstatus.values.Value;
import com.example.carduino.shared.models.trip.tripvalue.TripValue;

public class DistanceTripCard extends TripCard {
    private String unit;

    @Override
    public void init() {
        try {
            this.unit = ((Value) CarStatusEnum.valueOf(getTripValueEnum().name()).getType().newInstance()).getUnit();
        } catch (IllegalAccessException | java.lang.InstantiationException e) {
            throw new RuntimeException(e);
        }
    }

    @Override
    public void updateCard(TripValue value) {
        TextView avgTextView = getCardView().findViewById(R.id.value);
        if (value == null) {
            // Ripristina la UI allo stato iniziale/zero
            avgTextView.setText("-");
            return;
        }
        avgTextView.setText(getTransformedValue(value.getSum() != null ? value.getSum().toString() : null));
        avgTextView.requestLayout();
    }

    @Override
    public void createCard(Context context) {
        CardView v = (CardView) LayoutInflater.from(context).inflate(R.layout.card_trip_distance, null);

        TextView title = v.findViewById(R.id.title);
        title.setText(getTitle());
        TextView avgUnit = v.findViewById(R.id.unit);
        avgUnit.setText(this.unit);

        this.setCardView(v);
    }

    public String getUnit() {
        return unit;
    }

    public void setUnit(String unit) {
        this.unit = unit;
    }
}
