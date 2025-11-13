/**
 * This class is used iterator to parse xml file.
 *
 * @author Adam Chen
 * @version 1.0
 * @since 2025-11-13
 */
package com.adam.app.demoset.xml_parser.pattern;

import com.adam.app.demoset.xml_parser.util.NoSuchElementException;
import com.adam.app.demoset.xml_parser.util.XmlParsingException;

import org.xmlpull.v1.XmlPullParser;
import org.xmlpull.v1.XmlPullParserException;

import java.io.IOException;
import java.util.Iterator;

public class XmlEventIterator implements Iterator<Integer> {
    private final XmlPullParser mParser;
    private int mEventType = -1;

    public XmlEventIterator(XmlPullParser mParser) {
        this.mParser = mParser;

        this.mEventType = XmlPullParser.START_DOCUMENT;
    }

    @Override
    public boolean hasNext() {
        return this.mEventType != XmlPullParser.END_DOCUMENT;
    }

    @Override
    public Integer next() {
        if (!hasNext()) {
            throw new NoSuchElementException("No more elements");
        }

        try {
            this.mEventType = this.mParser.next();
        } catch (IOException | XmlPullParserException e) {
            throw new XmlParsingException("Failed to advance to next XML event", e);
        }
        return this.mEventType;
    }
}
