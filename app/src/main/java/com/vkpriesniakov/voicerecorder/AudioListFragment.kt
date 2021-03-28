package com.vkpriesniakov.voicerecorder

import android.content.Context
import android.media.MediaPlayer
import android.os.Bundle
import android.util.Log
import androidx.fragment.app.Fragment
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import android.widget.SeekBar
import android.widget.Toast
import androidx.constraintlayout.widget.ConstraintLayout
import androidx.core.content.res.ResourcesCompat
import androidx.lifecycle.lifecycleScope
import androidx.recyclerview.widget.LinearLayoutManager
import com.google.android.material.bottomsheet.BottomSheetBehavior
import com.vkpriesniakov.voicerecorder.databinding.FragmentAudioListBinding
import dagger.hilt.android.AndroidEntryPoint
import kotlinx.coroutines.*
import java.io.File
import javax.inject.Inject

@AndroidEntryPoint
class AudioListFragment : Fragment() {

    @Inject
    lateinit var mAllFiles: Array<out File>

    @Inject
    lateinit var mMediaPlayer: MediaPlayer

    private lateinit var mBottomSheetBehaviour: BottomSheetBehavior<ConstraintLayout>
    private var _binding: FragmentAudioListBinding? = null
    private val bdn get() = _binding!!
    private var isPlaying = false
    private lateinit var mFileToPlay: File

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

        mAudioListAdapter =
            AudioListAdapter(mAllFiles, context as Context, callback = { file, position ->
                mFileToPlay = file
                if (isPlaying) {
                    stopAudio()
                    playAudio(mFileToPlay)
                } else {
                    playAudio(mFileToPlay)
                }
            })

        bdn.audioListView.apply {
            setHasFixedSize(true)
            layoutManager = LinearLayoutManager(context)
            adapter = mAudioListAdapter
        }

        setupBottomSheet()
    }

    override fun onDestroyView() {
        super.onDestroyView()
        if (isPlaying) {
            mMediaPlayer.apply {
                stop()
                reset()
            }
        }
    }

    override fun onStop() {
        super.onStop()
        stopAudio()
    }

    private fun stopAudio() {

        isPlaying = false

        if (job?.isActive == true) {
            job?.cancel()
        }

        mMediaPlayer.apply {
            stop()
            reset()
//            release()
        }

        bdn.playerSheetInclude.playButtonSheet.setImageDrawable(
            ResourcesCompat.getDrawable(
                activity?.resources!!,
                R.drawable.ic_baseline_play_button,
                null
            )
        )

        bdn.playerSheetInclude.playerHeaderTitle.text = "Stop"
    }

    private fun playAudio(file: File) {

        isPlaying = true

        mBottomSheetBehaviour.state = BottomSheetBehavior.STATE_EXPANDED


        mMediaPlayer.apply {
            reset()
            setDataSource(file.absolutePath)
            prepare()
            start()
        }

        bdn.playerSheetInclude.playButtonSheet.setImageDrawable(
            ResourcesCompat.getDrawable(
                activity?.resources!!,
                R.drawable.ic_baseline_pause_24,
                null
            )
        )
        bdn.playerSheetInclude.textFileName.text = mFileToPlay.name
        bdn.playerSheetInclude.playerHeaderTitle.text = "Playing"

        updateJob()

        mMediaPlayer.setOnCompletionListener {
            stopAudio()
            bdn.playerSheetInclude.playerHeaderTitle.text = "Finished"
            playAudio(mFileToPlay)
        }

        bdn.playerSheetInclude.playerSeekbar.max = mMediaPlayer.duration

    }

    private fun pauseAudio() {
        job?.cancel()
        mMediaPlayer.pause()
        bdn.playerSheetInclude.playerHeaderTitle.text = "Paused"
        bdn.playerSheetInclude.playButtonSheet.setImageDrawable(
            ResourcesCompat.getDrawable(
                activity?.resources!!,
                R.drawable.ic_baseline_play_button,
                null
            )
        )
    }

    private fun resumeAudio() {
        updateJob()
        mMediaPlayer.start()
        bdn.playerSheetInclude.playerHeaderTitle.text = "Resumed"
        bdn.playerSheetInclude.playButtonSheet.setImageDrawable(
            ResourcesCompat.getDrawable(
                activity?.resources!!,
                R.drawable.ic_baseline_pause_24,
                null
            )
        )
    }

    private fun updateJob() {
        job = lifecycleScope.launch(Dispatchers.Main) {
            while (isPlaying) {
                bdn.playerSheetInclude.playerSeekbar.progress = mMediaPlayer.currentPosition
                delay(200)
            }
        }

        job?.start()
    }

    private fun setupBottomSheet() {
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
                pauseAudio()
                isPlaying = false
            } else {
                resumeAudio()
                isPlaying = true
            }
        }

        bdn.playerSheetInclude.playerSeekbar.setOnSeekBarChangeListener(object :
            SeekBar.OnSeekBarChangeListener {
            override fun onProgressChanged(seekBar: SeekBar?, progress: Int, fromUser: Boolean) {
//                TODO("Not yet implemented")
            }

            override fun onStartTrackingTouch(seekBar: SeekBar?) {
                pauseAudio()
            }

            override fun onStopTrackingTouch(seekBar: SeekBar?) {
                val progress = seekBar?.progress
                progress?.let { mMediaPlayer.seekTo(it) }
                resumeAudio()
            }


        })
    }
}