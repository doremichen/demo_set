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

import android.app.Application
import androidx.lifecycle.AndroidViewModel
import androidx.lifecycle.LiveData
import androidx.lifecycle.MutableLiveData
import androidx.lifecycle.viewModelScope
import com.adam.app.demoset.R
import com.adam.app.demoset.utils.Utils
import com.adam.app.demoset.coroutine.retrofit_api.RetrofitClient
import kotlinx.coroutines.launch

class RetrofitViewModel(application: Application) : AndroidViewModel(application) {

    // data bindingL userId
    val userId = MutableLiveData<String>("")

    // live data: isLoading
    private val _isLoading = MutableLiveData<Boolean>(false)
    val isLoading: LiveData<Boolean> = _isLoading

    // Live data: _resultDisplay
    private val _resultDisplay = MutableLiveData<String>()
    val resultDisplay: LiveData<String> = _resultDisplay

    /**
     * loadUser
     * @param id
     */
    fun loadUserData() {
        Utils.info(this, "loadUserData")
        val idText = userId.value
        if (idText.isNullOrBlank()) {
            // update _resultDisplay
            _resultDisplay.value = "Error: Please enter a valid User ID"
            return
        }

        val id = idText.toIntOrNull() ?: return // null-safe guard clause

        Utils.info(this, "Starting Coroutine to load user: $id")

        viewModelScope.launch {
            _isLoading.postValue(true)
            _resultDisplay.value = "Fetching data from server...\n--------------------------"

            try {
                // Retrofit 配合 suspend function，不需要手動切換到 Dispatchers.IO
                val user = RetrofitClient.apiService.getUser(id)

                _resultDisplay.value = getApplication<Application>().getString(
                    R.string.demo_retrofit_coroutine_user,
                    user.name,
                    user.email
                )
            } catch (e: Exception) {
                Utils.error(this, "API Call Failed: ${e.message}")
                _resultDisplay.value = getApplication<Application>().getString(
                    R.string.demo_retrofit_coroutine_error,
                    e.message ?: "Unknown Error"
                )

            } finally {
                _isLoading.postValue(false)
            }

//        // viewModelScope
//        viewModelScope.launch(Dispatchers.IO) {
//            Utils.info(this, "loadUser launch")
//            try {
//                    val user = RetrofitClient.apiService.getUser(id)
//                    // postvalue
//                    _resultDisplay.postValue(
//                        getApplication<Application>().getString(
//                            R.string.demo_retrofit_coroutine_user,
//                            user.name,
//                            user.email
//                        )
//                    )
//                } catch (e: Exception) {
//                    _resultDisplay.postValue(getApplication<Application>().getString(R.string.demo_retrofit_coroutine_error, e.message))
//                }
        }

    }


}