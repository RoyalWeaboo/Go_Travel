package com.binar.c5team.gotravel.view

import android.content.Context
import android.content.SharedPreferences
import android.os.Bundle
import android.util.Log
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import android.widget.Toast
import androidx.fragment.app.Fragment
import androidx.lifecycle.ViewModelProvider
import androidx.navigation.Navigation
import com.binar.c5team.gotravel.R
import com.binar.c5team.gotravel.databinding.FragmentLoginBinding
import com.binar.c5team.gotravel.viewmodel.UserViewModel

class LoginFragment : Fragment() {
    lateinit var binding: FragmentLoginBinding
    lateinit var sharedPref : SharedPreferences
    override fun onCreateView(
        inflater: LayoutInflater, container: ViewGroup?,
        savedInstanceState: Bundle?
    ): View? {
        binding = FragmentLoginBinding.inflate(inflater, container, false)
        return binding.root
    }

    override fun onViewCreated(view: View, savedInstanceState: Bundle?) {
        super.onViewCreated(view, savedInstanceState)
        sharedPref = requireActivity().getSharedPreferences("data", Context.MODE_PRIVATE)
        binding.btnLogin.setOnClickListener {
            validateLoginInput(view)
        }
        binding.tvRegister.setOnClickListener {
            Navigation.findNavController(view).navigate(R.id.action_loginFragment_to_registerFragment)
        }
    }

    private fun validateLoginInput(view : View) {
        val usernameInput = binding.inputUsername.editText?.text.toString()
        val passwordinput = binding.inputPassword.editText?.text.toString()
        if (usernameInput.isNotEmpty() && passwordinput.isNotEmpty()) {
            validateLoginData(view, usernameInput, passwordinput)
        } else {
            Toast.makeText(context, "Username or Password can't be empty !", Toast.LENGTH_SHORT)
                .show()
        }
    }

    private fun validateLoginData(view : View, username : String, password : String) {
        val viewModel = ViewModelProvider(requireActivity())[UserViewModel::class.java]
        viewModel.getLoginData().observe(viewLifecycleOwner) {
            if (it.token != "") {
                Log.d("Login Response :", it.toString())
                val saveData = sharedPref.edit()
                saveData.putString("session", "true")
                saveData.putString("username", it.username)
                saveData.putString("token", it.token)
                saveData.apply()
                Navigation.findNavController(view).navigate(R.id.action_loginFragment_to_registerFragment)
            } else {
                Toast.makeText(
                    requireActivity(),
                    "Wrong Username or Password",
                    Toast.LENGTH_SHORT
                ).show()
            }
        }
        viewModel.callLoginApi(username, password)
    }
}
