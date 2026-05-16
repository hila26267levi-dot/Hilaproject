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
import com.hila.myapplication.servicses.DatabaseService;

public class TeacherActivity extends AppCompatActivity {

    Button btn_addlesoon;
    private DatabaseService databaseService;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        EdgeToEdge.enable(this);
        setContentView(R.layout.activity_teacher);
        btn_addlesoon = findViewById(R.id.btn_teacher_addlesoon);
    }

    public void goAddLesson(View v) {
        startActivity(new Intent(TeacherActivity.this, Addnewlesson.class));
    }

    public void goMyLesson(View v) {
        startActivity(new Intent(TeacherActivity.this, TeacherLessonsList.class));
    }

    public void goMyProfile(View v) {
        startActivity(new Intent(TeacherActivity.this, teacher_profile.class));
    }

    @Override
    public boolean onCreateOptionsMenu(Menu menu) {
        getMenuInflater().inflate(R.menu.teacher_menu, menu);
        return true;
    }

    @Override
    public boolean onOptionsItemSelected(MenuItem item) {
        int id = item.getItemId();
        if (id == R.id.teacher_home) {
            startActivity(new Intent(TeacherActivity.this, TeacherActivity.class));
            return true;
        }
        if (id == R.id.teacher_profile) {
            startActivity(new Intent(TeacherActivity.this, teacher_profile.class));
            return true;
        }
        if (id == R.id.teacher_mylesson) {
            startActivity(new Intent(TeacherActivity.this, TeacherLessonsList.class));
            return true;
        }
        if (id == R.id.teacher_disconect) {
            FirebaseAuth.getInstance().signOut();
            Intent go = new Intent(TeacherActivity.this,
                    MainActivity.class);
            startActivity(go);
            finish();
        }
        if (id == R.id.teacher_adut) {
            Intent intent = new Intent(TeacherActivity.this, AdutActivity.class);
            intent.putExtra("userType", "teacher");
            startActivity(intent);
            return true;
        }
        return super.onOptionsItemSelected(item);
    }
}