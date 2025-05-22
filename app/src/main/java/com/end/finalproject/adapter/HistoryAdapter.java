// HistoryAdapter.java
package com.end.finalproject.adapter;

import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.TextView;

import androidx.annotation.NonNull;
import androidx.recyclerview.widget.RecyclerView;

import com.end.finalproject.R;
import com.end.finalproject.model.History;

import java.util.List;

public class HistoryAdapter extends RecyclerView.Adapter<HistoryAdapter.HistoryViewHolder> {
    private List<History> historyList;

    public HistoryAdapter(List<History> historyList) {
        this.historyList = historyList;
    }

    @NonNull
    @Override
    public HistoryViewHolder onCreateViewHolder(@NonNull ViewGroup parent, int viewType) {
        View view = LayoutInflater.from(parent.getContext())
                .inflate(R.layout.item_lich_su_giao_dich, parent, false);
        return new HistoryViewHolder(view);
    }

    @Override
    public void onBindViewHolder(@NonNull HistoryViewHolder holder, int position) {
        History history = historyList.get(position);
        // Split the balance string to extract timestamp/date part
        String status = history.getBalanceStatus();
        holder.tvThoiGian.setText(history.getDate());
        holder.tvThongTinGiaoDich.setText(status);
        holder.tvNguonDich.setText(history.getInfo());
    }

    @Override
    public int getItemCount() {
        return historyList.size();
    }

    static class HistoryViewHolder extends RecyclerView.ViewHolder {
        TextView tvThoiGian, tvThongTinGiaoDich, tvNguonDich;

        public HistoryViewHolder(@NonNull View itemView) {
            super(itemView);
            tvThoiGian = itemView.findViewById(R.id.tvThoiGian);
            tvThongTinGiaoDich = itemView.findViewById(R.id.tvThongTinGiaoDich);
            tvNguonDich = itemView.findViewById(R.id.tvNguonDich);
        }
    }
}
