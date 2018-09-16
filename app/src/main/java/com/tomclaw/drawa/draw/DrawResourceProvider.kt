package com.tomclaw.drawa.draw

import android.content.res.Resources
import com.tomclaw.drawa.R

interface DrawResourceProvider {

    val defaultColor: Int

}

class DrawResourceProviderImpl(val resources: Resources) : DrawResourceProvider {

    override val defaultColor = resources.getColor(R.color.color10)

}