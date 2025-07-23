/**
 * Copyright (C) 2022 Adam. All rights reserved.
 * @author Adam Chen
 *
 * @description
 *        This is a demo retrofit coroutine app.
 *
 * @version 1.0
 * @since 2025/07/23
 */
package com.adam.app.demoset.coroutine

import android.content.Context
import android.os.Bundle
import android.view.inputmethod.InputMethodManager
import androidx.appcompat.app.AppCompatActivity
import androidx.lifecycle.ViewModelProvider
import com.adam.app.demoset.Utils
import com.adam.app.demoset.coroutine.view_model.RetrofitViewModel
import com.adam.app.demoset.databinding.ActivityDemoRetrofitCoroutineBinding

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

        // set fetch data button click event
        mBinding.btnLoad.setOnClickListener {

            // Hide soft keyboard
            currentFocus?.let { view ->
                Utils.hideSoftKeyBoardFrom(this, view)
            }

            val id = mBinding.edtUserId.text.toString().toIntOrNull()
            if (id != null) {

                // check if id is valid, the id range is 1 to 10
                if (id < 1 || id > 10) {
                    mBinding.txtResult.text = "Invalid ID"
                    return@setOnClickListener
                }

                viewModel.loadUser(id)
            }
        }

        // observe the result
        viewModel.result.observe(this) { result ->
            mBinding.txtResult.text = result
        }

        // set back button click listener
        mBinding.btnBack.setOnClickListener {
            finish()
        }


    }
}