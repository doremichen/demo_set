/**
 * The main UI of the Demo set app
 * <p>
 * info: This class is the main UI of the Demo set app.
 *
 * @author: AdamChen
 * @date: 2018/9/19
 */
package com.adam.app.demoset;

import android.Manifest;
import android.content.Intent;
import android.content.res.Resources;
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
import androidx.annotation.RequiresApi;
import androidx.appcompat.app.AppCompatActivity;

import com.adam.app.demoset.databinding.ActivityMainBinding;

import org.w3c.dom.Document;
import org.w3c.dom.Element;
import org.w3c.dom.Node;
import org.w3c.dom.NodeList;
import org.xml.sax.SAXException;

import java.io.File;
import java.io.IOException;
import java.io.InputStream;
import java.lang.reflect.Field;
import java.util.ArrayList;
import java.util.HashMap;
import java.util.List;
import java.util.Map;
import java.util.stream.Collectors;
import java.util.stream.IntStream;

import javax.xml.parsers.DocumentBuilder;
import javax.xml.parsers.DocumentBuilderFactory;
import javax.xml.parsers.ParserConfigurationException;

@RequiresApi(api = Build.VERSION_CODES.TIRAMISU)
public class MainActivity extends AppCompatActivity {

    private static final String TAG = MainActivity.class.getSimpleName();

    private static final String LOG_STATUS = "log.status";
    private static final String[] PERMISSIONS = {
            Manifest.permission.POST_NOTIFICATIONS
    };
    // view binding
    private ActivityMainBinding mBinding;

    private List<ItemContent> mAllDemoList = new ArrayList<>();
    private List<ItemContent> mCurrentList = new ArrayList<>();
    private List<String> mCategoryList = new ArrayList<>();

    private boolean mShowingCategory = true;

    // main adapter
    private MainListAdapter mAdapter;


    private static Map<String, Integer> buildItemResMap(String prefix) {
        Map<String, Integer> map = new HashMap<>();
        try {
            // Use reflection to get all the fields in R.string
            Field[] fields = R.string.class.getFields();
            for (Field field : fields) {
                String name = field.getName();
                if (name.startsWith(prefix)) {
                    int resId = field.getInt(null); // use null in static field
                    map.put(name, resId);
                }
            }
        } catch (IllegalAccessException e) {
            Utils.info(MainActivity.class, "buildItemResMap error");
            e.printStackTrace();
        }
        return map;
    }

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        Utils.info(this, "onCreate");

        mBinding = ActivityMainBinding.inflate(getLayoutInflater());
        setContentView(mBinding.getRoot());

        ListView listView = mBinding.listView;
        View emptyView = mBinding.empty;
        listView.setEmptyView(emptyView);

        mAllDemoList = parseItemData();

        if (mAllDemoList != null) {
            buildCategoryList();
            showCategoryList();
            listView.setOnItemClickListener(this::onItemClick);
        }

        setupBackHandler();

        requestNotificationPermission();
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
     *
     */
    private void buildCategoryList() {
      mCategoryList.clear();

      for (ItemContent item : mAllDemoList) {
          if (!mCategoryList.contains(item.getCategory())) {
              mCategoryList.add(item.getCategory());
          }
      }
    }

    /**
     * showCategoryList
     */
    private void showCategoryList() {
        mShowingCategory = true;
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
     * This method reads an XML file ("itemData.xml") from the assets folder,
     * parses it using a DOM parser, extracts relevant attributes
     * from elements tagged "data", creates ItemContent objects representing
     * this data
     *
     * @return a list of these objects or null if the some exception is occurred.
     */
    private List<ItemContent> parseItemData() {
        try (InputStream iStream = getResources().getAssets().open("itemDataRes.xml")) { // Use try-with-resources for automatic resource closure
            DocumentBuilderFactory factory = DocumentBuilderFactory.newInstance();
            DocumentBuilder builder = factory.newDocumentBuilder();
            Document document = builder.parse(iStream);

            Element root = document.getDocumentElement();
            NodeList nodes = root.getElementsByTagName("data");


            Map<String, Integer> categoryResMap = buildItemResMap("category_");
            Map<String, Integer> titleResMap = buildItemResMap("title_demo_");
            return IntStream.range(0, nodes.getLength())
                    .mapToObj(nodes::item)
                    .filter(node -> node.getNodeType() == Node.ELEMENT_NODE)
                    .map(node -> (Element) node)
                    .map(itemData -> {
                        String category = getString(itemData, "category_", "category");
                        String localizedTitle = getString(itemData, "title_demo_", "titleRes");
                        return new ItemContent(
                                category,
                                localizedTitle,
                                itemData.getAttribute("clsname"),
                                itemData.getAttribute("pkgname")
                        );

                    })

                    .collect(Collectors.toList());

        } catch (IOException | ParserConfigurationException | SAXException e) {
            e.printStackTrace(); // Handle exceptions appropriately (e.g., log, display error message)
            return null; // Indicate parsing failure
        }
    }


    private String getString(Element itemData, String prefix, String attribute) {

        Resources res = getResources();

        Map<String, Integer> resMap = buildItemResMap(prefix);
        String attri = itemData.getAttribute(attribute);
        int resId = resMap.getOrDefault(attri, 0);
        return resId != 0 ? res.getString(resId) : attri;

    }


    @Override
    public boolean onCreateOptionsMenu(Menu menu) {
        this.getMenuInflater().inflate(R.menu.action_main_menu_list, menu);

        return true;
    }


    @Override
    public boolean onOptionsItemSelected(MenuItem item) {
        switch (item.getItemId()) {
            case R.id.start_log:
                enableLogcat(Utils.TRUE);
                break;
            case R.id.stop_log:
                enableLogcat(Utils.FALSE);
                break;
            case R.id.exit:
                Utils.info(this, "press exit item!!!");
                finish();
        }

        return false;
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
