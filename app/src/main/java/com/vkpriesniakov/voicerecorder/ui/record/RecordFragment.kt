package com.vkpriesniakov.voicerecorder.ui.record

import android.annotation.SuppressLint
import android.os.Bundle
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import androidx.activity.result.contract.ActivityResultContracts
import androidx.appcompat.app.AppCompatActivity
import androidx.lifecycle.lifecycleScope
import com.vkpriesniakov.voicerecorder.R
import com.vkpriesniakov.voicerecorder.base.BaseFragment
import com.vkpriesniakov.voicerecorder.databinding.FragmentRecordBinding
import com.vkpriesniakov.voicerecorder.ui.audio_list.AudioListPresenter
import com.vkpriesniakov.voicerecorder.ui.audio_list.AudioListView
import com.vkpriesniakov.voicerecorder.utils.*
import com.vkpriesniakov.voicerecorder.utils.Utils.Companion.checkIfPermissionGranted
import dagger.hilt.android.AndroidEntryPoint
import kotlinx.coroutines.launch
import javax.inject.Inject

@AndroidEntryPoint
class RecordFragment : BaseFragment(), View.OnClickListener{


    @Inject lateinit var recordController:RecordController
    @Inject lateinit var floatingButtonAnimator:AnimationControl


    private lateinit var mRecordFile: String
    private var mIsRecording: Boolean = false
    private var mPermissionAllowed = false

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

    @SuppressLint("UnsafeExperimentalUsageError")
    override fun onViewCreated(view: View, savedInstanceState: Bundle?) {
        super.onViewCreated(view, savedInstanceState)
        mPermissionAllowed = checkIfPermissionGranted(context)
        bdn.recordListButton.setOnClickListener(this)
        bdn.recordFab.setOnClickListener(this)



    }

    override fun onClick(v: View?) {
        when (v?.id) {
            R.id.record_list_button -> mainNavigator.navigate(R.id.action_recordFragment_to_audioListFragment)
            R.id.record_fab -> {
                this.lifecycleScope.launch {
                    makeRecord()
                }
            }
        }
    }

    private fun makeRecord() {
        if (mIsRecording) {
            recordController.stopRecording(bdn)
            floatingButtonAnimator.startStopAnimation(bdn.recordFab)
            mIsRecording = false
        } else {
            if (checkPermission()) {
                recordController.startRecording(bdn)
                floatingButtonAnimator.startPlayAnimation(bdn.recordFab)
                mIsRecording = true
            }
        }
    }

    override fun onDestroyView() {
        super.onDestroyView()
        if (mIsRecording) {
            recordController.stopRecording(bdn)
        }
        mIsRecording = false
    }

    override fun onStop() {
        super.onStop()
        if (mIsRecording) {
            recordController.stopRecording(bdn)
        }
        mIsRecording = false
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