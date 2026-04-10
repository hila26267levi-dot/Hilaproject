package com.hila.myapplication.screens;

import android.content.Intent;
import android.os.Bundle;
import android.view.Menu;
import android.view.MenuItem;
import android.widget.Button;
import android.widget.TextView;

import androidx.activity.EdgeToEdge;
import androidx.appcompat.app.AppCompatActivity;
import androidx.core.graphics.Insets;
import androidx.core.view.ViewCompat;
import androidx.core.view.WindowInsetsCompat;

import com.hila.myapplication.R;
import com.hila.myapplication.model.Teacher;
import com.hila.myapplication.servicses.DatabaseService;

public class teacher_profile extends AppCompatActivity {
    TextView tvFname, tvLname, tvClassteach, tvprofession, tvzoom, tvage , tvprice ;
    Intent takeit;
    Teacher currntTeacher=null;
    Button btn_E_profile ;

    String tId="" ;
    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        EdgeToEdge.enable(this);
        setContentView(R.layout.activity_teacher_profile);
        ViewCompat.setOnApplyWindowInsetsListener(findViewById(R.id.main), (v, insets) -> {
            Insets systemBars = insets.getInsets(WindowInsetsCompat.Type.systemBars());
            v.setPadding(systemBars.left, systemBars.top, systemBars.right, systemBars.bottom);
            return insets;
        });
        initViews();
        DatabaseService databaseService = DatabaseService.getInstance();

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

        tvFname=findViewById(R.id.tvProfileTeacher_Fname);
        tvLname=findViewById(R.id.tvProfileTeacher_Lname);
        tvClassteach=findViewById(R.id.tvProfileTeacher_teachclass);
        tvprofession=findViewById(R.id.tvProfileTeacher_subject);
        tvzoom=findViewById(R.id.tvProfileTeacher_zoom);
        tvage=findViewById(R.id.tvProfileTeacher_age);
        tvprice=findViewById(R.id.tvProfileTeacher_price);

    }
    //   של מורה תפריט צד
    @Override
    public boolean onCreateOptionsMenu(Menu menu) {
        getMenuInflater().inflate(R.menu.main_menu, menu);
        return true;
    }

    @Override
    public boolean onOptionsItemSelected(MenuItem item) {

        int id = item.getItemId();

        if (id == R.id.teacher_home) {
            Intent intent = new Intent(teacher_profile.this,TeacherActivity.class);
            startActivity(intent);

            return true;
        }

        if (id == R.id.teacher_profile) {
            Intent intent = new Intent(teacher_profile.this, teacher_profile.class);
            startActivity(intent);

            return true;
        }

        if (id == R.id.teacher_mylesson) {
            Intent intent = new Intent(teacher_profile.this, TeacherLessonsList.class);
            startActivity(intent);
            return true;
        }
        if (id == R.id.teacher_disconect) {
            Intent intent = new Intent(teacher_profile.this, disconect_forteacher.class);
            startActivity(intent);
            return true;
        }
        if (id == R.id.teacher_adut) {
            Intent intent = new Intent(teacher_profile.this, AdutActivity.class);
            startActivity(intent);
            return true;
        }
        return super.onOptionsItemSelected(item);
    }

}
