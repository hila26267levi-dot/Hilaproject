package com.hila.myapplication.screens;

import android.annotation.SuppressLint;
import android.content.Intent;
import android.content.SharedPreferences;
import android.net.Uri;
import android.os.Bundle;
import android.util.Log;
import android.view.View;
import android.widget.AdapterView;
import android.widget.Button;
import android.widget.EditText;
import android.widget.ImageButton;
import android.widget.ImageView;
import android.widget.Spinner;
import android.widget.Toast;

import androidx.activity.EdgeToEdge;
import androidx.appcompat.app.AppCompatActivity;

import com.hila.myapplication.R;
import com.hila.myapplication.adapters.ImageUtil;
import com.hila.myapplication.model.Teacher;
import com.hila.myapplication.servicses.DatabaseService;

public class RegisterTeacherActivity extends AppCompatActivity
        implements View.OnClickListener, AdapterView.OnItemSelectedListener {

    private static final String TAG = "RegisterActivity";

    private Button btnteacher;
    DatabaseService databaseService;

    EditText edittext_email, edittext_fname, edittext_lname, edittext_phone,
            edittext_price, edittext_password, edittext_subject, edittext_age;

    Spinner spZoom, spteachclass, spsubject;

    ImageButton imgCamera;   // add - לחיץ, פותח גלריה
    ImageView imgDisplay;    // add2 - עיצוב בלבד

    String subject    = "";
    String zoom       = "";
    String imageBase64 = "";
    private String password, email;
    int SELECT_PICTURE = 200;

    @SuppressLint({"WrongViewCast", "MissingInflatedId"})
    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        EdgeToEdge.enable(this);
        setContentView(R.layout.activity_teacher_register);

        ImageUtil.requestPermission(this);

        edittext_email    = findViewById(R.id.et_teacher_email);
        edittext_fname    = findViewById(R.id.et_teacher_fname);
        edittext_lname    = findViewById(R.id.et_teacher_lname);
        edittext_phone    = findViewById(R.id.et_teacher_phone);
        edittext_price    = findViewById(R.id.et_teacher_price);
        edittext_password = findViewById(R.id.et_teacher_password);
        spteachclass      = findViewById(R.id.et_teacher_teach_class);
        edittext_age      = findViewById(R.id.et_teacher_age);
        spsubject         = findViewById(R.id.spSubject);
        edittext_subject  = findViewById(R.id.etSubjects);

        imgCamera  = findViewById(R.id.imageView8);  // add - לחיץ
        imgDisplay = findViewById(R.id.imageView9);  // add2 - עיצוב

        imgCamera.setOnClickListener(this);

        spZoom = findViewById(R.id.spZoom);
        spZoom.setOnItemSelectedListener(new AdapterView.OnItemSelectedListener() {
            @Override
            public void onItemSelected(AdapterView<?> parent, View view, int position, long id) {
                if (position == 1) zoom = "כן";
                else if (position == 2) zoom = "לא";
                else zoom = ""; // לא נבחר
            }
            @Override
            public void onNothingSelected(AdapterView<?> parent) {}
        });

        databaseService = DatabaseService.getInstance();
        btnteacher = findViewById(R.id.btn_register);
        btnteacher.setOnClickListener(this);
        spsubject.setOnItemSelectedListener(this);
    }

    @Override
    public void onClick(View v) {
        if (v == imgCamera) {
            imageChooser();
            return;
        }

        if (v.getId() == btnteacher.getId()) {
            Log.d(TAG, "onClick: Register button clicked");

            email         = edittext_email.getText().toString().trim();
            password      = edittext_password.getText().toString().trim();
            String fName  = edittext_fname.getText().toString().trim();
            String lName  = edittext_lname.getText().toString().trim();
            String age    = edittext_age.getText().toString().trim();
            String phone  = edittext_phone.getText().toString().trim();
            String stprice= edittext_price.getText().toString().trim();
            String teachclass = spteachclass.getSelectedItem().toString();
            String subject2 = edittext_subject.getText().toString().trim();

            if (!validateInput(phone, fName, lName, email, password, stprice, age, teachclass, subject2, zoom))
                return;

            double price = Double.parseDouble(stprice);
            Teacher teacher = new Teacher("99", fName, lName, phone, email, password,
                    age, subject2, price, zoom, teachclass);
            teacher.setPic(imageBase64);
            createUserInDatabase(teacher);
        }
    }

    void imageChooser() {
        Intent i = new Intent();
        i.setType("image/*");
        i.setAction(Intent.ACTION_GET_CONTENT);
        startActivityForResult(Intent.createChooser(i, "בחר תמונה"), SELECT_PICTURE);
    }

    @Override
    public void onActivityResult(int requestCode, int resultCode, Intent data) {
        super.onActivityResult(requestCode, resultCode, data);
        if (resultCode == RESULT_OK && requestCode == SELECT_PICTURE && data != null) {
            Uri selectedImageUri = data.getData();
            if (selectedImageUri != null) {
                imgDisplay.setImageURI(selectedImageUri);
                imgDisplay.post(() -> {
                    String base64 = ImageUtil.convertTo64Base(imgDisplay);
                    if (base64 != null) imageBase64 = base64;
                });
            }
        }
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
                Intent mainIntent = new Intent(RegisterTeacherActivity.this, TeacherActivity.class);
                mainIntent.setFlags(Intent.FLAG_ACTIVITY_NEW_TASK | Intent.FLAG_ACTIVITY_CLEAR_TASK);
                startActivity(mainIntent);
            }
            @Override
            public void onFailed(Exception e) {
                Log.e(TAG, "createUserInDatabase: Failed to create user", e);
                Toast.makeText(RegisterTeacherActivity.this, "הרשמה נכשלה", Toast.LENGTH_SHORT).show();
            }
        });
    }

    @Override
    public void onItemSelected(AdapterView<?> parent, View view, int position, long id) {
        if (position > 0) {
            String subj = (String) parent.getItemAtPosition(position);
            if (!subject.contains(subj)) {
                subject += subj + ", ";
                edittext_subject.setText(subject);
            }
        }
    }

    @Override
    public void onNothingSelected(AdapterView<?> parent) {}

    public boolean validateInput(String phone, String fName, String lName,
                                 String email, String password, String stprice,
                                 String age, String teachclass, String subject2, String zoom) {

        String phoneRegex  = "^05[0-9]{8}$";
        String nameRegex   = "^[A-Za-zא-ת]+$";
        String emailRegex  = "^[a-zA-Z0-9._%+-]+@gmail\\.com$";
        String numberRegex = "^[0-9]+$";

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

        // טלפון - חייב להתחיל ב-05 ולהכיל 10 ספרות
        if (phone.isEmpty()) {
            Toast.makeText(this, "שגיאה: חובה להזין מספר טלפון!", Toast.LENGTH_LONG).show();
            return false;
        }
        if (!phone.matches(phoneRegex)) {
            Toast.makeText(this, "שגיאה: מספר טלפון חייב להתחיל ב-05 ולהכיל 10 ספרות!", Toast.LENGTH_LONG).show();
            return false;
        }

        // כיתות לימוד
        if (teachclass.isEmpty() || teachclass.equals("בחר כיתות לימוד")) {
            Toast.makeText(this, "שגיאה: חובה לבחור כיתות לימוד!", Toast.LENGTH_LONG).show();
            return false;
        }

        // מקצועות
        if (subject2.isEmpty()) {
            Toast.makeText(this, "שגיאה: חובה לבחור לפחות מקצוע אחד!", Toast.LENGTH_LONG).show();
            return false;
        }

        // גיל - חייב להיות מספר וגדול מ-18
        if (age.isEmpty()) {
            Toast.makeText(this, "שגיאה: חובה להזין גיל!", Toast.LENGTH_LONG).show();
            return false;
        }
        if (!age.matches(numberRegex)) {
            Toast.makeText(this, "שגיאה: גיל חייב להיות מספר בלבד!", Toast.LENGTH_LONG).show();
            return false;
        }
        if (Integer.parseInt(age) < 18) {
            Toast.makeText(this, "שגיאה: מורה חייב להיות מעל גיל 18!", Toast.LENGTH_LONG).show();
            return false;
        }

        // מחיר - חייב להיות מספר חיובי
        if (stprice.isEmpty()) {
            Toast.makeText(this, "שגיאה: חובה להזין מחיר!", Toast.LENGTH_LONG).show();
            return false;
        }
        if (!stprice.matches(numberRegex)) {
            Toast.makeText(this, "שגיאה: מחיר חייב להיות מספר בלבד!", Toast.LENGTH_LONG).show();
            return false;
        }
        if (Double.parseDouble(stprice) <= 0) {
            Toast.makeText(this, "שגיאה: מחיר חייב להיות מספר חיובי!", Toast.LENGTH_LONG).show();
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
            Toast.makeText(this, "שגיאה: סיסמה חייבת להכיל לפחות 6 תווים!", Toast.LENGTH_LONG).show();
            return false;
        }

        // זום - חובה לבחור
        if (zoom.isEmpty()) {
            Toast.makeText(this, "שגיאה: חובה לבחור אם השיעור בזום או לא!", Toast.LENGTH_LONG).show();
            return false;
        }

        Toast.makeText(this, "כל הפרטים תקינים!", Toast.LENGTH_LONG).show();
        return true;
    }
}