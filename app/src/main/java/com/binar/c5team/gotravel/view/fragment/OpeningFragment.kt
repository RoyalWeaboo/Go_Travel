package com.binar.c5team.gotravel.view.fragment

import android.content.Context
import android.content.SharedPreferences
import android.os.Bundle
import android.util.Log
import androidx.fragment.app.Fragment
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import androidx.navigation.Navigation
import com.binar.c5team.gotravel.R
import com.binar.c5team.gotravel.databinding.FragmentLoginOrRegisterBinding
import com.google.android.material.bottomnavigation.BottomNavigationView
import dagger.hilt.android.AndroidEntryPoint

class OpeningFragment : Fragment() {
    lateinit var binding : FragmentLoginOrRegisterBinding
    lateinit var sharedPref: SharedPreferences

    override fun onCreateView(
        inflater: LayoutInflater, container: ViewGroup?,
        savedInstanceState: Bundle?
    ): View {
        binding = FragmentLoginOrRegisterBinding.inflate(inflater, container, false)
        return binding.root
    }

    override fun onViewCreated(view: View, savedInstanceState: Bundle?) {
        super.onViewCreated(view, savedInstanceState)
        //throw RuntimeException("Test Crash") // Force a crash

        sharedPref = requireActivity().getSharedPreferences("data", Context.MODE_PRIVATE)
        val session = sharedPref.getString("session","").toString()

        if (session == "true"){
            Navigation.findNavController(view).navigate(R.id.action_openingFragment_to_homeFragment)
            val navBar = requireActivity().findViewById<BottomNavigationView>(R.id.bottom_nav)
            navBar.visibility = View.VISIBLE

            val guestNavBar = requireActivity().findViewById<BottomNavigationView>(R.id.guest_bottom_nav)
            guestNavBar.visibility = View.GONE
        }else{
            Log.d("Session status", session)
        }

        binding.skipLogin.setOnClickListener {
            Navigation.findNavController(view).navigate(R.id.action_openingFragment_to_guestHomeFragment)
        }

        binding.toCreate.setOnClickListener {
            Navigation.findNavController(view).navigate(R.id.action_openingFragment_to_registerFragment)
        }

        binding.toLogin.setOnClickListener {
            Navigation.findNavController(view).navigate(R.id.action_openingFragment_to_loginFragment)
        }
    }


}