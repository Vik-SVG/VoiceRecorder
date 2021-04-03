package com.vkpriesniakov.voicerecorder.base

open class BasePresenter<V : MvpView> : MvpPresenter<V> {

    private var mMvpView: V? = null
    override fun onAttachPresenterFun(mvpView: V) {
        mMvpView = mvpView
    }

    fun isViewAttached(): Boolean = mMvpView != null

    fun getMvpView(): V? = mMvpView

    override fun onDetachPresenterFun() {
        mMvpView = null
    }
}