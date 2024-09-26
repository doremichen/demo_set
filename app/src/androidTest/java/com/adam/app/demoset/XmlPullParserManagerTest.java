package com.adam.app.demoset;

import android.content.Context;
import androidx.test.platform.app.InstrumentationRegistry;
import androidx.test.ext.junit.runners.AndroidJUnit4;

import org.junit.Assert;
import org.junit.Before;
import org.junit.Test;
import org.junit.runner.RunWith;

import static org.junit.Assert.*;

import com.adam.app.demoset.xml_parser.XmlPullParserManager;

/**
 * Test SmlPullParserManager function
 */
@RunWith(AndroidJUnit4.class)
public class XmlPullParserManagerTest {

    private Context mCtx;

    @Before
    public void setUp() {
        mCtx = InstrumentationRegistry.getInstrumentation().getTargetContext();
        // init
        XmlPullParserManager.newInstance().init(mCtx);
    }

    @Test
    public void parse_xml_file() {
        XmlPullParserManager.newInstance().parse();
        Assert.assertTrue(true);
    }


}
