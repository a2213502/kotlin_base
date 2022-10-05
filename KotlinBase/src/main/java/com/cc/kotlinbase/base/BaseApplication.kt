package com.cc.kotlinbase.base

import android.app.Application
import android.content.Context

class BaseApplication  :Application(){


    override fun attachBaseContext(base: Context?) {
        super.attachBaseContext(base)
    }

}