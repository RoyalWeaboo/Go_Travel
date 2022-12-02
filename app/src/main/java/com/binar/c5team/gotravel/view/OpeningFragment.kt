package com.binar.c5team.gotravel.view

import android.os.Bundle
import androidx.fragment.app.Fragment
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import androidx.navigation.Navigation
import com.binar.c5team.gotravel.R
import com.binar.c5team.gotravel.databinding.FragmentLoginOrRegisterBinding
import com.binar.c5team.gotravel.databinding.FragmentRegisterBinding

class OpeningFragment : Fragment() {
    lateinit var binding : FragmentLoginOrRegisterBinding
    override fun onCreateView(
        inflater: LayoutInflater, container: ViewGroup?,
        savedInstanceState: Bundle?
    ): View? {
        // Inflate the layout for this fragment
        binding = FragmentLoginOrRegisterBinding.inflate(inflater, container, false)
        return binding.root
    }

    override fun onViewCreated(view: View, savedInstanceState: Bundle?) {
        super.onViewCreated(view, savedInstanceState)
        binding.toCreate.setOnClickListener {
            Navigation.findNavController(view).navigate(R.id.action_openingFragment_to_registerFragment)
        }
        binding.toLogin.setOnClickListener {
            Navigation.findNavController(view).navigate(R.id.action_openingFragment_to_loginFragment)
        }
    }

    override fun onDetach() {
        super.onDetach()
    }

}