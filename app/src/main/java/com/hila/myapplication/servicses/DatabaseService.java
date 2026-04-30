package com.hila.myapplication.servicses;

import android.util.Log;

import androidx.annotation.NonNull;
import androidx.annotation.Nullable;


import com.google.firebase.auth.FirebaseAuth;
import com.google.firebase.database.DataSnapshot;
import com.google.firebase.database.DatabaseError;
import com.google.firebase.database.DatabaseReference;
import com.google.firebase.database.FirebaseDatabase;
import com.google.firebase.database.MutableData;
import com.google.firebase.database.Transaction;
import com.google.firebase.database.ValueEventListener;
import com.hila.myapplication.model.Student;
import com.hila.myapplication.model.Teacher;
import com.hila.myapplication.model.TeacherLesson;
import com.hila.myapplication.model.User;

import org.jetbrains.annotations.NotNull;

import java.util.ArrayList;
import java.util.List;
import java.util.function.UnaryOperator;


/// a service to interact with the Firebase Realtime Database.
/// this class is a singleton, use getInstance() to get an instance of this class
///
/// @see #getInstance()
/// @see FirebaseDatabase
public class DatabaseService {

    /// tag for logging
    ///
    /// @see Log
    private static final String TAG = "DatabaseService";

    /// paths for different data types in the database
    ///
    /// @see DatabaseService#readData(String)
    private static final String STUDENT_PATH = "students",
            LESOON_THEACHER_PATH = "teacher_Lesson",
            LESOON_STUDENT_PATH = "student_Lesson",
            TEACHER_PATH = "teacher",
            ADMIN_PATH = "admin";

    /// callback interface for database operations
    ///
    /// @param <T> the type of the object to return
    /// @see DatabaseCallback#onCompleted(Object)
    /// @see DatabaseCallback#onFailed(Exception)
    public interface DatabaseCallback<T> {
        /// called when the operation is completed successfully
        public void onCompleted(T object);

        /// called when the operation fails with an exception
        public void onFailed(Exception e);
    }

    /// the instance of this class
    ///
    /// @see #getInstance()
    private static DatabaseService instance;

    /// the reference to the database
    ///
    /// @see DatabaseReference
    /// @see FirebaseDatabase#getReference()
    private final DatabaseReference databaseReference;

    /// use getInstance() to get an instance of this class
    ///
    /// @see DatabaseService#getInstance()
    private DatabaseService() {
        FirebaseDatabase firebaseDatabase = FirebaseDatabase.getInstance();
        databaseReference = firebaseDatabase.getReference();
    }

    /// get an instance of this class
    ///
    /// @return an instance of this class
    /// @see DatabaseService
    public static DatabaseService getInstance() {
        if (instance == null) {
            instance = new DatabaseService();
        }
        return instance;
    }


    // region private generic methods
    // to write and read data from the database

    /// write data to the database at a specific path
    ///
    /// @param path     the path to write the data to
    /// @param data     the data to write (can be any object, but must be serializable, i.e. must have a default constructor and all fields must have getters and setters)
    /// @param callback the callback to call when the operation is completed
    /// @see DatabaseCallback
    private void writeData(@NotNull final String path, @NotNull final Object data, final @Nullable DatabaseCallback<Void> callback) {
        readData(path).setValue(data, (error, ref) -> {
            if (error != null) {
                if (callback == null) return;
                callback.onFailed(error.toException());
            } else {
                if (callback == null) return;
                callback.onCompleted(null);
            }
        });
    }

    /// remove data from the database at a specific path
    ///
    /// @param path     the path to remove the data from
    /// @param callback the callback to call when the operation is completed
    /// @see DatabaseCallback
    private void deleteData(@NotNull final String path, @Nullable final DatabaseCallback<Void> callback) {
        readData(path).removeValue((error, ref) -> {
            if (error != null) {
                if (callback == null) return;
                callback.onFailed(error.toException());
            } else {
                if (callback == null) return;
                callback.onCompleted(null);
            }
        });
    }

    /// read data from the database at a specific path
    ///
    /// @param path the path to read the data from
    /// @return a DatabaseReference object to read the data from
    /// @see DatabaseReference

    private DatabaseReference readData(@NotNull final String path) {
        return databaseReference.child(path);
    }


    /// get data from the database at a specific path
    ///
    /// @param path     the path to get the data from
    /// @param clazz    the class of the object to return
    /// @param callback the callback to call when the operation is completed
    /// @see DatabaseCallback
    /// @see Class
    private <T> void getData(@NotNull final String path, @NotNull final Class<T> clazz, @NotNull final DatabaseCallback<T> callback) {
        readData(path).get().addOnCompleteListener(task -> {
            if (!task.isSuccessful()) {
                Log.e(TAG, "Error getting data", task.getException());
                callback.onFailed(task.getException());
                return;
            }
            T data = task.getResult().getValue(clazz);
            callback.onCompleted(data);
        });
    }

    /// get a list of data from the database at a specific path
    ///
    /// @param path     the path to get the data from
    /// @param clazz    the class of the objects to return
    /// @param callback the callback to call when the operation is completed
    private <T> void getDataList(@NotNull final String path, @NotNull final Class<T> clazz, @NotNull final DatabaseCallback<List<T>> callback) {
        readData(path).get().addOnCompleteListener(task -> {
            if (!task.isSuccessful()) {
                Log.e(TAG, "Error getting data", task.getException());
                callback.onFailed(task.getException());
                return;
            }
            List<T> tList = new ArrayList<>();
            task.getResult().getChildren().forEach(dataSnapshot -> {
                T t = dataSnapshot.getValue(clazz);
                tList.add(t);
            });

            callback.onCompleted(tList);
        });
    }

    /// generate a new id for a new object in the database
    ///
    /// @param path the path to generate the id for
    /// @return a new id for the object
    /// @see String
    /// @see DatabaseReference#push()

    private String generateNewId(@NotNull final String path) {
        return databaseReference.child(path).push().getKey();
    }


    /// run a transaction on the data at a specific path </br>
    /// good for incrementing a value or modifying an object in the database
    ///
    /// @param path     the path to run the transaction on
    /// @param clazz    the class of the object to return
    /// @param function the function to apply to the current value of the data
    /// @param callback the callback to call when the operation is completed
    /// @see DatabaseReference#runTransaction(Transaction.Handler)
    private <T> void runTransaction(@NotNull final String path, @NotNull final Class<T> clazz, @NotNull UnaryOperator<T> function, @NotNull final DatabaseCallback<T> callback) {
        readData(path).runTransaction(new Transaction.Handler() {
            @NonNull
            @Override
            public Transaction.Result doTransaction(@NonNull MutableData currentData) {
                T currentValue = currentData.getValue(clazz);
                if (currentValue == null) {
                    currentValue = function.apply(null);
                } else {
                    currentValue = function.apply(currentValue);
                }
                currentData.setValue(currentValue);
                return Transaction.success(currentData);
            }

            @Override
            public void onComplete(@Nullable DatabaseError error, boolean committed, @Nullable DataSnapshot currentData) {
                if (error != null) {
                    Log.e(TAG, "Transaction failed", error.toException());
                    callback.onFailed(error.toException());
                    return;
                }
                T result = currentData != null ? currentData.getValue(clazz) : null;
                callback.onCompleted(result);
            }
        });

    }

    // endregion of private methods for reading and writing data

    // public methods to interact with the database

    // region User Section


    /// create a new Student in the database
    ///
    /// @param student     the user object to create (without the id, null)
    /// @param callback the callback to call when the operation is completed
    ///                                              the callback will receive new user id
    ///                                            if the operation fails, the callback will receive an exception
    /// @see DatabaseCallback
    /// @see User
    public void createNewStudent(@NotNull final Student student,
                                 @Nullable final DatabaseCallback<String> callback) {
        FirebaseAuth mAuth = FirebaseAuth.getInstance();
        mAuth.createUserWithEmailAndPassword(student.getEmail(), student.getPassword())
                .addOnCompleteListener(task -> {
                    if (task.isSuccessful()) {
                        Log.d("TAG", "createUserWithEmail:success");
                        String uid = FirebaseAuth.getInstance().getCurrentUser().getUid();
                        student.setId(uid);
                        writeData(STUDENT_PATH + "/" + uid, student, new DatabaseCallback<Void>() {
                            @Override
                            public void onCompleted(Void v) {
                                if (callback != null) callback.onCompleted(uid);
                            }

                            @Override
                            public void onFailed(Exception e) {
                                if (callback != null) callback.onFailed(e);
                            }
                        });



                    } else {
                        Log.w("TAG", "createUserWithEmail:failure", task.getException());
                        if (callback != null)
                            callback.onFailed(task.getException());
                    }
                });
    }



    public void createNewAdmin(@NotNull final User user,
                                 @Nullable final DatabaseCallback<String> callback) {
        FirebaseAuth mAuth = FirebaseAuth.getInstance();
        mAuth.createUserWithEmailAndPassword(user.getEmail(), user.getPassword())
                .addOnCompleteListener(task -> {
                    if (task.isSuccessful()) {
                        Log.d("TAG", "createUserWithEmail:success");
                        String uid = FirebaseAuth.getInstance().getCurrentUser().getUid();
                        user.setId(uid);
                        writeData(ADMIN_PATH + "/" + uid, user, new DatabaseCallback<Void>() {
                            @Override
                            public void onCompleted(Void v) {
                                if (callback != null) callback.onCompleted(uid);
                            }

                            @Override
                            public void onFailed(Exception e) {
                                if (callback != null) callback.onFailed(e);
                            }
                        });



                    } else {
                        Log.w("TAG", "createUserWithEmail:failure", task.getException());
                        if (callback != null)
                            callback.onFailed(task.getException());
                    }
                });
    }

    public void   updateStudent(Student student,   @Nullable final DatabaseCallback<Void> callback ){


        writeData(STUDENT_PATH + "/" + student.getId(), student, callback) ;


                }


    /// create a new teacher in the database
    ///
    /// @param teacher  the user object to create (without the id, null)
    /// @param callback the callback to call when the operation is completed
    ///                                              the callback will receive new user id
    ///                                            if the operation fails, the callback will receive an exception
    /// @see DatabaseCallback
    /// @see User
    public void createNewTeacher(@NotNull final Teacher teacher,
                                 @Nullable final DatabaseCallback<String> callback) {
        FirebaseAuth mAuth = FirebaseAuth.getInstance();
        mAuth.createUserWithEmailAndPassword(teacher.getEmail(), teacher.getPassword())
                .addOnCompleteListener(task -> {
                    if (task.isSuccessful()) {
                        Log.d("TAG", "createrTeacherWithEmail:success");
                        String uid = FirebaseAuth.getInstance().getCurrentUser().getUid();
                        teacher.setId(uid);
                        writeData(TEACHER_PATH + "/" + uid, teacher, new DatabaseCallback<Void>() {
                            @Override
                            public void onCompleted(Void v) {
                                if (callback != null) callback.onCompleted(uid);
                            }

                            @Override
                            public void onFailed(Exception e) {
                                if (callback != null) callback.onFailed(e);
                            }
                        });
                    } else {
                        Log.w("TAG", "createUserWithEmail:failure", task.getException());
                        if (callback != null)
                            callback.onFailed(task.getException());
                    }
                });
    }


    /// Login with email and password
    ///
    /// @param email    , password
    /// @param callback the callback to call when the operation is completed
    ///                                              the callback will receive String (user id)
    ///                                            if the operation fails, the callback will receive an exception
    /// @see DatabaseCallback
    /// @see FirebaseAuth

    public void LoginUser(@NotNull final String email, final String password,
                          @Nullable final DatabaseCallback<String> callback) {

        FirebaseAuth mAuth = FirebaseAuth.getInstance();
        mAuth.signInWithEmailAndPassword(email, password)
                .addOnCompleteListener(task -> {
                    if (task.isSuccessful()) {
                        Log.d("TAG", "signinUserWithEmail:success");
                        String uid = FirebaseAuth.getInstance().getCurrentUser().getUid();
                        callback.onCompleted(uid);
                    } else {
                        Log.w("TAG", "signinUserWithEmail:failure", task.getException());
                        if (callback != null)
                            callback.onFailed(task.getException());
                    }
                });
    }


    /// get a user from the database
    ///
    /// @param uid      the id of the user to get
    /// @param callback the callback to call when the operation is completed
    ///                                               the callback will receive the user object
    ///                                             if the operation fails, the callback will receive an exception
    /// @see DatabaseCallback
    /// @see Teacher
    public void getTeacher(@NotNull final String uid, @NotNull final DatabaseCallback<Teacher> callback) {
        getData(TEACHER_PATH + "/" + uid, Teacher.class, callback);
    }

    public void getTeacher( @NotNull final DatabaseCallback<Teacher> callback) {
        FirebaseAuth mAuth = FirebaseAuth.getInstance();
        String uid=mAuth.getCurrentUser().getUid();


        getData(TEACHER_PATH + "/" + uid, Teacher.class, callback);
    }


    /// get all the users from the database
    ///
    /// @param callback the callback to call when the operation is completed
    ///                                              the callback will receive a list of user objects
    ///                                            if the operation fails, the callback will receive an exception
    /// @see DatabaseCallback
    /// @see List
    /// @see Teacher
    public void getTeacherList(@NotNull final DatabaseCallback<List<Teacher>> callback) {
        getDataList(TEACHER_PATH, Teacher.class, callback);
    }
    public  void  updateTeacher(@NotNull final Teacher teacher, @Nullable final DatabaseCallback<Void> callback){

        writeData(TEACHER_PATH + "/" + teacher.getId(), teacher, callback);


    }


    /// delete a user from the database
    ///
    /// @param uid      the user id to delete
    /// @param callback the callback to call when the operation is completed
    public void deleteTeacher(@NotNull final String uid, @Nullable final DatabaseCallback<Void> callback) {
        deleteData(TEACHER_PATH + "/" + uid, callback);
    }

    /// get a user from the database
    ///
    /// @param       the id of the user to get
    /// @param callback the callback to call when the operation is completed
    ///                                               the callback will receive the user object
    ///                                             if the operation fails, the callback will receive an exception
    /// @see DatabaseCallback
    /// @see Student
    public void getStudent(@NotNull final String uid, @NotNull final DatabaseCallback<Student> callback) {

        getData(STUDENT_PATH + "/" + uid, Student.class, callback);
    }

    public void getStudent( @NotNull final DatabaseCallback<Student> callback) {
        FirebaseAuth mAuth = FirebaseAuth.getInstance();//זה מציג את כל המופע של המשתמש המחובר
        String uid=mAuth.getCurrentUser().getUid();//תביא את הזהות של מי שהתחבר אחרון

        getData(STUDENT_PATH + "/" + uid, Student.class, callback);
    }

    //public  void  updateStudent(@NotNull final Student student, @Nullable final DatabaseCallback<Void> callback){

    //    writeData(STUDENT_PATH + "/" + student.getId(), student, callback);


  //  }



    /// get all the users from the database
    ///
    /// @param callback the callback to call when the operation is completed
    ///                                              the callback will receive a list of user objects
    ///                                            if the operation fails, the callback will receive an exception
    /// @see DatabaseCallback
    /// @see List
    /// @see Student
    public void getStudentList(@NotNull final DatabaseCallback<List<Student>> callback) {
        getDataList(STUDENT_PATH, Student.class, callback);
    }

    /// delete a user from the database
    ///
    /// @param uid      the user id to delete
    /// @param callback the callback to call when the operation is completed
    public void deleteStudent(@NotNull final String uid, @Nullable final DatabaseCallback<Void> callback) {
        deleteData(STUDENT_PATH + "/" + uid, callback);
    }


    public String generatelessonId()
    {
        return generateNewId(LESOON_THEACHER_PATH);
    }


    public  void  createNewLesson(@NotNull final TeacherLesson teacherLesson, @Nullable final DatabaseCallback<Void> callback){

        writeData(LESOON_THEACHER_PATH+"/"+teacherLesson.getTeacher().getId()+"/"+teacherLesson.getId(),teacherLesson,callback);

    }

    public  void  updateLesson(@NotNull final TeacherLesson teacherLesson, @Nullable final DatabaseCallback<Void> callback){

        writeData(LESOON_THEACHER_PATH+"/"+teacherLesson.getTeacher().getId()+"/"+teacherLesson.getId(),teacherLesson,callback);

    }


    public  void  setLessonForStudent(@NotNull final TeacherLesson teacherLesson, @Nullable final DatabaseCallback<Void> callback){

        writeData(LESOON_THEACHER_PATH+"/"+teacherLesson.getTeacher().getId()+"/"+teacherLesson.getId(),teacherLesson,callback);
        writeData(LESOON_STUDENT_PATH+"/"+teacherLesson.getStudent().getId()+"/"+ teacherLesson.getId(),teacherLesson,callback);

    }

    public  void  deleteLessonForStudent(@NotNull final TeacherLesson teacherLesson, @Nullable final DatabaseCallback<Void> callback){


        deleteData(LESOON_STUDENT_PATH+ "/"+teacherLesson.getStudent().getId()+"/"+ teacherLesson.getId(),callback);


        writeData(LESOON_THEACHER_PATH+"/"+teacherLesson.getTeacher().getId()+"/"+teacherLesson.getId(),teacherLesson,callback);


    }




    /// get all the users from the database
    ///
    /// @param callback the callback to call when the operation is completed
    ///                                              the callback will receive a list of user objects
    ///                                            if the operation fails, the callback will receive an exception
    /// @see DatabaseCallback
    /// @see List
    /// @see Student
    public void getThecherLessonList(  @NotNull final String tId,    @NotNull final DatabaseCallback<List<TeacherLesson>> callback) {
        getDataList(LESOON_THEACHER_PATH+"/"+ tId , TeacherLesson.class, callback);
    }

    /// get all the users from the database
    ///
    /// @param callback the callback to call when the operation is completed
    ///                                              the callback will receive a list of user objects
    ///                                            if the operation fails, the callback will receive an exception
    /// @see DatabaseCallback
    /// @see List
    /// @see Student
    public void getStudentLessonList(  @NotNull final String sId,    @NotNull final DatabaseCallback<List<TeacherLesson>> callback) {
        getDataList(LESOON_STUDENT_PATH+"/"+ sId, TeacherLesson.class, callback);
    }

    public void getAdmin(String uid,
                         DatabaseCallback<User> callback) {
        getData(ADMIN_PATH + "/" + uid, User.class, callback);
    }




}