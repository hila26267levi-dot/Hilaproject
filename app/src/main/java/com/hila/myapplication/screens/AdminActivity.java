package com.hila.myapplication.screens;

import android.content.Intent;
import android.os.Bundle;
import android.view.View;
import android.widget.Button;

import androidx.activity.EdgeToEdge;
import androidx.appcompat.app.AppCompatActivity;
import androidx.core.graphics.Insets;
import androidx.core.view.ViewCompat;
import androidx.core.view.WindowInsetsCompat;

import com.hila.myapplication.R;

public class AdminActivity extends AppCompatActivity implements View.OnClickListener  {
    Button btn_maneger_teacher, btn_maneger_student  ;
    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        EdgeToEdge.enable(this);
        setContentView(R.layout.activity_admin);
        btn_maneger_teacher= findViewById(R.id.btn_teacher_maneger);
        btn_maneger_student= findViewById(R.id.btn_student_maneger);
        btn_maneger_teacher.setOnClickListener( this);
        btn_maneger_student.setOnClickListener( this);




    }
//כדי לעשות מחיקה נעשה כמו במחיקת שיעור שכאשר ומדובר במנהל בליחה ארוכה על המשתמש זה נמחק
    @Override
    public void onClick(View v) {
        if(v==   btn_maneger_teacher){
            Intent intent = new Intent(AdminActivity.this,TeacherListActivity.class);
            startActivity(intent);
        }
        if(v==btn_maneger_student){
            Intent intent = new Intent(AdminActivity.this, StudentListActivity.class);
            startActivity(intent);


        }

    }
}