package com.bilal.datacollectionform.activity;

import android.content.Context;
import android.content.Intent;
import android.support.v7.app.AppCompatActivity;
import android.os.Bundle;

import com.bilal.datacollectionform.R;
import com.bilal.datacollectionform.model.FileModel;
import com.bilal.datacollectionform.model.FormAnswerModel;
import com.bilal.datacollectionform.model.PrimaryKeyModel;
import com.bilal.datacollectionform.model.UserModel;

import io.realm.Realm;
import io.realm.RealmConfiguration;

public class SplashActivity extends AppCompatActivity {

    Context context = this;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_splash);

        initializeRealm();
        initilizePrimaryKeys();
        deleteSyncedModels();

        if (UserModel.getUserFromRealm(context) != null) {
            startMainActivity();
        } else {
            startLoginActivity();
        }
    }

    private void initializeRealm() {
        Realm.init(getApplicationContext());

        // create your Realm configuration
        RealmConfiguration realmConfiguration = new RealmConfiguration
                .Builder()
                .deleteRealmIfMigrationNeeded()
                .name("myDataRealm")
                .build();
        Realm.setDefaultConfiguration(realmConfiguration);
    }

    private void initilizePrimaryKeys() {
        if (PrimaryKeyModel.getPrimaryKeyModel(context) == null) {
            PrimaryKeyModel.createPrimaryKeyModel(context);
        }
    }

    private void deleteSyncedModels() {
        FileModel.deleteAllSyncedModelsFromRealm(context);
        FormAnswerModel.deleteAllSyncedModelsFromRealm(context);
    }

    private void startMainActivity() {
        Intent intent = new Intent(context, MainActivity.class);
        startActivity(intent);
        finish();
    }

    private void startLoginActivity() {
        Intent intent = new Intent(context, LoginActivity.class);
        startActivity(intent);
        finish();
    }
}
