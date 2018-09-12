package com.bilal.datacollectionform.model;

import android.content.Context;
import android.net.Uri;
import android.util.Log;

import com.android.volley.NetworkResponse;
import com.android.volley.Request;
import com.android.volley.RequestQueue;
import com.android.volley.Response;
import com.android.volley.VolleyError;
import com.android.volley.toolbox.Volley;
import com.bilal.datacollectionform.helper.CallbackHelper;
import com.bilal.datacollectionform.helper.FileChooser;
import com.bilal.datacollectionform.request.VolleyMultipartRequest;

import java.io.BufferedInputStream;
import java.io.File;
import java.io.FileInputStream;
import java.io.FileNotFoundException;
import java.io.IOException;
import java.util.HashMap;
import java.util.Map;

import io.realm.Realm;
import io.realm.RealmObject;
import io.realm.RealmResults;
import io.realm.annotations.PrimaryKey;

import static java.security.AccessController.getContext;

public class FileModel extends RealmObject {

    public final static int TYPE_IMAGE = 1;
    public final static int TYPE_FILE = 2;
    private final static String TAG = "FileModel";
    @PrimaryKey
    public int primaryKey;
    public String uri;
    public String path;
    public int questionAnswerPrimaryKey;
    public int formId;
    public int type;
    public boolean syncedWithServer;

    public FileModel() {
        syncedWithServer = false;
    }

    public FileModel(FileModel model) {
        this.primaryKey = model.primaryKey;
        this.uri = model.uri;
        this.questionAnswerPrimaryKey = model.questionAnswerPrimaryKey;
        this.formId = model.formId;
        this.type = model.type;
        this.syncedWithServer = model.syncedWithServer;
    }

    private static byte[] getBytesForFile(File file) {
        int size = (int) file.length();
        byte[] bytes = new byte[size];
        try {
            BufferedInputStream buf = new BufferedInputStream(new FileInputStream(file));
            buf.read(bytes, 0, bytes.length);
            buf.close();
        } catch (FileNotFoundException e) {
            bytes = null;
            e.printStackTrace();
        } catch (IOException e) {
            bytes = null;
            e.printStackTrace();
        }
        return bytes;
    }

    public static RealmResults<FileModel> getAllUnsyncedModels(Context context) {
        Realm.init(context);
        Realm realm = Realm.getDefaultInstance();
        RealmResults<FileModel> realmResults = realm.where(FileModel.class)
                .equalTo("syncedWithServer", false).findAll();
        return realmResults;
    }

    public void syncUploadToServer(final Context context, final FileModel fileModel, final CallbackHelper.Callback callback) {
        RequestQueue queue = Volley.newRequestQueue(context);
        final File file = new File(fileModel.path);
        final byte[] fileBytes = getBytesForFile(file);
        final FileModel asyncModel = new FileModel(fileModel);
        String url = "http://rdaps.com/form/api/filesubmit.php";
        VolleyMultipartRequest multipartRequest = new VolleyMultipartRequest(Request.Method.POST, url, new Response.Listener<NetworkResponse>() {
            @Override
            public void onResponse(NetworkResponse response) {
                String resultResponse = new String(response.data);
                callback.onSuccess();
                /*
                try {
                    JSONObject result = new JSONObject(resultResponse);
                    String status = result.getString("status");
                    String message = result.getString("message");

                    if (status.equals(Constant.REQUEST_SUCCESS)) {
                        // tell everybody you have succed upload image and post strings
                        Log.i("Messsage", message);
                    } else {
                        Log.i("Unexpected", message);
                    }
                } catch (JSONException e) {
                    e.printStackTrace();
                }
                */
            }
        }, new Response.ErrorListener() {
            @Override
            public void onErrorResponse(VolleyError error) {
                NetworkResponse networkResponse = error.networkResponse;
                String errorMessage = "Unknown error";
                callback.onFailure();
                Log.d(TAG, "syncUploadToServer, onErrorResponse: " + error.toString());
                error.printStackTrace();
                /*
                if (networkResponse == null) {
                    if (error.getClass().equals(TimeoutError.class)) {
                        errorMessage = "Request timeout";
                    } else if (error.getClass().equals(NoConnectionError.class)) {
                        errorMessage = "Failed to connect server";
                    }
                } else {
                    String result = new String(networkResponse.data);
                    try {
                        JSONObject response = new JSONObject(result);
                        String status = response.getString("status");
                        String message = response.getString("message");

                        Log.e("Error Status", status);
                        Log.e("Error Message", message);

                        if (networkResponse.statusCode == 404) {
                            errorMessage = "Resource not found";
                        } else if (networkResponse.statusCode == 401) {
                            errorMessage = message+" Please login again";
                        } else if (networkResponse.statusCode == 400) {
                            errorMessage = message+ " Check your inputs";
                        } else if (networkResponse.statusCode == 500) {
                            errorMessage = message+" Something is getting wrong";
                        }
                    } catch (JSONException e) {
                        e.printStackTrace();
                    }
                }
                Log.i("Error", errorMessage);
                error.printStackTrace();
                */
            }
        }) {
            @Override
            protected Map<String, String> getParams() {
                Map<String, String> params = new HashMap<>();
                params.put("formid", asyncModel.formId + "");
                if (asyncModel.type == TYPE_IMAGE) {
                    params.put("type", "Image");
                } else if (asyncModel.type == TYPE_FILE) {
                    params.put("type", "File");
                }
                return params;
            }

            @Override
            protected Map<String, DataPart> getByteData() {
                Map<String, DataPart> params = new HashMap<>();
                // file name could found file base or direct access from real path
                // for now just get bitmap data from ImageView
                //params.put("avatar", new DataPart("file_avatar.jpg", AppHelper.getFileDataFromDrawable(getBaseContext(), mAvatarImage.getDrawable()), "image/jpeg"));
                if (asyncModel.type == TYPE_IMAGE) {
                    params.put("img", new DataPart(file.getName(), fileBytes, "image/jpeg"));
                } else if (asyncModel.type == TYPE_FILE) {
                    params.put("img", new DataPart(file.getName(), fileBytes, "image/jpeg")); // TODO: change MIME Type
                }
                return params;
            }
        };
        multipartRequest.setShouldCache(false);
        queue.add(multipartRequest);
    }

    public void setSyncedWithServer(Context context, boolean synced) {
        Realm.init(context);
        Realm realm = Realm.getDefaultInstance();
        realm.beginTransaction();
        this.syncedWithServer = synced;
        realm.commitTransaction();
        realm.close();
    }

    public static void saveToRealm(Context context, FileModel model) {
        Realm.init(context);
        model.primaryKey = PrimaryKeyModel.getFileModelPrimaryKey(context);
        Realm realm = Realm.getDefaultInstance();
        realm.beginTransaction();
        realm.copyToRealm(model);
        realm.commitTransaction();
        realm.close();
    }

    public static void deleteAllFromRealm(Context context) {
        Realm.init(context);
        Realm realm = Realm.getDefaultInstance();
        RealmResults<FileModel> realmResults = realm.where(FileModel.class).findAll();
        realm.beginTransaction();
        realmResults.deleteAllFromRealm();
        realm.commitTransaction();
        realm.close();
    }
}
