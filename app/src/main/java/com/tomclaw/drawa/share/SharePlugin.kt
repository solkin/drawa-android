package com.tomclaw.drawa.share

import io.reactivex.Observable
import io.reactivex.Single

interface SharePlugin {

    val weight: Int

    val image: Int

    val title: Int

    val description: Int

    val progress: Observable<Float>

    val operation: Single<ShareResult>

}