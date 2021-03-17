package com.vkpriesniakov.voicerecorder

import android.app.Application
import android.content.Context
import com.vkpriesniakov.voicerecorder.utils.FloatingButtonAnimator
import com.vkpriesniakov.voicerecorder.utils.RecordController
import org.kodein.di.DI
import org.kodein.di.DIAware
import org.kodein.di.android.androidCoreContextTranslators
import org.kodein.di.bind
import org.kodein.di.singleton

class MyApp : Application(), DIAware {
    override val di by DI.lazy {

        val diContainer = DI{

            bind() from singleton { RecordController() }

        }
    }
}