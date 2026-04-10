package com.hila.myapplication.screens;

import android.content.Intent;
import android.graphics.Bitmap;
import android.net.Uri;
import android.os.Bundle;
import android.provider.MediaStore;
import android.view.Menu;
import android.view.MenuItem;
import android.view.View;
import android.widget.Button;
import android.widget.ImageView;
import android.widget.TextView;

import androidx.activity.EdgeToEdge;
import androidx.activity.result.ActivityResultLauncher;
import androidx.activity.result.contract.ActivityResultContracts;
import androidx.appcompat.app.AppCompatActivity;
import androidx.core.graphics.Insets;
import androidx.core.view.ViewCompat;
import androidx.core.view.WindowInsetsCompat;

import com.hila.myapplication.R;
import com.hila.myapplication.adapters.ImageUtil;
import com.hila.myapplication.model.Student;
import com.hila.myapplication.servicses.DatabaseService;

public class StudentProfile extends AppCompatActivity implements View.OnClickListener {
    TextView tvFname, tvLname, tvemail, tvclass, tvphone;
    Student currntStudent = null;


    Intent takeit;

    String tId = null;

    private DatabaseService databaseService;
    Button btn_edit ;


String fname,lname, kita,phone;



    @Override
    protected void onCreate(Bundle savedInstanceState) {

        super.onCreate(savedInstanceState);
        EdgeToEdge.enable(this);
        setContentView(R.layout.activity_student_profile);

        ViewCompat.setOnApplyWindowInsetsListener(findViewById(R.id.main), (v, insets) -> {
            Insets systemBars = insets.getInsets(WindowInsetsCompat.Type.systemBars());
            v.setPadding(systemBars.left, systemBars.top, systemBars.right, systemBars.bottom);
            return insets;
        });

        initViews();

databaseService=DatabaseService.getInstance();

        databaseService.getStudent(new DatabaseService.DatabaseCallback<Student>() {
            @Override
            public void onCompleted(Student student) {
                currntStudent = student;
                if (currntStudent != null) {
                    tvFname.setText(currntStudent.getFname());
                    tvLname.setText(currntStudent.getLname());
                    tvemail.setText(currntStudent.getEmail());
                    tvclass.setText(currntStudent.getKita() + "");
                    tvphone.setText(currntStudent.getPhone());
                }

            }

            @Override
            public void onFailed(Exception e) {

            }


        });
    }
    //   }
    //  }

    private void initViews() {
        tvFname = findViewById(R.id.tvProfileStudent_Fname);
        tvLname = findViewById(R.id.tvProfileStudent_Lname);
        tvclass = findViewById(R.id.tvProfileStudent_class);
        tvemail = findViewById(R.id.tvProfileStudent_email);
        tvphone = findViewById(R.id.tvProfileStudent_phone);
        btn_edit= findViewById(R.id.btn_go_student_profile);
        btn_edit.setOnClickListener(this);

    }




    @Override
    public void onClick(View v) {
        Intent intent = new Intent(StudentProfile.this,Student_edit_profile.class);
        startActivity(intent);


    }
//תפריט צד
    @Override
    public boolean onCreateOptionsMenu(Menu menu) {
        getMenuInflater().inflate(R.menu.main_menu, menu);
        return true;
    }

    @Override
    public boolean onOptionsItemSelected(MenuItem item) {

        int id = item.getItemId();

        if (id == R.id.student_home) {
            Intent intent = new Intent(StudentProfile.this, StudentActivity.class);
            startActivity(intent);

            return true;
        }

        if (id == R.id.student_searchteacher) {
            Intent intent = new Intent(StudentProfile.this, TeacherListActivity.class);
            startActivity(intent);

            return true;
        }

        if (id == R.id.student_profile) {
            Intent intent = new Intent(StudentProfile.this, StudentProfile.class);
            startActivity(intent);
            return true;
        }
        if (id == R.id.student_disconect) {
            Intent intent = new Intent(StudentProfile.this, disconect_forstudent.class);
            startActivity(intent);
            return true;
        }
        if (id == R.id.student_mylesson) {
            Intent intent = new Intent(StudentProfile.this, student_lesson_list.class);
            startActivity(intent);
            return true;
        }
        if (id == R.id.student_adut) {
            Intent intent = new Intent(StudentProfile.this, AdutActivity.class);
            startActivity(intent);
            return true;
        }
        return super.onOptionsItemSelected(item);
    }
}
