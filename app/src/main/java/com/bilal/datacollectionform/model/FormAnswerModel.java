package com.bilal.datacollectionform.model;

import io.realm.RealmList;
import io.realm.RealmObject;
import io.realm.annotations.Ignore;

public class FormAnswerModel extends RealmObject{

    @Ignore
    private final static String TAG = "FromAnswerModel";

    public int id;
    public String label;
    public String type;
    public int required;
    public RealmList<AnswerConditionModel> conditionList;
    public RealmList<AnswerOptionModel> optionList;
    public String answer;
    public RealmList<AnswerOptionModel> answerList;

    public FormAnswerModel() {

    }

    public FormAnswerModel(FormAnswerModel formAnswerModel) {
        this.id = formAnswerModel.id;
        this.label = formAnswerModel.label;
        this.type = formAnswerModel.type;
        this.required = formAnswerModel.required;
        this.conditionList = formAnswerModel.conditionList;
        this.optionList = formAnswerModel.optionList;
        this.answer = formAnswerModel.answer;
        this.answerList = formAnswerModel.answerList;
    }
}
