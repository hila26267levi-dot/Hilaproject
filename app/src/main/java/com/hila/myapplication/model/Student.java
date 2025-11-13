package com.hila.myapplication.model;

public class Student  extends  User{

    protected  String kita;

    public Student(String id, String fname, String lname, String phone, String email, String password, String kita) {
        super(id, fname, lname, phone, email, password);
        this.kita = kita;
    }

    public Student(String kita) {
        this.kita = kita;
    }

    public Student() {
    }

    public String getKita() {
        return kita;
    }

    public void setKita(String kita) {
        this.kita = kita;
    }

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
