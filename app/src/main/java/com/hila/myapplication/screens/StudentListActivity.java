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

            // לחיצה קצרה — לא עושה כלום
            @Override
            public void onStudentClick(Student student) {
                // intentionally empty
            }

            // לחיצה ארוכה — מנהל בלבד: אישור מחיקה + SMS
            @Override
            public void onLongStudentClick(Student student) {
                if (!isAdmin) return;

                AlertDialog.Builder builder =
                        new AlertDialog.Builder(StudentListActivity.this);
                builder.setTitle("מחיקת תלמיד");
                builder.setMessage("האם אתה בטוח שברצונך למחוק את התלמיד "
                        + student.getFname() + " " + student.getLname() + "?");

                builder.setPositiveButton("כן, מחק",
                        new DialogInterface.OnClickListener() {
                            @Override
                            public void onClick(DialogInterface dialog, int which) {
                                databaseService.deleteStudent(student.getId(),
                                        new DatabaseService.DatabaseCallback<Void>() {
                                            @Override
                                            public void onCompleted(Void object) {
                                                studentAdapter.removeStudent(student);
                                                Toast.makeText(StudentListActivity.this,
                                                        "התלמיד נמחק בהצלחה",
                                                        Toast.LENGTH_SHORT).show();
                                                // שליחת SMS לתלמיד שנמחק
                                                sendSmsToStudent(student);
                                            }

                                            @Override
                                            public void onFailed(Exception e) {
                                                Toast.makeText(StudentListActivity.this,
                                                        "שגיאה במחיקה",
                                                        Toast.LENGTH_SHORT).show();
                                            }
                                        });
                            }
                        });

                builder.setNegativeButton("ביטול",
                        new DialogInterface.OnClickListener() {
                            @Override
                            public void onClick(DialogInterface dialog, int which) {
                                dialog.dismiss();
                            }
                        });

                builder.show();
            }
        });

        rcStudentList.setAdapter(studentAdapter);
    }

    // שליחת SMS לתלמיד שנמחק
    private void sendSmsToStudent(Student student) {
        if (student == null || student.getPhone() == null
                || student.getPhone().isEmpty()) return;

        String message =
                "שלום " + student.getFname() + " " + student.getLname() + ",\n"
                        + "לידיעתך, חשבונך הוסר מהאפליקציה על ידי מנהל המערכת.\n"
                        + "לפרטים נוספים פנה למנהל.\n"
                        + "בברכה";

        Intent smsIntent = new Intent(Intent.ACTION_SENDTO);
        smsIntent.setData(Uri.parse("smsto:" + student.getPhone()));
        smsIntent.putExtra("sms_body", message);
        startActivity(smsIntent);
    }

    @Override
    protected void onResume() {
        super.onResume();
        databaseService.getStudentList(new DatabaseService.DatabaseCallback<List<Student>>() {
            @Override
            public void onCompleted(List<Student> students) {
                studentAdapter.setStudentList(students);
                tvUserCount.setText("סך הכל תלמידים: " + students.size());
            }

            @Override
            public void onFailed(Exception e) {
                Log.e(TAG, "Failed to get student list", e);
            }
        });
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
            startActivity(new Intent(StudentListActivity.this, AdminActivity.class));
            return true;
        }
        if (id == R.id.admin_adut) {
            Intent intent = new Intent(StudentListActivity.this, AdutActivity.class);
            intent.putExtra("userType", "admin");
            startActivity(intent);
            return true;
        }
        if (id == R.id.admin_disconect) {
            startActivity(new Intent(StudentListActivity.this, disconect_foradmin.class));
            return true;
        }
        return super.onOptionsItemSelected(item);
    }
}