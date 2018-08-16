package com.bilal.datacollectionform.activity;

import android.app.ProgressDialog;
import android.content.Context;
import android.content.Intent;
import android.support.v7.app.AppCompatActivity;
import android.os.Bundle;
import android.view.View;
import android.widget.EditText;
import android.widget.TextView;
import android.widget.Toast;

import com.bilal.datacollectionform.R;
import com.bilal.datacollectionform.helper.CallbackHelper;
import com.bilal.datacollectionform.model.UserModel;

public class LoginActivity extends AppCompatActivity {

    private Context context = this;

    private EditText usernameEdittext;
    private EditText passwordEdittext;
    private TextView loginTextview;

    private ProgressDialog progressDialog;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_login);

        usernameEdittext = findViewById(R.id.edittext_username);
        passwordEdittext = findViewById(R.id.edittext_password);
        loginTextview = findViewById(R.id.textview_login);
        progressDialog = new ProgressDialog(context);

        loginTextview.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                String username = usernameEdittext.getText().toString();
                String password = passwordEdittext.getText().toString();

                if (username == null || username.trim().length() == 0) {
                    usernameEdittext.setError("Cannot be empty");
                } else if (password == null || password.length() == 0) {
                    passwordEdittext.setError("Cannot be empty");
                } else {
                    progressDialog.setMessage("Please wait");
                    progressDialog.show();
                    syncUser(username, password);
                }
            }
        });
    }

    private void syncUser(String username, String password) {
        UserModel.syncUserLogin(context, username, password, new CallbackHelper.LoginCallback() {
            @Override
            public void onSuccess() {
                progressDialog.dismiss();
                startMainActivity();
            }

            @Override
            public void onAuthenticationFailed() {
                Toast.makeText(context, "Incorrect username or password", Toast.LENGTH_SHORT).show();
            }

            @Override
            public void onFailure() {
                progressDialog.dismiss();
                Toast.makeText(context, "No internet", Toast.LENGTH_SHORT).show();
            }
        });
    }

    private void startMainActivity() {
        Intent intent = new Intent(context, MainActivity.class);
        startActivity(intent);
    }
}
