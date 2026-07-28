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
import androidx.recyclerview.widget.LinearLayoutManager;
import androidx.recyclerview.widget.RecyclerView;

import com.example.carduino.R;
import com.example.carduino.shared.models.trip.tripvalue.TripValue;
import com.example.carduino.shared.singletons.TripHistorySingleton;
import com.example.carduino.shared.utilities.LoggerUtilities;
import com.example.carduino.trip.cards.TripCard;

import java.util.Arrays;
import java.util.List;
import java.util.stream.Collectors;

public class TripFragment extends Fragment {
    GridLayout gridLayout;
    List<TripCard> cards;
    TripSummaryAdapter tripAdapter;

    int VIEW_COLUMN_COUNT;
    int VIEW_ROW_COUNT;
    final int MARGIN = 5;

    private Thread refreshThread;

    @Nullable
    @Override
    public View onCreateView(@NonNull LayoutInflater inflater, @Nullable ViewGroup container, @Nullable Bundle savedInstanceState) {
        View r = inflater.inflate(R.layout.fragment_trip, container, false);
        this.gridLayout = r.findViewById(R.id.lifetime_grid_container);

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

        VIEW_COLUMN_COUNT = cards.stream().mapToInt(c -> c.getColumn() + 1).max().orElse(1);
        VIEW_ROW_COUNT = cards.stream().mapToInt(c -> c.getRow() + 1).max().orElse(1);

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

                    if (card.getTripValueEnum() != null) {
                        TripValue v = TripHistorySingleton.getInstance().getLifetimeValue(card.getTripValueEnum());
                        if (v != null) card.updateCard(v);
                    }
                });
            }
        });

        // Colonna destra: storico trip scrollabile
        RecyclerView tripsRecyclerView = r.findViewById(R.id.trips_recycler_view);
        tripsRecyclerView.setLayoutManager(new LinearLayoutManager(getContext()));
        tripAdapter = new TripSummaryAdapter(TripHistorySingleton.getInstance().getAllTripsRecentFirst());
        tripsRecyclerView.setAdapter(tripAdapter);

        refreshThread = new Thread(() -> {
            while (refreshThread.isAlive() && !refreshThread.isInterrupted()) {
                if (getActivity() != null) {
                    getActivity().runOnUiThread(() -> {
                        cards.forEach(card -> {
                            if (card.getTripValueEnum() == null) return;
                            TripValue v = TripHistorySingleton.getInstance().getLifetimeValue(card.getTripValueEnum());
                            card.updateCard(v);
                        });
                        tripAdapter.updateTrips(TripHistorySingleton.getInstance().getAllTripsRecentFirst());
                    });
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