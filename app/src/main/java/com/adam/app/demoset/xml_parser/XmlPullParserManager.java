/**
 * Provider the xml file parser service
 */
package com.adam.app.demoset.xml_parser;

import android.content.Context;
import android.util.Log;

import com.adam.app.demoset.R;

import org.xmlpull.v1.XmlPullParser;
import org.xmlpull.v1.XmlPullParserException;

import java.io.IOException;
import java.util.ArrayList;
import java.util.List;

public class XmlPullParserManager {

    private static final String TAG = XmlPullParserManager.class.getSimpleName();
    private Context mContext;
    private List<ItemData> mList = new ArrayList<>();
    private ItemData mData;
    private String mStrOfXml;

    private XmlPullParserManager() {
    }

    public static XmlPullParserManager newInstance() {
        return Singleton.INSTANCE;
    }

    private static void info(String msg) {
        Log.i(TAG, msg);
    }

    public static void dumpList(List<ItemData> list) {
        list.stream().forEach(item -> info(item.toString()));
    }

    /**
     * The context must be the activity or service
     * Otherwise the xml file parser will occur exception.
     *
     * @param context
     */
    public void init(Context context) {
        info("[init]");
        this.mContext = context;
    }

    /**
     * parse xml file and generate list data
     *
     * @return
     */
    public List<ItemData> parse() {
        info("[parse]");
        if (this.mContext == null) {
            info("No context!!!Please init first!!!");
            return this.mList;
        }

        // get xml pull parser from xml file.
        XmlPullParser xmlParser = this.mContext.getResources().getXml(R.xml.xml_demo);
        // parse content of xml file
        try {
            int eventType = xmlParser.getEventType();
            buildList(eventType, xmlParser);
        } catch (XmlPullParserException e) {
            e.printStackTrace();
        }
        dumpList(this.mList);
        return this.mList;
    }

    private void buildList(int eventType, XmlPullParser parser) {
        info("[buildList]");
        info("[buildList]");
        // preCheck
        if ((eventType == -1) || (parser == null)) {
            return;
        }

        while (eventType != XmlPullParser.END_DOCUMENT) {
            // get tag name
            String tagName = parser.getName();
            info("tagName: " + tagName);
            switch (eventType) {
                case XmlPullParser.START_TAG:
                    info("==== XmlPullParser.START_TAG ====");
                    if (XmItemContent.TAG.equalsIgnoreCase(tagName)) {
                        // new ItemData
                        mData = new ItemData();
                    }
                    break;
                case XmlPullParser.TEXT:
                    info("==== XmlPullParser.TEXT ====");
                    mStrOfXml = parser.getText();
                    info("mStrOfXml: " + mStrOfXml);
                    break;
                case XmlPullParser.END_TAG:
                    info("==== XmlPullParser.END_TAG ====");
                    // config data and add data to list
                    if (XmItemContent.ID.equalsIgnoreCase(tagName)) {
                        this.mData.setId(Integer.parseInt(this.mStrOfXml));
                    } else if (XmItemContent.NAME.equalsIgnoreCase(tagName)) {
                        this.mData.setName(this.mStrOfXml);
                    } else if (XmItemContent.TAG.equalsIgnoreCase(tagName)) {
                        // add data to list
                        this.mList.add(this.mData);
                    }
                    break;
                default:
                    break;
            }

            // next event type
            try {
                eventType = parser.next();
            } catch (IOException | XmlPullParserException e) {
                e.printStackTrace();
            }
        }
    }

    public void clearList() {
        this.mList.clear();
    }


    private interface XmItemContent {
        String TAG = "item";
        String ID = "id";
        String NAME = "name";
    }

    private static class Singleton {
        private static final XmlPullParserManager INSTANCE = new XmlPullParserManager();
    }

}
