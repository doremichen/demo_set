/**
 * Copyright (C) 2026 Adam Chen. All rights reserved.
 * <p>
 * Description: This class is the activity of flowlab
 * </p>
 * @author Adam Chen
 * @version 1.0 - 2026/03/30
 */
package com.adam.app.demoset.flowlab

import android.os.Bundle
import androidx.activity.viewModels
import androidx.appcompat.app.AppCompatActivity
import androidx.lifecycle.Lifecycle
import androidx.lifecycle.lifecycleScope
import androidx.lifecycle.repeatOnLifecycle
import androidx.recyclerview.widget.LinearLayoutManager
import com.adam.app.demoset.LogAdapter
import com.adam.app.demoset.databinding.ActivityFlowLabBinding
import com.adam.app.demoset.flowlab.viewmodel.FlowLabViewModel
import com.adam.app.demoset.utils.UIUtils
import com.adam.app.demoset.utils.Utils
import kotlinx.coroutines.launch

class FlowLabActivity : AppCompatActivity() {

    // view binding
    private lateinit var _binding: ActivityFlowLabBinding

    // view model
    private val _viewModel: FlowLabViewModel by viewModels()

    // log adapter
    private var _logAdapter = LogAdapter()

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        // view binding
        _binding = ActivityFlowLabBinding.inflate(getLayoutInflater())
        setContentView(_binding.getRoot())

        UIUtils.applySystemBarInsets(_binding.getRoot(), _binding.layoutInstruction)

        // data binging
        _binding.setVm(_viewModel)
        _binding.setLifecycleOwner(this)

        setupRecyclerView()

        setupObservers()
    }

    private fun setupRecyclerView() {
        // init log dapter
        _logAdapter = LogAdapter()
        // linearlayout manager
        var linearLayoutManager = LinearLayoutManager(this)
        linearLayoutManager.setOrientation(LinearLayoutManager.VERTICAL)
        linearLayoutManager.setStackFromEnd(true)
        // set layout manager
        _binding.rvLogs.setLayoutManager(linearLayoutManager)
        // set adapter
        _binding.rvLogs.setAdapter(_logAdapter)

    }

    private fun setupObservers() {

        lifecycleScope.launch {
            repeatOnLifecycle(Lifecycle.State.STARTED) {
                _viewModel.logs.collect { it ->
                    _logAdapter.submitList(ArrayList(it)) {
                        val lastPosition = _logAdapter.getItemCount() - 1
                        if (lastPosition >= 0) {
                            _binding.rvLogs.smoothScrollToPosition(lastPosition)
                        }
                    }
                }
            }
        }

        lifecycleScope.launch {
            repeatOnLifecycle(Lifecycle.State.STARTED) {
                _viewModel.toastEvent.collect {
                    _viewModel.addLog(it)
                    Utils.showToast(this@FlowLabActivity, it)
                }
            }
        }

        lifecycleScope.launch {
            repeatOnLifecycle(Lifecycle.State.STARTED) {
                _viewModel.counterState.collect {
                    // add log
                    _viewModel.addLog("counter: $it")
                }
            }

        }
    }
}