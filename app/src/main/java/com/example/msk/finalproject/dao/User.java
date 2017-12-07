package com.example.msk.finalproject.dao;


import com.google.firebase.database.IgnoreExtraProperties;

/**
 * Created by MsK on 3/12/2017 AD.
 */

@IgnoreExtraProperties
public class User {

    private String email;
    private String password;
    private String firstname;
    private String lastname;
    private String role;


    public User(){

    }

    public User(String email,String password,String firstname,String lastname,String role){
        this.email = email;
        this.password = password;
        this.firstname = firstname;
        this.lastname = lastname;
        this.role = role;
    }

    public String getEmail() {
        return email;
    }

    public String getPassword() {
        return password;
    }

    public String getFirstname() {
        return firstname;
    }

    public String getLastname() {
        return lastname;
    }

    public String getRole() {
        return role;
    }
}
