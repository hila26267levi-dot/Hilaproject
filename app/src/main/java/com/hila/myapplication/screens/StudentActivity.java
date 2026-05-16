package com.hila.myapplication.screens;

import android.content.Intent;
import android.os.Bundle;
import android.view.Menu;
import android.view.MenuItem;
import android.view.View;
import android.widget.Button;

import androidx.activity.EdgeToEdge;
import androidx.appcompat.app.AppCompatActivity;

import com.google.firebase.auth.FirebaseAuth;
import com.hila.myapplication.R;

public class StudentActivity extends AppCompatActivity implements View.OnClickListener {

    Button btn_Studentpage_mylesson, btn_studentpage_myprofil, btn_studentpage_searchteacher;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        EdgeToEdge.enable(this);
        setContentView(R.layout.activity_student);
        btn_Studentpage_mylesson = findViewById(R.id.btn_studentpage_mylesson);
        btn_studentpage_searchteacher = findViewById(R.id.btn_studentpage_searchteacher);
        btn_studentpage_myprofil = findViewById(R.id.btn_studentpage_myprofil);
        btn_Studentpage_mylesson.setOnClickListener(this);
        btn_studentpage_myprofil.setOnClickListener(this);
        btn_studentpage_searchteacher.setOnClickListener(this);
    }

    @Override
    public void onClick(View v) {
        if (v == btn_Studentpage_mylesson) {
            startActivity(new Intent(StudentActivity.this, student_lesson_list.class));
        }
        if (v == btn_studentpage_searchteacher) {
            startActivity(new Intent(StudentActivity.this, TeacherListActivity.class));
        }
        if (v == btn_studentpage_myprofil) {
            startActivity(new Intent(StudentActivity.this, StudentProfile.class));
        }
    }

    @Override
    public boolean onCreateOptionsMenu(Menu menu) {
        getMenuInflater().inflate(R.menu.student_menu, menu);
        return true;
    }

    @Override
    public boolean onOptionsItemSelected(MenuItem item) {
        int id = item.getItemId();
        if (id == R.id.student_home) {
            startActivity(new Intent(StudentActivity.this, StudentActivity.class));
            return true;
        }
        if (id == R.id.student_searchteacher) {
            startActivity(new Intent(StudentActivity.this, TeacherListActivity.class));
            return true;
        }
        if (id == R.id.student_profile) {
            startActivity(new Intent(StudentActivity.this, StudentProfile.class));
            return true;
        }
        if (id == R.id.student_disconect) {
            FirebaseAuth.getInstance().signOut();
            Intent go = new Intent(StudentActivity.this,
                    MainActivity.class);
            startActivity(go);
            finish();
        }
        if (id == R.id.student_mylesson) {
            startActivity(new Intent(StudentActivity.this, student_lesson_list.class));
            return true;
        }
        if (id == R.id.student_adut) {
            Intent intent = new Intent(StudentActivity.this, AdutActivity.class);
            intent.putExtra("userType", "student");
            startActivity(intent);
            return true;
        }
        return super.onOptionsItemSelected(item);
    }
}