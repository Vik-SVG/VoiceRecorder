package com.vkpriesniakov.voicerecorder.di

import android.content.Context
import android.media.MediaPlayer
import android.media.MediaRecorder
import com.vkpriesniakov.voicerecorder.utils.AnimationControl
import com.vkpriesniakov.voicerecorder.utils.FloatingButtonAnimator
import com.vkpriesniakov.voicerecorder.utils.TimeAgo
import dagger.Module
import dagger.Provides
import dagger.hilt.InstallIn
import dagger.hilt.android.components.FragmentComponent
import dagger.hilt.android.qualifiers.ActivityContext
import dagger.hilt.android.qualifiers.ApplicationContext
import dagger.hilt.android.scopes.FragmentScoped
import java.io.File
import javax.inject.Qualifier


@Module
@InstallIn(FragmentComponent::class)
object BaseModule {

    @FragmentScoped
    @Provides
    fun provideFabAnimator(@ApplicationContext context: Context): AnimationControl =
        FloatingButtonAnimator(context)

    @FragmentScoped
    @Provides
    fun provideAllFiles(@ActivityContext activity: Context): Array<out File> {
        val path: String = activity.getExternalFilesDir("/")?.absolutePath ?: "/"
        return File(path).listFiles()
    }

    @Provides
    fun provideMediaPlayer():MediaPlayer = MediaPlayer()

    @Provides
    fun provideTimeAgo():TimeAgo = TimeAgo()

    @Provides
    fun provideMediaRec(): MediaRecorder = MediaRecorder()


/*
    @Provides
    fun provideTimeAgo():TimeAgo{
        return TimeAgo()
    }*/
}

/*@Module
@InstallIn(FragmentComponent::class)
abstract class mediaProvider{

    @FragmentScoped
    @Binds
    abstract fun provideMediaRecorder(mediaRecorder: MediaRecorder):MediaRecorder
}*/

@Qualifier
@Retention(AnnotationRetention.BINARY)
annotation class PermissionBinder
