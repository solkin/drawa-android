package com.tomclaw.drawa.share

import io.reactivex.Single
import java.io.File

interface SharePlugin {

    val image: Int

    val title: Int

    val description: Int

    val operation: Single<File>

}