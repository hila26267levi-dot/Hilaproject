package com.hila.myapplication.model;

import java.util.ArrayList;

public class Teacher extends User{

    protected String born ;
    protected ArrayList<String> professions ;
    protected Double price ;
    protected String zoom ;
    protected String teachclass ;


    public Teacher() {
    }

    public Teacher(String id, String fname, String lname, String phone, String email, String password, String born, Double price, ArrayList<String> professions, String teachclass, String zoom) {
        super(id, fname, lname, phone, email, password);
        this.born = born;
        this.price = price;
        this.professions = professions;
        this.teachclass = teachclass;
        this.zoom = zoom;
    }

    public String getBorn() {
        return born;
    }

    public void setBorn(String born) {
        this.born = born;
    }

    public Double getPrice() {
        return price;
    }

    public void setPrice(Double price) {
        this.price = price;
    }

    public ArrayList<String> getProfessions() {
        return professions;
    }

    public void setProfessions(ArrayList<String> professions) {
        this.professions = professions;
    }

    public String getTeachclass() {
        return teachclass;
    }

    public void setTeachclass(String teachclass) {
        this.teachclass = teachclass;
    }

    public String getZoom() {
        return zoom;
    }

    public void setZoom(String zoom) {
        this.zoom = zoom;
    }

    @Override
    public String toString() {
        return "Teacher{" +
                "born='" + born + '\'' +
                ", professions=" + professions +
                ", price=" + price +
                ", zoom='" + zoom + '\'' +
                ", teachclass='" + teachclass + '\'' +
                ", email='" + email + '\'' +
                ", fname='" + fname + '\'' +
                ", id='" + id + '\'' +
                ", lname='" + lname + '\'' +
                ", password='" + password + '\'' +
                ", phone='" + phone + '\'' +
                '}';
    }
}
