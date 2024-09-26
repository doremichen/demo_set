package com.adam.app.demoset.quicksetting;

import android.os.Bundle;
import androidx.appcompat.app.AppCompatActivity;
import android.widget.TextView;

import com.adam.app.demoset.R;

public class QuickSettingResultAct extends AppCompatActivity {

    private TextView mResult;

    public static final String KEY_RESULT_SETTING_TITLE = "key.result.title";
    public static final String KEY_RESULT_SETTING_STATE = "key.result.state";


    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_quick_setting_result);

        mResult = findViewById(R.id.QsResult);

        String qsTitle = getIntent().getStringExtra(KEY_RESULT_SETTING_TITLE);
        String qsMessage = getIntent().getStringExtra(KEY_RESULT_SETTING_STATE);
        StringBuilder sb = new StringBuilder();
        sb.append(qsTitle).append("\n");
        sb.append(qsMessage).append("\n");

        mResult.setText(sb.toString());

    }
}
