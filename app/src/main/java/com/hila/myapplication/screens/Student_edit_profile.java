package com.hila.myapplication.screens;

import android.content.Intent;
import android.graphics.Bitmap;
import android.net.Uri;
import android.os.Bundle;
import android.view.Menu;
import android.view.MenuItem;
import android.view.View;
import android.widget.Button;
import android.widget.EditText;
import android.widget.ImageButton;
import android.widget.ImageView;
import android.widget.Spinner;
import android.widget.Toast;

import androidx.activity.EdgeToEdge;
import androidx.appcompat.app.AppCompatActivity;

import com.google.firebase.auth.FirebaseAuth;
import com.hila.myapplication.R;
import com.hila.myapplication.adapters.ImageUtil;
import com.hila.myapplication.model.Student;
import com.hila.myapplication.servicses.DatabaseService;

public class Student_edit_profile extends AppCompatActivity implements View.OnClickListener {

    EditText etfname, etlname, etphone;
    Spinner spKita;
    Button btn_save;
    ImageButton imgCamera;  // add - לחיץ, פותח גלריה
    ImageView imgDisplay;   // add2 - עיצוב בלבד, לא לחיץ, מציג תמונה שנבחרה
    Student currentStudent;
    String uid;
    private DatabaseService databaseService;
    int SELECT_PICTURE = 200;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        EdgeToEdge.enable(this);
        setContentView(R.layout.activity_student_edit_profile);

        ImageUtil.requestPermission(this);
        databaseService = DatabaseService.getInstance();

        etfname    = findViewById(R.id.profile_student_E_Fname);
        etlname    = findViewById(R.id.profile_student_E_Lname);
        etphone    = findViewById(R.id.profile_student_E_phone);
        spKita     = findViewById(R.id.spstudent_kita);
        btn_save   = findViewById(R.id.btnSaveprofile_student);
        imgCamera  = findViewById(R.id.img_StudentProfile); // add - לחיץ
        imgDisplay = findViewById(R.id.add2);       // add2 - עיצוב בלבד

        btn_save.setOnClickListener(this);
        imgCamera.setOnClickListener(this); // רק add לחיץ

        uid = FirebaseAuth.getInstance().getCurrentUser().getUid();

        databaseService.getStudent(uid, new DatabaseService.DatabaseCallback<Student>() {
            @Override
            public void onCompleted(Student student) {
                currentStudent = student;
                loadStudentData();
            }
            @Override
            public void onFailed(Exception e) {
                Toast.makeText(Student_edit_profile.this, "שגיאה בטעינת נתונים", Toast.LENGTH_SHORT).show();
            }
        });
    }

    private void loadStudentData() {
        if (currentStudent == null) return;

        etfname.setText(currentStudent.getFname());
        etlname.setText(currentStudent.getLname());
        etphone.setText(currentStudent.getPhone());

        if (currentStudent.getKita() != null) {
            String[] kitaArray = getResources().getStringArray(R.array.my_items);
            for (int i = 0; i < kitaArray.length; i++) {
                if (kitaArray[i].equals(currentStudent.getKita())) {
                    spKita.setSelection(i);
                    break;
                }
            }
        }

        // תמונה קיימת מוצגת ב-imgDisplay (add2)
        if (currentStudent.getPic() != null && !currentStudent.getPic().isEmpty()
                && !currentStudent.getPic().equals("jjj")) {
            Bitmap bmp = ImageUtil.convertFromivIPic(currentStudent.getPic());
            if (bmp != null) {
                imgDisplay.setImageBitmap(bmp);
            }
        }
    }

    private void saveStudentProfile() {
        if (currentStudent == null) return;

        String fname = etfname.getText().toString().trim();
        String lname = etlname.getText().toString().trim();
        String phone = etphone.getText().toString().trim();
        String kita  = spKita.getSelectedItem().toString();

        String phoneRegex = "^05[0-9]{8}$";
        String nameRegex  = "^[A-Za-zא-ת]+$";

        if (fname.isEmpty())           { Toast.makeText(this, "שגיאה: חובה להזין שם פרטי!", Toast.LENGTH_LONG).show(); return; }
        if (!fname.matches(nameRegex)) { Toast.makeText(this, "שגיאה: שם פרטי חייב להכיל אותיות בלבד!", Toast.LENGTH_LONG).show(); return; }
        if (lname.isEmpty())           { Toast.makeText(this, "שגיאה: חובה להזין שם משפחה!", Toast.LENGTH_LONG).show(); return; }
        if (!lname.matches(nameRegex)) { Toast.makeText(this, "שגיאה: שם משפחה חייב להכיל אותיות בלבד!", Toast.LENGTH_LONG).show(); return; }
        if (phone.isEmpty())           { Toast.makeText(this, "שגיאה: חובה להזין מספר טלפון!", Toast.LENGTH_LONG).show(); return; }
        if (!phone.matches(phoneRegex)){ Toast.makeText(this, "שגיאה: מספר טלפון חייב להתחיל ב-05 ולהכיל 10 ספרות!", Toast.LENGTH_LONG).show(); return; }
        if (kita.equals("בחר כיתה"))  { Toast.makeText(this, "שגיאה: חובה לבחור כיתה!", Toast.LENGTH_LONG).show(); return; }

        currentStudent.setFname(fname);
        currentStudent.setLname(lname);
        currentStudent.setPhone(phone);
        currentStudent.setKita(kita);

        // שמירת תמונה מ-imgDisplay
        String picBase64 = ImageUtil.convertTo64Base(imgDisplay);
        if (picBase64 != null) {
            currentStudent.setPic(picBase64);
        }

        databaseService.updateStudent(currentStudent, new DatabaseService.DatabaseCallback<Void>() {
            @Override
            public void onCompleted(Void object) {
                Toast.makeText(Student_edit_profile.this, "הפרופיל נשמר בהצלחה!", Toast.LENGTH_SHORT).show();
                startActivity(new Intent(Student_edit_profile.this, StudentActivity.class));
            }
            @Override
            public void onFailed(Exception e) {
                Toast.makeText(Student_edit_profile.this, "שגיאה בשמירה", Toast.LENGTH_SHORT).show();
            }
        });
    }

    @Override
    public void onClick(View v) {
        if (v == btn_save) {
            saveStudentProfile();
        } else if (v == imgCamera) {
            // רק add פותח גלריה
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
        if (resultCode == RESULT_OK && requestCode == SELECT_PICTURE && data != null) {
            Uri selectedImageUri = data.getData();
            if (selectedImageUri != null) {
                // תמונה שנבחרה מוצגת ב-imgDisplay (add2) בלבד
                imgDisplay.setImageURI(selectedImageUri);
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
        if (id == R.id.student_home)         { startActivity(new Intent(this, StudentActivity.class)); return true; }
        if (id == R.id.student_searchteacher){ startActivity(new Intent(this, TeacherListActivity.class)); return true; }
        if (id == R.id.student_profile)      { startActivity(new Intent(this, StudentProfile.class)); return true; }
        if (id == R.id.student_disconect)    { startActivity(new Intent(this, disconect_forstudent.class)); return true; }
        if (id == R.id.student_mylesson)     { startActivity(new Intent(this, student_lesson_list.class)); return true; }
        if (id == R.id.student_adut)         { startActivity(new Intent(this, AdutActivity.class)); return true; }
        return super.onOptionsItemSelected(item);
    }
}