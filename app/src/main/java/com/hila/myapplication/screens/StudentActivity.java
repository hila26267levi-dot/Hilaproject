package com.hila.myapplication.screens;

import android.content.Intent;
import android.os.Bundle;
import android.view.Menu;
import android.view.MenuItem;
import android.view.View;
import android.widget.Button;

import androidx.activity.EdgeToEdge;
import androidx.appcompat.app.AppCompatActivity;

import com.hila.myapplication.R;

public class StudentActivity extends AppCompatActivity implements View.OnClickListener {

    Button btn_Studentpage_mylesson,btn_studentpage_myprofil,btn_studentpage_searchteacher;
    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        EdgeToEdge.enable(this);
        setContentView(R.layout.activity_student);
        btn_Studentpage_mylesson= findViewById(R.id.btn_studentpage_mylesson);
        btn_studentpage_searchteacher = findViewById(R.id.btn_studentpage_searchteacher);
        btn_studentpage_myprofil= findViewById(R.id.btn_studentpage_myprofil);
        btn_Studentpage_mylesson.setOnClickListener( this);
        btn_studentpage_myprofil.setOnClickListener(this);
        btn_studentpage_searchteacher.setOnClickListener(this);
    }

    @Override
    public void onClick(View v) {

        if(v==btn_Studentpage_mylesson){
            Intent intent = new Intent(StudentActivity.this,student_lesson_list.class);
            startActivity(intent);
        }
        if(v==    btn_studentpage_searchteacher){
            Intent intent = new Intent(StudentActivity.this, TeacherListActivity.class);
            startActivity(intent);


        }
        if(v==btn_studentpage_myprofil){
            Intent intent = new Intent(StudentActivity.this, StudentProfile.class);
            startActivity(intent);


        }

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
            Intent intent = new Intent(StudentActivity.this, StudentActivity.class);
            startActivity(intent);

            return true;
        }

        if (id == R.id.student_searchteacher) {
            Intent intent = new Intent(StudentActivity.this,TeacherListActivity.class);
            startActivity(intent);

            return true;
        }

        if (id == R.id.student_profile) {
            Intent intent = new Intent(StudentActivity.this, StudentProfile.class);
            startActivity(intent);
            return true;
        }
        if (id == R.id.student_disconect) {
            Intent intent = new Intent(StudentActivity.this, disconect_forstudent.class);
            startActivity(intent);
            return true;
        }
        if (id == R.id.student_mylesson) {
            Intent intent = new Intent(StudentActivity.this, student_lesson_list.class);
            startActivity(intent);
            return true;
        }
        if (id == R.id.student_adut) {
            Intent intent = new Intent(StudentActivity.this, AdutActivity.class);
            startActivity(intent);
            return true;
        }
        return super.onOptionsItemSelected(item);
    }


}