/**
 * File list adapter
 */
package com.adam.app.demoset.usb_storage;

import android.content.Context;
import android.view.View;
import android.view.ViewGroup;
import android.widget.BaseAdapter;
import android.widget.ImageView;
import android.widget.TextView;

import androidx.annotation.NonNull;

import com.adam.app.demoset.R;
import com.adam.app.demoset.Utils;
import com.github.mjdev.libaums.fs.UsbFile;

import java.io.File;
import java.util.Arrays;
import java.util.HashMap;
import java.util.List;
import java.util.Map;
import java.util.function.Function;

public class FileListAdapter<T> extends BaseAdapter {

    // list
    private List<T> mList;

    // view context
    private Context mContext;

    public FileListAdapter(Context context, List<T> list) {
        this.mContext = context;
        this.mList = list;
    }


    @Override
    public int getCount() {
        return this.mList.size();
    }

    @Override
    public Object getItem(int i) {
        return this.mList.get(i);
    }

    @Override
    public long getItemId(int i) {
        return i;
    }

    @Override
    public View getView(int i, View convertView, ViewGroup viewGroup) {
        Utils.info(this, "getView +++");
        // view
        ViewHolder holder = null;
        if (convertView == null) {
            convertView = View.inflate(this.mContext, R.layout.item_usb_file_list, null);
            holder = new ViewHolder(convertView);
            convertView.setTag(holder);
        } else {
            holder = (ViewHolder) convertView.getTag();
        }

        //  update list item
        T t = this.mList.get(i);
        int ResId = -1;
        String fileName = "";
        String fileSizeInfo = "";
        if (t instanceof UsbFile) {
            Utils.info(this, "UsbFile");
            UsbFile usbFile = (UsbFile) t;
            ResId = RESICONITEMS.getResourceIdBy(usbFile.isDirectory(), usbFile.getName());
            fileName = usbFile.getName();
            if (!usbFile.isDirectory()) {
                fileSizeInfo =  toFileSizeInfo(usbFile.getLength());
            }
        } else if (t instanceof File) {
            Utils.info(this, "File");
            File localFile = (File) t;
            ResId = RESICONITEMS.getResourceIdBy(localFile.isDirectory(), localFile.getName());
            fileName = localFile.getName();
            if (!localFile.isDirectory()) {
                fileSizeInfo =  toFileSizeInfo(localFile.length());
            }
        } else {
            throw new RuntimeException("Unknown class!!!");
        }

        // update
        holder.mIcon.setImageResource(ResId);
        holder.mFileName.setText(fileName);
        holder.mFileSize.setText(fileSizeInfo);
        Utils.info(this, "getView xxx");
        return convertView;
    }


    private enum RESICONITEMS {
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

        private RESICONITEMS(String imgType, int resId) {
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
            Utils.info(RESICONITEMS.class, "getResourceIdBy +++");
            fileName = fileName.toLowerCase();

            if (isFolder) {
                Utils.info(RESICONITEMS.class, "getResourceIdBy xxx: folder");
                return R.drawable.folder;
            }

            String finalFileName = fileName;
            return Arrays.stream(RESICONITEMS.values())
                    .filter(item -> finalFileName.endsWith(item.mImgType))
                    .mapToInt(item -> item.mResId)
                    .findFirst()
                    .orElse(R.drawable.unkown_file);
        }

    }


    private static final Map<Long, Function<Long, String>> FORMATTERS = new HashMap<>();
    static {
        FORMATTERS.put(1L, size -> size + " B");
        FORMATTERS.put(1024L, size -> String.format("%.2f kB", (double) size / 1024));
        FORMATTERS.put(1024L * 1024, size -> String.format("%.2f MB", (double) size / 1024 / 1024));
    }

    /**
     * Calculate File size infomation
     * @param size: file size
     * @return file size string
     */
    private String toFileSizeInfo(long size) {
        return FORMATTERS.entrySet().stream()
                .filter(entry -> size >= entry.getKey())
                .max(Map.Entry.comparingByKey())
                .map(Map.Entry::getValue)
                .map(formatter -> formatter.apply(size))
                .orElse(size + " B");
    }


    /**
     * List item view
     */
    private class ViewHolder {
        private TextView mFileName;
        private TextView mFileSize;
        private ImageView mIcon;

        ViewHolder(View view) {
            this.mFileName = view.findViewById(R.id.file_name_tv);
            this.mFileSize = view.findViewById(R.id.file_size_tv);
            this.mIcon = view.findViewById(R.id.file_icon_iv);
        }

    }

}
