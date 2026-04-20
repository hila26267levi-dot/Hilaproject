package com.hila.myapplication.screens;

import android.content.DialogInterface;
import android.content.Intent;
import android.os.Bundle;
import android.view.View;
import android.widget.Button;
import android.widget.Toast;

import androidx.activity.EdgeToEdge;
import androidx.appcompat.app.AlertDialog;
import androidx.appcompat.app.AppCompatActivity;
import androidx.core.graphics.Insets;
import androidx.core.view.ViewCompat;
import androidx.core.view.WindowInsetsCompat;

import com.hila.myapplication.R;
import com.hila.myapplication.adapters.StudentAdapter;
import com.hila.myapplication.adapters.TeacherAdapter;
import com.hila.myapplication.model.Student;
import com.hila.myapplication.model.Teacher;
import com.hila.myapplication.servicses.DatabaseService;

import androidx.recyclerview.widget.LinearLayoutManager;
import androidx.recyclerview.widget.RecyclerView;

import java.util.List;

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
            // מעבר לרשימת מורים עם אפשרות מחיקה
            openTeacherListForAdmin();
        }
        if (v == btn_maneger_student) {
            // מעבר לרשימת תלמידים עם אפשרות מחיקה
            openStudentListForAdmin();
        }
    }

    private void openTeacherListForAdmin() {
        Intent intent = new Intent(AdminActivity.this, TeacherListActivity.class);
        intent.putExtra("isAdmin", true); // מסמן שזה מנהל
        startActivity(intent);
    }

    private void openStudentListForAdmin() {
        Intent intent = new Intent(AdminActivity.this, StudentListActivity.class);
        intent.putExtra("isAdmin", true); // מסמן שזה מנהל
        startActivity(intent);
    }
}