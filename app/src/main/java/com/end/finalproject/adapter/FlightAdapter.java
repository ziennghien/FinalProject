package com.end.finalproject.adapter;

import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.TextView;

import androidx.annotation.NonNull;
import androidx.recyclerview.widget.RecyclerView;

import com.end.finalproject.R;
import com.end.finalproject.model.Flight;

import java.util.List;

public class FlightAdapter extends RecyclerView.Adapter<FlightAdapter.FlightViewHolder> {
    public interface OnItemClickListener {
        void onItemClick(Flight flight);
    }

    private List<Flight> flights;
    private OnItemClickListener listener;

    public FlightAdapter(List<Flight> flights, OnItemClickListener listener) {
        this.flights = flights;
        this.listener = listener;
    }

    @NonNull
    @Override
    public FlightViewHolder onCreateViewHolder(@NonNull ViewGroup parent, int viewType) {
        View view = LayoutInflater.from(parent.getContext())
                .inflate(R.layout.item_ve_may_bay, parent, false);
        return new FlightViewHolder(view);
    }

    @Override
    public void onBindViewHolder(@NonNull FlightViewHolder holder, int position) {
        Flight flight = flights.get(position);
        holder.bind(flight, listener);
    }

    @Override
    public int getItemCount() {
        return flights.size();
    }

    static class FlightViewHolder extends RecyclerView.ViewHolder {
        TextView tvCompany, tvRoute, tvDate, tvAvailableSeats, tvPrice;

        public FlightViewHolder(@NonNull View itemView) {
            super(itemView);
            tvCompany = itemView.findViewById(R.id.tvCompany);
            tvRoute = itemView.findViewById(R.id.tvRoute);
            tvDate = itemView.findViewById(R.id.tvDate);
            tvAvailableSeats = itemView.findViewById(R.id.tvAvailableSeats);
            tvPrice = itemView.findViewById(R.id.tvPrice);
        }

        public void bind(final Flight flight, final OnItemClickListener listener) {
            tvCompany.setText(flight.getCompany());
            tvRoute.setText(flight.getDeparture() + " - " + flight.getDestination());
            tvDate.setText(flight.getDate());
            int available = flight.getAvailable() != null ? flight.getAvailable().size() : 0;
            tvAvailableSeats.setText("Chỗ trống: " + available);
            tvPrice.setText(String.format("%,d VND", flight.getPrice()));
            itemView.setOnClickListener(v -> listener.onItemClick(flight));
        }
    }
}