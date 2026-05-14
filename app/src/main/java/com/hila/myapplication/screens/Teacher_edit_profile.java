package com.hila.myapplication.screens;

import android.content.Intent;
import android.graphics.Bitmap;
import android.net.Uri;
import android.os.Bundle;
import android.provider.MediaStore;
import android.view.Menu;
import android.view.MenuItem;
import android.view.View;
import android.widget.AdapterView;
import android.widget.Button;
import android.widget.EditText;
import android.widget.ImageButton;
import android.widget.ImageView;
import android.widget.Spinner;
import android.widget.Toast;

import androidx.activity.EdgeToEdge;
import androidx.activity.result.ActivityResultLauncher;
import androidx.activity.result.contract.ActivityResultContracts;
import androidx.appcompat.app.AppCompatActivity;

import com.google.firebase.auth.FirebaseAuth;
import com.hila.myapplication.R;
import com.hila.myapplication.adapters.ImageUtil;
import com.hila.myapplication.model.Teacher;
import com.hila.myapplication.servicses.DatabaseService;

public class Teacher_edit_profile extends AppCompatActivity implements View.OnClickListener {

    EditText fname, lname, age, price, teachclass, subject;
    Spinner spZoomEdit, spTeachclassEdit, spSubjectEdit;
    ImageButton img_Camera, img_gallery;  // add - לחיץ, פותח גלריה

    ImageView iv_Eteacher;


    /// Activity result launcher for selecting image from gallery
    private ActivityResultLauncher<Intent> selectImageLauncher;
    /// Activity result launcher for capturing image from camera
    private ActivityResultLauncher<Intent> captureImageLauncher;

    Teacher currentTeacher;
    String uid;
    Button btn_save;
    private DatabaseService databaseService;
    private String selectedZoom = "לא";
    int SELECT_PICTURE = 200;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        EdgeToEdge.enable(this);
        setContentView(R.layout.activity_teacher_edit_profile);

        ImageUtil.requestPermission(this);
        databaseService = DatabaseService.getInstance();

        setUpGallery();

        fname      = findViewById(R.id.profile_Teacher_E_Fname);
        lname      = findViewById(R.id.profile_Teacher_E_Lname);
        age        = findViewById(R.id.profile_Teacher_E_age);
        price      = findViewById(R.id.profile_Teacher_E_price);
        teachclass = findViewById(R.id.profile_Teacher_E_teachclass);
        subject    = findViewById(R.id.profile_Teacher_E_subject);
        btn_save   = findViewById(R.id.profile_Teacher_E_btn);
        img_Camera  = findViewById(R.id.img_camara_teacherE_Profile); // add - לחיץ
        img_gallery = findViewById(R.id.img_gallery_teacherE_Profile);       // add2 - עיצוב בלבד
        iv_Eteacher =  findViewById(R.id.iv_Eteacher);

        img_Camera.setOnClickListener(this);
        img_gallery.setOnClickListener(this);
        // רק add לחיץ
        btn_save.setOnClickListener(this);

        spZoomEdit       = findViewById(R.id.spZoomEdit);
        spTeachclassEdit = findViewById(R.id.sp_teachclass_edit);
        spSubjectEdit    = findViewById(R.id.sp_subject_edit);





        spTeachclassEdit.setOnItemSelectedListener(new AdapterView.OnItemSelectedListener() {
            @Override
            public void onItemSelected(AdapterView<?> parent, View view, int position, long id) {
                if (position > 0) {
                    String kit = (String) parent.getItemAtPosition(position);
                    String current = teachclass.getText().toString();
                    if (!current.contains(kit)) {
                        teachclass.setText(current.isEmpty() ? kit : current + ", " + kit);
                    }
                }
            }
            @Override public void onNothingSelected(AdapterView<?> parent) {}
        });

        spSubjectEdit.setOnItemSelectedListener(new AdapterView.OnItemSelectedListener() {
            @Override
            public void onItemSelected(AdapterView<?> parent, View view, int position, long id) {
                if (position > 0) {
                    String subj = (String) parent.getItemAtPosition(position);
                    String current = subject.getText().toString();
                    if (!current.contains(subj)) {
                        subject.setText(current.isEmpty() ? subj : current + ", " + subj);
                    }
                }
            }
            @Override public void onNothingSelected(AdapterView<?> parent) {}
        });

        spZoomEdit.setOnItemSelectedListener(new AdapterView.OnItemSelectedListener() {
            @Override
            public void onItemSelected(AdapterView<?> parent, View view, int position, long id) {
                if (position == 1) selectedZoom = "כן";
                else if (position == 2) selectedZoom = "לא";
            }
            @Override public void onNothingSelected(AdapterView<?> parent) {}
        });

        uid = FirebaseAuth.getInstance().getCurrentUser().getUid();

        databaseService.getTeacher(uid, new DatabaseService.DatabaseCallback<Teacher>() {
            @Override
            public void onCompleted(Teacher teacher) {
                currentTeacher = teacher;
                loadTeacherData();
            }
            @Override
            public void onFailed(Exception e) {
                Toast.makeText(Teacher_edit_profile.this, "שגיאה בטעינת נתונים", Toast.LENGTH_SHORT).show();
            }
        });
    }

    private void loadTeacherData() {
        if (currentTeacher == null) return;

        fname.setText(currentTeacher.getFname());
        lname.setText(currentTeacher.getLname());
        age.setText(currentTeacher.getAge());
        price.setText(String.valueOf(currentTeacher.getPrice()));
        teachclass.setText(currentTeacher.getTeachclass());
        subject.setText(currentTeacher.getSubject());

        // תמונה קיימת מוצגת ב-imgDisplay (add2)
        if (currentTeacher.getPic() != null && !currentTeacher.getPic().isEmpty()
                && !currentTeacher.getPic().equals("jkjk")) {
            Bitmap bmp = ImageUtil.convertFromivIPic(currentTeacher.getPic());
            if (bmp != null) {
                iv_Eteacher.setImageBitmap(bmp);
            }
        }

        String savedZoom = currentTeacher.getZoom();
        if (savedZoom != null) {
            if (savedZoom.equals("כן") || savedZoom.equals("yes")) {
                spZoomEdit.setSelection(1);
                selectedZoom = "כן";
            } else {
                spZoomEdit.setSelection(2);
                selectedZoom = "לא";
            }
        }
    }

    private void saveTeacherProfile() {
        if (currentTeacher == null) return;

        String fnameVal      = fname.getText().toString().trim();
        String lnameVal      = lname.getText().toString().trim();
        String priceVal      = price.getText().toString().trim();
        String ageVal        = age.getText().toString().trim();
        String teachclassVal = teachclass.getText().toString().trim();
        String subjectVal    = subject.getText().toString().trim();

        if (!validateInput(fnameVal, lnameVal, priceVal, ageVal)) return;

        currentTeacher.setFname(fnameVal);
        currentTeacher.setLname(lnameVal);
        currentTeacher.setAge(ageVal);
        currentTeacher.setPrice(Double.parseDouble(priceVal));
        currentTeacher.setTeachclass(teachclassVal);
        currentTeacher.setSubject(subjectVal);
        currentTeacher.setZoom(selectedZoom);

        // שמירת תמונה מ-imgDisplay
        String picBase64 = ImageUtil.convertTo64Base(iv_Eteacher);
        if (picBase64 != null) {
            currentTeacher.setPic(picBase64);
        }

        databaseService.updateTeacher(currentTeacher, new DatabaseService.DatabaseCallback<Void>() {
            @Override
            public void onCompleted(Void object) {
                Toast.makeText(Teacher_edit_profile.this, "הפרופיל עודכן בהצלחה!", Toast.LENGTH_SHORT).show();
                startActivity(new Intent(Teacher_edit_profile.this, TeacherActivity.class));
            }
            @Override
            public void onFailed(Exception e) {
                Toast.makeText(Teacher_edit_profile.this, "שגיאה בשמירה", Toast.LENGTH_SHORT).show();
            }
        });
    }

    @Override
    public void onClick(View v) {
        if (v == btn_save) {
            saveTeacherProfile();
        }

        else if (v == img_gallery) {
            // רק add פותח גלריה

            ImageUtil.requestPermission(Teacher_edit_profile.this);
            imageChooser();
        }



        else if (v ==img_Camera) {

            ImageUtil.requestPermission(Teacher_edit_profile.this);
            captureImageFromCamera();

        }
    }


    private void setUpGallery() {
        /// register the activity result launcher for selecting image from gallery
        selectImageLauncher = registerForActivityResult(
                new ActivityResultContracts.StartActivityForResult(),
                result -> {
                    if (result.getResultCode() == RESULT_OK && result.getData() != null) {
                        Uri selectedImage = result.getData().getData();
                        iv_Eteacher.setImageURI(selectedImage);
                        /// set the tag for the image view to null
                        iv_Eteacher.setTag(null);
                    }
                });

        /// register the activity result launcher for capturing image from camera
        captureImageLauncher = registerForActivityResult(
                new ActivityResultContracts.StartActivityForResult(),
                result -> {
                    if (result.getResultCode() == RESULT_OK && result.getData() != null) {
                        Bitmap bitmap = (Bitmap) result.getData().getExtras().get("data");
                        iv_Eteacher.setImageBitmap(bitmap);
                        /// set the tag for the image view to null
                        iv_Eteacher.setTag(null);
                    }
                });






    }

    /// capture image from camera
    private void captureImageFromCamera() {
        Intent takePictureIntent = new Intent(MediaStore.ACTION_IMAGE_CAPTURE);
        captureImageLauncher.launch(takePictureIntent);
    }



    void imageChooser() {
        Intent i = new Intent();
        i.setType("image/*");
        i.setAction(Intent.ACTION_GET_CONTENT);
        startActivityForResult(Intent.createChooser(i, "בחר תמונה"), SELECT_PICTURE);
    }

    @Override
    public void onActivityResult(int requestCode, int resultCode, Intent data) {
        super.onActivityResult(requestCode, resultCode, data);
        if (resultCode == RESULT_OK && requestCode == SELECT_PICTURE && data != null) {
            Uri selectedImageUri = data.getData();
            if (selectedImageUri != null) {
                // תמונה שנבחרה מוצגת ב-imgDisplay (add2) בלבד
                iv_Eteacher.setImageURI(selectedImageUri);
            }
        }
    }




    public boolean validateInput(String fname, String lname, String price, String age) {
        String nameRegex   = "^[A-Za-zא-ת]+$";
        String numberRegex = "^[0-9]+(\\.[0-9]+)?$";
        if (fname.isEmpty())             { Toast.makeText(this, "שגיאה: חובה להזין שם פרטי!", Toast.LENGTH_LONG).show(); return false; }
        if (!fname.matches(nameRegex))   { Toast.makeText(this, "שגיאה: שם פרטי חייב להכיל אותיות בלבד!", Toast.LENGTH_LONG).show(); return false; }
        if (lname.isEmpty())             { Toast.makeText(this, "שגיאה: חובה להזין שם משפחה!", Toast.LENGTH_LONG).show(); return false; }
        if (!lname.matches(nameRegex))   { Toast.makeText(this, "שגיאה: שם משפחה חייב להכיל אותיות בלבד!", Toast.LENGTH_LONG).show(); return false; }
        if (price.isEmpty())             { Toast.makeText(this, "שגיאה: חובה להזין מחיר!", Toast.LENGTH_LONG).show(); return false; }
        if (!price.matches(numberRegex)) { Toast.makeText(this, "שגיאה: מחיר חייב להיות מספר!", Toast.LENGTH_LONG).show(); return false; }
        if (age.isEmpty())               { Toast.makeText(this, "שגיאה: חובה להזין גיל!", Toast.LENGTH_LONG).show(); return false; }
        if (!age.matches("^[0-9]+$"))    { Toast.makeText(this, "שגיאה: גיל חייב להיות מספר!", Toast.LENGTH_LONG).show(); return false; }
        if (Integer.parseInt(age) < 10)  { Toast.makeText(this, "שגיאה: גיל חייב להיות 10 ומעלה!", Toast.LENGTH_LONG).show(); return false; }
        return true;
    }

    @Override
    public boolean onCreateOptionsMenu(Menu menu) {
        getMenuInflater().inflate(R.menu.teacher_menu, menu);
        return true;
    }

    @Override
    public boolean onOptionsItemSelected(MenuItem item) {
        int id = item.getItemId();
        if (id == R.id.teacher_home)     { startActivity(new Intent(this, TeacherActivity.class)); return true; }
        if (id == R.id.teacher_profile)  { startActivity(new Intent(this, teacher_profile.class)); return true; }
        if (id == R.id.teacher_mylesson) { startActivity(new Intent(this, TeacherLessonsList.class)); return true; }
        if (id == R.id.teacher_disconect){ startActivity(new Intent(this, disconect_forteacher.class)); return true; }
        if (id == R.id.teacher_adut)     { startActivity(new Intent(this, AdutActivity.class)); return true; }
        return super.onOptionsItemSelected(item);
    }


}