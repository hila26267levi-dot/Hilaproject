package com.hila.myapplication.screens;

import android.content.Intent;
import android.os.Bundle;
import android.view.Menu;
import android.view.MenuItem;
import android.view.View;
import android.widget.Button;
import android.widget.ImageView;
import android.widget.TextView;
import android.widget.Toast;

import androidx.activity.EdgeToEdge;
import androidx.appcompat.app.AppCompatActivity;
import androidx.core.graphics.Insets;
import androidx.core.view.ViewCompat;
import androidx.core.view.WindowInsetsCompat;

import com.google.firebase.auth.FirebaseAuth;
import com.hila.myapplication.R;
import com.hila.myapplication.adapters.ImageUtil;
import com.hila.myapplication.model.Teacher;
import com.hila.myapplication.servicses.DatabaseService;

public class teacher_profile extends AppCompatActivity {

    TextView tvFname, tvLname, tvClassteach, tvprofession, tvzoom, tvage, tvprice;
    Intent takeit;
    Teacher currntTeacher = null;
    Button btn_E_profile;
    String tId = "";

    ImageView img_teacher_pofile;


    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        EdgeToEdge.enable(this);
        setContentView(R.layout.activity_teacher_profile);
        ViewCompat.setOnApplyWindowInsetsListener(findViewById(R.id.main), (v, insets) -> {
            Insets systemBars = insets.getInsets(WindowInsetsCompat.Type.systemBars());
            v.setPadding(systemBars.left, systemBars.top, systemBars.right, systemBars.bottom);
            return insets;
        });

        initViews();
        DatabaseService databaseService = DatabaseService.getInstance();

        // קבלת teacherId מה-Intent, אם אין — משתמשים ב-UID של המשתמש המחובר
        takeit = getIntent();
        String intentId = takeit.getStringExtra("teacherId");
        if (intentId != null && !intentId.isEmpty()) {
            tId = intentId;
        } else {
            FirebaseAuth auth = FirebaseAuth.getInstance();
            if (auth.getCurrentUser() != null) {
                tId = auth.getCurrentUser().getUid();
            }
        }

        if (!tId.isEmpty()) {
            databaseService.getTeacher(tId, new DatabaseService.DatabaseCallback<Teacher>() {
                @Override
                public void onCompleted(Teacher teacher) {
                    currntTeacher = teacher;
                    if (teacher != null) {
                        tvFname.setText(teacher.getFname());
                        tvLname.setText(teacher.getLname());
                        tvClassteach.setText(teacher.getTeachclass());
                        tvprofession.setText(teacher.getSubject());
                        tvzoom.setText(teacher.getZoom());
                        tvage.setText(teacher.getAge());
                        tvprice.setText(teacher.getPrice() + "");
                        if(teacher.getPic()!=null){
                        img_teacher_pofile.setImageBitmap(ImageUtil.convertFromivIPic(teacher.getPic()));
                    } }
                        else {
                        Toast.makeText(teacher_profile.this,
                                "לא נמצאו פרטי מורה", Toast.LENGTH_SHORT).show();
                    }
                }

                @Override
                public void onFailed(Exception e) {
                    Toast.makeText(teacher_profile.this,
                            "שגיאה בטעינת פרטי המורה", Toast.LENGTH_SHORT).show();
                }
            });
        }

        // כפתור עריכת פרופיל
        btn_E_profile.setOnClickListener(v -> {
            Intent intent = new Intent(teacher_profile.this, Teacher_edit_profile.class);
            startActivity(intent);
        });
    }

    private void initViews() {
        tvFname = findViewById(R.id.tvProfileTeacher_Fname);
        tvLname = findViewById(R.id.tvProfileTeacher_Lname);
        tvClassteach = findViewById(R.id.tvProfileTeacher_teachclass);
        tvprofession = findViewById(R.id.tvProfileTeacher_subject);
        tvzoom = findViewById(R.id.tvProfileTeacher_zoom);
        tvage = findViewById(R.id.tvProfileTeacher_age);
        tvprice = findViewById(R.id.tvProfileTeacher_price);
        btn_E_profile = findViewById(R.id.btn_E_teacher_profile);

        img_teacher_pofile = findViewById(R.id.img_TeacherProfile);
    }

    @Override
    public boolean onCreateOptionsMenu(Menu menu) {
        getMenuInflater().inflate(R.menu.teacher_menu, menu);
        return true;
    }

    @Override
    public boolean onOptionsItemSelected(MenuItem item) {
        int id = item.getItemId();
        if (id == R.id.teacher_home) {
            startActivity(new Intent(this, TeacherActivity.class));
            return true;
        }
        if (id == R.id.teacher_profile) {
            startActivity(new Intent(this, teacher_profile.class));
            return true;
        }
        if (id == R.id.teacher_mylesson) {
            startActivity(new Intent(this, TeacherLessonsList.class));
            return true;
        }
        if (id == R.id.teacher_disconect) {
            startActivity(new Intent(this, disconect_forteacher.class));
            return true;
        }
        if (id == R.id.teacher_adut) {
            startActivity(new Intent(this, AdutActivity.class));
            return true;
        }
        return super.onOptionsItemSelected(item);
    }
}
