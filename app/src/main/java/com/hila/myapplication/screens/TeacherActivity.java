package com.hila.myapplication.screens;

import android.content.Intent;
import android.os.Bundle;
import android.view.Menu;
import android.view.MenuItem;
import android.view.View;
import android.widget.Button;

import androidx.activity.EdgeToEdge;
import androidx.appcompat.app.AppCompatActivity;

import com.hila.myapplication.R;
import com.hila.myapplication.servicses.DatabaseService;

public class TeacherActivity extends AppCompatActivity {
Button btn_mylessson, btn_addlesoon , btn_myprofile ;
    private DatabaseService databaseService;
    private View v;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        EdgeToEdge.enable(this);
        setContentView(R.layout.activity_teacher);
        btn_addlesoon = findViewById(R.id.btn_teacher_addlesoon);




    }

    public void goAddLesson(View v) {

            Intent intent = new Intent(TeacherActivity.this, Addnewlesson.class);
            startActivity(intent);
    }

    public void goMyLesson(View v) {

        Intent intent = new Intent(TeacherActivity.this, TeacherLessonsList.class);
        startActivity(intent);
    }
    public void goMyProfile(View v) {

        Intent intent = new Intent(TeacherActivity.this, TeacherProfile_forstudent.class);
        startActivity(intent);
    }
    //   של מורה תפריט צד
    @Override
    public boolean onCreateOptionsMenu(Menu menu) {
        getMenuInflater().inflate(R.menu.main_menu, menu);
        return true;
    }

    @Override
    public boolean onOptionsItemSelected(MenuItem item) {

        int id = item.getItemId();

        if (id == R.id.teacher_home) {
            Intent intent = new Intent(TeacherActivity.this,TeacherActivity.class);
            startActivity(intent);

            return true;
        }

        if (id == R.id.teacher_profile) {
            Intent intent = new Intent(TeacherActivity.this, teacher_profile.class);
            startActivity(intent);

            return true;
        }

        if (id == R.id.teacher_mylesson) {
            Intent intent = new Intent(TeacherActivity.this, TeacherLessonsList.class);
            startActivity(intent);
            return true;
        }
        if (id == R.id.teacher_disconect) {
            Intent intent = new Intent(TeacherActivity.this, disconect_forteacher.class);
            startActivity(intent);
            return true;
        }
        if (id == R.id.teacher_adut) {
            Intent intent = new Intent(TeacherActivity.this, AdutActivity.class);
            startActivity(intent);
            return true;
        }
        return super.onOptionsItemSelected(item);
    }

}