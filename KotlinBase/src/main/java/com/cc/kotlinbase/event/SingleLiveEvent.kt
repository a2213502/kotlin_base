package com.cc.kotlinbase.event

import android.util.Log
import androidx.annotation.MainThread
import androidx.lifecycle.LifecycleOwner
import androidx.lifecycle.MutableLiveData
import androidx.lifecycle.Observer
import java.util.concurrent.atomic.AtomicBoolean

/**
 * A lifecycle-aware observable that sends only new updates after subscription, used for events like
 * navigation and Snackbar messages.
 *
 *
 * This avoids a common problem with events: on configuration change (like rotation) an update
 * can be emitted if the observer is active. This LiveData only calls the observable if there's an
 * explicit call to setValue() or call().
 *
 *
 * Note that only one observer is going to be notified of changes.
 */
class SingleLiveEvent <T> : MutableLiveData<T>(){


    private val mPending = AtomicBoolean(false)

    @MainThread
    override  fun observe(owner: LifecycleOwner,observer: Observer<in T>){

        if(hasActiveObservers()){
            Log.e("SingleLiveEvent","注册了多个观察员，但只有一名观察员会收到更改通知。")

        }
        //observe the internal MutableLiveData

        super.observe(owner, Observer { t->
            if(mPending.compareAndSet(true,false)){
                observer.onChanged(t)

            }

        })

    }


    @MainThread
    override fun setValue(t: T?) {
        mPending.set(true)
        super.setValue(t)
    }

    /**
     * Used for case where T is Void ,to make calls cleaner
     */
    @MainThread
    fun call() {
        value = null
    }
}