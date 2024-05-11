package com.example.it_health

import android.app.Dialog
import android.content.Context
import android.content.Intent
import android.content.SharedPreferences
import android.graphics.Color
import android.graphics.drawable.ColorDrawable
import androidx.appcompat.app.AppCompatActivity
import android.os.Bundle
import android.view.View
import android.view.Window
import android.widget.RadioButton
import android.widget.RadioGroup
import android.widget.Toast
import com.example.it_health.databinding.ActivityAuthorBinding
import com.example.it_health.databinding.ActivityProfileBinding
import com.example.it_health.fragments.Todo
import com.google.android.material.bottomnavigation.BottomNavigationView
import com.google.firebase.auth.FirebaseAuth
import com.google.firebase.database.FirebaseDatabase
import dbClases.Users

private lateinit var binding: ActivityProfileBinding
private lateinit var auth: FirebaseAuth

class ActivityProfile : AppCompatActivity() {
    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        setContentView(R.layout.activity_profile)

        binding = ActivityProfileBinding.inflate(layoutInflater)
        setContentView(binding.root)

        auth = FirebaseAuth.getInstance()
        val sharedPreferences =
            getSharedPreferences("myPrefs", Context.MODE_PRIVATE)
        val editor = sharedPreferences.edit()
        changeProfileInfo(sharedPreferences, editor)

        //навигация
        val bottomNavigationView = findViewById<BottomNavigationView>(R.id.bottomNavigationView)
        bottomNavigationView.selectedItemId = R.id.profile

        bottomNavigationView.setOnItemSelectedListener { menuItem ->

            when (menuItem.itemId) {
                R.id.home -> {
                    startActivity(Intent(this, ActivityMainMenu::class.java))
                    //  overridePendingTransition(R.id.anim.slide_in_right,R.id.anim.left)
                    true
                }
                R.id.sport -> {
                    startActivity(Intent(this, ActivitySport::class.java))
                    true
                }
                R.id.todo -> {
                    startActivity(Intent(this, ActivityTodo::class.java))
                    true
                }
                R.id.profile -> {
                    true
                }
                else -> false
            }
        }

//кнопка редактирования
        binding.reduct.setOnClickListener {
            val dialog = Dialog(this)
            dialog.requestWindowFeature(Window.FEATURE_NO_TITLE)
            dialog.setCancelable(false)
            dialog.setContentView(R.layout.dialog_profile)
            dialog.window?.setBackgroundDrawable(ColorDrawable(Color.TRANSPARENT))
            dialog.show()


            //сохранение
            dialog.findViewById<View>(R.id.save).setOnClickListener {

                //ФИО
                val FIO =
                    dialog.findViewById<com.google.android.material.textfield.TextInputLayout>(R.id.task)
                        .getEditText()?.getText().toString()
                //РОСТ
                val Height =
                    dialog.findViewById<com.google.android.material.textfield.TextInputLayout>(R.id.date)
                        .getEditText()?.getText().toString()
                //ВЕС
                val Weight =
                    dialog.findViewById<com.google.android.material.textfield.TextInputLayout>(R.id.weight)
                        .getEditText()?.getText().toString()
                //ПОЛ
                var radioGroup = dialog.findViewById<RadioGroup>(R.id.radioGroup1)
                var selectedRadioButtonId = radioGroup.checkedRadioButtonId
                var selectedRadioButton = dialog.findViewById<RadioButton>(selectedRadioButtonId)
                var Sex = selectedRadioButton.text.toString()
                //рабочий день
                radioGroup = dialog.findViewById<RadioGroup>(R.id.radioGroup2)
                selectedRadioButtonId = radioGroup.checkedRadioButtonId
                selectedRadioButton = dialog.findViewById<RadioButton>(selectedRadioButtonId)
                var WorkTime = selectedRadioButton.text.toString()
                WorkTime = WorkTime.substring(0, WorkTime.length - 1);
                //образ жизни
                radioGroup = dialog.findViewById<RadioGroup>(R.id.radioGroup3)
                selectedRadioButtonId = radioGroup.checkedRadioButtonId
                selectedRadioButton = dialog.findViewById<RadioButton>(selectedRadioButtonId)
                var LifeStyle = selectedRadioButton.text.toString()

                if (FIO.isNotEmpty() && Height.isNotEmpty() && Weight.isNotEmpty()) {
//запись в FB и перезапись sharedpref
                    val userInfo = Users(
                        FIO,
                        Height,
                        Weight,
                        Sex,
                        WorkTime,
                        LifeStyle,
                    )
                    lateinit var waterNorm:String

                    if (Sex == "Жен") {
                        waterNorm=(Weight.toInt()*31).toString()
                    } else {
                        waterNorm=(Weight.toInt()*35).toString()
                    }
                    //запись в FB
                    FirebaseDatabase.getInstance().getReference("Users")
                        .child(FirebaseAuth.getInstance().currentUser!!.uid).child("User-info")
                        .setValue(userInfo)

                    FirebaseDatabase.getInstance().getReference("Users")
                        .child(FirebaseAuth.getInstance().currentUser!!.uid).child("MainWorkInfo").child("waterNorm")
                        .setValue(waterNorm)

                    //перезапись sp
                    editor.putString("height", Height.toString())
                    editor.putString("lifeStyle", LifeStyle.toString())
                    editor.putString("name", FIO.toString())
                    editor.putString("sex", Sex.toString())
                    editor.putString("weight", Weight.toString())
                    editor.putString("workTime", WorkTime.toString())
                    editor.putString("waterNorm", waterNorm.toString())
                    editor.apply()
//перезапись профиля
                    changeProfileInfo(sharedPreferences, editor)
                    Toast.makeText(this, "Данные успешно изменены", Toast.LENGTH_LONG)
                        .show()
                    dialog.dismiss() // Закрытие диалогового окна
                } else {
                    Toast.makeText(
                        this,
                        "Одно или несколько полей пусты",
                        Toast.LENGTH_LONG
                    ).show()

                }

            }
            // Закрытие диалогового окна
            dialog.findViewById<View>(R.id.cansel).setOnClickListener {
                dialog.dismiss() // Закрытие диалогового окна

            }
        }


        //кнопка выхода
        binding.Exit.setOnClickListener {
            val intent = Intent(this, MainActivity::class.java)
            FirebaseAuth.getInstance().signOut();
            startActivity(intent)
        }

    }
    fun changeProfileInfo(sharedPreferences: SharedPreferences, editor: SharedPreferences.Editor) {
        val savedName = sharedPreferences.getString("name", "")
        val savedHeight = sharedPreferences.getString("height", "")
        val savedWeight = sharedPreferences.getString("weight", "")
        val savedSex = sharedPreferences.getString("sex", "")
        val savedLifeStyle = sharedPreferences.getString("lifeStyle", "")
        val savedWorkTime = sharedPreferences.getString("workTime", "")

        // Обновляем данные в элементах интерфейса
        binding.textFIO.setText(savedName)
        binding.textHeight.setText(savedHeight)
        binding.textWeight.setText(savedWeight)
        binding.textSex.setText(savedSex)
        binding.textLifeStyle.setText(savedLifeStyle)
        binding.textWorkTime.setText(savedWorkTime + "ч")
    }
}