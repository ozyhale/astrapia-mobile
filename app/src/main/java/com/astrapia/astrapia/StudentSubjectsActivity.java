package com.astrapia.astrapia;

import androidx.annotation.NonNull;
import androidx.appcompat.app.AppCompatActivity;
import androidx.recyclerview.widget.LinearLayoutManager;
import androidx.recyclerview.widget.RecyclerView;

import android.annotation.SuppressLint;
import android.app.DatePickerDialog;
import android.os.Bundle;
import android.view.MenuItem;
import android.view.View;
import android.widget.Button;
import android.widget.DatePicker;
import android.widget.ProgressBar;

import com.astrapia.astrapia.adapters.StudentSubjectsAdapter;
import com.astrapia.astrapia.models.AttendanceSheet;
import com.astrapia.astrapia.models.Schedule;
import com.astrapia.astrapia.models.Student;
import com.astrapia.astrapia.models.StudentAttendance;
import com.astrapia.astrapia.models.StudentSchedule;
import com.astrapia.astrapia.models.Subject;
import com.google.android.gms.tasks.OnCompleteListener;
import com.google.android.gms.tasks.Task;
import com.google.firebase.auth.FirebaseAuth;
import com.google.firebase.firestore.DocumentSnapshot;
import com.google.firebase.firestore.FirebaseFirestore;
import com.google.firebase.firestore.QueryDocumentSnapshot;
import com.google.firebase.firestore.QuerySnapshot;
import com.google.gson.Gson;

import java.text.SimpleDateFormat;
import java.time.Month;
import java.util.ArrayList;
import java.util.Calendar;
import java.util.Date;
import java.util.List;

public class StudentSubjectsActivity extends AppCompatActivity implements View.OnClickListener, DatePickerDialog.OnDateSetListener {

    private Bundle bundle;
    private Button btnShowDatePicker;
    private Calendar calendar;

    private FirebaseAuth mAuth;
    private FirebaseFirestore db;

    private Student student;

    private List<StudentAttendance> studentAttendances;

    RecyclerView rvStudentAttendances;
    ProgressBar pbSSubjects;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_student_subjects);
        setTitle("Student Subjects");
        bundle = getIntent().getExtras();
        getSupportActionBar().setDisplayHomeAsUpEnabled(true);

        mAuth = FirebaseAuth.getInstance();
        db = FirebaseFirestore.getInstance();

        btnShowDatePicker = findViewById(R.id.btnShowDatePicker);

        calendar = Calendar.getInstance();
        Date c = calendar.getTime();

        @SuppressLint("SimpleDateFormat") SimpleDateFormat df = new SimpleDateFormat("MM/dd/yyyy");
        String formattedDate = df.format(c);

        btnShowDatePicker.setOnClickListener(this);
        btnShowDatePicker.setText(formattedDate);

        student = new Student();
        student.setId(bundle.getString("studentId"));
        student.setFirstName(bundle.getString("firstName"));
        student.setMiddleName(bundle.getString("middleName"));
        student.setLastName(bundle.getString("lastName"));
        student.setSchoolId(bundle.getString("schoolId"));
        student.setParentId(bundle.getString("parentId"));

        loadStudentAttendances();
    }

    private void loadStudentAttendances(){

        pbSSubjects = findViewById(R.id.pbSSubjects);
        pbSSubjects.setVisibility(View.VISIBLE);

        rvStudentAttendances = findViewById(R.id.rvStudentAttendances);
        rvStudentAttendances.setVisibility(View.INVISIBLE);

        studentAttendances = new ArrayList<>();

        int year = calendar.get(Calendar.YEAR);
        int month = calendar.get(Calendar.MONTH);
        int dayOfMonth = calendar.get(Calendar.DAY_OF_MONTH);

        db.collection("student_attendances")
                .whereEqualTo("student_id", student.getId())
                .whereEqualTo("attendance_sheet_date", formatDateForDB(year, month, dayOfMonth))
                .get()
                .addOnCompleteListener(new OnCompleteListener<QuerySnapshot>() {
                    @Override
                    public void onComplete(@NonNull Task<QuerySnapshot> task) {
                        if (task.isSuccessful()){
                            for(QueryDocumentSnapshot document : task.getResult()){
                                String id = document.getId();
                                String attendanceSheetId = document.getString("attendance_sheet_id");
                                String studentId = document.getString("studentId");
                                String status = document.getString("status");
                                String firstName = document.getString("first_name");
                                String middleName = document.getString("middle_name");
                                String lastName = document.getString("last_name");
                                String attendanceSheetDate = document.getString("attendance_sheet_date");
                                String subjectId = document.getString("subject_id");
                                String subjectCode = document.getString("subject_code");
                                String subjectName = document.getString("subject_name");

                                StudentAttendance studentAttendance = new StudentAttendance();
                                studentAttendance.setId(id);
                                studentAttendance.setAttendanceSheetId(attendanceSheetId);
                                studentAttendance.setStudentId(studentId);
                                studentAttendance.setStatus(status);
                                studentAttendance.setFirstName(firstName);
                                studentAttendance.setMiddleName(middleName);
                                studentAttendance.setLastName(lastName);
                                studentAttendance.setAttendanceSheetDate(attendanceSheetDate);
                                studentAttendance.setSubjectId(subjectId);
                                studentAttendance.setSubjectCode(subjectCode);
                                studentAttendance.setSubjectName(subjectName);

                                studentAttendances.add(studentAttendance);
                            }

                            populateRVStudentAttendances();
                        }else{
                            System.out.println(task.getException());
                        }
                    }
                });
    }

    private void populateRVStudentAttendances(){
        if(studentAttendances.size() > 0){
            rvStudentAttendances.setVisibility(View.VISIBLE);
            StudentSubjectsAdapter adapter = new StudentSubjectsAdapter(studentAttendances);
            rvStudentAttendances.setAdapter(adapter);
            rvStudentAttendances.setLayoutManager(new LinearLayoutManager(this));
        }

        pbSSubjects.setVisibility(View.GONE);
    }

    private static String formatDateForDB(int year, int month, int dayOfMonth){

        month++;

        String sMonth = "" + month;
        String sDayOfMonth = "" + dayOfMonth;

        if(month < 10){
            sMonth = "0" + month;
        }

        if(dayOfMonth < 10){
            sDayOfMonth = "0" + dayOfMonth;
        }

        return year + "-" + sMonth + "-" + sDayOfMonth;
    }

    @Override
    public boolean onOptionsItemSelected(@NonNull MenuItem item) {
        if(item.getItemId() == android.R.id.home){
            onBackPressed();
        }

        return super.onOptionsItemSelected(item);
    }

    @SuppressLint("SetTextI18n")
    @Override
    public void onDateSet(DatePicker view, int year, int month, int dayOfMonth) {
        btnShowDatePicker.setText((month+1) + "/" + dayOfMonth + "/" + year);
        calendar.set(Calendar.YEAR, year);
        calendar.set(Calendar.MONTH, month);
        calendar.set(Calendar.DAY_OF_MONTH, dayOfMonth);

        loadStudentAttendances();
    }

    @Override
    public void onClick(View v) {
        if(v.getId() == R.id.btnShowDatePicker){
            DatePickerDialog dialog = new DatePickerDialog(this, this, calendar.get(Calendar.YEAR), calendar.get(Calendar.MONTH), calendar.get(Calendar.DAY_OF_MONTH));
            dialog.show();
        }
    }
}
