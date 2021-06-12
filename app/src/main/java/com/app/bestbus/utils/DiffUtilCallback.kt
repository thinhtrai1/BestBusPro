package com.app.bestbus.utils

import androidx.recyclerview.widget.DiffUtil

class DiffUtilCallback<T>(private val primaryKey: T.() -> Any?) : DiffUtil.ItemCallback<T>() {
    override fun areItemsTheSame(oldItem: T, newItem: T): Boolean {
        return oldItem.primaryKey() == newItem.primaryKey()
    }

    override fun areContentsTheSame(oldItem: T, newItem: T): Boolean {
        return oldItem == newItem
    }
}