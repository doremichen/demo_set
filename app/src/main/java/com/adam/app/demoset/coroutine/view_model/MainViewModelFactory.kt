/**
 * Copyright (C) 2022 Adam. All rights reserved.
 * @author Adam Chen
 *
 * @description
 *             This class is view model factory.
 *
 * @version 1.0
 * @since 2025/07/23
 */
package com.adam.app.demoset.coroutine.view_model

import android.content.Context
import androidx.lifecycle.ViewModel
import androidx.lifecycle.ViewModelProvider

class MainViewModelFactory(private val context: Context): ViewModelProvider.Factory {

    /**
     * create is a function that takes a modelClass parameter of type Class<T> and returns an instance of T.
     */
    override fun <T : ViewModel> create(modelClass: Class<T>): T {
        if (modelClass.isAssignableFrom(MainViewModel::class.java)) {
            @Suppress("UNCHECKED_CAST")
            return MainViewModel(context.applicationContext) as T // 傳入 Application Context
        }
        throw IllegalArgumentException("Unknown ViewModel class")
    }
}