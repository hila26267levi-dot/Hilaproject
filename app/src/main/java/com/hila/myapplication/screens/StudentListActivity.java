package com.hila.myapplication.screens;

import android.content.Intent;
import android.os.Bundle;
import android.util.Log;

import androidx.activity.EdgeToEdge;
import androidx.appcompat.app.AlertDialog;
import androidx.appcompat.app.AppCompatActivity;
import androidx.core.graphics.Insets;
import androidx.core.view.ViewCompat;
import androidx.core.view.WindowInsetsCompat;
import androidx.recyclerview.widget.LinearLayoutManager;
import androidx.recyclerview.widget.RecyclerView;

import android.widget.TextView;
import android.widget.Toast;

import com.hila.myapplication.R;
import com.hila.myapplication.adapters.StudentAdapter;
import com.hila.myapplication.model.Student;
import com.hila.myapplication.servicses.DatabaseService;

import java.util.List;

public class StudentListActivity extends AppCompatActivity {

    boolean isAdmin = false;
    private static final String TAG = "StudentListActivity";
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

        databaseService = DatabaseService.getInstance();
        isAdmin = getIntent().getBooleanExtra("isAdmin", false);

        rcStudentList = findViewById(R.id.rcStudentList);
        tvUserCount = findViewById(R.id.tv_student_count);
        rcStudentList.setLayoutManager(new LinearLayoutManager(this));

        studentAdapter = new StudentAdapter(new StudentAdapter.OnStudentClickListener() {
            @Override
            public void onStudentClick(Student student) {
                Intent go = new Intent(StudentListActivity.this, StudentProfile.class);
                go.putExtra("studentId", student.getId());
                startActivity(go);
            }

            @Override
            public void onLongStudentClick(Student student) {
                if (!isAdmin) return;

                new AlertDialog.Builder(StudentListActivity.this)
                        .setTitle("מחיקת תלמיד")
                        .setMessage("האם אתה בטוח שברצונך למחוק את התלמיד "
                                + student.getFname() + " " + student.getLname() + "?")
                        .setPositiveButton("כן, מחק", (dialog, which) -> {
                            databaseService.deleteStudent(student.getId(),
                                    new DatabaseService.DatabaseCallback<Void>() {
                                        @Override
                                        public void onCompleted(Void object) {
                                            studentAdapter.removeStudent(student);
                                            int newCount = studentAdapter.getItemCount();
                                            tvUserCount.setText("סה\"כ תלמידים: " + newCount);
                                            Toast.makeText(StudentListActivity.this,
                                                    "התלמיד " + student.getFname() + " נמחק בהצלחה",
                                                    Toast.LENGTH_SHORT).show();
                                        }

                                        @Override
                                        public void onFailed(Exception e) {
                                            Log.e(TAG, "Failed to delete student", e);
                                            Toast.makeText(StudentListActivity.this,
                                                    "שגיאה במחיקת התלמיד, נסה שוב",
                                                    Toast.LENGTH_SHORT).show();
                                        }
                                    });
                        })
                        .setNegativeButton("ביטול", (dialog, which) -> dialog.dismiss())
                        .show();
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
                tvUserCount.setText("סה\"כ תלמידים: " + students.size());
            }

            @Override
            public void onFailed(Exception e) {
                Log.e(TAG, "Failed to get students list", e);
                Toast.makeText(StudentListActivity.this,
                        "שגיאה בטעינת רשימת התלמידים", Toast.LENGTH_SHORT).show();
            }
        });
    }
}