package com.hila.myapplication.screens;

import android.content.Intent;
import android.content.SharedPreferences;
import android.net.Uri;
import android.os.Bundle;
import android.util.Log;
import android.view.View;
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
import com.hila.myapplication.model.Student;
import com.hila.myapplication.servicses.DatabaseService;

public class RegisterStudentActivity extends AppCompatActivity implements View.OnClickListener {
    private static final String TAG = "RegisterActivity";

    private Button btn_student;
    DatabaseService databaseService;
    EditText edittext_email, edittext_fname, edittext_lname, edittext_password, edittext_phone;
    Spinner spKita;

    ImageButton imgCamera;  // add - לחיץ, פותח גלריה
    ImageView imgDisplay;   // add2 - עיצוב בלבד

    String imageBase64 = "";
    int SELECT_PICTURE = 200;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        EdgeToEdge.enable(this);
        setContentView(R.layout.activity_student_register);

        ImageUtil.requestPermission(this);

        edittext_email    = findViewById(R.id.et_student_email);
        edittext_fname    = findViewById(R.id.et_student_fname);
        edittext_lname    = findViewById(R.id.et_student_lname);
        edittext_password = findViewById(R.id.et_student_password);
        spKita            = findViewById(R.id.spstudent_kita);
        edittext_phone    = findViewById(R.id.et_student_phone);
        imgCamera         = findViewById(R.id.student2);   // add - לחיץ
        imgDisplay        = findViewById(R.id.profile);    // add2 - עיצוב בלבד

        imgCamera.setOnClickListener(this);

        databaseService = DatabaseService.getInstance();
        btn_student = findViewById(R.id.btn_register);
        btn_student.setOnClickListener(this);
    }

    @Override
    public void onClick(View v) {
        if (v == imgCamera) {
            imageChooser();
            return;
        }

        if (v.getId() == btn_student.getId()) {
            String email    = edittext_email.getText().toString().trim();
            String password = edittext_password.getText().toString().trim();
            String fName    = edittext_fname.getText().toString().trim();
            String lName    = edittext_lname.getText().toString().trim();
            String kita     = spKita.getSelectedItem().toString();
            String phone    = edittext_phone.getText().toString().trim();

            if (validateInput(phone, fName, lName, email, password, kita)) {
                registerUser(fName, lName, email, password, kita, phone);
            }
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

    private void registerUser(String fname, String lname, String email,
                              String password, String kita, String phone) {
        Student student = new Student("99", fname, lname, phone, email, password, imageBase64, kita);
        createUserInDatabase(student);
    }

    private void createUserInDatabase(Student student) {
        databaseService.createNewStudent(student, new DatabaseService.DatabaseCallback<String>() {
            @Override
            public void onCompleted(String uid) {
                student.setId(uid);
                SharedPreferences sharedPreferences = getSharedPreferences("myPrefs", MODE_PRIVATE);
                SharedPreferences.Editor editor = sharedPreferences.edit();
                editor.putString("email", student.getEmail());
                editor.putString("password", student.getPassword());
                editor.apply();
                Intent mainIntent = new Intent(RegisterStudentActivity.this, StudentActivity.class);
                mainIntent.setFlags(Intent.FLAG_ACTIVITY_NEW_TASK | Intent.FLAG_ACTIVITY_CLEAR_TASK);
                startActivity(mainIntent);
            }

            @Override
            public void onFailed(Exception e) {
                Toast.makeText(RegisterStudentActivity.this, "הרשמה נכשלה, אנא נסה שוב", Toast.LENGTH_SHORT).show();
            }
        });
    }

    public boolean validateInput(String phone, String fName, String lName,
                                 String email, String password, String kita) {
        String nameRegex = "^[A-Za-zא-ת]+$";

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
        if (!phone.matches("^05[0-9]{8}$")) {
            Toast.makeText(this, "שגיאה: מספר טלפון חייב להתחיל ב-05 ולהכיל 10 ספרות!", Toast.LENGTH_LONG).show();
            return false;
        }

        // כיתה
        if (kita.isEmpty() || kita.equals("בחר כיתה")) {
            Toast.makeText(this, "שגיאה: חובה לבחור כיתה!", Toast.LENGTH_LONG).show();
            return false;
        }

        // אימייל
        if (email.isEmpty()) {
            Toast.makeText(this, "שגיאה: חובה להזין אימייל!", Toast.LENGTH_LONG).show();
            return false;
        }
        if (!email.matches("^[a-zA-Z0-9._%+-]+@gmail\\.com$")) {
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

        return true;
    }
}