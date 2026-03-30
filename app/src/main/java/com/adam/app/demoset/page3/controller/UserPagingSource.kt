/**
 * Copyright (C) 2026 Adam Chen. All rights reserved.
 * <p>
 * Description: This class is the controller of page3
 * </p>
 * @author Adam Chen
 * @version 1.0 - 2026/03/30
 */
package com.adam.app.demoset.page3.controller

import androidx.paging.PagingSource
import androidx.paging.PagingState
import com.adam.app.demoset.page3.model.User
import kotlinx.coroutines.delay

class UserPagingSource : PagingSource<Int, User>() {
    override suspend fun load(params: LoadParams<Int>): LoadResult<Int, User> {
        return try {
            val currentPage = params.key ?: 1

            // monitor delay 2000ms
            delay(2000)

            // data
            val lastId = (currentPage - 1) * params.loadSize + 1
            val data = (1..params.loadSize).map {
                User(
                    id = lastId + it,
                    name = "User ${lastId + it}",
                    detail = "Page $currentPage Item $it"
                )
            }

            // return result
            LoadResult.Page(
                data = data,
                prevKey = if (currentPage == 1) null else currentPage - 1,
                nextKey = if (data.isEmpty()) null else currentPage + 1
            )

        } catch (e: Exception) {
            LoadResult.Error(e)
        }

    }

    override fun getRefreshKey(state: PagingState<Int, User>): Int? {
        return state.anchorPosition?.let { anchorPosition ->
            state.closestPageToPosition(anchorPosition)?.prevKey?.plus(1)
                ?: state.closestPageToPosition(anchorPosition)?.nextKey?.minus(1)
        }
    }
}