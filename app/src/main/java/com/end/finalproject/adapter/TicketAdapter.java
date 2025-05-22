package com.end.finalproject.adapter;

import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.TextView;

import androidx.annotation.NonNull;
import androidx.recyclerview.widget.RecyclerView;

import com.end.finalproject.R;
import com.end.finalproject.model.TicketItem;

import java.util.List;
import java.util.Locale;

public class TicketAdapter extends RecyclerView.Adapter<TicketAdapter.TicketViewHolder> {
    private List<TicketItem> tickets;

    public TicketAdapter(List<TicketItem> tickets) {
        this.tickets = tickets;
    }

    @NonNull
    @Override
    public TicketViewHolder onCreateViewHolder(@NonNull ViewGroup parent, int viewType) {
        View view = LayoutInflater.from(parent.getContext())
                .inflate(R.layout.item_ticket, parent, false);
        return new TicketViewHolder(view);
    }

    @Override
    public void onBindViewHolder(@NonNull TicketViewHolder holder, int position) {
        TicketItem item = tickets.get(position);
        holder.tvCategory.setText(item.getCategory().equals("movie") ? "Vé Phim" : "Vé Máy Bay");
        holder.tvTitle.setText(item.getName());
        holder.tvDate.setText("Ngày: " + item.getDate());
        holder.tvSeats.setText("Ghế: " + String.join(", ", item.getChosenSeats()));
        holder.tvPrice.setText(String.format(Locale.US, "%,d VND", item.getPrice()));
    }

    @Override
    public int getItemCount() {
        return tickets.size();
    }

    static class TicketViewHolder extends RecyclerView.ViewHolder {
        TextView tvCategory, tvTitle, tvDate, tvSeats, tvPrice;

        public TicketViewHolder(@NonNull View itemView) {
            super(itemView);
            tvCategory = itemView.findViewById(R.id.tvCategory);
            tvTitle    = itemView.findViewById(R.id.tvTitle);
            tvDate     = itemView.findViewById(R.id.tvDate);
            tvSeats    = itemView.findViewById(R.id.tvSeats);
            tvPrice    = itemView.findViewById(R.id.tvPrice);
        }
    }
}
