/**
 * Copyright 2025 Adam Chen
 * Description: WelcomeViewModel ViewModel is used Live data to demo Data Binding
 * Author: Adam Chen
 * Date: 2025/06/27
 */
package com.adam.app.demoset.data_binding;

import androidx.lifecycle.LiveData;
import androidx.lifecycle.MutableLiveData;
import androidx.lifecycle.ViewModel;

import java.util.Optional;

public class WelcomeViewModel extends ViewModel {
    // Live data: user name
    public final MutableLiveData<String> userName = new MutableLiveData<>();
    // Live data: welcome message
    public final MutableLiveData<String> welcomeMessage = new MutableLiveData<>();

}
