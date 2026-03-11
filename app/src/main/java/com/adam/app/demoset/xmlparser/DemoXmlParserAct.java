/**
 * Copyright (C) 2020 Adam Chen Demp set project. All rights reserved.
 *<p>
 * Description: This is a demo of xml parser.
 *</p>
 *
 * Author: Adam Chen
 * Date: 2020/11/11
 */
package com.adam.app.demoset.xmlparser;

import androidx.appcompat.app.AppCompatActivity;

import android.os.Bundle;
import android.widget.ArrayAdapter;
import android.widget.ListView;

import com.adam.app.demoset.R;
import com.adam.app.demoset.Utils;
import com.adam.app.demoset.xmlparser.model.ItemData;

import java.util.List;

public class DemoXmlParserAct extends AppCompatActivity {

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        Utils.info(this, "[onCreate]");
        setContentView(R.layout.activity_demo_xml_parser);

        ListView listView = this.findViewById(R.id.listView1);
        // init
        XmlPullParserManager.newInstance().init(this.getApplicationContext());
        List<ItemData> dataSet = XmlPullParserManager.newInstance().parse();
        // build list view
        ArrayAdapter<ItemData> arrayAdapter = new ArrayAdapter<ItemData>(this, android.R.layout.simple_list_item_1, dataSet);
        //XmlPullParserManager.dumpList(dataSet);
        listView.setAdapter(arrayAdapter);

    }

    @Override
    protected void onDestroy() {
        super.onDestroy();
        XmlPullParserManager.newInstance().clearList();
    }
}