package com.vkpriesniakov.voicerecorder.base

import androidx.appcompat.app.AppCompatActivity

abstract class BaseActivity : AppCompatActivity(), MvpView, BaseFragment.Callback {

    override fun mvpViewFun() {
//        TODO("Not yet implemented")
    }

    override fun onFragmentAttached() {
//        TODO("Not yet implemented")
    }



    override fun onFragmentDetached(tag: String?) {
//        TODO("Not yet implemented")
    }


}