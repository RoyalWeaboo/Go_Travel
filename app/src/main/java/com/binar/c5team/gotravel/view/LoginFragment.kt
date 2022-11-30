package com.binar.c5team.gotravel.view

import android.os.Bundle
import androidx.fragment.app.Fragment
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import android.widget.Toast
import com.binar.c5team.gotravel.R
import com.binar.c5team.gotravel.databinding.FragmentLoginDummyBinding

class LoginFragment : Fragment() {
    lateinit var binding : FragmentLoginDummyBinding
    override fun onCreateView(
        inflater: LayoutInflater, container: ViewGroup?,
        savedInstanceState: Bundle?
    ): View? {
        // Inflate the layout for this fragment
        binding = FragmentLoginDummyBinding.inflate(inflater, container, false)
        return binding.root
    }

    override fun onViewCreated(view: View, savedInstanceState: Bundle?) {
        super.onViewCreated(view, savedInstanceState)
        validateLoginInput()
    }

    private fun validateLoginInput() {
        if(binding.etUsername.text.isNotEmpty() && binding.etPassword.text.isEmpty()){
            validateLoginData()
        }else{
            Toast.makeText(context, "Username or Password can't be empty !", Toast.LENGTH_SHORT).show()
        }
    }

    private fun validateLoginData() {
        //do request
    }




}