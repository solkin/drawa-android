package com.tomclaw.drawa.draw

import android.util.SparseArray
import com.tomclaw.drawa.draw.tools.Tool

interface ToolProvider {

    fun getTool(type: Int): Tool

}

class ToolProviderImpl(toolSet: Set<Tool>) : ToolProvider {

    private val tools: SparseArray<Tool> = SparseArray()

    init {
        toolSet.forEach { tool ->
            tools.append(tool.type, tool)
        }
    }

    override fun getTool(type: Int) = tools[type]

}
