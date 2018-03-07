package com.tomclaw.drawa.draw

data class Event(val index: Int,
                 val toolType: Int,
                 val color: Int,
                 val size: Int,
                 val x: Int,
                 val y: Int,
                 val action: Int)
