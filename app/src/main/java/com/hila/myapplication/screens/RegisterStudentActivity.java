package com.hila.myapplication.screens;

import android.content.Intent;
import android.content.SharedPreferences;
import android.os.Bundle;
import android.util.Log;
import android.view.View;
import android.widget.Button;
import android.widget.EditText;
import android.widget.ImageButton;
import android.widget.Spinner;
import android.widget.Toast;

import androidx.activity.EdgeToEdge;
import androidx.appcompat.app.AppCompatActivity;

import com.hila.myapplication.R;
import com.hila.myapplication.model.Student;
import com.hila.myapplication.servicses.DatabaseService;
import com.hila.myapplication.utils.ImageHelper;

public class RegisterStudentActivity extends AppCompatActivity implements View.OnClickListener {
    private static final String TAG = "RegisterActivity";

    private Button btn_student;
    DatabaseService databaseService;
    EditText edittext_email, edittext_fname, edittext_lname, edittext_password, edittext_phone;
    Spinner spKita;
    ImageButton profileImageButton;
    String imageBase64 = "";

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        EdgeToEdge.enable(this);
        setContentView(R.layout.activity_student_register);

        edittext_email = findViewById(R.id.et_student_email);
        edittext_fname = findViewById(R.id.et_student_fname);
        edittext_lname = findViewById(R.id.et_student_lname);
        edittext_password = findViewById(R.id.et_student_password);
        spKita = findViewById(R.id.spstudent_kita);
        edittext_phone = findViewById(R.id.et_student_phone);
        profileImageButton = findViewById(R.id.profile);

        profileImageButton.setOnClickListener(v -> ImageHelper.openGallery(this));

        databaseService = DatabaseService.getInstance();
        btn_student = findViewById(R.id.btn_register);
        btn_student.setOnClickListener(this);
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
        if (v.getId() == btn_student.getId()) {
            Log.d(TAG, "onClick: Register button clicked");

            String email = edittext_email.getText().toString();
            String password = edittext_password.getText().toString();
            String fName = edittext_fname.getText().toString();
            String lName = edittext_lname.getText().toString();
            String kita = spKita.getSelectedItem().toString();
            String phone = edittext_phone.getText().toString();

            if (validateInput(phone, fName, lName, email, password, kita)) {
                registerUser(fName, lName, email, password, kita, phone);
            }
        }
    }

    private void registerUser(String fname, String lname, String email,
                              String password, String kita, String phone) {
        Log.d(TAG, "registerUser: Registering user...");
        Student student = new Student("99", fname, lname, phone, email, password, imageBase64, kita);
        Log.d(TAG, student.toString());
        createUserInDatabase(student);
    }

    private void createUserInDatabase(Student student) {
        databaseService.createNewStudent(student, new DatabaseService.DatabaseCallback<String>() {
            @Override
            public void onCompleted(String uid) {
                Log.d(TAG, "createUserInDatabase: User created successfully");
                student.setId(uid);
                SharedPreferences sharedPreferences = getSharedPreferences("myPrefs", MODE_PRIVATE);
                SharedPreferences.Editor editor = sharedPreferences.edit();
                editor.putString("email", student.getEmail());
                editor.putString("password", student.getPassword());
                editor.apply();
                Log.d(TAG, "createUserInDatabase: Redirecting to StudentActivity");
                Intent mainIntent = new Intent(RegisterStudentActivity.this, StudentActivity.class);
                mainIntent.setFlags(Intent.FLAG_ACTIVITY_NEW_TASK | Intent.FLAG_ACTIVITY_CLEAR_TASK);
                startActivity(mainIntent);
            }

            @Override
            public void onFailed(Exception e) {
                Log.e(TAG, "createUserInDatabase: Failed to create user", e);
                Toast.makeText(RegisterStudentActivity.this, "הרשמה נכשלה, אנא נסה שוב", Toast.LENGTH_SHORT).show();
            }
        });
    }

    public boolean validateInput(String phone, String fName, String lName,
                                 String email, String password, String kita) {
        String nameRegex = "^[A-Za-zא-ת]+$";
        if (fName.isEmpty()) { Toast.makeText(this, "שגיאה: חובה להזין שם פרטי!", Toast.LENGTH_LONG).show(); return false; }
        if (!fName.matches(nameRegex)) { Toast.makeText(this, "שגיאה: שם פרטי חייב להכיל אותיות בלבד!", Toast.LENGTH_LONG).show(); return false; }
        if (lName.isEmpty()) { Toast.makeText(this, "שגיאה: חובה להזין שם משפחה!", Toast.LENGTH_LONG).show(); return false; }
        if (!lName.matches(nameRegex)) { Toast.makeText(this, "שגיאה: שם משפחה חייב להכיל אותיות בלבד!", Toast.LENGTH_LONG).show(); return false; }
        if (phone.isEmpty()) { Toast.makeText(this, "שגיאה: חובה להזין מספר טלפון!", Toast.LENGTH_LONG).show(); return false; }
        if (!phone.matches("^05[0-9]{8}$")) { Toast.makeText(this, "שגיאה: טלפון חייב להתחיל ב-05 ולהכיל 10 ספרות!", Toast.LENGTH_LONG).show(); return false; }
        if (kita.isEmpty() || kita.equals("בחר כיתה")) { Toast.makeText(this, "שגיאה: חובה לבחור כיתה!", Toast.LENGTH_LONG).show(); return false; }
        if (email.isEmpty()) { Toast.makeText(this, "שגיאה: חובה להזין אימייל!", Toast.LENGTH_LONG).show(); return false; }
        if (!email.matches("^[a-zA-Z0-9._%+-]+@gmail\\.com$")) { Toast.makeText(this, "שגיאה: יש להזין Gmail תקינה!", Toast.LENGTH_LONG).show(); return false; }
        if (password.isEmpty()) { Toast.makeText(this, "שגיאה: חובה להזין סיסמה!", Toast.LENGTH_LONG).show(); return false; }
        if (password.length() < 6) { Toast.makeText(this, "שגיאה: סיסמה חייבת להכיל לפחות 6 תווים!", Toast.LENGTH_LONG).show(); return false; }
        return true;
    }
}