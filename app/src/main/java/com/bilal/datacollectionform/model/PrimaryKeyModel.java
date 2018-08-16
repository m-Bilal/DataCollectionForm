package com.bilal.datacollectionform.model;

import android.content.Context;

import io.realm.Realm;
import io.realm.RealmObject;
import io.realm.annotations.PrimaryKey;

public class PrimaryKeyModel extends RealmObject{

    private final static String TAG = "PrimaryKeyModel";

    public static final int ID = 1;

    @PrimaryKey
    public int id;
    public int formQuestionPrimaryKey;
    public int questionConditionPrimaryKey;
    public int questionOptionPrimaryKey;

    public static int getFormQuestionPrimaryKey(Context context) {
        Realm.init(context);
        Realm realm = Realm.getDefaultInstance();
        int key;
        PrimaryKeyModel primaryKeyModel = realm.where(PrimaryKeyModel.class).equalTo("id", ID).findFirst();
        realm.beginTransaction();
        key = primaryKeyModel.formQuestionPrimaryKey++;
        realm.copyToRealmOrUpdate(primaryKeyModel);
        realm.commitTransaction();
        realm.close();
        return key;
    }

    public static int getQuestionConditionPrimaryKey(Context context) {
        Realm.init(context);
        Realm realm = Realm.getDefaultInstance();
        int key;
        PrimaryKeyModel primaryKeyModel = realm.where(PrimaryKeyModel.class).equalTo("id", ID).findFirst();
        realm.beginTransaction();
        key = primaryKeyModel.questionConditionPrimaryKey++;
        realm.copyToRealmOrUpdate(primaryKeyModel);
        realm.commitTransaction();
        realm.close();
        return key;
    }

    public static int getQuestionOptionPrimaryKey(Context context) {
        Realm.init(context);
        Realm realm = Realm.getDefaultInstance();
        int key;
        PrimaryKeyModel primaryKeyModel = realm.where(PrimaryKeyModel.class).equalTo("id", ID).findFirst();
        realm.beginTransaction();
        key = primaryKeyModel.questionOptionPrimaryKey++;
        realm.copyToRealmOrUpdate(primaryKeyModel);
        realm.commitTransaction();
        realm.close();
        return key;
    }

    public static PrimaryKeyModel getPrimaryKeyModel(Context context) {
        Realm.init(context);
        Realm realm = Realm.getDefaultInstance();
        PrimaryKeyModel primaryKeyModel = realm.where(PrimaryKeyModel.class).equalTo("id", ID).findFirst();
        return primaryKeyModel;
    }

    public static void createPrimaryKeyModel(Context context) {
        Realm.init(context);
        Realm realm = Realm.getDefaultInstance();
        PrimaryKeyModel primaryKeyModel = new PrimaryKeyModel();
        primaryKeyModel.id = ID;
        primaryKeyModel.questionOptionPrimaryKey = 0;
        primaryKeyModel.questionConditionPrimaryKey = 0;
        primaryKeyModel.formQuestionPrimaryKey = 0;
        realm.beginTransaction();
        realm.copyToRealmOrUpdate(primaryKeyModel);
        realm.commitTransaction();
        realm.close();
    }
}
