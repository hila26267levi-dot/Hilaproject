package com.hila.myapplication.screens;

import android.content.Intent;
import android.os.Bundle;
import android.view.Menu;
import android.view.MenuItem;
import android.view.View;
import android.widget.AdapterView;
import android.widget.Button;
import android.widget.EditText;
import android.widget.Spinner;
import android.widget.Toast;

import androidx.activity.EdgeToEdge;
import androidx.appcompat.app.AppCompatActivity;
import androidx.core.graphics.Insets;
import androidx.core.view.ViewCompat;
import androidx.core.view.WindowInsetsCompat;

import com.google.firebase.auth.FirebaseAuth;
import com.hila.myapplication.R;
import com.hila.myapplication.model.Teacher;
import com.hila.myapplication.servicses.DatabaseService;

public class Teacher_edit_profile extends AppCompatActivity implements View.OnClickListener {

    EditText fname, zoom, lname, age, price, teachclass, subject;
    Spinner sp_teachclass_edit, sp_subject_edit;
    Teacher currentTeacher;
    String uid;
    Button btn_save;
    private DatabaseService databaseService;

    String selectedTeachclass = "";
    String selectedSubject = "";

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        EdgeToEdge.enable(this);
        setContentView(R.layout.activity_teacher_edit_profile);

        databaseService = DatabaseService.getInstance();

        fname = findViewById(R.id.profile_Teacher_E_Fname);
        lname = findViewById(R.id.profile_Teacher_E_Lname);
        age = findViewById(R.id.profile_Teacher_E_age);
        price = findViewById(R.id.profile_Teacher_E_price);
        teachclass = findViewById(R.id.profile_Teacher_E_teachclass);
        subject = findViewById(R.id.profile_Teacher_E_subject);
        zoom = findViewById(R.id.profile_Teacher_E_zoom);
        btn_save = findViewById(R.id.profile_Teacher_E_btn);
        btn_save.setOnClickListener(this);

        // ספינר כיתות לימוד
        sp_teachclass_edit = findViewById(R.id.sp_teachclass_edit);
        sp_teachclass_edit.setOnItemSelectedListener(new AdapterView.OnItemSelectedListener() {
            @Override
            public void onItemSelected(AdapterView<?> parent, View view, int position, long id) {
                if (position > 0) {
                    String kit = (String) parent.getItemAtPosition(position);
                    selectedTeachclass += kit + ", ";
                    teachclass.setText(selectedTeachclass);
                }
            }
            @Override
            public void onNothingSelected(AdapterView<?> parent) {}
        });

        // ספינר מקצוע
        sp_subject_edit = findViewById(R.id.sp_subject_edit);
        sp_subject_edit.setOnItemSelectedListener(new AdapterView.OnItemSelectedListener() {
            @Override
            public void onItemSelected(AdapterView<?> parent, View view, int position, long id) {
                if (position > 0) {
                    String subj = (String) parent.getItemAtPosition(position);
                    selectedSubject += subj + ", ";
                    subject.setText(selectedSubject);
                }
            }
            @Override
            public void onNothingSelected(AdapterView<?> parent) {}
        });

        if (FirebaseAuth.getInstance().getCurrentUser() == null) {
            Toast.makeText(this, "שגיאה: משתמש לא מחובר", Toast.LENGTH_SHORT).show();
            startActivity(new Intent(this, loginActivity.class));
            finish();
            return;
        }

        uid = FirebaseAuth.getInstance().getCurrentUser().getUid();

        databaseService.getTeacher(uid, new DatabaseService.DatabaseCallback<Teacher>() {
            @Override
            public void onCompleted(Teacher teacher) {
                currentTeacher = teacher;
                if (teacher != null) {
                    loadTeacherData();
                } else {
                    Toast.makeText(Teacher_edit_profile.this,
                            "לא נמצאו פרטי מורה", Toast.LENGTH_SHORT).show();
                }
            }

            @Override
            public void onFailed(Exception e) {
                Toast.makeText(Teacher_edit_profile.this,
                        "שגיאה בטעינת הפרטים", Toast.LENGTH_SHORT).show();
            }
        });
    }

    private void loadTeacherData() {
        if (currentTeacher == null) return;
        fname.setText(currentTeacher.getFname());
        lname.setText(currentTeacher.getLname());
        age.setText(currentTeacher.getAge());
        price.setText(String.valueOf(currentTeacher.getPrice()));
        teachclass.setText(currentTeacher.getTeachclass());
        selectedTeachclass = currentTeacher.getTeachclass() != null ?
                currentTeacher.getTeachclass() : "";
        subject.setText(currentTeacher.getSubject());
        selectedSubject = currentTeacher.getSubject() != null ?
                currentTeacher.getSubject() : "";
        zoom.setText(currentTeacher.getZoom());
    }

    private void saveTeacherProfile() {
        if (currentTeacher == null) {
            Toast.makeText(this, "שגיאה: לא נטענו פרטי מורה", Toast.LENGTH_SHORT).show();
            return;
        }

        String fnameVal = fname.getText().toString().trim();
        String lnameVal = lname.getText().toString().trim();
        String ageVal = age.getText().toString().trim();
        String priceVal = price.getText().toString().trim();
        String zoomVal = zoom.getText().toString().trim();

        // בדיקות תקינות
        String nameRegex = "^[A-Za-zא-ת]+$";

        if (fnameVal.isEmpty()) {
            Toast.makeText(this, "שגיאה: חובה להזין שם פרטי!", Toast.LENGTH_LONG).show();
            return;
        }
        if (!fnameVal.matches(nameRegex)) {
            Toast.makeText(this, "שגיאה: שם פרטי חייב להכיל אותיות בלבד!", Toast.LENGTH_LONG).show();
            return;
        }
        if (lnameVal.isEmpty()) {
            Toast.makeText(this, "שגיאה: חובה להזין שם משפחה!", Toast.LENGTH_LONG).show();
            return;
        }
        if (!lnameVal.matches(nameRegex)) {
            Toast.makeText(this, "שגיאה: שם משפחה חייב להכיל אותיות בלבד!", Toast.LENGTH_LONG).show();
            return;
        }
        if (ageVal.isEmpty()) {
            Toast.makeText(this, "שגיאה: חובה להזין גיל!", Toast.LENGTH_LONG).show();
            return;
        }
        if (!ageVal.matches("^[0-9]+$")) {
            Toast.makeText(this, "שגיאה: גיל חייב להיות מספר בלבד!", Toast.LENGTH_LONG).show();
            return;
        }
        int ageValue = Integer.parseInt(ageVal);
        if (ageValue < 18) {
            Toast.makeText(this, "שגיאה: גיל חייב להיות 18 ומעלה!", Toast.LENGTH_LONG).show();
            return;
        }
        // בדיקת כיתות לימוד — חובה מהספינר
        if (selectedTeachclass.isEmpty()) {
            Toast.makeText(this, "שגיאה: חובה לבחור כיתות לימוד!", Toast.LENGTH_LONG).show();
            return;
        }
        if (priceVal.isEmpty()) {
            Toast.makeText(this, "שגיאה: חובה להזין מחיר!", Toast.LENGTH_LONG).show();
            return;
        }
        try {
            double p = Double.parseDouble(priceVal);
            if (p <= 0) {
                Toast.makeText(this, "שגיאה: מחיר חייב להיות מספר חיובי!", Toast.LENGTH_LONG).show();
                return;
            }
        } catch (NumberFormatException e) {
            Toast.makeText(this, "שגיאה: מחיר חייב להיות מספר!", Toast.LENGTH_LONG).show();
            return;
        }
        // בדיקת מקצוע — חובה מהספינר
        if (selectedSubject.isEmpty()) {
            Toast.makeText(this, "שגיאה: חובה לבחור מקצוע מהרשימה!", Toast.LENGTH_LONG).show();
            return;
        }

        double priceDouble = Double.parseDouble(priceVal);

        currentTeacher.setFname(fnameVal);
        currentTeacher.setLname(lnameVal);
        currentTeacher.setAge(ageVal);
        currentTeacher.setPrice(priceDouble);
        currentTeacher.setTeachclass(selectedTeachclass);
        currentTeacher.setSubject(selectedSubject);
        currentTeacher.setZoom(zoomVal);

        databaseService.updateTeacher(currentTeacher, new DatabaseService.DatabaseCallback<Void>() {
            @Override
            public void onCompleted(Void object) {
                Toast.makeText(Teacher_edit_profile.this,
                        "הפרופיל עודכן בהצלחה!", Toast.LENGTH_SHORT).show();
                startActivity(new Intent(Teacher_edit_profile.this, TeacherActivity.class));
                finish();
            }

            @Override
            public void onFailed(Exception e) {
                Toast.makeText(Teacher_edit_profile.this,
                        "שגיאה בשמירת הפרופיל, נסה שוב", Toast.LENGTH_SHORT).show();
            }
        });
    }

    @Override
    public void onClick(View v) {
        saveTeacherProfile();
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