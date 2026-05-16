package com.hila.myapplication.screens;

import android.content.Intent;
import android.os.Bundle;
import android.view.Menu;
import android.view.MenuItem;
import android.view.View;
import android.widget.Button;
import android.widget.TextView;

import androidx.activity.EdgeToEdge;
import androidx.appcompat.app.AppCompatActivity;
import androidx.core.graphics.Insets;
import androidx.core.view.ViewCompat;
import androidx.core.view.WindowInsetsCompat;

import com.google.firebase.auth.FirebaseAuth;
import com.hila.myapplication.R;
import com.hila.myapplication.model.Teacher;
import com.hila.myapplication.servicses.DatabaseService;

public class TeacherProfile_forstudent extends AppCompatActivity {

    TextView tvFname, tvLname, tvClassteach, tvprofession, tvzoom, tvage , tvprice ;
    Button btnSetLesson;
    Intent takeit;
    Teacher currntTeacher=null;

    String tId="" ;

    private DatabaseService databaseService;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        EdgeToEdge.enable(this);
        setContentView(R.layout.activity_teacher_profile_forstudent);

        ViewCompat.setOnApplyWindowInsetsListener(findViewById(R.id.main), (v, insets) -> {
            Insets systemBars = insets.getInsets(WindowInsetsCompat.Type.systemBars());
            v.setPadding(systemBars.left, systemBars.top, systemBars.right, systemBars.bottom);
            return insets;
        });
        initViews();
        databaseService=DatabaseService.getInstance();
//בגלל שהוספנו שהמעבר מהדף הזה יקח גם את הזהות של אותו מורה נדע לבדוק בדף הבא שמדובר בתלמיד ולא למורה בגלל ש tid לא ריק
        takeit=getIntent();
        tId=takeit.getStringExtra("teacherId");

        if(!tId.isEmpty()){



            databaseService.getTeacher(tId, new DatabaseService.DatabaseCallback<Teacher>() {
                @Override
                public void onCompleted(Teacher teacher) {

                    currntTeacher=teacher;
                    if(teacher!=null){
                        tvFname.setText(teacher.getFname());
                        tvLname.setText(teacher.getLname());
                        tvClassteach.setText(teacher.getTeachclass());
                        tvprofession.setText(teacher.getSubject());
                        tvzoom.setText(teacher.getZoom());
                        tvage.setText(teacher.getAge());
                       tvprice.setText(teacher.getPrice()+"");
                    }

                }

                @Override
                public void onFailed(Exception e) {

                }
            });




        }

    }

    private void initViews() {

        tvFname=findViewById(R.id.tvProfileTeacher_forstudent_Fname);
        tvLname=findViewById(R.id.tvProfileTeacher_forstudent_Lname);
        tvClassteach=findViewById(R.id.tvProfileTeacher_forstudent_teachclass);
        tvprofession=findViewById(R.id.tvProfileTeacher_forstudent_subject);
        tvzoom=findViewById(R.id.tvProfileTeacher_forstudent_zoom);
        tvage=findViewById(R.id.tvProfileTeacher_forstudent_age);
        tvprice=findViewById(R.id.tvProfileTeacher_forstudent_price);
        btnSetLesson=findViewById(R.id.btnProfileTeacher_forstudent_hislessons);

    }

//הפעולה של הכפתור
    //כאן זה לוקח איתו זהות של מורה ובזכות זה נוכל להבדיל בין התלמיד למורה
    public void goTeacherLesson(View view) {

        Intent intent = new Intent(TeacherProfile_forstudent.this, TeacherLessonsList.class);
         intent.putExtra("teacherId",tId);
        startActivity(intent);


    }
    //   של תלמיד תפריט צד
    @Override
    public boolean onCreateOptionsMenu(Menu menu) {
        getMenuInflater().inflate(R.menu.student_menu, menu);
        return true;
    }

    @Override
    public boolean onOptionsItemSelected(MenuItem item) {

        int id = item.getItemId();

        if (id == R.id.student_home) {
            Intent intent = new Intent(TeacherProfile_forstudent.this, StudentActivity.class);
            startActivity(intent);

            return true;
        }

        if (id == R.id.student_searchteacher) {
            Intent intent = new Intent(TeacherProfile_forstudent.this, TeacherListActivity.class);
            startActivity(intent);

            return true;
        }

        if (id == R.id.student_profile) {
            Intent intent = new Intent(TeacherProfile_forstudent.this, StudentProfile.class);
            startActivity(intent);
            return true;
        }
        if (id == R.id.student_disconect) {
            FirebaseAuth.getInstance().signOut();
            Intent go = new Intent(TeacherProfile_forstudent.this,
                    MainActivity.class);
            startActivity(go);
            finish();
        }
        if (id == R.id.student_mylesson) {
            Intent intent = new Intent(TeacherProfile_forstudent.this, student_lesson_list.class);
            startActivity(intent);
            return true;
        }
        if (id == R.id.student_adut) {
            Intent intent = new Intent(TeacherProfile_forstudent.this, AdutActivity.class);
            startActivity(intent);
            return true;
        }
        return super.onOptionsItemSelected(item);
    }

}