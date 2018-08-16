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

import org.json.JSONObject;

import java.text.SimpleDateFormat;
import java.util.Date;
import java.util.HashMap;
import java.util.Map;

import io.realm.Realm;
import io.realm.RealmObject;
import io.realm.RealmResults;
import io.realm.annotations.Ignore;
import io.realm.annotations.PrimaryKey;

public class UserModel extends RealmObject{

    @Ignore
    private final static String TAG = "UserModel";

    public int login;
    public String message;
    public String user;
    @PrimaryKey
    public int userId;
    public int noOfEntries;
    public int noOfForms;
    public String name;

    public UserModel() {

    }

    public UserModel(UserModel userModel) {
        this.login = userModel.login;
        this.message = userModel.message;
        this.user = userModel.user;
        this.userId = userModel.userId;
        this.noOfEntries = userModel.noOfEntries;
        this.noOfForms = userModel.noOfForms;
        this.name = userModel.name;
    }

    public static void syncUserLogin(final Context context, final String username, final String password, final CallbackHelper.LoginCallback callback) {
        RequestQueue queue = Volley.newRequestQueue(context);
        String url = "http://rdaps.com/form/api/login.php";
        StringRequest postRequest = new StringRequest(Request.Method.POST, url,
                new Response.Listener<String>() {
                    @Override
                    public void onResponse(String response) {
                        try {
                            deleteUserFromRealm(context);
                            JSONObject jsonObject = new JSONObject(response);
                            UserModel userModel = new UserModel();
                            userModel.login = jsonObject.getInt("login");
                            if (userModel.login == 1) {
                                userModel.user = jsonObject.getString("user");
                                userModel.userId = jsonObject.getInt("userid");
                                userModel.noOfEntries = jsonObject.getInt("no_of_entries");
                                userModel.noOfForms = jsonObject.getInt("no_of_form");
                                userModel.name = jsonObject.getString("name");
                                userModel.message = jsonObject.getString("mes");
                                saveUserToRealm(context, userModel);
                                callback.onSuccess();
                            } else {
                                callback.onAuthenticationFailed();
                            }
                        } catch (Exception e) {
                            e.printStackTrace();
                            Log.e(TAG, "syncUserLogin(), onResponse(), catch: " + e.toString());
                            callback.onFailure();
                        }
                    }
                },
                new Response.ErrorListener() {
                    @Override
                    public void onErrorResponse(VolleyError error) {
                        // error
                        Log.d(TAG, "syncUserLogin(), onErrorResponse(): " + error.toString());
                        error.printStackTrace();
                        callback.onFailure();
                    }
                }
        ) {
            @Override
            protected Map<String, String> getParams() {
                Map<String, String> params = new HashMap<String, String>();
                params.put("user", username);
                params.put("pwd", password);
                return params;
            }
        };
        postRequest.setShouldCache(false);
        queue.add(postRequest);
    }

    public static void saveUserToRealm(Context context, UserModel userModel) {
        Realm.init(context);
        Realm realm = Realm.getDefaultInstance();
        realm.beginTransaction();
        realm.copyToRealmOrUpdate(userModel);
        realm.commitTransaction();
        realm.close();
    }

    public static void deleteUserFromRealm(Context context) {
        Realm.init(context);
        Realm realm = Realm.getDefaultInstance();
        RealmResults<UserModel> realmResults = realm.where(UserModel.class).findAll();
        realm.beginTransaction();
        realmResults.deleteAllFromRealm();
        realm.commitTransaction();
        realm.close();
    }

    public static UserModel getUserFromRealm(Context context) {
        Realm.init(context);
        Realm realm = Realm.getDefaultInstance();
        UserModel userModel = realm.where(UserModel.class).findFirst();
        if (userModel != null) {
            return new UserModel(userModel);
        } else {
            return null;
        }
    }
}
