package com.bilal.datacollectionform.activity;

import android.content.Context;
import android.os.Bundle;
import android.support.v4.app.Fragment;
import android.support.v7.app.AppCompatActivity;
import android.support.v7.widget.Toolbar;
import android.util.Log;
import android.view.View;
import android.widget.FrameLayout;
import android.widget.TextView;

import com.bilal.datacollectionform.R;
import com.bilal.datacollectionform.fragment.AnswerListFragment;
import com.bilal.datacollectionform.fragment.CheckboxAnswerFragment;
import com.bilal.datacollectionform.fragment.DateAnswerFragment;
import com.bilal.datacollectionform.fragment.DropdownAnswerFragment;
import com.bilal.datacollectionform.fragment.FileAnswerFragment;
import com.bilal.datacollectionform.fragment.RadioButtonAnswerFragment;
import com.bilal.datacollectionform.fragment.StarAnswerFragment;
import com.bilal.datacollectionform.fragment.TextAnswerFragment;
import com.bilal.datacollectionform.fragment.TimeAnswerFragment;
import com.bilal.datacollectionform.helper.CallbackHelper;
import com.bilal.datacollectionform.model.FormAnswerModel;
import com.bilal.datacollectionform.model.FormModel;
import com.bilal.datacollectionform.model.FormQuestionModel;
import com.bilal.datacollectionform.model.QuestionAnswerModel;
import com.bilal.datacollectionform.model.UserModel;

import java.util.LinkedList;
import java.util.List;

public class FormQuestionActivity extends AppCompatActivity implements CallbackHelper.FragmentAnswerCallback {

    private final static String TAG = "FormQuestionActivity";

    public final static String INTENT_ARG_FORM_ID = "form_id";

    public final static String BUNDLE_ARG_QUESTION_KEY = "primary_key";
    public final static String BUNDLE_ARG_ANSWER_FORM_KEY = "answer_form_key";
    public final static String BUNDLE_ARG_POSITION = "pos";

    private Context context;

    private FormModel formModel;
    private List<Fragment> fragmentList;

    private Toolbar toolbar;
    private FrameLayout frameLayout;
    private TextView previousTextview;
    private TextView nextTextview;

    private FormAnswerModel formAnswerModel;

    private int pos;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_form_question);

        context = this;

        frameLayout = findViewById(R.id.frame_layout);
        toolbar = findViewById(R.id.toolbar);
        previousTextview = findViewById(R.id.textview_previous);
        nextTextview = findViewById(R.id.textview_next);
        setSupportActionBar(toolbar);
        getSupportActionBar().setDisplayHomeAsUpEnabled(true);
        getSupportActionBar().setDisplayShowHomeEnabled(true);
        getSupportActionBar().setDisplayShowTitleEnabled(false);

        int formId = getIntent().getIntExtra(INTENT_ARG_FORM_ID, 0);
        formModel = FormModel.getFromForId(context, formId);
        formAnswerModel = FormAnswerModel.createModel(context, UserModel.getUserFromRealm(context),
                formModel);

        createQuestionFragments();
        attachFirstFragment();

        previousTextview.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                attachPreviousFragment();
            }
        });

        nextTextview.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                attachNextFragment();
            }
        });
    }

    private void createQuestionFragments() {
        fragmentList = new LinkedList<>();
        pos = 0;
        int j = 0;
        for (FormQuestionModel i : formModel.formQuestionModelRealmList) {
            if (i.type.equalsIgnoreCase(FormQuestionModel.TYPE_EMAIL)
                    || i.type.equalsIgnoreCase(FormQuestionModel.TYPE_PARAGRAPH)
                    || i.type.equalsIgnoreCase(FormQuestionModel.TYPE_TEXT)) {
                Fragment fragment = new TextAnswerFragment();
                Bundle bundle = new Bundle();
                Log.d(TAG, "createQuestionFragment, pk : " + i.primaryKey);
                bundle.putInt(BUNDLE_ARG_QUESTION_KEY, i.primaryKey);
                bundle.putInt(BUNDLE_ARG_ANSWER_FORM_KEY, formAnswerModel.primaryKey);
                bundle.putInt(BUNDLE_ARG_POSITION, j++);
                fragment.setArguments(bundle);
                fragmentList.add(fragment);
            } else if (i.type.equalsIgnoreCase(FormQuestionModel.TYPE_DROPDOWN)) {
                Fragment fragment = new DropdownAnswerFragment();
                Bundle bundle = new Bundle();
                bundle.putInt(BUNDLE_ARG_QUESTION_KEY, i.primaryKey);
                bundle.putInt(BUNDLE_ARG_ANSWER_FORM_KEY, formAnswerModel.primaryKey);
                bundle.putInt(BUNDLE_ARG_POSITION, j++);
                fragment.setArguments(bundle);
                fragmentList.add(fragment);
            } else if (i.type.equalsIgnoreCase(FormQuestionModel.TYPE_DATE)) {
                Fragment fragment = new DateAnswerFragment();
                Bundle bundle = new Bundle();
                bundle.putInt(BUNDLE_ARG_QUESTION_KEY, i.primaryKey);
                bundle.putInt(BUNDLE_ARG_ANSWER_FORM_KEY, formAnswerModel.primaryKey);
                bundle.putInt(BUNDLE_ARG_POSITION, j++);
                fragment.setArguments(bundle);
                fragmentList.add(fragment);
            } else if (i.type.equalsIgnoreCase(FormQuestionModel.TYPE_STARS)) {
                Fragment fragment = new StarAnswerFragment();
                Bundle bundle = new Bundle();
                bundle.putInt(BUNDLE_ARG_QUESTION_KEY, i.primaryKey);
                bundle.putInt(BUNDLE_ARG_ANSWER_FORM_KEY, formAnswerModel.primaryKey);
                bundle.putInt(BUNDLE_ARG_POSITION, j++);
                fragment.setArguments(bundle);
                fragmentList.add(fragment);
            } else if (i.type.equalsIgnoreCase(FormQuestionModel.TYPE_FILE_UPLOAD)) {
                Fragment fragment = new FileAnswerFragment();
                Bundle bundle = new Bundle();
                bundle.putInt(BUNDLE_ARG_QUESTION_KEY, i.primaryKey);
                bundle.putInt(BUNDLE_ARG_ANSWER_FORM_KEY, formAnswerModel.primaryKey);
                bundle.putInt(BUNDLE_ARG_POSITION, j++);
                fragment.setArguments(bundle);
                fragmentList.add(fragment);
            } else if (i.type.equalsIgnoreCase(FormQuestionModel.TYPE_CHECKBOX)) {
                Fragment fragment = new CheckboxAnswerFragment();
                Bundle bundle = new Bundle();
                bundle.putInt(BUNDLE_ARG_QUESTION_KEY, i.primaryKey);
                bundle.putInt(BUNDLE_ARG_ANSWER_FORM_KEY, formAnswerModel.primaryKey);
                bundle.putInt(BUNDLE_ARG_POSITION, j++);
                fragment.setArguments(bundle);
                fragmentList.add(fragment);
            } else if (i.type.equalsIgnoreCase(FormQuestionModel.TYPE_RADIO_BUTTON)) {
                Fragment fragment = new RadioButtonAnswerFragment();
                Bundle bundle = new Bundle();
                bundle.putInt(BUNDLE_ARG_QUESTION_KEY, i.primaryKey);
                bundle.putInt(BUNDLE_ARG_ANSWER_FORM_KEY, formAnswerModel.primaryKey);
                bundle.putInt(BUNDLE_ARG_POSITION, j++);
                fragment.setArguments(bundle);
                fragmentList.add(fragment);
            } else if (i.type.equalsIgnoreCase(FormQuestionModel.TYPE_TIME)) {
                Fragment fragment = new TimeAnswerFragment();
                Bundle bundle = new Bundle();
                bundle.putInt(BUNDLE_ARG_QUESTION_KEY, i.primaryKey);
                bundle.putInt(BUNDLE_ARG_ANSWER_FORM_KEY, formAnswerModel.primaryKey);
                bundle.putInt(BUNDLE_ARG_POSITION, j++);
                fragment.setArguments(bundle);
                fragmentList.add(fragment);
            } else if (i.type.equalsIgnoreCase(FormQuestionModel.TYPE_HIDDEN)) {
                // Hidden
            } else if (i.type.equalsIgnoreCase(FormQuestionModel.TYPE_SUBMIT)) {
                // submit button
            }
        }
    }

    private void attachFirstFragment() {
        frameLayout.removeAllViews();
        getSupportFragmentManager().beginTransaction().add(R.id.frame_layout, fragmentList.get(pos)).commit();
    }

    private void attachNextFragment() {
        pos++;
        if (pos < fragmentList.size()) {
            frameLayout.removeAllViews();
            getSupportFragmentManager().beginTransaction().replace(R.id.frame_layout, fragmentList.get(pos)).commit();
        } else if (pos == fragmentList.size()) {
            frameLayout.removeAllViews();
            Fragment fragment = new AnswerListFragment();
            Bundle bundle = new Bundle();
            bundle.putInt(BUNDLE_ARG_QUESTION_KEY, formAnswerModel.primaryKey);
            fragment.setArguments(bundle);
            getSupportFragmentManager().beginTransaction().replace(R.id.frame_layout, fragment).commit();
        }
    }

    private void attachPreviousFragment() {
        pos--;
        if (pos >= 0) {
            frameLayout.removeAllViews();
            getSupportFragmentManager().beginTransaction().replace(R.id.frame_layout, fragmentList.get(pos)).commit();
        }
    }

    @Override
    public void addAnswer(QuestionAnswerModel questionAnswerModel) {
        formAnswerModel.addAnswer(context, questionAnswerModel);
    }

    @Override
    public void updateAnswer(QuestionAnswerModel questionAnswerModel) {
        QuestionAnswerModel.updateInRealm(context, questionAnswerModel);
    }
}
