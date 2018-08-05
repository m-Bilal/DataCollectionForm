package com.bilal.datacollectionform.model;

import io.realm.RealmObject;
import io.realm.annotations.Ignore;

public class AnswerConditionModel extends RealmObject {

    @Ignore
    private final String TAG = "AnswerConditionModel";

    public String equals;
    public String $do;
    public String to;
    public String law;
    public String doIt;

    public AnswerConditionModel() {

    }

    public AnswerConditionModel(AnswerConditionModel answerConditionModel) {
        this.equals = answerConditionModel.equals;
        this.$do = answerConditionModel.$do;
        this.to = answerConditionModel.to;
        this.law = answerConditionModel.law;
        this.doIt = answerConditionModel.doIt;
    }
}
