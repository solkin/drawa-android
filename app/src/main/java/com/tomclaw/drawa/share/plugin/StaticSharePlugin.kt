package com.tomclaw.drawa.share.plugin

import com.tomclaw.drawa.R
import com.tomclaw.drawa.share.SharePlugin
import io.reactivex.Observable
import java.io.File

class StaticSharePlugin : SharePlugin {

    override val image: Int
        get() = R.drawable.image
    override val title: Int
        get() = R.string.static_share_title
    override val description: Int
        get() = R.string.static_share_description

    override val operation: Observable<File> = Observable.empty()

}