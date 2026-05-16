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

        teacherLessonAdapter = new TeacherLessonAdapter(lessonList,
                new TeacherLessonAdapter.OnLessonClickListener() {

                    @Override
                    public void onLessonClick(TeacherLesson lesson) {
                    }

                    @Override
                    public void onLongLessonClick(TeacherLesson lesson) {

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

                                        if (lesson.getStudent() == null) {
                                            Toast.makeText(student_lesson_list.this,
                                                    "שיעור זה אינו תפוס, אין צורך לבטל",
                                                    Toast.LENGTH_SHORT).show();
                                            return;
                                        }

                                        lessonList.remove(lesson);
                                        teacherLessonAdapter.notifyDataSetChanged();
                                        sendSmsToTeacher(lesson);
                                        lesson.setStatus("availbale");
                                        databaseService.deleteLessonForStudent(lesson, new DatabaseService.DatabaseCallback<Void>() {
                                            @Override
                                            public void onCompleted(Void object) {
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

    private void sendSmsToTeacher(TeacherLesson lesson) {
        if (lesson.getTeacher() != null
                && lesson.getTeacher().getPhone() != null
                && lesson.getStudent() != null) {
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
            smsIntent.setData(Uri.parse("smsto:" + lesson.getTeacher().getPhone()));
            smsIntent.putExtra("sms_body", message);
            startActivity(smsIntent);
        }
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
            FirebaseAuth.getInstance().signOut();
            Intent go = new Intent(student_lesson_list.this,
                    MainActivity.class);
            startActivity(go);
            finish();
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































