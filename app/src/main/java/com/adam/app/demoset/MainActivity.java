/**
 * The main UI of the Demo set app
 * <p>
 * info:
 *
 * @author: AdamChen
 * @date: 2018/9/19
 */

package com.adam.app.demoset;

import android.content.Intent;
import android.os.Bundle;
import android.view.Menu;
import android.view.MenuItem;
import android.view.View;
import android.widget.AdapterView;
import android.widget.ListView;

import androidx.appcompat.app.AppCompatActivity;

import org.w3c.dom.Document;
import org.w3c.dom.Element;
import org.w3c.dom.NodeList;
import org.xml.sax.SAXException;

import java.io.File;
import java.io.IOException;
import java.io.InputStream;
import java.util.ArrayList;
import java.util.List;

import javax.xml.parsers.DocumentBuilder;
import javax.xml.parsers.DocumentBuilderFactory;
import javax.xml.parsers.ParserConfigurationException;

public class MainActivity extends AppCompatActivity {

    private static final String TAG = MainActivity.class.getSimpleName();

    private static final String LOG_STATUS = "log.status";
    private ListView mList;


    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        Utils.info(this, "onCreate");
        setContentView(R.layout.activity_main);

        this.mList = this.findViewById(R.id.list_view);

        // prepare item list
        List<ItemContent> itemDatas = new ArrayList<>();

        // parse config xml file to general item data
        try {
            // Open xml file
            InputStream iStream = this.getResources().getAssets().open("itemData.xml");

            DocumentBuilderFactory factory = DocumentBuilderFactory.newInstance();
            DocumentBuilder builder = factory.newDocumentBuilder();
            Document document = builder.parse(iStream);

            Element root = document.getDocumentElement();
            NodeList nodes = root.getElementsByTagName("data");

            final int length = nodes.getLength();
            for (int i = 0; i < length; i++) {
                Element itemData = (Element) nodes.item(i);
                ItemContent dataContent = new ItemContent(itemData.getAttribute("title"),
                        itemData.getAttribute("clsname"), itemData.getAttribute("pkgname"));
                itemDatas.add(dataContent);
            }

        } catch (IOException e) {
            e.printStackTrace();
        } catch (ParserConfigurationException e) {
            e.printStackTrace();
        } catch (SAXException e) {
            e.printStackTrace();
        }

        // Set list adapter
        this.mList.setAdapter(new MainListAdapter(this, itemDatas));

        mList.setOnItemClickListener(new AdapterView.OnItemClickListener() {
            @Override
            public void onItemClick(AdapterView<?> parent, View view, int position, long id) {
                ItemContent data = (ItemContent) parent.getItemAtPosition(position);
                Utils.info(this, "the item: " + data.getTitle());

                // Go to the specified demo item
                Intent it = new Intent();
                it.setClassName(data.getPkgName(), data.getClassName());
                MainActivity.this.startActivity(it);

            }
        });

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


    private void enableLogcat(String enable) {
        Utils.info(this, "enableLogcat enter");
        String logstatus = System.getProperty(LOG_STATUS);
        if (logstatus != null && logstatus.equals(enable)) {
            Utils.showToast(this, "The log status has " +
                    (Utils.TRUE.equals(enable) ? "enable" : "disable"));
            return;
        }

        System.setProperty(LOG_STATUS, enable);


        File fileDir = this.getFilesDir();
        String filePath = fileDir.getPath() + "/" + System.currentTimeMillis() + ".log";

        Utils.enableLog(enable, filePath);

    }
}
