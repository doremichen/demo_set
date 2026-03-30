/**
 * Copyright (C) 2026 Adam Chen. All rights reserved.
 * <p>
 * Description: This class is the adapter of user paging list
 * </p>
 * @author Adam Chen
 * @version 1.0 - 2026/03/30
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