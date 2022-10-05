package com.cc.kotlinbase.net

/**
 * 错误类型枚举类
 */
enum class ERROR (private val code:Int ,private val errMsg:String) {


    /**
     * 未知错误
     */
    UNKNOWN(1000,"未知错误"),
    /**
     * 解析错误
     */
    PARSE_ERROR(1001,"解析错误"),

    /**
     * 网络错误
     */
    NETWORK_ERROR(1002,"网络错误"),

    /**
     * 协议出错
     */
    HTTP_ERROR(1003,"协议出错"),

    /**
     * 证书错误
     */
    SSL_ERROR(1004,"证书错误"),

    /**
     * 链接超时
     */
    TIMEOUT_ERROR(1006,"链接超时");

    fun getMessage():String{
        return errMsg
    }

    fun getCode():Int{
        return code
    }
}