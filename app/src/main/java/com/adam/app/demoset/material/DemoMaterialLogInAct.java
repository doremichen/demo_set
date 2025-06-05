package com.adam.app.demoset.material;

import android.os.Bundle;

import com.google.android.material.button.MaterialButton;
import com.google.android.material.progressindicator.CircularProgressIndicator;
import com.google.android.material.snackbar.Snackbar;
import com.google.android.material.textfield.TextInputEditText;
import androidx.appcompat.app.AppCompatActivity;
import androidx.appcompat.widget.Toolbar;
import androidx.coordinatorlayout.widget.CoordinatorLayout;

import android.os.Handler;
import android.text.TextUtils;
import android.view.View;
import android.widget.CheckBox;

import com.adam.app.demoset.R;
import com.adam.app.demoset.Utils;

import java.util.Objects;

public class DemoMaterialLogInAct extends AppCompatActivity {

    // default password
    private static final String DEFAULT_PASSWORD = "12345678";

    private static final String PREF_NAME = "login_pref";
    private static final String KEY_USERNAME = "username";
    private static final String KEY_PASSWORD = "password";
    private static final String KEY_REMEMBER = "remember_me";

    private TextInputEditText mName;
    private TextInputEditText mPassword;
    private CircularProgressIndicator mProgressBar;
    private CheckBox mRememberMeCheckBox;
    private MaterialButton mLoginButton;
    private CoordinatorLayout mCoordinatorLayout;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_demo_material_log_in);
        Toolbar toolbar = findViewById(R.id.toolbar);
        setSupportActionBar(toolbar);

        mName = findViewById(R.id.name_edit_text);
        mPassword = findViewById(R.id.password_edit_text);
        mProgressBar = findViewById(R.id.progress_bar);
        mRememberMeCheckBox = findViewById(R.id.remember_me);
        mLoginButton = findViewById(R.id.login_button);
        mCoordinatorLayout = findViewById(R.id.snackbar_container);

        mLoginButton.setOnClickListener(v -> onLoginButtonClick());

        // get remember me checkbox status from shared preference
        boolean rememberMe = getSharedPreferences(PREF_NAME, MODE_PRIVATE)
                .getBoolean(KEY_REMEMBER, false);
        if (rememberMe) {
            // set checkbox status
            mRememberMeCheckBox.setChecked(true);
            // set name and password from shared preference
            String name = getSharedPreferences(PREF_NAME, MODE_PRIVATE)
                    .getString(KEY_USERNAME, "");
            String password = getSharedPreferences(PREF_NAME, MODE_PRIVATE)
                    .getString(KEY_PASSWORD, "");
            mPassword.setText(password);
            mName.setText(name);
            mPassword.requestFocus();
        }
    }


    public void onExit(View view) {
        this.finish();
    }

    private void onLoginButtonClick() {

        // Get name and password
        String name = Objects.requireNonNull(mName.getText()).toString().trim();
        String password = Objects.requireNonNull(mPassword.getText()).toString().trim();

        if (!Utils.areAllNotNull(name, password)) {
            // show toast info user
            Utils.showToast(this, "Please input non-empty data");
            return;
        }

        if (name.isEmpty() || password.length() < 8) {
            // show toast info user
            Utils.showToast(this, "Please input valid data");
            return;
        }

        // start progressbar indicator
        this.mProgressBar.setVisibility(View.VISIBLE);
        this.mLoginButton.setEnabled(false);
        // Simulate login process
        Handler handler = new Handler();
        handler.postDelayed(new Runnable(){
            @Override
            public void run() {
                if (!TextUtils.equals(password, DEFAULT_PASSWORD)) {
                    // Show toast to info user
                    Utils.showToast(DemoMaterialLogInAct.this, "Password error...");
                    showSnackbar("Login Failed!!!");
                    return;
                }

                // Save remember me status if checked
                if (mRememberMeCheckBox.isChecked()) {
                    getSharedPreferences(PREF_NAME, MODE_PRIVATE)
                            .edit()
                            .putString(KEY_USERNAME, name)
                            .putString(KEY_PASSWORD, password)
                            .putBoolean(KEY_REMEMBER, true)
                            .apply();
                } else {
                    getSharedPreferences(PREF_NAME, MODE_PRIVATE)
                            .edit()
                            .remove(KEY_USERNAME)
                            .remove(KEY_PASSWORD)
                            .putBoolean(KEY_REMEMBER, false)
                            .apply();
                }


                Utils.showToast(DemoMaterialLogInAct.this, "Pass!!!");
                showSnackbar("Login Successful!!!");
                // Login success
                mProgressBar.setVisibility(View.INVISIBLE);
                mLoginButton.setEnabled(true);
                // Go to next activity


            }}, 2000L);

    }

    private void showSnackbar(String message) {
        Snackbar.make(mCoordinatorLayout, message, Snackbar.LENGTH_LONG).show();
    }
}
