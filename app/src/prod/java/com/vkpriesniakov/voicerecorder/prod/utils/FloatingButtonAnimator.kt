package com.vkpriesniakov.voicerecorder.prod.utils

import android.content.Context
import android.content.res.ColorStateList
import com.google.android.material.floatingactionbutton.FloatingActionButton
import com.vkpriesniakov.voicerecorder.R

interface AnimationControl {

    fun startPlayAnimation(fab: FloatingActionButton)

    fun startStopAnimation(fab: FloatingActionButton)

    fun defaultAnimation(
        scale: Float,
        icon: Int,
        colorFilter: Int,
        background: Int,
        rotation: Float,
        fab: FloatingActionButton,
        context: Context
    )
}

class FloatingButtonAnimator(
    private val context: Context
) : AnimationControl {

    override fun defaultAnimation(
        scale: Float,
        icon: Int,
        colorFilter: Int,
        background: Int,
        rotation: Float,
        fab: FloatingActionButton,
        context: Context
    ) {
        fab.animate()
            .rotationBy(rotation)
            .setDuration(100)
            .scaleX(0.7f)
            .scaleY(0.7f)
            .withEndAction {
                fab.apply {
                    setImageResource(icon) // setting other icon
                    setColorFilter(context.getResources().getColor(colorFilter))
                    backgroundTintList = ColorStateList.valueOf(
                        getResources().getColor(
                            background
                        )
                    )
                    animate()
                        .rotationBy(rotation) //Complete the rest of the rotation
                        .setDuration(100)
                        .scaleX(scale) //Scaling back to what it was
                        .scaleY(scale)
                        .start()
                }
            }
            .start()
    }

    override fun startPlayAnimation(fab:FloatingActionButton) {
        defaultAnimation(
            1.2f, R.drawable.ic_baseline_stop_24,
            R.color.colorPrimary, R.color.colorDarkBlue,
            -ROTATION_ANGLE,
            fab,
            context
        )
    }

    override fun startStopAnimation(fab:FloatingActionButton) {
        defaultAnimation(
            1f, R.drawable.ic_start_record,
            R.color.white, R.color.purple_200,
            ROTATION_ANGLE,
            fab,
            context
        )
    }

}