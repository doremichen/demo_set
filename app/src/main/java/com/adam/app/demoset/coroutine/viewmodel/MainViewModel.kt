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

package com.adam.app.demoset.coroutine.viewmodel

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