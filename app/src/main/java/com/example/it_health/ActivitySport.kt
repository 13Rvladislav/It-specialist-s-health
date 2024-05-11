package com.example.it_health

import android.app.Dialog
import android.content.Intent
import android.graphics.Color
import android.graphics.drawable.ColorDrawable
import androidx.appcompat.app.AppCompatActivity
import android.os.Bundle
import android.view.Window
import android.widget.ImageButton
import android.widget.TextView
import android.widget.Toast
import com.example.it_health.databinding.ActivityMainMenuBinding
import com.example.it_health.databinding.ActivitySportBinding
import com.example.it_health.fragments.Todo
import com.google.android.material.bottomnavigation.BottomNavigationView
import com.google.android.material.textfield.TextInputLayout


private lateinit var binding: ActivitySportBinding

lateinit var cansel: ImageButton
lateinit var btn: TextView
var dialog: Dialog? = null
var trainingNumber = 1

class ActivitySport : AppCompatActivity() {
    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        setContentView(R.layout.activity_sport)
        binding = ActivitySportBinding.inflate(layoutInflater)
        setContentView(binding.root)

        //навигация
        val bottomNavigationView = findViewById<BottomNavigationView>(R.id.bottomNavigationView)
        bottomNavigationView.selectedItemId = R.id.sport

        bottomNavigationView.setOnItemSelectedListener { menuItem ->

            when (menuItem.itemId) {
                R.id.home -> {
                    startActivity(Intent(this, ActivityMainMenu::class.java))
                    //  overridePendingTransition(R.id.anim.slide_in_right,R.id.anim.left)
                    true
                }
                R.id.sport -> {
                    true
                }
                R.id.todo -> {
                    startActivity(Intent(this, ActivityTodo::class.java))
                    true
                }
                R.id.profile -> {
                    startActivity(Intent(this, ActivityProfile::class.java))
                    true
                }
                else -> false
            }
        }

        binding.razm.setOnClickListener {
            showTrainingDialog(getTrainingLayout(trainingNumber))
        }
    }

    fun showTrainingDialog(dialogLayout: Int) {
        dialog?.dismiss()
        dialog = Dialog(this)
        dialog?.requestWindowFeature(Window.FEATURE_NO_TITLE)
        dialog?.setContentView(dialogLayout)
        dialog?.window?.setBackgroundDrawable(ColorDrawable(Color.TRANSPARENT))
        dialog?.show()

        dialog?.findViewById<ImageButton>(R.id.cansel)?.setOnClickListener {
            dialog?.dismiss()
        }

        dialog?.findViewById<TextView>(R.id.authorization)?.setOnClickListener {
            trainingNumber++
            if (trainingNumber <= 6) {
                showTrainingDialog(getTrainingLayout(trainingNumber))
            } else if (trainingNumber == 7) {
                dialog!!.dismiss()
                trainingNumber = 1
            }
        }
    }


    fun getTrainingLayout(trainingNumber: Int): Int {
        return when (trainingNumber) {
            1 -> R.layout.dialog_training_1
            2 -> R.layout.dialog_training_2
            3 -> R.layout.dialog_training_3
            4 -> R.layout.dialog_training_4
            5 -> R.layout.dialog_training_5
            6 -> R.layout.dialog_training_6
            else -> R.layout.dialog_training_1 // Вернуть начальное окно, если достигнут максимальный уровень
        }
    }
}