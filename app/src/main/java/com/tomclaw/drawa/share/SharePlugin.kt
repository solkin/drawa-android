package com.tomclaw.drawa.share

import io.reactivex.Observable
import java.io.File

interface SharePlugin {

    val image: Int

    val title: Int

    val description: Int

    val operation: Observable<File>

}