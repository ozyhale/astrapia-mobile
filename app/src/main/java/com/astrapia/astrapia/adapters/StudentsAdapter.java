package com.astrapia.astrapia.adapters;

import android.annotation.SuppressLint;
import android.content.Context;
import android.content.Intent;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.ImageButton;
import android.widget.TextView;

import androidx.annotation.NonNull;
import androidx.recyclerview.widget.RecyclerView;

import com.astrapia.astrapia.R;
import com.astrapia.astrapia.StudentSubjectsActivity;
import com.astrapia.astrapia.models.Student;

import java.util.List;

public class StudentsAdapter extends RecyclerView.Adapter<StudentsAdapter.ViewHolder> {

    private List<Student> students;
    private Context context;

    // Pass in the contact array into the constructor
    public StudentsAdapter(List<Student> students, Context context) {
        this.students = students;
        this.context = context;
    }

    @NonNull
    @Override
    public ViewHolder onCreateViewHolder(@NonNull ViewGroup parent, int viewType) {
        LayoutInflater inflater = LayoutInflater.from(parent.getContext());
        View view = inflater.inflate(R.layout.item_student, parent, false);
        return new ViewHolder(view);
    }

    @SuppressLint("SetTextI18n")
    @Override
    public void onBindViewHolder(@NonNull ViewHolder holder, int position) {
        final Student student = students.get(position);

        holder.tvFirstName.setText("First Name: " + student.getFirstName());
        holder.tvMiddleName.setText("Middle Name: " + student.getMiddleName());
        holder.tvLastName.setText("Last Name: " + student.getLastName());
        holder.tvSchoolId.setText("School ID: " + student.getSchoolId());
        holder.btnShowStudentSubjects.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                Intent intent = new Intent(context, StudentSubjectsActivity.class);
                intent.putExtra("studentId", student.getId());
                intent.putExtra("firstName", student.getFirstName());
                intent.putExtra("middleName", student.getMiddleName());
                intent.putExtra("lastName", student.getLastName());
                intent.putExtra("schoolId", student.getSchoolId());
                intent.putExtra("parentId", student.getParentId());
                intent.setFlags(Intent.FLAG_ACTIVITY_NEW_TASK);
                context.startActivity(intent);
            }
        });
    }

    @Override
    public int getItemCount() {
        return this.students.size();
    }

    static class ViewHolder extends RecyclerView.ViewHolder {

        TextView tvMiddleName;
        TextView tvFirstName;
        TextView tvLastName;
        TextView tvSchoolId;
        ImageButton btnShowStudentSubjects;

        ViewHolder(View itemView) {
            super(itemView);

            tvFirstName = itemView.findViewById(R.id.tv_first_name);
            tvMiddleName = itemView.findViewById(R.id.tv_middle_name);
            tvLastName = itemView.findViewById(R.id.tv_last_name);
            tvSchoolId = itemView.findViewById(R.id.tv_school_id);
            btnShowStudentSubjects = itemView.findViewById(R.id.btnShowStudentSubjects);
        }
    }
}
