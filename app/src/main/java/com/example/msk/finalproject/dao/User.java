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
    private Boolean admin;


    public User(){

    }

    public User(String email, String password, String firstname, String lastname, Boolean admin) {
        this.email = email;
        this.password = password;
        this.firstname = firstname;
        this.lastname = lastname;
        this.admin = admin;
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

    public Boolean getAdmin() {
        return admin;
    }
}
