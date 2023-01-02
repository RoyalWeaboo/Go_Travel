package com.binar.c5team.gotravel.view.fragment

import android.content.Context
import android.content.SharedPreferences
import android.os.Bundle
import android.os.Handler
import android.util.Log
import androidx.fragment.app.Fragment
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import androidx.navigation.Navigation
import com.binar.c5team.gotravel.R
import com.binar.c5team.gotravel.databinding.FragmentSplashScreenBinding
import com.google.android.material.bottomnavigation.BottomNavigationView
import com.google.android.material.transition.MaterialFadeThrough
import com.google.android.material.transition.MaterialSharedAxis

class SplashScreenFragment : Fragment() {
    lateinit var binding : FragmentSplashScreenBinding
    lateinit var sharedPref: SharedPreferences

    override fun onCreateView(
        inflater: LayoutInflater, container: ViewGroup?,
        savedInstanceState: Bundle?
    ): View {
        enterTransition = MaterialFadeThrough()
        exitTransition = MaterialSharedAxis(MaterialSharedAxis.X,true)
        reenterTransition = MaterialSharedAxis(MaterialSharedAxis.X,false)

        binding = FragmentSplashScreenBinding.inflate(inflater, container, false)
        return binding.root
    }

    override fun onViewCreated(view: View, savedInstanceState: Bundle?) {
        super.onViewCreated(view, savedInstanceState)
        //throw RuntimeException("Test Crash") // Force a crash

        val navBar = requireActivity().findViewById<BottomNavigationView>(R.id.bottom_nav)
        navBar.visibility = View.GONE
        val guestNavBar = requireActivity().findViewById<BottomNavigationView>(R.id.guest_bottom_nav)
        guestNavBar.visibility = View.GONE

        Handler().postDelayed({
            sharedPref = requireActivity().getSharedPreferences("data", Context.MODE_PRIVATE)
            val session = sharedPref.getString("session","false").toString()
            if (session == "true"){
                Navigation.findNavController(view).navigate(R.id.action_splashScreenFragment_to_homeFragment)
                navBar.visibility = View.VISIBLE
                guestNavBar.visibility = View.GONE
            }else{
                Navigation.findNavController(view).navigate(R.id.action_splashScreenFragment_to_openingFragment)
                Log.d("Session status", session)
            }
        }, 3000)
    }


}