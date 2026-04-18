package com.hila.myapplication.screens;

import android.content.Intent;
import android.os.Bundle;
import android.view.Menu;
import android.view.MenuItem;
import android.view.View;
import android.widget.Button;
import android.widget.CheckBox;
import android.widget.EditText;
import android.widget.Toast;

import androidx.activity.EdgeToEdge;
import androidx.appcompat.app.AppCompatActivity;
import androidx.core.graphics.Insets;
import androidx.core.view.ViewCompat;
import androidx.core.view.WindowInsetsCompat;

import com.google.firebase.auth.FirebaseAuth;
import com.google.firebase.database.DatabaseReference;
import com.google.firebase.database.FirebaseDatabase;
import com.hila.myapplication.R;
import com.hila.myapplication.model.Teacher;
import com.hila.myapplication.servicses.DatabaseService;

public class Teacher_edit_profile extends AppCompatActivity implements View.OnClickListener {
EditText fname , zoom , lname , age,  price, teachclass , subject ;
 Teacher  currentTeacher ;
 String uid ;
Button btn_save ;
    private DatabaseService databaseService;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_teacher_edit_profile);

        // אתחול Firebase service
        databaseService = DatabaseService.getInstance();

        // חיבור השדות
        fname = findViewById(R.id.profile_Teacher_E_Fname);
        lname = findViewById(R.id.profile_Teacher_E_Lname);
        age = findViewById(R.id.profile_Teacher_E_age);
        price = findViewById(R.id.profile_Teacher_E_price);
        teachclass = findViewById(R.id.profile_Teacher_E_teachclass);
        subject = findViewById(R.id.profile_Teacher_E_subject);
        zoom = findViewById(R.id.profile_Teacher_E_zoom);
        btn_save = findViewById(R.id.profile_Teacher_E_btn);

        // קבלת ה-UID של המשתמש הנוכחי
        uid = FirebaseAuth.getInstance().getCurrentUser().getUid();

        // משיכת המורה הנוכחי מהFirebase
        databaseService.getTeacher(uid, new DatabaseService.DatabaseCallback<Teacher>() {
            @Override
            public void onCompleted(Teacher teacher) {
                currentTeacher=teacher;
                loadTeacherData();

            }

            @Override
            public void onFailed(Exception e) {

            }


        });

        // כפתור שמירה
        btn_save.setOnClickListener(this);
    }


// firebaseפונקציה הזאת לוקחת את המורה מה
// וממלאת את הטופס כדי שהמשתמש יראה את הפרטים שלו
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

// שמירת העריכה
private void saveTeacherProfile() {
    if (currentTeacher == null) return;



  //  validateInput(String fname, String lname, String price, String age) {

    // עדכון האובייקט
    currentTeacher.setFname(fname.getText().toString());
    currentTeacher.setLname(lname.getText().toString());
    currentTeacher.setAge(age.getText().toString());
    currentTeacher.setPrice(Double.parseDouble(price.getText().toString()));
    currentTeacher.setTeachclass(teachclass.getText().toString());
    currentTeacher.setSubject(subject.getText().toString());
    currentTeacher.setZoom(zoom.getText().toString());


    // עדכון בFirebase
    databaseService.updateTeacher(currentTeacher, new DatabaseService.DatabaseCallback<Void>() {
        @Override
        public void onCompleted(Void object) {

        }

        @Override
        public void onFailed(Exception e) {

        }

    });
}




    //בדיקות תקינות
    //בדיקות תקינות
    public void validateInput(String fname, String lname, String price, String age) {
        String nameRegex = "^[A-Za-z]+$";
        String numberRegex = "^[0-9]+$";

        // שם פרטי
        if (fname.isEmpty()) {
            Toast.makeText(this, "שגיאה: חובה להזין שם פרטי!", Toast.LENGTH_LONG).show();
            return;
        }
        if (!fname.matches(nameRegex)) {
            Toast.makeText(this, "שגיאה: שם פרטי חייב להכיל אותיות בלבד!", Toast.LENGTH_LONG).show();
            return;
        }

        // שם משפחה
        if (lname.isEmpty()) {
            Toast.makeText(this, "שגיאה: חובה להזין שם משפחה!", Toast.LENGTH_LONG).show();
            return;
        }
        if (!lname.matches(nameRegex)) {
            Toast.makeText(this, "שגיאה: שם משפחה חייב להכיל אותיות בלבד!", Toast.LENGTH_LONG).show();
            return;
        }

        // מחיר
        if (price.isEmpty()) {
            Toast.makeText(this, "שגיאה: חובה להזין מחיר!", Toast.LENGTH_LONG).show();
            return;
        }
        if (!price.matches(numberRegex)) {
            Toast.makeText(this, "שגיאה: מחיר חייב להיות מספר בלבד!", Toast.LENGTH_LONG).show();
            return;
        }

        // גיל
        if (age.isEmpty()) {
            Toast.makeText(this, "שגיאה: חובה להזין גיל!", Toast.LENGTH_LONG).show();
            return;
        }
        if (!age.matches(numberRegex)) {
            Toast.makeText(this, "שגיאה: גיל חייב להיות מספר בלבד!", Toast.LENGTH_LONG).show();
            return;
        }

        int ageValue = Integer.parseInt(age);
        if (ageValue < 10) {
            Toast.makeText(this, "שגיאה: גיל חייב להיות 10 ומעלה!", Toast.LENGTH_LONG).show();
            return;
        }

        // אם הכל תקין
        Toast.makeText(this, "כל הפרטים תקינים!", Toast.LENGTH_LONG).show();
    }








    //   של מורה תפריט צד
    @Override
    public boolean onCreateOptionsMenu(Menu menu) {
        getMenuInflater().inflate(R.menu.teacher_menu, menu);
        return true;
    }

    @Override
    public boolean onOptionsItemSelected(MenuItem item) {

        int id = item.getItemId();

        if (id == R.id.teacher_home) {
            Intent intent = new Intent(Teacher_edit_profile.this,TeacherActivity.class);
            startActivity(intent);

            return true;
        }

        if (id == R.id.teacher_profile) {
            Intent intent = new Intent(Teacher_edit_profile.this, teacher_profile.class);
            startActivity(intent);

            return true;
        }

        if (id == R.id.teacher_mylesson) {
            Intent intent = new Intent(Teacher_edit_profile.this, TeacherLessonsList.class);
            startActivity(intent);
            return true;
        }
        if (id == R.id.teacher_disconect) {
            Intent intent = new Intent(Teacher_edit_profile.this, disconect_forteacher.class);
            startActivity(intent);
            return true;
        }
        if (id == R.id.teacher_adut) {
            Intent intent = new Intent(Teacher_edit_profile.this, AdutActivity.class);
            startActivity(intent);
            return true;
        }
        return super.onOptionsItemSelected(item);
    }

    @Override
    public void onClick(View v) {
        saveTeacherProfile();
    }
}