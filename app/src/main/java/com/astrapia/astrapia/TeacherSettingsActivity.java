package com.astrapia.astrapia;

import androidx.annotation.NonNull;
import androidx.appcompat.app.AppCompatActivity;

import android.os.Bundle;
import android.view.MenuItem;
import android.view.View;
import android.widget.EditText;
import android.widget.ProgressBar;
import android.widget.Toast;

import com.google.android.gms.tasks.OnCompleteListener;
import com.google.android.gms.tasks.Task;
import com.google.firebase.auth.FirebaseAuth;
import com.google.firebase.firestore.DocumentSnapshot;
import com.google.firebase.firestore.FirebaseFirestore;

import java.util.HashMap;

public class TeacherSettingsActivity extends AppCompatActivity implements View.OnClickListener {

    private FirebaseAuth mAuth;
    private FirebaseFirestore db;
    private ProgressBar pbTS;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_teacher_settings);
        getSupportActionBar().setDisplayHomeAsUpEnabled(true);
        setTitle("Settings");

        mAuth = FirebaseAuth.getInstance();
        db = FirebaseFirestore.getInstance();

        findViewById(R.id.btnTSSave).setEnabled(false);
        pbTS = findViewById(R.id.pbTS);

        db.collection("teachers")
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

                            EditText etTSFirstName = findViewById(R.id.etTSFirstName);
                            EditText etTSMiddleName = findViewById(R.id.etTSMiddleName);
                            EditText etTSLastName = findViewById(R.id.etTSLastName);

                            etTSFirstName.setText(firstName);
                            etTSMiddleName.setText(middleName);
                            etTSLastName.setText(lastName);
                        }

                        pbTS.setVisibility(View.GONE);
                        findViewById(R.id.btnTSSave).setEnabled(true);

                        populateButtons();
                    }
                });
    }

    private void populateButtons(){
        findViewById(R.id.btnTSSave).setOnClickListener(this);
    }

    @Override
    public boolean onOptionsItemSelected(@NonNull MenuItem item) {
        if(item.getItemId() == android.R.id.home){
            onBackPressed();
        }

        return super.onOptionsItemSelected(item);
    }

    @Override
    public void onClick(View v) {

        pbTS.setVisibility(View.VISIBLE);
        findViewById(R.id.btnTSSave).setEnabled(false);

        if(v.getId() == R.id.btnTSSave){
            EditText etTSFirstName = findViewById(R.id.etTSFirstName);
            EditText etTSMiddleName = findViewById(R.id.etTSMiddleName);
            EditText etTSLastName = findViewById(R.id.etTSLastName);

            HashMap<String, Object> hashMap = new HashMap<>();
            hashMap.put("first_name", etTSFirstName.getText().toString());
            hashMap.put("middle_name", etTSMiddleName.getText().toString());
            hashMap.put("last_name", etTSLastName.getText().toString());

            db.collection("teachers")
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
    }
}
