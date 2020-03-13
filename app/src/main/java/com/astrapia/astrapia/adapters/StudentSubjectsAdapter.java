package com.astrapia.astrapia.adapters;

import android.annotation.SuppressLint;
import android.content.Context;
import android.graphics.Color;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.TextView;

import androidx.annotation.NonNull;
import androidx.recyclerview.widget.RecyclerView;

import com.astrapia.astrapia.R;
import com.astrapia.astrapia.models.StudentAttendance;

import java.util.List;

public class StudentSubjectsAdapter extends RecyclerView.Adapter<StudentSubjectsAdapter.ViewHolder> {

    private List<StudentAttendance> studentAttendances;

    public StudentSubjectsAdapter(List<StudentAttendance> studentAttendances) {
        this.studentAttendances = studentAttendances;
    }

    @NonNull
    @Override
    public ViewHolder onCreateViewHolder(@NonNull ViewGroup parent, int viewType) {
        Context context = parent.getContext();
        LayoutInflater inflater = LayoutInflater.from(context);
        View contactView = inflater.inflate(R.layout.item_student_subjects, parent, false);
        return new ViewHolder(contactView);
    }

    @SuppressLint("SetTextI18n")
    @Override
    public void onBindViewHolder(@NonNull ViewHolder holder, int position) {
        StudentAttendance studentAttendance = studentAttendances.get(position);

        int red = Color.parseColor("#FF5252");
        int green = Color.parseColor("#FF69F0AE");

        holder.tvSubjectName.setText(studentAttendance.getSubjectName() + "(" + studentAttendance.getSubjectCode() + ")");
        holder.vStatus.setBackgroundColor(studentAttendance.getStatus().equals("1") ? green : red);
    }

    @Override
    public int getItemCount() {
        return studentAttendances.size();
    }

    static class ViewHolder extends RecyclerView.ViewHolder {

        TextView tvSubjectName;
        View vStatus;

        ViewHolder(View itemView) {
            super(itemView);

            tvSubjectName = itemView.findViewById(R.id.tvSubjectName);
            vStatus = itemView.findViewById(R.id.vStatus);
        }
    }
}
