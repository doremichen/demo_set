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

package com.adam.app.demoset;

import android.Manifest;
import android.content.Intent;
import android.content.res.XmlResourceParser;
import android.os.Build;
import android.os.Bundle;
import android.provider.Settings;
import android.view.Menu;
import android.view.MenuItem;
import android.view.View;
import android.widget.AdapterView;
import android.widget.ListView;

import androidx.activity.OnBackPressedCallback;
import androidx.activity.result.contract.ActivityResultContracts;
import androidx.annotation.NonNull;
import androidx.annotation.RequiresApi;
import androidx.appcompat.app.AppCompatActivity;

import com.adam.app.demoset.databinding.ActivityMainBinding;
import com.adam.app.demoset.main.ItemContent;
import com.adam.app.demoset.utils.Utils;

import java.io.File;
import java.util.ArrayList;
import java.util.List;
import java.util.Map;

@RequiresApi(api = Build.VERSION_CODES.TIRAMISU)
public class MainActivity extends AppCompatActivity {

    private static final String LOG_STATUS = "log.status";
    private static final String[] PERMISSIONS = {
            Manifest.permission.POST_NOTIFICATIONS
    };
    private static final String STATE_SHOWING_CATEGORY = "showing_category";
    private static final String STATE_SELECTED_CATEGORY = "selected_category";

    // view binding
    private ActivityMainBinding mBinding;

    private List<ItemContent> mAllDemoList = new ArrayList<>();
    private List<ItemContent> mCurrentList = new ArrayList<>();
    private List<String> mCategoryList = new ArrayList<>();

    private boolean mShowingCategory = true;
    private String mSelectedCategory = "";

    // main adapter
    private MainListAdapter mAdapter;



    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        Utils.info(this, "onCreate");

        if (savedInstanceState != null) {
            mShowingCategory = savedInstanceState.getBoolean(STATE_SHOWING_CATEGORY, true);
            mSelectedCategory = savedInstanceState.getString(STATE_SELECTED_CATEGORY, "");
        }

        mBinding = ActivityMainBinding.inflate(getLayoutInflater());
        setContentView(mBinding.getRoot());

        ListView listView = mBinding.listView;
        View emptyView = mBinding.empty;
        listView.setEmptyView(emptyView);

        mAllDemoList = parseItemData();

        if (mAllDemoList != null) {
            buildCategoryList();
            if (mShowingCategory) {
                showCategoryList();
            } else {
                showDemoList(mSelectedCategory);
            }
            listView.setOnItemClickListener(this::onItemClick);
        }

        setupBackHandler();

        requestNotificationPermission();
    }

    @Override
    protected void onSaveInstanceState(@NonNull android.os.Bundle outState) {
        super.onSaveInstanceState(outState);
        outState.putBoolean(STATE_SHOWING_CATEGORY, mShowingCategory);
        outState.putString(STATE_SELECTED_CATEGORY, mSelectedCategory);
    }

    /**
     * This method is used to set on click event for the list view.
     *
     *
     * @param parent AdapterView
     * @param view View
     * @param position int
     * @param id long
     */
    private void onItemClick(AdapterView<?> parent, View view, int position, long id) {
        ItemContent item = (ItemContent) parent.getItemAtPosition(position);
        // show category
        if (mShowingCategory) {
            Utils.info(this, "enter category: " + item.getCategory());
            showDemoList(item.getCategory());
            return;
        }

        Utils.info(this, "open demo: " + item.getTitle());
        openDemo(item);

    }

    /**
     * build category list
     */
    private void buildCategoryList() {
        mCategoryList.clear();
        for (ItemContent item : mAllDemoList) {
            String category = item.getCategory();
            if (!mCategoryList.contains(category)) {
                mCategoryList.add(category);
            }
        }
    }

    /**
     * showCategoryList
     */
    private void showCategoryList() {
        mShowingCategory = true;
        mSelectedCategory = "";
        mCurrentList.clear();

        for (String cat: mCategoryList) {
            mCurrentList.add(new ItemContent(cat, cat.toUpperCase(), "", ""));
        }

        // adapter
        mAdapter = new MainListAdapter(this, mCurrentList);
        mBinding.listView.setAdapter(mAdapter);

    }


    /**
     * showDemoList
     *
     * @param category String
     */
    private void showDemoList(String category) {
        mShowingCategory = false;
        mSelectedCategory = category;

        mCurrentList.clear();

        for (ItemContent item : mAllDemoList) {
            if (item.getCategory().equals(category)) {
                mCurrentList.add(item);
            }
        }

        // adapter
        mAdapter = new MainListAdapter(this, mCurrentList);
        mBinding.listView.setAdapter(mAdapter);

    }


    /**
     * openDemo
     *
     * @param item ItemContent
     */
    private void openDemo(ItemContent item) {
        try {

            Intent intent = new Intent();

            intent.setClassName(item.getPkgname(), item.getClsname());

            startActivity(intent);

        } catch (Exception e) {

            e.printStackTrace();

            Utils.showToast(this, "Demo launch failed");

        }
    }

    /**
     * setupBackHandler
     */
    private void setupBackHandler() {
        getOnBackPressedDispatcher().addCallback(this,
                new OnBackPressedCallback(true) {
                    @Override
                    public void handleOnBackPressed() {
                        if (mShowingCategory) {
                            Utils.info(MainActivity.this, "exit app");
                            setEnabled(false);
                            getOnBackPressedDispatcher().onBackPressed();
                            return;
                        }
                        Utils.info(MainActivity.this, "back to category");
                        showCategoryList();
                    }
                });
    }


    /**
     * This method requests notification permission for the app.
     */
    private void requestNotificationPermission() {
        // register for activity result
        registerForActivityResult(new ActivityResultContracts.RequestMultiplePermissions(),
                this::handlePermissionResult).launch(PERMISSIONS);
    }

    /**
     * This method handles the result of the permission request.
     *
     * @param result a map of permissions and their granted status.
     */
    private void handlePermissionResult(Map<String, Boolean> result) {
        boolean allGranted = true;
        for (Map.Entry<String, Boolean> entry : result.entrySet()) {
            String permission = entry.getKey();
            Boolean granted = entry.getValue();

            if (Boolean.TRUE.equals(granted)) {
                Utils.info(MainActivity.this, permission + " granted");
            } else {
                Utils.info(MainActivity.this, permission + " denied");
                if (Manifest.permission.POST_NOTIFICATIONS.equals(permission)) {
                    showNotifyDialog();
                }
                allGranted = false;
            }
        }

        String msg = allGranted ? getString(R.string.demo_all_permissions_granted_msg)
                : getString(R.string.demo_some_permissions_denied_msg);
        Utils.showToast(MainActivity.this, msg);

    }

    /**
     * This method shows a dialog to the user to enable notification permission.
     */
    private void showNotifyDialog() {
        // post DialogButton
        Utils.DialogButton okBtn = new Utils.DialogButton(getString(R.string.label_setting_btn),
                (dialog, which) -> startEnableNotifySetting());
        Utils.showAlertDialog(this, R.string.label_notification_permission_label,
                R.string.label_notification_permission_description, okBtn);

    }

    /**
     * This method reads an XML file from the res/xml folder,
     * parses it using a XmlResourceParser which is pre-compiled and more efficient.
     *
     * @return a list of these objects or null if the some exception is occurred.
     */
    private List<ItemContent> parseItemData() {
        List<ItemContent> list = new ArrayList<>();
        try (XmlResourceParser parser = getResources().getXml(R.xml.item_data_res)) {
            int eventType = parser.getEventType();
            while (eventType != XmlResourceParser.END_DOCUMENT) {
                if (eventType == XmlResourceParser.START_TAG && "data".equals(parser.getName())) {
                    // Get resource IDs directly from compiled XML
                    int catResId = parser.getAttributeResourceValue("http://schemas.android.com/apk/res-auto", "category", 0);
                    int titleResId = parser.getAttributeResourceValue("http://schemas.android.com/apk/res-auto", "titleRes", 0);

                    String category = (catResId != 0) ? getString(catResId) : "";
                    String localizedTitle = (titleResId != 0) ? getString(titleResId) : "";
                    String clsname = parser.getAttributeValue("http://schemas.android.com/apk/res-auto", "clsname");
                    String pkgname = parser.getAttributeValue("http://schemas.android.com/apk/res-auto", "pkgname");

                    list.add(new ItemContent(category, localizedTitle, clsname, pkgname));
                }
                eventType = parser.next();
            }
        } catch (Exception e) {
            Utils.info(this, "parseItemData error: " + e.getMessage());
            return null;
        }
        return list;
    }




    @Override
    public boolean onCreateOptionsMenu(Menu menu) {
        this.getMenuInflater().inflate(R.menu.action_main_menu_list, menu);

        return true;
    }


    @Override
    public boolean onOptionsItemSelected(MenuItem item) {
        MenuAction action = MenuAction.fromId(item.getItemId());
        if (action != null) {
            // log item
            Utils.info(this, "onOptionsItemSelected: " + item.getTitle());
            action.execute(this);
            return true;
        }
        return super.onOptionsItemSelected(item);
    }

    /**
     * Enum implementing Strategy pattern for Menu Actions
     */
    private enum MenuAction {
        START_LOG(R.id.start_log) {
            @Override
            void execute(MainActivity activity) {
                activity.enableLogcat(Utils.TRUE);
            }
        },
        STOP_LOG(R.id.stop_log) {
            @Override
            void execute(MainActivity activity) {
                activity.enableLogcat(Utils.FALSE);
            }
        },
        EXIT(R.id.exit) {
            @Override
            void execute(MainActivity activity) {
                activity.finish();
            }
        };

        private final int menuId;

        MenuAction(int menuId) {
            this.menuId = menuId;
        }

        abstract void execute(MainActivity activity);

        static MenuAction fromId(int id) {
            for (MenuAction action : values()) {
                if (action.menuId == id) {
                    return action;
                }
            }
            return null;
        }
    }


    /**
     * This method aims to open the notification settings for the current app.
     * It handles the differences in how to launch this setting screen between
     * Android versions before and after Oreo (8.0).
     */
    private void startEnableNotifySetting() {
        Intent intent = new Intent();
        intent.addFlags(Intent.FLAG_ACTIVITY_NEW_TASK);
        if (Build.VERSION.SDK_INT >= Build.VERSION_CODES.O) {
            intent.setAction(Settings.ACTION_APP_NOTIFICATION_SETTINGS);
            intent.putExtra(Settings.EXTRA_APP_PACKAGE, getPackageName());
        } else {
            intent.setAction("android.settings.ACTION_APP_NOTIFICATION_SETTINGS");
            intent.putExtra("app_package", getPackageName());
            intent.putExtra("app_uid", getApplicationInfo().uid);
        }
        startActivity(intent);
    }

    private void enableLogcat(String enable) {
        Utils.info(this, "enableLogcat enter");
        String logStatus = System.getProperty(LOG_STATUS);
        if (logStatus != null && logStatus.equals(enable)) {
            Utils.showToast(this, "The log status has been " +
                    (Utils.TRUE.equals(enable) ? "enabled" : "disabled"));
            return;
        }

        System.setProperty(LOG_STATUS, enable);


        File fileDir = this.getFilesDir();
        String filePath = fileDir.getPath() + "/" + System.currentTimeMillis() + ".log";
        Utils.info(this, "log path: " + filePath);
        Utils.enableLog(enable, filePath);

    }
}
