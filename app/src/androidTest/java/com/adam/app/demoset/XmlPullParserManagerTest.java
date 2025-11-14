/**
 * init(context) init correct
 * parse() return empty list when context is null
 * parse() generate list when parse the real xml file
 * clearList() clear list
 *
 * @author Adam Chen
 * @version 1.0
 * @since 2025-11-14
 */
package com.adam.app.demoset;

import static org.junit.Assert.assertNotNull;
import static org.junit.Assert.assertTrue;

import android.content.Context;

import androidx.test.core.app.ApplicationProvider;
import androidx.test.ext.junit.runners.AndroidJUnit4;

import com.adam.app.demoset.xml_parser.XmlPullParserManager;
import com.adam.app.demoset.xml_parser.model.ItemData;

import org.junit.After;
import org.junit.Before;
import org.junit.Test;
import org.junit.runner.RunWith;

import java.util.List;

/**
 * Test SmlPullParserManager function
 */
@RunWith(AndroidJUnit4.class)
public class XmlPullParserManagerTest {

    private Context mContext;
    private XmlPullParserManager mParserManager;

    @Before
    public void setUp() {

        mContext = ApplicationProvider.getApplicationContext();
        mParserManager = XmlPullParserManager.newInstance();
        // clear list
        mParserManager.clearList();
    }

    @After
    public void tearDown() {

        mParserManager.clearList();
        mParserManager.init(null);
    }

    // ---------------------------------------------
    // 1 parse() un init context
    // ---------------------------------------------
    @Test
    public void testParseWithoutInit() {

        List<ItemData> result = mParserManager.parse();
        // check
        assertNotNull(result);


        assertTrue(result.isEmpty());
    }

    // ---------------------------------------------
    // 2 init() set context
    // ---------------------------------------------
    @Test
    public void testInitContext() {

        mParserManager.init(mContext);
        List<ItemData> result = mParserManager.parse();
        // check
        assertNotNull(result);
    }

    // ---------------------------------------------
    // 3 parse() generate non empty list
    // ---------------------------------------------
    @Test
    public void testParseGeneratesList() {

        mParserManager.init(mContext);
        List<ItemData> result = mParserManager.parse();
        assertNotNull(result);
        // check
        assertTrue(result.size() > 0);
    }

    // ---------------------------------------------
    // 4 clearList() clear list
    // ---------------------------------------------
    @Test
    public void testClearList() {

        mParserManager.init(mContext);
        mParserManager.parse();
        mParserManager.clearList();
        List<ItemData> listAfterClear = mParserManager.parse();
        assertNotNull(listAfterClear);
        // check
        assertTrue(listAfterClear.size() > 0);
    }


}
