/**
 * Copyright (C) 2022 Adam. All rights reserved.
 * @author Adam Chen
 *
 * @description
 *        This class is used to provide data to the view.
 *
 * @version 1.0
 * @since 2025/07/23
 */
package com.adam.app.demoset.coroutine.view_model

import android.content.Context
import androidx.lifecycle.MutableLiveData
import androidx.lifecycle.ViewModel
import androidx.lifecycle.viewModelScope
import com.adam.app.demoset.R
import kotlinx.coroutines.Dispatchers
import kotlinx.coroutines.async
import kotlinx.coroutines.delay
import kotlinx.coroutines.launch

class MainViewModel(private val mContext: Context) : ViewModel() {
    // _result is private and only accessible within this ViewModel class
    private val _result = MutableLiveData<String>()
    // result is exposed as a read-only LiveData
    val result: MutableLiveData<String>
        get() = _result

    /**
     * loadData is a function that takes no arguments and returns nothing.
     */
    fun loadData() {
        viewModelScope.launch(Dispatchers.IO) {
            // fetch data from source 1
            val data1 = async { fetchFromSource1() }
            // fetch data from source 2
            val data2 = async { fetchFromSource2() }
            // combine the two data sources
            val result = mContext.getString(
                R.string.demo_simple_coroutine_result,
                data1.await(),
                data2.await()
            )
            _result.postValue(result)
        }
    }

    /**
     * fetchFromSource1
     *        monitor to fetch data from source 1
     */
    private suspend fun fetchFromSource1(): String {
        // delay 1 second
        delay(1000L)
        return mContext.getString(R.string.demo_simple_coroutine_data_from_source_1)
    }

    /**
     * fetchFromSource2
     *         monitor to fetch data from source 2
     */
    private suspend fun fetchFromSource2(): String {
        // delay 2 second
        delay(2000L)
        return mContext.getString(R.string.demo_simple_coroutine_data_from_source_2)
    }


}