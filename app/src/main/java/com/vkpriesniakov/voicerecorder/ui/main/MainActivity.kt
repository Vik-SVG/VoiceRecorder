package com.vkpriesniakov.voicerecorder.ui.main

import android.os.Bundle
import com.vkpriesniakov.voicerecorder.R
import com.vkpriesniakov.voicerecorder.base.BaseActivity
import dagger.hilt.android.AndroidEntryPoint


@AndroidEntryPoint
class MainActivity : BaseActivity(), MainMvpView  {

    lateinit var mPresenter:MainMvpPresenter<MainMvpView>

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        setContentView(R.layout.activity_main)

        mPresenter = MainMvpPresenterImpl<MainMvpView>()
        mPresenter.onAttachPresenterFun(this)

    }

    override fun onDestroy() {
        super.onDestroy()
        mPresenter.onDetachPresenterFun()
    }

    override fun mainMvpViewFun() {
//        TODO("Not yet implemented")
    }
}