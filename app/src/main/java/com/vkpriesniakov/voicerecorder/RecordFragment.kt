package com.vkpriesniakov.voicerecorder

import android.content.Context
import android.media.MediaRecorder
import android.os.Bundle
import android.os.SystemClock
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import androidx.activity.result.contract.ActivityResultContracts
import androidx.appcompat.app.AppCompatActivity
import androidx.fragment.app.Fragment
import androidx.navigation.NavController
import androidx.navigation.Navigation
import com.vkpriesniakov.voicerecorder.databinding.FragmentRecordBinding
import com.vkpriesniakov.voicerecorder.utils.FloatingButtonAnimator
import com.vkpriesniakov.voicerecorder.utils.RECORD_PERMISSION
import com.vkpriesniakov.voicerecorder.utils.Utils
import com.vkpriesniakov.voicerecorder.utils.Utils.Companion.checkIfPermissionGranted
import kotlinx.coroutines.GlobalScope
import kotlinx.coroutines.delay
import kotlinx.coroutines.launch
import java.text.SimpleDateFormat
import java.util.*


class RecordFragment : Fragment(), View.OnClickListener {

    private lateinit var mNavController: NavController
    private lateinit var mRecordFile: String
    private var mIsRecording: Boolean = false
    private var mPermissionAllowed = false
    private var mMediaRecorder: MediaRecorder? = null
    private lateinit var mFabAnimator: FloatingButtonAnimator

    private var _binding: FragmentRecordBinding? = null

    // This property is only valid between onCreateView and
// onDestroyView.
    private val bdn get() = _binding!!


    override fun onCreateView(
        inflater: LayoutInflater,
        container: ViewGroup?,
        savedInstanceState: Bundle?
    ): View {
        _binding = FragmentRecordBinding.inflate(inflater, container, false)
        return bdn.root
    }

    override fun onViewCreated(view: View, savedInstanceState: Bundle?) {
        super.onViewCreated(view, savedInstanceState)
        mPermissionAllowed = checkIfPermissionGranted(context)
        mNavController = Navigation.findNavController(view)
        mFabAnimator = FloatingButtonAnimator(bdn.recordFab, context as Context)
        bdn.recordListButton.setOnClickListener(this)
        bdn.recordFab.setOnClickListener(this)
    }

    override fun onClick(v: View?) {
        when (v?.id) {
            R.id.record_list_button -> mNavController.navigate(R.id.action_recordFragment_to_audioListFragment)
            R.id.record_fab -> {
                if (mIsRecording) {

                    stopRecording()
                    mFabAnimator.startStopAnimation()

                    mIsRecording = false
                } else {
                    if (checkPermission()) {

                        startRecording()
                        mFabAnimator.startPlayAnimation()

                        mIsRecording = true
                    }
                }
            }
        }
    }

    private fun startRecording() {

        bdn.recordTimer.base = SystemClock.elapsedRealtime()
        bdn.recordTimer.start()

        val recordPath = activity?.getExternalFilesDir("/")?.absolutePath
        val formatter = SimpleDateFormat(
            "yyyy_MM_dd_hh_mm_ss",
            Locale.getDefault()
        )
        val currentDate = Date()
        mRecordFile = "record_${formatter.format(currentDate)}.3gp"
        mMediaRecorder = MediaRecorder().apply {
            setAudioSource(MediaRecorder.AudioSource.MIC)
            setOutputFormat(MediaRecorder.OutputFormat.THREE_GPP)
            setOutputFile("$recordPath/$mRecordFile")
            setAudioEncoder(MediaRecorder.AudioEncoder.AMR_NB)
            prepare()
            start()
        }
        bdn.recordFilenameText.text = "Recording file: $mRecordFile"
        delayFloatingButton()
    }

    private fun stopRecording() {
        bdn.recordTimer.stop()
        mMediaRecorder?.apply {
            stop()
            release()
        }
        mMediaRecorder = null
        bdn.recordFilenameText.text = "Recording stopped, saved: $mRecordFile"
        delayFloatingButton()
    }

    private fun delayFloatingButton(delayTime: Long = 1000L) {
        // delay for second to prevent MediaRecorder stop error,
        // when user is fast clicking on floating button
        bdn.recordFab.isClickable = false
        GlobalScope.launch {
            delay(delayTime)
            bdn.recordFab.isClickable = true
        }
    }

    private val requestPermissionLauncher =
        registerForActivityResult(
            ActivityResultContracts.RequestPermission()
        ) { isGranted: Boolean ->
            when {
                isGranted -> mPermissionAllowed = true
                !isGranted -> {
                    mPermissionAllowed = false
                    if (!shouldShowRequestPermissionRationale(RECORD_PERMISSION))
//                        showSettingsSnackbar()
                        Utils.showSettingsSnackbar(
                            bdn,
                            activity as AppCompatActivity,
                            activitySettingsResult
                        )
                }
            }
        }

    private fun checkPermission(): Boolean {
        requestPermissionLauncher.launch(RECORD_PERMISSION)
        return mPermissionAllowed
    }

    private val activitySettingsResult =
        registerForActivityResult(ActivityResultContracts.StartActivityForResult()) {
            mPermissionAllowed = checkIfPermissionGranted(context)
        }

}