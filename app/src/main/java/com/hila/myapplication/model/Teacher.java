package com.hila.myapplication.model;

import java.io.Serializable;

public class Teacher extends User implements Serializable {

    protected String age ;
    protected String subject ;
    protected double price ;
    protected String zoom ;
    protected String teachclass ;


    public Teacher() {
    }


    public Teacher(Teacher teacher) {
        super(teacher.id, teacher.fname, teacher.lname, teacher.phone, teacher.email);
        this.age = teacher.age;
        this.subject = teacher.subject;
        this.price = teacher.price;
        this.zoom = teacher.zoom;
        this.teachclass = teacher.teachclass;
    }

    public Teacher(String id, String fname, String lname, String phone, String email, String password, String age, String subject, double price, String zoom, String teachclass) {
        super(id, fname, lname, phone, email, password);
        this.age = age;
        this.subject = subject;
        this.price = price;
        this.zoom = zoom;
        this.teachclass = teachclass;
    }

    public Teacher(String age, String subject, double price, String zoom, String teachclass) {
        this.age = age;
        this.subject = subject;
        this.price = price;
        this.zoom = zoom;
        this.teachclass = teachclass;
    }

    public String getAge() {
        return age;
    }

    public void setAge(String age) {
        this.age = age;
    }

    public String getSubject() {
        return subject;
    }

    public void setSubject(String subject) {
        this.subject = subject;
    }

    public double getPrice() {
        return price;
    }

    public void setPrice(double price) {
        this.price = price;
    }

    public String getZoom() {
        return zoom;
    }

    public void setZoom(String zoom) {
        this.zoom = zoom;
    }

    public String getTeachclass() {
        return teachclass;
    }

    public void setTeachclass(String teachclass) {
        this.teachclass = teachclass;
    }

    @Override
    public String toString() {
        return "Teacher" +
                ", age='" + age + '\'' +
                ", subject='" + subject + '\'' +
                ", price=" + price +
                ", zoom='" + zoom + '\'' +
                ", teachclass='" + teachclass + '\'' +
                ", id='" + id + '\'' +
                ", fname='" + fname + '\'' +
                ", lname='" + lname + '\'' +
                ", phone='" + phone + '\'' +
                ", email='" + email + '\'' +
                ", password='" + password + '\'' +
                '}';
    }
}
