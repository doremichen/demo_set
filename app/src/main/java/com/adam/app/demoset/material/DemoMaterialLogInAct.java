package com.adam.app.demoset.material;

import android.os.Bundle;
import com.google.android.material.textfield.TextInputEditText;
import androidx.appcompat.app.AppCompatActivity;
import androidx.appcompat.widget.Toolbar;

import android.text.TextUtils;
import android.view.View;

import com.adam.app.demoset.R;
import com.adam.app.demoset.Utils;

public class DemoMaterialLogInAct extends AppCompatActivity {

    // default password
    private static final String DEFAULT_PASSWORD = "12345678";

    private TextInputEditText mName;
    private TextInputEditText mPassword;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_demo_material_log_in);
        Toolbar toolbar = findViewById(R.id.toolbar);
        setSupportActionBar(toolbar);

        mName = findViewById(R.id.name_edit_text);
        mPassword = findViewById(R.id.password_edit_text);


    }

    public void onNext(View view) {
        // check input valid
        if (checkInputValid()) {
            Utils.showAlertDialog(this, "Not implemented next UI yet...", null);
        }

    }

    public void onExit(View view) {
        this.finish();
    }

    private boolean checkInputValid() {

        // Get name and password
        String name = mName.getText().toString();
        String password = mPassword.getText().toString();

        if (!Utils.areAllNotNull(name, password)) {
            // show toast info user
            Utils.showToast(this, "Please input non-empty data");
            return false;
        }

        if (name.length() == 0 || password.length() < 8) {
            // show toast info user
            Utils.showToast(this, "Please input valid data");
            return false;
        }

        if (!TextUtils.equals(password, DEFAULT_PASSWORD)) {
            // Show toast to info user
            Utils.showToast(this, "Password error...");
            return false;
        }

        Utils.showToast(this, "Pass!!!");
        return true;
    }
}
