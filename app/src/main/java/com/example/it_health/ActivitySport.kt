package com.example.it_health

import android.content.Intent
import androidx.appcompat.app.AppCompatActivity
import android.os.Bundle
import com.example.it_health.fragments.Todo
import com.google.android.material.bottomnavigation.BottomNavigationView

class ActivitySport : AppCompatActivity() {
    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        setContentView(R.layout.activity_sport)


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

    }
}