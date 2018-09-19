package com.bilal.datacollectionform.model;

import android.content.Context;
import android.util.Log;

import com.android.volley.Request;
import com.android.volley.RequestQueue;
import com.android.volley.Response;
import com.android.volley.VolleyError;
import com.android.volley.toolbox.StringRequest;
import com.android.volley.toolbox.Volley;
import com.bilal.datacollectionform.helper.CallbackHelper;

import org.json.JSONArray;

import java.util.HashMap;
import java.util.LinkedList;
import java.util.List;
import java.util.Map;

import io.realm.Realm;
import io.realm.RealmList;
import io.realm.RealmObject;
import io.realm.RealmResults;
import io.realm.annotations.Ignore;
import io.realm.annotations.PrimaryKey;

public class FormModel extends RealmObject {

    // The model from server that contains all questions as a list

    @Ignore
    private final static String TAG = "FromModel";

    @PrimaryKey
    public int formId;
    public String formName;
    public RealmList<FormQuestionModel> formQuestionModelRealmList;
    public boolean syncedWithServer;
    public boolean previouslyOpened;

    @Ignore
    public List<FormQuestionModel> formQuestionModelList;

    public FormModel() {
        syncedWithServer = false;
    }

    public FormModel(FormModel formModel) {
        this.formId = formModel.formId;
        this.formName = formModel.formName;
        this.formQuestionModelRealmList = formModel.formQuestionModelRealmList;
        formQuestionModelList = new LinkedList<>();
        for (FormQuestionModel i : this.formQuestionModelRealmList) {
            formQuestionModelList.add(new FormQuestionModel(i));
        }
        this.syncedWithServer = formModel.syncedWithServer;
        this.previouslyOpened = formModel.previouslyOpened;
    }

    public static void syncAllForms(final Context context, final UserModel userModel, final CallbackHelper.Callback callback) {
        RequestQueue queue = Volley.newRequestQueue(context);
        String url = "http://rdaps.com/form/api/form.php";
        StringRequest postRequest = new StringRequest(Request.Method.POST, url,
                new Response.Listener<String>() {
                    @Override
                    public void onResponse(String response) {
                        try {
                            JSONArray jsonArray = new JSONArray(response);
                            for (int i = 0; i < jsonArray.length(); i++) {
                                FormModel formModel = new FormModel();
                                formModel.formId = jsonArray.getJSONObject(i).getInt("formid");
                                formModel.formName = jsonArray.getJSONObject(i).getString("formname");
                                if (alreadyOpened(context, formModel.formId)) {
                                    formModel.previouslyOpened = true;
                                } else {
                                    formModel.previouslyOpened = false;
                                    saveFromToRealm(context, formModel);
                                }

                            }
                            callback.onSuccess();
                        } catch (Exception e) {
                            e.printStackTrace();
                            Log.e(TAG, "syncAllForms(), onResponse(), catch: " + e.toString());
                            callback.onFailure();
                        }
                    }
                },
                new Response.ErrorListener() {
                    @Override
                    public void onErrorResponse(VolleyError error) {
                        // error
                        Log.d(TAG, "syncAllForms(), onErrorResponse(): " + error.toString());
                        error.printStackTrace();
                        callback.onFailure();
                    }
                }
        ) {
            @Override
            protected Map<String, String> getParams() {
                Map<String, String> params = new HashMap<String, String>();
                params.put("userid", userModel.userId + "");
                return params;
            }
        };
        postRequest.setShouldCache(false);
        queue.add(postRequest);
    }

    public void setOpened(Context context) {
        Realm.init(context);
        Realm realm = Realm.getDefaultInstance();
        realm.beginTransaction();
        this.previouslyOpened = true;
        realm.copyToRealmOrUpdate(this);
        realm.commitTransaction();
        realm.close();
    }

    public static void saveFromToRealm(Context context, FormModel formModel) {
        Realm.init(context);
        Realm realm = Realm.getDefaultInstance();
        realm.beginTransaction();
        realm.copyToRealmOrUpdate(formModel);
        realm.commitTransaction();
        realm.close();
    }

    public static RealmResults<FormModel> getAllFromRealm(Context context) {
        Realm.init(context);
        Realm realm = Realm.getDefaultInstance();
        RealmResults<FormModel> formModels = realm.where(FormModel.class).findAll();
        return formModels;
    }

    public static RealmResults<FormModel> getAllUnopenedFromRealm(Context context) {
        // For new forms
        Realm.init(context);
        Realm realm = Realm.getDefaultInstance();
        RealmResults<FormModel> formModels = realm.where(FormModel.class)
                .equalTo("previouslyOpened", false).findAll();
        return formModels;
    }

    public static RealmResults<FormModel> getAllOpenedFromRealm(Context context) {
        // For existing forms
        Realm.init(context);
        Realm realm = Realm.getDefaultInstance();
        RealmResults<FormModel> formModels = realm.where(FormModel.class)
                .equalTo("previouslyOpened", true).findAll();
        return formModels;
    }

    public static void addQuestionToForm(Context context, FormModel formModel, FormQuestionModel formQuestionModel) {
        Realm.init(context);
        Realm realm = Realm.getDefaultInstance();
        realm.beginTransaction();
        FormModel formModel1 = realm.where(FormModel.class).equalTo("formId", formModel.formId).findFirst();
        if (formModel1.formQuestionModelRealmList == null) {
            formModel1.formQuestionModelRealmList = new RealmList<>();
        }
        formModel1.formQuestionModelRealmList.add(formQuestionModel);
        realm.commitTransaction();
        realm.close();
    }

    public static FormModel getFromForId(Context context, int id) {
        Realm.init(context);
        Realm realm = Realm.getDefaultInstance();
        FormModel model = realm.where(FormModel.class).equalTo("formId", id).findFirst();
        return new FormModel(model);
    }

    private static boolean alreadyOpened(Context context, int formId) {
        Realm.init(context);
        Realm realm = Realm.getDefaultInstance();
        RealmResults<FormQuestionModel> realmResults = realm.where(FormQuestionModel.class)
                .equalTo("formId", formId).findAll();
        if (realmResults.size() > 0) {
            realm.close();
            return true;
        } else {
            realm.close();
            return false;
        }
    }

    public static void checkOpenedFroms(Context context) {
        Realm.init(context);
        Realm realm = Realm.getDefaultInstance();
        RealmResults<FormModel> realmResults = realm.where(FormModel.class)
                .equalTo("previouslyOpened", true).findAll();
        for (FormModel i : realmResults) {

        }
    }

    public static String getName(Context context, int formId) {
        Realm.init(context);
        Realm realm = Realm.getDefaultInstance();
        FormModel model = realm.where(FormModel.class).equalTo("formId", formId).findFirst();
        String name = model.formName;
        realm.close();
        return name;
    }
}
