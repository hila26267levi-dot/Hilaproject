package com.hila.myapplication.screens;

import android.content.Intent;
import android.content.SharedPreferences;
import android.os.Bundle;
import android.view.Menu;
import android.view.MenuItem;
import android.view.View;
import android.widget.Button;
import android.widget.EditText;
import android.widget.Toast;

import androidx.activity.EdgeToEdge;
import androidx.appcompat.app.AppCompatActivity;
import androidx.core.graphics.Insets;
import androidx.core.view.ViewCompat;
import androidx.core.view.WindowInsetsCompat;

import com.hila.myapplication.R;
import com.hila.myapplication.model.Student;
import com.hila.myapplication.model.Teacher;
import com.hila.myapplication.model.User;
import com.hila.myapplication.servicses.DatabaseService;

public class loginActivity extends AppCompatActivity {
    EditText et_email, et_password;
    Button log_btn;
    SharedPreferences sharedPreferences;

    // משתנה שמציין מי לחץ — מורה או תלמיד
    String userType = ""; // "student" או "teacher"

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        EdgeToEdge.enable(this);
        setContentView(R.layout.activity_login);
        ViewCompat.setOnApplyWindowInsetsListener(findViewById(R.id.main), (v, insets) -> {
            Insets systemBars = insets.getInsets(WindowInsetsCompat.Type.systemBars());
            v.setPadding(systemBars.left, systemBars.top, systemBars.right, systemBars.bottom);
            return insets;
        });

        sharedPreferences = getSharedPreferences("myPrefs", MODE_PRIVATE);

        et_email = findViewById(R.id.et_login_email);
        et_password = findViewById(R.id.et_login_password);

        et_email.setText(sharedPreferences.getString("email", ""));
        et_password.setText(sharedPreferences.getString("password", ""));

        // קבלת סוג המשתמש מהIntent
        userType = getIntent().getStringExtra("userType");
        if (userType == null) userType = "";

        log_btn = findViewById(R.id.btn_login);

        log_btn.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                login();
            }
        });
    }

    private void login() {
        String email = et_email.getText().toString();
        String password = et_password.getText().toString();

        if (email.isEmpty()) {
            Toast.makeText(this, "חובה להזין אימייל", Toast.LENGTH_LONG).show();
            return;
        }
        if (password.isEmpty()) {
            Toast.makeText(this, "חובה להזין סיסמה", Toast.LENGTH_LONG).show();
            return;
        }

        DatabaseService.getInstance().LoginUser(email, password,
                new DatabaseService.DatabaseCallback<String>() {
                    @Override
                    public void onCompleted(String uid) {
                        SharedPreferences.Editor editor = sharedPreferences.edit();
                        editor.putString("email", email);
                        editor.putString("password", password);
                        editor.apply();

                        getStudentFromDB(uid);
                    }

                    @Override
                    public void onFailed(Exception e) {
                        Toast.makeText(loginActivity.this,
                                "אימייל או סיסמה שגויים", Toast.LENGTH_SHORT).show();
                    }
                });
    }

    private void getStudentFromDB(String uid) {
        DatabaseService.getInstance().getStudent(
                new DatabaseService.DatabaseCallback<Student>() {
                    @Override
                    public void onCompleted(Student student) {
                        if (student == null) {
                            getTeacherFromDB(uid);
                            return;
                        }
                        // נמצא תלמיד
                        if (userType.equals("teacher")) {
                            // לחץ על מורה אבל הוא תלמיד
                            Toast.makeText(loginActivity.this,
                                    "שגיאה: משתמש זה רשום כתלמיד ולא כמורה",
                                    Toast.LENGTH_LONG).show();
                            return;
                        }
                        Intent intent = new Intent(loginActivity.this, StudentActivity.class);
                        startActivity(intent);
                    }

                    @Override
                    public void onFailed(Exception e) {
                        getTeacherFromDB(uid);
                    }
                });
    }

    private void getTeacherFromDB(String uid) {
        DatabaseService.getInstance().getTeacher(uid,
                new DatabaseService.DatabaseCallback<Teacher>() {
                    @Override
                    public void onCompleted(Teacher teacher) {
                        if (teacher == null) {
                            getAdminFromDB(uid);
                            return;
                        }
                        // נמצא מורה
                        if (userType.equals("student")) {
                            // לחץ על תלמיד אבל הוא מורה
                            Toast.makeText(loginActivity.this,
                                    "שגיאה: משתמש זה רשום כמורה ולא כתלמיד",
                                    Toast.LENGTH_LONG).show();
                            return;
                        }
                        Intent intent = new Intent(loginActivity.this, TeacherActivity.class);
                        startActivity(intent);
                    }

                    @Override
                    public void onFailed(Exception e) {
                        getAdminFromDB(uid);
                    }
                });
    }

    private void getAdminFromDB(String uid) {
        DatabaseService.getInstance().getAdmin(uid,
                new DatabaseService.DatabaseCallback<User>() {
                    @Override
                    public void onCompleted(User admin) {
                        if (admin == null) {
                            Toast.makeText(loginActivity.this,
                                    "משתמש לא נמצא במערכת",
                                    Toast.LENGTH_SHORT).show();
                            return;
                        }
                        Intent intent = new Intent(loginActivity.this, AdminActivity.class);
                        startActivity(intent);
                    }

                    @Override
                    public void onFailed(Exception e) {
                        Toast.makeText(loginActivity.this,
                                "שגיאה בכניסה למערכת", Toast.LENGTH_SHORT).show();
                    }
                });
    }


}