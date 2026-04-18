package com.hila.myapplication.screens;

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
import android.widget.RadioButton;
import android.widget.RadioGroup;
import android.widget.Spinner;
import android.widget.TextView;
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

public class Addnewlesson extends AppCompatActivity implements View.OnClickListener {

    private static final String TAG = "RegisterActivity";


    private Button btnlesson;


    DatabaseService databaseService;

    EditText   edittext_subgect,edittext_time,edittext_date,et_class,edittext_price;

    Teacher teacher=null; ;

    Spinner sp_teachway ;

    String subject="";

    String ifzoom=""  ;

    CheckBox ck_zoom ;
    private String kite="";


    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        EdgeToEdge.enable(this);
        setContentView(R.layout.activity_addnewlesson);
        ViewCompat.setOnApplyWindowInsetsListener(findViewById(R.id.main), (v, insets) -> {
            Insets systemBars = insets.getInsets(WindowInsetsCompat.Type.systemBars());
            v.setPadding(systemBars.left, systemBars.top, systemBars.right, systemBars.bottom);
            return insets;
        });

        databaseService=DatabaseService.getInstance();
        databaseService.getTeacher(new DatabaseService.DatabaseCallback<Teacher>() {
            @Override
            public void onCompleted(Teacher object) {
                teacher=object;

            }

            @Override
            public void onFailed(Exception e) {

            }
        });
        et_class = findViewById(R.id.etGradeClass);
        edittext_time = findViewById(R.id.et_timelesson_addlesson);
        edittext_subgect = findViewById(R.id.subjectlesson_addlesson);
        edittext_date= findViewById(R.id.et_datelesson_addlesson);
        edittext_price= findViewById(R.id.et_pricelesson_addlesson);
        sp_teachway= findViewById(R.id.sp_Class_addlesson);
        sp_teachway.setOnItemSelectedListener(new AdapterView.OnItemSelectedListener() {
             @Override
             public void onItemSelected(AdapterView<?> parent, View view, int position, long id) {
                 if(position>0) {

                     String kit= (String) parent.getItemAtPosition(position);
                     kite +=kit+", ";

                     et_class.setText(kite);

                 }
             }

             @Override
             public void onNothingSelected(AdapterView<?> parent) {

             }
         });


        btnlesson= findViewById(R.id.button_addlesson);
        ck_zoom = findViewById(R.id.ck_zoom);
        ck_zoom.setOnCheckedChangeListener(new CompoundButton.OnCheckedChangeListener() {
            @Override
            public void onCheckedChanged(@NonNull CompoundButton buttonView, boolean isChecked) {


                if (isChecked) {
                    ifzoom = "למידה בזום" ;
                }
                else
                {
                    ifzoom = "מגיע לבית התלממיד ";                }
            }
        });


        /// set the click listener
        btnlesson.setOnClickListener(this);//זה אומר שצריך לעשות פעולב כאשר נלחץ על כפתור
        // כתוב this כי זה  בעמוד הז ה לכל view מסויים
    }

    @Override
    public void onClick(View v) {//זה הפעולה עצמה לכפתור

            Log.d(TAG, "onClick: Register button clicked");

            /// get the input from the user
            String subject =  edittext_subgect .getText().toString();
            String date = edittext_date.getText().toString();
            String time = edittext_time.getText().toString();
          //  String kita = sp_teachway.getSelectedItem().toString();
        double price = Double.parseDouble(edittext_price.getText().toString());
            String lesson_id=databaseService.generatelessonId();



        TeacherLesson  teacherLesson=new TeacherLesson( lesson_id,teacher,subject,ifzoom,time,date,"availbale",kite,price) ;

databaseService.createNewLesson(teacherLesson, new DatabaseService.DatabaseCallback<Void>() {
    @Override
    public void onCompleted(Void object) {

        Intent intent = new Intent(Addnewlesson.this, Addnewlesson.class);
        startActivity(intent);

    }

    @Override
    public void onFailed(Exception e) {

    }
});




            Log.d(TAG, "onClick: Registering user...");




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
            Intent intent = new Intent(Addnewlesson.this,TeacherActivity.class);
            startActivity(intent);

            return true;
        }

        if (id == R.id.teacher_profile) {
            Intent intent = new Intent(Addnewlesson.this, teacher_profile.class);
            startActivity(intent);

            return true;
        }

        if (id == R.id.teacher_mylesson) {
            Intent intent = new Intent(Addnewlesson.this, TeacherLessonsList.class);
            startActivity(intent);
            return true;
        }
        if (id == R.id.teacher_disconect) {
            Intent intent = new Intent(Addnewlesson.this, disconect_forteacher.class);
            startActivity(intent);
            return true;
        }
        if (id == R.id.teacher_adut) {
            Intent intent = new Intent(Addnewlesson.this, AdutActivity.class);
            startActivity(intent);
            return true;
        }
        return super.onOptionsItemSelected(item);
    }
        
    }




