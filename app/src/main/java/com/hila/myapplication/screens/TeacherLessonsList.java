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
import java.text.SimpleDateFormat;
import java.util.ArrayList;
import java.util.Calendar;
import java.util.Date;
import java.util.List;
import java.util.Locale;
import android.view.Menu;
import android.view.MenuItem;

public class TeacherLessonsList extends AppCompatActivity {

    private static final String TAG = "UsersListActivity";
    private TeacherLessonAdapter teacherLessonAdapter;
    private DatabaseService databaseService;

    RecyclerView rvLessonList;
    List<TeacherLesson> lessonList = new ArrayList<>();
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

        databaseService.getThecherLessonList(tid, new DatabaseService.DatabaseCallback<List<TeacherLesson>>() {
            @Override
            public void onCompleted(List<TeacherLesson> lessonList2) {
                // תאריך היום בפורמט dd.MM
                Calendar todayCal = Calendar.getInstance();
                int todayDay = todayCal.get(Calendar.DAY_OF_MONTH);
                int todayMonth = todayCal.get(Calendar.MONTH) + 1; // ינואר = 0

                for (TeacherLesson lesson : lessonList2) {

                    if (theOwner) {
                        // מורה רואה את כל השיעורים שלו ללא סינון
                        lessonList.add(lesson);
                    } else {
                        // תלמיד — רואה רק שיעורים פנויים שהתאריך לא עבר
                        boolean dateExpired = false;
                        try {
                            if (lesson.getDate() != null && !lesson.getDate().isEmpty()) {
                                // פורמט dd.MM
                                String[] parts = lesson.getDate().split("\\.");
                                if (parts.length == 2) {
                                    int lessonDay = Integer.parseInt(parts[0].trim());
                                    int lessonMonth = Integer.parseInt(parts[1].trim());

                                    if (lessonMonth < todayMonth) {
                                        dateExpired = true;
                                    } else if (lessonMonth == todayMonth && lessonDay < todayDay) {
                                        dateExpired = true;
                                    }
                                }
                            }
                        } catch (Exception e) {
                            // תאריך לא תקין — מציג את השיעור
                        }

                        if (!dateExpired &&
                                (lesson.getStatus() == null || lesson.getStatus().equals("availbale"))) {
                            lessonList.add(lesson);
                        }
                    }
                }
                teacherLessonAdapter.notifyDataSetChanged();
            }

            @Override
            public void onFailed(Exception e) {
            }
        });

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

    @Override
    public boolean onCreateOptionsMenu(Menu menu) {
        getMenuInflater().inflate(R.menu.teacher_menu, menu);
        return true;
    }

    @Override
    public boolean onOptionsItemSelected(MenuItem item) {
        int id = item.getItemId();

        if (id == R.id.teacher_home) {
            Intent intent = new Intent(TeacherLessonsList.this, TeacherActivity.class);
            startActivity(intent);
            return true;
        }
        if (id == R.id.teacher_profile) {
            Intent intent = new Intent(TeacherLessonsList.this, teacher_profile.class);
            startActivity(intent);
            return true;
        }
        if (id == R.id.teacher_mylesson) {
            Intent intent = new Intent(TeacherLessonsList.this, TeacherLessonsList.class);
            startActivity(intent);
            return true;
        }
        if (id == R.id.teacher_disconect) {
            FirebaseAuth.getInstance().signOut();
            Intent go = new Intent(TeacherLessonsList.this,
                    MainActivity.class);
            startActivity(go);
            finish();
        }
        if (id == R.id.teacher_adut) {
            Intent intent = new Intent(TeacherLessonsList.this, AdutActivity.class);
            startActivity(intent);
            return true;
        }
        return super.onOptionsItemSelected(item);
    }
}


