package com.bilal.datacollectionform.model;

import android.content.Context;

import io.realm.Realm;
import io.realm.RealmObject;
import io.realm.annotations.PrimaryKey;

public class PrimaryKeyModel extends RealmObject{

    private final static String TAG = "PrimaryKeyModel";

    private final static int ID = 1;

    @PrimaryKey
    public int id;
    public int formQuestionPrimaryKey;
    public int questionConditionPrimaryKey;
    public int questionOptionPrimaryKey;
    public int formAnswerPrimaryKey;
    public int questionAnswerPrimaryKey;
    public int fileModelPrimaryKey;

    public static int getFileModelPrimaryKey(Context context) {
        Realm.init(context);
        Realm realm = Realm.getDefaultInstance();
        int key;
        PrimaryKeyModel primaryKeyModel = realm.where(PrimaryKeyModel.class).equalTo("id", ID).findFirst();
        realm.beginTransaction();
        key = primaryKeyModel.fileModelPrimaryKey++;
        realm.copyToRealmOrUpdate(primaryKeyModel);
        realm.commitTransaction();
        realm.close();
        return key;
    }

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

    public static int getFormAnswerPrimaryKey(Context context) {
        Realm.init(context);
        Realm realm = Realm.getDefaultInstance();
        int key;
        PrimaryKeyModel primaryKeyModel = realm.where(PrimaryKeyModel.class).equalTo("id", ID).findFirst();
        realm.beginTransaction();
        key = primaryKeyModel.formAnswerPrimaryKey++;
        realm.copyToRealmOrUpdate(primaryKeyModel);
        realm.commitTransaction();
        realm.close();
        return key;
    }

    public static int getQuestionAnswerPrimaryKey(Context context) {
        Realm.init(context);
        Realm realm = Realm.getDefaultInstance();
        int key;
        PrimaryKeyModel primaryKeyModel = realm.where(PrimaryKeyModel.class).equalTo("id", ID).findFirst();
        realm.beginTransaction();
        key = primaryKeyModel.questionAnswerPrimaryKey++;
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
        primaryKeyModel.questionOptionPrimaryKey = 1;
        primaryKeyModel.questionConditionPrimaryKey = 1;
        primaryKeyModel.formQuestionPrimaryKey = 1;
        primaryKeyModel.formAnswerPrimaryKey = 1;
        primaryKeyModel.questionAnswerPrimaryKey = 1;
        primaryKeyModel.fileModelPrimaryKey = 1;
        realm.beginTransaction();
        realm.copyToRealmOrUpdate(primaryKeyModel);
        realm.commitTransaction();
        realm.close();
    }
}
