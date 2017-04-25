package com.training.serverinteraction;

import java.io.Serializable;

/**
 * Created by GOD on 4/11/2017.
 */

public class Student implements Serializable {

    int id;

    String name,phone, email, gender, city;

    public Student() {
    }

    public Student(int id, String name, String phone, String email, String gender, String city) {
        this.id = id;
        this.name = name;
        this.phone = phone;
        this.email = email;
        this.gender = gender;
        this.city = city;
    }

    public int getId() {
        return id;
    }

    public void setId(int id) {
        this.id = id;
    }

    public String getName() {
        return name;
    }

    public void setName(String name) {
        this.name = name;
    }

    public String getPhone() {
        return phone;
    }

    public void setPhone(String phone) {
        this.phone = phone;
    }

    public String getEmail() {
        return email;
    }

    public void setEmail(String email) {
        this.email = email;
    }

    public String getGender() {
        return gender;
    }

    public void setGender(String gender) {
        this.gender = gender;
    }

    public String getCity() {
        return city;
    }

    public void setCity(String city) {
        this.city = city;
    }

    @Override
    public String toString() {
        return "Student{" +
                "\nID is: " + id +
                "\n Name is: " + name +
                "\nPhone is: " + phone +
                "\n Email is: " + email +
                "\n Gender is: " + gender +
                "\n City is: " + city +
                '}';
    }
}
