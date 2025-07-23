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
package com.adam.app.demoset.coroutine.view_model

import android.app.Application
import androidx.lifecycle.AndroidViewModel
import androidx.lifecycle.LiveData
import androidx.lifecycle.MutableLiveData
import androidx.lifecycle.viewModelScope
import com.adam.app.demoset.R
import com.adam.app.demoset.Utils
import com.adam.app.demoset.coroutine.retrofit_api.RetrofitClient
import kotlinx.coroutines.Dispatchers
import kotlinx.coroutines.launch

class RetrofitViewModel(application: Application) : AndroidViewModel(application) {

    // _result
    private val _result = MutableLiveData<String>()
    val result: LiveData<String> = _result

    /**
     * loadUser
     * @param id
     */
    fun loadUser(id: Int) {
        Utils.info(this, "loadUser")
        // viewModelScope
        viewModelScope.launch(Dispatchers.IO) {
            Utils.info(this, "loadUser launch")
            try {
                    val user = RetrofitClient.apiService.getUser(id)
                    // postvalue
                    _result.postValue(
                        getApplication<Application>().getString(
                            R.string.demo_retrofit_coroutine_user,
                            user.name,
                            user.email
                        )
                    )
                } catch (e: Exception) {
                    _result.postValue(getApplication<Application>().getString(R.string.demo_retrofit_coroutine_error, e.message))
                }
        }

    }


}