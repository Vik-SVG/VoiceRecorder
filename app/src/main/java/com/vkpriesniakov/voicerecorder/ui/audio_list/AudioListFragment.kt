package com.vkpriesniakov.voicerecorder.ui.audio_list

import android.media.MediaPlayer
import android.os.Bundle
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import android.widget.SeekBar
import androidx.constraintlayout.widget.ConstraintLayout
import androidx.core.content.res.ResourcesCompat
import androidx.lifecycle.lifecycleScope
import androidx.recyclerview.widget.LinearLayoutManager
import com.google.android.material.bottomsheet.BottomSheetBehavior
import com.vkpriesniakov.voicerecorder.adapters.AudioListAdapter
import com.vkpriesniakov.voicerecorder.R
import com.vkpriesniakov.voicerecorder.base.BaseFragment
import com.vkpriesniakov.voicerecorder.databinding.FragmentAudioListBinding
import dagger.hilt.android.AndroidEntryPoint
import kotlinx.coroutines.*
import java.io.File
import javax.inject.Inject

@AndroidEntryPoint
class AudioListFragment : BaseFragment(), AudioListView {

    @Inject
    lateinit var mAllFiles: Array<out File>

//    @Inject
//    lateinit var mMediaPlayer: MediaPlayer

    lateinit var mPresenter: AudioListPresenter<AudioListView>

    private lateinit var mBottomSheetBehaviour: BottomSheetBehavior<ConstraintLayout>
    private var _binding: FragmentAudioListBinding? = null
    private val bdn get() = _binding!!
    private var isPlaying = false

    private var job: Job? = null

    private lateinit var mAudioListAdapter: AudioListAdapter

    override fun onCreateView(
        inflater: LayoutInflater, container: ViewGroup?,
        savedInstanceState: Bundle?
    ): View {
        // Inflate the layout for this fragment
        _binding = FragmentAudioListBinding.inflate(inflater, container, false)
        return bdn.root
    }

    override fun onViewCreated(view: View, savedInstanceState: Bundle?) {
        super.onViewCreated(view, savedInstanceState)
        mPresenter = AudioListPresenterImpl()
        mPresenter.onAttachPresenterFun(this)

        mAudioListAdapter =
            AudioListAdapter(mAllFiles, this, callback = { file, position ->

                mPresenter.mPresenterFile = file

                if (isPlaying) {
                    mPresenter.onStopAudio()
                    mPresenter.onPlayAudio()
                } else {
                    mPresenter.onPlayAudio()
                }
            })

        bdn.audioListView.apply {
            setHasFixedSize(true)
            layoutManager = LinearLayoutManager(context)
            adapter = mAudioListAdapter
        }

        mPresenter.onSetupBottomSheet()

    }

    override fun onDestroyView() {
        super.onDestroyView()
        mPresenter.onDetachMediaPlayer(isPlaying)
        mPresenter.onDetachPresenterFun()
    }

    override fun onStop() {
        super.onStop()
        mPresenter.onStopAudio()
    }

    override fun stopAudio() {

        isPlaying = false

        if (job?.isActive == true) {
            job?.cancel()
        }

//        mPresenter.onStopMediaPlayer()

        bdn.playerSheetInclude.playButtonSheet.setImageDrawable(
            ResourcesCompat.getDrawable(
                activity?.resources!!,
                R.drawable.ic_baseline_play_button,
                null
            )
        )

        bdn.playerSheetInclude.playerHeaderTitle.text = "Stop"
    }

    override fun playAudio(file: File) {

        isPlaying = true

        mBottomSheetBehaviour.state = BottomSheetBehavior.STATE_EXPANDED

        bdn.playerSheetInclude.playButtonSheet.setImageDrawable(
            ResourcesCompat.getDrawable(
                activity?.resources!!,
                R.drawable.ic_baseline_pause_24,
                null
            )
        )
        bdn.playerSheetInclude.textFileName.text = mPresenter.mPresenterFile?.name
        bdn.playerSheetInclude.playerHeaderTitle.text = "Playing"

        mPresenter.onUpdateJob()

        bdn.playerSheetInclude.playerSeekbar.max = mPresenter.mMediaPlayer.duration
        mPresenter.mMediaPlayer.setOnCompletionListener {
            mPresenter.onStopAudio()
            bdn.playerSheetInclude.playerHeaderTitle.text = "Finished"
            mPresenter.onPlayAudio()
        }
    }

    override fun pauseAudio() {
        job?.cancel()
        bdn.playerSheetInclude.playerHeaderTitle.text = "Paused"
        bdn.playerSheetInclude.playButtonSheet.setImageDrawable(
            ResourcesCompat.getDrawable(
                activity?.resources!!,
                R.drawable.ic_baseline_play_button,
                null
            )
        )
    }

    override fun resumeAudio() {
        bdn.playerSheetInclude.playerHeaderTitle.text = "Resumed"
        bdn.playerSheetInclude.playButtonSheet.setImageDrawable(
            ResourcesCompat.getDrawable(
                activity?.resources!!,
                R.drawable.ic_baseline_pause_24,
                null
            )
        )
    }

    override fun updateJob() {
        job = lifecycleScope.launch(Dispatchers.Main) {
            while (isPlaying) {
                bdn.playerSheetInclude.playerSeekbar.progress =
                    mPresenter.mMediaPlayer.currentPosition
                delay(200)
            }
        }

        job?.start()
    }

    override fun setupBottomSheet() {
        mBottomSheetBehaviour = BottomSheetBehavior.from(bdn.playerSheetInclude.root).also {

            it.addBottomSheetCallback(object :
                BottomSheetBehavior.BottomSheetCallback() {
                override fun onStateChanged(bottomSheet: View, newState: Int) {
                    if (newState == BottomSheetBehavior.STATE_HIDDEN) {
                        it.state = BottomSheetBehavior.STATE_COLLAPSED
                    }
                }

                override fun onSlide(bottomSheet: View, slideOffset: Float) {

                }
            })
        }

        bdn.playerSheetInclude.playButtonSheet.setOnClickListener {

            if (isPlaying) {
                mPresenter.onPauseAudio()
                isPlaying = false
            } else {
                mPresenter.onResumeAudio()
                isPlaying = true
            }
        }

        bdn.playerSheetInclude.playerSeekbar.setOnSeekBarChangeListener(object :
            SeekBar.OnSeekBarChangeListener {
            override fun onProgressChanged(seekBar: SeekBar?, progress: Int, fromUser: Boolean) {
//                TODO("Not yet implemented")
            }

            override fun onStartTrackingTouch(seekBar: SeekBar?) {
                mPresenter.onPauseAudio()
            }

            override fun onStopTrackingTouch(seekBar: SeekBar?) {
                val progress = seekBar?.progress
                progress?.let { mPresenter.mMediaPlayer.seekTo(it) }
                mPresenter.onResumeAudio()
            }
        })
    }
}