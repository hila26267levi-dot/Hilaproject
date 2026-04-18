package com.hila.myapplication.screens;

import static android.widget.Toast.LENGTH_LONG;

import android.content.Intent;
import android.os.Bundle;
import android.util.Log;
import android.view.Menu;
import android.view.MenuItem;
import android.widget.TextView;
import android.widget.Toast;

import androidx.activity.EdgeToEdge;
import androidx.appcompat.app.AppCompatActivity;
import androidx.core.graphics.Insets;
import androidx.core.view.ViewCompat;
import androidx.core.view.WindowInsetsCompat;
import androidx.recyclerview.widget.LinearLayoutManager;
import androidx.recyclerview.widget.RecyclerView;

import com.google.firebase.auth.FirebaseAuth;
import com.hila.myapplication.R;
import com.hila.myapplication.adapters.TeacherLessonAdapter;
import com.hila.myapplication.model.TeacherLesson;
import com.hila.myapplication.servicses.DatabaseService;

import java.util.ArrayList;
import java.util.List;

public class student_lesson_list extends AppCompatActivity {

    private static final String TAG = "StudentLessonList";
    private TeacherLessonAdapter teacherLessonAdapter;

    private DatabaseService databaseService;

    RecyclerView rvStudentLessonList;//מציג את כל השעורים וממחזר את התצוגה שקיימת למידע החדש כאשר נגלול
    List<TeacherLesson> lessonList = new ArrayList<>();//רשימה של שעורים של מורה
    FirebaseAuth mAuth;// בזה נשתמש כדי לקחת את הזהות של המורה ככה נעשה לפי מורה זה רשימה של שעורים בשבילו
    String sid="";


    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        EdgeToEdge.enable(this);
        setContentView(R.layout.activity_student_lesson_list);
        ViewCompat.setOnApplyWindowInsetsListener(findViewById(R.id.main), (v, insets) -> {
            Insets systemBars = insets.getInsets(WindowInsetsCompat.Type.systemBars());
            v.setPadding(systemBars.left, systemBars.top, systemBars.right, systemBars.bottom);
            return insets;
        });

        rvStudentLessonList = findViewById(R.id.rcStudent_lesson_List);
        rvStudentLessonList.setLayoutManager(new LinearLayoutManager(this));
//
//




        teacherLessonAdapter = new TeacherLessonAdapter(lessonList, new TeacherLessonAdapter.OnLessonClickListener() {
            @Override
            public void onLessonClick(TeacherLesson lesson) {
// מורה לחיצה מביאה לעמוד עריכת השיעור
//
//
//
                //    Intent intent=new Intent(student_lesson_list.this,SetLesson.class);

                //  intent.putExtra("Lesson",lesson);

                //   startActivity(intent);


           }



            //מחיקת שיעור צריך להוסיף כזה תנאי גם למנהל מוסיפה תנאי ואז בודקת אם מדובר במנהל
           @Override
           public void onLongLessonClick(TeacherLesson lesson) {




           }




        });
//
        rvStudentLessonList.setAdapter(teacherLessonAdapter);





        databaseService=DatabaseService.getInstance();


            mAuth = FirebaseAuth.getInstance();
            sid = mAuth.getUid();







        databaseService.getStudentLessonList(sid, new DatabaseService.DatabaseCallback<List<TeacherLesson>>() {
            @Override
            public void onCompleted(List<TeacherLesson> lessonList2) {

                if(lessonList2!=null&& lessonList2.size()>0) {

                    lessonList.addAll(lessonList2);


                    teacherLessonAdapter.notifyDataSetChanged();
                }

            }

            @Override
            public void onFailed(Exception e) {
                Toast.makeText(student_lesson_list.this,"noLessons",LENGTH_LONG).show();

            }
        });







    }


    //   של תלמיד תפריט צד
    @Override
    public boolean onCreateOptionsMenu(Menu menu) {
        getMenuInflater().inflate(R.menu.student_menu, menu);
        return true;
    }

    @Override
    public boolean onOptionsItemSelected(MenuItem item) {

        int id = item.getItemId();

        if (id == R.id.student_home) {
            Intent intent = new Intent(student_lesson_list.this, StudentActivity.class);
            startActivity(intent);

            return true;
        }

        if (id == R.id.student_searchteacher) {
            Intent intent = new Intent(student_lesson_list.this, TeacherListActivity.class);
            startActivity(intent);

            return true;
        }

        if (id == R.id.student_profile) {
            Intent intent = new Intent(student_lesson_list.this, StudentProfile.class);
            startActivity(intent);
            return true;
        }
        if (id == R.id.student_disconect) {
            Intent intent = new Intent(student_lesson_list.this, disconect_forstudent.class);
            startActivity(intent);
            return true;
        }
        if (id == R.id.student_mylesson) {
            Intent intent = new Intent(student_lesson_list.this, student_lesson_list.class);
            startActivity(intent);
            return true;
        }
        if (id == R.id.student_adut) {
            Intent intent = new Intent(student_lesson_list.this, AdutActivity.class);
            startActivity(intent);
            return true;
        }

        return super.onOptionsItemSelected(item);
    }
}


