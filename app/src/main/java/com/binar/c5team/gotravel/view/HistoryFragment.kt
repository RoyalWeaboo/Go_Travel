package com.binar.c5team.gotravel.view

import android.os.Bundle
import androidx.fragment.app.Fragment
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import androidx.navigation.fragment.findNavController
import androidx.navigation.ui.NavigationUI
import com.binar.c5team.gotravel.R
import com.binar.c5team.gotravel.databinding.FragmentHistoryBinding
import com.binar.c5team.gotravel.databinding.FragmentProfileBinding
import com.google.android.material.bottomnavigation.BottomNavigationView

class HistoryFragment : Fragment() {
    lateinit var binding : FragmentHistoryBinding
    override fun onCreateView(
        inflater: LayoutInflater, container: ViewGroup?,
        savedInstanceState: Bundle?
    ): View? {
        binding = FragmentHistoryBinding.inflate(inflater, container, false)
        return binding.root
    }

    override fun onViewCreated(view: View, savedInstanceState: Bundle?) {
        super.onViewCreated(view, savedInstanceState)
        val navBar = requireActivity().findViewById<BottomNavigationView>(com.binar.c5team.gotravel.R.id.bottom_nav)
        navBar.visibility = View.VISIBLE
    }

}