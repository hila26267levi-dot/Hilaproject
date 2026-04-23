package com.hila.myapplication.screens;

import static android.widget.Toast.LENGTH_LONG;

import android.content.Intent;
import android.os.Bundle;
import android.view.Menu;
import android.view.MenuItem;
import android.view.View;
import android.widget.AdapterView;
import android.widget.Button;
import android.widget.CheckBox;
import android.widget.CompoundButton;
import android.widget.EditText;
import android.widget.Spinner;
import android.widget.Toast;

import androidx.activity.EdgeToEdge;
import androidx.annotation.NonNull;
import androidx.appcompat.app.AppCompatActivity;
import androidx.core.graphics.Insets;
import androidx.core.view.ViewCompat;
import androidx.core.view.WindowInsetsCompat;

import com.hila.myapplication.R;
import com.hila.myapplication.model.Teacher;
import com.hila.myapplication.model.TeacherLesson;
import com.hila.myapplication.servicses.DatabaseService;

public class EditLesson extends AppCompatActivity implements View.OnClickListener {

    private DatabaseService databaseService;
    Intent takeit;
    TeacherLesson theLesson = null;
    private Button btnlesson;
    EditText edittext_subgect, edittext_time, edittext_date, et_class, editText_price;
    Teacher teacher = null;
    double price;
    Spinner sp_teachway;
    String ifzoom = "";
    CheckBox ck_zoom;
    private String kite = "";

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        EdgeToEdge.enable(this);
        setContentView(R.layout.activity_edit_lesson);
        ViewCompat.setOnApplyWindowInsetsListener(findViewById(R.id.main), (v, insets) -> {
            Insets systemBars = insets.getInsets(WindowInsetsCompat.Type.systemBars());
            v.setPadding(systemBars.left, systemBars.top, systemBars.right, systemBars.bottom);
            return insets;
        });

        databaseService = DatabaseService.getInstance();
        initViews();

        takeit = getIntent();
        theLesson = (TeacherLesson) takeit.getSerializableExtra("Lesson");

        if (theLesson == null) {
            Toast.makeText(this, "שגיאה: לא נמצא שיעור לעריכה", Toast.LENGTH_SHORT).show();
            finish();
            return;
        }

        // מילוי השדות מהשיעור הקיים
        et_class.setText(theLesson.getKita());
        edittext_subgect.setText(theLesson.getSubject());
        edittext_time.setText(theLesson.getTime());
        edittext_date.setText(theLesson.getDate());
        editText_price.setText(String.valueOf(theLesson.getPrice()));
        ifzoom = theLesson.getZoomORhome();
        kite = theLesson.getKita() != null ? theLesson.getKita() : "";
        price = theLesson.getPrice();

        ck_zoom.setChecked("למידה בזום".equals(ifzoom));
    }

    private void initViews() {
        et_class = findViewById(R.id.etGradeClassE);
        edittext_time = findViewById(R.id.et_timelesson_addlessonE);
        edittext_subgect = findViewById(R.id.subjectlesson_addlessonE);
        edittext_date = findViewById(R.id.et_datelesson_addlessonE);
        editText_price = findViewById(R.id.et_pricelesson_addlesson);
        btnlesson = findViewById(R.id.button_addlessonE);
        ck_zoom = findViewById(R.id.ck_zoomE);
        sp_teachway = findViewById(R.id.sp_Class_addlessonE);

        sp_teachway.setOnItemSelectedListener(new AdapterView.OnItemSelectedListener() {
            @Override
            public void onItemSelected(AdapterView<?> parent, View view, int position, long id) {
                if (position > 0) {
                    String kit = (String) parent.getItemAtPosition(position);
                    kite += kit + ", ";
                    et_class.setText(kite);
                }
            }
            @Override
            public void onNothingSelected(AdapterView<?> parent) {}
        });

        ck_zoom.setOnCheckedChangeListener((@NonNull CompoundButton buttonView, boolean isChecked) -> {
            ifzoom = isChecked ? "למידה בזום" : "מגיע לבית התלמיד";
        });

        btnlesson.setOnClickListener(this);
    }

    @Override
    public void onClick(View v) {
        String subject = edittext_subgect.getText().toString().trim();
        String date = edittext_date.getText().toString().trim();
        String time = edittext_time.getText().toString().trim();
        String priceStr = editText_price.getText().toString().trim();

        // בדיקות תקינות
        if (subject.isEmpty()) {
            Toast.makeText(this, "חובה להזין מקצוע", LENGTH_LONG).show();
            return;
        }
        if (date.isEmpty()) {
            Toast.makeText(this, "חובה להזין תאריך", LENGTH_LONG).show();
            return;
        }
        if (time.isEmpty()) {
            Toast.makeText(this, "חובה להזין שעה", LENGTH_LONG).show();
            return;
        }
        if (priceStr.isEmpty()) {
            Toast.makeText(this, "חובה להזין מחיר", LENGTH_LONG).show();
            return;
        }

        try {
            price = Double.parseDouble(priceStr);
        } catch (NumberFormatException e) {
            Toast.makeText(this, "שגיאה: מחיר חייב להיות מספר", LENGTH_LONG).show();
            return;
        }

        TeacherLesson teacherLesson = new TeacherLesson(
                theLesson.getId(),
                theLesson.getTeacher(),
                subject,
                ifzoom,
                time,
                date,
                theLesson.getStatus(),
                kite,
                price
        );

        databaseService.updateLesson(teacherLesson, new DatabaseService.DatabaseCallback<Void>() {
            @Override
            public void onCompleted(Void object) {
                Toast.makeText(EditLesson.this,
                        "השיעור עודכן בהצלחה!", Toast.LENGTH_SHORT).show();
                Intent intent = new Intent(EditLesson.this, TeacherActivity.class);
                startActivity(intent);
                finish();
            }

            @Override
            public void onFailed(Exception e) {
                Toast.makeText(EditLesson.this,
                        "שגיאה בעדכון השיעור, נסה שוב", Toast.LENGTH_SHORT).show();
            }
        });
    }

    @Override
    public boolean onCreateOptionsMenu(Menu menu) {
        getMenuInflater().inflate(R.menu.teacher_menu, menu);
        return true;
    }

    @Override
    public boolean onOptionsItemSelected(MenuItem item) {
        int id = item.getItemId();
        if (id == R.id.teacher_home) {
            startActivity(new Intent(this, TeacherActivity.class));
            return true;
        }
        if (id == R.id.teacher_profile) {
            startActivity(new Intent(this, teacher_profile.class));
            return true;
        }
        if (id == R.id.teacher_mylesson) {
            startActivity(new Intent(this, TeacherLessonsList.class));
            return true;
        }
        if (id == R.id.teacher_disconect) {
            startActivity(new Intent(this, disconect_forteacher.class));
            return true;
        }
        if (id == R.id.teacher_adut) {
            startActivity(new Intent(this, AdutActivity.class));
            return true;
        }
        return super.onOptionsItemSelected(item);
    }
}