package com.cc.kotlinbase.net

import com.cc.kotlinbase.base.IBaseResponse
import java.lang.Exception

/**
 * 网络异常类
 */
open class CCResponseThrowable :Exception{

    var code:Int
    var errorMsg:String

    constructor(error: ERROR,e:Throwable?=null):super(e){

        this.code = error.getCode()
        this.errorMsg  = error.getMessage()

    }

    constructor(code:Int,errorMsg :String,e:Throwable?=null):super(e){

        this.code = code ;
        this.errorMsg = errorMsg;
    }
    constructor(base:IBaseResponse<*>,e:Throwable?=null):super(e){


        this.code = base.code()
        this.errorMsg = base.msg()
    }
}