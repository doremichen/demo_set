package com.adam.app.demoset.usb_storage;

import static org.junit.Assert.assertEquals;

import com.adam.app.demoset.usb_storage.adapter.FileListAdapter;

import org.junit.Test;

public class FileListAdapterTest {

    @Test
    public void testToFileSizeInfo() {
        assertEquals("0 B", FileListAdapter.toFileSizeInfo(0));
        assertEquals("0 B", FileListAdapter.toFileSizeInfo(-1));
        assertEquals("512.00 B", FileListAdapter.toFileSizeInfo(512));
        assertEquals("1.00 kB", FileListAdapter.toFileSizeInfo(1024));
        assertEquals("1.00 MB", FileListAdapter.toFileSizeInfo(1024 * 1024));
        assertEquals("1.00 GB", FileListAdapter.toFileSizeInfo(1024L * 1024 * 1024));
        assertEquals("1.50 MB", FileListAdapter.toFileSizeInfo((long) (1.5 * 1024 * 1024)));
    }
}
