package com.hila.myapplication.screens;

import android.content.Intent;
import android.content.SharedPreferences;
import android.os.Bundle;
import android.view.Menu;
import android.view.MenuItem;
import android.view.View;
import android.widget.Button;

import androidx.activity.EdgeToEdge;
import androidx.appcompat.app.AppCompatActivity;
import androidx.core.graphics.Insets;
import androidx.core.view.ViewCompat;
import androidx.core.view.WindowInsetsCompat;

import com.google.firebase.auth.FirebaseAuth;
import com.hila.myapplication.R;

public class disconect_forstudent extends AppCompatActivity {

    Button btn_disconect;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        EdgeToEdge.enable(this);
        setContentView(R.layout.activity_disconect_forstudent);
        ViewCompat.setOnApplyWindowInsetsListener(findViewById(R.id.main), (v, insets) -> {
            Insets systemBars = insets.getInsets(WindowInsetsCompat.Type.systemBars());
            v.setPadding(systemBars.left, systemBars.top, systemBars.right, systemBars.bottom);
            return insets;
        });

        btn_disconect = findViewById(R.id.btn_disconect_student);

        btn_disconect.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {

                // התנתקות מ-Firebase
                FirebaseAuth.getInstance().signOut();

                // מחיקת פרטי הכניסה מ-SharedPreferences
                SharedPreferences sharedPreferences =
                        getSharedPreferences("myPrefs", MODE_PRIVATE);
                SharedPreferences.Editor editor = sharedPreferences.edit();
                editor.remove("email");
                editor.remove("password");
                editor.apply();

                // מעבר למסך הראשי
                Intent intent = new Intent(disconect_forstudent.this, MainActivity.class);
                intent.setFlags(Intent.FLAG_ACTIVITY_NEW_TASK
                        | Intent.FLAG_ACTIVITY_CLEAR_TASK);
                startActivity(intent);
            }
        });
    }

    @Override
    public boolean onCreateOptionsMenu(Menu menu) {
        getMenuInflater().inflate(R.menu.student_menu, menu);
        return true;
    }

    @Override
    public boolean onOptionsItemSelected(MenuItem item) {
        int id = item.getItemId();

        if (id == R.id.student_home) {
            Intent intent = new Intent(disconect_forstudent.this, StudentActivity.class);
            startActivity(intent);
            return true;
        }
        if (id == R.id.student_searchteacher) {
            Intent intent = new Intent(disconect_forstudent.this, TeacherListActivity.class);
            startActivity(intent);
            return true;
        }
        if (id == R.id.student_profile) {
            Intent intent = new Intent(disconect_forstudent.this, StudentProfile.class);
            startActivity(intent);
            return true;
        }
        if (id == R.id.student_disconect) {
            Intent intent = new Intent(disconect_forstudent.this, disconect_forstudent.class);
            startActivity(intent);
            return true;
        }
        if (id == R.id.student_mylesson) {
            Intent intent = new Intent(disconect_forstudent.this, student_lesson_list.class);
            startActivity(intent);
            return true;
        }
        if (id == R.id.student_adut) {
            Intent intent = new Intent(disconect_forstudent.this, AdutActivity.class);
            startActivity(intent);
            return true;
        }
        return super.onOptionsItemSelected(item);
    }
}