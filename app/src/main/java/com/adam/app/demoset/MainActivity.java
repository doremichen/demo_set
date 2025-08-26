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

import androidx.activity.result.contract.ActivityResultContracts;
import androidx.appcompat.app.AppCompatActivity;

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

public class MainActivity extends AppCompatActivity {

    private static final String TAG = MainActivity.class.getSimpleName();

    private static final String LOG_STATUS = "log.status";


    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        Utils.info(this, "onCreate");
        setContentView(R.layout.activity_main);

        ListView listView = findViewById(R.id.list_view); // More descriptive variable name
        // empty text view
        View emptyView = findViewById(android.R.id.empty);
        // set empty view to listview
        listView.setEmptyView(emptyView);


        List<ItemContent> items = parseItemData(); // Extract parsing logic to a separate method
        if (items != null) {
            MainListAdapter adapter = new MainListAdapter(this, items); // Create adapter instance
            listView.setAdapter(adapter);

            listView.setOnItemClickListener((parent, view, position, id) -> {
                ItemContent data = (ItemContent) parent.getItemAtPosition(position);
                Utils.info(this, "the item: " + data.getTitle());

                // Go to the specified demo item
                Intent intent = new Intent(); // Rename 'it' to 'intent' for clarity
                intent.setClassName(data.getPkgName(), data.getClassName());
                startActivity(intent);
            });
        }

        requestNotificationPermission();

        //startEnableNotifySetting();
    }

    /**
     * This method requests notification permission for the app.
     */
    private void requestNotificationPermission() {
        if (Build.VERSION.SDK_INT >= Build.VERSION_CODES.TIRAMISU) {
            // notification post permission
            String[] permissions = {
                    Manifest.permission.POST_NOTIFICATIONS
            };
            // register for activity result
            registerForActivityResult(new ActivityResultContracts.RequestMultiplePermissions(),
                    result -> {
                        boolean allGranted = true;
                        for (Map.Entry<String, Boolean> entry : result.entrySet()) {
                            String permission = entry.getKey();
                            Boolean granted = entry.getValue();
                            if (Boolean.TRUE.equals(granted)) {
                                Utils.info(MainActivity.this, permission + " granted");
                            } else {
                                Utils.info(MainActivity.this, permission + " denied");
                                if (Manifest.permission.POST_NOTIFICATIONS.equals(permission)) {
                                    showPermissionExplanationDialog();
                                }
                                allGranted = false;
                            }
                        }

                        if (allGranted) {
                            // All permissions granted
                            Utils.showToast(MainActivity.this, "All permissions granted");
                        } else {
                            // Some permissions denied
                            Utils.showToast(MainActivity.this, "Some permissions denied");
                        }
                    }).launch(permissions);
        }
    }

    private void showPermissionExplanationDialog() {
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
     * @return a list of these objects or null if the some exception is occurred.
     */
    private List<ItemContent> parseItemData() {
        try (InputStream iStream = getResources().getAssets().open("itemDataRes.xml")) { // Use try-with-resources for automatic resource closure
            DocumentBuilderFactory factory = DocumentBuilderFactory.newInstance();
            DocumentBuilder builder = factory.newDocumentBuilder();
            Document document = builder.parse(iStream);

            Element root = document.getDocumentElement();
            NodeList nodes = root.getElementsByTagName("data");

            Resources res = getResources();
            //String packageName = getPackageName();

            Map<String, Integer> titleResMap = buildTitleResMap();
            return IntStream.range(0, nodes.getLength())
                    .mapToObj(nodes::item)
                    .filter(node -> node.getNodeType() == Node.ELEMENT_NODE)
                    .map(node -> (Element) node)
                    .map(itemData -> {
                        String titleResKey = itemData.getAttribute("titleRes");
                        int titleResId = titleResMap.getOrDefault(titleResKey, 0); //res.getIdentifier(titleResKey, "string", packageName);
                        String localizedTitle = titleResId != 0 ? res.getString(titleResId) : titleResKey;
                        return new ItemContent(
                                localizedTitle,
                                itemData.getAttribute("clsname"),
                                itemData.getAttribute("pkgname"));
                    })
                    .collect(Collectors.toList());

        } catch (IOException | ParserConfigurationException | SAXException e) {
            e.printStackTrace(); // Handle exceptions appropriately (e.g., log, display error message)
            return null; // Indicate parsing failure
        }
    }

    private static Map<String, Integer> buildTitleResMap() {
        Map<String, Integer> map = new HashMap<>();
        try {
            // Use reflection to get all the fields in R.string
            Field[] fields = R.string.class.getFields();
            for (Field field : fields) {
                String name = field.getName();
                if (name.startsWith("title_demo_")) {
                    int resId = field.getInt(null); // use null in static field
                    map.put(name, resId);
                }
            }
        } catch (IllegalAccessException e) {
            e.printStackTrace();
        }
        return map;
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
