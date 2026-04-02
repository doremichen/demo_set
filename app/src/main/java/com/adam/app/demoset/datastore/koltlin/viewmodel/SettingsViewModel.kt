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

package com.adam.app.demoset.datastore.koltlin.viewmodel

import android.app.Application
import androidx.lifecycle.AndroidViewModel
import androidx.lifecycle.LiveData
import androidx.lifecycle.MutableLiveData
import androidx.lifecycle.asLiveData
import androidx.lifecycle.viewModelScope
import com.adam.app.demoset.R
import com.adam.app.demoset.datastore.koltlin.repository.SettingsRepository
import com.adam.app.demoset.datastore.model.SettingsModel
import kotlinx.coroutines.launch

class SettingsViewModel(application: Application) : AndroidViewModel(application) {
    // repository
    private val _repository = SettingsRepository.getInstance(application)

    // flow -> live data provide to xml file
    val settings: LiveData<SettingsModel> = _repository.settingsFlow.asLiveData()

    private val _logs = MutableLiveData<List<String>>(mutableListOf())
    val logs: LiveData<List<String>> = _logs


    // setter ---

    fun getSettingsSummary(settings: SettingsModel?): String {
        settings ?: return ""

        val mode = getApplication<Application>().resources.getString(
            if (settings.isDarkMode) R.string.demo_datastore_mode_dark else R.string.demo_datastore_mode_light
        )

        return getApplication<Application>().resources.getString(
            R.string.demo_datastore_settings_summary,
            mode,
            settings.language,
            settings.fontSize
        )
    }

    fun getFontSizePreviewText(fontSize: Float): String {
        return getApplication<Application>().resources.getString(
            R.string.demo_datastore_font_size_preview,
            fontSize)
    }

    fun toggleDarkMode(isDarkMode: Boolean) {
        viewModelScope.launch {
            _repository.setDarkMode(isDarkMode)
        }
        // log
        addLog("toggleDarkMode: $isDarkMode")
    }

    fun changeLanguage(language: String) {
        viewModelScope.launch {
            _repository.setLanguage(language)
        }

        // log
        addLog("changeLanguage: $language")
    }

    fun setFontSize(fontSize: Float) {
        viewModelScope.launch {
            _repository.setFontSize(fontSize)
        }

        // log
        addLog("setFontSize: $fontSize")
    
    }

    fun addLog(log: String) {
        val logs = _logs.value?.toMutableList() ?: mutableListOf()
        logs.add(log)
        _logs.value = logs
    }

}