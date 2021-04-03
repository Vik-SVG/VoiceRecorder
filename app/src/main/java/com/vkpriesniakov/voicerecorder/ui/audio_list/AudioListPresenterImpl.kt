package com.vkpriesniakov.voicerecorder.ui.audio_list

import android.media.MediaPlayer
import com.vkpriesniakov.voicerecorder.base.BasePresenter
import java.io.File

class AudioListPresenterImpl<V : AudioListView> : BasePresenter<V>(), AudioListPresenter<V> {

    override var mPresenterFile: File? = null

    override var mMediaPlayer: MediaPlayer = MediaPlayer()

    override fun audioListPresenterFun() {
    }

    override fun onSetupBottomSheet() {
        getMvpView()?.setupBottomSheet()
    }

    override fun onPlayAudio() {
        onStartMediaPlayer()
        mPresenterFile?.let { getMvpView()?.playAudio(it) }
    }

    override fun onStopAudio() {
        onStopMediaPlayer()
        getMvpView()?.stopAudio()
    }

    override fun onUpdateJob() {
        getMvpView()?.updateJob()
    }

    override fun onPauseAudio() {
        mMediaPlayer.pause()
        getMvpView()?.pauseAudio()
    }

    override fun onResumeAudio() {
        mMediaPlayer.start()
        getMvpView()?.updateJob()
        getMvpView()?.resumeAudio()
    }

    override fun onStopMediaPlayer() {
        mMediaPlayer.apply {
            stop()
            reset()
//            release()
        }
    }

    override fun onDetachMediaPlayer(isPlaying: Boolean) {
        if (isPlaying) {
            mMediaPlayer.apply {
                stop()
                reset()
            }
        }
    }

    override fun onStartMediaPlayer() {
        mMediaPlayer.apply {
            reset()
            setDataSource(mPresenterFile?.absolutePath)
            prepare()
            start()
        }
    }
}