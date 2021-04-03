package com.vkpriesniakov.voicerecorder.adapters

import android.view.LayoutInflater
import android.view.ViewGroup
import androidx.fragment.app.Fragment
import androidx.recyclerview.widget.RecyclerView
import com.vkpriesniakov.voicerecorder.databinding.SingleListItemBinding
import com.vkpriesniakov.voicerecorder.utils.TimeAgo
import dagger.hilt.EntryPoint
import dagger.hilt.InstallIn
import dagger.hilt.android.EntryPointAccessors
import dagger.hilt.android.components.FragmentComponent
import java.io.File

class AudioListAdapter (
    private val allFiles: Array<out File>?,
    fragment: Fragment,
    private val callback: (File, Int) -> Unit
) :
    RecyclerView.Adapter<AudioListAdapter.AudioViewHolder>() {

    @EntryPoint
    @InstallIn(FragmentComponent::class)
    interface AudioLIstAdapterEntry{
        fun provideTimeAgo():TimeAgo
    }

    val context = fragment.context

    private val timeAgoEntry = EntryPointAccessors.fromFragment(fragment, AudioLIstAdapterEntry::class.java)

    val timeAgo = timeAgoEntry.provideTimeAgo()


    private val inflater = LayoutInflater.from(context)

//    @Inject
//    lateinit var timeAgo:TimeAgo

//    val timeAgo = TimeAgo()

    inner class AudioViewHolder(private val bdnView: SingleListItemBinding) :
        RecyclerView.ViewHolder(bdnView.root) {
        fun bind(position: Int) {
            bdnView.fileNameTxt.text = allFiles?.get(position)?.name
            bdnView.listDate.text =
                allFiles?.get(position)?.let { timeAgo.getTimeAgo(it.lastModified()) }

            bdnView.root.setOnClickListener {
                allFiles?.get(position)?.let { it1 -> callback.invoke(it1, position) }
            }
        }
    }

    override fun onCreateViewHolder(parent: ViewGroup, viewType: Int): AudioViewHolder {
        val bdn = SingleListItemBinding.inflate(inflater, parent, false)
        return AudioViewHolder(bdn)
    }

    override fun onBindViewHolder(holder: AudioViewHolder, position: Int) {
        holder.bind(position)
    }

    override fun getItemCount(): Int = allFiles?.size ?: 0
}