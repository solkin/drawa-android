package com.tomclaw.drawa.share

import io.reactivex.Observable
import java.io.File

interface ShareTypePlugin {

    val image: Int

    val title: Int

    val description: Int

    fun getOperation(): Observable<File>

}