package com.tomclaw.drawa.draw

import com.tomclaw.drawa.draw.tools.Tool

interface ToolProvider {

    fun getTool(type: Int) : Tool

}

class ToolProviderImpl() : ToolProvider {

    override fun getTool(type: Int): Tool {
        TODO("not implemented")
    }

}
