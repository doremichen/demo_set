/**
 * Copyright (C) 2022 Adam. All rights reserved.
 * @author Adam Chen
 *
 * @description
 *        This is a demo simple coroutine app.
 *
 * @version 1.0
 * @since 2025/07/22
 */
package com.adam.app.demoset.coroutine

import android.os.Bundle
import androidx.appcompat.app.AppCompatActivity
import androidx.lifecycle.ViewModelProvider
import com.adam.app.demoset.coroutine.view_model.MainViewModel
import com.adam.app.demoset.coroutine.view_model.MainViewModelFactory
import com.adam.app.demoset.databinding.ActivityDemoSimpleCoroutingBinding

class DemoSimpleCoroutineAct : AppCompatActivity() {

    // view binding
    private lateinit var mBinding: ActivityDemoSimpleCoroutingBinding

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        // view binding
        mBinding = ActivityDemoSimpleCoroutingBinding.inflate(layoutInflater)
        setContentView(mBinding.root)

        // view model initialization by factory
        val viewModel = ViewModelProvider(
            this,
            MainViewModelFactory(this)
        )[MainViewModel::class.java]


        // set fetch data button click event
        mBinding.btnFetch.setOnClickListener {
            viewModel.loadData()
        }

        // observe the result
        viewModel.result.observe(this) { result ->
            mBinding.txtUserInfo.text = result
        }

        // set back button click event
        mBinding.btnBack.setOnClickListener {
            finish()
        }


    }
}