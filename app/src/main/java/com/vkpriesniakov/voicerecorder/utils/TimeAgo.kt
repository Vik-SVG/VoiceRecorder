package com.vkpriesniakov.voicerecorder.utils

import java.util.*
import java.util.concurrent.TimeUnit

class TimeAgo  {

    fun getTimeAgo(
        duration: Long
    ): String {
        val now = Date()
        val seconds = TimeUnit.MILLISECONDS.toSeconds(now.time - duration)
        val minutes = TimeUnit.MILLISECONDS.toMinutes(now.time - duration)
        val hours = TimeUnit.MILLISECONDS.toHours(now.time - duration)
        val days = TimeUnit.MILLISECONDS.toDays(now.time - duration)

        return when {
            seconds < 60 -> {
                "just now"
            }
            minutes == 1L -> {
                "a minute ago"
            }
            minutes in 1..60 -> {
                "$minutes minutes ago"
            }
            hours == 1L -> {
                "an hour ago"
            }
            hours in 1..24 -> {
                "$hours hours ago"
            }
            days == 1L -> {
                "a day ago"
            }
            else -> {
                "$days days ago"
            }
        }
    }
}