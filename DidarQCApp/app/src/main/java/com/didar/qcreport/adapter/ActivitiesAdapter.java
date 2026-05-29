package com.didar.qcreport.adapter;

import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.TextView;

import androidx.annotation.NonNull;
import androidx.recyclerview.widget.RecyclerView;

import com.didar.qcreport.R;
import com.didar.qcreport.model.ActivityItem;

import java.util.List;

public class ActivitiesAdapter extends RecyclerView.Adapter<ActivitiesAdapter.ViewHolder> {

    private final List<ActivityItem> items;

    public ActivitiesAdapter(List<ActivityItem> items) {
        this.items = items;
    }

    @NonNull
    @Override
    public ViewHolder onCreateViewHolder(@NonNull ViewGroup parent, int viewType) {
        View view = LayoutInflater.from(parent.getContext())
                .inflate(R.layout.item_activity, parent, false);
        return new ViewHolder(view);
    }

    @Override
    public void onBindViewHolder(@NonNull ViewHolder holder, int position) {
        ActivityItem item = items.get(position);

        holder.tvIndex.setText(String.valueOf(position + 1));
        holder.tvTitle.setText(item.title.isEmpty() ? item.typeName : item.title);
        holder.tvContact.setText(item.contactName.isEmpty() ? "—" : item.contactName);
        holder.tvDeal.setText(item.dealTitle.isEmpty() ? "—" : item.dealTitle);
        holder.tvOwner.setText(item.ownerName.isEmpty() ? "—" : item.ownerName);
        holder.tvDate.setText(item.date.isEmpty() ? "—" : item.date);
        holder.tvDescription.setVisibility(item.description.isEmpty() ? View.GONE : View.VISIBLE);
        holder.tvDescription.setText(item.description);
    }

    @Override
    public int getItemCount() {
        return items.size();
    }

    static class ViewHolder extends RecyclerView.ViewHolder {
        TextView tvIndex, tvTitle, tvContact, tvDeal, tvOwner, tvDate, tvDescription;

        ViewHolder(@NonNull View itemView) {
            super(itemView);
            tvIndex       = itemView.findViewById(R.id.tv_index);
            tvTitle       = itemView.findViewById(R.id.tv_title);
            tvContact     = itemView.findViewById(R.id.tv_contact);
            tvDeal        = itemView.findViewById(R.id.tv_deal);
            tvOwner       = itemView.findViewById(R.id.tv_owner);
            tvDate        = itemView.findViewById(R.id.tv_date);
            tvDescription = itemView.findViewById(R.id.tv_description);
        }
    }
}
