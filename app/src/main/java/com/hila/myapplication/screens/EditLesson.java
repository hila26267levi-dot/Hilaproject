package com.hila.myapplication.screens;

import static android.widget.Toast.LENGTH_LONG;

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

public class EditLesson extends AppCompatActivity implements View.OnClickListener {

    private DatabaseService databaseService;
    Intent takeit;

    TeacherLesson theLesson = null;

    private Button btnlesson;

    EditText edittext_subgect, edittext_time, edittext_date, et_class, editText_price;

    Teacher teacher = null;
    double price;

    Spinner sp_teachway;
    Spinner sp_subject;

    String subject = "";
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

        if (theLesson != null) {
            et_class.setText(theLesson.getKita());
            edittext_subgect.setText(theLesson.getSubject());
            edittext_time.setText(theLesson.getTime());
            edittext_date.setText(theLesson.getDate());
            editText_price.setText(String.valueOf(theLesson.getPrice()));
            ifzoom = theLesson.getZoomORhome();
            if (ifzoom.equals("למידה בזום"))
                ck_zoom.setChecked(true);
            else
                ck_zoom.setChecked(false);
        }
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
            public void onNothingSelected(AdapterView<?> parent) {
            }
        });

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

    private boolean validateInput(String subject, String date, String time, String priceStr) {

        // בדיקת מקצוע — חובה
        if (subject.isEmpty()) {
            Toast.makeText(this, "חובה להזין מקצוע", LENGTH_LONG).show();
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
                Toast.makeText(this, " תאריך לא תקין", LENGTH_LONG).show();
                return false;
            }

            // בדיקה שהתאריך לא עבר
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

        // בדיקת שעה — חובה, רק מספרים ונקודותיים (לדוגמה 10:30)
        if (time.isEmpty()) {
            Toast.makeText(this, "חובה להזין שעה", LENGTH_LONG).show();
            return false;
        }

        if (!time.matches("^\\d{1,2}:\\d{2}$")) {
            Toast.makeText(this, "פורמט שעה לא תקין, יש להזין בפורמט HH:MM (לדוגמה: 10:30)", LENGTH_LONG).show();
            return false;
        }

        // בדיקת מחיר — חובה ומספר
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

        return true;
    }

    @Override
    public void onClick(View v) {
        Log.d("TAG", "onClick: Register button clicked");

        String subject = edittext_subgect.getText().toString();
        String date = edittext_date.getText().toString();
        String time = edittext_time.getText().toString();
        String priceStr = editText_price.getText().toString();

        if (!validateInput(subject, date, time, priceStr)) {
            return;
        }

        price = Double.parseDouble(priceStr);

        TeacherLesson teacherLesson = new TeacherLesson(
                theLesson.getId(),
                theLesson.getTeacher(),
                subject,
                ifzoom,
                time,
                date,
                "availbale",
                kite,
                price
        );

        databaseService.updateLesson(teacherLesson, new DatabaseService.DatabaseCallback<Void>() {
            @Override
            public void onCompleted(Void object) {
                Intent intent = new Intent(EditLesson.this, TeacherActivity.class);
                startActivity(intent);
            }

            @Override
            public void onFailed(Exception e) {
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
            Intent intent = new Intent(EditLesson.this, TeacherActivity.class);
            startActivity(intent);
            return true;
        }
        if (id == R.id.teacher_profile) {
            Intent intent = new Intent(EditLesson.this, teacher_profile.class);
            startActivity(intent);
            return true;
        }
        if (id == R.id.teacher_mylesson) {
            Intent intent = new Intent(EditLesson.this, TeacherLessonsList.class);
            startActivity(intent);
            return true;
        }
        if (id == R.id.teacher_disconect) {
            Intent intent = new Intent(EditLesson.this, disconect_forteacher.class);
            startActivity(intent);
            return true;
        }
        if (id == R.id.teacher_adut) {
            Intent intent = new Intent(EditLesson.this, AdutActivity.class);
            startActivity(intent);
            return true;
        }
        return super.onOptionsItemSelected(item);
    }
}