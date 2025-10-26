package com.example.dispenser.adapter;

import android.content.Context;
import android.media.Image;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.ImageView;
import android.widget.TextView;


import androidx.annotation.NonNull;
import androidx.recyclerview.widget.RecyclerView;

import com.example.dispenser.R;
import com.example.dispenser.data.model.Dispenser;

import java.util.List;

public class DispenserAdapter extends RecyclerView.Adapter<DispenserAdapter.MyViewHolder> {
    Context activityContext;
    List<Dispenser>listData;
    private OnDispenserClickListener listener;
    public interface OnDispenserClickListener {
        void onDispenserClick(Dispenser dispenser);
    }
    public void setOnDispenserClickListener(OnDispenserClickListener listener) {
        this.listener = listener;
    }
    public DispenserAdapter(Context activityContext, List<Dispenser> listData) {
        this.activityContext = activityContext;
        this.listData = listData;
    }

    @NonNull
    @Override
    public DispenserAdapter.MyViewHolder onCreateViewHolder(@NonNull ViewGroup parent, int viewType) {
        activityContext= parent.getContext();
        LayoutInflater inflater=LayoutInflater.from(activityContext);
        View view=inflater.inflate(R.layout.list_dispenser,parent,false);
        MyViewHolder holder=new MyViewHolder(view);

        return holder;
    }

    @Override
    public void onBindViewHolder(@NonNull DispenserAdapter.MyViewHolder holder, int position) {
        String currentName=listData.get(position).getDeviceName();
        String currentStatus=listData.get(position).getStatus();

        holder.name.setText(currentName);
        holder.status.setText(currentStatus);
        holder.itemView.setOnClickListener(onClickListener -> {
            if (listener != null) {
                listener.onDispenserClick(listData.get(position));
            }
        });
    }

    @Override
    public int getItemCount() {
        return listData.size();
    }


    public class MyViewHolder extends RecyclerView.ViewHolder {
        TextView name,status;

        public MyViewHolder(@NonNull View itemView) {
            super(itemView);
            name=itemView.findViewById(R.id.itemName);
            status=itemView.findViewById(R.id.itemStatus);



        }
    }
}