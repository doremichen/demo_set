/**
 * Copyright (C) 2026 Adam Chen. All rights reserved.
 * <p>
 * Description: This class is the view model of page3
 * </p>
 * @author Adam Chen
 * @version 1.0 - 2026/03/30
 */
package com.adam.app.demoset.page3.viewmodel

import androidx.lifecycle.ViewModel
import androidx.lifecycle.viewModelScope
import androidx.paging.Pager
import androidx.paging.PagingConfig
import androidx.paging.PagingData
import androidx.paging.cachedIn
import com.adam.app.demoset.page3.controller.UserPagingSource
import com.adam.app.demoset.page3.model.User
import kotlinx.coroutines.flow.Flow

class PagingLabViewModel : ViewModel() {

    val userFlow: Flow<PagingData<User>> = Pager(
        config = PagingConfig(
            pageSize = 20,
            enablePlaceholders = false,
            initialLoadSize = 20
        ),
        pagingSourceFactory = { UserPagingSource() }
    ).flow.cachedIn(viewModelScope)

}