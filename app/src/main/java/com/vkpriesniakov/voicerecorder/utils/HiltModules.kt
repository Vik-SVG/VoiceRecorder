package com.vkpriesniakov.voicerecorder.utils

import android.content.Context
import android.media.MediaRecorder
import dagger.Module
import dagger.Provides
import dagger.hilt.InstallIn
import dagger.hilt.android.components.FragmentComponent
import dagger.hilt.android.qualifiers.ApplicationContext
import dagger.hilt.android.scopes.FragmentScoped
import java.lang.ref.WeakReference


@Module
@InstallIn(FragmentComponent::class)
object BaseModule {

    @FragmentScoped
    @Provides
    fun provideFabAnimator(@ApplicationContext context: Context): AnimationControl {
        return FloatingButtonAnimator(context)
    }


    @FragmentScoped
    @Provides
    fun provideMediaRec(): WeakReference<MediaRecorder> {
        return WeakReference(MediaRecorder())
    }

}

/*@Module
@InstallIn(FragmentComponent::class)
abstract class mediaProvider{

    @FragmentScoped
    @Binds
    abstract fun provideMediaRecorder(mediaRecorder: MediaRecorder):MediaRecorder
}*/
