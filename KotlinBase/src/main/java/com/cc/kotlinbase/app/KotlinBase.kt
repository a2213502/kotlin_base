package com.cc.kotlinbase.app

import androidx.lifecycle.ViewModelProvider
import com.cc.kotlinbase.base.ViewModelFactory

object KotlinBase {

    fun viewModelFactory(): ViewModelProvider.Factory? = ViewModelFactory.getInstance()

}