package com.bilal.datacollectionform.model;

import android.annotation.TargetApi;
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
import java.util.Map;

import io.realm.Realm;
import io.realm.RealmList;
import io.realm.RealmObject;
import io.realm.RealmResults;
import io.realm.annotations.Ignore;
import io.realm.annotations.PrimaryKey;

public class FormQuestionModel extends RealmObject{

    @Ignore
    private final static String TAG = "FormQuestionModel";

    public static final String TYPE_TEXT = "text";
    public static final String TYPE_DROPDOWN = "dropdown";
    public static final String TYPE_DATE = "date";
    public static final String TYPE_STARS = "stars";
    public static final String TYPE_FILE_UPLOAD = "upload";
    public static final String TYPE_CHECKBOX = "check";
    public static final String TYPE_RADIO_BUTTON= "radio";
    public static final String TYPE_EMAIL = "email";
    public static final String TYPE_TIME = "time";
    public static final String TYPE_IMAGE = "image";
    public static final String TYPE_HIDDEN = "hidden";
    public static final String TYPE_PARAGRAPH = "para";
    public static final String TYPE_SUBMIT = "submitt";

    @PrimaryKey
    public int primaryKey;

    public int formId;
    public int id; // Server id, not to be used as primary key
    public String label;
    public String type;
    public int required;
    public RealmList<QuestionConditionModel> conditionList;
    public RealmList<QuestionOptionModel> optionList;

    public FormQuestionModel() {

    }

    public FormQuestionModel(FormQuestionModel formQuestionModel) {
        this.id = formQuestionModel.id;
        this.label = formQuestionModel.label;
        this.type = formQuestionModel.type;
        this.required = formQuestionModel.required;
        this.conditionList = formQuestionModel.conditionList;
        this.optionList = formQuestionModel.optionList;
    }

    public static void syncForm(final Context context, final FormModel formModel, final CallbackHelper.Callback callback) {
        RequestQueue queue = Volley.newRequestQueue(context);
        String url = "http://rdaps.com/form/api/form_view.php";
        StringRequest postRequest = new StringRequest(Request.Method.POST, url,
                new Response.Listener<String>() {
                    @Override
                    public void onResponse(String response) {
                        try {
                            deleteAllForForm(context, formModel);
                            JSONArray jsonArray = new JSONArray(response);
                            for (int i = 0; i < jsonArray.length(); i++) {
                                FormQuestionModel formQuestionModel = new FormQuestionModel();
                                formQuestionModel.primaryKey = PrimaryKeyModel.getFormQuestionPrimaryKey(context);
                                formQuestionModel.formId = formModel.formId;
                                formQuestionModel.id = i;
                                try {
                                    formQuestionModel.label = jsonArray.getJSONObject(i).getString("label");
                                } catch (Exception e) {
                                    formQuestionModel.label = "";
                                }
                                try {
                                    formQuestionModel.type = jsonArray.getJSONObject(i).getString("type");
                                } catch (Exception e) {
                                    formQuestionModel.type = null;
                                }
                                formQuestionModel.required = jsonArray.getJSONObject(i).getInt("required");
                                JSONArray conditions;
                                try {
                                  conditions = jsonArray.getJSONObject(i).getJSONArray("condition");
                                } catch (Exception e) {
                                    conditions = null;
                                }
                                JSONArray options;
                                try {
                                    options = jsonArray.getJSONObject(i).getJSONArray("options");
                                } catch (Exception e) {
                                    options = null;
                                }

                                if (conditions != null) {
                                    formQuestionModel.conditionList = new RealmList<>();
                                    for (int j = 0; j < conditions.length(); j++) {
                                        QuestionConditionModel conditionModel = new QuestionConditionModel();
                                        conditionModel.primaryKey = PrimaryKeyModel.getQuestionConditionPrimaryKey(context);
                                        try {
                                            conditionModel.equals = conditions.getJSONObject(j).getString("equals");
                                        } catch (Exception e) {
                                            conditionModel.equals = null;
                                        }
                                        try {
                                            conditionModel.$do = conditions.getJSONObject(j).getString("do");
                                        } catch (Exception e) {
                                            conditionModel.$do = null;
                                        }
                                        try {
                                            conditionModel.doIt = conditions.getJSONObject(j).getString("doit");
                                        } catch (Exception e) {
                                            conditionModel.doIt = null;
                                        }
                                        try {
                                            conditionModel.to = conditions.getJSONObject(j).getString("to");
                                        } catch (Exception e) {
                                            conditionModel.to = null;
                                        }
                                        try {
                                            conditionModel.law = conditions.getJSONObject(j).getString("law");
                                        } catch (Exception e) {
                                            conditionModel.law = null;
                                        }
                                        QuestionConditionModel.saveToRealm(context, conditionModel);
                                        formQuestionModel.conditionList.add(conditionModel);
                                    }
                                }

                                if (options != null) {
                                    formQuestionModel.optionList = new RealmList<>();
                                    for (int j = 0; j < options.length(); j++) {
                                        QuestionOptionModel questionOptionModel = new QuestionOptionModel();
                                        questionOptionModel.primaryKey = PrimaryKeyModel.getQuestionOptionPrimaryKey(context);
                                        try {
                                            questionOptionModel.value = options.getJSONObject(j).getString("val");
                                        } catch (Exception e) {
                                            questionOptionModel.value = "";
                                        }
                                        try {
                                            questionOptionModel.sMin = options.getJSONObject(j).getString("smin");
                                            questionOptionModel.containsSMin = true;
                                        } catch (Exception e) {
                                            questionOptionModel.containsSMin = false;
                                        }
                                        try {
                                            questionOptionModel.sMax = options.getJSONObject(j).getString("smax");
                                            questionOptionModel.containsSMax = true;
                                        } catch (Exception e) {
                                            questionOptionModel.containsSMax = false;
                                        }
                                        try {
                                            questionOptionModel.hashkey = options.getJSONObject(j).getString("$$hashKey");
                                        } catch (Exception e) {
                                            questionOptionModel.hashkey = "";
                                        }
                                        QuestionOptionModel.saveToRealm(context, questionOptionModel);
                                        formQuestionModel.optionList.add(questionOptionModel);
                                    }
                                }
                                saveToRealm(context, formQuestionModel);
                                FormModel.addQuestionToForm(context, formModel, formQuestionModel);
                            }
                            callback.onSuccess();
                        } catch (Exception e) {
                            e.printStackTrace();
                            Log.e(TAG, "syncForm(), onResponse(), catch: " + e.toString());
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
                params.put("formid", formModel.formId + "");
                return params;
            }
        };
        postRequest.setShouldCache(false);
        queue.add(postRequest);
    }

    public static void saveToRealm(Context context, FormQuestionModel formQuestionModel) {
        Realm.init(context);
        Realm realm = Realm.getDefaultInstance();
        realm.beginTransaction();
        realm.copyToRealmOrUpdate(formQuestionModel);
        realm.commitTransaction();
        realm.close();
    }

    public static RealmList<FormQuestionModel> getAllForForm(Context context, FormModel formModel) {
        Realm.init(context);
        Realm realm = Realm.getDefaultInstance();
        realm.beginTransaction();
        RealmResults<FormQuestionModel> realmResults = realm.where(FormQuestionModel.class)
                .equalTo("formId", formModel.formId).findAll();
        RealmList<FormQuestionModel> list = new RealmList<>();
        list.addAll(realmResults.subList(0, realmResults.size()));
        realm.close();
        return list;
    }

    public static void deleteAllForForm(Context context, FormModel formModel) {
        Realm.init(context);
        Realm realm = Realm.getDefaultInstance();
        RealmResults<FormQuestionModel> realmResults = realm.where(FormQuestionModel.class)
                .equalTo("formId", formModel.formId).findAll();
        realm.beginTransaction();
        realmResults.deleteAllFromRealm();
        realm.commitTransaction();
        realm.close();
    }

    public static FormQuestionModel getModelForPrimaryKey(Context context, int primaryKey) {
        Realm.init(context);
        Realm realm = Realm.getDefaultInstance();
        FormQuestionModel formQuestionModel = realm.where(FormQuestionModel.class).equalTo("primaryKey", primaryKey)
                .findFirst();
        return formQuestionModel;
    }
}
