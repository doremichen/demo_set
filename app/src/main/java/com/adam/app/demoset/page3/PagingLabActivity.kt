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

package com.adam.app.demoset.page3

import android.os.Bundle
import androidx.activity.viewModels
import androidx.appcompat.app.AppCompatActivity
import androidx.core.view.isVisible
import androidx.lifecycle.Lifecycle
import androidx.lifecycle.lifecycleScope
import androidx.lifecycle.repeatOnLifecycle
import androidx.paging.LoadState
import com.adam.app.demoset.databinding.ActivityPagingLabBinding
import com.adam.app.demoset.page3.adapter.UserPagingAdapter
import com.adam.app.demoset.page3.viewmodel.PagingLabViewModel
import com.adam.app.demoset.utils.UIUtils
import kotlinx.coroutines.flow.collectLatest
import kotlinx.coroutines.launch

class PagingLabActivity : AppCompatActivity() {
    // view binding
    private lateinit var _binding: ActivityPagingLabBinding
    private val _viewModel by viewModels<PagingLabViewModel>()

    // paging adapter
    private val _pagingAdapter = UserPagingAdapter()

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        _binding = ActivityPagingLabBinding.inflate(layoutInflater)
        setContentView(_binding.root)

        UIUtils.applySystemBarInsets(_binding.root, _binding.layoutInstruction)

        // setup recycle view
        _binding.rvPaging.adapter = _pagingAdapter

        // observer paging data
        lifecycleScope.launch {
            repeatOnLifecycle(Lifecycle.State.STARTED) {
                _viewModel.userFlow.collectLatest { pagingData ->
                    _pagingAdapter.submitData(pagingData)
                }
            }
        }

        // monitor loading
        lifecycleScope.launch {
            repeatOnLifecycle(Lifecycle.State.STARTED) {
                _pagingAdapter.loadStateFlow.collect { loadState ->
                    // 方式 A：監測本地數據源（最常用於純 PagingSource）
                    val isRefreshing = loadState.source.refresh is LoadState.Loading

                    // 方式 B：監測整體的刷新狀態（不論是 source 還是 mediator）
                    // val isRefreshing = loadState.refresh is LoadState.Loading

                    // 當使用了 RemoteMediator（通常用於資料庫 + 網路同步的架構）時，這個欄位才會有值.
                    //val isRefreshing = loadState.mediator.refresh is LoadState.Loading

                    _binding.pbLoading.isVisible = isRefreshing
                }
            }
        }

        // refresh list
        _binding.btnRefresh.setOnClickListener {
            _pagingAdapter.refresh()
        }

    }
}