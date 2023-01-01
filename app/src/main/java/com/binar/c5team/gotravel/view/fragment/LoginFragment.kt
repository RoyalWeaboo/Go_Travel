package com.binar.c5team.gotravel.view.fragment

import android.content.Context
import android.content.SharedPreferences
import android.net.ConnectivityManager
import android.os.Bundle
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import android.widget.Toast
import androidx.fragment.app.Fragment
import androidx.lifecycle.ViewModelProvider
import androidx.navigation.Navigation
import androidx.navigation.fragment.findNavController
import com.binar.c5team.gotravel.R
import com.binar.c5team.gotravel.databinding.FragmentLoginBinding
import com.binar.c5team.gotravel.viewmodel.FlightViewModel
import com.google.android.material.bottomnavigation.BottomNavigationView
import com.google.android.material.transition.MaterialSharedAxis

class LoginFragment : Fragment() {
    lateinit var binding: FragmentLoginBinding

    //shared preferences
    lateinit var sharedPref: SharedPreferences

    //progressbar
    var progressView: ViewGroup? = null
    private var isProgressShowing = false

    //data
    var userId: Int = 0
    var usernameRes: String = ""
    var token: String = ""

    //viewmodel
    lateinit var viewModel: FlightViewModel

    //connection
    private var connection : Boolean = false

    override fun onCreateView(
        inflater: LayoutInflater, container: ViewGroup?,
        savedInstanceState: Bundle?
    ): View {
        //transition anim
        enterTransition = MaterialSharedAxis(MaterialSharedAxis.X,true)
        exitTransition = MaterialSharedAxis(MaterialSharedAxis.X,true)
        reenterTransition = MaterialSharedAxis(MaterialSharedAxis.X,false)
        //vm
        viewModel = ViewModelProvider(this)[FlightViewModel::class.java]
        //inflating layout
        binding = FragmentLoginBinding.inflate(inflater, container, false)
        return binding.root
    }

    override fun onViewCreated(view: View, savedInstanceState: Bundle?) {
        super.onViewCreated(view, savedInstanceState)
        val navBar = requireActivity().findViewById<BottomNavigationView>(R.id.bottom_nav)
        navBar.visibility = View.GONE
        val guestNavBar = requireActivity().findViewById<BottomNavigationView>(R.id.guest_bottom_nav)
        guestNavBar.visibility = View.GONE

        checkConnection()

        sharedPref = requireActivity().getSharedPreferences("data", Context.MODE_PRIVATE)

        binding.btnBack.setOnClickListener {
            Navigation.findNavController(view)
                .navigate(R.id.action_loginFragment_to_openingFragment)
        }

        binding.btnLogin.setOnClickListener {
            if (connection){
                validateLoginInput(view)
            }else{
                Toast.makeText(context, "No internet connection", Toast.LENGTH_SHORT).show()
            }

        }
        binding.tvRegister.setOnClickListener {
            Navigation.findNavController(view)
                .navigate(R.id.action_loginFragment_to_registerFragment)
        }
    }

    private fun validateLoginInput(view: View) {
        val usernameInput = binding.inputUsername.editText?.text.toString()
        val passwordinput = binding.inputPassword.editText?.text.toString()

        if (usernameInput.isNotEmpty() && passwordinput.isNotEmpty()) {
            validateLoginData(view, usernameInput, passwordinput)
        } else {
            Toast.makeText(context, "Username or Password can't be empty !", Toast.LENGTH_SHORT)
                .show()
        }
    }

    private fun validateLoginData(view: View, username: String, password: String) {
        showProgressingView()
        viewModel.postLoginApi(this, username, password)

        viewModel.loading.observe(viewLifecycleOwner) {
            if (!it){
                val saveData = sharedPref.edit()
                saveData.putString("session", "true")
                saveData.putInt("userId", userId)
                saveData.putString("username", usernameRes)
                saveData.putString("token", token)
                saveData.apply()
                hideProgressingView()

                //check whether the token was added(which mean login is success)
                if (token != "") {
                    Toast.makeText(context, "Login Successful !", Toast.LENGTH_SHORT).show()
                    Navigation.findNavController(view)
                        .navigate(R.id.action_loginFragment_to_homeFragment)
                }else{
                    refreshCurrentFragment()
                    Toast.makeText(context, "Wrong username or password !", Toast.LENGTH_SHORT).show()
                }
            }
        }
    }

    private fun showProgressingView() {
        if (!isProgressShowing) {
            isProgressShowing = true
            progressView = layoutInflater.inflate(R.layout.progress_bar, null) as ViewGroup
            val v: View = requireView().rootView
            val viewGroup = v as ViewGroup
            viewGroup.addView(progressView)
        }
    }

    private fun hideProgressingView() {
        val v: View = requireView().rootView
        val viewGroup = v as ViewGroup
        viewGroup.removeView(progressView)
        isProgressShowing = false
    }

    private fun checkConnection() {
        val cm = requireActivity().getSystemService(Context.CONNECTIVITY_SERVICE) as ConnectivityManager
        val res = cm.activeNetwork
        connection = res != null
    }

    private fun refreshCurrentFragment(){
        val id = findNavController().currentDestination?.id
        findNavController().popBackStack(id!!,true)
        findNavController().navigate(id)
    }
}
