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

import com.adam.app.demoset.xmlparser.util.NoSuchElementException;
import com.adam.app.demoset.xmlparser.util.XmlParsingException;

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
