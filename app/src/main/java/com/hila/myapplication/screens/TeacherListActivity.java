package com.hila.myapplication.screens;

import android.content.Intent;
import android.os.Bundle;
import android.util.Log;
import android.view.Menu;
import android.view.MenuItem;
import android.widget.TextView;

import androidx.activity.EdgeToEdge;
import androidx.appcompat.app.AppCompatActivity;
import androidx.core.graphics.Insets;
import androidx.core.view.ViewCompat;
import androidx.core.view.WindowInsetsCompat;
import androidx.recyclerview.widget.LinearLayoutManager;
import androidx.recyclerview.widget.RecyclerView;

import com.hila.myapplication.R;
import com.hila.myapplication.adapters.TeacherAdapter;
import com.hila.myapplication.model.Teacher;
import com.hila.myapplication.servicses.DatabaseService;

import java.util.List;

public class TeacherListActivity extends AppCompatActivity {

    private static final String TAG = "UsersListActivity";
    private TeacherAdapter teacherAdapter;
    private TextView tvUserCount;
    private RecyclerView rcTeacherList;
    private DatabaseService databaseService;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        EdgeToEdge.enable(this);
        setContentView(R.layout.activity_teacher_list);
        ViewCompat.setOnApplyWindowInsetsListener(findViewById(R.id.main), (v, insets) -> {
            Insets systemBars = insets.getInsets(WindowInsetsCompat.Type.systemBars());
            v.setPadding(systemBars.left, systemBars.top, systemBars.right, systemBars.bottom);
            return insets;
        });


        databaseService=DatabaseService.getInstance();

        rcTeacherList = findViewById(R.id.rcTeacherList);
        tvUserCount = findViewById(R.id.tv_teacher_count);
        rcTeacherList.setLayoutManager(new LinearLayoutManager(this));
        teacherAdapter = new TeacherAdapter(new TeacherAdapter.OnTeacherClickListener() {
            @Override
            public void onTeacherClick(Teacher teacher) {


                Intent  go=new Intent(TeacherListActivity.this,  TeacherProfile_forstudent.class);
                go.putExtra("teacherId", teacher.getId());
                startActivity(go);


            }

            @Override
            public void onLongTeacherClick(Teacher teacher) {

            }


        });

        rcTeacherList.setAdapter(teacherAdapter);
    }


    @Override
    protected void onResume() {
        super.onResume();
        databaseService.getTeacherList(new DatabaseService.DatabaseCallback<List<Teacher>>() {
            @Override
            public void onCompleted(List<Teacher> teachers) {
                teacherAdapter.setTeacherList(teachers);
                tvUserCount.setText("Total users: " + teachers.size());
            }

            @Override
            public void onFailed(Exception e) {
                Log.e(TAG, "Failed to get users list", e);
            }
        });
    }
    //תפריט צד
    @Override
    public boolean onCreateOptionsMenu(Menu menu) {
        getMenuInflater().inflate(R.menu.student_menu, menu);
        return true;
    }

    @Override
    public boolean onOptionsItemSelected(MenuItem item) {

        int id = item.getItemId();

        if (id == R.id.student_home) {
            Intent intent = new Intent(TeacherListActivity.this, StudentActivity.class);
            startActivity(intent);

            return true;
        }

        if (id == R.id.student_searchteacher) {
            Intent intent = new Intent(TeacherListActivity.this, TeacherListActivity.class);
            startActivity(intent);

            return true;
        }

        if (id == R.id.student_profile) {
            Intent intent = new Intent(TeacherListActivity.this, TeacherListActivity.class);
            startActivity(intent);
            return true;
        }
        if (id == R.id.student_disconect) {
            Intent intent = new Intent(TeacherListActivity.this, disconect_forstudent.class);
            startActivity(intent);
            return true;
        }
        if (id == R.id.student_mylesson) {
            Intent intent = new Intent(TeacherListActivity.this, student_lesson_list.class);
            startActivity(intent);
            return true;
        }
        return super.onOptionsItemSelected(item);
    }


}