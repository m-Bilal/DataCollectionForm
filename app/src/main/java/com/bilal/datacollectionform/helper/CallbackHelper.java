package com.bilal.datacollectionform.helper;


import android.support.v4.app.Fragment;

import com.bilal.datacollectionform.model.FileModel;
import com.bilal.datacollectionform.model.FormAnswerModel;
import com.bilal.datacollectionform.model.FormModel;
import com.bilal.datacollectionform.model.QuestionAnswerModel;

public class CallbackHelper {

    public interface Callback {
        void onSuccess();
        void onFailure();
    }

    public interface IntCallback {
        void onSuccess(int response);
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

    public interface FragmentCallback {
        void setCurrentFragment(Fragment fragment);
    }

    public interface FileUploadServiceCallback {
        void updateFileUpload(int synced, int failed, int total);
        void completedFileUpload();
        void setSyncedWithServer(FileModel model, boolean synced);
    }

    public interface FormUploadServiceCallback {
        void updateFormUpload(int synced, int failed, int total);
        void completedFormUpload();
        void setSyncedWithServer(FormAnswerModel model, boolean synced);
    }
}
