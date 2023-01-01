package com.binar.c5team.gotravel.view.guestfragment

import android.os.Bundle
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import androidx.fragment.app.Fragment
import androidx.navigation.Navigation
import com.binar.c5team.gotravel.R
import com.binar.c5team.gotravel.databinding.FragmentGuestHistoryBinding
import com.google.android.material.transition.MaterialFadeThrough


class GuestHistoryFragment : Fragment() {
    lateinit var binding : FragmentGuestHistoryBinding
    override fun onCreateView(
        inflater: LayoutInflater, container: ViewGroup?,
        savedInstanceState: Bundle?
    ): View {
        //transition anim
        enterTransition = MaterialFadeThrough()
        reenterTransition = MaterialFadeThrough()
        exitTransition = MaterialFadeThrough()
        //inflating layout
        binding = FragmentGuestHistoryBinding.inflate(inflater, container, false)
        return binding.root
    }

    override fun onViewCreated(view: View, savedInstanceState: Bundle?) {
        super.onViewCreated(view, savedInstanceState)
        binding.loginHistory.setOnClickListener {
            Navigation.findNavController(view).navigate(R.id.action_guestHistoryFragment_to_loginFragment)
        }
    }


}