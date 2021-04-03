package com.vkpriesniakov.voicerecorder.ui.main

import android.content.Context
import com.vkpriesniakov.voicerecorder.base.BasePresenter

class MainMvpPresenterImpl <V:MainMvpView>(): BasePresenter<V>(), MainMvpPresenter<V>{



    override fun mainMvpPresenterFun() {
        getMvpView()?.mainMvpViewFun()

    }



}