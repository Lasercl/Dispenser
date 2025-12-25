package com.example.dispenser.ui.schedule.list_schedule;

import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.TextView;
import androidx.annotation.NonNull;
import androidx.appcompat.widget.SwitchCompat;
import androidx.recyclerview.widget.RecyclerView;
import com.example.dispenser.R;
import com.example.dispenser.data.model.ScheduleRTDB;

import java.util.List;
import java.util.Locale;

public class ScheduleAdapter extends RecyclerView.Adapter<ScheduleAdapter.ViewHolder> {

    private List<ScheduleRTDB> list;
    private OnScheduleClickListener listener;

    // Interface untuk handle klik Edit dan Switch
    public interface OnScheduleClickListener {
        void onEditClick(int position, ScheduleRTDB schedule);
        void onToggleActive(int position, boolean isActive);
    }

    public ScheduleAdapter(List<ScheduleRTDB> list, OnScheduleClickListener listener) {
        this.list = list;
        this.listener = listener;
    }

    @NonNull
    @Override
    public ViewHolder onCreateViewHolder(@NonNull ViewGroup parent, int viewType) {
        View view = LayoutInflater.from(parent.getContext()).inflate(R.layout.fragment_schedule_list_item, parent, false);
        return new ViewHolder(view);
    }

    @Override
    public void onBindViewHolder(@NonNull ViewHolder holder, int position) {
        ScheduleRTDB schedule = list.get(position);

        // 1. Set Waktu (Format 00:00)
        holder.tvTime.setText(String.format(Locale.getDefault(), "%02d:%02d", schedule.hour, schedule.minute));

        // 2. Set Info Resep & Botol
        holder.tvInfo.setText(String.format("%s | %d Botol", schedule.categoryName, schedule.count));

        // 3. Konversi dowMask ke Nama Hari
        holder.tvDays.setText(parseMaskToDays(schedule.dowMask));

        // 4. Set Status Switch
        holder.switchActive.setChecked(schedule.enabled == 1);

        // Listener untuk Switch (Toggle Aktif/Mati)
        holder.switchActive.setOnClickListener(v -> {
            listener.onToggleActive(position, holder.switchActive.isChecked());
        });

        // Listener untuk Edit (Klik di seluruh area kartu)
        holder.itemView.setOnClickListener(v -> {
            listener.onEditClick(position, schedule);
        });
    }

    @Override
    public int getItemCount() {
        return list.size();
    }

    // Fungsi sakti untuk mengubah angka Masker (0-127) ke teks Hari
    private String parseMaskToDays(int mask) {
        if (mask == 0) return "Tidak ada hari";
        if (mask == 127) return "Setiap Hari";

        String[] days = {"Ming", "Sen", "Sel", "Rab", "Kam", "Jum", "Sab"};
        StringBuilder sb = new StringBuilder();
        for (int i = 0; i < 7; i++) {
            if ((mask & (1 << i)) != 0) {
                if (sb.length() > 0) sb.append(", ");
                sb.append(days[i]);
            }
        }
        return sb.toString();
    }

    public static class ViewHolder extends RecyclerView.ViewHolder {
        TextView tvTime, tvDays, tvInfo;
        SwitchCompat switchActive;

        public ViewHolder(@NonNull View itemView) {
            super(itemView);
            tvTime = itemView.findViewById(R.id.tv_item_time);
            tvDays = itemView.findViewById(R.id.tv_item_days);
            tvInfo = itemView.findViewById(R.id.tv_item_info);
            switchActive = itemView.findViewById(R.id.switch_active);
        }
    }
}