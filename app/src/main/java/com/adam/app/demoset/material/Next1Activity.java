package com.adam.app.demoset.material;

import android.os.Bundle;
import android.view.Menu;
import android.view.MenuItem;
import android.widget.Button;

import androidx.appcompat.app.AppCompatActivity;
import androidx.core.app.NavUtils;

import com.adam.app.demoset.R;
import com.adam.app.demoset.Utils;
import com.google.android.material.appbar.MaterialToolbar;
import com.google.android.material.button.MaterialButton;
import com.google.android.material.button.MaterialButtonToggleGroup;
import com.google.android.material.textview.MaterialTextView;

import java.util.Objects;

public class Next1Activity extends AppCompatActivity {

    // TAG
    private static final String TAG = Next1Activity.class.getSimpleName();

    private MaterialToolbar mTopAppBar;
    private MaterialTextView mTextView;
    private MaterialButton mBtnFinish;
    private Button mBtnTextButton;
    private Button mBtnOutlinedButton;
    // button toggle group
    private MaterialButtonToggleGroup mBtnToggleGroup;


    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_next1);

        mTopAppBar = findViewById(R.id.next_topAppBar);
        mTextView = findViewById(R.id.tv_login_hello_next);
        mBtnFinish = findViewById(R.id.btn_login_contained_button);
        mBtnTextButton = findViewById(R.id.btn_text_button);
        mBtnOutlinedButton = findViewById(R.id.btn_outlined_button);
        mBtnToggleGroup = findViewById(R.id.btn_toggle_group);

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
            Utils.info(Next1Activity.class, "Contained button pressed");
            showInfo("Contained button");
            // show finish toast
            Utils.showToast(this, "Contained button");

        });

        mBtnTextButton.setOnClickListener(v -> {
            Utils.info(Next1Activity.class, "text button pressed");
            showInfo("text button");
            // show finish toast
            Utils.showToast(this, "text button");
        });

        mBtnOutlinedButton.setOnClickListener(v -> {
           Utils.info(Next1Activity.class, "outlined button pressed");
           showInfo("outlined button");
           // show finish toast
           Utils.showToast(this, "outlined button");
        });

        mBtnToggleGroup.addOnButtonCheckedListener((group, checkedId, isChecked) -> {
            // log
            Utils.info(Next1Activity.class, "button toggle group pressed");
            // log with button title
            Utils.info(Next1Activity.class, "button title: "
                    + group.findViewById(checkedId).getContentDescription()
                    + " isChecked: " + isChecked);
            if (isChecked) {
                // show text view
                showInfo("button toggle group: " + group.findViewById(checkedId).getContentDescription());
                // show finish toast
                Utils.showToast(this, "button toggle group");
            }

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