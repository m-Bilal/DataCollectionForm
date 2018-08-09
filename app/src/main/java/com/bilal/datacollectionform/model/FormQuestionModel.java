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
import java.util.Map;

import io.realm.Realm;
import io.realm.RealmList;
import io.realm.RealmObject;
import io.realm.annotations.Ignore;

public class FormQuestionModel extends RealmObject{

    @Ignore
    private final static String TAG = "FromAnswerModel";

    public int formId;
    public int id;
    public String label;
    public String type;
    public int required;
    public RealmList<QuestionConditionModel> conditionList;
    public RealmList<QuestionOptionModel> optionList;
    public String answer;
    public RealmList<QuestionOptionModel> answerList;

    public FormQuestionModel() {

    }

    public FormQuestionModel(FormQuestionModel formQuestionModel) {
        this.id = formQuestionModel.id;
        this.label = formQuestionModel.label;
        this.type = formQuestionModel.type;
        this.required = formQuestionModel.required;
        this.conditionList = formQuestionModel.conditionList;
        this.optionList = formQuestionModel.optionList;
        this.answer = formQuestionModel.answer;
        this.answerList = formQuestionModel.answerList;
    }

    public static void syncAllFroms(final Context context, final FormModel formModel, final CallbackHelper.Callback callback) {
        RequestQueue queue = Volley.newRequestQueue(context);
        String url = "http://rdaps.com/form/api/form_view.php";
        StringRequest postRequest = new StringRequest(Request.Method.POST, url,
                new Response.Listener<String>() {
                    @Override
                    public void onResponse(String response) {
                        try {
                            JSONArray jsonArray = new JSONArray(response);
                            for (int i = 0; i < jsonArray.length(); i++) {
                                FormQuestionModel formQuestionModel = new FormQuestionModel();
                                formQuestionModel.formId = formModel.formId;
                                formQuestionModel.id = jsonArray.getJSONObject(i).getInt("id");
                                formQuestionModel.label = jsonArray.getJSONObject(i).getString("label");
                                formQuestionModel.type = jsonArray.getJSONObject(i).getString("type");
                                formQuestionModel.required = jsonArray.getJSONObject(i).getInt("required");
                                JSONArray conditions = jsonArray.getJSONObject(i).getJSONArray("condition");
                                JSONArray options = jsonArray.getJSONObject(i).getJSONArray("options");

                                if (conditions != null) {
                                    formQuestionModel.conditionList = new RealmList<>();
                                    for (int j = 0; j < conditions.length(); j++) {
                                        QuestionConditionModel conditionModel = new QuestionConditionModel();
                                        conditionModel.equals = jsonArray.getJSONObject(j).getString("equals");
                                        conditionModel.$do = jsonArray.getJSONObject(j).getString("do");
                                        conditionModel.doIt = jsonArray.getJSONObject(j).getString("doit");
                                        conditionModel.to = jsonArray.getJSONObject(j).getString("to");
                                        conditionModel.law = jsonArray.getJSONObject(j).getString("law");
                                        QuestionConditionModel.saveToRealm(context, conditionModel);
                                        formQuestionModel.conditionList.add(conditionModel);
                                    }
                                }

                                if (options != null) {
                                    formQuestionModel.optionList = new RealmList<>();
                                    for (int j = 0; j < options.length(); j++) {
                                        QuestionOptionModel questionOptionModel = new QuestionOptionModel();
                                        questionOptionModel.value = jsonArray.getJSONObject(j).getString("val");
                                        try {
                                            questionOptionModel.sMin = jsonArray.getJSONObject(j).getString("smin");
                                            questionOptionModel.containsSMin = true;
                                        } catch (Exception e) {
                                            questionOptionModel.containsSMin = false;
                                        }
                                        try {
                                            questionOptionModel.sMax = jsonArray.getJSONObject(j).getString("smax");
                                            questionOptionModel.containsSMax = true;
                                        } catch (Exception e) {
                                            questionOptionModel.containsSMax = false;
                                        }
                                        questionOptionModel.hashkey = jsonArray.getJSONObject(j).getString("$$hashkey");
                                        QuestionOptionModel.saveToRealm(context, questionOptionModel);
                                        formQuestionModel.optionList.add(questionOptionModel);
                                    }
                                }
                                saveToRealm(context, formQuestionModel);
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
}
