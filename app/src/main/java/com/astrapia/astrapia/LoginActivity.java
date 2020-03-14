package com.astrapia.astrapia;

import android.content.Intent;
import android.os.Bundle;
import android.view.View;
import android.widget.Button;
import android.widget.Spinner;
import android.widget.TextView;
import android.widget.Toast;

import androidx.annotation.NonNull;
import androidx.appcompat.app.AppCompatActivity;

import com.google.android.gms.tasks.OnCompleteListener;
import com.google.android.gms.tasks.Task;
import com.google.firebase.auth.AuthResult;
import com.google.firebase.auth.FirebaseAuth;
import com.google.firebase.firestore.DocumentReference;
import com.google.firebase.firestore.DocumentSnapshot;
import com.google.firebase.firestore.FirebaseFirestore;

public class LoginActivity extends AppCompatActivity {

    private FirebaseAuth mAuth;
    private FirebaseFirestore db;

    private TextView email;
    private TextView password;
    private Button login;
    private Button register;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_login);

        mAuth = FirebaseAuth.getInstance();
        db = FirebaseFirestore.getInstance();

        email = findViewById(R.id.email);
        password = findViewById(R.id.password);
        login = findViewById(R.id.login);
        register = findViewById(R.id.register);

        login.setOnClickListener(new View.OnClickListener(){
            @Override
            public void onClick(View v) {
                login();
            }
        });
        register.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                Intent intent = new Intent(getApplicationContext(), RegistrationActivity.class);
                startActivity(intent);
            }
        });
    }

    private void login(){

        if(!email.getText().toString().isEmpty() && !password.getText().toString().isEmpty()){
            login.setEnabled(false);
            findViewById(R.id.pb_login).setVisibility(View.VISIBLE);
            mAuth.signInWithEmailAndPassword(email.getText().toString(), password.getText().toString())
                .addOnCompleteListener(this, new OnCompleteListener<AuthResult>(){
                    @Override
                    public void onComplete(@NonNull Task<AuthResult> task) {
                        if(task.isSuccessful()){
                            startUserDashboardActivity();
                        }else{
                            Toast.makeText(getApplicationContext(),"Error logging in. Please provide us the correct credentials",Toast.LENGTH_LONG).show();
                        }
                    }
                });
        }else{
            Toast.makeText(getApplicationContext(),"Error logging in. Please provide us the correct credentials",Toast.LENGTH_LONG).show();
        }
    }

    private void startUserDashboardActivity(){

        String[] userTypes = getResources().getStringArray(R.array.user_type);
        Spinner spnrLoginUserType = findViewById(R.id.spnr_login_user_type);

        if(userTypes[spnrLoginUserType.getSelectedItemPosition()].equals("Teacher")){
            db.collection("teachers")
                    .document(mAuth.getCurrentUser().getUid())
                    .get()
                    .addOnCompleteListener(new OnCompleteListener<DocumentSnapshot>() {
                @Override
                public void onComplete(@NonNull Task<DocumentSnapshot> task) {

                    if(task.isSuccessful() && task.getResult().exists()){
                        startTeacherDashboardActivity();
                    }else{
                        Toast.makeText(getApplicationContext(),"Error logging in. Please provide us the correct credentials",Toast.LENGTH_LONG).show();
                    }

                    login.setEnabled(true);
                    findViewById(R.id.pb_login).setVisibility(View.GONE);
                }
            });
        }else if(userTypes[spnrLoginUserType.getSelectedItemPosition()].equals("Parent")){
            db.collection("parents")
                    .document(mAuth.getCurrentUser().getUid())
                    .get()
                    .addOnCompleteListener(new OnCompleteListener<DocumentSnapshot>() {
                        @Override
                        public void onComplete(@NonNull Task<DocumentSnapshot> task) {
                            if(task.isSuccessful() && task.getResult().exists()){
                                startParentDashboardActivity();
                            }else{
                                Toast.makeText(getApplicationContext(),"Error logging in. Please provide us the correct credentials",Toast.LENGTH_LONG).show();
                            }

                            login.setEnabled(true);
                            findViewById(R.id.pb_login).setVisibility(View.GONE);

                        }
                    });
        }
    }

    private void startTeacherDashboardActivity(){
        Intent intent = new Intent(this, TeacherDashboardActivity.class);
        startActivity(intent);
        finish();
    }

    private void startParentDashboardActivity(){
        Intent intent = new Intent(this, ParentDashboardActivity.class);
        startActivity(intent);
        finish();
    }
}
