package com.example.it_health

import android.annotation.SuppressLint
import android.content.Intent
import androidx.appcompat.app.AppCompatActivity
import android.os.Bundle
import android.widget.Toast
import com.example.it_health.databinding.ActivityRegBinding

import com.google.android.gms.tasks.OnCompleteListener
import com.google.firebase.auth.FirebaseAuth
import com.google.firebase.database.FirebaseDatabase

import com.google.firebase.ktx.Firebase
import dbClases.MainWorkInfo
import dbClases.Users

class RegActivity : AppCompatActivity() {

    private lateinit var binding: ActivityRegBinding
    private lateinit var firebaseAuth: FirebaseAuth


    @SuppressLint("RestrictedApi")
    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        setContentView(R.layout.activity_reg)

        binding = ActivityRegBinding.inflate(layoutInflater)
        setContentView(binding.root)
        firebaseAuth = FirebaseAuth.getInstance()
        binding.imageButton?.setOnClickListener {
            onBackPressed()
        }
        binding.BtnReg?.setOnClickListener {

            val email = binding.email.getEditText()?.getText().toString()
            val pass = binding.Password.getEditText()?.getText().toString()
            val confirmPass = binding.ConfirmPassword.getEditText()?.getText().toString()

            var name: String = "Пользователь"
            var height: String = "170"
            var weight: String = "66"
            var sex: String = "муж"
            var workTime: String = "8ч"
            var lifeStyle: String = "Умеренный"

            var water: String = "0"
            var step: String = "0"
            var sleep: String = "0"
            var waterNorm: String = "0"

            val mainInfo = MainWorkInfo(
                water,
                step,
                sleep,
                waterNorm,
            )

            val user = Users(
                name,
                height,
                weight,
                sex,
                workTime,
                lifeStyle,
            )
// TODO: написать обязательный ввод имени компании и сделать верификацию
            if (email.isNotEmpty() && pass.isNotEmpty() && confirmPass.isNotEmpty()) {
                if (pass.length > 6) {
                    if (pass == confirmPass) {
                        firebaseAuth.createUserWithEmailAndPassword(email, pass)
                            .addOnCompleteListener() {
                                if (it.isSuccessful) {
                                    FirebaseDatabase.getInstance().getReference("Users")
                                        .child(FirebaseAuth.getInstance().currentUser!!.uid)
                                        .child("MainWorkInfo").setValue(mainInfo)


                                    FirebaseDatabase.getInstance().getReference("Users")
                                        .child(FirebaseAuth.getInstance().currentUser!!.uid)
                                        .child("User-info").setValue(user)
                                        .addOnCompleteListener(OnCompleteListener<Void> {
                                            val intent = Intent(this, AuthorActivity::class.java)
                                            startActivity(intent)

                                        })

                                } else {
                                    Toast.makeText(
                                        this,
                                        it.exception.toString(),
                                        Toast.LENGTH_SHORT
                                    )
                                        .show()
                                }
                                firebaseAuth.currentUser?.sendEmailVerification()
                                    ?.addOnCompleteListener { emailTask ->
                                        if (emailTask.isSuccessful) {
                                            Toast.makeText(
                                                this,
                                                "Письмо для подтверждения успешно отправлено",
                                                Toast.LENGTH_SHORT
                                            )
                                                .show()
                                        } else {
                                            Toast.makeText(
                                                this,
                                                "Письмо для подтверждения успешно отправлено",
                                                Toast.LENGTH_SHORT
                                            )
                                                .show()
                                        }
                                    }
                            }

                    } else {
                        Toast.makeText(this, "Пароли не совпадают", Toast.LENGTH_SHORT).show();
                    }
                } else {
                    Toast.makeText(this, "Длина пароля меньше 6 символов", Toast.LENGTH_SHORT)
                        .show()
                }
            } else {
                Toast.makeText(this, "Одно из полей не заполнено", Toast.LENGTH_SHORT).show();
            }
        }

    }
}


