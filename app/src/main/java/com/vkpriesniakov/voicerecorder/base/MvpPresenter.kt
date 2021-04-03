package com.vkpriesniakov.voicerecorder.base

interface MvpPresenter<V : MvpView> {

    fun onAttachPresenterFun(mvpView:V)

    fun onDetachPresenterFun()

}