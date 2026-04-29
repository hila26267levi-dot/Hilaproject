package com.hila.myapplication.screens;

import android.content.Intent;
import android.content.SharedPreferences;
import android.os.Bundle;
import android.view.Menu;
import android.view.MenuItem;
import android.view.View;
import android.widget.Button;
import android.widget.Toast;

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
                FirebaseAuth.getInstance().signOut();

                SharedPreferences sharedPreferences =
                        getSharedPreferences("myPrefs", MODE_PRIVATE);
                SharedPreferences.Editor editor = sharedPreferences.edit();
                editor.remove("email");
                editor.remove("password");
                editor.apply();

                Toast.makeText(disconect_forstudent.this,
                        "התנתקות בוצעה בהצלחה", Toast.LENGTH_LONG).show();

                Intent intent = new Intent(disconect_forstudent.this, MainActivity.class);
                intent.setFlags(Intent.FLAG_ACTIVITY_NEW_TASK | Intent.FLAG_ACTIVITY_CLEAR_TASK);
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
            startActivity(new Intent(disconect_forstudent.this, StudentActivity.class));
            return true;
        }
        if (id == R.id.student_searchteacher) {
            startActivity(new Intent(disconect_forstudent.this, TeacherListActivity.class));
            return true;
        }
        if (id == R.id.student_profile) {
            startActivity(new Intent(disconect_forstudent.this, StudentProfile.class));
            return true;
        }
        if (id == R.id.student_disconect) {
            startActivity(new Intent(disconect_forstudent.this, disconect_forstudent.class));
            return true;
        }
        if (id == R.id.student_mylesson) {
            startActivity(new Intent(disconect_forstudent.this, student_lesson_list.class));
            return true;
        }
        if (id == R.id.student_adut) {
            startActivity(new Intent(disconect_forstudent.this, AdutActivity.class));
            return true;
        }
        return super.onOptionsItemSelected(item);
    }
}