package com.vkpriesniakov.voicerecorder.di

import androidx.fragment.app.Fragment
import com.vkpriesniakov.voicerecorder.utils.FloatingButtonAnimator
import com.vkpriesniakov.voicerecorder.utils.RecordController
import com.vkpriesniakov.voicerecorder.utils.Utils
import org.kodein.di.*
import org.kodein.di.android.x.androidXContextTranslators
import org.kodein.di.bindings.WeakContextScope

val kodeinContainer = DI.Module {
    import(androidXContextTranslators)
    bind<RecordController>() with singleton { RecordController() }
    bind<FloatingButtonAnimator>() with scoped(WeakContextScope.of<Fragment>()).singleton {
        FloatingButtonAnimator(
            context.requireContext()
        )
    }
    bind<Boolean>(tag = "storage_permission") with scoped(WeakContextScope.of<Fragment>()).provider {
        Utils.checkIfPermissionGranted(
            context.context
        )
    }
}