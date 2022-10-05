package com.cc.kotlinbase.base

import android.os.Bundle
import androidx.appcompat.app.AppCompatActivity
import androidx.databinding.DataBindingUtil
import androidx.databinding.ViewDataBinding
import androidx.lifecycle.Observer
import androidx.lifecycle.ViewModelProvider
import com.afollestad.materialdialogs.MaterialDialog
import com.afollestad.materialdialogs.customview.customView
import com.afollestad.materialdialogs.lifecycle.lifecycleOwner
import com.blankj.utilcode.util.ToastUtils
import com.cc.kotlinbase.R
import com.cc.kotlinbase.app.KotlinBase
import com.cc.kotlinbase.event.Msg
import java.lang.reflect.ParameterizedType

abstract class BaseActivity<VM :BaseViewModel ,DB : ViewDataBinding> :AppCompatActivity(){


    private lateinit var viewModel:VM

    private var mBinding:DB? = null

    private var dialog: MaterialDialog? = null

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)

        initViewDataBinding()
        lifecycle.addObserver(viewModel)

        registDefaultUIEvent()

        initView(savedInstanceState)
        initData()

    }
    /**
     * 获取界面布局
     * @return Int 布局资源Id
     */
    abstract fun getLayoutId() :Int

    /**
     * 初始化控件
     */
    abstract fun initView(savedInstanceState: Bundle?)

    /**
     * 初始化数据
     */
    abstract fun initData()






    /**
     *  注册默认UI事件
     */
    private fun registDefaultUIEvent() {

        viewModel.defaultUI.showDialog.observe(this, Observer {
            showLoading()
        })


        viewModel.defaultUI.dismissDialog.observe(this, Observer {

            dismissLoading()
        })

        viewModel.defaultUI.toastEvent.observe(this, Observer {

            ToastUtils.showShort(it)
        })

        viewModel.defaultUI.msgEvent.observe(this, Observer {
            handleEvent(it)
        })
    }





    /**
     * 初始化databinding
     */
    private fun initViewDataBinding() {
        val clazz = (javaClass.genericSuperclass as ParameterizedType).actualTypeArguments[1]as Class<*>


        if (clazz != ViewDataBinding::class.java && ViewDataBinding::class.java.isAssignableFrom(clazz)) {


            mBinding = DataBindingUtil.setContentView(this,getLayoutId())

            mBinding?.lifecycleOwner = this
        }else{
            setContentView(getLayoutId())
        }
        createViewModel()
    }


    /**
     * viewModel与Activity通信的方法
     *
     * eg: defUI.msgEvent.postValue(Msg(0, obj = item))
     * @param msg Msg
     */
    open fun handleEvent(msg:Msg){

    }
    /**
     * 取消加载弹窗
     */
    private fun dismissLoading() {

        dialog?.run { if(isShowing) dismiss() }
    }

    /**
     * 显示加载弹窗
     */
    private fun showLoading() {

        (dialog?: MaterialDialog(this))
            .cancelable(false)
            .cornerRadius(8f)
            .customView(R.layout.dialog_loading)
            .lifecycleOwner(this)
            .maxWidth(R.dimen.dialog_width)
            .show()

    }
    /**
     * 创建ViewModel
     */
    private fun createViewModel() {
        val type = javaClass.genericSuperclass
        if( type is ParameterizedType){
            val tClass =  type.actualTypeArguments[0] as? Class<VM>?: BaseViewModel::class.java
            viewModel = ViewModelProvider(viewModelStore,defaultViewModelProviderFactory).get(tClass) as VM
        }
    }
    override fun getDefaultViewModelProviderFactory(): ViewModelProvider.Factory {
        return KotlinBase.viewModelFactory()?:super.getDefaultViewModelProviderFactory()
    }

}