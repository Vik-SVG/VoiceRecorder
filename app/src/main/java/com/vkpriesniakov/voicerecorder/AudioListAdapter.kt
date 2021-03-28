package com.vkpriesniakov.voicerecorder

import android.content.Context
import android.view.LayoutInflater
import android.view.ViewGroup
import androidx.recyclerview.widget.RecyclerView
import com.vkpriesniakov.voicerecorder.databinding.SingleListItemBinding
import com.vkpriesniakov.voicerecorder.utils.TimeAgo
import java.io.File

class AudioListAdapter (
    private val allFiles: Array<out File>?,
    context: Context,
    private val callback: (File, Int) -> Unit
) :
    RecyclerView.Adapter<AudioListAdapter.AudioViewHolder>() {
    private val inflater = LayoutInflater.from(context)

//    @Inject lateinit var timeAgo:TimeAgo

    val timeAgo = TimeAgo()

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