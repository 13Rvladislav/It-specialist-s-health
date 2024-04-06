package com.example.it_health

import androidx.appcompat.app.AppCompatActivity
import android.os.Bundle
import androidx.fragment.app.Fragment
import com.example.it_health.databinding.ActivityMenuBinding
import com.example.it_health.fragments.Home
import com.example.it_health.fragments.Profile
import com.example.it_health.fragments.Sport
import com.example.it_health.fragments.Todo

class ActivityMenu : AppCompatActivity() {

    private lateinit var binding: ActivityMenuBinding
    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        binding=ActivityMenuBinding.inflate(layoutInflater)
        setContentView(binding.root)
        binding.bottomNavigationView.setSelectedItemId(R.id.home);
        replaceFragment(Home())

        binding.bottomNavigationView.setOnItemSelectedListener {
            when(it.itemId){
                R.id.sport->replaceFragment(Sport())
                R.id.home->replaceFragment(Home())
                R.id.todo->replaceFragment(Todo())
                R.id.profile->replaceFragment(Profile())

                else->{

                }
            }
            true
        }
    }

    private  fun  replaceFragment(fragment: Fragment){
        val fragmentManager=supportFragmentManager
        val fragmentTransaction =fragmentManager.beginTransaction()
        fragmentTransaction.replace(R.id.frame_layout,fragment)
        fragmentTransaction.commit()
    }
}