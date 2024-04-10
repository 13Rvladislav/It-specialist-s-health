package com.example.it_health

import android.content.Context
import android.content.Intent
import android.os.Bundle
import android.view.View
import android.view.WindowManager
import android.widget.Button
import android.widget.TextView
import android.widget.Toast
import androidx.appcompat.app.AppCompatActivity
import com.google.firebase.auth.FirebaseAuth

class MainActivity : AppCompatActivity() {

    private lateinit var auth: FirebaseAuth
    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        setContentView(R.layout.activity_main)
            //скрыть строку состояния
        window.setFlags(WindowManager.LayoutParams.FLAG_FULLSCREEN,WindowManager.LayoutParams.FLAG_FULLSCREEN)
            //создание переменных кнопок
        val RegBtn = findViewById<TextView>(R.id.RegBtn)
        val AuthBtn = findViewById<Button>(R.id.authorization)
        auth = FirebaseAuth.getInstance()

        // Проверяем, авторизован ли пользователь
        val currentUser = auth.currentUser
        if (currentUser != null) {
            // Пользователь уже авторизован, переходим на главный экран
            startActivity(Intent(this, ActivityMenu::class.java))
        } else {


            //кнопка регистрации
            RegBtn.setOnClickListener() {
                val intent = Intent(this, RegActivity::class.java)
                startActivity(intent)
            }
            //кнопка авторизации
            AuthBtn.setOnClickListener() {
                val intent = Intent(this, AuthorActivity::class.java)
                startActivity(intent)
            }
        }



    }
}