package com.hila.myapplication.screens;


import android.content.Intent;
import android.os.Bundle;
import android.util.Log;

import androidx.activity.EdgeToEdge;
import androidx.appcompat.app.AppCompatActivity;
import androidx.core.graphics.Insets;
import androidx.core.view.ViewCompat;
import androidx.core.view.WindowInsetsCompat;
import androidx.recyclerview.widget.LinearLayoutManager;
import androidx.recyclerview.widget.RecyclerView;

import android.widget.TextView;


import com.hila.myapplication.R;
import com.hila.myapplication.adapters.StudentAdapter;
import com.hila.myapplication.model.Student;
import com.hila.myapplication.servicses.DatabaseService;

import java.util.List;

public class StudentListActivity extends AppCompatActivity {

    private static final String TAG = "UsersListActivity";
    private StudentAdapter studentAdapter;
    private TextView tvUserCount;
    private RecyclerView rcStudentList;
    private DatabaseService databaseService;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        EdgeToEdge.enable(this);
        setContentView(R.layout.activity_student_list);
        ViewCompat.setOnApplyWindowInsetsListener(findViewById(R.id.main), (v, insets) -> {
            Insets systemBars = insets.getInsets(WindowInsetsCompat.Type.systemBars());
            v.setPadding(systemBars.left, systemBars.top, systemBars.right, systemBars.bottom);
            return insets;
        });

        databaseService=DatabaseService.getInstance();

        rcStudentList = findViewById(R.id.rcStudentList);
         tvUserCount = findViewById(R.id.tv_student_count);
        rcStudentList.setLayoutManager(new LinearLayoutManager(this));
        studentAdapter = new StudentAdapter(new StudentAdapter.OnStudentClickListener() {
            @Override
            public void onStudentClick(Student student) {

                Intent go=new Intent(StudentListActivity.this, StudentProfile.class);
                go.putExtra("studentId", student.getId());
                startActivity(go);


            }

            @Override
            public void onLongStudentClick(Student student) {

            }


        });

        rcStudentList.setAdapter(studentAdapter);
    }


    @Override
    protected void onResume() {
        super.onResume();
        databaseService.getStudentList(new DatabaseService.DatabaseCallback<List<Student>>() {
            @Override
            public void onCompleted(List<Student> students) {
                studentAdapter.setStudentList(students);
                tvUserCount.setText("Total users: " + students.size());
            }

            @Override
            public void onFailed(Exception e) {
                Log.e(TAG, "Failed to get users list", e);
            }
        });
    }

}