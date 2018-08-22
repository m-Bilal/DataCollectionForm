package com.bilal.datacollectionform.model;

import android.content.Context;
import android.util.Log;

import org.json.JSONObject;

import java.util.Date;

import io.realm.Realm;
import io.realm.RealmList;
import io.realm.RealmObject;
import io.realm.annotations.Ignore;
import io.realm.annotations.PrimaryKey;

public class FormAnswerModel extends RealmObject {

    @Ignore
    private final static String TAG = "FormAnswerModel";

    @PrimaryKey
    public int primaryKey;
    public int formId;
    public int userId;
    public long captureDate;
    public boolean syncedWithServer;
    public RealmList<QuestionAnswerModel> questionAnswerModelRealmList;
    public String json;

    public FormAnswerModel() {

    }

    public FormAnswerModel(FormAnswerModel model) {
        this.primaryKey = model.primaryKey;
        this.formId = model.formId;
        this.userId = model.userId;
        this.captureDate = model.captureDate;
        this.syncedWithServer = model.syncedWithServer;
        this.questionAnswerModelRealmList = model.questionAnswerModelRealmList;
    }

    public void saveJson(Context context) {
        try {
            JSONObject jsonObject = new JSONObject();
            JSONObject formInfo = new JSONObject();
            formInfo.put("formid", this.formId);
            formInfo.put("userid", this.userId);
            formInfo.put("capturedate", this.captureDate);
            jsonObject.put("0", formInfo);
            int pos = 1;
            for (QuestionAnswerModel i : questionAnswerModelRealmList) {
                JSONObject questionJson = new JSONObject();
                questionJson.put("label", i.label);
                questionJson.put("value", i.value);
                questionJson.put("type", i.type);
                jsonObject.put("" + pos++, questionJson);
            }
            Realm.init(context);
            Realm realm = Realm.getDefaultInstance();
            realm.beginTransaction();
            this.json = jsonObject.toString();
            realm.commitTransaction();
            realm.close();
        } catch (Exception e) {
            Log.e(TAG, "saveJson(), exception : " + e.toString());
            e.printStackTrace();
        }
    }

    public static FormAnswerModel createModel(Context context, UserModel userModel, FormModel formModel) {
        int key = PrimaryKeyModel.getFormAnswerPrimaryKey(context);
        FormAnswerModel formAnswerModel = new FormAnswerModel();
        formAnswerModel.primaryKey = key;
        formAnswerModel.formId = formModel.formId;
        formAnswerModel.userId = userModel.userId;
        Date date = new Date();
        formAnswerModel.captureDate = date.getTime();
        formAnswerModel.syncedWithServer = false;
        formAnswerModel.questionAnswerModelRealmList = new RealmList<>();
        saveToRealm(context, formAnswerModel);
        return new FormAnswerModel(formAnswerModel);
    }

    public static void saveToRealm(Context context, FormAnswerModel model) {
        Realm.init(context);
        Realm realm = Realm.getDefaultInstance();
        realm.beginTransaction();
        realm.copyToRealm(model);
        realm.commitTransaction();
        realm.close();
    }

    public void addAnswer(Context context, QuestionAnswerModel questionAnswerModel) {
        QuestionAnswerModel.saveToRealm(context, questionAnswerModel);
        Realm.init(context);
        Realm realm = Realm.getDefaultInstance();
        realm.beginTransaction();
        this.questionAnswerModelRealmList.add(questionAnswerModel);
        realm.copyToRealmOrUpdate(this);
        realm.commitTransaction();
        realm.close();
    }

    public static FormAnswerModel getModelForPrimaryKey(Context context, int primaryKey) {
        Realm.init(context);
        Realm realm = Realm.getDefaultInstance();
        return realm.where(FormAnswerModel.class).equalTo("primaryKey", primaryKey).findFirst();
    }
}
