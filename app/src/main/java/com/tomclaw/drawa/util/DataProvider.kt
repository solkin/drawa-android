package com.tomclaw.drawa.util

import java.util.*
import java.util.Collections.emptyList

class DataProvider<A> {

    private var data: List<A> = emptyList()

    fun getItem(position: Int): A = data[position]

    fun size() = data.size

    fun setData(data: List<A>) {
        this.data = ArrayList(data)
    }

}
