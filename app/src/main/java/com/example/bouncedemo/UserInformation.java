package com.example.bouncedemo;

public class UserInformation {

    public String name;
    public String latitude;
    public String longitude;

    public UserInformation(){               //default constructor which invokes on object creation of respective class in MainActivity.java

    }

    public UserInformation(String name, String latitude, String longitude){    //parameterized constructor which will store the retrieved data from firebase
        this.name=name;
        this.latitude=latitude;
        this.longitude=longitude;
    }
}