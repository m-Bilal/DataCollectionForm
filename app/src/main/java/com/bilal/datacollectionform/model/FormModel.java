package com.bilal.datacollectionform.model;

import io.realm.RealmList;
import io.realm.RealmObject;
import io.realm.annotations.Ignore;

public class FormModel extends RealmObject {

    @Ignore
    private final static String TAG = "FromModel";

    public int formId;
    public int formName;
    public RealmList<FormAnswerModel> answerModelList;
    public boolean syncedWithServer;

    public FormModel() {
        syncedWithServer = false;
    }

    public FormModel(FormModel formModel) {
        this.formId = formModel.formId;
        this.formName = formModel.formName;
        this.answerModelList = formModel.answerModelList;
        this.syncedWithServer = formModel.syncedWithServer;
    }
}
