package com.vkpriesniakov.voicerecorder.ui.record

import android.annotation.SuppressLint
import android.content.res.Configuration
import android.os.Bundle
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import androidx.activity.result.contract.ActivityResultContracts
import androidx.appcompat.app.AppCompatActivity
import androidx.appcompat.app.AppCompatDelegate
import androidx.lifecycle.lifecycleScope
import com.vkpriesniakov.voicerecorder.R
import com.vkpriesniakov.voicerecorder.base.BaseFragment
import com.vkpriesniakov.voicerecorder.databinding.FragmentRecordBinding
import com.vkpriesniakov.voicerecorder.utils.*
import com.vkpriesniakov.voicerecorder.utils.Utils.Companion.checkIfPermissionGranted
import dagger.hilt.android.AndroidEntryPoint
import io.reactivex.rxjava3.core.Observable
import io.reactivex.rxjava3.subjects.BehaviorSubject
import io.reactivex.rxjava3.subjects.PublishSubject
import io.reactivex.rxjava3.subjects.Subject
import kotlinx.coroutines.launch
import javax.inject.Inject


@AndroidEntryPoint
class RecordFragment : BaseFragment(), View.OnClickListener {


    private var isDarkerThemeEnabled: Boolean = true

    @Inject
    lateinit var recordController: RecordController

    @Inject
    lateinit var floatingButtonAnimator: AnimationControl

    lateinit var mObservable: Subject<Boolean>
    private lateinit var mRecordFile: String
    private var mIsRecording: Boolean = false
    private var mPermissionAllowed = false

    private var _binding: FragmentRecordBinding? = null

    lateinit var t: Observable<Boolean>
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

        val nightModeFlags: Int = resources.configuration.uiMode and
                Configuration.UI_MODE_NIGHT_MASK

        mPermissionAllowed = checkIfPermissionGranted(context)
        bdn.recordListButton.setOnClickListener(this)
        bdn.recordFab.setOnClickListener(this)
        bdn.enableDarkTheme.setOnClickListener(this)


    }

    override fun onClick(v: View?) {
        when (v?.id) {
            R.id.record_list_button -> mainNavigator.navigate(R.id.action_recordFragment_to_audioListFragment)
            R.id.record_fab -> {
                this.lifecycleScope.launch {
                    makeRecord()
                }
            }
            bdn.enableDarkTheme.id -> {
                AppCompatDelegate.setDefaultNightMode(if (isDarkerThemeEnabled) AppCompatDelegate.MODE_NIGHT_NO else (AppCompatDelegate.MODE_NIGHT_YES))
                isDarkerThemeEnabled = !isDarkerThemeEnabled
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