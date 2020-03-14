package com.astrapia.astrapia;

import androidx.annotation.NonNull;
import androidx.appcompat.app.AppCompatActivity;

import android.os.Bundle;
import android.view.MenuItem;
import android.view.View;
import android.widget.RadioButton;

public class RegistrationActivity extends AppCompatActivity {

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_registration);
        getSupportActionBar().setDisplayHomeAsUpEnabled(true);
        setTitle("Registration");
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

    @Override
    public boolean onOptionsItemSelected(@NonNull MenuItem item) {
        if(item.getItemId() == android.R.id.home){
            onBackPressed();
        }

        return super.onOptionsItemSelected(item);
    }
}
