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
import dbClases.Userss

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
            val companyName = binding.name?.text.toString()
            val email = binding.email?.text.toString()
            val pass = binding.Password?.text.toString()
            val confirmPass = binding.ConfirmPassword?.text.toString()
            val user = Userss(
                companyName
            )
// TODO: написать обязательный ввод имени компании и сделать верификацию
            if (email.isNotEmpty() && pass.isNotEmpty() && confirmPass.isNotEmpty()) {
                if (pass == confirmPass) {
                    firebaseAuth.createUserWithEmailAndPassword(email, pass)
                        .addOnCompleteListener() {
                            if (it.isSuccessful) {
                                FirebaseDatabase.getInstance().getReference("Users")
                                    .child(FirebaseAuth.getInstance().currentUser!!.uid)
                                    .setValue(user).addOnCompleteListener(OnCompleteListener<Void> {
                                        val intent = Intent(this, AuthorActivity::class.java)
                                        startActivity(intent)
                                    })

                            } else {
                                Toast.makeText(this, it.exception.toString(), Toast.LENGTH_SHORT)
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
                    Toast.makeText(this, "Пароли не совпадают", Toast.LENGTH_SHORT).show(); }
            } else {
                Toast.makeText(this, "Одно из полей не заполнено", Toast.LENGTH_SHORT).show();
            }
        }

    }
}


