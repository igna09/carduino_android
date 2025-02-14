package com.example.carduino.trip.cards;

import android.content.Context;
import android.view.LayoutInflater;
import android.widget.TextView;

import androidx.cardview.widget.CardView;

import com.example.carduino.R;
import com.example.carduino.shared.models.RoadInfo;
import com.example.carduino.shared.models.carstatus.CarStatusEnum;
import com.example.carduino.shared.models.carstatus.values.Value;
import com.example.carduino.shared.models.trip.tripvalue.TripValue;
import com.example.carduino.shared.singletons.SharedDataSingleton;

public class RoadTripCard extends TripCard {
    private String unit;

    @Override
    public void init() {}

    @Override
    public void updateCard(TripValue value) {
        RoadInfo roadInfo = SharedDataSingleton.getInstance().getRoadInfo();

        TextView nameTextView = getCardView().findViewById(R.id.name);
        nameTextView.setText(roadInfo.getName() != null ? roadInfo.getName() : "N/A");
        nameTextView.requestLayout();

        TextView limitTextView = getCardView().findViewById(R.id.limit);
        limitTextView.setText(roadInfo.getLimit() != null ? roadInfo.getLimit().toString() : "N/A");
        limitTextView.requestLayout();
    }

    @Override
    public void createCard(Context context) {
        CardView v = (CardView) LayoutInflater.from(context).inflate(R.layout.card_trip_road, null);

        TextView title = v.findViewById(R.id.title);
        title.setText(getTitle());

        this.setCardView(v);
    }

    public String getUnit() {
        return unit;
    }

    public void setUnit(String unit) {
        this.unit = unit;
    }
}
