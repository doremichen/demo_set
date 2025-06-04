/**
 * Copyright (C) 2018 Adam. All rights reserved.
 *
 */
package com.adam.app.demoset.tablelayout;

import android.os.Bundle;
import androidx.appcompat.app.AppCompatActivity;
import android.view.View;
import android.widget.Button;

import com.adam.app.demoset.R;
import com.adam.app.demoset.Utils;

public class DemoTableAct extends AppCompatActivity {

    private boolean mIsCircle;
    // button array size 9
    private final Button[] mButtons = new Button[9];

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_demo_table);

        // initial buttons and set listener
        initButtons();


    }

    /**
     * Init buttons
     */
    private void initButtons() {
        Utils.info(this, "initButtons enter");
        // loop
        for (int i = 0; i < mButtons.length; i++) {
            String btnId = "btnItem" + (i + 1);
            int id = getResources().getIdentifier(btnId, "id", getPackageName());
            mButtons[i] = findViewById(id);
            // set click listener
            mButtons[i].setOnClickListener(new View.OnClickListener() {
                @Override
                public void onClick(View v) {
                    Utils.info(DemoTableAct.this, "onClick enter");
                    // get button
                    Button btn = (Button) v;
                    // update button text
                    btn.setText(updateInfo());
                }
            });
        }

    }


    /**
     * Exit UI
     */
    public void Exit(View v) {
        Utils.info(this, "Exit enter");
        this.finish();
    }

    /**
     * Reset UI
     * @param v
     */
    public void Reset(View v) {
        Utils.info(this, "Reset enter");
        resetButtons();
    }

    private String updateInfo() {
        Utils.info(this, "updateInfo enter");

        mIsCircle = !mIsCircle;
        return mIsCircle ? "X" : "O";
    }

    /**
     * Reset buttons
     */
    private void resetButtons() {
        Utils.info(this, "Reset enter");
        // foreach loop
        for (Button btn : mButtons) {
            btn.setText("");
        }
    }
}
