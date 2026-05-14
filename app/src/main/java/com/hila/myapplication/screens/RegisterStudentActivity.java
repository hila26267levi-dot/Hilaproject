package com.hila.myapplication.screens;

import android.content.Intent;
import android.content.SharedPreferences;
import android.graphics.Bitmap;
import android.net.Uri;
import android.os.Bundle;
import android.provider.MediaStore;
import android.util.Log;
import android.view.View;
import android.widget.Button;
import android.widget.EditText;
import android.widget.ImageButton;
import android.widget.ImageView;
import android.widget.Spinner;
import android.widget.Toast;

import androidx.activity.EdgeToEdge;
import androidx.activity.result.ActivityResultLauncher;
import androidx.activity.result.contract.ActivityResultContracts;
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

    ImageButton img_Camera;   // כפתור מצלמה
    ImageButton img_gallery;  // כפתור גלריה
    ImageView IvR_student;    // ImageView להצגת התמונה שנבחרה

    // launchers - בדיוק כמו ב-Student_edit_profile
    private ActivityResultLauncher<Intent> selectImageLauncher;
    private ActivityResultLauncher<Intent> captureImageLauncher;

    String imageBase64 = "";

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        EdgeToEdge.enable(this);
        setContentView(R.layout.activity_student_register);

        ImageUtil.requestPermission(this);

        // חיבור Views
        edittext_email    = findViewById(R.id.et_student_email);
        edittext_fname    = findViewById(R.id.et_student_fname);
        edittext_lname    = findViewById(R.id.et_student_lname);
        edittext_password = findViewById(R.id.et_student_password);
        spKita            = findViewById(R.id.spstudent_kita);
        edittext_phone    = findViewById(R.id.et_student_phone);
        img_Camera        = findViewById(R.id.student_register_camara);
        img_gallery       = findViewById(R.id.student_register_gallery);
        IvR_student       = findViewById(R.id.iv_Rstudent);

        // הגדרת Launchers - בדיוק כמו ב-Student_edit_profile
        setUpLaunchers();

        img_Camera.setOnClickListener(this);
        img_gallery.setOnClickListener(this);

        databaseService = DatabaseService.getInstance();
        btn_student = findViewById(R.id.btn_register);
        btn_student.setOnClickListener(this);
    }

    // בדיוק כמו setUpGallery ב-Student_edit_profile
    private void setUpLaunchers() {
        // גלריה
        selectImageLauncher = registerForActivityResult(
                new ActivityResultContracts.StartActivityForResult(),
                result -> {
                    if (result.getResultCode() == RESULT_OK && result.getData() != null) {
                        Uri selectedImage = result.getData().getData();
                        IvR_student.setImageURI(selectedImage);
                        IvR_student.setTag(null);
                        // שמירת base64
                        IvR_student.post(() -> {
                            String base64 = ImageUtil.convertTo64Base(IvR_student);
                            if (base64 != null) imageBase64 = base64;
                        });
                    }
                });

        // מצלמה
        captureImageLauncher = registerForActivityResult(
                new ActivityResultContracts.StartActivityForResult(),
                result -> {
                    if (result.getResultCode() == RESULT_OK && result.getData() != null) {
                        Bitmap bitmap = (Bitmap) result.getData().getExtras().get("data");
                        IvR_student.setImageBitmap(bitmap);
                        IvR_student.setTag(null);
                        // שמירת base64
                        IvR_student.post(() -> {
                            String base64 = ImageUtil.convertTo64Base(IvR_student);
                            if (base64 != null) imageBase64 = base64;
                        });
                    }
                });
    }

    @Override
    public void onClick(View v) {
        if (v == img_gallery) {
            // פתיחת גלריה
            ImageUtil.requestPermission(this);
            Intent i = new Intent();
            i.setType("image/*");
            i.setAction(Intent.ACTION_GET_CONTENT);
            selectImageLauncher.launch(Intent.createChooser(i, "בחר תמונה"));
            return;
        }

        if (v == img_Camera) {
            // פתיחת מצלמה
            ImageUtil.requestPermission(this);
            Intent takePictureIntent = new Intent(MediaStore.ACTION_IMAGE_CAPTURE);
            captureImageLauncher.launch(takePictureIntent);
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

        if (fName.isEmpty())           { Toast.makeText(this, "שגיאה: חובה להזין שם פרטי!", Toast.LENGTH_LONG).show(); return false; }
        if (!fName.matches(nameRegex)) { Toast.makeText(this, "שגיאה: שם פרטי חייב להכיל אותיות בלבד!", Toast.LENGTH_LONG).show(); return false; }
        if (lName.isEmpty())           { Toast.makeText(this, "שגיאה: חובה להזין שם משפחה!", Toast.LENGTH_LONG).show(); return false; }
        if (!lName.matches(nameRegex)) { Toast.makeText(this, "שגיאה: שם משפחה חייב להכיל אותיות בלבד!", Toast.LENGTH_LONG).show(); return false; }
        if (phone.isEmpty())           { Toast.makeText(this, "שגיאה: חובה להזין מספר טלפון!", Toast.LENGTH_LONG).show(); return false; }
        if (!phone.matches("^05[0-9]{8}$")) { Toast.makeText(this, "שגיאה: מספר טלפון חייב להתחיל ב-05 ולהכיל 10 ספרות!", Toast.LENGTH_LONG).show(); return false; }
        if (kita.isEmpty() || kita.equals("בחר כיתה")) { Toast.makeText(this, "שגיאה: חובה לבחור כיתה!", Toast.LENGTH_LONG).show(); return false; }
        if (email.isEmpty())           { Toast.makeText(this, "שגיאה: חובה להזין אימייל!", Toast.LENGTH_LONG).show(); return false; }
        if (!email.matches("^[a-zA-Z0-9._%+-]+@gmail\\.com$")) { Toast.makeText(this, "שגיאה: יש להזין כתובת Gmail תקינה!", Toast.LENGTH_LONG).show(); return false; }
        if (password.isEmpty())        { Toast.makeText(this, "שגיאה: חובה להזין סיסמה!", Toast.LENGTH_LONG).show(); return false; }
        if (password.length() < 6)     { Toast.makeText(this, "שגיאה: סיסמה חייבת להכיל לפחות 6 תווים!", Toast.LENGTH_LONG).show(); return false; }

        return true;
    }
}