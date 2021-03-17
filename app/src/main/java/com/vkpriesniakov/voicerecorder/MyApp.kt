package com.vkpriesniakov.voicerecorder

import android.app.Application
import com.vkpriesniakov.voicerecorder.di.kodeinContainer
import org.kodein.di.DI
import org.kodein.di.DIAware
import org.kodein.di.android.x.androidXModule

class MyApp : Application(), DIAware {
    override val di by DI.lazy {
        import(kodeinContainer)
        import(androidXModule(this@MyApp))
    }
}