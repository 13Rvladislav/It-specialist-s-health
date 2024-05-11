package com.example.it_health

import android.content.SharedPreferences
import android.os.CountDownTimer
import androidx.lifecycle.ViewModel
import java.text.DecimalFormat
import java.text.NumberFormat
class TimerViewModel : ViewModel() {
    var timer: CountDownTimer? = null

    fun startTimer(time: Long, onTick: (Long) -> Unit, onFinish: () -> Unit) {
        timer = object : CountDownTimer(time, 1000) {
            override fun onTick(millisUntilFinished: Long) {
                onTick(millisUntilFinished)
            }

            override fun onFinish() {
                onFinish()
            }
        }.start()
    }

    fun cancelTimer() {
        timer?.cancel()
    }
}

