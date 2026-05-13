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
import android.widget.ImageButton;
import android.widget.Spinner;
import android.widget.Toast;

import androidx.activity.EdgeToEdge;
import androidx.appcompat.app.AppCompatActivity;

import com.hila.myapplication.R;
import com.hila.myapplication.model.Teacher;
import com.hila.myapplication.servicses.DatabaseService;
import com.hila.myapplication.utils.ImageHelper;

public class RegisterTeacherActivity extends AppCompatActivity implements View.OnClickListener, AdapterView.OnItemSelectedListener {
    private static final String TAG = "RegisterActivity";

    private Button btnteacher;
    DatabaseService databaseService;
    EditText edittext_email, edittext_fname, edittext_lname, edittext_phone, edittext_price,
            edittext_password, edittext_subject, edittext_age;
    Spinner spZoom, spteachclass, spsubject;
    ImageButton profileImageButton;
    String subject = "";
    String zoom = "לא";
    String imageBase64 = "";
    private String password, email;

    @SuppressLint({"WrongViewCast", "MissingInflatedId"})
    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        EdgeToEdge.enable(this);
        setContentView(R.layout.activity_teacher_register);

        edittext_email = findViewById(R.id.et_teacher_email);
        edittext_fname = findViewById(R.id.et_teacher_fname);
        edittext_lname = findViewById(R.id.et_teacher_lname);
        edittext_phone = findViewById(R.id.et_teacher_phone);
        edittext_price = findViewById(R.id.et_teacher_price);
        edittext_password = findViewById(R.id.et_teacher_password);
        spteachclass = findViewById(R.id.et_teacher_teach_class);
        edittext_age = findViewById(R.id.et_teacher_age);
        spsubject = findViewById(R.id.spSubject);
        edittext_subject = findViewById(R.id.etSubjects);
        profileImageButton = findViewById(R.id.imageView9);

        profileImageButton.setOnClickListener(v -> ImageHelper.openGallery(this));

        spZoom = findViewById(R.id.spZoom);
        spZoom.setOnItemSelectedListener(new AdapterView.OnItemSelectedListener() {
            @Override
            public void onItemSelected(AdapterView<?> parent, View view, int position, long id) {
                if (position == 1) zoom = "כן";
                else if (position == 2) zoom = "לא";
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
    protected void onActivityResult(int requestCode, int resultCode, Intent data) {
        super.onActivityResult(requestCode, resultCode, data);
        String result = ImageHelper.handleActivityResult(this, requestCode, resultCode, data);
        if (result != null) {
            imageBase64 = result;
            profileImageButton.setImageBitmap(ImageHelper.base64ToBitmap(result));
        }
    }

    @Override
    public void onClick(View v) {
        if (v.getId() == btnteacher.getId()) {
            Log.d(TAG, "onClick: Register button clicked");

            email = edittext_email.getText().toString();
            password = edittext_password.getText().toString();
            String fName = edittext_fname.getText().toString();
            String lName = edittext_lname.getText().toString();
            String age = edittext_age.getText().toString();
            String phone = edittext_phone.getText().toString();
            String stprice = edittext_price.getText().toString();
            String teachclass = spteachclass.getSelectedItem().toString();
            String subject2 = edittext_subject.getText().toString();

            if (!validateInput(phone, fName, lName, email, password, stprice, age)) return;

            double price = Double.parseDouble(stprice);
            Teacher teacher = new Teacher("99", fName, lName, phone, email, password, age, subject2, price, zoom, teachclass);
            teacher.setImage(imageBase64);
            createUserInDatabase(teacher);
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
    public void onNothingSelected(AdapterView<?> parent) {}

    public boolean validateInput(String phone, String fName, String lName,
                                 String email, String password, String stprice, String age) {
        String phoneRegex = "^[0-9]{10}$";
        String nameRegex = "^[A-Za-zא-ת]+$";
        String emailRegex = "^[a-zA-Z0-9._%+-]+@gmail\\.com$";
        String numberRegex = "^[0-9]+$";
        if (phone.isEmpty()) { Toast.makeText(this, "שגיאה: חובה להזין מספר טלפון!", Toast.LENGTH_LONG).show(); return false; }
        if (!phone.matches(phoneRegex)) { Toast.makeText(this, "שגיאה: טלפון חייב להיות 10 ספרות!", Toast.LENGTH_LONG).show(); return false; }
        if (fName.isEmpty()) { Toast.makeText(this, "שגיאה: חובה להזין שם פרטי!", Toast.LENGTH_LONG).show(); return false; }
        if (!fName.matches(nameRegex)) { Toast.makeText(this, "שגיאה: שם פרטי חייב להכיל אותיות בלבד!", Toast.LENGTH_LONG).show(); return false; }
        if (lName.isEmpty()) { Toast.makeText(this, "שגיאה: חובה להזין שם משפחה!", Toast.LENGTH_LONG).show(); return false; }
        if (!lName.matches(nameRegex)) { Toast.makeText(this, "שגיאה: שם משפחה חייב להכיל אותיות בלבד!", Toast.LENGTH_LONG).show(); return false; }
        if (email.isEmpty()) { Toast.makeText(this, "שגיאה: חובה להזין אימייל!", Toast.LENGTH_LONG).show(); return false; }
        if (!email.matches(emailRegex)) { Toast.makeText(this, "שגיאה: יש להזין Gmail תקינה!", Toast.LENGTH_LONG).show(); return false; }
        if (password.isEmpty()) { Toast.makeText(this, "שגיאה: חובה להזין סיסמה!", Toast.LENGTH_LONG).show(); return false; }
        if (password.length() < 6) { Toast.makeText(this, "שגיאה: סיסמה חייבת להכיל לפחות 6 תווים!", Toast.LENGTH_LONG).show(); return false; }
        if (stprice.isEmpty()) { Toast.makeText(this, "שגיאה: חובה להזין מחיר!", Toast.LENGTH_LONG).show(); return false; }
        if (!stprice.matches(numberRegex)) { Toast.makeText(this, "שגיאה: מחיר חייב להיות מספר!", Toast.LENGTH_LONG).show(); return false; }
        if (age.isEmpty()) { Toast.makeText(this, "שגיאה: חובה להזין גיל!", Toast.LENGTH_LONG).show(); return false; }
        if (!age.matches(numberRegex)) { Toast.makeText(this, "שגיאה: גיל חייב להיות מספר!", Toast.LENGTH_LONG).show(); return false; }
        if (Integer.parseInt(age) < 10) { Toast.makeText(this, "שגיאה: גיל חייב להיות 10 ומעלה!", Toast.LENGTH_LONG).show(); return false; }
        Toast.makeText(this, "כל הפרטים תקינים!", Toast.LENGTH_LONG).show();
        return true;
    }
}