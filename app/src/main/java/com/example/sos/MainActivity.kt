package com.example.sos

import android.os.Bundle
import android.os.CountDownTimer
import android.view.animation.AnimationUtils
import androidx.appcompat.app.AppCompatActivity
import androidx.core.content.ContextCompat
import android.widget.Button
import android.widget.TextView
import android.media.MediaPlayer
import android.os.VibrationEffect
import android.os.Vibrator
import android.os.VibratorManager
import android.content.Context

class MainActivity : AppCompatActivity() {
    private lateinit var timerTextView: TextView
    private lateinit var startButton: Button
    private lateinit var mediaPlayer: MediaPlayer
    private lateinit var vibrator: Vibrator
    private var timer: CountDownTimer? = null
    private var isRunning = false

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        setContentView(R.layout.activity_main)

        timerTextView = findViewById(R.id.timerTextView)
        startButton = findViewById(R.id.startButton)
        vibrator = if (android.os.Build.VERSION.SDK_INT >= android.os.Build.VERSION_CODES.S) {
            val vibratorManager = getSystemService(VibratorManager::class.java)
            vibratorManager.defaultVibrator
        } else {
            getSystemService(Context.VIBRATOR_SERVICE) as Vibrator
        }
        mediaPlayer = MediaPlayer.create(this, R.raw.beep)

        startButton.setOnClickListener {
            if (!isRunning) {
                startDramaticCountdown()
            } else {
                stopCountdown()
            }
        }
    }

    private fun startDramaticCountdown() {
        isRunning = true
        startButton.text = "ABORT"
        startButton.setBackgroundColor(ContextCompat.getColor(this, android.R.color.holo_red_dark))

        timer = object : CountDownTimer(60000, 1000) {
            override fun onTick(millisUntilFinished: Long) {
                val seconds = millisUntilFinished / 1000
                timerTextView.text = String.format("%02d:%02d", seconds / 60, seconds % 60)

                if (seconds <= 10) {
                    val blinkAnimation = AnimationUtils.loadAnimation(this@MainActivity, R.anim.blink)
                    timerTextView.startAnimation(blinkAnimation)

                    vibrator.vibrate(VibrationEffect.createOneShot(200, VibrationEffect.DEFAULT_AMPLITUDE))
                    mediaPlayer.start()
                }
            }

            override fun onFinish() {
                timerTextView.text = "00:00"
                resetTimer()
            }
        }.start()
    }

    private fun stopCountdown() {
        resetTimer()
    }

    private fun resetTimer() {
        timer?.cancel()
        isRunning = false
        startButton.text = "START"
        startButton.setBackgroundColor(ContextCompat.getColor(this, android.R.color.holo_green_dark))
        timerTextView.clearAnimation()
        timerTextView.text = "01:00"
    }

    override fun onDestroy() {
        super.onDestroy()
        mediaPlayer.release()
        timer?.cancel()
    }
}
