package com.bilal.datacollectionform.model;

import android.content.Context;

import io.realm.Realm;
import io.realm.RealmObject;
import io.realm.annotations.Ignore;
import io.realm.annotations.PrimaryKey;

public class QuestionOptionModel extends RealmObject{

    @Ignore
    private final static String TAG = "AnswerOptionModel";

    @PrimaryKey
    public int primaryKey;
    public String value;
    public String hashkey;
    public String sMin;
    public String sMax;
    public boolean containsSMin;
    public boolean containsSMax;

    public QuestionOptionModel() {

    }

    public QuestionOptionModel(QuestionOptionModel questionOptionModel) {
        this.value = questionOptionModel.value;
        this.hashkey = questionOptionModel.hashkey;
        this.sMin = questionOptionModel.sMin;
        this.sMax = questionOptionModel.sMax;
        this.containsSMax = questionOptionModel.containsSMax;
        this.containsSMin = questionOptionModel.containsSMin;
    }

    public static void saveToRealm(Context context, QuestionOptionModel questionOptionModel) {
        Realm.init(context);
        Realm realm = Realm.getDefaultInstance();
        realm.beginTransaction();
        realm.copyToRealmOrUpdate(questionOptionModel);
        realm.commitTransaction();
        realm.close();
    }
}
