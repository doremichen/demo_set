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

package com.adam.app.demoset.xmlparser.pattern;

import com.adam.app.demoset.utils.Utils;
import com.adam.app.demoset.xmlparser.model.ItemData;
import com.adam.app.demoset.xmlparser.util.XmlItemContent;

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
