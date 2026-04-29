package com.hila.myapplication.screens;

import android.annotation.SuppressLint;
import android.content.Intent;
import android.content.SharedPreferences;
import android.os.Bundle;
import android.util.Log;
import android.view.View;
import android.widget.AdapterView;
import android.widget.Button;
import android.widget.EditText;
import android.widget.RadioButton;
import android.widget.RadioGroup;
import android.widget.Spinner;
import android.widget.Toast;

import androidx.activity.EdgeToEdge;
import androidx.appcompat.app.AppCompatActivity;

import com.hila.myapplication.R;
import com.hila.myapplication.model.Teacher;
import com.hila.myapplication.servicses.DatabaseService;

public class RegisterTeacherActivity extends AppCompatActivity implements View.OnClickListener, AdapterView.OnItemSelectedListener {
    private static final String TAG = "RegisterActivity";

    private Button btnteacher;

    DatabaseService databaseService;

    EditText edittext_email, edittext_fname, edittext_lname, edittext_phone, edittext_price, edittext_password, edittext_subject, edittext_age;

    RadioGroup teacher_zoom;

    RadioButton rbyes, rbno;

    Spinner spteachclass, spsubject;

    String subject = "";
    private String password;
    private String email;

    @SuppressLint({"WrongViewCast", "MissingInflatedId"})
    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        EdgeToEdge.enable(this);
        setContentView(R.layout.activity_teacher_register);

        edittext_email    = findViewById(R.id.et_teacher_email);
        edittext_fname    = findViewById(R.id.et_teacher_fname);
        edittext_lname    = findViewById(R.id.et_teacher_lname);
        edittext_phone    = findViewById(R.id.et_teacher_phone);
        edittext_price    = findViewById(R.id.et_teacher_price);
        teacher_zoom      = findViewById(R.id.zoomYesNo);
        edittext_password = findViewById(R.id.et_teacher_password);
        spteachclass      = findViewById(R.id.et_teacher_teach_class);
        edittext_age      = findViewById(R.id.et_teacher_age);
        spsubject         = findViewById(R.id.spSubject);
        edittext_subject  = findViewById(R.id.etSubjects);
        rbyes             = findViewById(R.id.rbYes);
        rbno              = findViewById(R.id.rbNo);
        btnteacher        = findViewById(R.id.btn_register);

        databaseService = DatabaseService.getInstance();

        btnteacher.setOnClickListener(this);
        spsubject.setOnItemSelectedListener(this);
    }

    @Override
    public void onClick(View v) {
        if (v.getId() == btnteacher.getId()) {
            Log.d(TAG, "onClick: Register button clicked");

            email          = edittext_email.getText().toString();
            password       = edittext_password.getText().toString();
            String fName   = edittext_fname.getText().toString();
            String lName   = edittext_lname.getText().toString();
            String age     = edittext_age.getText().toString();
            String phone   = edittext_phone.getText().toString();
            String stprice = edittext_price.getText().toString();

            // ✅ בדיקת זום - חייב לבחור כן או לא
            if (!rbyes.isChecked() && !rbno.isChecked()) {
                Toast.makeText(this, "שגיאה: חובה לבחור האם השיעור מתקיים בזום!", Toast.LENGTH_LONG).show();
                return;
            }

            String zoom = rbyes.isChecked() ? "yes" : "no";

            // ✅ בדיקת ספינר מקצוע - חייב לבחור
            if (spsubject.getSelectedItemPosition() == 0) {
                Toast.makeText(this, "שגיאה: חובה לבחור מקצוע!", Toast.LENGTH_LONG).show();
                return;
            }

            // ✅ בדיקת ספינר כיתה - חייב לבחור
            if (spteachclass.getSelectedItemPosition() == 0) {
                Toast.makeText(this, "שגיאה: חובה לבחור כיתה!", Toast.LENGTH_LONG).show();
                return;
            }

            String teachclass = spteachclass.getSelectedItem().toString();
            String subject2   = edittext_subject.getText().toString();

            // ✅ בדיקת תקינות שאר השדות - עצור אם יש שגיאה
            if (!validateInput(phone, fName, lName, email, password, stprice, age)) {
                return;
            }

            double price = Double.parseDouble(stprice);

            Log.d(TAG, "onClick: Registering user...");
            registerUser(fName, lName, phone, email, password, age, subject2, price, zoom, teachclass, "jkjk");
        }
    }

    private void registerUser(String fname, String lname, String phone, String email,
                              String password, String age, String subject,
                              double price, String zoom, String teachclass, String id) {
        Log.d(TAG, "registerUser: Registering user...");

        Teacher teacher = new Teacher("99", fname, lname, phone, email, password, age, subject, price, zoom, teachclass);
        Log.d(TAG, teacher.toString());

        createUserInDatabase(teacher);
    }

    private void createUserInDatabase(Teacher teacher) {
        databaseService.createNewTeacher(teacher, new DatabaseService.DatabaseCallback<String>() {
            @Override
            public void onCompleted(String uid) {
                Log.d(TAG, "createUserInDatabase: User created successfully");

                teacher.setId(uid);

                SharedPreferences sharedPreferences = getSharedPreferences("myPrefs", MODE_PRIVATE);
                SharedPreferences.Editor editor = sharedPreferences.edit();
                editor.putString("email", email);
                editor.putString("password", password);
                editor.apply();

                Log.d(TAG, "createUserInDatabase: Redirecting to TeacherActivity");

                Intent mainIntent = new Intent(RegisterTeacherActivity.this, TeacherActivity.class);
                mainIntent.setFlags(Intent.FLAG_ACTIVITY_NEW_TASK | Intent.FLAG_ACTIVITY_CLEAR_TASK);
                startActivity(mainIntent);
            }

            @Override
            public void onFailed(Exception e) {
                Log.e(TAG, "createUserInDatabase: Failed to create user", e);
                Toast.makeText(RegisterTeacherActivity.this, "Failed to register user", Toast.LENGTH_SHORT).show();
            }
        });
    }

    @Override
    public void onItemSelected(AdapterView<?> parent, View view, int position, long id) {
        if (position > 0) {
            String subj = (String) parent.getItemAtPosition(position);
            subject += subj + ", ";
            edittext_subject.setText(subject);
        }
    }

    @Override
    public void onNothingSelected(AdapterView<?> parent) {
    }

    // בדיקות תקינות — מחזירה true אם הכל תקין
    public boolean validateInput(String phone, String fName, String lName,
                                 String email, String password,
                                 String stprice, String age) {

        String phoneRegex  = "^[0-9]{10}$";
        String nameRegex   = "^[A-Za-z]+$";
        String emailRegex  = "^[a-zA-Z0-9._%+-]+@gmail\\.com$";
        String numberRegex = "^[0-9]+$";

        // טלפון
        if (phone.isEmpty()) {
            Toast.makeText(this, "שגיאה: חובה להזין מספר טלפון!", Toast.LENGTH_LONG).show();
            return false;
        }
        if (!phone.matches(phoneRegex)) {
            Toast.makeText(this, "שגיאה: מספר הטלפון חייב להיות בדיוק 10 ספרות!", Toast.LENGTH_LONG).show();
            return false;
        }

        // שם פרטי
        if (fName.isEmpty()) {
            Toast.makeText(this, "שגיאה: חובה להזין שם פרטי!", Toast.LENGTH_LONG).show();
            return false;
        }
        if (!fName.matches(nameRegex)) {
            Toast.makeText(this, "שגיאה: שם פרטי חייב להכיל אותיות בלבד!", Toast.LENGTH_LONG).show();
            return false;
        }

        // שם משפחה
        if (lName.isEmpty()) {
            Toast.makeText(this, "שגיאה: חובה להזין שם משפחה!", Toast.LENGTH_LONG).show();
            return false;
        }
        if (!lName.matches(nameRegex)) {
            Toast.makeText(this, "שגיאה: שם משפחה חייב להכיל אותיות בלבד!", Toast.LENGTH_LONG).show();
            return false;
        }

        // אימייל
        if (email.isEmpty()) {
            Toast.makeText(this, "שגיאה: חובה להזין אימייל!", Toast.LENGTH_LONG).show();
            return false;
        }
        if (!email.matches(emailRegex)) {
            Toast.makeText(this, "שגיאה: יש להזין כתובת Gmail תקינה!", Toast.LENGTH_LONG).show();
            return false;
        }

        // סיסמה
        if (password.isEmpty()) {
            Toast.makeText(this, "שגיאה: חובה להזין סיסמה!", Toast.LENGTH_LONG).show();
            return false;
        }
        if (password.length() < 6) {
            Toast.makeText(this, "שגיאה: הסיסמה חייבת להכיל לפחות 6 תווים!", Toast.LENGTH_LONG).show();
            return false;
        }

        // מחיר
        if (stprice.isEmpty()) {
            Toast.makeText(this, "שגיאה: חובה להזין מחיר!", Toast.LENGTH_LONG).show();
            return false;
        }
        if (!stprice.matches(numberRegex)) {
            Toast.makeText(this, "שגיאה: מחיר חייב להיות מספר בלבד!", Toast.LENGTH_LONG).show();
            return false;
        }

        // גיל
        if (age.isEmpty()) {
            Toast.makeText(this, "שגיאה: חובה להזין גיל!", Toast.LENGTH_LONG).show();
            return false;
        }
        if (!age.matches(numberRegex)) {
            Toast.makeText(this, "שגיאה: גיל חייב להיות מספר בלבד!", Toast.LENGTH_LONG).show();
            return false;
        }

        int ageValue = Integer.parseInt(age);
        if (ageValue < 18) {
            Toast.makeText(this, "שגיאה: גיל חייב להיות 18 ומעלה!", Toast.LENGTH_LONG).show();
            return false;
        }

        Toast.makeText(this, "כל הפרטים תקינים!", Toast.LENGTH_LONG).show();
        return true;
    }
}