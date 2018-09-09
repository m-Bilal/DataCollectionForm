package com.bilal.datacollectionform.model;

import android.content.Context;
import android.net.Uri;

import java.util.Map;
import java.util.TreeMap;

import io.realm.Realm;
import io.realm.RealmList;
import io.realm.RealmObject;
import io.realm.RealmResults;
import io.realm.annotations.Ignore;
import io.realm.annotations.PrimaryKey;

public class QuestionAnswerModel extends RealmObject {

    @Ignore
    private final static String TAG = "QuestionAnswerModel";

    @PrimaryKey
    public int primaryKey;
    public String label;
    public String value;
    public String type;
    public int formId;

    public QuestionAnswerModel() {

    }

    public QuestionAnswerModel(QuestionAnswerModel model) {
        this.primaryKey = model.primaryKey;
        this.label = model.label;
        this.value = model.value;
        this.type = model.type;
        this.formId = model.formId;
    }

    public static void saveToRealm(Context context, QuestionAnswerModel model) {
        Realm.init(context);
        model.primaryKey = PrimaryKeyModel.getQuestionAnswerPrimaryKey(context);
        Realm realm = Realm.getDefaultInstance();
        realm.beginTransaction();
        realm.copyToRealm(model);
        realm.commitTransaction();
        realm.close();
    }

    public static void updateInRealm(Context context, QuestionAnswerModel model) {
        Realm.init(context);
        Realm realm = Realm.getDefaultInstance();
        realm.beginTransaction();
        realm.copyToRealmOrUpdate(model);
        realm.commitTransaction();
        realm.close();
    }



    public static RealmResults<QuestionAnswerModel> getAllModelsForFormId(Context context, FormAnswerModel formAnswerModel) {
        Realm.init(context);
        Realm realm = Realm.getDefaultInstance();
        RealmResults<QuestionAnswerModel> realmResults = realm.where(QuestionAnswerModel.class)
                .equalTo("formId", formAnswerModel.formId).findAll();
        return realmResults;
    }

    public static QuestionAnswerModel getModelForPrimaryKey(Context context, int key) {
        Realm.init(context);
        Realm realm = Realm.getDefaultInstance();
        QuestionAnswerModel model = realm.where(QuestionAnswerModel.class).equalTo("primaryKey", key).findFirst();
        return model;
    }
}