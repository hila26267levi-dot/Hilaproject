package com.hila.myapplication.screens;

import android.content.DialogInterface;
import android.content.Intent;
import android.net.Uri;
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

            @Override
            public void onLongTeacherClick(Teacher teacher) {
                if (isAdmin) {
                    AlertDialog.Builder builder = new AlertDialog.Builder(TeacherListActivity.this);
                    builder.setTitle("מחיקת מורה");
                    builder.setMessage("האם אתה בטוח שברצונך למחוק את המורה "
                            + teacher.getFname() + " " + teacher.getLname() + "?");

                    builder.setPositiveButton("כן, מחק", new DialogInterface.OnClickListener() {
                        @Override
                        public void onClick(DialogInterface dialog, int which) {
                            databaseService.deleteTeacher(teacher.getId(),
                                    new DatabaseService.DatabaseCallback<Void>() {
                                        @Override
                                        public void onCompleted(Void object) {
                                            teacherAdapter.removeTeacher(teacher);
                                            Toast.makeText(TeacherListActivity.this,
                                                    "המורה נמחק בהצלחה",
                                                    Toast.LENGTH_SHORT).show();
                                            sendSmsToDeletedUser(teacher.getPhone(), teacher.getFname());
                                        }

                                        @Override
                                        public void onFailed(Exception e) {
                                            Toast.makeText(TeacherListActivity.this,
                                                    "שגיאה במחיקה",
                                                    Toast.LENGTH_SHORT).show();
                                        }
                                    });
                        }
                    });

                    builder.setNegativeButton("ביטול", new DialogInterface.OnClickListener() {
                        @Override
                        public void onClick(DialogInterface dialog, int which) {
                            dialog.dismiss();
                        }
                    });

                    builder.show();
                }
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
                Log.e(TAG, "Failed to get teachers list", e);
            }
        });
    }

    private void sendSmsToDeletedUser(String phone, String fname) {
        if (phone == null || phone.isEmpty()) return;
        String message = "שלום " + fname + ",\n"
                + "לידיעתך, חשבונך באפליקציה נמחק על ידי מנהל המערכת.\n"
                + "אינך רשאי/ת להמשיך להשתמש באפליקציה.\n"
                + "בברכה, צוות האפליקציה";
        Intent smsIntent = new Intent(Intent.ACTION_SENDTO);
        smsIntent.setData(Uri.parse("smsto:" + phone));
        smsIntent.putExtra("sms_body", message);
        startActivity(smsIntent);
    }

    // תפריט צד — מנהל או תלמיד לפי isAdmin
    @Override
    public boolean onCreateOptionsMenu(Menu menu) {
        if (isAdmin) {
            getMenuInflater().inflate(R.menu.admin_menu, menu);
        } else {
            getMenuInflater().inflate(R.menu.student_menu, menu);
        }
        return true;
    }

    @Override
    public boolean onOptionsItemSelected(MenuItem item) {
        int id = item.getItemId();

        // תפריט מנהל
        if (id == R.id.admin) {
            Intent intent = new Intent(TeacherListActivity.this, AdminActivity.class);
            startActivity(intent);
            return true;
        }
        if (id == R.id.admin_adut) {
            Intent intent = new Intent(TeacherListActivity.this, AdutActivity.class);
            intent.putExtra("isAdmin", true);
            startActivity(intent);
            return true;
        }

        if (id == R.id.admin_disconect) {
            Intent intent = new Intent(TeacherListActivity.this, disconect_foradmin.class);
            startActivity(intent);
            return true;
        }

        // תפריט תלמיד
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
            Intent intent = new Intent(TeacherListActivity.this, StudentProfile.class);
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
        if (id == R.id.student_adut) {
            Intent intent = new Intent(TeacherListActivity.this, AdutActivity.class);
            startActivity(intent);
            return true;
        }

        return super.onOptionsItemSelected(item);
    }
}