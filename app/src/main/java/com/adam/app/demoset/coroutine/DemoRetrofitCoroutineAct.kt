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