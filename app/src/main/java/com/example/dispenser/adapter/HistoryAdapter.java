package com.example.dispenser.adapter;

import android.graphics.Color;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.TextView;

import androidx.annotation.NonNull;
import androidx.recyclerview.widget.RecyclerView;

import com.example.dispenser.R;
import com.example.dispenser.data.DispenserUtility;
import com.example.dispenser.data.model.HistoryModel;

import java.util.List;

public class HistoryAdapter extends RecyclerView.Adapter<HistoryAdapter.ViewHolder> {

    private List<HistoryModel> list;

    public HistoryAdapter(List<HistoryModel> list) {
        this.list = list;
    }

    public static class ViewHolder extends RecyclerView.ViewHolder {
        // Sesuaikan dengan ID baru di XML
        TextView txtDispenser, txtPowerStatus, txtTimeUsed;
        TextView txtNameLiquidA, txtVolumeTankA;
        TextView txtNameLiquidB, txtVolumeTankB;
        TextView txtCountBottle, txtTime;
        TextView txtVariantName;

        public ViewHolder(@NonNull View itemView) {
            super(itemView);
            txtDispenser = itemView.findViewById(R.id.txtDispenser);
            txtPowerStatus = itemView.findViewById(R.id.txtPowerStatus);
            txtTimeUsed = itemView.findViewById(R.id.txtTimeUsed);

            txtNameLiquidA = itemView.findViewById(R.id.txtNameLiquidA);
            txtVolumeTankA = itemView.findViewById(R.id.txtVolumeTankA);

            txtNameLiquidB = itemView.findViewById(R.id.txtNameLiquidB);
            txtVolumeTankB = itemView.findViewById(R.id.txtVolumeTankB);

            txtCountBottle = itemView.findViewById(R.id.txtCountBottle);
            txtTime = itemView.findViewById(R.id.txtTime);
            txtVariantName=itemView.findViewById(R.id.txtVariantName);
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

        // 1. Header & Status
        holder.txtDispenser.setText(h.getDeviceName());
        holder.txtTimeUsed.setText("Durasi: " + DispenserUtility.getTimeUsed(h.getTimeUsed()));

        // Logic sederhana untuk Power Status
        if (h.isPower()) { // Asumsi ada method isPowerOn() di model kamu
            holder.txtPowerStatus.setText("POWER ON");
            holder.txtPowerStatus.setTextColor(Color.parseColor("#008B74"));
        } else {
            holder.txtPowerStatus.setText("POWER OFF");
            holder.txtPowerStatus.setTextColor(Color.RED);
        }

        // 2. Data Liquid A (Gunakan variant/nama dari model)
        holder.txtNameLiquidA.setText(h.getLiquidNameA()); // Sesuaikan method di model
        holder.txtVolumeTankA.setText("Tank: " + h.getWaterLevelTankA() + "ml | Fill: " + h.getVolumeFilledA() + "ml");

        // 3. Data Liquid B
        holder.txtNameLiquidB.setText(h.getLiquidNameB()); // Sesuaikan method di model
        holder.txtVolumeTankB.setText("Tank: " + h.getWaterLevelTankB() + "ml | Fill: " + h.getVolumeFilledB() + "ml");

        // 4. Footer
        holder.txtCountBottle.setText("Batch: " + h.getCurrentBottle() + " | Total: " + h.getBottleCount());
        holder.txtTime.setText(DispenserUtility.formatHistoryDate(h.getTimeNow()));
        holder.txtVariantName.setText(h.getVariant());
    }

    @Override
    public int getItemCount() {
        return list.size();
    }
}