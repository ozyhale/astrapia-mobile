package com.astrapia.astrapia;

import androidx.annotation.NonNull;
import androidx.appcompat.app.AppCompatActivity;

import android.content.Intent;
import android.os.Bundle;
import android.view.MenuItem;
import android.view.View;
import android.widget.CheckBox;
import android.widget.EditText;
import android.widget.RadioButton;
import android.widget.ScrollView;
import android.widget.Toast;

import com.google.android.gms.tasks.OnCompleteListener;
import com.google.android.gms.tasks.Task;
import com.google.firebase.auth.AuthResult;
import com.google.firebase.auth.FirebaseAuth;
import com.google.firebase.firestore.FirebaseFirestore;
import com.google.firebase.firestore.QueryDocumentSnapshot;
import com.google.firebase.firestore.QuerySnapshot;

import java.util.ArrayList;
import java.util.Arrays;
import java.util.HashMap;
import java.util.List;

public class RegistrationActivity extends AppCompatActivity {

    private FirebaseAuth mAuth;
    private FirebaseFirestore db;

    private int teacherScheduleSize = 0;
    private int teacherScheduleCount = 0;

    private int parentStudentSize = 0;
    private int parentStudentCount = 0;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_registration);
        getSupportActionBar().setDisplayHomeAsUpEnabled(true);
        setTitle("Registration");

        mAuth = FirebaseAuth.getInstance();
        db = FirebaseFirestore.getInstance();
    }

    public void onRadioButtonClicked(View view){
        boolean checked = ((RadioButton) view).isChecked();

        switch(view.getId()) {
            case R.id.rbRegTeacher:
                if (checked){
                    findViewById(R.id.etRegSchoolID).setVisibility(View.GONE);
                    findViewById(R.id.etRegTeacherCode).setVisibility(View.VISIBLE);
                    findViewById(R.id.tvRegSchoolID).setVisibility(View.GONE);
                    findViewById(R.id.tvRegTeacherCode).setVisibility(View.VISIBLE);
                }
                break;
            case R.id.rbRegParent:
                if (checked){
                    findViewById(R.id.etRegSchoolID).setVisibility(View.VISIBLE);
                    findViewById(R.id.etRegTeacherCode).setVisibility(View.GONE);
                    findViewById(R.id.tvRegSchoolID).setVisibility(View.VISIBLE);
                    findViewById(R.id.tvRegTeacherCode).setVisibility(View.GONE);
                }
                break;
        }
    }

    public static boolean validateEmail(String email){
        String emailPattern = "[a-zA-Z0-9._-]+@[a-z]+\\.+[a-z]+";
        return email.matches(emailPattern);
    }

    public void register(View view){

        findViewById(R.id.pbReg).setVisibility(View.VISIBLE);
        findViewById(R.id.btnRegRegister).setEnabled(false);
        ScrollView svReg = findViewById(R.id.svReg);
        svReg.fullScroll(ScrollView.FOCUS_UP);

        boolean hasInvalidation = false;
        String emptyFields = "";
        String invalidEmailMessage = "";
        String passwordNotMatchedMessage = "";
        String unselectedUserTypeMessage = "";

        EditText etRegFirstName = findViewById(R.id.etRegFirstName);
        EditText etRegMiddleName = findViewById(R.id.etRegMiddleName);
        EditText etRegLastName = findViewById(R.id.etRegLastName);
        EditText etRegEmail = findViewById(R.id.etRegEmail);
        EditText etRegPassword = findViewById(R.id.etRegPassword);
        EditText etRegRetypePassword = findViewById(R.id.etRegRetypePassword);
        RadioButton rbRegTeacher = findViewById(R.id.rbRegTeacher);
        RadioButton rbRegParent = findViewById(R.id.rbRegParent);
        EditText etRegTeacherCode = findViewById(R.id.etRegTeacherCode);
        EditText etRegSchoolID = findViewById(R.id.etRegSchoolID);

        if(etRegFirstName.getText().toString().isEmpty()){
            hasInvalidation = true;
            emptyFields += "First Name";
        }

        if(etRegMiddleName.getText().toString().isEmpty()){
            hasInvalidation = true;
            if(!emptyFields.isEmpty()) emptyFields += ", ";
            emptyFields += "Middle Name";
        }

        if(etRegLastName.getText().toString().isEmpty()){
            hasInvalidation = true;
            if(!emptyFields.isEmpty()) emptyFields += ", ";
            emptyFields += "Last Name";
        }

        if(etRegEmail.getText().toString().isEmpty()){
            hasInvalidation = true;
            if(!emptyFields.isEmpty()) emptyFields += ", ";
            emptyFields += "Email Address";
        }else if(!validateEmail(etRegEmail.getText().toString())){
            hasInvalidation = true;
            invalidEmailMessage = "Invalid Email Address. ";
        }

        if(etRegPassword.getText().toString().isEmpty()){
            hasInvalidation = true;
            if(!emptyFields.isEmpty()) emptyFields += ", ";
            emptyFields += "Password";
        }else if(etRegRetypePassword.getText().toString().isEmpty()){
            hasInvalidation = true;
            if(!emptyFields.isEmpty()) emptyFields += ", ";
            emptyFields += "Retype Password";
        }else if(!etRegRetypePassword.getText().toString().equals(etRegPassword.getText().toString())){
            passwordNotMatchedMessage = "Passwords not matched. ";
        }

        if(!rbRegTeacher.isChecked() && !rbRegParent.isChecked()){
            hasInvalidation = true;
            unselectedUserTypeMessage = "Please specify if you're a teacher or a parent. ";
        }else if(rbRegTeacher.isChecked() && etRegTeacherCode.getText().toString().isEmpty()){
            hasInvalidation = true;
            if(!emptyFields.isEmpty()) emptyFields += ", ";
            emptyFields += "Teacher Code";
        }else if(rbRegParent.isChecked() && etRegSchoolID.getText().toString().isEmpty()){
            hasInvalidation = true;
            if(!emptyFields.isEmpty()) emptyFields += ", ";
            emptyFields += "School ID(s)";
        }

        if(!hasInvalidation){
            mAuth.createUserWithEmailAndPassword(etRegEmail.getText().toString(), etRegPassword.getText().toString())
                    .addOnCompleteListener(new OnCompleteListener<AuthResult>() {
                        @Override
                        public void onComplete(@NonNull Task<AuthResult> task) {
                            if(task.isSuccessful()){
                                registerStep2();
                            }else{
                                Toast.makeText(getApplicationContext(),"Error: " + task.getException(),Toast.LENGTH_LONG).show();
                                findViewById(R.id.pbReg).setVisibility(View.GONE);
                                findViewById(R.id.btnRegRegister).setEnabled(true);
                            }
                        }
                    });
        }else{
            Toast.makeText(getApplicationContext(),"Error: " + (emptyFields.isEmpty() ? "" : emptyFields + " are required. ") + invalidEmailMessage + passwordNotMatchedMessage + unselectedUserTypeMessage,Toast.LENGTH_LONG).show();
            findViewById(R.id.pbReg).setVisibility(View.GONE);
            findViewById(R.id.btnRegRegister).setEnabled(true);
        }
    }

    private void registerStep2(){

        String collection = "";

        EditText etRegFirstName = findViewById(R.id.etRegFirstName);
        EditText etRegMiddleName = findViewById(R.id.etRegMiddleName);
        EditText etRegLastName = findViewById(R.id.etRegLastName);
        RadioButton rbRegTeacher = findViewById(R.id.rbRegTeacher);
        RadioButton rbRegParent = findViewById(R.id.rbRegParent);

        HashMap<String,Object> hashMap = new HashMap<>();
        hashMap.put("id", mAuth.getCurrentUser().getUid());
        hashMap.put("first_name", etRegFirstName.getText().toString());
        hashMap.put("middle_name", etRegMiddleName.getText().toString());
        hashMap.put("last_name", etRegLastName.getText().toString());

        if(rbRegTeacher.isChecked()){
            EditText etRegTeacherCode = findViewById(R.id.etRegTeacherCode);
            hashMap.put("code", etRegTeacherCode.getText().toString());

            collection = "teachers";
        }else if(rbRegParent.isChecked()){
            collection = "parents";
        }

        final String finalCollection = collection;

        db.collection(finalCollection)
                .document(mAuth.getCurrentUser().getUid())
                .set(hashMap)
                .addOnCompleteListener(new OnCompleteListener<Void>() {
                    @Override
                    public void onComplete(@NonNull Task<Void> task) {
                        if(task.isSuccessful()){
                            if(finalCollection.equals("teachers")){
                                mapTeacherSchedules();
                            }else if(finalCollection.equals("parents")){
                                mapParentStudents();
                            }
                        }
                    }
                });
    }

    private void mapTeacherSchedules(){
        EditText etRegTeacherCode = findViewById(R.id.etRegTeacherCode);

        db.collection("schedules")
                .whereEqualTo("teacher_code", etRegTeacherCode.getText().toString())
                .get()
                .addOnCompleteListener(new OnCompleteListener<QuerySnapshot>() {
                    @Override
                    public void onComplete(@NonNull Task<QuerySnapshot> task) {
                        if(task.isSuccessful()){
                            teacherScheduleCount = 0;
                            teacherScheduleSize = task.getResult().size();
                            for(QueryDocumentSnapshot document : task.getResult()){
                                mapTeacherSchedule(document.getId());
                            }

                            if(teacherScheduleSize == 0){
                                login();
                            }
                        }
                    }
                });
    }

    private void mapTeacherSchedule(String id){
        db.collection("schedules")
                .document(id)
                .update("teacher_id", mAuth.getCurrentUser().getUid())
                .addOnCompleteListener(new OnCompleteListener<Void>() {
                    @Override
                    public void onComplete(@NonNull Task<Void> task) {
                        teacherScheduleCount++;
                        login();
                    }
                });
    }

    private void mapParentStudents(){
        EditText etRegSchoolID = findViewById(R.id.etRegSchoolID);

        String[] _schoolIds = etRegSchoolID.getText().toString().split(",");
        String[] __schoolIds = Arrays.copyOfRange(_schoolIds, 0, 10);
        List<String> schoolIds = new ArrayList<>();

        for(String schoolId : __schoolIds){
            if(schoolId != null){
                String _schoolId = schoolId.trim();
                if(!_schoolId.equals("")){
                    schoolIds.add(_schoolId);
                }
            }
        }

        db.collection("students")
                .whereIn("school_id", schoolIds)
                .get()
                .addOnCompleteListener(new OnCompleteListener<QuerySnapshot>() {
                    @Override
                    public void onComplete(@NonNull Task<QuerySnapshot> task) {
                        if(task.isSuccessful()){
                            parentStudentCount = 0;
                            parentStudentSize = task.getResult().size();
                            for(QueryDocumentSnapshot document : task.getResult()){
                                mapParentStudent(document.getId());
                            }

                            if(parentStudentSize == 0){
                                login();
                            }
                        }
                    }
                });
    }

    private void mapParentStudent(String studentId){
        db.collection("students")
                .document(studentId)
                .update("parent_id", mAuth.getCurrentUser().getUid())
                .addOnCompleteListener(new OnCompleteListener<Void>() {
                    @Override
                    public void onComplete(@NonNull Task<Void> task) {
                        parentStudentCount++;
                        login();
                    }
                });
    }

    private void login(){
        if((teacherScheduleSize == teacherScheduleCount) || (parentStudentSize == parentStudentCount)){
            Intent intent = new Intent(this, MainActivity.class);
            intent.setFlags(Intent.FLAG_ACTIVITY_NEW_TASK | Intent.FLAG_ACTIVITY_CLEAR_TASK);
            startActivity(intent);
            finish();
        }
    }

    @Override
    public boolean onOptionsItemSelected(@NonNull MenuItem item) {
        if(item.getItemId() == android.R.id.home){
            onBackPressed();
        }

        return super.onOptionsItemSelected(item);
    }
}
