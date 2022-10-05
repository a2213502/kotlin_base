package com.cc.kotlinbase.base

interface IBaseResponse<T> {
    fun code():Int
    fun msg():String
    fun data():T
    fun isSuccess():Boolean

}