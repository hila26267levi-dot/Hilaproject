package com.hila.myapplication.screens;

import android.content.Intent;
import android.os.Bundle;
import android.view.Menu;
import android.view.MenuItem;
import android.view.View;
import android.widget.Button;

import androidx.activity.EdgeToEdge;
import androidx.appcompat.app.AppCompatActivity;
import androidx.core.graphics.Insets;
import androidx.core.view.ViewCompat;
import androidx.core.view.WindowInsetsCompat;

import com.hila.myapplication.R;
import com.hila.myapplication.servicses.DatabaseService;

public class AdminActivity extends AppCompatActivity implements View.OnClickListener {

    Button btn_maneger_teacher, btn_maneger_student;
    private DatabaseService databaseService;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        EdgeToEdge.enable(this);
        setContentView(R.layout.activity_admin);
        ViewCompat.setOnApplyWindowInsetsListener(findViewById(R.id.main), (v, insets) -> {
            Insets systemBars = insets.getInsets(WindowInsetsCompat.Type.systemBars());
            v.setPadding(systemBars.left, systemBars.top, systemBars.right, systemBars.bottom);
            return insets;
        });

        databaseService = DatabaseService.getInstance();

        btn_maneger_teacher = findViewById(R.id.btn_teacher_maneger);
        btn_maneger_student = findViewById(R.id.btn_student_maneger);
        btn_maneger_teacher.setOnClickListener(this);
        btn_maneger_student.setOnClickListener(this);
    }

    @Override
    public void onClick(View v) {
        if (v == btn_maneger_teacher) {
            Intent intent = new Intent(AdminActivity.this, TeacherListActivity.class);
            intent.putExtra("isAdmin", true);
            startActivity(intent);
        }
        if (v == btn_maneger_student) {
            Intent intent = new Intent(AdminActivity.this, StudentListActivity.class);
            intent.putExtra("isAdmin", true);
            startActivity(intent);
        }
    }

    // תפריט מנהל
    @Override
    public boolean onCreateOptionsMenu(Menu menu) {
        getMenuInflater().inflate(R.menu.admin_menu, menu);
        return true;
    }

    @Override
    public boolean onOptionsItemSelected(MenuItem item) {
        int id = item.getItemId();
        if (id == R.id.admin) {
            startActivity(new Intent(AdminActivity.this, AdminActivity.class));
            return true;
        }
        if (id == R.id.admin_adut) {
            Intent intent = new Intent(AdminActivity.this, AdutActivity.class);
            intent.putExtra("userType", "admin");
            startActivity(intent);
            return true;
        }
        if (id == R.id.admin_disconect) {
            startActivity(new Intent(AdminActivity.this, disconect_foradmin.class));
            return true;
        }
        return super.onOptionsItemSelected(item);
    }
}