/**
 * Copyright (C) 2026 Adam Chen. All rights reserved.
 *
 * Description: This is the Category item.
 *
 * @author Adam Chen
 * @version 1.0 - 2026/03/09
 */
package com.adam.app.demoset;

public class CategoryItem {

    private final String mName;

    public CategoryItem(String name) {
        this.mName = name;
    }

    // --- getter ---
    public String getName() {
        return mName;
    }
}
