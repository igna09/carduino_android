package com.example.carduino.trip.cards;

import android.content.Context;
import android.view.LayoutInflater;

import androidx.appcompat.widget.AppCompatButton;
import androidx.cardview.widget.CardView;

import com.example.carduino.R;
import com.example.carduino.shared.models.trip.tripvalue.TripValue;
import com.example.carduino.shared.singletons.TripHistorySingleton;
import com.example.carduino.shared.utilities.LoggerUtilities;

import java.io.IOException;

public class ButtonsTripCard extends TripCard {

    @Override
    public void updateCard(TripValue value) {}

    @Override
    public void createCard(Context context) {
        CardView v = (CardView) LayoutInflater.from(context).inflate(R.layout.card_trip_buttons, null);

        AppCompatButton resetButton = v.findViewById(R.id.reset_button);
        resetButton.setOnClickListener(e -> {
            try {
                TripHistorySingleton.getInstance().resetAllTrips();
            } catch (IOException exception) {
                LoggerUtilities.logException(exception);
            }
        });
        /*AppCompatButton restoreButton = v.findViewById(R.id.restore_button);
        restoreButton.setOnClickListener(e -> {
            try {
                TripHistorySingleton.getInstance().loadTrips();
            } catch (Exception exception) {
                LoggerUtilities.logException(exception);
            }
        });*/

        this.setCardView(v);
    }
}
