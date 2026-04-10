package com.hila.myapplication.screens;

import android.content.Intent;
import android.os.Bundle;
import android.widget.TextView;

import androidx.activity.EdgeToEdge;
import androidx.appcompat.app.AppCompatActivity;
import androidx.core.graphics.Insets;
import androidx.core.view.ViewCompat;
import androidx.core.view.WindowInsetsCompat;
import androidx.recyclerview.widget.LinearLayoutManager;
import androidx.recyclerview.widget.RecyclerView;

import com.google.firebase.auth.FirebaseAuth;
import com.hila.myapplication.R;
import com.hila.myapplication.adapters.StudentAdapter;
import com.hila.myapplication.adapters.TeacherAdapter;
import com.hila.myapplication.adapters.TeacherLessonAdapter;
import com.hila.myapplication.model.Teacher;
import com.hila.myapplication.model.TeacherLesson;
import com.hila.myapplication.servicses.DatabaseService;

import java.util.ArrayList;
import java.util.List;

public class TeacherLessonsList extends AppCompatActivity {



    private static final String TAG = "UsersListActivity";
    private TeacherLessonAdapter teacherLessonAdapter;

    private DatabaseService databaseService;

    RecyclerView rvLessonList;//מציג את כל השעורים וממחזר את התצוגה שקיימת למידע החדש כאשר נגלול
    List<TeacherLesson> lessonList = new ArrayList<>();//רשימה של שעורים של מורה
    FirebaseAuth mAuth;// בזה נשתמש כדי לקחת את הזהות של המורה ככה נעשה לפי מורה זה רשימה של שעורים בשבילו
    String tid="";
    private Intent takeit;

    boolean theOwner=false;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        EdgeToEdge.enable(this);
        setContentView(R.layout.activity_teacher_lessons_list);
        ViewCompat.setOnApplyWindowInsetsListener(findViewById(R.id.main), (v, insets) -> {
            Insets systemBars = insets.getInsets(WindowInsetsCompat.Type.systemBars());
            v.setPadding(systemBars.left, systemBars.top, systemBars.right, systemBars.bottom);
            return insets;
        });

        rvLessonList = findViewById(R.id.rvThecherLessonList);
        rvLessonList.setLayoutManager(new LinearLayoutManager(this));
//
//


        databaseService=DatabaseService.getInstance();
// from Student
        takeit=getIntent();
        tid=takeit.getStringExtra("teacherId");

// from Teacher
        if(tid==null||tid.isEmpty()) {
            mAuth = FirebaseAuth.getInstance();
            tid = mAuth.getUid();
            theOwner=true;
        }

        // Add lessons



        databaseService.getThecherLessonList(tid, new DatabaseService.DatabaseCallback<List<TeacherLesson>>() {
            @Override
            public void onCompleted(List<TeacherLesson> lessonList2) {
                lessonList.addAll(lessonList2);

                teacherLessonAdapter.notifyDataSetChanged();

            }

            @Override
            public void onFailed(Exception e) {

            }
        });






        teacherLessonAdapter = new TeacherLessonAdapter(lessonList, new TeacherLessonAdapter.OnLessonClickListener() {
            @Override
            public void onLessonClick(TeacherLesson lesson) {
//אם מורה לחיצה מביאה לעמוד עריכת השיעור
                if(theOwner){
                    Intent intent=new Intent(TeacherLessonsList.this,EditLesson.class);

                   intent.putExtra("Lesson",lesson);

                    startActivity(intent);


                }

                // אם זה תלמיד מעביר לעמוד קביעת שיעור
                else{


                    Intent intent=new Intent(TeacherLessonsList.this,SetLesson.class);

                    intent.putExtra("Lesson",lesson);

                    startActivity(intent);


                }


            }
//מחיקת שיעור צריך להוסיף כזה תנאי גם למנהל מוסיפה תנאי ואז בודקת אם מדובר במנהל
            @Override
            public void onLongLessonClick(TeacherLesson lesson) {
                    if(theOwner){
                        lessonList.remove(lesson);
                        teacherLessonAdapter.notifyDataSetChanged();


                    }



            }




        });

        rvLessonList.setAdapter(teacherLessonAdapter);




}
}