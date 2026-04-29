package com.hila.myapplication.screens;

import android.content.Intent;
import android.os.Bundle;
import android.view.View;
import android.widget.Button;

import androidx.appcompat.app.AppCompatActivity;

import com.hila.myapplication.R;

public class MainActivity extends AppCompatActivity implements View.OnClickListener {



    Button btnTeacher, btnStudent ,btnlogin_mainpage ;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_main);
        btnStudent = findViewById(R.id.btn_student);
        btnTeacher = findViewById(R.id.btn_teacher);
        btnlogin_mainpage = findViewById(R.id.btn_login_mainpage1);
        btnlogin_mainpage.setOnClickListener(MainActivity.this);
        btnStudent.setOnClickListener(MainActivity.this);
        btnTeacher.setOnClickListener(MainActivity.this);
    }

    @Override
    public void onClick(View v) {
        if (v.getId() == btnStudent.getId()) {
            Intent intent = new Intent(MainActivity.this, RegisterStudentActivity.class);
            startActivity(intent);
        }
        else if (v.getId() == btnTeacher.getId()) {
            Intent intent = new Intent(MainActivity.this,RegisterTeacherActivity.class);
            startActivity(intent);
        }
        else if (v.getId() == btnlogin_mainpage.getId()) {
            Intent intent = new Intent(MainActivity.this, loginActivity.class);
            startActivity(intent);
        }
    }
}