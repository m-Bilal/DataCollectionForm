package com.bilal.datacollectionform.model;

import io.realm.RealmObject;
import io.realm.annotations.Ignore;

public class AnswerOptionModel extends RealmObject{

    @Ignore
    private final static String TAG = "AnswerOptionModel";

    public String value;
    public String hashkey;
    public String sMin;
    public String sMax;

    public AnswerOptionModel() {

    }

    public AnswerOptionModel(AnswerOptionModel answerOptionModel) {
        this.value = answerOptionModel.value;
        this.hashkey = answerOptionModel.hashkey;
        this.sMin = answerOptionModel.sMin;
        this.sMax = answerOptionModel.sMax;
    }
}
