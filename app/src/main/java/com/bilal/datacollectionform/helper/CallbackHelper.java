package com.bilal.datacollectionform.helper;

public class CallbackHelper {

    public interface Callback {
        void onSuccess();
        void onFailure();
    }

    public interface LoginCallback {
        void onSuccess();
        void onAuthenticationFailed(); // Called when the username/password is incorrect
        void onFailure();
    }
}
