package com.hila.myapplication.screens;

import android.content.Intent;
import android.content.SharedPreferences;
import android.os.Bundle;
import android.util.Log;
import android.view.View;
import android.widget.Button;
import android.widget.EditText;
import android.widget.Spinner;
import android.widget.Toast;

import androidx.activity.EdgeToEdge;
import androidx.appcompat.app.AppCompatActivity;

import com.hila.myapplication.R;
import com.hila.myapplication.model.Student;
import com.hila.myapplication.servicses.DatabaseService;

public class RegisterStudentActivity extends AppCompatActivity implements View.OnClickListener {
    private static final String TAG = "RegisterActivity";


    private Button btn_student;


    DatabaseService databaseService;

    EditText edittext_email, edittext_fname, edittext_lname, edittext_password,edittext_phone;

    Spinner spKita;
    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        EdgeToEdge.enable(this);
        setContentView(R.layout.activity_student_register);

        edittext_email = findViewById(R.id.et_student_email);
        edittext_fname = findViewById(R.id.et_student_fname);
        edittext_lname = findViewById(R.id.et_student_lname);
        edittext_password = findViewById(R.id.et_student_password);
        spKita= findViewById(R.id.spstudent_kita);
        edittext_phone= findViewById(R.id.et_student_phone);

        databaseService = DatabaseService.getInstance();

        btn_student = findViewById(R.id.btn_register);
        /// set the click listener
      btn_student.setOnClickListener(this);
    }

    @Override
    public void onClick(View v) {
        if (v.getId() == btn_student.getId()) {
            Log.d(TAG, "onClick: Register button clicked");

            /// get the input from the user
            String email = edittext_email.getText().toString();
            String password = edittext_password.getText().toString();
            String fName = edittext_fname.getText().toString();
            String lName = edittext_lname.getText().toString();
            String kita = spKita.getSelectedItem().toString()+"";
            String phone= edittext_phone.getText().toString();

            Log.d(TAG, "onClick: Registering user...");
            /// Register user
            registerUser(fName, lName, email, password, kita,phone);
        }
    }


    /// Register the user
    private void registerUser(String fname, String lname, String email, String password,String kita,String phone) {
        Log.d(TAG, "registerUser: Registering user...");


        /// create a new user object
        Student student = new Student("99", fname, lname, phone, email,password, kita,"jjj");
        Log.d(TAG,student.toString());
//      proceed to create the user
        createUserInDatabase(student);

    }

    private void createUserInDatabase(Student student) {
        databaseService.createNewStudent(student, new DatabaseService.DatabaseCallback<String>() {
            @Override
            public void onCompleted(String uid) {
                Log.d(TAG, "createUserInDatabase: User created successfully");
                /// save the user to shared preferences
                student.setId(uid);
                SharedPreferences sharedPreferences = getSharedPreferences("myPrefs", MODE_PRIVATE);
                SharedPreferences.Editor editor = sharedPreferences.edit();

                editor.putString("email", student.getEmail());
                editor.putString("password", student.getPassword());

                editor.apply(); //
                Log.d(TAG, "createUserInDatabase: Redirecting to MainActivity");
                /// Redirect to MainActivity and clear back stack to prevent user from going back to register screen
                Intent mainIntent = new Intent(RegisterStudentActivity.this, StudentActivity.class);
                /// clear the back stack (clear history) and start the MainActivity
                mainIntent.setFlags(Intent.FLAG_ACTIVITY_NEW_TASK | Intent.FLAG_ACTIVITY_CLEAR_TASK);
                startActivity(mainIntent);
            }

            @Override
            public void onFailed(Exception e) {
                Log.e(TAG, "createUserInDatabase: Failed to create user", e);
                /// show error message to user
                Toast.makeText(RegisterStudentActivity.this, "Failed to register user", Toast.LENGTH_SHORT).show();
                /// sign out the user if failed to register

            }
        });
    }


}