package com.cc.kotlinbase.base

import android.app.Application
import androidx.lifecycle.AndroidViewModel
import androidx.lifecycle.LifecycleObserver
import androidx.lifecycle.viewModelScope
import com.blankj.utilcode.util.Utils
import com.cc.kotlinbase.app.KotlinBase
import com.cc.kotlinbase.event.Msg
import com.cc.kotlinbase.event.SingleLiveEvent
import com.cc.kotlinbase.net.CCResponseThrowable
import com.cc.kotlinbase.net.ExceptionHandle
import kotlinx.coroutines.*
import kotlinx.coroutines.flow.Flow
import kotlinx.coroutines.flow.flow

/**
 * ViewModel 基类
 */
open class BaseViewModel(application: Application = Utils.getApp()) : AndroidViewModel(application),
    LifecycleObserver {


    val defaultUI: UIChange by lazy { UIChange() }

    /**
     * 所有网络请求都在viewModelScope域中启动,当页面销毁时会自动调用viewModel的 #onCleared() 方法取消所有协程
     */
    fun launchUI(block: suspend CoroutineScope.() -> Unit) = viewModelScope.launch { block() }

    /**
     * 用流的方式进行网络请求
     */
    fun <T> launchFlow(block: suspend () -> T): Flow<T> {

        return flow {
            emit(block())
        }

    }


    /**
     * 检查请求结果,是否是服务器正确返回,否则抛异常
     * @param block  请求代码块
     * @param success 成功回调
     * @param error 失败回调
     * @param complete 完成回调
     * @param isShowDialog 是否显示加载框
     */
    fun <T> launchForCheck(
        block: suspend CoroutineScope.() -> IBaseResponse<T>,
        success: (T) -> Unit,
        error: (CCResponseThrowable) -> Unit = {
            defaultUI.toastEvent.postValue("${it.code}:${it.errorMsg}")
        },
        complete: () -> Unit = {},
        isShowDialog: Boolean = true
    ) {
        if (isShowDialog) defaultUI.showDialog.call()

        launchUI {
            handleException(
                block = {
                    withContext(Dispatchers.IO) {
                        block().let {
                            if (it.isSuccess()) it.data()
                            else throw CCResponseThrowable(it.code(), it.msg())
                        }
                    }.also {
                        success(it)
                    }
                },
                error = {
                    error(it)
                },
                complete = {
                    defaultUI.dismissDialog.call()
                    complete()
                })

        }

    }

    /**
     * 不关心请求结果
     * @param block  请求代码块
     * @param error  失败回调
     * @param complete  完成回调(不管请求成功或者失败都会调用)
     * @param isShowDialog Boolean 是否显示加载框
     */
    fun launchByPass(
        block: suspend CoroutineScope.() -> Unit,
        error: suspend CoroutineScope.(CCResponseThrowable) -> Unit = {
            defaultUI.toastEvent.postValue("${it.code}:${it.message}")
        },
        complete: suspend CoroutineScope.() -> Unit = {},
        isShowDialog: Boolean = true
    ) {
        if (isShowDialog) defaultUI.showDialog.call()
        launchUI {

            handleException(
                block = withContext(Dispatchers.IO) { block },
                error = { error(it) },
                complete = {
                    defaultUI.dismissDialog.call()
                    complete()
                }
            )
        }

    }

    /**
     * 异常统一处理
     */

    private suspend fun handleException(
        block: suspend CoroutineScope.() -> Unit,
        error: suspend CoroutineScope.(CCResponseThrowable) -> Unit,
        complete: suspend CoroutineScope.() -> Unit
    ) {
        coroutineScope {
            try {
                block()
            } catch (e: Throwable) {
                error(ExceptionHandle.handleException(e))
            } finally {

                complete()
            }

        }

    }

    /**
     * UI事件
     */
    inner class UIChange {
        /**
         * 展示弹窗
         */
        val showDialog by lazy { SingleLiveEvent<String>() }

        /**
         * 取消弹窗
         */
        val dismissDialog by lazy { SingleLiveEvent<Void>() }

        /**
         * 吐司
         */
        val toastEvent by lazy { SingleLiveEvent<String>() }

        /**
         * 发送事件
         */
        val msgEvent by lazy { SingleLiveEvent<Msg>() }
    }
}





