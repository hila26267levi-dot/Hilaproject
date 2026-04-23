package com.hila.myapplication.screens;

import android.content.Intent;
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

import com.google.firebase.auth.FirebaseAuth;
import com.hila.myapplication.R;
import com.hila.myapplication.model.Teacher;
import com.hila.myapplication.servicses.DatabaseService;

public class Teacher_edit_profile extends AppCompatActivity implements View.OnClickListener {

    EditText fname, zoom, lname, age, price, teachclass, subject;
    Teacher currentTeacher;
    String uid;
    Button btn_save;
    private DatabaseService databaseService;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        EdgeToEdge.enable(this);
        setContentView(R.layout.activity_teacher_edit_profile);
        ViewCompat.setOnApplyWindowInsetsListener(findViewById(R.id.main), (v, insets) -> {
            Insets systemBars = insets.getInsets(WindowInsetsCompat.Type.systemBars());
            v.setPadding(systemBars.left, systemBars.top, systemBars.right, systemBars.bottom);
            return insets;
        });

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
        subject.setText(currentTeacher.getSubject());
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
        String teachclassVal = teachclass.getText().toString().trim();
        String subjectVal = subject.getText().toString().trim();
        String zoomVal = zoom.getText().toString().trim();

        // בדיקות תקינות
        if (!validateInput(fnameVal, lnameVal, priceVal, ageVal)) return;

        double priceDouble;
        try {
            priceDouble = Double.parseDouble(priceVal);
        } catch (NumberFormatException e) {
            Toast.makeText(this, "שגיאה: מחיר חייב להיות מספר", Toast.LENGTH_LONG).show();
            return;
        }

        currentTeacher.setFname(fnameVal);
        currentTeacher.setLname(lnameVal);
        currentTeacher.setAge(ageVal);
        currentTeacher.setPrice(priceDouble);
        currentTeacher.setTeachclass(teachclassVal);
        currentTeacher.setSubject(subjectVal);
        currentTeacher.setZoom(zoomVal);

        databaseService.updateTeacher(currentTeacher, new DatabaseService.DatabaseCallback<Void>() {
            @Override
            public void onCompleted(Void object) {
                Toast.makeText(Teacher_edit_profile.this,
                        "הפרופיל עודכן בהצלחה!", Toast.LENGTH_SHORT).show();
                Intent intent = new Intent(Teacher_edit_profile.this, TeacherActivity.class);
                startActivity(intent);
                finish();
            }

            @Override
            public void onFailed(Exception e) {
                Toast.makeText(Teacher_edit_profile.this,
                        "שגיאה בשמירת הפרופיל, נסה שוב", Toast.LENGTH_SHORT).show();
            }
        });
    }

    public boolean validateInput(String fnameVal, String lnameVal, String priceVal, String ageVal) {
        String nameRegex = "^[A-Za-zא-ת]+$";
        String numberRegex = "^[0-9]+(\\.[0-9]+)?$";

        if (fnameVal.isEmpty()) {
            Toast.makeText(this, "שגיאה: חובה להזין שם פרטי!", Toast.LENGTH_LONG).show();
            return false;
        }
        if (!fnameVal.matches(nameRegex)) {
            Toast.makeText(this, "שגיאה: שם פרטי חייב להכיל אותיות בלבד!", Toast.LENGTH_LONG).show();
            return false;
        }
        if (lnameVal.isEmpty()) {
            Toast.makeText(this, "שגיאה: חובה להזין שם משפחה!", Toast.LENGTH_LONG).show();
            return false;
        }
        if (!lnameVal.matches(nameRegex)) {
            Toast.makeText(this, "שגיאה: שם משפחה חייב להכיל אותיות בלבד!", Toast.LENGTH_LONG).show();
            return false;
        }
        if (priceVal.isEmpty()) {
            Toast.makeText(this, "שגיאה: חובה להזין מחיר!", Toast.LENGTH_LONG).show();
            return false;
        }
        if (!priceVal.matches(numberRegex)) {
            Toast.makeText(this, "שגיאה: מחיר חייב להיות מספר בלבד!", Toast.LENGTH_LONG).show();
            return false;
        }
        if (ageVal.isEmpty()) {
            Toast.makeText(this, "שגיאה: חובה להזין גיל!", Toast.LENGTH_LONG).show();
            return false;
        }
        if (!ageVal.matches("^[0-9]+$")) {
            Toast.makeText(this, "שגיאה: גיל חייב להיות מספר בלבד!", Toast.LENGTH_LONG).show();
            return false;
        }
        int ageValue = Integer.parseInt(ageVal);
        if (ageValue < 10) {
            Toast.makeText(this, "שגיאה: גיל חייב להיות 10 ומעלה!", Toast.LENGTH_LONG).show();
            return false;
        }
        return true;
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