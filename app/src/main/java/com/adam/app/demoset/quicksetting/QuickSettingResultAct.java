/*
 * Copyright (c) 2026 Adam Chen
 *
 * Permission is hereby granted, free of charge, to any person obtaining a copy
 * of this software and associated documentation files (the "Software"), to deal
 * in the Software without restriction, including without limitation the rights
 * to use, copy, modify, merge, publish, distribute, sublicense, and/or sell
 * copies of the Software, and to permit persons to whom the Software is
 * furnished to do so, subject to the following conditions:
 *
 * The above copyright notice and this permission notice shall be included in all
 * copies or substantial portions of the Software.
 *
 * THE SOFTWARE IS PROVIDED "AS IS", WITHOUT WARRANTY OF ANY KIND, EXPRESS OR
 * IMPLIED, INCLUDING BUT NOT LIMITED TO THE WARRANTIES OF MERCHANTABILITY,
 * FITNESS FOR A PARTICULAR PURPOSE AND NONINFRINGEMENT. IN NO EVENT SHALL THE
 * AUTHORS OR COPYRIGHT HOLDERS BE LIABLE FOR ANY CLAIM, DAMAGES OR OTHER
 * LIABILITY, WHETHER IN AN ACTION OF CONTRACT, TORT OR OTHERWISE, ARISING FROM,
 * OUT OF OR IN CONNECTION WITH THE SOFTWARE OR THE USE OR OTHER DEALINGS IN THE
 * SOFTWARE.
 */

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
