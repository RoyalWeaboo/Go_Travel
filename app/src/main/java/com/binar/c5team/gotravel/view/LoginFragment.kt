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
import androidx.navigation.Navigation
import androidx.navigation.fragment.findNavController
import com.binar.c5team.gotravel.R
import com.binar.c5team.gotravel.databinding.FragmentLoginBinding
import com.binar.c5team.gotravel.model.LoginData
import com.binar.c5team.gotravel.model.LoginResponse
import com.binar.c5team.gotravel.network.RetrofitClient
import com.google.android.material.bottomnavigation.BottomNavigationView
import retrofit2.Call
import retrofit2.Callback
import retrofit2.Response


class LoginFragment : Fragment() {
    lateinit var binding: FragmentLoginBinding
    lateinit var sharedPref: SharedPreferences
    override fun onCreateView(
        inflater: LayoutInflater, container: ViewGroup?,
        savedInstanceState: Bundle?
    ): View? {
        binding = FragmentLoginBinding.inflate(inflater, container, false)
        return binding.root
    }

    override fun onViewCreated(view: View, savedInstanceState: Bundle?) {
        super.onViewCreated(view, savedInstanceState)
        val navBar = requireActivity().findViewById<BottomNavigationView>(R.id.bottom_nav)
        navBar.visibility = View.GONE
        binding.loginProgressBar.visibility = View.GONE
        sharedPref = requireActivity().getSharedPreferences("data", Context.MODE_PRIVATE)

        binding.btnBack.setOnClickListener {
            findNavController().navigate(R.id.action_loginFragment_to_openingFragment)
        }

        binding.btnLogin.setOnClickListener {
            validateLoginInput()
        }
        binding.tvRegister.setOnClickListener {
            Navigation.findNavController(view)
                .navigate(R.id.action_loginFragment_to_registerFragment)
        }
    }

    private fun validateLoginInput() {
        val usernameInput = binding.inputUsername.editText?.text.toString()
        val passwordinput = binding.inputPassword.editText?.text.toString()

        if (usernameInput.isNotEmpty() && passwordinput.isNotEmpty()) {
            binding.loginProgressBar.visibility = View.VISIBLE
            validateLoginData(usernameInput, passwordinput)
        } else {
            Toast.makeText(context, "Username or Password can't be empty !", Toast.LENGTH_SHORT)
                .show()
        }
    }

    private fun validateLoginData(username: String, password: String) {
        RetrofitClient.apiInstance.login(LoginData(username, password))
            .enqueue(object : Callback<LoginResponse> {
                override fun onResponse(
                    call: Call<LoginResponse>,
                    response: Response<LoginResponse>
                ) {
                    if (response.isSuccessful) {
                        if (response.body()?.message == "Login Success") {
                            Log.d("login response", response.body().toString())
                            val saveData = sharedPref.edit()
                            saveData.putString("session", "true")
                            saveData.putInt("userId", response.body()!!.id)
                            saveData.putString("username", response.body()?.username)
                            saveData.putString("token", response.body()?.token)
                            saveData.apply()
                            binding.loginProgressBar.visibility = View.GONE
                            findNavController().navigate(R.id.action_loginFragment_to_homeFragment)
                        }
                    } else {
                        Log.d("login response", response.body().toString())
                        binding.loginProgressBar.visibility = View.GONE
                        Toast.makeText(
                            context,
                            "Wrong Username or Password",
                            Toast.LENGTH_SHORT
                        ).show()
                    }
                }

                override fun onFailure(call: Call<LoginResponse>, t: Throwable) {
                    Log.d("Login Data Error", call.toString())
                    binding.loginProgressBar.visibility = View.GONE
                    Toast.makeText(
                        context,
                        "Something Went Wrong",
                        Toast.LENGTH_SHORT
                    ).show()
                }

            })
        binding.loginProgressBar.visibility = View.GONE
    }
}
