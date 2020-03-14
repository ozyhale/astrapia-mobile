package com.astrapia.astrapia;

import androidx.annotation.NonNull;
import androidx.appcompat.app.AlertDialog;
import androidx.appcompat.app.AppCompatActivity;

import android.content.DialogInterface;
import android.os.Bundle;
import android.view.MenuItem;
import android.view.View;
import android.widget.Button;
import android.widget.EditText;
import android.widget.ProgressBar;
import android.widget.Toast;

import com.astrapia.astrapia.models.Student;
import com.google.android.gms.tasks.OnCompleteListener;
import com.google.android.gms.tasks.Task;
import com.google.firebase.auth.FirebaseAuth;
import com.google.firebase.firestore.DocumentSnapshot;
import com.google.firebase.firestore.FirebaseFirestore;
import com.google.firebase.firestore.QueryDocumentSnapshot;
import com.google.firebase.firestore.QuerySnapshot;

import java.util.HashMap;

public class ParentSettingsActivity extends AppCompatActivity implements View.OnClickListener {

    private FirebaseAuth mAuth;
    private FirebaseFirestore db;

    private ProgressBar pbPS;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_parent_settings);
        getSupportActionBar().setDisplayHomeAsUpEnabled(true);
        setTitle("Settings");

        mAuth = FirebaseAuth.getInstance();
        db = FirebaseFirestore.getInstance();

        findViewById(R.id.btnPSSave).setEnabled(false);
        findViewById(R.id.btnPSAddSchoolID).setEnabled(false);
        findViewById(R.id.btnPSDeleteSchoolID).setEnabled(false);

        pbPS = findViewById(R.id.pbPS);

        db.collection("parents")
                .document(mAuth.getCurrentUser().getUid())
                .get()
                .addOnCompleteListener(new OnCompleteListener<DocumentSnapshot>() {
                    @Override
                    public void onComplete(@NonNull Task<DocumentSnapshot> task) {
                        if(task.isSuccessful()){
                            DocumentSnapshot document = task.getResult();

                            String firstName = document.getString("first_name");
                            String middleName = document.getString("middle_name");
                            String lastName = document.getString("last_name");

                            EditText etPSFirstName = findViewById(R.id.etPSFirstName);
                            EditText etPSMiddleName = findViewById(R.id.etPSMiddleName);
                            EditText etPSLastName = findViewById(R.id.etPSLastName);

                            etPSFirstName.setText(firstName);
                            etPSMiddleName.setText(middleName);
                            etPSLastName.setText(lastName);
                        }

                        populateButtons();
                    }
                });
    }

    private void populateButtons(){
        findViewById(R.id.btnPSSave).setEnabled(true);
        findViewById(R.id.btnPSAddSchoolID).setEnabled(true);
        findViewById(R.id.btnPSDeleteSchoolID).setEnabled(true);

        findViewById(R.id.btnPSSave).setOnClickListener(this);
        findViewById(R.id.btnPSAddSchoolID).setOnClickListener(this);
        findViewById(R.id.btnPSDeleteSchoolID).setOnClickListener(this);

        pbPS.setVisibility(View.GONE);
    }

    @Override
    public boolean onOptionsItemSelected(@NonNull MenuItem item) {
        if(item.getItemId() == android.R.id.home){
            onBackPressed();
        }

        return super.onOptionsItemSelected(item);
    }

    @Override
    public void onClick(final View v) {
        setEnabledAllButtons(false);
        pbPS.setVisibility(View.VISIBLE);
        if(v.getId() == R.id.btnPSSave){

            EditText etPSFirstName = findViewById(R.id.etPSFirstName);
            EditText etPSMiddleName = findViewById(R.id.etPSMiddleName);
            EditText etPSLastName = findViewById(R.id.etPSLastName);

            HashMap<String, Object> hashMap = new HashMap<>();
            hashMap.put("first_name", etPSFirstName.getText().toString());
            hashMap.put("middle_name", etPSMiddleName.getText().toString());
            hashMap.put("last_name", etPSLastName.getText().toString());

            db.collection("parents")
                    .document(mAuth.getCurrentUser().getUid())
                    .update(hashMap)
                    .addOnCompleteListener(new OnCompleteListener<Void>() {
                        @Override
                        public void onComplete(@NonNull Task<Void> task) {
                            if(task.isSuccessful()){
                                Toast.makeText(getApplicationContext(),"User information has been saved",Toast.LENGTH_LONG).show();
                                onBackPressed();
                            }
                        }
                    });
        }

        if(v.getId() == R.id.btnPSAddSchoolID){
            EditText editText = findViewById(R.id.etAddSchoolID);
            String schoolId = editText.getText().toString();
            if(!schoolId.equals("")){
                db.collection("students")
                        .whereEqualTo("school_id", schoolId)
                        .get()
                        .addOnCompleteListener(new OnCompleteListener<QuerySnapshot>() {
                            @Override
                            public void onComplete(@NonNull Task<QuerySnapshot> task) {
                                if(task.isSuccessful()){

                                    Student student = new Student();

                                    for(QueryDocumentSnapshot document : task.getResult()){
                                        String id = document.getId();
                                        String firstName = document.getString("first_name");
                                        String middleName = document.getString("middle_name");
                                        String lastName = document.getString("last_name");
                                        String parentId = document.getString("parent_id");

                                        student.setId(id);
                                        student.setFirstName(firstName);
                                        student.setMiddleName(middleName);
                                        student.setLastName(lastName);
                                        student.setParentId(parentId);

                                        break;
                                    }

                                    if(task.getResult().size() == 0){
                                        Toast.makeText(getApplicationContext(),"Student Id does not exists",Toast.LENGTH_LONG).show();
                                        setEnabledAllButtons(true);
                                        pbPS.setVisibility(View.GONE);
                                    }else if(student.getParentId().equals(mAuth.getCurrentUser().getUid())){
                                        Toast.makeText(getApplicationContext(),"Student already added",Toast.LENGTH_LONG).show();
                                        setEnabledAllButtons(true);
                                        pbPS.setVisibility(View.GONE);
                                    }else if(!student.getParentId().equals("")){
                                        Toast.makeText(getApplicationContext(),"Student already added to other parent",Toast.LENGTH_LONG).show();
                                        setEnabledAllButtons(true);
                                        pbPS.setVisibility(View.GONE);
                                    }else{
                                        showConfirmChildDialog(student.getFullName(), student.getId());
                                    }
                                }
                            }
                        });
            }else{
                Toast.makeText(getApplicationContext(),"Oops! you forgot something",Toast.LENGTH_LONG).show();
                setEnabledAllButtons(true);
                pbPS.setVisibility(View.GONE);
            }
        }

        if(v.getId() == R.id.btnPSDeleteSchoolID){
            System.out.println("DELETE ID");
        }
    }

    private void setEnabledAllButtons(boolean enabled){
        findViewById(R.id.btnPSSave).setEnabled(enabled);
        findViewById(R.id.btnPSAddSchoolID).setEnabled(enabled);
        findViewById(R.id.btnPSDeleteSchoolID).setEnabled(enabled);
    }

    private void showConfirmChildDialog(String studentFullName, final String studentId){

        pbPS.setVisibility(View.GONE);

        AlertDialog.Builder builder1 = new AlertDialog.Builder(this);
        builder1.setTitle("Please confirm if this is your child")
                .setMessage(studentFullName)
                .setPositiveButton(android.R.string.yes, new DialogInterface.OnClickListener() {
                    public void onClick(DialogInterface dialog, int whichButton) {
                        saveStudentSchoolId(studentId, mAuth.getCurrentUser().getUid());
                    }})
                .setNegativeButton(android.R.string.no, new DialogInterface.OnClickListener() {
                    @Override
                    public void onClick(DialogInterface dialog, int which) {
                        setEnabledAllButtons(false);
                    }
                });
        builder1.create().show();
    }

    private void saveStudentSchoolId(String id, String parentId){

        final Button btn = findViewById(R.id.btnPSAddSchoolID);

        pbPS.setVisibility(View.VISIBLE);

        db.collection("students")
                .document(id)
                .update("parent_id", parentId)
                .addOnCompleteListener(new OnCompleteListener<Void>() {
                    @Override
                    public void onComplete(@NonNull Task<Void> task) {
                        if(task.isSuccessful()){
                            onBackPressed();
                            Toast.makeText(getApplicationContext(),"Student added",Toast.LENGTH_LONG).show();
                        }

                        setEnabledAllButtons(true);
                        pbPS.setVisibility(View.GONE);
                    }
                });
    }
}
