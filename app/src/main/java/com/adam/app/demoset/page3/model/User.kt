/**
 * Copyright (C) 2026 Adam Chen. All rights reserved.
 * <p>
 * Description: This class is the model of page3
 * </p>
 * @author Adam Chen
 * @version 1.0 - 2026/03/30
 */
package com.adam.app.demoset.page3.model

import com.google.gson.annotations.SerializedName

data class User(
    @SerializedName("id")
    val id: Int,
    @SerializedName("name")
    val name: String,
    @SerializedName("detail")
    val detail: String,
)
