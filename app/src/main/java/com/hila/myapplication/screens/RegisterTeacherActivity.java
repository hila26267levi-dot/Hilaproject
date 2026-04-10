package com.hila.myapplication.screens;

import android.annotation.SuppressLint;
import android.content.Intent;
import android.content.SharedPreferences;
import android.os.Bundle;
import android.util.Log;
import android.view.View;
import android.widget.AdapterView;
import android.widget.Button;
import android.widget.EditText;
import android.widget.RadioButton;
import android.widget.RadioGroup;
import android.widget.Spinner;
import android.widget.Toast;

import androidx.activity.EdgeToEdge;
import androidx.appcompat.app.AppCompatActivity;

import com.hila.myapplication.R;
import com.hila.myapplication.model.Teacher;
import com.hila.myapplication.servicses.DatabaseService;

public class RegisterTeacherActivity extends AppCompatActivity implements View.OnClickListener, AdapterView.OnItemSelectedListener {
    private static final String TAG = "RegisterActivity";


    private Button btnteacher;


    DatabaseService databaseService;

    EditText edittext_email, edittext_fname, edittext_lname, edittext_phone, edittext_price,  edittext_password, edittext_subject, edittext_age;

    RadioGroup teacher_zoom ;

    RadioButton rbyes, rbno;



    Spinner spteachclass,spsubject ;

 String subject="";
    private View v;
    private String password;
    private String email;

    @SuppressLint({"WrongViewCast", "MissingInflatedId"})
    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        EdgeToEdge.enable(this);
        setContentView(R.layout.activity_teacher_register);//מציג את הדף של התצוגה ורק ככה אפשר לעשות קישור בין המשתניםש יצרתי לכאן למשתנים בעמוד  שהקוד הציג
        edittext_email = findViewById(R.id.et_teacher_email);
        edittext_fname = findViewById(R.id.et_teacher_fname);
        edittext_lname = findViewById(R.id.et_teacher_lname);
        edittext_phone = findViewById(R.id.et_teacher_phone);
        edittext_price = findViewById(R.id.et_teacher_price);
        teacher_zoom = findViewById(R.id.zoomYesNo);
        edittext_password = findViewById(R.id.et_teacher_password);
        spteachclass = findViewById(R.id.et_teacher_teach_class);
        edittext_age = findViewById(R.id.et_teacher_age);
        spsubject = findViewById(R.id. spSubject);
        edittext_subject= findViewById(R.id.etSubjects);
        databaseService = DatabaseService.getInstance();
        rbyes= findViewById(R.id.rbYes);
        rbno= findViewById(R.id.rbNo);



        btnteacher = findViewById(R.id.btn_register);
        /// set the click listener
        btnteacher.setOnClickListener(this);//זה אומר שצריך לעשות פעולב כאשר נלחץ על כפתור
        // כתוב this כי זה  בעמוד הז ה לכל view מסויים
        spsubject.setOnItemSelectedListener(this);

    }

    @Override
    public void onClick(View v) {//זה הפעולה עצמה לכפתור

        String zoom="no";
        if (v.getId() == btnteacher.getId()) {
            Log.d(TAG, "onClick: Register button clicked");

            /// get the input from the user
             email = edittext_email.getText().toString();
             password = edittext_password.getText().toString();
            String fName = edittext_fname.getText().toString();
            String lName = edittext_lname.getText().toString();
            String age = edittext_age.getText().toString();
            String phone = edittext_phone.getText().toString();
            String stprice = edittext_price.getText().toString();
            if (rbyes.isChecked())

                 zoom="yes";
            else  zoom="no";



            String teachclass = spteachclass.getSelectedItem().toString();

            String subject2=edittext_subject.getText().toString();






            Log.d(TAG, "onClick: Registering user...");

            double price = Double.parseDouble(stprice);

            /// Register user
            registerUser(fName, lName, phone, email, password, age,subject2 , price, zoom, teachclass,"jkjk");

        }
    }


    /// Register the user
    private void registerUser(String fname, String lname, String phone, String email, String password, String age,String subject, double price, String zoom, String teachclass,String id) {
        Log.d(TAG, "registerUser: Registering user...");


        /// create a new user object
        Teacher teacher = new Teacher("99", fname, lname, phone, email, password, age,subject,price, zoom, teachclass);
        Log.d(TAG, teacher.toString());

        /// proceed to create the user
        createUserInDatabase(teacher);

    }

    private void createUserInDatabase(Teacher teacher) {
        databaseService.createNewTeacher(teacher, new DatabaseService.DatabaseCallback<String>() {
            @Override
            public void onCompleted(String uid) {
                Log.d(TAG, "createUserInDatabase: User created successfully");
                /// save the user to shared preferences
                teacher.setId(uid);
                SharedPreferences sharedPreferences = getSharedPreferences("myPrefs", MODE_PRIVATE);
                SharedPreferences.Editor editor = sharedPreferences.edit();

                editor.putString("email", email);
                editor.putString("password", password);

                editor.apply(); // or editor.commit();
                Log.d(TAG, "createUserInDatabase: Redirecting to MainActivity");
                /// Redirect to MainActivity and clear back stack to prevent user from going back to register screen
                Intent mainIntent = new Intent(RegisterTeacherActivity.this, TeacherActivity.class);
                /// clear the back stack (clear history) and start the MainActivity
                mainIntent.setFlags(Intent.FLAG_ACTIVITY_NEW_TASK | Intent.FLAG_ACTIVITY_CLEAR_TASK);
                startActivity(mainIntent);
            }

            @Override
            public void onFailed(Exception e) {
                Log.e(TAG, "createUserInDatabase: Failed to create user", e);
                /// show error message to user
                Toast.makeText(RegisterTeacherActivity.this, "Failed to register user", Toast.LENGTH_SHORT).show();
                /// sign out the user if failed to register

            }
        });
    }


    @Override
    public void onItemSelected(AdapterView<?> parent, View view, int position, long id) {
        if(position>0) {

            String subj= (String) parent.getItemAtPosition(position);
            subject +=subj+", ";

            edittext_subject.setText(subject);

        }

    }

    @Override
    public void onNothingSelected(AdapterView<?> parent) {

    }
}