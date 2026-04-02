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

package com.adam.app.demoset.datastore.koltlin

import android.os.Bundle
import androidx.activity.viewModels
import androidx.appcompat.app.AppCompatActivity
import androidx.appcompat.app.AppCompatDelegate
import androidx.recyclerview.widget.LinearLayoutManager
import com.adam.app.demoset.databinding.ActivityDataStoreDemo2Binding
import com.adam.app.demoset.datastore.koltlin.viewmodel.SettingsViewModel
import com.adam.app.demoset.datastore.model.SettingsModel
import com.adam.app.demoset.utils.LocaleUtils
import com.adam.app.demoset.utils.LogAdapter
import com.adam.app.demoset.utils.UIUtils
import com.adam.app.demoset.utils.Utils
import com.google.android.material.slider.Slider

class DataStoreDemoAct : AppCompatActivity() {

    // view binding
    private lateinit var _binding: ActivityDataStoreDemo2Binding

    // log adapter
    private lateinit var _logAdapter: LogAdapter

    // currentLanguage
    private var _currentLanguage = "zh_TW"

    // view model
    private val _viewModel: SettingsViewModel by viewModels()


    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        // view binding
        _binding = ActivityDataStoreDemo2Binding.inflate(layoutInflater)
        setContentView(_binding.root)

        UIUtils.applySystemBarInsets(_binding.root, _binding.appBarWrapper)

        _binding.vm = _viewModel
        _binding.lifecycleOwner = this



        initLogList()
        setupListeners()
        observeData()

    }

    private fun initLogList() {
        _logAdapter = LogAdapter()
        val manager = LinearLayoutManager(this)
        manager.stackFromEnd = true
        // set linear layout
        _binding.rvLogs.layoutManager = manager
        _binding.rvLogs.adapter = _logAdapter
    }

    private fun setupListeners() {
        // set slider listener
        _binding.sliderFontSize.addOnSliderTouchListener(object : Slider.OnSliderTouchListener {
            override fun onStartTrackingTouch(slider: Slider) {
                Utils.info(this@DataStoreDemoAct, "start tracking")
            }

            override fun onStopTrackingTouch(slider: Slider) {
                Utils.info(this@DataStoreDemoAct, "stop tracking")
                _viewModel.setFontSize(slider.value)
            }

        })
    }

    private fun observeData() {
        // observer settings
        _viewModel.settings.observe(this) {
            it?.let {
                updateUI(it)
            }
        }

        // observer logs
        _viewModel.logs.observe(this) {
            it?.let {
                updateLog(it)
            }
        }

    }

    private fun updateLog(it: List<String>) {
        _logAdapter.submitList(ArrayList<String>(it)) {
            _binding.rvLogs.scrollToPosition(_logAdapter.getItemCount() - 1)
        }
    }

    private fun updateUI(settings: SettingsModel) {
        _viewModel.addLog("update UI")
        // handle dark mode
        var targetMode = if (settings.isDarkMode) {
            AppCompatDelegate.MODE_NIGHT_YES
        } else {
            AppCompatDelegate.MODE_NIGHT_NO
        }
        // check if need change
        if (targetMode != AppCompatDelegate.getDefaultNightMode()) {
            AppCompatDelegate.setDefaultNightMode(targetMode)
        }

        // handle language setting
        if (_currentLanguage != settings.language) {
            _currentLanguage = settings.language
            LocaleUtils.applyLocale(this, settings.language)
        }

        // handle font size
        var max = _binding.sliderFontSize.valueTo
        var min = _binding.sliderFontSize.valueFrom
        var safeValue = Math.max(min, Math.min(max, settings.fontSize))
        _binding.sliderFontSize.value = safeValue
    }
}