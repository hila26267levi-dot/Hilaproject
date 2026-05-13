package com.hila.myapplication.screens;

import android.content.Intent;
import android.os.Bundle;
import android.view.Menu;
import android.view.MenuItem;
import android.view.View;
import android.widget.AdapterView;
import android.widget.Button;
import android.widget.EditText;
import android.widget.ImageView;
import android.widget.Spinner;
import android.widget.Toast;

import androidx.activity.EdgeToEdge;
import androidx.appcompat.app.AppCompatActivity;

import com.google.firebase.auth.FirebaseAuth;
import com.hila.myapplication.R;
import com.hila.myapplication.model.Teacher;
import com.hila.myapplication.servicses.DatabaseService;
import com.hila.myapplication.utils.ImageHelper;

public class Teacher_edit_profile extends AppCompatActivity implements View.OnClickListener {

    EditText fname, lname, age, price, teachclass, subject;
    Spinner spZoomEdit;
    ImageView profileImageView;
    Teacher currentTeacher;
    String uid;
    Button btn_save;
    private DatabaseService databaseService;
    private String selectedZoom = "לא";
    private String imageBase64 = "";

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_teacher_edit_profile);

        databaseService = DatabaseService.getInstance();

        fname = findViewById(R.id.profile_Teacher_E_Fname);
        lname = findViewById(R.id.profile_Teacher_E_Lname);
        age = findViewById(R.id.profile_Teacher_E_age);
        price = findViewById(R.id.profile_Teacher_E_price);
        teachclass = findViewById(R.id.profile_Teacher_E_teachclass);
        subject = findViewById(R.id.profile_Teacher_E_subject);
        profileImageView = findViewById(R.id.img_TeacherProfile);

        profileImageView.setOnClickListener(v -> ImageHelper.openGallery(this));

        spZoomEdit = findViewById(R.id.spZoomEdit);
        spZoomEdit.setOnItemSelectedListener(new AdapterView.OnItemSelectedListener() {
            @Override
            public void onItemSelected(AdapterView<?> parent, View view, int position, long id) {
                if (position == 1) selectedZoom = "כן";
                else if (position == 2) selectedZoom = "לא";
            }
            @Override
            public void onNothingSelected(AdapterView<?> parent) {}
        });

        btn_save = findViewById(R.id.profile_Teacher_E_btn);
        uid = FirebaseAuth.getInstance().getCurrentUser().getUid();

        databaseService.getTeacher(uid, new DatabaseService.DatabaseCallback<Teacher>() {
            @Override
            public void onCompleted(Teacher teacher) {
                currentTeacher = teacher;
                loadTeacherData();
            }
            @Override
            public void onFailed(Exception e) {}
        });

        btn_save.setOnClickListener(this);
    }

    @Override
    protected void onActivityResult(int requestCode, int resultCode, Intent data) {
        super.onActivityResult(requestCode, resultCode, data);
        String result = ImageHelper.handleActivityResult(this, requestCode, resultCode, data);
        if (result != null) {
            imageBase64 = result;
            profileImageView.setImageBitmap(ImageHelper.base64ToBitmap(result));
        }
    }

    private void loadTeacherData() {
        if (currentTeacher == null) return;
        fname.setText(currentTeacher.getFname());
        lname.setText(currentTeacher.getLname());
        age.setText(currentTeacher.getAge());
        price.setText(String.valueOf(currentTeacher.getPrice()));
        teachclass.setText(currentTeacher.getTeachclass());
        subject.setText(currentTeacher.getSubject());

        // טען תמונה קיימת
        if (currentTeacher.getImage() != null && !currentTeacher.getImage().isEmpty()) {
            imageBase64 = currentTeacher.getImage();
            profileImageView.setImageBitmap(ImageHelper.base64ToBitmap(imageBase64));
        }

        String savedZoom = currentTeacher.getZoom();
        if (savedZoom != null) {
            if (savedZoom.equals("כן") || savedZoom.equals("yes")) {
                spZoomEdit.setSelection(1);
                selectedZoom = "כן";
            } else {
                spZoomEdit.setSelection(2);
                selectedZoom = "לא";
            }
        }
    }

    private void saveTeacherProfile() {
        if (currentTeacher == null) return;

        String fnameVal = fname.getText().toString();
        String lnameVal = lname.getText().toString();
        String priceVal = price.getText().toString();
        String ageVal = age.getText().toString();

        if (!validateInput(fnameVal, lnameVal, priceVal, ageVal)) return;

        currentTeacher.setFname(fnameVal);
        currentTeacher.setLname(lnameVal);
        currentTeacher.setAge(ageVal);
        currentTeacher.setPrice(Double.parseDouble(priceVal));
        currentTeacher.setTeachclass(teachclass.getText().toString());
        currentTeacher.setSubject(subject.getText().toString());
        currentTeacher.setZoom(selectedZoom);
        currentTeacher.setImage(imageBase64);

        databaseService.updateTeacher(currentTeacher, new DatabaseService.DatabaseCallback<Void>() {
            @Override
            public void onCompleted(Void object) {
                Toast.makeText(Teacher_edit_profile.this, "הפרופיל עודכן בהצלחה!", Toast.LENGTH_SHORT).show();
                Intent intent = new Intent(Teacher_edit_profile.this, TeacherActivity.class);
                startActivity(intent);
            }
            @Override
            public void onFailed(Exception e) {
                Toast.makeText(Teacher_edit_profile.this, "שגיאה בשמירה", Toast.LENGTH_SHORT).show();
            }
        });
    }

    public boolean validateInput(String fname, String lname, String price, String age) {
        String nameRegex = "^[A-Za-zא-ת]+$";
        String numberRegex = "^[0-9]+$";
        if (fname.isEmpty()) { Toast.makeText(this, "שגיאה: חובה להזין שם פרטי!", Toast.LENGTH_LONG).show(); return false; }
        if (!fname.matches(nameRegex)) { Toast.makeText(this, "שגיאה: שם פרטי חייב להכיל אותיות בלבד!", Toast.LENGTH_LONG).show(); return false; }
        if (lname.isEmpty()) { Toast.makeText(this, "שגיאה: חובה להזין שם משפחה!", Toast.LENGTH_LONG).show(); return false; }
        if (!lname.matches(nameRegex)) { Toast.makeText(this, "שגיאה: שם משפחה חייב להכיל אותיות בלבד!", Toast.LENGTH_LONG).show(); return false; }
        if (price.isEmpty()) { Toast.makeText(this, "שגיאה: חובה להזין מחיר!", Toast.LENGTH_LONG).show(); return false; }
        if (!price.matches(numberRegex)) { Toast.makeText(this, "שגיאה: מחיר חייב להיות מספר בלבד!", Toast.LENGTH_LONG).show(); return false; }
        if (age.isEmpty()) { Toast.makeText(this, "שגיאה: חובה להזין גיל!", Toast.LENGTH_LONG).show(); return false; }
        if (!age.matches(numberRegex)) { Toast.makeText(this, "שגיאה: גיל חייב להיות מספר בלבד!", Toast.LENGTH_LONG).show(); return false; }
        if (Integer.parseInt(age) < 10) { Toast.makeText(this, "שגיאה: גיל חייב להיות 10 ומעלה!", Toast.LENGTH_LONG).show(); return false; }
        return true;
    }

    @Override
    public boolean onCreateOptionsMenu(Menu menu) {
        getMenuInflater().inflate(R.menu.teacher_menu, menu);
        return true;
    }

    @Override
    public boolean onOptionsItemSelected(MenuItem item) {
        int id = item.getItemId();
        if (id == R.id.teacher_home) { startActivity(new Intent(this, TeacherActivity.class)); return true; }
        if (id == R.id.teacher_profile) { startActivity(new Intent(this, teacher_profile.class)); return true; }
        if (id == R.id.teacher_mylesson) { startActivity(new Intent(this, TeacherLessonsList.class)); return true; }
        if (id == R.id.teacher_disconect) { startActivity(new Intent(this, disconect_forteacher.class)); return true; }
        if (id == R.id.teacher_adut) { startActivity(new Intent(this, AdutActivity.class)); return true; }
        return super.onOptionsItemSelected(item);
    }

    @Override
    public void onClick(View v) {
        saveTeacherProfile();
    }
}