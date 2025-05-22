package com.end.finalproject.adapter;

import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.TextView;
import androidx.annotation.NonNull;
import androidx.recyclerview.widget.RecyclerView;
import com.end.finalproject.R;
import com.end.finalproject.model.Customer;
import java.util.List;

public class CustomerAdapter extends RecyclerView.Adapter<CustomerAdapter.CustomerViewHolder> {
    private final List<Customer> customers;
    private final OnItemClickListener listener;

    public interface OnItemClickListener {
        void onItemClick(Customer customer);
    }

    public CustomerAdapter(List<Customer> customers, OnItemClickListener listener) {
        this.customers = customers;
        this.listener = listener;
    }

    @NonNull
    @Override
    public CustomerViewHolder onCreateViewHolder(@NonNull ViewGroup parent, int viewType) {
        View view = LayoutInflater.from(parent.getContext())
                .inflate(R.layout.item_khach_hang, parent, false);
        return new CustomerViewHolder(view);
    }

    @Override
    public void onBindViewHolder(@NonNull CustomerViewHolder holder, int position) {
        Customer customer = customers.get(position);
        holder.tvCustomerId.setText(customer.getId());
        holder.tvCustomerName.setText(customer.getName());
        holder.itemView.setOnClickListener(v -> listener.onItemClick(customer));
    }

    @Override
    public int getItemCount() {
        return customers.size();
    }

    public static class CustomerViewHolder extends RecyclerView.ViewHolder {
        TextView tvCustomerId, tvCustomerName;

        public CustomerViewHolder(@NonNull View itemView) {
            super(itemView);
            tvCustomerId   = itemView.findViewById(R.id.tvCustomerId);
            tvCustomerName = itemView.findViewById(R.id.tvCustomerName);
        }
    }
}
