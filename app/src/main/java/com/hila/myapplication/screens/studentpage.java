package com.hila.myapplication.screens;

import android.content.Intent;
import android.os.Bundle;
import android.view.View;
import android.widget.Button;

import androidx.activity.EdgeToEdge;
import androidx.appcompat.app.AppCompatActivity;

import com.hila.myapplication.R;

public class studentpage extends AppCompatActivity implements View.OnClickListener {
Button btnStudent_r ,btnStudent_l;
    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        EdgeToEdge.enable(this);
        setContentView(R.layout.activity_studentpage);
        btnStudent_r= findViewById(R.id.btn_student_regisret1);
        btnStudent_l = findViewById(R.id.btn_student_login1);
        btnStudent_r.setOnClickListener( this);
        btnStudent_l.setOnClickListener( this);
        
    }


    @Override
    public void onClick(View v) {

        if(v==btnStudent_l){
            Intent intent = new Intent(studentpage.this, loginActivity.class);
            startActivity(intent);
        }
        if(v==btnStudent_r){
            Intent intent = new Intent(studentpage.this, RegisterStudentActivity.class);
            startActivity(intent);


        }


    }
}