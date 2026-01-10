package com.example.dispenser.ui.schedule.list_schedule;

import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.CheckBox;
import android.widget.TextView;
import androidx.annotation.NonNull;
import androidx.appcompat.widget.SwitchCompat;
import androidx.recyclerview.widget.RecyclerView;
import com.example.dispenser.R;
import com.example.dispenser.data.model.ScheduleRTDB;

import java.util.ArrayList;
import java.util.HashSet;
import java.util.List;
import java.util.Locale;
import java.util.Set;

public class ScheduleAdapter extends RecyclerView.Adapter<ScheduleAdapter.ViewHolder> {

    private List<ScheduleRTDB> list;
    private OnScheduleClickListener listener;

    // --- FITUR BARU: SELECTION MODE ---
    private boolean isSelectionMode = false;
    private final Set<Integer> selectedPositions = new HashSet<>();
    private OnSelectionModeListener selectionListener;

    public interface OnScheduleClickListener {
        void onEditClick(int position, ScheduleRTDB schedule);
        void onToggleActive(int position, boolean isActive);
    }

    // Interface untuk memberitahu Fragment saat mode seleksi berubah
    public interface OnSelectionModeListener {
        void onSelectionChanged(int count);
        void onSelectionModeToggle(boolean active);
    }

    public void setOnSelectionModeListener(OnSelectionModeListener selectionListener) {
        this.selectionListener = selectionListener;
    }
    // ----------------------------------

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

        holder.tvTime.setText(String.format(Locale.getDefault(), "%02d:%02d", schedule.hour, schedule.minute));
        holder.tvInfo.setText(String.format("%s | %d Botol", schedule.categoryName, schedule.count));
        holder.tvDays.setText(parseMaskToDays(schedule.dowMask));
        holder.switchActive.setChecked(schedule.enabled == 1);

        // --- LOGIKA SELECTION MODE DI UI ---

        // 1. Atur visibilitas CheckBox dan Switch
        if (isSelectionMode) {
            holder.cbSelect.setVisibility(View.VISIBLE);
            holder.switchActive.setVisibility(View.GONE);
            holder.cbSelect.setChecked(selectedPositions.contains(position));
        } else {
            holder.cbSelect.setVisibility(View.GONE);
            holder.switchActive.setVisibility(View.VISIBLE);
        }

        // 2. Klik Biasa (Toggle Centang jika Mode Seleksi, Edit jika Mode Biasa)
        holder.itemView.setOnClickListener(v -> {
            if (isSelectionMode) {
                toggleSelection(position);
            } else {
                listener.onEditClick(position, schedule);
            }
        });

        // 3. Tekan Lama (Memicu Mode Seleksi)
        holder.itemView.setOnLongClickListener(v -> {
            if (!isSelectionMode) {
                isSelectionMode = true;
                if (selectionListener != null) selectionListener.onSelectionModeToggle(true);
                toggleSelection(position);
            }
            return true;
        });

        holder.switchActive.setOnClickListener(v -> {
            listener.onToggleActive(position, holder.switchActive.isChecked());
        });
    }

    private void toggleSelection(int position) {
        if (selectedPositions.contains(position)) {
            selectedPositions.remove(position);
        } else {
            selectedPositions.add(position);
        }

        if (selectionListener != null) {
            selectionListener.onSelectionChanged(selectedPositions.size());
            // Jika user menghapus semua centang secara manual, matikan mode seleksi
            if (selectedPositions.isEmpty()) {
                exitSelectionMode();
            }
        }
        notifyDataSetChanged();
    }

    public void exitSelectionMode() {
        isSelectionMode = false;
        selectedPositions.clear();
        if (selectionListener != null) selectionListener.onSelectionModeToggle(false);
        notifyDataSetChanged();
    }

    public List<ScheduleRTDB> getSelectedItems() {
        List<ScheduleRTDB> selected = new ArrayList<>();
        for (Integer pos : selectedPositions) {
            selected.add(list.get(pos));
        }
        return selected;
    }

    @Override
    public int getItemCount() {
        return list.size();
    }

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
        CheckBox cbSelect; // Tambahkan ini

        public ViewHolder(@NonNull View itemView) {
            super(itemView);
            tvTime = itemView.findViewById(R.id.tv_item_time);
            tvDays = itemView.findViewById(R.id.tv_item_days);
            tvInfo = itemView.findViewById(R.id.tv_item_info);
            switchActive = itemView.findViewById(R.id.switch_active);
            cbSelect = itemView.findViewById(R.id.cb_select); // Inisialisasi
        }
    }
}