package com.binar.c5team.gotravel.view.guestfragment

import android.os.Bundle
import androidx.fragment.app.Fragment
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import androidx.navigation.Navigation
import com.binar.c5team.gotravel.R
import com.binar.c5team.gotravel.databinding.FragmentGuestProfileBinding
import com.google.android.material.transition.MaterialFadeThrough

class GuestProfileFragment : Fragment() {
    lateinit var binding : FragmentGuestProfileBinding
    override fun onCreateView(
        inflater: LayoutInflater, container: ViewGroup?,
        savedInstanceState: Bundle?
    ): View {
        //transition anim
        enterTransition = MaterialFadeThrough()
        reenterTransition = MaterialFadeThrough()
        exitTransition = MaterialFadeThrough()
        //inflating layout
        binding = FragmentGuestProfileBinding.inflate(inflater, container, false)
        return binding.root
    }

    override fun onViewCreated(view: View, savedInstanceState: Bundle?) {
        super.onViewCreated(view, savedInstanceState)
        binding.loginProfile.setOnClickListener {
            Navigation.findNavController(view).navigate(R.id.action_guestProfileFragment_to_loginFragment)
        }
    }

}