package com.example.attendanceapp;

import android.content.Context;
import android.view.ContextMenu;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.TextView;
import android.widget.Toast;

import androidx.annotation.NonNull;
import androidx.recyclerview.widget.RecyclerView;

import java.util.ArrayList;

public class RVAdapter extends RecyclerView.Adapter<RVAdapter.MyHolder> {
    private Context context;
    private ArrayList<ClassItem> classItems;
    // on item click
    OnItemClickListener onItemClickListener;
    public interface OnItemClickListener{
        void onClick(int p);
    }
    public void setOnItemClickListener(OnItemClickListener onItemClickListener) {
        this.onItemClickListener = onItemClickListener;
    }

    public RVAdapter(Context context, ArrayList<ClassItem> classItems) {
        this.context = context;
        this.classItems = classItems;
    }

    @NonNull
    @Override
    public MyHolder onCreateViewHolder(@NonNull ViewGroup parent, int viewType) {
        View view = LayoutInflater.from(context).inflate(R.layout.class_item,parent,false);
        return new MyHolder(view, onItemClickListener);
    }

    @Override
    public void onBindViewHolder(@NonNull MyHolder holder, int position) {
        holder.subject_name.setText(classItems.get(position).getSubject_name());
        holder.class_name.setText(classItems.get(position).getClass_name());
        if(classItems.get(position).getAttendance_today()){
            holder.take_attendance.setVisibility(View.GONE);
            holder.total_present.setVisibility(View.VISIBLE);
            holder.total_absent.setVisibility(View.VISIBLE);
            long t = classItems.get(position).getTotal_student();
            long p = classItems.get(position).getTotal_present();
            holder.total_present.setText(String.valueOf(p));
            holder.total_absent.setText(String.valueOf(t-p));
        }
        else {
            holder.total_present.setVisibility(View.GONE);
            holder.total_absent.setVisibility(View.GONE);
            holder.take_attendance.setVisibility(View.VISIBLE);
        }

    }

    @Override
    public int getItemCount() {
        return classItems.size();
    }

    public class MyHolder extends RecyclerView.ViewHolder implements View.OnCreateContextMenuListener {
        TextView subject_name, class_name, take_attendance, total_present, total_absent;
        public MyHolder(@NonNull View itemView, OnItemClickListener onItemClickListener) {
            super(itemView);

            subject_name = itemView.findViewById(R.id.subject_name_tv);
            class_name = itemView.findViewById(R.id.class_name_tv);
            take_attendance = itemView.findViewById(R.id.take_attendance_tv);
            total_present = itemView.findViewById(R.id.total_present_tv);
            total_absent = itemView.findViewById(R.id.total_absent_tv);

            itemView.setOnClickListener(view -> onItemClickListener.onClick(getAdapterPosition()));
            itemView.setOnCreateContextMenuListener(this);
        }

        @Override
        public void onCreateContextMenu(ContextMenu menu, View view, ContextMenu.ContextMenuInfo menuInfo) {
            menu.add(getAdapterPosition(),0,0,"EDIT");
            menu.add(getAdapterPosition(),1,0,"DELETE");
        }
    }
}
