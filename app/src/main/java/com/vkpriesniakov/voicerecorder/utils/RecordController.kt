package com.vkpriesniakov.voicerecorder.utils

import android.media.MediaRecorder
import android.os.SystemClock
import android.widget.Chronometer
import com.google.android.material.floatingactionbutton.FloatingActionButton
import com.vkpriesniakov.voicerecorder.databinding.FragmentRecordBinding
import kotlinx.coroutines.GlobalScope
import kotlinx.coroutines.coroutineScope
import kotlinx.coroutines.delay
import kotlinx.coroutines.launch
import java.text.SimpleDateFormat
import java.util.*
import kotlin.coroutines.coroutineContext

interface RecordControllerInterface {
    fun startRecording(bdn: FragmentRecordBinding)
    fun stopRecording(bdn: FragmentRecordBinding)
    fun startingDelay(delayTime: Long, recordFab:FloatingActionButton)
    fun startTimer(recordTimer:Chronometer)
    fun stopTimer(recordTimer:Chronometer)
}

class RecordController : RecordControllerInterface {

    private var mMediaRecorder:MediaRecorder?
    private var mRecordFile:String?
    init {
        mMediaRecorder = MediaRecorder()
        mRecordFile = ""
    }



    override fun startRecording(bdn: FragmentRecordBinding) {
        startTimer(bdn.recordTimer)
        val recordPath = bdn.root.context?.getExternalFilesDir("/")?.absolutePath
        val formatter = SimpleDateFormat(
            "yyyy_MM_dd_hh_mm_ss",
            Locale.getDefault()
        )
        val currentDate = Date()
        mRecordFile = "record_${formatter.format(currentDate)}.3gp"
        mMediaRecorder?.apply {
            setAudioSource(MediaRecorder.AudioSource.MIC)
            setOutputFormat(MediaRecorder.OutputFormat.THREE_GPP)
            setOutputFile("$recordPath/$mRecordFile")
            setAudioEncoder(MediaRecorder.AudioEncoder.AMR_NB)
            prepare()
            start()
        }
        bdn.recordFilenameText.text = "Recording file: $mRecordFile"
        startingDelay(1000L, bdn.recordFab)
    }

    override fun stopRecording(bdn: FragmentRecordBinding) {
        stopTimer(bdn.recordTimer)
        mMediaRecorder?.apply {
            stop()
            release()
        }
        mMediaRecorder = null
        bdn.recordFilenameText.text = "Recording stopped, saved: $mRecordFile"
        startingDelay(1000L,bdn.recordFab)
    }

    override fun startingDelay(delayTime: Long, recordFab:FloatingActionButton) {
        // delay for second to prevent MediaRecorder stop error,
        // when user is fast clicking on floating button
        recordFab.isClickable = false
        GlobalScope.launch {
            delay(delayTime)
            recordFab.isClickable = true
        }
    }

    override fun startTimer(recordTimer:Chronometer) {
        recordTimer.base = SystemClock.elapsedRealtime()
        recordTimer.start()
    }

    override fun stopTimer(recordTimer:Chronometer) {
        recordTimer.stop()
    }
}