package com.hila.myapplication.screens;

import android.content.Intent;
import android.os.Bundle;
import android.util.Log;
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

public class Addnewlesson extends AppCompatActivity implements View.OnClickListener {

    private static final String TAG = "Addnewlesson";

    private Button btnlesson;

    DatabaseService databaseService;

    EditText edittext_subgect, edittext_time, edittext_date, et_class, edittext_price;

    Teacher teacher = null;

    Spinner sp_teachway;
    Spinner sp_subject; // ספינר מקצועות

    String ifzoom = "";

    CheckBox ck_zoom;
    private String kite = "";
    private String selectedSubject = ""; // המקצוע שנבחר

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        EdgeToEdge.enable(this);
        setContentView(R.layout.activity_addnewlesson);
        ViewCompat.setOnApplyWindowInsetsListener(findViewById(R.id.main), (v, insets) -> {
            Insets systemBars = insets.getInsets(WindowInsetsCompat.Type.systemBars());
            v.setPadding(systemBars.left, systemBars.top, systemBars.right, systemBars.bottom);
            return insets;
        });

        databaseService = DatabaseService.getInstance();
        databaseService.getTeacher(new DatabaseService.DatabaseCallback<Teacher>() {
            @Override
            public void onCompleted(Teacher object) {
                teacher = object;
            }

            @Override
            public void onFailed(Exception e) {
                Log.e(TAG, "Failed to get teacher", e);
            }
        });

        et_class = findViewById(R.id.etGradeClass);
        edittext_time = findViewById(R.id.et_timelesson_addlesson);
        edittext_subgect = findViewById(R.id.subjectlesson_addlesson);
        edittext_date = findViewById(R.id.et_datelesson_addlesson);
        edittext_price = findViewById(R.id.et_pricelesson_addlesson);
        sp_teachway = findViewById(R.id.sp_Class_addlesson);
        sp_subject = findViewById(R.id.sp_subject_addlesson); // ספינר מקצועות חדש

        // ספינר כיתות
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
            public void onNothingSelected(AdapterView<?> parent) {
            }
        });

        // ספינר מקצועות
        sp_subject.setOnItemSelectedListener(new AdapterView.OnItemSelectedListener() {
            @Override
            public void onItemSelected(AdapterView<?> parent, View view, int position, long id) {
                if (position > 0) {
                    selectedSubject = (String) parent.getItemAtPosition(position);
                    edittext_subgect.setText(selectedSubject);
                }
            }

            @Override
            public void onNothingSelected(AdapterView<?> parent) {
            }
        });

        btnlesson = findViewById(R.id.button_addlesson);
        ck_zoom = findViewById(R.id.ck_zoom);
        ck_zoom.setOnCheckedChangeListener(new CompoundButton.OnCheckedChangeListener() {
            @Override
            public void onCheckedChanged(@NonNull CompoundButton buttonView, boolean isChecked) {
                if (isChecked) {
                    ifzoom = "למידה בזום";
                } else {
                    ifzoom = "מגיע לבית התלמיד ";
                }
            }
        });

        btnlesson.setOnClickListener(this);
    }

    /**
     * בדיקת תקינות שעה בפורמט HH:mm
     * שעות: 0-23, דקות: 0-59
     */
    private boolean isValidTime(String time) {
        if (time == null || time.isEmpty()) return false;
        if (!time.matches("^\\d{1,2}:\\d{2}$")) return false;
        String[] parts = time.split(":");
        try {
            int hours = Integer.parseInt(parts[0]);
            int minutes = Integer.parseInt(parts[1]);
            return hours >= 0 && hours <= 23 && minutes >= 0 && minutes <= 59;
        } catch (NumberFormatException e) {
            return false;
        }
    }

    @Override
    public void onClick(View v) {
        Log.d(TAG, "onClick: Add lesson button clicked");

        String subject = edittext_subgect.getText().toString().trim();
        String date = edittext_date.getText().toString().trim();
        String time = edittext_time.getText().toString().trim();
        String priceStr = edittext_price.getText().toString().trim();
        String kita = et_class.getText().toString().trim();

        // בדיקות תקינות
        if (selectedSubject.isEmpty() || selectedSubject.equals("בחר מקצוע ")) {
            Toast.makeText(this, "חובה לבחור מקצוע מהרשימה", Toast.LENGTH_LONG).show();
            return;
        }

        if (date.isEmpty()) {
            Toast.makeText(this, "חובה להזין תאריך", Toast.LENGTH_LONG).show();
            return;
        }

        if (time.isEmpty()) {
            Toast.makeText(this, "חובה להזין שעה", Toast.LENGTH_LONG).show();
            return;
        }

        if (!isValidTime(time)) {
            Toast.makeText(this, "שגיאה: השעה חייבת להיות בפורמט תקין (HH:mm), שעות 0-23, דקות 0-59", Toast.LENGTH_LONG).show();
            return;
        }

        if (priceStr.isEmpty()) {
            Toast.makeText(this, "חובה להזין מחיר", Toast.LENGTH_LONG).show();
            return;
        }

        double price;
        try {
            price = Double.parseDouble(priceStr);
        } catch (NumberFormatException e) {
            Toast.makeText(this, "מחיר חייב להיות מספר תקין", Toast.LENGTH_LONG).show();
            return;
        }

        if (kita.isEmpty()) {
            Toast.makeText(this, "חובה לבחור כיתה", Toast.LENGTH_LONG).show();
            return;
        }

        if (teacher == null) {
            Toast.makeText(this, "שגיאה: לא ניתן לטעון פרטי מורה", Toast.LENGTH_LONG).show();
            return;
        }

        String lesson_id = databaseService.generatelessonId();

        TeacherLesson teacherLesson = new TeacherLesson(
                lesson_id,
                teacher,
                selectedSubject,
                ifzoom,
                time,
                date,
                "availbale",
                kita,
                price
        );

        databaseService.createNewLesson(teacherLesson, new DatabaseService.DatabaseCallback<Void>() {
            @Override
            public void onCompleted(Void object) {
                Toast.makeText(Addnewlesson.this, "השיעור נוסף בהצלחה!", Toast.LENGTH_SHORT).show();
                // איפוס הטופס על ידי טעינה מחדש של הדף
                Intent intent = new Intent(Addnewlesson.this, Addnewlesson.class);
                startActivity(intent);
                finish();
            }

            @Override
            public void onFailed(Exception e) {
                Toast.makeText(Addnewlesson.this, "שגיאה בהוספת השיעור", Toast.LENGTH_SHORT).show();
            }
        });

        Log.d(TAG, "onClick: Lesson being added...");
    }

    // תפריט צד מורה
    @Override
    public boolean onCreateOptionsMenu(Menu menu) {
        getMenuInflater().inflate(R.menu.teacher_menu, menu);
        return true;
    }

    @Override
    public boolean onOptionsItemSelected(MenuItem item) {
        int id = item.getItemId();

        if (id == R.id.teacher_home) {
            Intent intent = new Intent(Addnewlesson.this, TeacherActivity.class);
            startActivity(intent);
            return true;
        }
        if (id == R.id.teacher_profile) {
            Intent intent = new Intent(Addnewlesson.this, teacher_profile.class);
            startActivity(intent);
            return true;
        }
        if (id == R.id.teacher_mylesson) {
            Intent intent = new Intent(Addnewlesson.this, TeacherLessonsList.class);
            startActivity(intent);
            return true;
        }
        if (id == R.id.teacher_disconect) {
            Intent intent = new Intent(Addnewlesson.this, disconect_forteacher.class);
            startActivity(intent);
            return true;
        }
        if (id == R.id.teacher_adut) {
            Intent intent = new Intent(Addnewlesson.this, AdutActivity.class);
            startActivity(intent);
            return true;
        }
        return super.onOptionsItemSelected(item);
    }
}

