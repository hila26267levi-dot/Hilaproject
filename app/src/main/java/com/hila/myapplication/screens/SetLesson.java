package com.hila.myapplication.screens;

import android.content.Intent;
import android.os.Bundle;
import android.view.Menu;
import android.view.MenuItem;
import android.view.View;
import android.widget.Button;
import android.widget.CheckBox;
import android.widget.Spinner;
import android.widget.TextView;
import android.widget.Toast;

import androidx.activity.EdgeToEdge;
import androidx.appcompat.app.AppCompatActivity;
import androidx.core.graphics.Insets;
import androidx.core.view.ViewCompat;
import androidx.core.view.WindowInsetsCompat;

import com.hila.myapplication.R;
import com.hila.myapplication.model.Student;
import com.hila.myapplication.model.Teacher;
import com.hila.myapplication.model.TeacherLesson;
import com.hila.myapplication.servicses.DatabaseService;

public class SetLesson extends AppCompatActivity implements View.OnClickListener {

    private DatabaseService databaseService;
    Intent takeit;
    TeacherLesson theLesson = null;
    private Button btnSEtLesson;
    TextView edittext_subgect, edittext_time, edittext_date, et_class;
    Teacher teacher = null;
    Spinner sp_teachway;
    String subject = "";
    String ifzoom = "";
    CheckBox ck_zoom;
    private String kite = "";

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        EdgeToEdge.enable(this);
        setContentView(R.layout.activity_set_lesson);
        ViewCompat.setOnApplyWindowInsetsListener(findViewById(R.id.main), (v, insets) -> {
            Insets systemBars = insets.getInsets(WindowInsetsCompat.Type.systemBars());
            v.setPadding(systemBars.left, systemBars.top, systemBars.right, systemBars.bottom);
            return insets;
        });

        databaseService = DatabaseService.getInstance();
        initViews();

        takeit = getIntent();
        theLesson = (TeacherLesson) takeit.getSerializableExtra("Lesson");

        if (theLesson != null) {
            et_class.setText(theLesson.getKita());
            edittext_subgect.setText(theLesson.getSubject());
            edittext_time.setText(theLesson.getTime());
            edittext_date.setText(theLesson.getDate());
            ifzoom = theLesson.getZoomORhome();
            if (ifzoom.equals("למידה בזום"))
                ck_zoom.setChecked(true);
            else
                ck_zoom.setChecked(false);
        }
    }

    private void initViews() {
        et_class = findViewById(R.id.etGradeClassSet);
        edittext_time = findViewById(R.id.et_timelesson_addlessonSet);
        edittext_subgect = findViewById(R.id.subjectlesson_addlessonSet);
        edittext_date = findViewById(R.id.et_datelesson_addlessonSet);
        btnSEtLesson = findViewById(R.id.btnSetLesson);
        ck_zoom = findViewById(R.id.ck_zoomSet);
        btnSEtLesson.setOnClickListener(this);
    }

    @Override
    public void onClick(View v) {
        databaseService.getStudent(new DatabaseService.DatabaseCallback<Student>() {
            @Override
            public void onCompleted(Student student) {
                student = new Student(student);
                theLesson.setStudent(student);
                theLesson.setStatus("taken");
                databaseService.setLessonForStudent(theLesson, new DatabaseService.DatabaseCallback<Void>() {
                    @Override
                    public void onCompleted(Void object) {
                        // הודעה שהשיעור נקבע בהצלחה
                        Toast.makeText(SetLesson.this,
                                "השיעור נקבע בהצלחה!", Toast.LENGTH_LONG).show();
                        Intent go = new Intent(SetLesson.this, StudentActivity.class);
                        startActivity(go);
                    }

                    @Override
                    public void onFailed(Exception e) {
                        Toast.makeText(SetLesson.this,
                                "שגיאה בקביעת השיעור", Toast.LENGTH_LONG).show();
                    }
                });
            }

            @Override
            public void onFailed(Exception e) {
                Toast.makeText(SetLesson.this,
                        "שגיאה בטעינת פרטי תלמיד", Toast.LENGTH_LONG).show();
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
            startActivity(new Intent(SetLesson.this, StudentActivity.class));
            return true;
        }
        if (id == R.id.student_searchteacher) {
            startActivity(new Intent(SetLesson.this, TeacherListActivity.class));
            return true;
        }
        if (id == R.id.student_profile) {
            startActivity(new Intent(SetLesson.this, StudentProfile.class));
            return true;
        }
        if (id == R.id.student_disconect) {
            startActivity(new Intent(SetLesson.this, disconect_forstudent.class));
            return true;
        }
        if (id == R.id.student_mylesson) {
            startActivity(new Intent(SetLesson.this, student_lesson_list.class));
            return true;
        }
        if (id == R.id.student_adut) {
            startActivity(new Intent(SetLesson.this, AdutActivity.class));
            return true;
        }
        return super.onOptionsItemSelected(item);
    }
}