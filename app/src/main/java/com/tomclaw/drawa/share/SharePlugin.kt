package com.tomclaw.drawa.share

import io.reactivex.Single

interface SharePlugin {

    val weight: Int

    val image: Int

    val title: Int

    val description: Int

    val operation: Single<ShareResult>

}