package com.vkpriesniakov.voicerecorder

import android.content.Context
import android.os.Bundle
import androidx.fragment.app.Fragment
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import android.widget.Toast
import androidx.constraintlayout.widget.ConstraintLayout
import androidx.recyclerview.widget.LinearLayoutManager
import com.google.android.material.bottomsheet.BottomSheetBehavior
import com.vkpriesniakov.voicerecorder.databinding.FragmentAudioListBinding
import java.io.File


class AudioListFragment : Fragment() {

    private lateinit var mBottomSheetBehaviour: BottomSheetBehavior<ConstraintLayout>
    private var _binding: FragmentAudioListBinding? = null
    private val bdn get() = _binding!!
    private var mAllFiles: Array<out File>? = null
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

        val path: String = activity?.getExternalFilesDir("/")?.absolutePath ?: ""
        val directory = File(path)
        mAllFiles = directory.listFiles()
        mAudioListAdapter =
            AudioListAdapter(mAllFiles, context as Context, callback = { file, position ->
                Toast.makeText(requireContext(), "$position click", Toast.LENGTH_SHORT).show()
            })

        bdn.audioListView.apply {
            setHasFixedSize(true)
            layoutManager = LinearLayoutManager(context)
            adapter = mAudioListAdapter

        }


        mBottomSheetBehaviour = BottomSheetBehavior.from(bdn.playerSheetInclude.root)
        mBottomSheetBehaviour.addBottomSheetCallback(object :
            BottomSheetBehavior.BottomSheetCallback() {
            override fun onStateChanged(bottomSheet: View, newState: Int) {
                if (newState == BottomSheetBehavior.STATE_HIDDEN) {
                    mBottomSheetBehaviour.state = BottomSheetBehavior.STATE_COLLAPSED
                }
            }

            override fun onSlide(bottomSheet: View, slideOffset: Float) {

            }
        })

    }
}