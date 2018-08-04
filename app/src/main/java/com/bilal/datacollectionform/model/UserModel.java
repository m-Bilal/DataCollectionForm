package com.bilal.datacollectionform.model;

import io.realm.RealmObject;
import io.realm.annotations.Ignore;
import io.realm.annotations.PrimaryKey;

public class UserModel extends RealmObject{

    @Ignore
    private final static String TAG = "UserModel";

    @PrimaryKey
    public int login;
    public String message;
    public String user;
    public int userId;
    public int noOfEntries;
    public int noOfForms;
    public String name;

    public UserModel() {

    }

    public UserModel(UserModel userModel) {
        this.login = userModel.login;
        this.message = userModel.message;
        this.user = userModel.user;
        this.userId = userModel.userId;
        this.noOfEntries = userModel.noOfEntries;
        this.noOfForms = userModel.noOfForms;
        this.name = userModel.name;
    }
}
