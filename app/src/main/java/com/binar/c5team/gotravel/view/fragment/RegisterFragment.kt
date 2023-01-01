package com.binar.c5team.gotravel.view.fragment

import android.app.DatePickerDialog
import android.content.Context
import android.net.ConnectivityManager
import android.os.Bundle
import android.util.Log
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import android.widget.ArrayAdapter
import android.widget.AutoCompleteTextView
import android.widget.Toast
import androidx.fragment.app.Fragment
import androidx.lifecycle.ViewModelProvider
import androidx.navigation.Navigation
import com.binar.c5team.gotravel.R
import com.binar.c5team.gotravel.databinding.FragmentRegisterBinding
import com.binar.c5team.gotravel.viewmodel.FlightViewModel
import com.google.android.material.bottomnavigation.BottomNavigationView
import com.google.android.material.transition.MaterialSharedAxis
import java.text.SimpleDateFormat
import java.util.*

class RegisterFragment : Fragment() {
    lateinit var binding : FragmentRegisterBinding

    //progressbar
    var progressView: ViewGroup? = null
    private var isProgressShowing = false

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
        binding = FragmentRegisterBinding.inflate(inflater, container, false)
        return binding.root
    }

    override fun onViewCreated(view: View, savedInstanceState: Bundle?) {
        super.onViewCreated(view, savedInstanceState)
        val navBar = requireActivity().findViewById<BottomNavigationView>(R.id.bottom_nav)
        navBar.visibility = View.GONE
        val guestNavBar = requireActivity().findViewById<BottomNavigationView>(R.id.guest_bottom_nav)
        guestNavBar.visibility = View.GONE

        checkConnection()

        viewModel.loading.observe(viewLifecycleOwner) {
            when (it) {
                true -> showProgressingView()
                false -> hideProgressingView()
            }
        }

        // for dropdown menu gender
        val items = listOf("Male", "Female")
        val adapter = ArrayAdapter(requireContext(), R.layout.list_item, items)
        (binding.inputGender.editText as? AutoCompleteTextView)?.setAdapter(adapter)

        binding.btnBack.setOnClickListener {
            Navigation.findNavController(view).navigate(R.id.action_registerFragment_to_openingFragment)
        }

        binding.pickDate.setOnClickListener {
            openDatePicker()
        }

        binding.btnRegister.setOnClickListener {
            if (connection) {
                validateInput(view)
            }else{
                Toast.makeText(
                    context,
                    "No internet connection",
                    Toast.LENGTH_SHORT
                ).show()
            }
        }

    }

    private fun validateInput(view : View) {
        val username = binding.inputUsername.editText?.text.toString()
        val fullname = binding.inputFullname.editText?.text.toString()
        val email = binding.inputEmail.editText?.text.toString()
        val password = binding.inputPassword.editText?.text.toString()
        val conPassword = binding.inputConfirmPassword.editText?.text.toString()
        val birthDate = binding.inputDate.editText?.text.toString()
        val gender = binding.inputGender.editText?.text.toString()
        val address = binding.inputAddr.editText?.text.toString()

        if (conPassword == password){
            if (username.isNotBlank()&&fullname.isNotBlank()&&email.isNotBlank()&&password.isNotBlank()&&birthDate.isNotBlank()&&gender.isNotBlank()&&address.isNotBlank()){
                register(view, username, fullname, email, password, birthDate, gender, address)
            }else{
                Toast.makeText(context, "You must fill all data required !", Toast.LENGTH_SHORT)
                    .show()
            }
        }else{
            Toast.makeText(context, "Password Do Not Match !", Toast.LENGTH_SHORT)
                .show()
        }
    }

    private fun register(
        view : View,
        username: String,
        fullname: String,
        email: String,
        password: String,
        birthDate: String,
        gender: String,
        address: String
    ) {
        showProgressingView()
        viewModel = ViewModelProvider(this)[FlightViewModel::class.java]
        viewModel.getRegisterData().observe(viewLifecycleOwner) {
            if (it!=null) {
                Log.d("Register Response :", it.toString())
                Toast.makeText(
                    context,
                    "Succesfully register a new account !",
                    Toast.LENGTH_SHORT
                ).show()
                hideProgressingView()
                Navigation.findNavController(view).navigate(R.id.action_registerFragment_to_loginFragment)
            } else {
                Toast.makeText(
                    requireActivity(),
                    "Registration Failed",
                    Toast.LENGTH_SHORT
                ).show()
                hideProgressingView()
            }
        }
        viewModel.callRegisterApi(username, fullname, email, password, birthDate, gender, address)
    }

    private fun openDatePicker() {
        val calendar = Calendar.getInstance()
        val year = calendar.get(Calendar.YEAR)
        val month = calendar.get(Calendar.MONTH)
        val day = calendar.get(Calendar.DAY_OF_MONTH)

        val datePickerDialog = DatePickerDialog(
            requireActivity(),
            { _, y, m, d ->
                val formatter = SimpleDateFormat("yyyy-MM-dd", Locale.getDefault())
                val date = Calendar.getInstance()
                date.set(y, m, d)
                val dateString = formatter.format(date.time)
                binding.inputDate.editText?.setText(dateString)
            },
            year,
            month,
            day
        )
        datePickerDialog.datePicker.maxDate = System.currentTimeMillis()

        datePickerDialog.show()
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

}