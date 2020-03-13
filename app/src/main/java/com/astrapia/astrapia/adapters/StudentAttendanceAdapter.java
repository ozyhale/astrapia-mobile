package com.astrapia.astrapia.adapters;

import android.content.Context;
import android.graphics.Color;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.CheckBox;
import android.widget.LinearLayout;
import android.widget.TextView;

import androidx.annotation.NonNull;
import androidx.recyclerview.widget.RecyclerView;

import com.astrapia.astrapia.R;
import com.astrapia.astrapia.models.Student;
import com.astrapia.astrapia.models.StudentAttendance;

import java.util.List;

public class StudentAttendanceAdapter extends RecyclerView.Adapter<StudentAttendanceAdapter.ViewHolder> {

    private List<StudentAttendance> studentAttendances;

    public StudentAttendanceAdapter(List<StudentAttendance> studentAttendances) {
        this.studentAttendances = studentAttendances;
    }

    public List<StudentAttendance> getStudentAttendances() {
        return studentAttendances;
    }

    public void setStudentAttendances(List<StudentAttendance> studentAttendances) {
        this.studentAttendances = studentAttendances;
    }

    @NonNull
    @Override
    public ViewHolder onCreateViewHolder(@NonNull ViewGroup parent, int viewType) {
        Context context = parent.getContext();
        LayoutInflater inflater = LayoutInflater.from(context);

        // Inflate the custom layout
        View contactView = inflater.inflate(R.layout.item_student_attendance, parent, false);

        // Return a new holder instance
        return new ViewHolder(contactView);
    }

    @Override
    public void onBindViewHolder(@NonNull final ViewHolder holder, final int position) {
        StudentAttendance studentAttendance = this.studentAttendances.get(position);

        final LinearLayout llStudentAttendance = holder.llStudentAttendance;
        TextView tvSAStudentFullName = holder.tvSAStudentFullName;
        CheckBox cbSAStatus = holder.cbSAStatus;

        tvSAStudentFullName.setText(studentAttendance.getFullName(Student.MODE_LAST_NAME_FIRST));
        cbSAStatus.setChecked(studentAttendance.getStatus().equals("1"));

        if(studentAttendance.getStatus().equals("1")){
            llStudentAttendance.setBackgroundColor(Color.parseColor("#5669F0AE"));
        }

        cbSAStatus.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                CheckBox cb = (CheckBox) v;
                if(cb.isChecked()){
                    llStudentAttendance.setBackgroundColor(Color.parseColor("#5669F0AE"));
                    studentAttendances.get(position).setStatus("1");
                }else{
                    llStudentAttendance.setBackgroundColor(Color.TRANSPARENT);
                    studentAttendances.get(position).setStatus("0");
                }
            }
        });
    }

    @Override
    public int getItemCount() {
        return studentAttendances.size();
    }

    static class ViewHolder extends RecyclerView.ViewHolder {

        LinearLayout llStudentAttendance;
        TextView tvSAStudentFullName;
        CheckBox cbSAStatus;

        ViewHolder(View itemView) {
            // Stores the itemView in a public final member variable that can be used
            // to access the context from any ViewHolder instance.
            super(itemView);

            llStudentAttendance = itemView.findViewById(R.id.ll_student_attendance);
            tvSAStudentFullName = itemView.findViewById(R.id.tv_sa_student_full_name);
            cbSAStatus = itemView.findViewById(R.id.cb_sa_status);
        }
    }
}
