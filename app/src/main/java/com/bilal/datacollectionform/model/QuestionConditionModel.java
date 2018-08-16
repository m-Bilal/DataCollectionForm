package com.bilal.datacollectionform.model;

import android.content.Context;

import io.realm.Realm;
import io.realm.RealmObject;
import io.realm.annotations.Ignore;
import io.realm.annotations.PrimaryKey;

public class QuestionConditionModel extends RealmObject {

    @Ignore
    private final String TAG = "AnswerConditionModel";

    @PrimaryKey
    public int primaryKey;
    public String equals;
    public String $do;
    public String to;
    public String law;
    public String doIt;

    public QuestionConditionModel() {

    }

    public QuestionConditionModel(QuestionConditionModel questionConditionModel) {
        this.equals = questionConditionModel.equals;
        this.$do = questionConditionModel.$do;
        this.to = questionConditionModel.to;
        this.law = questionConditionModel.law;
        this.doIt = questionConditionModel.doIt;
    }

    public static void saveToRealm(Context context, QuestionConditionModel questionConditionModel) {
        Realm.init(context);
        Realm realm = Realm.getDefaultInstance();
        realm.beginTransaction();
        realm.copyToRealmOrUpdate(questionConditionModel);
        realm.commitTransaction();
        realm.close();
    }
}
