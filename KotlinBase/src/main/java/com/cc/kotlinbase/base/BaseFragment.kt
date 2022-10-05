package com.cc.kotlinbase.base

import android.os.Bundle
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import androidx.databinding.DataBindingUtil
import androidx.databinding.ViewDataBinding
import androidx.fragment.app.Fragment
import androidx.lifecycle.Lifecycle
import androidx.lifecycle.Observer
import androidx.lifecycle.ViewModelProvider
import androidx.lifecycle.ViewModelStore
import com.afollestad.materialdialogs.MaterialDialog
import com.afollestad.materialdialogs.customview.customView
import com.afollestad.materialdialogs.lifecycle.lifecycleOwner
import com.blankj.utilcode.util.LogUtils
import com.blankj.utilcode.util.ToastUtils
import com.cc.kotlinbase.R
import com.cc.kotlinbase.app.KotlinBase
import com.cc.kotlinbase.event.Msg
import java.lang.reflect.ParameterizedType

abstract class BaseFragment<VM:BaseViewModel,DB:ViewDataBinding> : Fragment() {



    private lateinit var viewModel: VM
    private var mBinding:DB? = null

    //是否第一次加载

    private var isFirst:Boolean = true

    private var dialog:MaterialDialog?=null



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
     * 懒加载
     */
    open fun lazyLoadData() {}

    override fun onCreateView(
        inflater: LayoutInflater,
        container: ViewGroup?,
        savedInstanceState: Bundle?
    ): View? {

        val clazz = (javaClass.genericSuperclass as ParameterizedType).actualTypeArguments[1] as Class<*>

        if(clazz!= ViewDataBinding::class.java &&
                ViewDataBinding::class.java.isAssignableFrom(clazz)){

            mBinding = DataBindingUtil.inflate(inflater,getLayoutId(),container,false)
            return mBinding?.root
        }


        return inflater.inflate(getLayoutId(),container,false)
    }

    override fun onViewCreated(view: View, savedInstanceState: Bundle?) {
        super.onViewCreated(view, savedInstanceState)
        onVisible()
        createViewModel()
        lifecycle.addObserver(viewModel)
        //注册 UI事件
        registDefaultUIEvent()
        initView(savedInstanceState)
        initData()
    }

    override fun onResume() {
        super.onResume()
        onVisible()
    }



    /**
     * 是否需要懒加载
     */
    private fun onVisible() {
        if (lifecycle.currentState == Lifecycle.State.STARTED && isFirst) {
            lazyLoadData()
        }

    }
    /**
     *  注册默认UI事件
     */
    private fun registDefaultUIEvent() {

        viewModel.defaultUI.showDialog.observe(viewLifecycleOwner, Observer {
            showLoading()
        })


        viewModel.defaultUI.dismissDialog.observe(viewLifecycleOwner, Observer {

            dismissLoading()
        })

        viewModel.defaultUI.toastEvent.observe(viewLifecycleOwner, Observer {

            ToastUtils.showShort(it)
        })

        viewModel.defaultUI.msgEvent.observe(viewLifecycleOwner, Observer {
            handleEvent(it)
        })
    }

    /**
     * viewModel与Activity通信的方法
     *
     * eg: defUI.msgEvent.postValue(Msg(0, obj = item))
     * @param msg Msg
     */
    open fun handleEvent(msg: Msg){

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

        if(dialog ==null){

            dialog = context?.let {

                MaterialDialog(it)
                .cancelable(false)
                .cornerRadius(8f)
                .customView(R.layout.dialog_loading)
                .lifecycleOwner(this)
                .maxWidth(R.dimen.dialog_width)

            }

        }

        dialog?.show()
    }
    /**
     * 创建 ViewModel
     *
     * 共享 ViewModel的时候，重写  Fragmnt 的 getViewModelStore() 方法，
     * 返回 activity 的  ViewModelStore 或者 父 Fragmnt 的 ViewModelStore
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