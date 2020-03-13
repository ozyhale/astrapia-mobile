package com.astrapia.astrapia;

import android.content.Intent;
import android.os.Bundle;
import android.widget.Toast;

import androidx.annotation.NonNull;
import androidx.appcompat.app.AppCompatActivity;

import com.google.android.gms.tasks.OnCompleteListener;
import com.google.android.gms.tasks.Task;
import com.google.firebase.auth.FirebaseAuth;
import com.google.firebase.auth.FirebaseUser;
import com.google.firebase.firestore.DocumentSnapshot;
import com.google.firebase.firestore.FirebaseFirestore;

public class MainActivity extends AppCompatActivity{

    private FirebaseAuth mAuth;
    private FirebaseFirestore db;

    private int isTeacher = -1;
    private int isParent = -1;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_main);
        mAuth = FirebaseAuth.getInstance();
        db = FirebaseFirestore.getInstance();
    }

    @Override
    public void onStart() {
        super.onStart();

        FirebaseUser currentUser = mAuth.getCurrentUser();

        Intent intent;

        if(currentUser == null){
            intent = new Intent(this, LoginActivity.class);
            startActivity(intent);
            finish();
        }else{

            db.collection("teachers")
                    .document(mAuth.getCurrentUser().getUid())
                    .get()
                    .addOnCompleteListener(new OnCompleteListener<DocumentSnapshot>() {
                        @Override
                        public void onComplete(@NonNull Task<DocumentSnapshot> task) {
                            if(task.isSuccessful()){
                                isTeacher = task.getResult().exists() ? 1 : 0;
                                startActivity();
                            }
                        }
                    });

            db.collection("parents")
                    .document(mAuth.getCurrentUser().getUid())
                    .get()
                    .addOnCompleteListener(new OnCompleteListener<DocumentSnapshot>() {
                        @Override
                        public void onComplete(@NonNull Task<DocumentSnapshot> task) {
                            if(task.isSuccessful()){
                                isParent = task.getResult().exists() ? 1 : 0;
                                startActivity();
                            }
                        }
                    });
        }

    }

    public void startActivity() {
        if(isTeacher != -1 && isParent != -1){
            Intent intent = null;

            if(isTeacher == 1){
                intent = new Intent(this, TeacherDashboardActivity.class);
            }else if(isParent == 1){
                intent = new Intent(this, ParentDashboardActivity.class);
            }

            startActivity(intent);
            finish();
        }
    }
}
