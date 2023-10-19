package com.example.it_health

import android.content.Intent
import androidx.appcompat.app.AppCompatActivity
import android.os.Bundle
import android.view.WindowManager
import android.widget.Toast
import com.example.it_health.databinding.ActivityAuthorBinding
import com.google.firebase.auth.FirebaseAuth

class AuthorActivity : AppCompatActivity() {
    private lateinit var binding: ActivityAuthorBinding
    private lateinit var firebaseAuth: FirebaseAuth

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        binding = ActivityAuthorBinding.inflate(layoutInflater)
        setContentView(binding.root)

        window.setFlags(
            WindowManager.LayoutParams.FLAG_FULLSCREEN,
            WindowManager.LayoutParams.FLAG_FULLSCREEN
        )


        firebaseAuth = FirebaseAuth.getInstance()

        //зарегистрировать аккаунт из авторизации
        binding.imageButton?.setOnClickListener {
            onBackPressed()
            //   val intent = Intent(this, AuthorActivity::class.java)
            // startActivity(intent)
        }

        binding.authorization.setOnClickListener {

            val email = binding.email?.text.toString()
            val pass = binding.password?.text.toString()

            if (email.isNotEmpty() && pass.isNotEmpty()) {

                firebaseAuth.signInWithEmailAndPassword(email, pass)
                    .addOnCompleteListener() {
                        if (it.isSuccessful) {


                            val intent = Intent(this, ActivityMainMenu::class.java)
                            startActivity(intent)

                        } else {
                            Toast.makeText(this, it.exception.toString(), Toast.LENGTH_SHORT)
                                .show()
                        }

                    }


            } else {
                Toast.makeText(this, "Одно из полей не заполнено", Toast.LENGTH_SHORT).show()
            }
        }


    }
}