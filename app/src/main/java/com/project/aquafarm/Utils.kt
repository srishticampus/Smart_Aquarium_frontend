package com.project.aquafarm

import android.content.Context
import android.graphics.Canvas
import android.graphics.Path
import android.os.SystemClock
import androidx.appcompat.widget.AppCompatTextView
import kotlin.math.min

class WaveyTextView(ctx: Context?) : AppCompatTextView(ctx!!) {
    private var leftOffset = 0

    private enum class TransitionState {
        TRANSITION_STARTING,
        TRANSITION_RUNNING,
        TRANSITION_NONE
    }

    private var transitionState: TransitionState? = null
    private var animDuration = 0.0
    private var startTimeMillis = 0.0
    private var wavePath: Path? = null
    private val pxWLength = 175
    fun resetTextPosition() {
        leftOffset = 0
        transitionState = TransitionState.TRANSITION_NONE
        invalidate()
    }

    fun animateToRight(animDuration: Double) {
        this.animDuration = animDuration
        transitionState = TransitionState.TRANSITION_STARTING
        invalidate()
    }

    public override fun onDraw(canvas: Canvas) {
        if (wavePath == null) {
            generateWavePath()
        }
        var done = true
        when (transitionState) {
            TransitionState.TRANSITION_STARTING -> {
                done = false
                transitionState = TransitionState.TRANSITION_RUNNING
                startTimeMillis = SystemClock.uptimeMillis().toDouble()
            }

            TransitionState.TRANSITION_RUNNING -> {
                var normalized = ((SystemClock.uptimeMillis() - startTimeMillis)
                        / animDuration)
                done = normalized >= 1.0
                normalized = min(normalized, 1.0)
                leftOffset = (width * normalized).toInt()
            }

            else -> {}
        }
        canvas.drawTextOnPath(
            getText().toString(),
            wavePath!!,
            leftOffset.toFloat(),
            ((height - (paddingTop + paddingBottom)) / 4).toFloat(),
            paint
        )
        if (!done) {
            invalidate()
        }
    }

    private fun generateWavePath() {
        wavePath = Path()
        var lOffset = 0
        var ct = 0
        wavePath!!.moveTo(0f, (height / 2).toFloat())
        while (lOffset < width) {
            wavePath!!.quadTo(
                (lOffset + pxWLength / 4).toFloat(),
                (height * (ct++ % 2)).toFloat(),
                (lOffset + pxWLength / 2).toFloat(),
                (height / 2).toFloat()
            )
            lOffset += pxWLength / 2
        }
    }
}