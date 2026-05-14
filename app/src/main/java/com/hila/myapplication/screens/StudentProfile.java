package com.hila.myapplication.screens;

import android.content.Intent;
import android.graphics.Bitmap;
import android.os.Bundle;
import android.view.Menu;
import android.view.MenuItem;
import android.view.View;
import android.widget.Button;
import android.widget.ImageView;
import android.widget.TextView;

import androidx.activity.EdgeToEdge;
import androidx.appcompat.app.AppCompatActivity;

import com.hila.myapplication.R;
import com.hila.myapplication.adapters.ImageUtil;
import com.hila.myapplication.model.Student;
import com.hila.myapplication.servicses.DatabaseService;

public class StudentProfile extends AppCompatActivity implements View.OnClickListener {
    TextView tvFname, tvLname, tvemail, tvclass, tvphone;
    ImageView imgProfile;
    Student currentStudent = null;

    private DatabaseService databaseService;
    Button btn_edit;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_student_profile);

        initViews();

        databaseService = DatabaseService.getInstance();

        databaseService.getStudent(new DatabaseService.DatabaseCallback<Student>() {
            @Override
            public void onCompleted(Student student) {
                currentStudent = student;
                if (currentStudent != null) {
                    tvFname.setText(currentStudent.getFname());
                    tvLname.setText(currentStudent.getLname());
                    tvemail.setText(currentStudent.getEmail());
                    tvclass.setText(currentStudent.getKita() + "");
                    tvphone.setText(currentStudent.getPhone());

                    // הצגת התמונה השמורה
                    if (currentStudent.getPic() != null && !currentStudent.getPic().isEmpty()
                            && !currentStudent.getPic().equals("jjj")) {
                        Bitmap bmp = ImageUtil.convertFromivIPic(currentStudent.getPic());
                        if (bmp != null) {
                            imgProfile.setImageBitmap(bmp);
                        }
                    }
                }
            }

            @Override
            public void onFailed(Exception e) {
            }
        });
    }

    private void initViews() {
        tvFname = findViewById(R.id.tvProfileStudent_Fname);
        tvLname = findViewById(R.id.tvProfileStudent_Lname);
        tvclass = findViewById(R.id.tvProfileStudent_class);
        tvemail = findViewById(R.id.tvProfileStudent_email);
        tvphone = findViewById(R.id.tvProfileStudent_phone);
        imgProfile = findViewById(R.id.img_StudentProfile);
        btn_edit = findViewById(R.id.btn_go_student_profile);
        btn_edit.setOnClickListener(this);
    }

    @Override
    public void onClick(View v) {
        Intent intent = new Intent(StudentProfile.this, Student_edit_profile.class);
        startActivity(intent);
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
            startActivity(new Intent(this, StudentActivity.class));
            return true;
        }
        if (id == R.id.student_searchteacher) {
            startActivity(new Intent(this, TeacherListActivity.class));
            return true;
        }
        if (id == R.id.student_profile) {
            startActivity(new Intent(this, StudentProfile.class));
            return true;
        }
        if (id == R.id.student_disconect) {
            startActivity(new Intent(this, disconect_forstudent.class));
            return true;
        }
        if (id == R.id.student_mylesson) {
            startActivity(new Intent(this, student_lesson_list.class));
            return true;
        }
        if (id == R.id.student_adut) {
            startActivity(new Intent(this, AdutActivity.class));
            return true;
        }
        return super.onOptionsItemSelected(item);
    }
}
