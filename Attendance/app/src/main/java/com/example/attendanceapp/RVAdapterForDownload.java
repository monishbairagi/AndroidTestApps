package com.example.attendanceapp;

import android.content.Context;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.TextView;

import androidx.annotation.NonNull;
import androidx.recyclerview.widget.RecyclerView;

import java.util.ArrayList;

public class RVAdapterForDownload extends RecyclerView.Adapter<RVAdapterForDownload.MyHolder>{
    Context context;
    ArrayList<ClassItem> classItems;

    RVAdapter.OnItemClickListener onItemClickListener;
    public interface OnItemClickListener{
        void onClick(int p);
    }
    public void setOnItemClickListener(RVAdapter.OnItemClickListener onItemClickListener) {
        this.onItemClickListener = onItemClickListener;
    }

    public RVAdapterForDownload(Context context, ArrayList<ClassItem> classItems) {
        this.context = context;
        this.classItems = classItems;
    }

    @NonNull
    @Override
    public RVAdapterForDownload.MyHolder onCreateViewHolder(@NonNull ViewGroup parent, int viewType) {
        View view = LayoutInflater.from(context).inflate(R.layout.class_item,parent,false);
        return new RVAdapterForDownload.MyHolder(view,onItemClickListener);
    }

    @Override
    public void onBindViewHolder(@NonNull RVAdapterForDownload.MyHolder holder, int position) {
        holder.subject_name.setText(classItems.get(position).getSubject_name());
        holder.class_name.setText(classItems.get(position).getClass_name());
        holder.total_present.setVisibility(View.GONE);
        holder.total_absent.setVisibility(View.GONE);
        holder.take_attendance.setVisibility(View.VISIBLE);
        holder.take_attendance.setText("See Record");
    }

    @Override
    public int getItemCount() {
        return classItems.size();
    }

    public class MyHolder extends RecyclerView.ViewHolder {
        TextView subject_name, class_name, take_attendance, total_present, total_absent;
        public MyHolder(@NonNull View itemView, RVAdapter.OnItemClickListener onItemClickListener) {
            super(itemView);

            subject_name = itemView.findViewById(R.id.subject_name_tv);
            class_name = itemView.findViewById(R.id.class_name_tv);
            take_attendance = itemView.findViewById(R.id.take_attendance_tv);
            total_present = itemView.findViewById(R.id.total_present_tv);
            total_absent = itemView.findViewById(R.id.total_absent_tv);

            itemView.setOnClickListener(view -> onItemClickListener.onClick(getAdapterPosition()));
        }
    }
}
