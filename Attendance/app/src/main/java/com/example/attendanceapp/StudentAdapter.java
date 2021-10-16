package com.example.attendanceapp;

import android.content.Context;
import android.graphics.Color;
import android.view.ContextMenu;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.TextView;

import androidx.annotation.NonNull;
import androidx.cardview.widget.CardView;
import androidx.core.content.ContextCompat;
import androidx.recyclerview.widget.RecyclerView;

import java.util.ArrayList;

public class StudentAdapter extends RecyclerView.Adapter<StudentAdapter.MyHolder> {

    private Context context;
    private ArrayList<StudentItem> studentItems;

    OnItemClickListener onItemClickListener;
    public interface OnItemClickListener{
        void onClick(int p);
    }
    public void setOnItemClickListener(OnItemClickListener onItemClickListener) {
        this.onItemClickListener = onItemClickListener;
    }

    public StudentAdapter(Context context, ArrayList<StudentItem> studentItems) {
        this.context = context;
        this.studentItems = studentItems;
    }

    @NonNull
    @Override
    public MyHolder onCreateViewHolder(@NonNull ViewGroup parent, int viewType) {
        View view = LayoutInflater.from(context).inflate(R.layout.student_item,parent,false);
        return new MyHolder(view, onItemClickListener);
    }

    @Override
    public void onBindViewHolder(@NonNull MyHolder holder, int position) {
        holder.roll.setText(Integer.toString(studentItems.get(position).getRoll()));
        holder.name.setText(studentItems.get(position).getName());
        holder.status.setText(studentItems.get(position).getStatus());
        holder.cardView.setCardBackgroundColor(getColor(position,holder));
    }

    private int getColor(int position, MyHolder holder) {
        String status = studentItems.get(position).getStatus();
        if(status=="A") {
            holder.status.setVisibility(View.VISIBLE);
            return Color.parseColor("#" + Integer.toHexString(ContextCompat.getColor(context, R.color.absent)));
        }
        else if(status=="P"){
            holder.status.setVisibility(View.VISIBLE);
            return Color.parseColor("#"+Integer.toHexString(ContextCompat.getColor(context,R.color.present)));
        }
        else {
            holder.status.setVisibility(View.INVISIBLE);
            return Color.parseColor("#"+Integer.toHexString(ContextCompat.getColor(context,R.color.white)));
        }
    }

    @Override
    public int getItemCount() {
        return studentItems.size();
    }

    public class MyHolder extends RecyclerView.ViewHolder implements View.OnCreateContextMenuListener{
        TextView roll, name, status;
        CardView cardView;
        public MyHolder(@NonNull View itemView, OnItemClickListener onItemClickListener) {
            super(itemView);

            roll = itemView.findViewById(R.id.student_roll_tv);
            name = itemView.findViewById(R.id.student_name_tv);
            status = itemView.findViewById(R.id.student_status_tv);
            cardView = itemView.findViewById(R.id.student_item_cv);

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
