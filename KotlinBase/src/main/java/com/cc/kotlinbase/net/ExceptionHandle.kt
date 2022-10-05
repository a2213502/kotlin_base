package com.cc.kotlinbase.net

import android.net.ParseException
import com.google.gson.JsonParseException
import com.google.gson.stream.MalformedJsonException
import org.json.JSONException
import retrofit2.HttpException
import java.net.ConnectException

/**
 * 异常处理类
 */
object ExceptionHandle {


    /**
     * 异常统一处理成cc异常
     */
    fun handleException(e:Throwable) :CCResponseThrowable{


        val ex: CCResponseThrowable
        if (e is CCResponseThrowable) {
            ex = e
        } else if (e is HttpException) {
            ex = CCResponseThrowable(ERROR.HTTP_ERROR, e)
        } else if (e is JsonParseException
            || e is JSONException
            || e is ParseException || e is MalformedJsonException
        ) {
            ex = CCResponseThrowable(ERROR.PARSE_ERROR, e)
        } else if (e is ConnectException) {
            ex = CCResponseThrowable(ERROR.NETWORK_ERROR, e)
        } else if (e is javax.net.ssl.SSLException) {
            ex = CCResponseThrowable(ERROR.SSL_ERROR, e)
        } else if (e is java.net.SocketTimeoutException) {
            ex = CCResponseThrowable(ERROR.TIMEOUT_ERROR, e)
        } else if (e is java.net.UnknownHostException) {
            ex = CCResponseThrowable(ERROR.TIMEOUT_ERROR, e)
        } else {
            ex = if (!e.message.isNullOrEmpty()) CCResponseThrowable(ERROR.UNKNOWN.getCode(), e.message!!, e)
            else CCResponseThrowable(ERROR.UNKNOWN, e)
        }
        return ex

    }

}