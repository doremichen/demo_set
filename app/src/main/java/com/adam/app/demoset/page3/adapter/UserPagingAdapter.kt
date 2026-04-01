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

package com.adam.app.demoset.page3.adapter

import android.view.LayoutInflater
import android.view.ViewGroup
import androidx.paging.PagingDataAdapter
import androidx.recyclerview.widget.DiffUtil
import androidx.recyclerview.widget.RecyclerView
import com.adam.app.demoset.databinding.ItemUserRowBinding
import com.adam.app.demoset.page3.model.User

class UserPagingAdapter: PagingDataAdapter<User, UserPagingAdapter.UserViewHolder>(UserDiffCallback) {
    override fun onCreateViewHolder(
        p0: ViewGroup,
        p1: Int
    ): UserViewHolder {
        // view binding
        val binding = ItemUserRowBinding.inflate(LayoutInflater.from(p0.context), p0, false)
        return UserViewHolder(binding)
    }

    override fun onBindViewHolder(
        p0: UserViewHolder,
        p1: Int
    ) {
        val user = getItem(p1)
        user?.let {
            p0.bind(it)
        }
    }


    // view holder
    class UserViewHolder(private val binding: ItemUserRowBinding) : RecyclerView.ViewHolder(binding.root) {
        // bind
        fun bind(user: User) {
            binding.user = user
            binding.executePendingBindings()

//            binding.tvName.text = user.name
//            binding.tvDetail.text = user.detail
        }
    }

    object UserDiffCallback : DiffUtil.ItemCallback<User>() {
        override fun areItemsTheSame(
            p0: User,
            p1: User
        ): Boolean {
            return p0.id == p1.id
        }

        override fun areContentsTheSame(
            p0: User,
            p1: User
        ): Boolean {
            return p0 == p1
        }

    }

}