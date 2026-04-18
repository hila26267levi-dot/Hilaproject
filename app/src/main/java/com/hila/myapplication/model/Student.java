package com.hila.myapplication.model;

import androidx.annotation.NonNull;

import java.io.Serializable;

public class Student extends User implements Serializable {

    protected String kita;

    public Student() {
        super();
    }


   // public Student(String id, String fname, String lname, String phone, String email, String password, String kita) {
     //   super(id, fname, lname, phone, email, password);
     //   this.kita = kita;
  //  }

    public Student(String id, String fname, String lname, String phone, String email, String password, String pic, String kita) {
        super(id, fname, lname, phone, email, password, pic);
        this.kita = kita;
    }

    public Student(String kita) {
        this.kita = kita;
    }

    public Student(Student student) {
        super(student.id, student.fname, student.lname, student.phone, student.email);
        this.kita = student.kita;

    }

    public String getKita() {
        return kita;
    }

    public void setKita(String kita) {
        this.kita = kita;
    }



    @NonNull
    @Override
    public String toString() {
        return "Student{" +
                "kita='" + kita + '\'' +
                ", id='" + id + '\'' +
                ", fname='" + fname + '\'' +
                ", lname='" + lname + '\'' +
                ", phone='" + phone + '\'' +
                ", email='" + email + '\'' +
                ", password='" + password + '\'' +
                '}';
    }
}
