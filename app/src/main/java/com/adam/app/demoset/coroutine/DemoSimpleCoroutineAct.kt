/**
 * Copyright (c) 2022 Adam Chen Demo set project. All rights reserved.
 * <p>
 * Description: This is a simple coroutine demo.
 * </p>
 *
 * @author Adam Chen
 * @version 1.0 - 2025/07/23
 */
package com.adam.app.demoset.coroutine

import android.os.Bundle
import androidx.appcompat.app.AppCompatActivity
import androidx.lifecycle.ViewModelProvider
import com.adam.app.demoset.coroutine.viewmodel.MainViewModel
import com.adam.app.demoset.coroutine.viewmodel.MainViewModelFactory
import com.adam.app.demoset.databinding.ActivityDemoSimpleCoroutingBinding
import com.adam.app.demoset.utils.UIUtils

class DemoSimpleCoroutineAct : AppCompatActivity() {

    // view binding
    private lateinit var mBinding: ActivityDemoSimpleCoroutingBinding

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        // view binding
        mBinding = ActivityDemoSimpleCoroutingBinding.inflate(layoutInflater)
        setContentView(mBinding.root)

        UIUtils.applySystemBarInsets(mBinding.root, mBinding.txtIntro)

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