package com.hila.myapplication.screens;

import android.content.Intent;
import android.os.Bundle;
import android.view.View;
import android.widget.Button;

import androidx.appcompat.app.AppCompatActivity;

import com.hila.myapplication.R;

public class MainActivity extends AppCompatActivity implements View.OnClickListener {



    Button btnTeacher, btnStudent  ;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_main);
        btnStudent = findViewById(R.id.btn_student);
        btnTeacher = findViewById(R.id.btn_teacher);
        btnStudent.setOnClickListener(MainActivity.this);
        btnTeacher.setOnClickListener(MainActivity.this);
    }

    @Override
    public void onClick(View v) {
        if (v.getId() == btnStudent.getId()) {
            Intent intent = new Intent(MainActivity.this, studentpage.class);
            startActivity(intent);
        }
        else if (v.getId() == btnTeacher.getId()) {
            Intent intent = new Intent(MainActivity.this, teacherpage.class);
            startActivity(intent);
        }
    }
}