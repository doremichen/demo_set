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

package com.adam.app.demoset.usb_storage.adapter;

import android.content.Context;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.BaseAdapter;

import androidx.annotation.NonNull;
import androidx.databinding.DataBindingUtil;

import com.adam.app.demoset.R;
import com.adam.app.demoset.databinding.ItemUsbFileListBinding;
import com.adam.app.demoset.usb_storage.model.FileItem;
import com.adam.app.demoset.utils.Utils;

import java.util.HashMap;
import java.util.List;
import java.util.Map;

public class FileListAdapter extends BaseAdapter {

    private List<FileItem> mList;
    private final Context mContext;

    public FileListAdapter(Context context, List<FileItem> list) {
        this.mContext = context;
        this.mList = list;
    }

    public void updateList(List<FileItem> newList) {
        this.mList = newList;
        notifyDataSetChanged();
    }


    @Override
    public int getCount() {
        return this.mList.size();
    }

    @Override
    public FileItem getItem(int i) {
        return this.mList.get(i);
    }

    @Override
    public long getItemId(int i) {
        return i;
    }

    @Override
    public View getView(int i, View convertView, ViewGroup viewGroup) {
        ItemUsbFileListBinding binding;
        if (convertView == null) {
            binding = DataBindingUtil.inflate(LayoutInflater.from(mContext), R.layout.item_usb_file_list, viewGroup, false);
            convertView = binding.getRoot();
            convertView.setTag(binding);
        } else {
            binding = (ItemUsbFileListBinding) convertView.getTag();
        }

        FileItem item = mList.get(i);
        binding.setFileItem(item);
        binding.fileIconIv.setImageResource(item.getIconResId());
        binding.executePendingBindings();

        return convertView;
    }


    public enum FileIcon {
        JPG(".jpg", R.drawable.image),
        PNG(".png", R.drawable.image),
        TXT(".txt", R.drawable.file),
        PDF(".pdf", R.drawable.pdf),
        XLS(".xls", R.drawable.xls),
        XLSX(".xlsx", R.drawable.xls),
        PPT(".ppt", R.drawable.ppt),
        PPTX(".pptx", R.drawable.ppt),
        DOC(".doc", R.drawable.doc),
        DOCX(".docx", R.drawable.doc),
        MP4(".mp4", R.drawable.video),
        AVI(".avi", R.drawable.video);

        private final String mImgType;
        private final int mResId;

        private static final Map<String, Integer> sEXTENSION_MAP = new HashMap<>();
        static {
            for (FileIcon item : values()) {
                sEXTENSION_MAP.put(item.mImgType, item.mResId);
            }
        }

        FileIcon(String imgType, int resId) {
            this.mImgType = imgType;
            this.mResId = resId;
        }

        /**
         * Get the resource id
         * @param isFolder: Check whether it is folder
         * @param fileName: file full name
         * @return resource id
         */
        public static int getResourceIdBy(boolean isFolder, @NonNull String fileName) {
            Utils.info(FileIcon.class, "getResourceIdBy");
            if (isFolder) return R.drawable.folder;

            String name = fileName.toLowerCase();
            for (Map.Entry<String, Integer> entry : sEXTENSION_MAP.entrySet()) {
                if (name.endsWith(entry.getKey())) return entry.getValue();
            }
            return R.drawable.unkown_file;
        }

    }


    /**
     * Calculate File size information
     * @param size: file size
     * @return file size string
     */
    public static String toFileSizeInfo(long size) {
        if (size <= 0) return "0 B";
        final String[] units = new String[] { "B", "kB", "MB", "GB", "TB" };
        int digitGroups = (int) (Math.log10(size) / Math.log10(1024));
        return String.format("%.2f %s", size / Math.pow(1024, digitGroups), units[digitGroups]);
    }

}
