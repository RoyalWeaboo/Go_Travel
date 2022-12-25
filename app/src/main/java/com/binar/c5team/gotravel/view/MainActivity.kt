package com.binar.c5team.gotravel.view

import android.content.Context
import android.content.SharedPreferences
import android.os.Bundle
import android.util.Log
import androidx.appcompat.app.AppCompatActivity
import androidx.navigation.findNavController
import androidx.navigation.fragment.NavHostFragment
import androidx.navigation.ui.NavigationUI.setupWithNavController
import com.binar.c5team.gotravel.R
import com.binar.c5team.gotravel.databinding.ActivityMainBinding
import com.google.android.material.bottomnavigation.BottomNavigationView
import java.lang.Exception


class MainActivity : AppCompatActivity() {
    lateinit var binding : ActivityMainBinding
    private lateinit var bottomNavigationView : BottomNavigationView
    private lateinit var guestBottomNavigationView : BottomNavigationView
    private lateinit var sharedPref: SharedPreferences
    private var session: String = ""

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)

        binding = ActivityMainBinding.inflate(layoutInflater)
        val view = binding.root
        setContentView(view)

        sharedPref = this.getSharedPreferences("data", Context.MODE_PRIVATE)

        session = sharedPref.getString("session", "false").toString()

        if(session=="true"){
            setUpNavigation()
        }else{
            setUpGuestNavigation()
        }

        try {
            val value = intent.extras!!["redirect"].toString()
            val navHostFragment = supportFragmentManager.findFragmentById(R.id.fragmentContainerView) as NavHostFragment
            val navController = navHostFragment.navController
            if(value == "historyFragment") {
                navController.navigate(R.id.historyFragment)
            }else{
                navController.navigate(R.id.openingFragment)
            }
        }catch (e : Exception){
            Log.d("Error", e.toString())
        }
    }

    private fun setUpGuestNavigation() {
        guestBottomNavigationView = binding.guestBottomNav
        val navHostFragment = supportFragmentManager
            .findFragmentById(R.id.fragmentContainerView) as NavHostFragment?
        setupWithNavController(
            guestBottomNavigationView,
            navHostFragment!!.navController
        )
    }


    private fun setUpNavigation() {
        bottomNavigationView = binding.bottomNav
        val navHostFragment = supportFragmentManager
            .findFragmentById(R.id.fragmentContainerView) as NavHostFragment?
        setupWithNavController(
            bottomNavigationView,
            navHostFragment!!.navController
        )
    }
}