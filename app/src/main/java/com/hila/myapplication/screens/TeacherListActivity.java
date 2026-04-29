package com.hila.myapplication.screens;

import android.content.DialogInterface;
import android.content.Intent;
import android.os.Bundle;
import android.util.Log;
import android.view.Menu;
import android.view.MenuItem;
import android.widget.TextView;
import android.widget.Toast;

import androidx.activity.EdgeToEdge;
import androidx.appcompat.app.AlertDialog;
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

    boolean isAdmin = false;
    private static final String TAG = "TeacherListActivity";
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

        databaseService = DatabaseService.getInstance();
        isAdmin = getIntent().getBooleanExtra("isAdmin", false);

        rcTeacherList = findViewById(R.id.rcTeacherList);
        tvUserCount = findViewById(R.id.tv_teacher_count);
        rcTeacherList.setLayoutManager(new LinearLayoutManager(this));

        teacherAdapter = new TeacherAdapter(new TeacherAdapter.OnTeacherClickListener() {
            @Override
            public void onTeacherClick(Teacher teacher) {
                Intent go = new Intent(TeacherListActivity.this, TeacherProfile_forstudent.class);
                go.putExtra("teacherId", teacher.getId());
                startActivity(go);
            }
//מחיקה של מורה על ידי המנהל
            @Override
            public void onLongTeacherClick(Teacher teacher) {
                if (!isAdmin) return;

                new AlertDialog.Builder(TeacherListActivity.this)
                        .setTitle("מחיקת מורה")
                        .setMessage("האם אתה בטוח שברצונך למחוק את המורה "
                                + teacher.getFname() + " " + teacher.getLname() + "?")
                        .setPositiveButton("כן, מחק", (dialog, which) -> {
                            databaseService.deleteTeacher(teacher.getId(),
                                    new DatabaseService.DatabaseCallback<Void>() {
                                        @Override
                                        public void onCompleted(Void object) {
                                            teacherAdapter.removeTeacher(teacher);
                                            int newCount = teacherAdapter.getItemCount();
                                            tvUserCount.setText("סה\"כ מורים: " + newCount);
                                            Toast.makeText(TeacherListActivity.this,
                                                    "המורה " + teacher.getFname() + " נמחק בהצלחה",
                                                    Toast.LENGTH_SHORT).show();
                                        }

                                        @Override
                                        public void onFailed(Exception e) {
                                            Log.e(TAG, "Failed to delete teacher", e);
                                            Toast.makeText(TeacherListActivity.this,
                                                    "שגיאה במחיקת המורה, נסה שוב",
                                                    Toast.LENGTH_SHORT).show();
                                        }
                                    });
                        })
                        .setNegativeButton("ביטול", (dialog, which) -> dialog.dismiss())
                        .show();
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
                tvUserCount.setText("סה\"כ מורים: " + teachers.size());
            }

            @Override
            public void onFailed(Exception e) {
                Log.e(TAG, "Failed to get teacher list", e);
                Toast.makeText(TeacherListActivity.this,
                        "שגיאה בטעינת רשימת המורים", Toast.LENGTH_SHORT).show();
            }
        });
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
            startActivity(new Intent(this, StudentActivity.class));
            return true;
        }
        if (id == R.id.student_searchteacher) {
            startActivity(new Intent(this, TeacherListActivity.class));
            return true;
        }
        if (id == R.id.student_profile) {
            startActivity(new Intent(this, StudentProfile.class));
            return true;
        }
        if (id == R.id.student_disconect) {
            startActivity(new Intent(this, disconect_forstudent.class));
            return true;
        }
        if (id == R.id.student_mylesson) {
            startActivity(new Intent(this, student_lesson_list.class));
            return true;
        }
        return super.onOptionsItemSelected(item);
    }
}