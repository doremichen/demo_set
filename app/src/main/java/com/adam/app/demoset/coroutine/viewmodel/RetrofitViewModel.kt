/**
 * Copyright (C) 2022 Adam. All rights reserved.
 * @author Adam Chen
 *
 * @description
 *             This class is used to represent a Retrofit view model.
 *
 * @version 1.0
 * @since 2025/07/23
 */
package com.adam.app.demoset.coroutine.viewmodel

import android.app.Application
import androidx.lifecycle.AndroidViewModel
import androidx.lifecycle.LiveData
import androidx.lifecycle.MutableLiveData
import androidx.lifecycle.ViewModel
import androidx.lifecycle.viewModelScope
import com.adam.app.demoset.R
import com.adam.app.demoset.Utils
import com.adam.app.demoset.coroutine.retrofit_api.RetrofitClient
import kotlinx.coroutines.Dispatchers
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