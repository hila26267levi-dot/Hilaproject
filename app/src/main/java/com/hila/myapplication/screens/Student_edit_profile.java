package com.hila.myapplication.screens;

import android.content.Intent;
import android.graphics.Bitmap;
import android.net.Uri;
import android.os.Bundle;
import android.view.Menu;
import android.view.MenuItem;
import android.view.View;
import android.widget.ArrayAdapter;
import android.widget.Button;
import android.widget.EditText;
import android.widget.ImageView;
import android.widget.Spinner;
import android.widget.Toast;

import androidx.activity.EdgeToEdge;
import androidx.activity.result.ActivityResultLauncher;
import androidx.activity.result.contract.ActivityResultContracts;
import androidx.appcompat.app.AppCompatActivity;
import androidx.core.graphics.Insets;
import androidx.core.view.ViewCompat;
import androidx.core.view.WindowInsetsCompat;

import com.google.firebase.auth.FirebaseAuth;
import com.hila.myapplication.R;
import com.hila.myapplication.adapters.ImageUtil;
import com.hila.myapplication.model.Student;
import com.hila.myapplication.servicses.DatabaseService;

public class Student_edit_profile extends AppCompatActivity implements View.OnClickListener {

    EditText etfname, etlname, etphone;
    Spinner spkita; // *** שונה מ-EditText ל-Spinner ***
    Button btn_save;
    Student currentStudent;
    String uid;
    String kita, fname, lname, phone;
    private DatabaseService databaseService;

    private ActivityResultLauncher<Intent> captureImageLauncher;
    int SELECT_PICTURE = 200;
    private ImageView img;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        EdgeToEdge.enable(this);
        setContentView(R.layout.activity_student_edit_profile);
        ViewCompat.setOnApplyWindowInsetsListener(findViewById(R.id.main), (v, insets) -> {
            Insets systemBars = insets.getInsets(WindowInsetsCompat.Type.systemBars());
            v.setPadding(systemBars.left, systemBars.top, systemBars.right, systemBars.bottom);
            return insets;
        });

        ImageUtil.requestPermission(this);
        databaseService = DatabaseService.getInstance();

        captureImageLauncher = registerForActivityResult(
                new ActivityResultContracts.StartActivityForResult(),
                result -> {
                    if (result.getResultCode() == RESULT_OK && result.getData() != null) {
                        Bitmap bitmap = (Bitmap) result.getData().getExtras().get("data");
                        img.setImageBitmap(bitmap);
                    }
                });

        etfname  = findViewById(R.id.profile_student_E_Fname);
        etlname  = findViewById(R.id.profile_student_E_Lname);
        etphone  = findViewById(R.id.profile_student_E_phone);
        spkita   = findViewById(R.id.spstudent_kita); // *** ספינר במקום EditText ***
        btn_save = findViewById(R.id.btnSaveprofile_student);
        img      = findViewById(R.id.img_StudentProfile);

        btn_save.setOnClickListener(this);
        img.setOnClickListener(this);

        if (FirebaseAuth.getInstance().getCurrentUser() == null) {
            Toast.makeText(this, "שגיאה: משתמש לא מחובר", Toast.LENGTH_SHORT).show();
            startActivity(new Intent(this, loginActivity.class));
            finish();
            return;
        }

        uid = FirebaseAuth.getInstance().getCurrentUser().getUid();

        databaseService.getStudent(uid, new DatabaseService.DatabaseCallback<Student>() {
            @Override
            public void onCompleted(Student student) {
                currentStudent = student;
                if (student != null) {
                    loadStudentData();
                } else {
                    Toast.makeText(Student_edit_profile.this,
                            "לא נמצאו פרטי תלמיד", Toast.LENGTH_SHORT).show();
                }
            }

            @Override
            public void onFailed(Exception e) {
                Toast.makeText(Student_edit_profile.this,
                        "שגיאה בטעינת הפרטים", Toast.LENGTH_SHORT).show();
            }
        });
    }

    private void loadStudentData() {
        if (currentStudent == null) return;

        etfname.setText(currentStudent.getFname());
        etlname.setText(currentStudent.getLname());
        etphone.setText(currentStudent.getPhone());

        // *** טעינת הכיתה הקיימת לתוך הספינר ***
        // הספינר כבר מחובר ל-@array/my_items דרך ה-XML,
        // אז רק צריך לבחור את הפריט המתאים
        if (currentStudent.getKita() != null) {
            ArrayAdapter<CharSequence> adapter = ArrayAdapter.createFromResource(
                    this, R.array.my_items, android.R.layout.simple_spinner_item);
            adapter.setDropDownViewResource(android.R.layout.simple_spinner_dropdown_item);
            spkita.setAdapter(adapter);

            int pos = adapter.getPosition(currentStudent.getKita());
            if (pos >= 0) spkita.setSelection(pos);
        }

        if (currentStudent.getPic() != null) {
            img.setImageBitmap(ImageUtil.convertFromivIPic(currentStudent.getPic()));
        }
    }

    private void saveStudentProfile() {
        if (currentStudent == null) {
            Toast.makeText(this, "שגיאה: לא נטענו פרטי תלמיד", Toast.LENGTH_SHORT).show();
            return;
        }

        fname = etfname.getText().toString().trim();
        lname = etlname.getText().toString().trim();
        phone = etphone.getText().toString().trim();
        // *** קריאת הכיתה מהספינר ***
        kita  = spkita.getSelectedItem() != null ? spkita.getSelectedItem().toString() : "";

        // בדיקות תקינות — אם נכשל, עוצרים כאן
        if (!validateInput(fname, lname, phone, kita)) return;

        currentStudent.setFname(fname);
        currentStudent.setLname(lname);
        currentStudent.setPhone(phone);
        currentStudent.setKita(kita);

        // שמירת תמונה אם שונתה
        String pic = ImageUtil.convertTo64Base(img);
        if (pic != null) {
            currentStudent.setPic(pic);
        }

        databaseService.updateStudent(currentStudent, new DatabaseService.DatabaseCallback<Void>() {
            @Override
            public void onCompleted(Void object) {
                Toast.makeText(Student_edit_profile.this,
                        "הפרופיל עודכן בהצלחה!", Toast.LENGTH_SHORT).show();
                startActivity(new Intent(Student_edit_profile.this, StudentActivity.class));
                finish();
            }

            @Override
            public void onFailed(Exception e) {
                Toast.makeText(Student_edit_profile.this,
                        "שגיאה בשמירת הפרופיל, נסה שוב", Toast.LENGTH_SHORT).show();
            }
        });
    }

    // בדיקות תקינות — זהות לדף ההרשמה RegisterStudentActivity
    public boolean validateInput(String fnameVal, String lnameVal, String phoneVal, String kitaVal) {

        String nameRegex = "^[A-Za-zא-ת]+$";

        // שם פרטי
        if (fnameVal.isEmpty()) {
            Toast.makeText(this, "שגיאה: חובה להזין שם פרטי!", Toast.LENGTH_LONG).show();
            return false;
        }
        if (!fnameVal.matches(nameRegex)) {
            Toast.makeText(this, "שגיאה: שם פרטי חייב להכיל אותיות בלבד (עברית או אנגלית)!", Toast.LENGTH_LONG).show();
            return false;
        }

        // שם משפחה
        if (lnameVal.isEmpty()) {
            Toast.makeText(this, "שגיאה: חובה להזין שם משפחה!", Toast.LENGTH_LONG).show();
            return false;
        }
        if (!lnameVal.matches(nameRegex)) {
            Toast.makeText(this, "שגיאה: שם משפחה חייב להכיל אותיות בלבד (עברית או אנגלית)!", Toast.LENGTH_LONG).show();
            return false;
        }

        // טלפון — זהה לדף ההרשמה: מתחיל ב-05, בדיוק 10 ספרות
        if (phoneVal.isEmpty()) {
            Toast.makeText(this, "שגיאה: חובה להזין מספר טלפון!", Toast.LENGTH_LONG).show();
            return false;
        }
        if (!phoneVal.matches("^05[0-9]{8}$")) {
            Toast.makeText(this, "שגיאה: מספר הטלפון חייב להתחיל ב-05 ולהכיל בדיוק 10 ספרות!", Toast.LENGTH_LONG).show();
            return false;
        }

        // *** כיתה — חובה לבחור מהספינר, לא להשאיר על "בחר כיתה" ***
        if (kitaVal.isEmpty() || kitaVal.equals("בחר כיתה")) {
            Toast.makeText(this, "שגיאה: חובה לבחור כיתה!", Toast.LENGTH_LONG).show();
            return false;
        }

        return true;
    }

    @Override
    public void onClick(View v) {
        if (v == btn_save) {
            saveStudentProfile();
        } else if (v == img) {
            imageChooser();
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
        if (resultCode == RESULT_OK && requestCode == SELECT_PICTURE) {
            Uri selectedImageUri = data.getData();
            if (selectedImageUri != null) {
                img.setImageURI(selectedImageUri);
            }
        }
    }

    @Override
    public boolean onCreateOptionsMenu(Menu menu) {
        getMenuInflater().inflate(R.menu.student_menu, menu);
        return true;
    }

    @Override
    public boolean onOptionsItemSelected(MenuItem item) {
        int id = item.getItemId();
        if (id == R.id.student_home) {
            startActivity(new Intent(this, StudentActivity.class));
            return true;
        }
        if (id == R.id.student_searchteacher) {
            startActivity(new Intent(this, TeacherListActivity.class));
            return true;
        }
        if (id == R.id.student_profile) {
            startActivity(new Intent(this, StudentProfile.class));
            return true;
        }
        if (id == R.id.student_disconect) {
            startActivity(new Intent(this, disconect_forstudent.class));
            return true;
        }
        if (id == R.id.student_mylesson) {
            startActivity(new Intent(this, student_lesson_list.class));
            return true;
        }
        if (id == R.id.student_adut) {
            startActivity(new Intent(this, AdutActivity.class));
            return true;
        }
        return super.onOptionsItemSelected(item);
    }
}



