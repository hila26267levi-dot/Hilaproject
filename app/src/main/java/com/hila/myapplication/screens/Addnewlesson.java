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

import java.util.Calendar;

import static android.widget.Toast.LENGTH_LONG;

public class Addnewlesson extends AppCompatActivity implements View.OnClickListener {

    private static final String TAG = "RegisterActivity";

    private Button btnlesson;
    DatabaseService databaseService;

    EditText edittext_time, edittext_date, et_class, edittext_price;

    Teacher teacher = null;

    Spinner sp_teachway;
    Spinner sp_subject_addlesson;

    String ifzoom = "";
    String selectedSubject = "";

    CheckBox ck_zoom;
    private String kite = "";

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
            }
        });

        et_class = findViewById(R.id.etGradeClass);
        edittext_time = findViewById(R.id.et_timelesson_addlesson);
        edittext_date = findViewById(R.id.et_datelesson_addlesson);
        edittext_price = findViewById(R.id.et_pricelesson_addlesson);

        // ספינר כיתה
        sp_teachway = findViewById(R.id.sp_Class_addlesson);
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

        // ספינר מקצוע
        sp_subject_addlesson = findViewById(R.id.sp_subject_addlesson);
        sp_subject_addlesson.setOnItemSelectedListener(new AdapterView.OnItemSelectedListener() {
            @Override
            public void onItemSelected(AdapterView<?> parent, View view, int position, long id) {
                if (position > 0) {
                    selectedSubject = (String) parent.getItemAtPosition(position);
                } else {
                    selectedSubject = "";
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
                    ifzoom = "מגיע לבית התלמיד";
                }
            }
        });

        btnlesson.setOnClickListener(this);
    }

    private boolean validateInput(String date, String time, String priceStr) {

        // בדיקת מקצוע — חובה מהספינר
        if (selectedSubject.isEmpty()) {
            Toast.makeText(this, "חובה לבחור מקצוע מהרשימה", LENGTH_LONG).show();
            return false;
        }

        // בדיקת תאריך — חובה ופורמט dd.MM ותאריך לא עבר
        if (date.isEmpty()) {
            Toast.makeText(this, "חובה להזין תאריך", LENGTH_LONG).show();
            return false;
        }

        if (!date.matches("^\\d{1,2}\\.\\d{1,2}$")) {
            Toast.makeText(this, "פורמט תאריך לא תקין, יש להזין בפורמט dd.MM (לדוגמה: 25.4)", LENGTH_LONG).show();
            return false;
        }

        try {
            String[] parts = date.split("\\.");
            int lessonDay = Integer.parseInt(parts[0].trim());
            int lessonMonth = Integer.parseInt(parts[1].trim());

            if (lessonMonth < 1 || lessonMonth > 12 || lessonDay < 1 || lessonDay > 31) {
                Toast.makeText(this, "תאריך לא תקין", LENGTH_LONG).show();
                return false;
            }

            Calendar todayCal = Calendar.getInstance();
            int todayDay = todayCal.get(Calendar.DAY_OF_MONTH);
            int todayMonth = todayCal.get(Calendar.MONTH) + 1;

            if (lessonMonth < todayMonth ||
                    (lessonMonth == todayMonth && lessonDay < todayDay)) {
                Toast.makeText(this, "שגיאה: התאריך שהזנת כבר עבר", LENGTH_LONG).show();
                return false;
            }

        } catch (Exception e) {
            Toast.makeText(this, "פורמט תאריך לא תקין", LENGTH_LONG).show();
            return false;
        }

        // בדיקת שעה — חובה, פורמט HH:MM
        if (time.isEmpty()) {
            Toast.makeText(this, "חובה להזין שעה", LENGTH_LONG).show();
            return false;
        }

        if (!time.matches("^\\d{1,2}:\\d{2}$")) {
            Toast.makeText(this, "פורמט שעה לא תקין, יש להזין בפורמט HH:MM (לדוגמה: 10:30)", LENGTH_LONG).show();
            return false;
        }

        // בדיקת מחיר — חובה ומספר חיובי
        if (priceStr.isEmpty()) {
            Toast.makeText(this, "חובה להזין מחיר", LENGTH_LONG).show();
            return false;
        }

        try {
            double p = Double.parseDouble(priceStr);
            if (p <= 0) {
                Toast.makeText(this, "מחיר חייב להיות מספר חיובי", LENGTH_LONG).show();
                return false;
            }
        } catch (NumberFormatException e) {
            Toast.makeText(this, "שגיאה: מחיר חייב להיות מספר", LENGTH_LONG).show();
            return false;
        }

        // בדיקת כיתה — חובה לבחור
        if (kite.isEmpty()) {
            Toast.makeText(this, "חובה לבחור כיתה", LENGTH_LONG).show();
            return false;
        }

        // בדיקת דרך למידה — חובה לסמן
        if (ifzoom.isEmpty()) {
            Toast.makeText(this, "חובה לבחור דרך למידה", LENGTH_LONG).show();
            return false;
        }

        return true;
    }

    @Override
    public void onClick(View v) {
        Log.d(TAG, "onClick: Register button clicked");

        String date = edittext_date.getText().toString();
        String time = edittext_time.getText().toString();
        String priceStr = edittext_price.getText().toString();

        if (!validateInput(date, time, priceStr)) {
            return;
        }

        double price = Double.parseDouble(priceStr);
        String lesson_id = databaseService.generatelessonId();

        TeacherLesson teacherLesson = new TeacherLesson(
                lesson_id, teacher, selectedSubject, ifzoom, time, date, "availbale", kite, price);

        databaseService.createNewLesson(teacherLesson, new DatabaseService.DatabaseCallback<Void>() {
            @Override
            public void onCompleted(Void object) {
                Toast.makeText(Addnewlesson.this, "השיעור נוסף בהצלחה!", LENGTH_LONG).show();
                Intent intent = new Intent(Addnewlesson.this, TeacherLessonsList.class);
                startActivity(intent);
            }

            @Override
            public void onFailed(Exception e) {
                Toast.makeText(Addnewlesson.this, "שגיאה בהוספת השיעור", LENGTH_LONG).show();
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
            startActivity(new Intent(Addnewlesson.this, TeacherActivity.class));
            return true;
        }
        if (id == R.id.teacher_profile) {
            startActivity(new Intent(Addnewlesson.this, teacher_profile.class));
            return true;
        }
        if (id == R.id.teacher_mylesson) {
            startActivity(new Intent(Addnewlesson.this, TeacherLessonsList.class));
            return true;
        }
        if (id == R.id.teacher_disconect) {
            startActivity(new Intent(Addnewlesson.this, disconect_forteacher.class));
            return true;
        }
        if (id == R.id.teacher_adut) {
            startActivity(new Intent(Addnewlesson.this, AdutActivity.class));
            return true;
        }
        return super.onOptionsItemSelected(item);
    }
}


