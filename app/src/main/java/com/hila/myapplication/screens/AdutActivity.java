package com.hila.myapplication.screens;

import android.content.Intent;
import android.os.Bundle;
import android.view.Menu;
import android.view.MenuItem;

import androidx.activity.EdgeToEdge;
import androidx.appcompat.app.AppCompatActivity;
import androidx.core.graphics.Insets;
import androidx.core.view.ViewCompat;
import androidx.core.view.WindowInsetsCompat;

import com.hila.myapplication.R;

public class AdutActivity extends AppCompatActivity {

    private String userType = "student"; // ברירת מחדל

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        EdgeToEdge.enable(this);
        setContentView(R.layout.activity_adut);
        ViewCompat.setOnApplyWindowInsetsListener(findViewById(R.id.main), (v, insets) -> {
            Insets systemBars = insets.getInsets(WindowInsetsCompat.Type.systemBars());
            v.setPadding(systemBars.left, systemBars.top, systemBars.right, systemBars.bottom);
            return insets;
        });

        // קבל את סוג המשתמש מה-Intent
        String type = getIntent().getStringExtra("userType");
        if (type != null) {
            userType = type;
        }
    }

    @Override
    public boolean onCreateOptionsMenu(Menu menu) {
        if ("teacher".equals(userType)) {
            getMenuInflater().inflate(R.menu.teacher_menu, menu);
        } else if ("admin".equals(userType)) {
            getMenuInflater().inflate(R.menu.admin_menu, menu);
        } else {
            // student
            getMenuInflater().inflate(R.menu.student_menu, menu);
        }
        return true;
    }

    @Override
    public boolean onOptionsItemSelected(MenuItem item) {
        int id = item.getItemId();

        // ---- תפריט תלמיד ----
        if (id == R.id.student_home) {
            startActivity(new Intent(AdutActivity.this, StudentActivity.class));
            return true;
        }
        if (id == R.id.student_searchteacher) {
            startActivity(new Intent(AdutActivity.this, TeacherListActivity.class));
            return true;
        }
        if (id == R.id.student_profile) {
            startActivity(new Intent(AdutActivity.this, StudentProfile.class));
            return true;
        }
        if (id == R.id.student_disconect) {
            startActivity(new Intent(AdutActivity.this, disconect_forstudent.class));
            return true;
        }
        if (id == R.id.student_mylesson) {
            startActivity(new Intent(AdutActivity.this, student_lesson_list.class));
            return true;
        }
        if (id == R.id.student_adut) {
            return true; // כבר כאן
        }

        // ---- תפריט מורה ----
        if (id == R.id.teacher_home) {
            startActivity(new Intent(AdutActivity.this, TeacherActivity.class));
            return true;
        }
        if (id == R.id.teacher_profile) {
            startActivity(new Intent(AdutActivity.this, teacher_profile.class));
            return true;
        }
        if (id == R.id.teacher_mylesson) {
            startActivity(new Intent(AdutActivity.this, TeacherLessonsList.class));
            return true;
        }
        if (id == R.id.teacher_disconect) {
            startActivity(new Intent(AdutActivity.this, disconect_forteacher.class));
            return true;
        }
        if (id == R.id.teacher_adut) {
            return true; // כבר כאן
        }

        // ---- תפריט מנהל ----
        if (id == R.id.admin) {
            startActivity(new Intent(AdutActivity.this, AdminActivity.class));
            return true;
        }
        if (id == R.id.admin_adut) {
            return true; // כבר כאן
        }
        if (id == R.id.admin_disconect) {
            startActivity(new Intent(AdutActivity.this, disconect_foradmin.class));
            return true;
        }

        return super.onOptionsItemSelected(item);
    }
}