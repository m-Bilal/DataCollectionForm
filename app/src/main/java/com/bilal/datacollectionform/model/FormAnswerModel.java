package com.bilal.datacollectionform.model;

import android.content.Context;
import android.net.Uri;
import android.util.Log;

import com.android.volley.Request;
import com.android.volley.RequestQueue;
import com.android.volley.Response;
import com.android.volley.VolleyError;
import com.android.volley.toolbox.StringRequest;
import com.android.volley.toolbox.Volley;
import com.bilal.datacollectionform.helper.CallbackHelper;
import com.bilal.datacollectionform.helper.FileChooser;
import com.bilal.datacollectionform.helper.Helper;
import com.bilal.datacollectionform.service.FileUploadFirebaseService;

import org.json.JSONObject;

import java.util.ArrayList;
import java.util.Arrays;
import java.util.Date;
import java.util.List;

import io.realm.Realm;
import io.realm.RealmList;
import io.realm.RealmObject;
import io.realm.RealmResults;
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
        this.json = model.json;
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
                if (i.type.equals(FormQuestionModel.TYPE_FILE_UPLOAD) || i.type.equals(FormQuestionModel.TYPE_IMAGE)) {
                    JSONObject questionJson = new JSONObject();
                    questionJson.put("label", i.label);
                    questionJson.put("type", i.type);
                    jsonObject.put("" + pos++, questionJson);
                    if (i.value != null) {
                        if (i.value.length() > 0) {
                            Uri uri = Uri.parse(i.value);
                            questionJson.put("value", Helper.getFileName(context, uri));

                            FileModel fileModel = new FileModel();
                            fileModel.uri = i.value;
                            if (i.type.equals(FormQuestionModel.TYPE_FILE_UPLOAD)) {
                                fileModel.path = FileChooser.getPath(context, uri);
                            } else if (i.type.equals(FormQuestionModel.TYPE_IMAGE)) {
                                fileModel.path = FileChooser.getImageFilePath(context, uri);
                            }
                            fileModel.questionAnswerPrimaryKey = i.primaryKey;
                            fileModel.formId = i.formId;
                            if (i.type.equals(FormQuestionModel.TYPE_IMAGE)) {
                                fileModel.type = FileModel.TYPE_IMAGE;
                            } else {
                                fileModel.type = FileModel.TYPE_FILE;
                            }
                            fileModel.syncedWithServer = false;
                            fileModel.primaryKey = PrimaryKeyModel.getFileModelPrimaryKey(context);
                            FileModel.saveToRealm(context, fileModel);

                        } else {
                            questionJson.put("value","");
                        }
                    } else {
                        questionJson.put("value","");
                    }
                } else {
                    JSONObject questionJson = new JSONObject();
                    questionJson.put("label", i.label);
                    questionJson.put("value", i.value);
                    questionJson.put("type", i.type);
                    jsonObject.put("" + pos++, questionJson);
                }
            }
            FormAnswerModel.saveToRealm(context, this);
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

    public void syncUploadToServer(final Context context, final FormAnswerModel formAnswerModel, final CallbackHelper.IntCallback callback) {
        RequestQueue queue = Volley.newRequestQueue(context);
        final FormAnswerModel asyncModel = new FormAnswerModel(formAnswerModel);
        String url = "http://rdaps.com/form/api/submit.php";
        StringRequest postRequest = new StringRequest(Request.Method.POST, url,
                new Response.Listener<String>() {
                    @Override
                    public void onResponse(String response) {
                        //formAnswerModel.setSyncedWithServer(context, true);
                        callback.onSuccess(Integer.parseInt(response));
                    }
                },
                new Response.ErrorListener() {
                    @Override
                    public void onErrorResponse(VolleyError error) {
                        //formAnswerModel.setSyncedWithServer(context, false);
                        Log.e(TAG, "syncUploadToServer, onErrorResponse : " + error.toString());
                        error.printStackTrace();
                        callback.onFailure();
                    }
                }
        ) {
            @Override
            public byte[] getBody() throws com.android.volley.AuthFailureError {
                return asyncModel.json.getBytes();
            }

            public String getBodyContentType() {
                return "application/text; charset=utf-8";
            }
            /*
            @Override
            protected Map<String, String> getParams() {
                Map<String, String> params = new HashMap<String, String>();
                params.put("datasubmissionjson", formAnswerModel.json);
                return params;
            }
            */
        };
        postRequest.setShouldCache(false);
        queue.add(postRequest);
    }

    public static void setSyncedWithServer(Context context, FormAnswerModel model, boolean synced) {
        Realm.init(context);
        Realm realm = Realm.getDefaultInstance();
        realm.beginTransaction();
        model.syncedWithServer = synced;
        realm.commitTransaction();
        realm.close();
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
        realm.copyToRealmOrUpdate(model);
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

    public static void deleteModel(Context context, FormAnswerModel model) {
        Realm.init(context);
        FormAnswerModel formAnswerModel = getModelForPrimaryKey(context, model.primaryKey);
        Realm realm = Realm.getDefaultInstance();
        realm.beginTransaction();
        formAnswerModel.deleteFromRealm();
        realm.commitTransaction();
        realm.close();
    }

    public static ArrayList<FormAnswerModel> getAllUnsyncedModels(Context context) {
        Realm.init(context);
        Realm realm = Realm.getDefaultInstance();
        RealmResults<FormAnswerModel> realmResults = realm.where(FormAnswerModel.class)
                .equalTo("syncedWithServer", false).findAll();
        ArrayList<FormAnswerModel> list = new ArrayList<>();
        for (FormAnswerModel i : realmResults) {
            FormAnswerModel model = new FormAnswerModel(i);
            list.add(model);
        }
        realm.close();
        return  list;
    }
}
