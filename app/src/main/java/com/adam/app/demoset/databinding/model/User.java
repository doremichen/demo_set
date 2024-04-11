package com.adam.app.demoset.databinding.model;

public class User {
    private String mFirstName;
    private String mLastName;
    private int mAge;

    public User(String firstName, String lastName, int age) {
        mFirstName = firstName;
        mLastName = lastName;
        mAge = age;
    }

    public void updateInfo(String firstName, String lastName) {
        this.mFirstName = firstName;
        this.mLastName = lastName;
    }


    public String getFirstName() {
        return mFirstName;
    }

    public String getLastName() {
        return mLastName;
    }

    public int getAge() {
        return mAge;
    }
}
