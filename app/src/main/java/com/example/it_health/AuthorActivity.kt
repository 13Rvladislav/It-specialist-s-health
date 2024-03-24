package com.example.it_health

import android.content.Intent
import androidx.appcompat.app.AppCompatActivity
import android.os.Bundle
import android.view.WindowManager
import android.widget.Toast
import com.example.it_health.databinding.ActivityAuthorBinding
import com.google.firebase.auth.FirebaseAuth
import com.google.firebase.ktx.Firebase

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


        // переход  восстановлению пароля problemAuth
        binding.problemAuth?.setOnClickListener {
            val intent = Intent(this, ActivityProblemAuth::class.java)
            startActivity(intent)
           // val intent = Intent(this, ActivityProblemAuth::class.java)
         //  startActivity(intent)

        }

        //возврат назад
        binding.imageButton?.setOnClickListener {
            onBackPressed()
        }

        binding.authorization.setOnClickListener {

            val email = binding.email?.text.toString()
            val pass = binding.password?.text.toString()

            if (email.isNotEmpty() && pass.isNotEmpty()) {

                firebaseAuth.signInWithEmailAndPassword(email, pass)
                    .addOnCompleteListener() {
                        if (it.isSuccessful) {
                            if (firebaseAuth.currentUser?.isEmailVerified == true) {

                                val intent = Intent(this, ActivityMainMenu::class.java)
                                startActivity(intent)
                            }
                            else{
                                Toast.makeText(this, " Вы не подтвердили адресс электронной почты ", Toast.LENGTH_SHORT)
                                    .show()
                            }

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