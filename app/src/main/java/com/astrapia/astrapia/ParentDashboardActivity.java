package com.astrapia.astrapia;

import androidx.annotation.NonNull;
import androidx.appcompat.app.AppCompatActivity;
import androidx.recyclerview.widget.LinearLayoutManager;
import androidx.recyclerview.widget.RecyclerView;

import android.content.Intent;
import android.os.Bundle;
import android.view.Menu;
import android.view.MenuItem;
import android.view.View;
import android.widget.ProgressBar;

import com.astrapia.astrapia.adapters.StudentsAdapter;
import com.astrapia.astrapia.models.Student;
import com.google.android.gms.tasks.OnCompleteListener;
import com.google.android.gms.tasks.Task;
import com.google.firebase.auth.FirebaseAuth;
import com.google.firebase.firestore.FirebaseFirestore;
import com.google.firebase.firestore.QueryDocumentSnapshot;
import com.google.firebase.firestore.QuerySnapshot;

import java.util.ArrayList;
import java.util.List;

public class ParentDashboardActivity extends AppCompatActivity {

    private FirebaseAuth mAuth;
    private FirebaseFirestore db;

    private List<Student> students;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_parent_dashboard);
        setTitle("Students");

        mAuth = FirebaseAuth.getInstance();
        db = FirebaseFirestore.getInstance();
    }

    @Override
    protected void onResume() {
        loadStudents();
        super.onResume();
    }

    private void loadStudents(){
        students = new ArrayList<>();

        db.collection("students")
                .whereEqualTo("parent_id", mAuth.getCurrentUser().getUid())
                .get()
                .addOnCompleteListener(new OnCompleteListener<QuerySnapshot>() {
                    @Override
                    public void onComplete(@NonNull Task<QuerySnapshot> task) {
                        populateStudents(task);
                    }
                });
    }

    private void populateStudents(Task<QuerySnapshot> task){
        for(QueryDocumentSnapshot document : task.getResult()){

            String id = document.getId();
            String firstName = document.getString("first_name");
            String middleName = document.getString("middle_name");
            String lastName = document.getString("last_name");
            String schoolId = document.getString("school_id");
            String parentId = document.getString("parent_id");

            Student student = new Student(id,firstName, middleName, lastName, schoolId, parentId);
            students.add(student);
        }

        RecyclerView rvStudents = findViewById(R.id.rv_students);

        StudentsAdapter adapter = new StudentsAdapter(students, this);
        rvStudents.setAdapter(adapter);
        rvStudents.setLayoutManager(new LinearLayoutManager(this));

        ProgressBar pbPD = findViewById(R.id.pbPD);
        pbPD.setVisibility(View.GONE);
    }

    @Override
    public boolean onCreateOptionsMenu(Menu menu) {
        getMenuInflater().inflate(R.menu.user_dashboard_menu, menu);
        return super.onCreateOptionsMenu(menu);
    }

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
            intent = new Intent(this, ParentSettingsActivity.class);
            startActivity(intent);
        }

        return super.onOptionsItemSelected(item);
    }
}
