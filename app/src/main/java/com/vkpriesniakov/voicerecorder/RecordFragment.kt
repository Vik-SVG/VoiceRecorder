package com.vkpriesniakov.voicerecorder

import android.content.res.ColorStateList
import android.os.Bundle
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import androidx.activity.result.contract.ActivityResultContracts
import androidx.appcompat.app.AppCompatActivity
import androidx.fragment.app.Fragment
import androidx.navigation.NavController
import androidx.navigation.Navigation
import com.vkpriesniakov.voicerecorder.databinding.FragmentRecordBinding
import com.vkpriesniakov.voicerecorder.utils.RECORD_PERMISSION
import com.vkpriesniakov.voicerecorder.utils.ROTATION_ANGLE
import com.vkpriesniakov.voicerecorder.utils.Utils
import com.vkpriesniakov.voicerecorder.utils.Utils.Companion.checkIfPermissionGranted


class RecordFragment : Fragment(), View.OnClickListener {

    private lateinit var mNavController: NavController
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
    ): View? {
        _binding = FragmentRecordBinding.inflate(inflater, container, false)
        return bdn.root
    }

    override fun onViewCreated(view: View, savedInstanceState: Bundle?) {
        super.onViewCreated(view, savedInstanceState)
        mPermissionAllowed = checkIfPermissionGranted(context)
        mNavController = Navigation.findNavController(view)
        bdn.recordListButton.setOnClickListener(this)
        bdn.recordFab.setOnClickListener(this)
    }

    override fun onClick(v: View?) {
        when (v?.id) {
            R.id.record_list_button -> mNavController.navigate(R.id.action_recordFragment_to_audioListFragment)
            R.id.record_fab -> {
                if (mIsRecording) {
                    //Stop record
                    setAnimation(
                        1f, R.drawable.ic_start_record,
                        R.color.white, R.color.purple_200,
                        ROTATION_ANGLE
                    )
                    mIsRecording = false
                } else {
                    if (checkPermission()) {
                        setAnimation(
                            1.2f, R.drawable.ic_baseline_stop_24,
                            R.color.colorPrimary, R.color.colowDarkBlue,
                            -ROTATION_ANGLE
                        )

                        //TODO: Start record

                        mIsRecording = true
                    }
                }
            }
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

    private fun setAnimation(
        scale: Float,
        icon: Int,
        colorFilter: Int,
        background: Int,
        rotation: Float
    ) {
        bdn.recordFab.animate()
            .rotationBy(rotation)
            .setDuration(100)
            .scaleX(0.7f)
            .scaleY(0.7f)
            .withEndAction {
                bdn.recordFab.setImageResource(icon) // setting other icon
                bdn.recordFab.setColorFilter(getResources().getColor(colorFilter))
                bdn.recordFab.backgroundTintList = ColorStateList.valueOf(
                    getResources().getColor(
                        background
                    )
                )
                //Shrink Animation
                bdn.recordFab.animate()
                    .rotationBy(rotation) //Complete the rest of the rotation
                    .setDuration(100)
                    .scaleX(scale) //Scaling back to what it was
                    .scaleY(scale)
                    .start()
            }
            .start()
    }

}