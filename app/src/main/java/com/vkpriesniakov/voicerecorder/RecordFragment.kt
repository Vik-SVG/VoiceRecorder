package com.vkpriesniakov.voicerecorder

import android.content.Context
import android.media.MediaRecorder
import android.os.Bundle
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import androidx.activity.result.contract.ActivityResultContracts
import androidx.appcompat.app.AppCompatActivity
import androidx.fragment.app.Fragment
import androidx.lifecycle.lifecycleScope
import androidx.navigation.NavController
import androidx.navigation.Navigation
import com.vkpriesniakov.voicerecorder.databinding.FragmentRecordBinding
import com.vkpriesniakov.voicerecorder.utils.FloatingButtonAnimator
import com.vkpriesniakov.voicerecorder.utils.RECORD_PERMISSION
import com.vkpriesniakov.voicerecorder.utils.RecordController
import com.vkpriesniakov.voicerecorder.utils.Utils
import com.vkpriesniakov.voicerecorder.utils.Utils.Companion.checkIfPermissionGranted
import kotlinx.coroutines.launch


class RecordFragment : Fragment(), View.OnClickListener {

    private lateinit var mNavController: NavController
    private lateinit var mRecordFile: String
    private var mIsRecording: Boolean = false
    private var mPermissionAllowed = false
    private lateinit var mRecordController: RecordController
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
        mPermissionAllowed = checkIfPermissionGranted(context) //make a Koin
        mRecordController = RecordController()  //make a Koin
        mNavController = Navigation.findNavController(view)  //make a Koin
        mFabAnimator = FloatingButtonAnimator(context as Context)  //make a Koin
        bdn.recordListButton.setOnClickListener(this)
        bdn.recordFab.setOnClickListener(this)
    }

    override fun onClick(v: View?) {
        when (v?.id) {
            R.id.record_list_button -> mNavController.navigate(R.id.action_recordFragment_to_audioListFragment)
            R.id.record_fab -> {
                this.lifecycleScope.launch{
                    makeRecord()
                }
            }
        }
    }

    private fun makeRecord() {
        if (mIsRecording) {
            mRecordController.stopRecording(bdn)
            mFabAnimator.startStopAnimation(bdn.recordFab)
            mIsRecording = false
        } else {
            if (checkPermission()) {
                mRecordController.startRecording(bdn)
                mFabAnimator.startPlayAnimation(bdn.recordFab)
                mIsRecording = true
            }
        }
    }

    override fun onDestroyView() {
        super.onDestroyView()
        mRecordController.stopRecording(bdn)
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