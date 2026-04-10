package com.hila.myapplication.model;

import java.io.Serializable;

public class TeacherLesson implements Serializable {
    String id;

    Teacher teacher;
    String subject;
    String zoomORhome;
    String time ;
    String date ;
    String status ;

    String kita;
    Student student;


    public TeacherLesson(String id, Teacher teacher, String subject, String zoomORhome, String time, String date, String status, String kita) {
        this.id = id;
        this.teacher = teacher;
        this.subject = subject;
        this.zoomORhome = zoomORhome;
        this.time = time;
        this.date = date;
        this.status = status;
        this.kita = kita;
    }

    public TeacherLesson(String id, Teacher teacher, String subject, String zoomORhome, String time, String date, String status, String kita, Student student) {
        this.id = id;
        this.teacher = teacher;
        this.subject = subject;
        this.zoomORhome = zoomORhome;
        this.time = time;
        this.date = date;
        this.status = status;
        this.kita = kita;
        this.student = student;
    }

    public TeacherLesson() {
    }

    public void setId(String id) {
        this.id = id;
    }

    public void setTeacher(Teacher teacher) {
        this.teacher = teacher;
    }

    public void setSubject(String subject) {
        this.subject = subject;
    }

    public void setZoomORhome(String zoomORhome) {
        this.zoomORhome = zoomORhome;
    }

    public void setTime(String time) {
        this.time = time;
    }

    public void setDate(String date) {
        this.date = date;
    }

    public void setStatus(String status) {
        this.status = status;
    }

    public void setKita(String kita) {
        this.kita = kita;
    }

    public String getId() {
        return id;
    }

    public Teacher getTeacher() {
        return teacher;
    }

    public String getSubject() {
        return subject;
    }

    public String getZoomORhome() {
        return zoomORhome;
    }

    public String getTime() {
        return time;
    }

    public String getDate() {
        return date;
    }

    public String getStatus() {
        return status;
    }

    public String getKita() {
        return kita;
    }

    public Student getStudent() {
        return student;
    }

    public void setStudent(Student student) {
        this.student = student;
    }

    @Override
    public String toString() {
        return "TeacherLesson{" +
                "id='" + id + '\'' +
                ", teacher=" + teacher +
                ", subject='" + subject + '\'' +
                ", zoomORhome='" + zoomORhome + '\'' +
                ", time='" + time + '\'' +
                ", date='" + date + '\'' +
                ", status='" + status + '\'' +
                ", kita='" + kita + '\'' +
                ", student=" + student +
                '}';
    }

}
