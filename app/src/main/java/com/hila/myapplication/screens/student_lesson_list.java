package com.hila.myapplication.screens;

import static android.widget.Toast.LENGTH_LONG;

import static androidx.core.content.ContextCompat.startActivity;

import android.content.DialogInterface;
import android.content.Intent;
import android.net.Uri;
import android.os.Bundle;
import android.view.Menu;
import android.view.MenuItem;
import android.widget.Toast;

import androidx.activity.EdgeToEdge;
import androidx.appcompat.app.AlertDialog;
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

    RecyclerView rvStudentLessonList;
    List<TeacherLesson> lessonList = new ArrayList<>();
    FirebaseAuth mAuth;
    String sid = "";

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
        databaseService = DatabaseService.getInstance();
        mAuth = FirebaseAuth.getInstance();
        sid = mAuth.getUid();
        // הגדרת האדפטר
        teacherLessonAdapter = new TeacherLessonAdapter(lessonList,
                new TeacherLessonAdapter.OnLessonClickListener() {

                    @Override
                    public void onLessonClick(TeacherLesson lesson) {
                        // לחיצה רגילה — לא עושה כלום כרגע
                    }

                    @Override
                    public void onLongLessonClick(TeacherLesson lesson) {

                        // Dialog אישור ביטול
                        AlertDialog.Builder builder =
                                new AlertDialog.Builder(student_lesson_list.this);
                        builder.setTitle("ביטול שיעור");
                        builder.setMessage("האם אתה בטוח שברצונך לבטל את השיעור?\n"
                                + "מקצוע: " + lesson.getSubject() + "\n"
                                + "תאריך: " + lesson.getDate() + "\n"
                                + "שעה: " + lesson.getTime());

                        builder.setPositiveButton("כן, בטל שיעור",
                                new DialogInterface.OnClickListener() {
                                    @Override
                                    public void onClick(DialogInterface dialog, int which) {

                                        // שלב 1 — מחיקה מהרשימה המקומית
                                        lessonList.remove(lesson);
                                        teacherLessonAdapter.notifyDataSetChanged();

                                        // שלב 2 — מחיקה מ-Firebase של התלמיד
                                        // משתמשת ב-deleteData שכבר קיים ב-DatabaseService
                                        sendSmsToTeacher(lesson);                                                    // שלב 3 — מחזירים שיעור לפנוי אצל המורה

                                        lesson.setStatus("availbale");
                                        databaseService.deleteLessonForStudent(lesson, new DatabaseService.DatabaseCallback<Void>() {
                                            @Override
                                            public void onCompleted(Void object) {

                                                // שלב 4 — SMS למורה

                                            }

                                            @Override
                                            public void onFailed(Exception e) {
                                            }
                                        });
                                    }


                                });


                        builder.setNegativeButton("ביטול",
                                new DialogInterface.OnClickListener() {
                                    @Override
                                    public void onClick(DialogInterface dialog, int which) {
                                        dialog.dismiss();
                                    }
                                });
                        builder.show();
                    }

                });


        rvStudentLessonList.setAdapter(teacherLessonAdapter);

// שליפת שיעורי התלמיד מ-Firebase
        databaseService.getStudentLessonList(sid,
                new DatabaseService.DatabaseCallback<List<TeacherLesson>>() {
                    @Override
                    public void onCompleted(List<TeacherLesson> lessonList2) {
                        if (lessonList2 != null && lessonList2.size() > 0) {
                            lessonList.addAll(lessonList2);
                            teacherLessonAdapter.notifyDataSetChanged();
                        }
                    }

                    @Override
                    public void onFailed(Exception e) {
                        Toast.makeText(student_lesson_list.this,
                                "noLessons", LENGTH_LONG).show();
                    }
                });
    }

// פונקציה לשליחת SMS למורה
private void sendSmsToTeacher(TeacherLesson lesson) {
    if (lesson.getTeacher() != null
            && lesson.getTeacher().getPhone() != null) {
        String message =
                "שלום " + lesson.getTeacher().getFname()
                        + " " + lesson.getTeacher().getLname() + ",\n"
                        + "לידיעתך, התלמיד "
                        + lesson.getStudent().getFname()
                        + " " + lesson.getStudent().getLname()
                        + " ביטל את השיעור:\n"
                        + "מקצוע: " + lesson.getSubject() + "\n"
                        + "תאריך: " + lesson.getDate() + "\n"
                        + "שעה: " + lesson.getTime() + "\n"
                        + "השיעור חזר להיות פנוי.\n"
                        + "בברכה";

        Intent smsIntent = new Intent(Intent.ACTION_SENDTO);
        smsIntent.setData(Uri.parse("smsto:"
                + lesson.getTeacher().getPhone()));
        smsIntent.putExtra("sms_body", message);
        startActivity(smsIntent);
    }
}

// תפריט צד תלמיד
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































