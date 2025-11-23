package com.example.dispenser.adapter;

import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.TextView;

import androidx.annotation.NonNull;
import androidx.recyclerview.widget.RecyclerView;

import com.example.dispenser.R;
import com.example.dispenser.data.model.HistoryModel;

import java.util.List;

public class HistoryAdapter extends RecyclerView.Adapter<HistoryAdapter.ViewHolder> {

    private List<HistoryModel> list;

    public HistoryAdapter(List<HistoryModel> list) {
        this.list = list;
    }

    public static class ViewHolder extends RecyclerView.ViewHolder {
        TextView txtTime, txtDispenser, txtVariant,txtVolume,txtCountBottle,txtTimeUsed,txtDispoenserVolume;

        public ViewHolder(@NonNull View itemView) {
            super(itemView);
            txtTime = itemView.findViewById(R.id.txtTime);
            txtDispenser = itemView.findViewById(R.id.txtDispenser);
            txtVariant = itemView.findViewById(R.id.txtVariant);

            txtVolume= itemView.findViewById(R.id.txtVolume);
            txtCountBottle = itemView.findViewById(R.id.txtCountBottle);
            txtTimeUsed=itemView.findViewById(R.id.timeUsed);
            txtDispoenserVolume=itemView.findViewById(R.id.dispenserVolume);
        }
    }

    @NonNull
    @Override
    public ViewHolder onCreateViewHolder(@NonNull ViewGroup parent, int viewType) {
        View v = LayoutInflater.from(parent.getContext())
                .inflate(R.layout.item_history_card, parent, false);
        return new ViewHolder(v);
    }

    @Override
    public void onBindViewHolder(@NonNull ViewHolder holder, int position) {
        HistoryModel h = list.get(position);

        holder.txtTime.setText(h.getTimestamp());
        holder.txtDispenser.setText("Dispenser: " + h.getDispenserId());
        holder.txtVariant.setText("Variant: " +  h.getVariant());
        holder.txtVolume.setText("Volume: " + h.getVolume());
        holder.txtCountBottle.setText("Count: " + h.getBottleCount());
        holder.txtTimeUsed.setText("Time used: " + h.getTimeUsed());
        holder.txtDispoenserVolume.setText("Dispenser volume: " + h.getDispenserVolume());
        holder.txtTime.setText(h.getTimestamp());

//        holder.txtDetail.setText(h.volume + " ml  •  " + h.mode + "  •  User: " + h.user);
    }

    @Override
    public int getItemCount() {
        return list.size();
    }
}