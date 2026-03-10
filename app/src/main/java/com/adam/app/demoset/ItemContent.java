/**
 * This contains the information of the every items
 * <p>
 * info:
 *
 * @author: AdamChen
 * @date: 2018/9/19
 */

package com.adam.app.demoset;


public class ItemContent {

    private final String mCategoryResKey;
    private final String mCategory;
    private final String mTitle;
    private final String mClassName;
    private final String mPkgName;


    public ItemContent(String categoryResKey,
                       String title,
                       String className,
                       String pkgName) {
        this.mCategoryResKey = categoryResKey;
        this.mCategory = categoryResKey.toUpperCase();
        this.mTitle = title;
        this.mClassName = className;
        this.mPkgName = pkgName;
    }


    // --- getter ---
    public String getCategoryResKey() {
        return mCategoryResKey;
    }

    public String getCategory() {
        return mCategory;
    }

    public String getTitle() {
        return mTitle;
    }

    public String getClsname() {
        return mClassName;
    }

    public String getPkgname() {
        return mPkgName;
    }
}
