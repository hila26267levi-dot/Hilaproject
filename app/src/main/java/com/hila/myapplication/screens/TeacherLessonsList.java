package com.hila.myapplication.screens;
import android.content.DialogInterface;
import android.content.Intent;
import android.net.Uri;
import android.os.Bundle;
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

public class TeacherLessonsList extends AppCompatActivity {

    private static final String TAG = "UsersListActivity";
    private TeacherLessonAdapter teacherLessonAdapter;
    private DatabaseService databaseService;

    RecyclerView rvLessonList;
    List<TeacherLesson> lessonList = new ArrayList<>();
    List<TeacherLesson> filterlist = new ArrayList<>();
    FirebaseAuth mAuth;
    String tid = "";
    private Intent takeit;
    boolean theOwner = false;

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

        databaseService = DatabaseService.getInstance();

        takeit = getIntent();
        tid = takeit.getStringExtra("teacherId");

        if (tid == null || tid.isEmpty()) {
            mAuth = FirebaseAuth.getInstance();
            tid = mAuth.getUid();
            theOwner = true;

        }

        // שליפת רשימת שיעורים מ-Firebase
        databaseService.getThecherLessonList(tid, new DatabaseService.DatabaseCallback<List<TeacherLesson>>() {
            @Override
            public void onCompleted(List<TeacherLesson> lessonList2) {

                lessonList.addAll(lessonList2);

                setFilterlist();
            }

            @Override
            public void onFailed(Exception e) {
            }
        });

        // הגדרת האדפטר
        teacherLessonAdapter = new TeacherLessonAdapter(lessonList, new TeacherLessonAdapter.OnLessonClickListener() {

            @Override
            public void onLessonClick(TeacherLesson lesson) {
                if (theOwner) {
                    Intent intent = new Intent(TeacherLessonsList.this, EditLesson.class);
                    intent.putExtra("Lesson", lesson);
                    startActivity(intent);
                } else {
                    Intent intent = new Intent(TeacherLessonsList.this, SetLesson.class);
                    intent.putExtra("Lesson", lesson);
                    startActivity(intent);
                }
            }

            @Override
            public void onLongLessonClick(TeacherLesson lesson) {
                if (theOwner) {
                    AlertDialog.Builder builder = new AlertDialog.Builder(TeacherLessonsList.this);
                    builder.setTitle("מחיקת שיעור");
                    builder.setMessage("האם אתה בטוח שברצונך למחוק את השיעור?");

                    builder.setPositiveButton("כן, מחק", new DialogInterface.OnClickListener() {
                        @Override
                        public void onClick(DialogInterface dialog, int which) {
                            lessonList.remove(lesson);
                            teacherLessonAdapter.notifyDataSetChanged();
                            sendSmsToStudent(lesson);
                            databaseService.deleteLessonForStudent(lesson, new DatabaseService.DatabaseCallback<Void>() {
                                @Override
                                public void onCompleted(Void object) {
                                    Intent intent = new Intent(TeacherLessonsList.this, TeacherActivity.class);
                                    startActivity(intent);
                                }

                                @Override
                                public void onFailed(Exception e) {
                                }
                            });
                        }
                    });

                    builder.setNegativeButton("ביטול", new DialogInterface.OnClickListener() {
                        @Override
                        public void onClick(DialogInterface dialog, int which) {
                            dialog.dismiss();
                        }
                    });

                    builder.show();
                }
            }
        });

        rvLessonList.setAdapter(teacherLessonAdapter);
    }


    private void setFilterlist(){


        for (TeacherLesson lesson : lessonList) {
            if (theOwner) {
                // מורה רואה את כל השיעורים שלו
                filterlist.add(lesson);
            } else {
                // תלמיד רואה רק שיעורים פנויים
                if (lesson.getStatus() == null || lesson.getStatus().equals("availbale")) {
                    filterlist.add(lesson);
                }
            }
        }

        teacherLessonAdapter.setLessonList(filterlist);
        teacherLessonAdapter.notifyDataSetChanged();
    }

    private void sendSmsToStudent(TeacherLesson lesson) {
        if (lesson.getStudent() != null && lesson.getStudent().getPhone() != null) {
            String message =
                    "שלום " + lesson.getStudent().getFname()
                            + " " + lesson.getStudent().getLname() + ",\n"
                            + "לידיעתך, השיעור הבא בוטל:\n"
                            + "מקצוע: " + lesson.getSubject() + "\n"
                            + "תאריך: " + lesson.getDate() + "\n"
                            + "שעה: " + lesson.getTime() + "\n"
                            + "מורה: " + lesson.getTeacher().getFname()
                            + " " + lesson.getTeacher().getLname() + "\n"
                            + "בברכה";

            Intent smsIntent = new Intent(Intent.ACTION_SENDTO);
            smsIntent.setData(Uri.parse("smsto:" + lesson.getStudent().getPhone()));
            smsIntent.putExtra("sms_body", message);
            startActivity(smsIntent);
        }
    }
}



