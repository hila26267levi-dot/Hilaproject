package com.hila.myapplication.screens;

import android.content.Intent;
import android.os.Bundle;
import android.view.View;
import android.widget.Button;

import androidx.activity.EdgeToEdge;
import androidx.appcompat.app.AppCompatActivity;

import com.hila.myapplication.R;

public class teacherpage extends AppCompatActivity implements View.OnClickListener{

 Button  btnTeacher_l, btnTeacher_r ;
    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        EdgeToEdge.enable(this);
        setContentView(R.layout.activity_teacherpage);
        btnTeacher_l= findViewById(R.id.btn_teacher_login1);
        btnTeacher_r = findViewById(R.id.btn_teacher_regisret1);
        btnTeacher_l.setOnClickListener( teacherpage.this);
        btnTeacher_r.setOnClickListener( teacherpage.this);
    }


    @Override
    public void onClick(View v) {

        if(v==btnTeacher_l){
            Intent intent = new Intent( teacherpage.this, loginActivity.class);
            startActivity(intent);

        }
        if(v==btnTeacher_r){
            Intent intent = new Intent(teacherpage.this, RegisterTeacherActivity.class);
            startActivity(intent);


        }

    }
}