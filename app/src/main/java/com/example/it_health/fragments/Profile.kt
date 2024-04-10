package com.example.it_health.fragments

import android.content.Intent
import android.os.Bundle
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import androidx.fragment.app.Fragment
import com.example.it_health.MainActivity
import com.example.it_health.R
import com.example.it_health.databinding.FragmentProfileBinding
import com.google.firebase.auth.FirebaseAuth


class Profile : Fragment(R.layout.fragment_profile) {

    private var _binding: FragmentProfileBinding? = null

    private val binding get() = _binding!!
    private lateinit var auth: FirebaseAuth


    override fun onCreateView(
        inflater: LayoutInflater, container: ViewGroup?,
        savedInstanceState: Bundle?
    ): View? {
        _binding = FragmentProfileBinding.inflate(inflater, container, false)
        val view = binding.root
        return view
    }

    override fun onViewCreated(view: View, savedInstanceState: Bundle?) {
        super.onViewCreated(view, savedInstanceState)
        auth = FirebaseAuth.getInstance()




        //кнопка выхода
        binding.Exit.setOnClickListener{
            val intent = Intent(activity, MainActivity::class.java)
            FirebaseAuth.getInstance().signOut();
            startActivity(intent)
        }
    }




    override fun onDestroyView() {
        super.onDestroyView()
        _binding = null
    }

}