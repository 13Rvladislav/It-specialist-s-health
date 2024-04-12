package com.example.it_health

import androidx.appcompat.app.AppCompatActivity
import android.os.Bundle
import androidx.fragment.app.Fragment
import com.example.it_health.databinding.ActivityMenuBinding
import com.example.it_health.fragments.Home
import com.example.it_health.fragments.Profile
import com.example.it_health.fragments.Sport
import com.example.it_health.fragments.Todo
import com.google.firebase.auth.FirebaseAuth
import com.google.firebase.database.DatabaseReference
import com.google.firebase.database.FirebaseDatabase
import com.google.firebase.ktx.Firebase

class ActivityMenu : AppCompatActivity() {

    // Получить ID текущего авторизованного пользователя
    val currentUser = FirebaseAuth.getInstance().currentUser
    val userId = currentUser?.uid

    // Ссылка на базу данных Firebase для текущего пользователя
    val databaseRef = FirebaseDatabase.getInstance().getReference("Users/$userId/User-info")

    private lateinit var binding: ActivityMenuBinding
    private lateinit var database: DatabaseReference

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        binding = ActivityMenuBinding.inflate(layoutInflater)
        setContentView(binding.root)
        binding.bottomNavigationView.setSelectedItemId(R.id.home);
        replaceFragment(Home())
        //получение данных из FB
        readData()

        binding.bottomNavigationView.setOnItemSelectedListener {
            when (it.itemId) {
                R.id.sport -> replaceFragment(Sport())
                R.id.home -> replaceFragment(Home())
                R.id.todo -> replaceFragment(Todo())
                R.id.profile -> replaceFragment(Profile())

                else -> {

                }
            }
            true
        }

    }

    private fun readData() {
        val sharedPreferences = getSharedPreferences("myPrefs", MODE_PRIVATE)
        val editor = sharedPreferences.edit()
        //получение информации о пользователе user-info
        database = FirebaseDatabase.getInstance().getReference("Users")
        database.child(FirebaseAuth.getInstance().currentUser!!.uid).child("User-info").get()
            .addOnSuccessListener {
                if (it.exists()) {
                    val fio = it.child("name").value
                    val lifeStyle = it.child("lifeStyle").value
                    val height = it.child("height").value
                    val weight = it.child("weight").value
                    val sex = it.child("sex").value
                    val workTime = it.child("workTime").value

                    editor.putString("height", height.toString())
                    editor.putString("lifeStyle", lifeStyle.toString())
                    editor.putString("name", fio.toString())
                    editor.putString("sex", sex.toString())
                    editor.putString("weight", weight.toString())
                    editor.putString("workTime", workTime.toString())

                    editor.apply()

                }

                database = FirebaseDatabase.getInstance().getReference("Users")
                database.child(FirebaseAuth.getInstance().currentUser!!.uid).child("MainWorkInfo")
                    .get()
                    .addOnSuccessListener {
                        if (it.exists()) {
                            val waterNorm = it.child("waterNorm").value
                            editor.putString("waterNorm", waterNorm.toString())
                            editor.apply()

                        }
                    }
            }
    }

    private fun replaceFragment(fragment: Fragment) {
        val fragmentManager = supportFragmentManager
        val fragmentTransaction = fragmentManager.beginTransaction()
        fragmentTransaction.replace(R.id.frame_layout, fragment)
        fragmentTransaction.commit()
    }
}