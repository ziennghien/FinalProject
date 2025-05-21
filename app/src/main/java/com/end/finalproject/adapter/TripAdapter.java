package com.end.finalproject.adapter;

import android.content.Context;
import android.content.Intent;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.TextView;

import androidx.annotation.NonNull;
import androidx.recyclerview.widget.RecyclerView;

import com.end.finalproject.R;
import com.end.finalproject.model.Trip;
import com.end.finalproject.entertainment.ChonGheActivity;

import java.util.List;

public class TripAdapter extends RecyclerView.Adapter<TripAdapter.TripViewHolder> {
    private final List<Trip> trips;

    public TripAdapter(List<Trip> trips, Object o) {
        this.trips = trips;
    }

    @NonNull
    @Override
    public TripViewHolder onCreateViewHolder(@NonNull ViewGroup parent, int viewType) {
        View v = LayoutInflater.from(parent.getContext())
                .inflate(R.layout.item_chuyen_di, parent, false);
        return new TripViewHolder(v);
    }

    @Override
    public void onBindViewHolder(@NonNull TripViewHolder holder, int position) {
        Trip t = trips.get(position);
        holder.tvDepartureDestination.setText(
                t.getDeparture() + " - " + t.getDestination()
        );
        holder.tvBusCompany.setText(t.getBusCompany());
        holder.tvSeat.setText("Còn " + t.getSeat() + " chỗ");
        holder.tvPrice.setText(String.format("%,d VND", t.getPrice()));

        holder.itemView.setOnClickListener(v -> {
            Context ctx = v.getContext();
            Intent intent = new Intent(ctx, ChonGheActivity.class);
            intent.putExtra("departure", t.getDeparture());
            intent.putExtra("destination", t.getDestination());
            intent.putExtra("busCompany", t.getBusCompany());
            intent.putExtra("dateDepart", t.getDateDepart());
            intent.putExtra("price", t.getPrice());
            intent.putExtra("seat", t.getSeat());
            ctx.startActivity(intent);
        });
    }

    @Override
    public int getItemCount() { return trips.size(); }

    static class TripViewHolder extends RecyclerView.ViewHolder {
        TextView tvDepartureDestination, tvBusCompany, tvSeat, tvPrice;

        public TripViewHolder(@NonNull View itemView) {
            super(itemView);
            tvDepartureDestination = itemView.findViewById(R.id.tvDepartureDestination);
            tvBusCompany           = itemView.findViewById(R.id.tvBusCompany);
            tvSeat                 = itemView.findViewById(R.id.tvSeat);
            tvPrice                = itemView.findViewById(R.id.tvPrice);
        }
    }
}
