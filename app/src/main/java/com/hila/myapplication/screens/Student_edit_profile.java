package com.hila.myapplication.screens;

import android.content.Intent;
import android.graphics.Bitmap;
import android.net.Uri;
import android.os.Bundle;
import android.provider.MediaStore;
import android.view.Menu;
import android.view.MenuItem;
import android.view.View;
import android.widget.Button;
import android.widget.EditText;
import android.widget.ImageView;
import android.widget.Toast;

import androidx.activity.EdgeToEdge;
import androidx.activity.result.ActivityResultLauncher;
import androidx.activity.result.contract.ActivityResultContracts;
import androidx.appcompat.app.AppCompatActivity;
import androidx.core.graphics.Insets;
import androidx.core.view.ViewCompat;
import androidx.core.view.WindowInsetsCompat;

import com.google.firebase.auth.FirebaseAuth;
import com.hila.myapplication.R;
import com.hila.myapplication.adapters.ImageUtil;
import com.hila.myapplication.model.Student;
import com.hila.myapplication.model.Student;
import com.hila.myapplication.servicses.DatabaseService;

public class Student_edit_profile extends AppCompatActivity implements View.OnClickListener {
    EditText etkita, etfname, etlname, etphone;
    Button btn_save;
    Student currentStudent;
    String uid;

    String kita, fname, lname, phone;
    private DatabaseService databaseService;

    private ActivityResultLauncher<Intent> captureImageLauncher;
    /// Activity result launcher for capturing image from camera

    private Button btnGallery, btnCamera, btnAddItem;
    // constant to compare
    // the activity result code
    int SELECT_PICTURE = 200;
    private ImageView img;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        EdgeToEdge.enable(this);
        setContentView(R.layout.activity_student_edit_profile);


        /// request permission for the camera and storage
        ImageUtil.requestPermission(this);

        /// get the instance of the database service
        databaseService = DatabaseService.getInstance();


        /// register the activity result launcher for capturing image from camera
        captureImageLauncher = registerForActivityResult(
                new ActivityResultContracts.StartActivityForResult(),
                result -> {
                    if (result.getResultCode() == RESULT_OK && result.getData() != null) {
                        Bitmap bitmap = (Bitmap) result.getData().getExtras().get("data");
                        img.setImageBitmap(bitmap);
                    }
                });




        //   String imagePic = ImageUtil.convertTo64Base(img);


        // אתחול Firebase service
        databaseService = DatabaseService.getInstance();
        //חיבור שדות 
        etfname = findViewById(R.id.profile_student_E_Fname);
        etlname = findViewById(R.id.profile_student_E_Lname);
        etphone = findViewById(R.id.profile_student_E_phone);

        etkita = findViewById(R.id.profile_student_E_class);
        btn_save = findViewById(R.id.btnSaveprofile_student);

        btn_save.setOnClickListener(this);
       img=findViewById(R.id.img_StudentProfile);
       img.setOnClickListener(this);

        // קבלת ה-UID של המשתמש הנוכחי
        uid = FirebaseAuth.getInstance().getCurrentUser().getUid();

        // משיכת המורה הנוכחי מהFirebase
        databaseService.getStudent(uid, new DatabaseService.DatabaseCallback<Student>() {
            @Override
            public void onCompleted(Student student) {
                currentStudent = student;
                loadStudentData();

            }

            @Override
            public void onFailed(Exception e) {

            }
        });

        // כפתור שמירה


    }





    // firebaseפונקציה הזאת לוקחת את המורה מה
// וממלאת את הטופס כדי שהמשתמש יראה את הפרטים שלו
    private void loadStudentData() {
        if (currentStudent == null) return;
        etfname.setText(currentStudent.getFname());
        etlname.setText(currentStudent.getLname());
        etphone.setText(currentStudent.getPhone());

        etkita.setText(currentStudent.getKita());
    }

    // שמירת העריכה
    private void saveStudentProfile() {
        if (currentStudent == null) return;

        // עדכון האובייקט
     fname=   etfname.getText().toString();

     //בדיקה
       lname=etlname.getText().toString();

       phone=etphone.getText().toString();
       kita=etkita.getText().toString();



        currentStudent.setFname(fname);
        currentStudent.setKita(kita);
        currentStudent.setPhone(phone);
        currentStudent.setLname(lname);
    // עדכון בFirebase
     databaseService.updateStudent(currentStudent,new DatabaseService.DatabaseCallback<Void>()

    {
        @Override
        public void onCompleted (Void object){
            Intent intent = new Intent(Student_edit_profile.this, StudentActivity.class);
            startActivity(intent);



        }

        @Override
        public void onFailed (Exception e){

    }
    });
}



        //   של תלמיד תפריט צד
        @Override
        public boolean onCreateOptionsMenu (Menu menu){
            getMenuInflater().inflate(R.menu.main_menu, menu);
            return true;
        }

        @Override
        public boolean onOptionsItemSelected (MenuItem item){

            int id = item.getItemId();

            if (id == R.id.student_home) {
                Intent intent = new Intent(Student_edit_profile.this, StudentActivity.class);
                startActivity(intent);

                return true;
            }

            if (id == R.id.student_searchteacher) {
                Intent intent = new Intent(Student_edit_profile.this, TeacherListActivity.class);
                startActivity(intent);

                return true;
            }

            if (id == R.id.student_profile) {
                Intent intent = new Intent(Student_edit_profile.this, StudentProfile.class);
                startActivity(intent);
                return true;
            }
            if (id == R.id.student_disconect) {
                Intent intent = new Intent(Student_edit_profile.this, disconect_forstudent.class);
                startActivity(intent);
                return true;
            }
            if (id == R.id.student_mylesson) {
                Intent intent = new Intent(Student_edit_profile.this, student_lesson_list.class);
                startActivity(intent);
                return true;
            }
            if (id == R.id.student_adut) {
                Intent intent = new Intent(Student_edit_profile.this, AdutActivity.class);
                startActivity(intent);
                return true;
            }

            return super.onOptionsItemSelected(item);
        }

    @Override
    public void onClick(View v) {
        if(v==btn_save) {
            saveStudentProfile();
        }
      else  if(v==img){
            imageChooser();



        }
    }
    void imageChooser() {

        // create an instance of the
        // intent of the type image
        Intent i = new Intent();
        i.setType("image/*");
        i.setAction(Intent.ACTION_GET_CONTENT);

        // pass the constant to compare it
        // with the returned requestCode
        startActivityForResult(Intent.createChooser(i, "Select Picture"), SELECT_PICTURE);
    }

    // this function is triggered when user
    // selects the image from the imageChooser
    public void onActivityResult(int requestCode, int resultCode, Intent data) {
        super.onActivityResult(requestCode, resultCode, data);

        if (resultCode == RESULT_OK) {

            // compare the resultCode with the
            // SELECT_PICTURE constant
            if (requestCode == SELECT_PICTURE) {
                // Get the url of the image from data
                Uri selectedImageUri = data.getData();
                if (null != selectedImageUri) {
                    // update the preview image in the layout
                    img.setImageURI(selectedImageUri);
                }
            }
        }
    }


}
