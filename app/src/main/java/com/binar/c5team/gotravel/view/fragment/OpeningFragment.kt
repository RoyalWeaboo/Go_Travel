package com.binar.c5team.gotravel.view.fragment

import android.os.Bundle
import androidx.fragment.app.Fragment
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import androidx.navigation.Navigation
import com.binar.c5team.gotravel.R
import com.binar.c5team.gotravel.databinding.FragmentLoginOrRegisterBinding
import com.google.android.material.bottomnavigation.BottomNavigationView
import com.google.android.material.transition.MaterialFadeThrough
import com.google.android.material.transition.MaterialSharedAxis

class OpeningFragment : Fragment() {
    lateinit var binding : FragmentLoginOrRegisterBinding

    override fun onCreateView(
        inflater: LayoutInflater, container: ViewGroup?,
        savedInstanceState: Bundle?
    ): View {
        //transition anim
        enterTransition = MaterialFadeThrough()
        exitTransition = MaterialSharedAxis(MaterialSharedAxis.X,true)
        reenterTransition = MaterialSharedAxis(MaterialSharedAxis.X,false)
        binding = FragmentLoginOrRegisterBinding.inflate(inflater, container, false)
        return binding.root
    }

    override fun onViewCreated(view: View, savedInstanceState: Bundle?) {
        super.onViewCreated(view, savedInstanceState)
        //throw RuntimeException("Test Crash") // Force a crash

        val navBar = requireActivity().findViewById<BottomNavigationView>(R.id.bottom_nav)
        navBar.visibility = View.GONE
        val guestNavBar = requireActivity().findViewById<BottomNavigationView>(R.id.guest_bottom_nav)
        guestNavBar.visibility = View.GONE

        binding.skipLogin.setOnClickListener {
            val bun = Bundle()
            bun.putString("fromDes", "opening")
            Navigation.findNavController(view).navigate(R.id.action_openingFragment_to_guestHomeFragment, bun)
        }

        binding.toCreate.setOnClickListener {
            val bun = Bundle()
            bun.putString("fromDes", "opening")
            Navigation.findNavController(view).navigate(R.id.action_openingFragment_to_registerFragment, bun)
        }

        binding.toLogin.setOnClickListener {
            Navigation.findNavController(view).navigate(R.id.action_openingFragment_to_loginFragment)
        }
    }


}