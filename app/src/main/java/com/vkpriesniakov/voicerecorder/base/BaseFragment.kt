package com.vkpriesniakov.voicerecorder.base

import android.content.Context
import android.os.Bundle
import android.view.View
import androidx.fragment.app.Fragment
import androidx.navigation.NavController
import androidx.navigation.Navigation

abstract class BaseFragment : Fragment(), MvpView {

    lateinit var mainNavigator: NavController

    var mActivity: BaseActivity? = null

    override fun onViewCreated(view: View, savedInstanceState: Bundle?) {
        super.onViewCreated(view, savedInstanceState)

        mainNavigator = Navigation.findNavController(view)

    }

    override fun onAttach(context: Context) {
        super.onAttach(context)

        if (context is BaseActivity) {
            val activity:BaseActivity = context
            this.mActivity = activity
            activity.onFragmentAttached()
        }

    }

    override fun onDetach() {
        super.onDetach()
        mActivity = null
    }

    override fun mvpViewFun() {
//        TODO("Not yet implemented")
    }

    interface Callback {
        fun onFragmentAttached()
        fun onFragmentDetached(tag: String?)
    }

}