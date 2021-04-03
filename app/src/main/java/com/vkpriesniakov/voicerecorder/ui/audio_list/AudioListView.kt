package com.vkpriesniakov.voicerecorder.ui.audio_list

import com.vkpriesniakov.voicerecorder.base.MvpView
import java.io.File

interface AudioListView : MvpView {

    fun setupBottomSheet()
    fun playAudio(file: File)
    fun stopAudio()
    fun pauseAudio()
    fun resumeAudio()
    fun updateJob()
}