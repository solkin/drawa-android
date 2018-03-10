package com.tomclaw.drawa.draw

import com.tomclaw.drawa.draw.tools.Tool

interface ToolProvider {

    fun getTool(type: Int): Tool

    fun listTools(): List<Tool>

}

class ToolProviderImpl(toolSet: Set<Tool>) : ToolProvider {

    private val tools = HashMap<Int, Tool>()

    init {
        toolSet.forEach { tool ->
            tools[tool.type] = tool
        }
    }

    override fun getTool(type: Int): Tool = tools[type]
            ?: throw IllegalArgumentException("No tool found for type $type")

    override fun listTools(): List<Tool> = ArrayList(tools.values)

}
