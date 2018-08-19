package com.bilal.datacollectionform.helper;

import com.bilal.datacollectionform.model.QuestionAnswerModel;

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

    public interface FragmentAnswerCallback {
        void addAnswer(QuestionAnswerModel questionAnswerModel);
        void updateAnswer(QuestionAnswerModel questionAnswerModel);
    }
}
