package com.astrapia.astrapia;

import androidx.annotation.NonNull;
import androidx.appcompat.app.AlertDialog;
import androidx.appcompat.app.AppCompatActivity;
import androidx.recyclerview.widget.LinearLayoutManager;
import androidx.recyclerview.widget.RecyclerView;

import android.annotation.SuppressLint;
import android.content.DialogInterface;
import android.content.Intent;
import android.os.Bundle;
import android.view.Menu;
import android.view.MenuItem;
import android.view.View;
import android.widget.ArrayAdapter;
import android.widget.Spinner;
import android.widget.TextView;
import android.widget.Toast;

import com.astrapia.astrapia.adapters.AttendanceSheetsAdapter;
import com.astrapia.astrapia.models.AttendanceSheet;
import com.astrapia.astrapia.models.Schedule;
import com.google.android.gms.tasks.OnCompleteListener;
import com.google.android.gms.tasks.Task;
import com.google.android.material.floatingactionbutton.FloatingActionButton;
import com.google.firebase.auth.FirebaseAuth;
import com.google.firebase.firestore.DocumentReference;
import com.google.firebase.firestore.FirebaseFirestore;
import com.google.firebase.firestore.QueryDocumentSnapshot;
import com.google.firebase.firestore.QuerySnapshot;

import java.text.SimpleDateFormat;
import java.util.ArrayList;
import java.util.Date;
import java.util.HashMap;
import java.util.List;
import java.util.Map;

public class TeacherDashboardActivity extends AppCompatActivity {

    private FirebaseAuth mAuth;
    private FirebaseFirestore db;

    ArrayList<AttendanceSheet> attendanceSheets;
    AttendanceSheetsAdapter attendanceSheetsAdapter;
    AlertDialog createAttendanceSheetDialog;

    List<Schedule> schedules;

    @SuppressLint("ResourceType")
    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_teacher_dashboard);
        getSupportActionBar().setTitle("Attendance Sheets");

        mAuth = FirebaseAuth.getInstance();
        db = FirebaseFirestore.getInstance();

        //Dialog when creating a new attendance sheet
        AlertDialog.Builder builder = new AlertDialog.Builder(this);
        builder.setTitle("Create Attendance Sheet")
                .setPositiveButton("Save", new DialogInterface.OnClickListener() {
                    @Override
                    public void onClick(DialogInterface dialog, int which) {
                        createAttendanceSheet();
                    }
                })
                .setNegativeButton("Cancel", new DialogInterface.OnClickListener() {
                    @Override
                    public void onClick(DialogInterface dialog, int which) {

                    }
                })
                .setView(getLayoutInflater().inflate(R.layout.dialog_attendance_sheet, null));

        createAttendanceSheetDialog = builder.create();

        //Add Button
        FloatingActionButton fabAddAttendanceSheet = findViewById(R.id.fab_add_attendance_sheet);
        fabAddAttendanceSheet.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                createAttendanceSheetDialog.show();

                TextView tvAttendanceSheetName = createAttendanceSheetDialog.findViewById(R.id.tv_attendance_sheet_name);
                Spinner spnrSchedules = createAttendanceSheetDialog.findViewById(R.id.spnr_schedules);

                tvAttendanceSheetName.setText("");
                spnrSchedules.setSelection(0);

                if(schedules == null){
                    loadSchedules();
                }
            }
        });
    }

    @Override
    protected void onResume() {
        loadAttendanceSheets();
        super.onResume();
    }

    private void createAttendanceSheet(){

        SimpleDateFormat simpleDateFormat = new SimpleDateFormat("YYYY-MM-dd");
        String currentDate = simpleDateFormat.format(new Date());

        TextView tvAttendanceSheetName = createAttendanceSheetDialog.findViewById(R.id.tv_attendance_sheet_name);
        Spinner spnrSchedules = createAttendanceSheetDialog.findViewById(R.id.spnr_schedules);

        String attendanceSheetName = tvAttendanceSheetName.getText().toString();
        String scheduleId = schedules.get(spnrSchedules.getSelectedItemPosition()).getId();

        if(attendanceSheetName != "" && scheduleId != null){
            DocumentReference documentReference = db.collection("attendance_sheets").document();

            Map<String, Object> attendanceSheet = new HashMap<>();
            attendanceSheet.put("id", documentReference.getId());
            attendanceSheet.put("date", currentDate);
            attendanceSheet.put("name", attendanceSheetName);
            attendanceSheet.put("schedule_id", scheduleId);
            attendanceSheet.put("teacher_id", mAuth.getCurrentUser().getUid());
            attendanceSheet.put("deleted", "0");

            documentReference.set(attendanceSheet);

            loadAttendanceSheets();

            Intent intent = new Intent(this, AttendanceSheetActivity.class);
            intent.putExtra("title", attendanceSheet.get("name").toString());
            intent.putExtra("name",  attendanceSheet.get("name").toString());
            intent.putExtra("id",  attendanceSheet.get("id").toString());
            intent.putExtra("date",  attendanceSheet.get("date").toString());
            intent.putExtra("scheduleId",  attendanceSheet.get("schedule_id").toString());
            intent.putExtra("teacherId",  attendanceSheet.get("teacher_id").toString());
            startActivity(intent);
        }else{
            Toast.makeText(getApplicationContext(),"Oops! I think you're missing something",Toast.LENGTH_LONG).show();
        }
    }

    private void loadAttendanceSheets(){

        attendanceSheets = new ArrayList<>();

        db.collection("attendance_sheets")
                .whereEqualTo("teacher_id", mAuth.getCurrentUser().getUid())
                .whereEqualTo("deleted", "0")
                .get()
                .addOnCompleteListener(new OnCompleteListener<QuerySnapshot>() {
                    @Override
                    public void onComplete(@NonNull Task<QuerySnapshot> task) {
                        populateRVattendanceSheets(task);
                    }
                });
    }

    private void loadSchedules(){

        schedules = new ArrayList<>();

        db.collection("schedules")
                .whereEqualTo("teacher_id", mAuth.getCurrentUser().getUid())
                .get()
                .addOnCompleteListener(new OnCompleteListener<QuerySnapshot>() {
                    @Override
                    public void onComplete(@NonNull Task<QuerySnapshot> task) {
                        populateSpnrSchedules(task);
                    }
                });
    }

    private void populateRVattendanceSheets(Task<QuerySnapshot> task){

        if (task.isSuccessful()) {
            for (QueryDocumentSnapshot document : task.getResult()) {
                String id = document.getId();
                String name = document.getString("name");
                String date = document.getString("date");
                String scheduleId = document.getString("schedule_id");
                String teacherId = document.getString("teacher_id");
                AttendanceSheet attendanceSheet = new AttendanceSheet(id, name, date, scheduleId, teacherId);
                attendanceSheets.add(attendanceSheet);
            }
        }

        // Lookup the recyclerview in activity layout
        RecyclerView rvAttendanceSheet = findViewById(R.id.rv_attendance_sheet);

        if(!attendanceSheets.isEmpty()){
            // Create adapter passing in the sample user data
            attendanceSheetsAdapter = new AttendanceSheetsAdapter(attendanceSheets, getApplicationContext());

            // Attach the adapter to the recyclerview to populate items
            rvAttendanceSheet.setAdapter(attendanceSheetsAdapter);

            // Set layout manager to position the items
            rvAttendanceSheet.setLayoutManager(new LinearLayoutManager(this));
        }

        findViewById(R.id.pb_td).setVisibility(View.GONE);
    }

    private void populateSpnrSchedules(Task<QuerySnapshot> task){

        List<String> spnrSchedulesItemList = new ArrayList<>();

        spnrSchedulesItemList.add("");
        schedules.add(new Schedule());

        if(task.isSuccessful()){
            for (QueryDocumentSnapshot document : task.getResult()) {
                String id = document.getId();
                String code = document.getString("code");
                String name = document.getString("name");
                String startTime = document.getString("start_time");
                String endTime = document.getString("end_time");
                String teacherId = document.getString("teacher_id");
                String subjectId = document.getString("subject_id");
                String days = document.getString("days");
                Schedule schedule = new Schedule(id, code, name, startTime, endTime, teacherId, subjectId, days);
                schedules.add(schedule);
                spnrSchedulesItemList.add(name + " (" + code + ")");
            }
        }

        Spinner spnrSchedules = createAttendanceSheetDialog.findViewById(R.id.spnr_schedules);

        ArrayAdapter<String> dataAdapter = new ArrayAdapter<>(this,
                android.R.layout.simple_spinner_item, spnrSchedulesItemList);
        dataAdapter.setDropDownViewResource(android.R.layout.simple_spinner_dropdown_item);

        spnrSchedules.setAdapter(dataAdapter);
    }

    // create an action bar button
    @Override
    public boolean onCreateOptionsMenu(Menu menu) {
        // R.menu.teacher_dashboard_menu is a reference to an xml file named teacher_dashboard_menur_dashboard_menu.xml which should be inside your res/menu directory.
        // If you don't have res/menu, just create a directory named "menu" inside res
        getMenuInflater().inflate(R.menu.user_dashboard_menu, menu);
        return super.onCreateOptionsMenu(menu);
    }

    // handle button activities
    @Override
    public boolean onOptionsItemSelected(MenuItem item) {
        int id = item.getItemId();

        Intent intent = null;

        if (id == R.id.menu_item_logout) {
            mAuth.signOut();
            intent = new Intent(this, LoginActivity.class);
            startActivity(intent);
            finish();
        }

        if (id == R.id.menu_item_settings) {
            intent = new Intent(this, TeacherSettingsActivity.class);
            startActivity(intent);
        }

        return super.onOptionsItemSelected(item);
    }
}
