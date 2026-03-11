/**
 * Copyright (C) 2020 Adam Chen Demp set project. All rights reserved.
 *<p>
 * Description:
 * Store data form xml_demo.xml file
 * </p>
 *
 * Author: Adam Chen
 * Date: 2020/11/11
 */
package com.adam.app.demoset.xmlparser.model;

import androidx.annotation.NonNull;

public class ItemData {

    private int mId;
    private String mName;

    public ItemData() {}

    public ItemData(int id, String name) {
        this.mId = id;
        this.mName = name;
    }

    public void setId(int id) {
        this.mId = id;
    }

    public int getId() {
        return this.mId;
    }

    public void setName(String name) {
        this.mName = name;
    }

    public String getName() {
        return  this.mName;
    }

    @NonNull
    @Override
    public String toString() {
        StringBuilder stb = new StringBuilder("\n");
        stb.append("id: ").append(this.mId).append("\n");
        stb.append("name: ").append(this.mName).append("\n");
        return stb.toString();
    }
}
