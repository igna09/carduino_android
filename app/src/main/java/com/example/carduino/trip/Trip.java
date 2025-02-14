package com.example.carduino.trip;

import android.os.Bundle;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.view.ViewTreeObserver;
import android.widget.GridLayout;

import androidx.annotation.NonNull;
import androidx.annotation.Nullable;
import androidx.fragment.app.Fragment;

import com.example.carduino.R;
import com.example.carduino.shared.models.trip.tripvalue.TripValue;
import com.example.carduino.shared.singletons.TripSingleton;
import com.example.carduino.shared.utilities.LoggerUtilities;
import com.example.carduino.trip.cards.TripCard;

import java.util.Arrays;
import java.util.List;
import java.util.stream.Collectors;

public class Trip extends Fragment {
    GridLayout gridLayout;
    List<TripCard> cards;

    final int VIEW_COLUMN_COUNT = 4;
    final int VIEW_ROW_COUNT = 3;
    final int MARGIN = 5;

    private Thread refreshThread;

    @Nullable
    @Override
    public View onCreateView(@NonNull LayoutInflater inflater, @Nullable ViewGroup container, @Nullable Bundle savedInstanceState) {
        View r = inflater.inflate(R.layout.fragment_trip, container, false);
        this.gridLayout = r.findViewById(R.id.trip_grid_view_container);

        com.example.carduino.shared.models.trip.Trip trip = TripSingleton.getInstance().getTrip();
        cards = Arrays.stream(TripCardEnum.values()).map(tripCardEnum -> {
            TripCard card = null;
            try {
                card = (TripCard) tripCardEnum.cardTripClass.newInstance();
            } catch (IllegalAccessException | java.lang.InstantiationException e) {
                throw new RuntimeException(e);
            }
            card.setColumn(tripCardEnum.column);
            card.setRow(tripCardEnum.row);
            card.setTripValueEnum(tripCardEnum.tripValueEnum);
            card.setTitle(tripCardEnum.label);
            card.setMargin(MARGIN);
            card.setValueTransformator(tripCardEnum.transformator);

            card.init();

            return card;
        }).collect(Collectors.toList());

        int viewColumnSpan = cards.stream().reduce(0, (acc, cur) -> acc + (cur.getColumnSpan() - 1), Integer::sum);
        int viewRowSpan = cards.stream().reduce(0, (acc, cur) -> acc + (cur.getRowSpan() - 1), Integer::sum);

        ViewTreeObserver vto = gridLayout.getViewTreeObserver();
        vto.addOnGlobalLayoutListener(new ViewTreeObserver.OnGlobalLayoutListener() {
            @Override
            public void onGlobalLayout() {
                gridLayout.getViewTreeObserver().removeOnGlobalLayoutListener(this);
                final int viewWidth  = gridLayout.getMeasuredWidth();
                final int viewHeight = gridLayout.getMeasuredHeight();

                cards.forEach(card -> {
                    card.setWidth((viewWidth - ((VIEW_COLUMN_COUNT - viewColumnSpan) * (MARGIN * 2))) / VIEW_COLUMN_COUNT);
                    card.setHeight((viewHeight - ((VIEW_ROW_COUNT - viewRowSpan) * (MARGIN * 2))) / VIEW_ROW_COUNT);

                    card.createCard(getContext());

                    gridLayout.addView(card.getCardView(), card.getLayoutParams());

                    if(card.getTripValueEnum()!= null && trip.getTripValues().containsKey(card.getTripValueEnum().name()) && trip.getTripValues().get(card.getTripValueEnum().name()) != null) {
                        TripValue v = (TripValue) trip.getTripValues().get(card.getTripValueEnum().name());
                        card.updateCard(v);
                    }
                });
            }
        });

        refreshThread = new Thread(() -> {
            while(refreshThread.isAlive() && !refreshThread.isInterrupted()) {
                if(getActivity() != null) {
                    getActivity().runOnUiThread(() -> cards.forEach(card -> {
                        TripValue tripValue = TripSingleton.getInstance().getTrip().getTripValues().get(card.getTripValueEnum());
                        card.updateCard(tripValue);
                    }));
                    try {
                        Thread.sleep(1000);
                    } catch (InterruptedException e) {
                        LoggerUtilities.logMessage("Trip Fragment", "refreshThread interrupted while sleeping");
                    }
                }
            }
        });
        refreshThread.start();

        return r;
    }

    @Override
    public void onViewCreated(@NonNull View view, @Nullable Bundle savedInstanceState) {
        super.onViewCreated(view, savedInstanceState);
    }

    @Override
    public void onDestroy() {
        // TODO: move in onPause?
        cards.forEach(cardModel -> {
//            CarStatusSingleton.getInstance().getCarStatus().removePropertyChangeListener(cardModel.propertyChangeListener);
            cardModel.setCardView(null);
        });

        super.onDestroy();
    }

    @Override
    public void onStop() {
        super.onStop();
        refreshThread.interrupt();
    }
}
