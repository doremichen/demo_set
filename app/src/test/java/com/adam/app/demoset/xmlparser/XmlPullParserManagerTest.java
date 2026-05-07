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

import static org.junit.Assert.assertNotNull;
import static org.junit.Assert.assertTrue;

import android.content.Context;
import android.os.Build;

import androidx.test.core.app.ApplicationProvider;

import com.adam.app.demoset.xmlparser.model.ItemData;

import org.junit.Before;
import org.junit.Test;
import org.junit.runner.RunWith;
import org.robolectric.RobolectricTestRunner;
import org.robolectric.annotation.Config;

import java.util.List;

/**
 * Unit tests for XmlPullParserManager using Robolectric.
 */
@RunWith(RobolectricTestRunner.class)
@Config(sdk = Build.VERSION_CODES.P)
public class XmlPullParserManagerTest {

    private Context mContext;
    private XmlPullParserManager mParserManager;

    @Before
    public void setUp() {
        mContext = ApplicationProvider.getApplicationContext();
        mParserManager = XmlPullParserManager.newInstance();
    }

    @Test
    public void testParseWithNullContext() {
        // According to our refactored code, passing null returns an empty list
        List<ItemData> result = mParserManager.parse(null);
        assertNotNull(result);
        assertTrue(result.isEmpty());
    }

    @Test
    public void testParseGeneratesList() {
        // This will parse R.xml.xml_demo
        List<ItemData> result = mParserManager.parse(mContext);
        assertNotNull(result);
        // Ensure that the real XML file is parsed correctly in the test environment
        assertTrue("Parsed list should not be empty", result.size() > 0);
    }
}
