package com.hila.myapplication.screens;

import static android.widget.Toast.LENGTH_LONG;

import android.content.Intent;
import android.os.Bundle;
import android.util.Log;
import android.view.Menu;
import android.view.MenuItem;
import android.view.View;
import android.widget.AdapterView;
import android.widget.Button;
import android.widget.CheckBox;
import android.widget.CompoundButton;
import android.widget.EditText;
import android.widget.Spinner;
import android.widget.Toast;

import androidx.activity.EdgeToEdge;
import androidx.annotation.NonNull;
import androidx.appcompat.app.AppCompatActivity;
import androidx.core.graphics.Insets;
import androidx.core.view.ViewCompat;
import androidx.core.view.WindowInsetsCompat;

import com.hila.myapplication.R;
import com.hila.myapplication.model.Teacher;
import com.hila.myapplication.model.TeacherLesson;
import com.hila.myapplication.servicses.DatabaseService;

public class EditLesson extends AppCompatActivity implements View.OnClickListener {

    private DatabaseService databaseService;
    Intent takeit;

    TeacherLesson theLesson = null;

    private Button btnlesson;


    EditText edittext_subgect, edittext_time, edittext_date, et_class,editText_price;

    Teacher teacher = null;
    ;
double price ;
    Spinner sp_teachway;

    String subject = "";

    String ifzoom = "";

    CheckBox ck_zoom;
    private String kite = "";

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        EdgeToEdge.enable(this);
        setContentView(R.layout.activity_edit_lesson);
        ViewCompat.setOnApplyWindowInsetsListener(findViewById(R.id.main), (v, insets) -> {
            Insets systemBars = insets.getInsets(WindowInsetsCompat.Type.systemBars());
            v.setPadding(systemBars.left, systemBars.top, systemBars.right, systemBars.bottom);
            return insets;
        });


        databaseService = DatabaseService.getInstance();

        initViews();

        takeit = getIntent();
        theLesson = (TeacherLesson) takeit.getSerializableExtra("Lesson");


        if (theLesson != null) {

            et_class.setText(theLesson.getKita());
            edittext_subgect.setText(theLesson.getSubject());
            edittext_time.setText(theLesson.getTime());
            edittext_date.setText(theLesson.getDate());
            editText_price.setText(String.valueOf(theLesson.getPrice()));
           ifzoom=  theLesson.getZoomORhome();
            if( ifzoom.equals("למידה בזום"))
                ck_zoom.setChecked(true);
            else  ck_zoom.setChecked(false);
        }
    }

    private void initViews() {

        et_class = findViewById(R.id.etGradeClassE);
        edittext_time = findViewById(R.id.et_timelesson_addlessonE);
        edittext_subgect = findViewById(R.id.subjectlesson_addlessonE);
        edittext_date = findViewById(R.id.et_datelesson_addlessonE);
        editText_price= findViewById(R.id.et_pricelesson_addlesson);
        btnlesson = findViewById(R.id.button_addlessonE);
        ck_zoom = findViewById(R.id.ck_zoomE);
        sp_teachway = findViewById(R.id.sp_Class_addlessonE);
        sp_teachway.setOnItemSelectedListener(new AdapterView.OnItemSelectedListener() {
            @Override
            public void onItemSelected(AdapterView<?> parent, View view, int position, long id) {
                if (position > 0) {

                    String kit = (String) parent.getItemAtPosition(position);
                    kite += kit + ", ";

                    et_class.setText(kite);


                }
            }

            @Override
            public void onNothingSelected(AdapterView<?> parent) {

            }
        });



        ck_zoom.setOnCheckedChangeListener(new CompoundButton.OnCheckedChangeListener() {
            @Override
            public void onCheckedChanged(@NonNull CompoundButton buttonView, boolean isChecked) {


                if (isChecked) {
                    ifzoom = "למידה בזום";
                } else {
                    ifzoom = "מגיע לבית התלמיד ";
                }
            }
        });


        /// set the click listener
        btnlesson.setOnClickListener(this);//זה אומר שצריך לעשות פעולב כאשר נלחץ על כפתור
        // כתוב this כי זה  בעמוד הז ה לכל view מסויים
    }

    @Override
    public void onClick(View v) {
        Log.d("TAG", "onClick: Register button clicked");

        /// get the input from the user
        String subject = edittext_subgect.getText().toString();
        String date = edittext_date.getText().toString();
        String time = edittext_time.getText().toString();
        //  String kita = sp_teachway.getSelectedItem().toString();

        if(subject.isEmpty())//תנאים
        {
            Toast.makeText(this,"חובה לבחור מקצוע",LENGTH_LONG).show();
        }
        else if (date.isEmpty())
        {
            Toast.makeText(this,"חובה לבחור תאריך",LENGTH_LONG).show();
        }
        else if (time.isEmpty())
        {
            Toast.makeText(this,"חובה לבחור שעה",LENGTH_LONG).show();
        }






        TeacherLesson teacherLesson = new TeacherLesson(theLesson.getId(), theLesson.getTeacher(), subject, ifzoom, time, date, "availbale", kite,price);

        databaseService.updateLesson(teacherLesson, new DatabaseService.DatabaseCallback<Void>() {
            @Override
            public void onCompleted(Void object) {

                Intent intent = new Intent(EditLesson.this, TeacherActivity.class);
                startActivity(intent);

            }

            @Override
            public void onFailed(Exception e) {

            }


        });
    }

    //   של מורה תפריט צד
    @Override
    public boolean onCreateOptionsMenu(Menu menu) {
        getMenuInflater().inflate(R.menu.teacher_menu, menu);
        return true;
    }

    @Override
    public boolean onOptionsItemSelected(MenuItem item) {

        int id = item.getItemId();

        if (id == R.id.teacher_home) {
            Intent intent = new Intent(EditLesson.this,TeacherActivity.class);
            startActivity(intent);

            return true;
        }

        if (id == R.id.teacher_profile) {
            Intent intent = new Intent(EditLesson.this, teacher_profile.class);
            startActivity(intent);

            return true;
        }

        if (id == R.id.teacher_mylesson) {
            Intent intent = new Intent(EditLesson.this, TeacherLessonsList.class);
            startActivity(intent);
            return true;
        }
        if (id == R.id.teacher_disconect) {
            Intent intent = new Intent(EditLesson.this, disconect_forteacher.class);
            startActivity(intent);
            return true;
        }
        if (id == R.id.teacher_adut) {
            Intent intent = new Intent(EditLesson.this, AdutActivity.class);
            startActivity(intent);
            return true;
        }
        return super.onOptionsItemSelected(item);
    }

}
