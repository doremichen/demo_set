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

package com.adam.app.demoset.xmlparser;

import android.content.Context;

import com.adam.app.demoset.R;
import com.adam.app.demoset.utils.Utils;
import com.adam.app.demoset.xmlparser.model.ItemData;
import com.adam.app.demoset.xmlparser.pattern.XmlEventIterator;
import com.adam.app.demoset.xmlparser.pattern.XmlParsingState;
import com.adam.app.demoset.xmlparser.util.XmlParsingException;

import org.xmlpull.v1.XmlPullParser;

import java.util.ArrayList;
import java.util.List;

public class XmlPullParserManager {

    private static final String TAG = XmlPullParserManager.class.getSimpleName();
    private Context mContext;
    private List<ItemData> mList = new ArrayList<>();
    private XmlPullParserListener mListener;

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
     * set xml parser listener
     *
     * @param listener
     */
    public void setListener(XmlPullParserListener listener) {
        mListener = listener;
    }

    private void addLog(String msg) {
        if (mListener != null) {
            mListener.onMessage(msg);
        }
    }

    /**
     * The context must be the activity or service
     * Otherwise the xml file parser will occur exception.
     *
     * @param context
     */
    public void init(Context context) {
        info("[init]");
        addLog("[init]");
        this.mContext = context;
    }

    /**
     * parse xml file and generate list data
     *
     * @return
     */
    public List<ItemData> parse() {
        info("[parse]");
        addLog("[parse]");
        if (this.mContext == null) {
            info("No context!!!Please init first!!!");
            addLog("No context!!!Please init first!!!");
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
                addLog("event type: " + eventType);
                XmlParsingState currState = XmlParsingState.from(eventType);
                if (currState == null) {
                    currState = XmlParsingState.IGNORE;
                }
                addLog("state: " + currState);
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
        addLog("[clearList]");
        this.mList.clear();
    }

    public interface XmlPullParserListener {
        void onMessage(String msg);
    }

    private static class Singleton {
        private static final XmlPullParserManager INSTANCE = new XmlPullParserManager();
    }

}
