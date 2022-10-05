package com.cc.kotlinbase.app

import androidx.lifecycle.ViewModelProvider
import com.cc.kotlinbase.base.ViewModelFactory

interface GlobalConfig {

    fun viewModelFactory():ViewModelProvider.Factory?= ViewModelFactory.getInstance()
}