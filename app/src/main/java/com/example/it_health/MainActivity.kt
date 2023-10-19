package com.example.it_health

import android.content.Intent
import android.os.Bundle
import android.view.View
import android.view.WindowManager
import android.widget.Button
import android.widget.Toast
import androidx.appcompat.app.AppCompatActivity

class MainActivity : AppCompatActivity() {
    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        setContentView(R.layout.activity_main)
            //скрыть строку состояния
        window.setFlags(WindowManager.LayoutParams.FLAG_FULLSCREEN,WindowManager.LayoutParams.FLAG_FULLSCREEN)
            //создание переменных кнопок
        val RegBtn = findViewById<Button>(R.id.registration)
        val AuthBtn = findViewById<Button>(R.id.authorization)

            //кнопка регистрации
        RegBtn.setOnClickListener(){
           val intent =Intent(this,RegActivity::class.java)
            startActivity(intent)
        }
            //кнопка авторизации
        AuthBtn.setOnClickListener(){
            val intent =Intent(this,AuthorActivity::class.java)
            startActivity(intent)
        }




    }
}