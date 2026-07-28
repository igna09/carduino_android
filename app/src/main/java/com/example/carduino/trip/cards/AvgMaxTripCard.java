package com.example.carduino.trip.cards;

import android.content.Context;
import android.view.LayoutInflater;
import android.widget.TextView;

import androidx.cardview.widget.CardView;

import com.example.carduino.R;
import com.example.carduino.shared.models.carstatus.CarStatusEnum;
import com.example.carduino.shared.models.carstatus.values.Value;
import com.example.carduino.shared.models.trip.tripvalue.TripValue;

public class AvgMaxTripCard extends TripCard {
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
        TextView avgTextView = getCardView().findViewById(R.id.avg_value);
        TextView maxTextView = getCardView().findViewById(R.id.max_value);
        if (value == null) {
            // Ripristina la UI allo stato iniziale/zero
            avgTextView.setText("-");
            maxTextView.setText("-");
            return;
        }
        avgTextView.setText(getTransformedValue(value.getAverage() != null ? value.getAverage().toString() : null));
        avgTextView.requestLayout();
        maxTextView.setText(getTransformedValue(value.getMax() != null ? value.getMax().toString() : null));
        maxTextView.requestLayout();
    }

    @Override
    public void createCard(Context context) {
        CardView v = (CardView) LayoutInflater.from(context).inflate(R.layout.card_trip, null);

        TextView title = v.findViewById(R.id.title);
        title.setText(getTitle());
        TextView avgUnit = v.findViewById(R.id.avg_unit);
        avgUnit.setText(this.unit);
        TextView maxUnit = v.findViewById(R.id.max_unit);
        maxUnit.setText(this.unit);

        this.setCardView(v);
    }

    public String getUnit() {
        return unit;
    }

    public void setUnit(String unit) {
        this.unit = unit;
    }
}
