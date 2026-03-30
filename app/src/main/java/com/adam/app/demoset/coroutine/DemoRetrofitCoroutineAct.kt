/**
 * Copyright (c) 2022 Adam Chen Demo set project. All rights reserved.
 * <p>
 * Description: This is a retrofit coroutine demo.
 * </p>
 *
 * @author Adam Chen
 * @version 1.0 - 2025/07/23
 */
package com.adam.app.demoset.coroutine

import android.os.Bundle
import androidx.appcompat.app.AppCompatActivity
import androidx.lifecycle.ViewModelProvider
import com.adam.app.demoset.coroutine.viewmodel.RetrofitViewModel
import com.adam.app.demoset.databinding.ActivityDemoRetrofitCoroutineBinding
import com.adam.app.demoset.utils.UIUtils

class DemoRetrofitCoroutineAct : AppCompatActivity() {

    // view binding
    private lateinit var mBinding: ActivityDemoRetrofitCoroutineBinding

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        // view binding
        mBinding = ActivityDemoRetrofitCoroutineBinding.inflate(layoutInflater)
        setContentView(mBinding.root)

        // view model initialization
        val viewModel = ViewModelProvider(this)[RetrofitViewModel::class.java]
        // data binding to view
        mBinding.vm = viewModel
        mBinding.lifecycleOwner = this

        setupSystemBars()

        initListeners()

        // set fetch data button click event
//        mBinding.btnLoad.setOnClickListener {
//
//            // Hide soft keyboard
//            currentFocus?.let { view ->
//                Utils.hideSoftKeyBoardFrom(this, view)
//            }
//
//            val id = mBinding.edtUserId.text.toString().toIntOrNull()
//            if (id != null) {
//
//                // check if id is valid, the id range is 1 to 10
//                if (id < 1 || id > 10) {
//                    mBinding.txtResult.text = "Invalid ID"
//                    return@setOnClickListener
//                }
//
//                viewModel.loadUserData(id)
//            }
//        }

        // observe the result
//        viewModel.resultDisplay.observe(this) { result ->
//            mBinding.txtResult.text = result
//        }
//
//        // set back button click listener
//        mBinding.btnBack.setOnClickListener {
//            finish()
//        }


    }

    private fun setupSystemBars() {
        UIUtils.hideSystemBar(window)
        UIUtils.applySystemBarInsets(mBinding.rootLayout, mBinding.appBarWrapper)
    }

    private fun initListeners() {
        mBinding.btnBack.setOnClickListener {
            finish()
        }
    }
}