package com.example.carduino.trip;

import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.TextView;

import androidx.annotation.NonNull;
import androidx.recyclerview.widget.RecyclerView;

import com.example.carduino.R;
import com.example.carduino.shared.models.trip.tripvalue.TripValue;
import com.example.carduino.shared.models.trip.tripvalue.TripValueEnum;

import java.text.SimpleDateFormat;
import java.util.List;
import java.util.Locale;

public class TripSummaryAdapter extends RecyclerView.Adapter<TripSummaryAdapter.VH> {
    private List<Trip> trips;
    private final SimpleDateFormat sdf = new SimpleDateFormat("dd/MM HH:mm", Locale.getDefault());

    public TripSummaryAdapter(List<Trip> trips) { this.trips = trips; }

    public void updateTrips(List<Trip> newTrips) {
        this.trips = newTrips;
        notifyDataSetChanged();
    }

    @NonNull
    @Override
    public VH onCreateViewHolder(@NonNull ViewGroup parent, int viewType) {
        View v = LayoutInflater.from(parent.getContext()).inflate(R.layout.item_trip_summary, parent, false);
        return new VH(v);
    }

    @Override
    public void onBindViewHolder(@NonNull VH h, int position) {
        Trip t = trips.get(position);
        h.date.setText(t.getBegin() != null ? sdf.format(t.getBegin()) : "-");

        TripValue dist = t.getTripValues().get(TripValueEnum.DISTANCE);
        TripValue speed = t.getTripValues().get(TripValueEnum.SPEED);
        TripValue fuel = t.getTripValues().get(TripValueEnum.FUEL_CONSUMPTION);

        String s = String.format(Locale.getDefault(), "Dist: %s km | Vel media: %s | Consumo: %s",
                dist != null ? dist.getSum() : "-",
                speed != null ? speed.getAverage() : "-",
                fuel != null ? fuel.getAverage() : "-");
        h.summary.setText(s + (t.isStarted() ? " (in corso)" : ""));
    }

    @Override public int getItemCount() { return trips.size(); }

    static class VH extends RecyclerView.ViewHolder {
        TextView date, summary;
        VH(View v) {
            super(v);
            date = v.findViewById(R.id.trip_date);
            summary = v.findViewById(R.id.trip_summary);
        }
    }
}