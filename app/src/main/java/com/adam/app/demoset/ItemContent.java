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

    private String mTitle;
    private String mClassName;
    private String mPkgName;

    public ItemContent(String title, String className, String pkgName) {
        this.mTitle = title;
        this.mClassName = className;
        this.mPkgName = pkgName;

    }

    public String getTitle() {
        return this.mTitle;
    }

    public String getClassName() {
        return this.mClassName;
    }

    public String getPkgName() {
        return this.mPkgName;
    }

}
