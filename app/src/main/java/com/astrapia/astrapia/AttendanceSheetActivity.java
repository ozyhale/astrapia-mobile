package com.astrapia.astrapia;

import android.annotation.SuppressLint;
import android.content.DialogInterface;
import android.os.Build;
import android.os.Bundle;
import android.view.Menu;
import android.view.MenuItem;
import android.view.View;
import android.widget.CheckBox;
import android.widget.EditText;
import android.widget.LinearLayout;
import android.widget.Switch;
import android.widget.TextView;
import android.widget.Toast;

import androidx.annotation.NonNull;
import androidx.annotation.RequiresApi;
import androidx.appcompat.app.AlertDialog;
import androidx.appcompat.app.AppCompatActivity;
import androidx.recyclerview.widget.LinearLayoutManager;
import androidx.recyclerview.widget.RecyclerView;

import com.astrapia.astrapia.adapters.StudentAttendanceAdapter;
import com.astrapia.astrapia.models.Schedule;
import com.astrapia.astrapia.models.StudentAttendance;
import com.astrapia.astrapia.models.Subject;
import com.google.android.gms.tasks.OnCompleteListener;
import com.google.android.gms.tasks.OnFailureListener;
import com.google.android.gms.tasks.OnSuccessListener;
import com.google.android.gms.tasks.Task;
import com.google.firebase.firestore.CollectionReference;
import com.google.firebase.firestore.DocumentReference;
import com.google.firebase.firestore.DocumentSnapshot;
import com.google.firebase.firestore.FirebaseFirestore;
import com.google.firebase.firestore.QueryDocumentSnapshot;
import com.google.firebase.firestore.QuerySnapshot;
import com.google.firebase.firestore.WriteBatch;

import java.util.ArrayList;
import java.util.HashMap;
import java.util.List;
import java.util.Objects;

public class AttendanceSheetActivity extends AppCompatActivity {

    private FirebaseFirestore db;

    private Bundle bundle;

    private Schedule schedule;

    private List<StudentAttendance> studentAttendances;

    private static final String STUDENT_ATTENDANCES = "student_attendances";
    private static final String STUDENT_SCHEDULES = "student_schedules";
    private AlertDialog saveAttendanceSheetDialog;
    private EditText etSAAttendanceSheetName;
    private TextView tvASAttendanceSheetName;
    private AlertDialog deleteAttendanceSheetDialog;

    private Subject subject;

    @RequiresApi(api = Build.VERSION_CODES.KITKAT)
    @SuppressLint("SetTextI18n")
    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_attendance_sheet);

        db = FirebaseFirestore.getInstance();

        bundle = getIntent().getExtras();

        TextView tvASDateCreated = findViewById(R.id.tv_as_date_created);
        tvASAttendanceSheetName = findViewById(R.id.tv_as_attendance_sheet_name);

        assert bundle != null;
        setTitle("Attendance Sheet");
        Objects.requireNonNull(getSupportActionBar()).setDisplayHomeAsUpEnabled(true);

        String[] dateCreated = Objects.requireNonNull(bundle.getString("date")).split("-");
        String year = dateCreated[0];
        String month = dateCreated[1];
        String day = dateCreated[2];

        tvASDateCreated.setText("Date Created: " + month + "/" + day + "/" + year);
        tvASAttendanceSheetName.setText("Sheet Name: " + bundle.getString("name"));

        etSAAttendanceSheetName = new EditText(this);
        LinearLayout.LayoutParams lp = new LinearLayout.LayoutParams(
                LinearLayout.LayoutParams.MATCH_PARENT,
                LinearLayout.LayoutParams.MATCH_PARENT);
        etSAAttendanceSheetName.setLayoutParams(lp);
        etSAAttendanceSheetName.setText(bundle.getString("name"));

        AlertDialog.Builder builder = new AlertDialog.Builder(this);
        builder.setTitle("Edit Attendance Sheet")
                .setPositiveButton("Save", new DialogInterface.OnClickListener() {
                    @Override
                    public void onClick(DialogInterface dialog, int which) {
                        saveAttendanceSheet();
                    }
                })
                .setNegativeButton("Cancel", new DialogInterface.OnClickListener() {
                    @Override
                    public void onClick(DialogInterface dialog, int which) {

                    }
                })
                .setView(etSAAttendanceSheetName);

        saveAttendanceSheetDialog = builder.create();

        AlertDialog.Builder builder1 = new AlertDialog.Builder(this);
        builder1.setTitle("Confirm Deletion")
                .setMessage("Delete this item?")
                .setIcon(R.drawable.ic_warning_black_24dp)
                .setPositiveButton(android.R.string.ok, new DialogInterface.OnClickListener() {
                    public void onClick(DialogInterface dialog, int whichButton) {
                        deleteAttendanceSheet();
                    }})
                .setNegativeButton(android.R.string.cancel, null);

        deleteAttendanceSheetDialog = builder1.create();


        loadSchedule();
        loadStudentAttendances();
    }

    private void deleteAttendanceSheet(){

        findViewById(R.id.pb_as).setVisibility(View.VISIBLE);

        db.collection("attendance_sheets")
                .document(bundle.getString("id"))
                .update("deleted", "1")
                .addOnSuccessListener(new OnSuccessListener<Void>() {
                    @Override
                    public void onSuccess(Void aVoid) {
                        onBackPressed();
                        Toast.makeText(getApplicationContext(),"Deleted",Toast.LENGTH_LONG).show();
                    }
                })
                .addOnFailureListener(new OnFailureListener() {
                    @Override
                    public void onFailure(@NonNull Exception e) {
                        System.out.println(e);
                        Toast.makeText(getApplicationContext(),"Error! " + e.getMessage(),Toast.LENGTH_LONG).show();
                    }
                });
    }

    private void saveAttendanceSheet(){
        db.collection("attendance_sheets")
                .document(bundle.getString("id"))
                .update("name", etSAAttendanceSheetName.getText().toString())
                .addOnSuccessListener(new OnSuccessListener<Void>() {
                    @Override
                    public void onSuccess(Void aVoid) {
                        tvASAttendanceSheetName.setText("Sheet Name: " + etSAAttendanceSheetName.getText().toString());
                        Toast.makeText(getApplicationContext(),"Horray! Attendance sheet has been edited :)",Toast.LENGTH_LONG).show();
                    }
                })
                .addOnFailureListener(new OnFailureListener() {
                    @Override
                    public void onFailure(@NonNull Exception e) {
                        System.out.println(e);
                        Toast.makeText(getApplicationContext(),"Error! " + e.getMessage(),Toast.LENGTH_LONG).show();
                    }
                });
    }

    private void loadStudentAttendances(){

        studentAttendances = new ArrayList<>();

        db.collection("student_attendances")
                .whereEqualTo("attendance_sheet_id", bundle.getString("id"))
                .get()
                .addOnCompleteListener(new OnCompleteListener<QuerySnapshot>() {
            @Override
            public void onComplete(@NonNull Task<QuerySnapshot> task) {
                if (task.isSuccessful()) {
                    if(task.getResult().size() > 0){
                        populateRVStudentAttendances(task, STUDENT_ATTENDANCES);
                    }else{
                        loadStudentSchedules(bundle.getString("scheduleId"));
                    }
                }
            }
        });
    }

    private void loadStudentSchedules(String scheduleId){
        db.collection("student_schedules")
                .whereEqualTo("schedule_id", scheduleId)
                .get()
                .addOnCompleteListener(new OnCompleteListener<QuerySnapshot>() {
            @Override
            public void onComplete(@NonNull Task<QuerySnapshot> task) {
                populateRVStudentAttendances(task, STUDENT_SCHEDULES);
            }
        });
    }

    private void populateRVStudentAttendances(Task<QuerySnapshot> task, String dataSource){

        if (task.isSuccessful()) {
            for (QueryDocumentSnapshot document : task.getResult()) {

                String firstName = document.getString("first_name");
                String middleName = document.getString("middle_name");
                String lastName = document.getString("last_name");
                String studentId = document.getString("student_id");
                String attendanceSheetId = bundle.getString("id");

                String id = null;
                String status = "0";

                if(dataSource.equals(STUDENT_ATTENDANCES)){
                    id = document.getId();
                    status = document.getString("status");
                }

                StudentAttendance studentAttendance = new StudentAttendance(id, attendanceSheetId, studentId, status, firstName, middleName, lastName);

                studentAttendances.add(studentAttendance);
            }
        }

        final RecyclerView recyclerView = findViewById(R.id.rv_student_attendances);

        StudentAttendanceAdapter studentAttendanceAdapter = new StudentAttendanceAdapter(studentAttendances);

        recyclerView.setAdapter(studentAttendanceAdapter);
        recyclerView.setLayoutManager(new LinearLayoutManager(this));

//        Switch swtchASStudentList = findViewById(R.id.swtch_as_student_list);
//        swtchASStudentList.setEnabled(true);
//        swtchASStudentList.setOnClickListener(new View.OnClickListener() {
//            @Override
//            public void onClick(View v) {
//                Switch sw = (Switch) v;
//                RecyclerView.LayoutManager recyclerViewLayoutManager = recyclerView.getLayoutManager();
//
//                for (int i=0;i<recyclerViewLayoutManager.getItemCount();i++){
//                    CheckBox cbSAStatus = recyclerViewLayoutManager.findViewByPosition(i).findViewById(R.id.cb_sa_status);
//                    cbSAStatus.setEnabled(sw.isChecked());
//                }
//            }
//        });

        findViewById(R.id.pb_as).setVisibility(View.GONE);
    }

    private void saveStudentAttendances(){

        findViewById(R.id.pb_as).setVisibility(View.VISIBLE);

        WriteBatch writeBatch = db.batch();

        CollectionReference studentAttendancesCollection = db.collection("student_attendances");

        RecyclerView recyclerView = findViewById(R.id.rv_student_attendances);
        final StudentAttendanceAdapter studentAttendanceAdapter = (StudentAttendanceAdapter) recyclerView.getAdapter();
        final List<StudentAttendance> studentAttendances = studentAttendanceAdapter.getStudentAttendances();

        for (StudentAttendance studentAttendance : studentAttendances) {

            DocumentReference saDocumentReference;

            if(studentAttendance.getId() == null){

                saDocumentReference = studentAttendancesCollection.document();

                HashMap<String,String> hashMap = new HashMap<>();
                hashMap.put("id", saDocumentReference.getId());
                hashMap.put("attendance_sheet_id", studentAttendance.getAttendanceSheetId());
                hashMap.put("first_name", studentAttendance.getFirstName());
                hashMap.put("middle_name", studentAttendance.getMiddleName());
                hashMap.put("last_name", studentAttendance.getLastName());
                hashMap.put("student_id", studentAttendance.getStudentId());
                hashMap.put("status", studentAttendance.getStatus());

                //adding for optimization
                hashMap.put("attendance_sheet_date", bundle.getString("date"));
                hashMap.put("subject_id", subject.getId());
                hashMap.put("subject_code", subject.getCode());
                hashMap.put("subject_name", subject.getName());

                studentAttendance.setId(hashMap.get("id"));

                writeBatch.set(saDocumentReference, hashMap);
            }else{
                saDocumentReference = studentAttendancesCollection.document(studentAttendance.getId());
                writeBatch.update(saDocumentReference, "status", studentAttendance.getStatus());
            }
        }

        writeBatch.commit().addOnCompleteListener(new OnCompleteListener<Void>() {
            @Override
            public void onComplete(@NonNull Task<Void> task) {
                if(task.isSuccessful()){
                    onBackPressed();
                    Toast.makeText(getApplicationContext(),"Horray! Attendance has been updated :)",Toast.LENGTH_LONG).show();
                }else {
                    System.out.println(task.getException());
                    Toast.makeText(getApplicationContext(),"Error! " + task.getException().getMessage(),Toast.LENGTH_LONG).show();
                }
            }
        });
    }

    private void loadSchedule(){
        db.collection("schedules")
                .document(bundle.getString("scheduleId"))
                .get()
                .addOnCompleteListener(new OnCompleteListener<DocumentSnapshot>() {
                    @Override
                    public void onComplete(@NonNull Task<DocumentSnapshot> task) {
                        populateTVASSchedule(task);
                    }
                });
    }

    @SuppressLint("SetTextI18n")
    private void populateTVASSchedule(Task<DocumentSnapshot> task) {

        TextView tvASSchedule = findViewById(R.id.tv_as_schedule);
        TextView tvASScheduleTime = findViewById(R.id.tv_as_schedule_time);

        DocumentSnapshot document = task.getResult();

        assert document != null;
        String id = document.getId();
        String name = document.getString("name");
        String code = document.getString("code");
        String days = document.getString("days");
        String startTime = document.getString("start_time");
        String endTime = document.getString("end_time");
        String teacherId = document.getString("teacher_id");
        String subjectId = document.getString("subject_id");
        schedule = new Schedule(id, code, name, startTime, endTime, teacherId, subjectId, days);

        assert startTime != null;
        assert endTime != null;
        tvASSchedule.setText("Schedule: " + code + " " + name + " ");
        tvASScheduleTime.setText("Schedule Time: " + days + " " + convertTimeTo12(startTime) + " - " + convertTimeTo12(endTime));

        loadSubject(subjectId);
    }

    private void loadSubject(String subjectId){
        db.collection("subjects")
                .document(subjectId)
                .get()
                .addOnCompleteListener(new OnCompleteListener<DocumentSnapshot>() {
            @Override
            public void onComplete(@NonNull Task<DocumentSnapshot> task) {
                populateTVASSubject(task);
            }
        });
    }

    private void populateTVASSubject(Task<DocumentSnapshot> task){
        TextView tvASSubject = findViewById(R.id.tv_as_subject);

        DocumentSnapshot document = task.getResult();

        assert document != null;
        String id = document.getId();
        String name = document.getString("name");
        String code = document.getString("code");

        subject = new Subject();
        subject.setId(id);
        subject.setName(name);
        subject.setCode(code);

        tvASSubject.setText("Subject: " + name + " (" + code + ")");
    }

    public static String convertTimeTo12(String time){
        String[] aTime = time.split(":");

        String hour = aTime[0];
        String min = aTime[1];
        String mrdm = "";

        int ihour = Integer.parseInt(hour);

        if(ihour > 12){
            hour = (ihour - 12) + "";
        }

        if(ihour > 11){
            mrdm = "PM";
        }else{
            mrdm = "AM";
        }

        return hour + ":" + min + " " + mrdm;
    }

    @Override
    public boolean onCreateOptionsMenu(Menu menu) {
        // R.menu.teacher_dashboard_menu is a reference to an xml file named teacher_dashboard_menur_dashboard_menu.xml which should be inside your res/menu directory.
        // If you don't have res/menu, just create a directory named "menu" inside res
        getMenuInflater().inflate(R.menu.attendance_sheet_menu, menu);
        return super.onCreateOptionsMenu(menu);
    }

    @Override
    public boolean onOptionsItemSelected(@NonNull MenuItem item) {
        if(item.getItemId() == android.R.id.home){
            onBackPressed();
        }

        if(item.getItemId() == R.id.mi_sa_save){
            saveStudentAttendances();
            item.setEnabled(false);
        }

        if(item.getItemId() == R.id.mi_sa_edit){
            saveAttendanceSheetDialog.show();
        }

        if(item.getItemId() == R.id.mi_sa_delete){
            deleteAttendanceSheetDialog.show();
        }

        return super.onOptionsItemSelected(item);
    }
}
