package com.binar.c5team.gotravel.view.guestview

import android.os.Bundle
import androidx.fragment.app.Fragment
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import com.binar.c5team.gotravel.R
import com.binar.c5team.gotravel.databinding.FragmentGuestHomeBinding
import com.binar.c5team.gotravel.databinding.FragmentGuestProfileBinding


class GuestProfileFragment : Fragment() {
    lateinit var binding : FragmentGuestProfileBinding
    override fun onCreateView(
        inflater: LayoutInflater, container: ViewGroup?,
        savedInstanceState: Bundle?
    ): View? {
        binding = FragmentGuestProfileBinding.inflate(inflater, container, false)
        return binding.root
    }


}