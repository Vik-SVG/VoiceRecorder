package com.vkpriesniakov.voicerecorder.ui.audio_list

import android.media.MediaPlayer
import com.vkpriesniakov.voicerecorder.base.MvpPresenter
import java.io.File

interface AudioListPresenter<V : AudioListView>:MvpPresenter<V>{

    var mPresenterFile:File?
    var mMediaPlayer: MediaPlayer

    fun audioListPresenterFun()
    fun onSetupBottomSheet()
    fun onPlayAudio()
    fun onStopAudio()
    fun onPauseAudio()
    fun onResumeAudio()
    fun onUpdateJob()
    fun onDetachMediaPlayer(isPlaying: Boolean)
    fun onStopMediaPlayer()
    fun onStartMediaPlayer()
}