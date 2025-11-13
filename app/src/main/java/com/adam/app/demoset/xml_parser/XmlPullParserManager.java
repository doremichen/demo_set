/**
 * Provider the xml file parser service
 */
package com.adam.app.demoset.xml_parser;

import android.content.Context;

import com.adam.app.demoset.R;
import com.adam.app.demoset.Utils;
import com.adam.app.demoset.xml_parser.model.ItemData;
import com.adam.app.demoset.xml_parser.pattern.XmlEventIterator;
import com.adam.app.demoset.xml_parser.pattern.XmlParsingState;
import com.adam.app.demoset.xml_parser.util.XmlParsingException;

import org.xmlpull.v1.XmlPullParser;

import java.util.ArrayList;
import java.util.List;

public class XmlPullParserManager {

    private static final String TAG = XmlPullParserManager.class.getSimpleName();
    private Context mContext;
    private List<ItemData> mList = new ArrayList<>();

    private XmlPullParserManager() {
    }

    public static XmlPullParserManager newInstance() {
        return Singleton.INSTANCE;
    }

    private static void info(String msg) {
        Utils.log(TAG, msg);
    }

    public static void dumpList(List<ItemData> list) {
        Utils.log(TAG, "Dump list:");
        Utils.log(TAG, "========================================");
        list.stream().forEach(item -> info(item.toString()));
        Utils.log(TAG, "========================================");
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

        // clear list
        this.mList.clear();


        try {
            // get xml pull parser from xml file.
            XmlPullParser xmlParser = this.mContext.getResources().getXml(R.xml.xml_demo);

            // build Xml state context
            XmlParsingState.StateContext stateContext = new XmlParsingState.StateContext(this.mList);
            // initial xml event iterator
            XmlEventIterator eventIterator = new XmlEventIterator(xmlParser);
            // foreach
            eventIterator.forEachRemaining(eventType -> {
                XmlParsingState currState = XmlParsingState.from(eventType);
                if (currState == null) {
                    currState = XmlParsingState.IGNORE;
                }
                currState.handle(stateContext, xmlParser);
            });

            stateContext.dumpList();

        } catch (XmlParsingException e) {
            Utils.error(this, "Failed to parse XML: " + e.getCause());
            e.printStackTrace();
        }

        dumpList(this.mList);
        return this.mList;
    }

    public void clearList() {
        this.mList.clear();
    }


    private static class Singleton {
        private static final XmlPullParserManager INSTANCE = new XmlPullParserManager();
    }

}
