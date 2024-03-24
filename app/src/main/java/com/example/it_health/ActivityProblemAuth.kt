package com.example.it_health

import android.content.Intent
import androidx.appcompat.app.AppCompatActivity
import android.os.Bundle
import android.widget.Button
import android.widget.Toast
import com.example.it_health.databinding.ActivityAuthorBinding
import com.example.it_health.databinding.ActivityAuthorBinding.inflate
import com.example.it_health.databinding.ActivityProblemAuthBinding
import com.example.it_health.databinding.ActivityRegBinding
import com.google.firebase.auth.FirebaseAuth


class ActivityProblemAuth : AppCompatActivity() {

    private lateinit var binding: ActivityProblemAuthBinding


    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        setContentView(R.layout.activity_problem_auth)
        binding = ActivityProblemAuthBinding.inflate(layoutInflater)
        setContentView(binding.root)
        //кнопка назад
        binding.imageButton?.setOnClickListener {
            onBackPressed()
        }


        //кнопка регистрации
        binding.ResetPassBtn.setOnClickListener() {
            val email = binding.email?.text.toString()
            if (email.isNotEmpty() ) {
                FirebaseAuth.getInstance().sendPasswordResetEmail(email)
                    .addOnCompleteListener { task ->
                        if (task.isSuccessful) {
                            Toast.makeText(this, "Письмо для восстановления пароля успешно отправлено", Toast.LENGTH_SHORT)
                                .show()
                        } else {
                            Toast.makeText(this,"При отправке письма произошла ошибка", Toast.LENGTH_SHORT)
                                .show()
                        }
                    }
            }




            val intent = Intent(this, AuthorActivity::class.java)
            startActivity(intent)
        }


    }
}