/**
 * This class is used to xml parse state
 *
 * @author Adam Chen
 * @version 1.0
 * @since 2025-11-13
 */
package com.adam.app.demoset.xml_parser.pattern;

import com.adam.app.demoset.Utils;
import com.adam.app.demoset.xml_parser.model.ItemData;
import com.adam.app.demoset.xml_parser.util.XmlItemContent;

import org.xmlpull.v1.XmlPullParser;

import java.util.HashMap;
import java.util.List;
import java.util.Map;

public enum XmlParsingState {

    START_TAG {
        @Override
        public void handle(StateContext stateContext, XmlPullParser parser) {
            Utils.log(TAG, "start tag");
            String tagName = parser.getName();
            // check tag name
            if (XmlItemContent.TAG.equalsIgnoreCase(tagName)) {
                stateContext.setCurrentItem(new ItemData());
            }
        }
    },
    END_TAG {
        @Override
        public void handle(StateContext stateContext, XmlPullParser parser) {
            Utils.log(TAG, "end tag");
            String tagName = parser.getName();
            ItemData currItemData = stateContext.getCurrentItem();
            String currText = stateContext.getCurrentText();
            if (currItemData != null && currText != null) {
                // set id
                if (XmlItemContent.ID.equalsIgnoreCase(tagName)) {
                    currItemData.setId(Integer.parseInt(currText));
                } else if (XmlItemContent.NAME.equalsIgnoreCase(tagName)) { // set name
                    currItemData.setName(currText);
                }
            }
            // add item to list
            if (XmlItemContent.TAG.equalsIgnoreCase(tagName) && currItemData != null) {
                stateContext.getList().add(currItemData);
                // clear data
                stateContext.setCurrentItem(null);
            }

            stateContext.setCurrentText(null);
        }
    },
    TEXT {
        @Override
        public void handle(StateContext stateContext, XmlPullParser parser) {
            Utils.log(TAG, "text");
            String text = parser.getText();
            if (text != null && !text.trim().isEmpty()) {
                stateContext.setCurrentText(text.trim());
            }
        }
    },
    IGNORE {
        @Override
        public void handle(StateContext stateContext, XmlPullParser parser) {
            Utils.log(TAG, "ignore");
        }
    };

    // TAG
    private static final String TAG = "XmlParsingState";

    //Map: XmlPullEvent -> State
    private static final Map<Integer, XmlParsingState> mStateMap = new HashMap<>() {
        {
            put(XmlPullParser.START_TAG, START_TAG);
            put(XmlPullParser.END_TAG, END_TAG);
            put(XmlPullParser.TEXT, TEXT);
        }
    };

    /**
     * from
     *   get state from xml pull event
     *
     * @param eventType xml pull event
     * @return state
     */
    public static XmlParsingState from(int eventType) {
        return mStateMap.get(eventType);
    }

    /**
     * handle
     *   handle content in xml file
     * @param stateContext
     * @param parser
     */
    public abstract void handle(StateContext stateContext, XmlPullParser parser);

    /**
     * state Context
     */
    public static class StateContext {
        private final List<ItemData> mList;
        private ItemData mCurrentItem;
        private String mCurrentText;

        public StateContext(List<ItemData> mList) {
            this.mList = mList;
        }

        public ItemData getCurrentItem() {
            return mCurrentItem;
        }

        public void setCurrentItem(ItemData mCurrentItem) {
            this.mCurrentItem = mCurrentItem;
        }

        public String getCurrentText() {
            return mCurrentText;
        }

        public void setCurrentText(String mCurrentText) {
            this.mCurrentText = mCurrentText;
        }

        public List<ItemData> getList() {
            return mList;
        }

        public void dumpList() {
            Utils.log(TAG, "dump list");
            Utils.log(TAG, "===================================");
            for (ItemData itemData : mList) {
                Utils.log(TAG, itemData.toString());
            }
            Utils.log(TAG, "===================================");
        }
    }
}
