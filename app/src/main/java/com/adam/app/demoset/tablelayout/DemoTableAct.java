package com.adam.app.demoset.tablelayout;

import android.support.v7.app.AppCompatActivity;
import android.os.Bundle;
import android.view.View;
import android.widget.Button;

import com.adam.app.demoset.R;
import com.adam.app.demoset.Utils;

public class DemoTableAct extends AppCompatActivity {

    private boolean mIsCircle;
    private Button mBtn1;
    private Button mBtn2;
    private Button mBtn3;
    private Button mBtn4;
    private Button mBtn5;
    private Button mBtn6;
    private Button mBtn7;
    private Button mBtn8;
    private Button mBtn9;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_demo_table);
        mBtn1 = this.findViewById(R.id.btnItem1);
        mBtn2 = this.findViewById(R.id.btnItem2);
        mBtn3 = this.findViewById(R.id.btnItem3);
        mBtn4 = this.findViewById(R.id.btnItem4);
        mBtn5 = this.findViewById(R.id.btnItem5);
        mBtn6 = this.findViewById(R.id.btnItem6);
        mBtn7 = this.findViewById(R.id.btnItem7);
        mBtn8 = this.findViewById(R.id.btnItem8);
        mBtn9 = this.findViewById(R.id.btnItem9);

    }

    public void Button1(View v) {
        Utils.inFo(this, "Button1 enter");
        mBtn1.setText(updateInfo());

    }

    public void Button2(View v) {
        Utils.inFo(this, "Button2 enter");
        mBtn2.setText(updateInfo());

    }

    public void Button3(View v) {
        Utils.inFo(this, "Button3 enter");
        mBtn3.setText(updateInfo());

    }

    public void Button4(View v) {
        Utils.inFo(this, "Button4 enter");
        mBtn4.setText(updateInfo());

    }

    public void Button5(View v) {
        Utils.inFo(this, "Button5 enter");
        mBtn5.setText(updateInfo());

    }

    public void Button6(View v) {
        Utils.inFo(this, "Button6 enter");
        mBtn6.setText(updateInfo());

    }

    public void Button7(View v) {
        Utils.inFo(this, "Button7 enter");
        mBtn7.setText(updateInfo());

    }

    public void Button8(View v) {
        Utils.inFo(this, "Button8 enter");
        mBtn8.setText(updateInfo());

    }

    public void Button9(View v) {
        Utils.inFo(this, "Button9 enter");
        mBtn9.setText(updateInfo());

    }

    /**
     * Exit UI
     */
    public void Exit(View v) {
        Utils.inFo(this, "Exit enter");
        this.finish();
    }

    public void Reset(View v) {
        Utils.inFo(this, "Reset enter");
        resetButton();
    }

    private String updateInfo() {
        Utils.inFo(this, "updateInfo enter");

        if (mIsCircle) {
            mIsCircle = false;
            return "O";
        } else {
            mIsCircle = true;
            return "X";
        }
    }

    private void resetButton() {
        Utils.inFo(this, "Reset enter");
        mBtn1.setText("");
        mBtn2.setText("");
        mBtn3.setText("");
        mBtn4.setText("");
        mBtn5.setText("");
        mBtn6.setText("");
        mBtn7.setText("");
        mBtn8.setText("");
        mBtn9.setText("");
    }
}
