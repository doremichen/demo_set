package com.adam.app.demoset.material;

import android.os.Bundle;
import android.view.Menu;
import android.view.MenuItem;

import androidx.activity.EdgeToEdge;
import androidx.appcompat.app.AppCompatActivity;
import androidx.core.app.NavUtils;
import androidx.core.graphics.Insets;
import androidx.core.view.ViewCompat;
import androidx.core.view.WindowCompat;
import androidx.core.view.WindowInsetsCompat;

import com.adam.app.demoset.R;
import com.adam.app.demoset.Utils;
import com.google.android.material.appbar.MaterialToolbar;
import com.google.android.material.button.MaterialButton;
import com.google.android.material.textview.MaterialTextView;

import java.util.Objects;

public class Next1Activity extends AppCompatActivity {

    // TAG
    private static final String TAG = Next1Activity.class.getSimpleName();

    private MaterialToolbar mTopAppBar;
    private MaterialTextView mTextView;
    private MaterialButton mBtnFinish;


    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_next1);

        mTopAppBar = findViewById(R.id.next_topAppBar);
        mTextView = findViewById(R.id.tv_login_hello_next);
        mBtnFinish = findViewById(R.id.btn_finsh);

        setSupportActionBar(mTopAppBar); //need to use onCreateOptionsMenu handle menu。
        // get support action bar to enable back button
        Objects.requireNonNull(getSupportActionBar()).setDisplayHomeAsUpEnabled(true);

        mTopAppBar.setNavigationOnClickListener(v -> {
            // Handle navigation icon press
            Utils.info(Next1Activity.class, "navigation icon pressed");
            showInfo("navigation icon");
            // navigate to previous activity
            NavUtils.navigateUpFromSameTask(Next1Activity.this);
        });

        mTopAppBar.setOnMenuItemClickListener(this::onMenuItemClick);

        mBtnFinish.setOnClickListener(v -> {
            Utils.info(Next1Activity.class, "finish button pressed");
            showInfo("finish button");
            // show finish toast
            Utils.showToast(this, "finish");

        });
    }

    // override menu inflate
    @Override
    public boolean onCreateOptionsMenu(Menu menu) {
        getMenuInflater().inflate(R.menu.top_app_bar, menu);
        return true;
    }


    private boolean onMenuItemClick(MenuItem menuItem) {
        Utils.info(Next1Activity.class, "menu item pressed");
        // Log menu item title
        Utils.info(Next1Activity.class, "item: " + menuItem.getTitle().toString() + "is clicked!!!");
        // show text view
        showInfo(menuItem.getTitle().toString());
        return true;
    }

    private void showInfo(String info) {
        StringBuilder stb = new StringBuilder();
        stb.append("item: ").append(info).append(" is clicked!!!");
        mTextView.setText(stb.toString());
    }
}