package com.cc.kotlinbase.event

/**
 * 事件对象
 */
class Msg @JvmOverloads constructor(
    var code: Int = 0,
    var msg: String = "",
    var arg1: Int = 0,
    var arg2: Int = 0,
    var obj: Any? = null

)