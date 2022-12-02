package com.binar.c5team.gotravel.view

import android.app.DatePickerDialog
import android.content.Context
import android.content.SharedPreferences
import android.os.Bundle
import androidx.fragment.app.Fragment
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import android.widget.AdapterView
import android.widget.ArrayAdapter
import android.widget.Toast
import androidx.core.view.get
import androidx.navigation.Navigation
import androidx.navigation.fragment.findNavController
import androidx.navigation.ui.NavigationUI
import com.binar.c5team.gotravel.R
import com.binar.c5team.gotravel.databinding.FragmentHomeBinding
import com.binar.c5team.gotravel.databinding.FragmentProfileBinding
import com.google.android.material.bottomnavigation.BottomNavigationView
import java.text.SimpleDateFormat
import java.util.*

class ProfileFragment : Fragment() {

    private lateinit var binding: FragmentProfileBinding
    private lateinit var sharedPref : SharedPreferences

    private val gender = arrayOf("Pria","Wanita")

    override fun onCreateView(
        inflater: LayoutInflater, container: ViewGroup?,
        savedInstanceState: Bundle?
    ): View? {
        binding = FragmentProfileBinding.inflate(inflater, container, false)
        return binding.root
    }

    override fun onViewCreated(view: View, savedInstanceState: Bundle?) {
        super.onViewCreated(view, savedInstanceState)
        val navBar = requireActivity().findViewById<BottomNavigationView>(com.binar.c5team.gotravel.R.id.bottom_nav)
        navBar.visibility = View.VISIBLE

        sharedPref = requireActivity().getSharedPreferences("data", Context.MODE_PRIVATE)

        binding.btnCustomerService.setOnClickListener {

        }

        binding.btnChangePassword.setOnClickListener {

        }

        binding.btnHelp.setOnClickListener {

        }

        binding.btnGoTravelApp.setOnClickListener {

        }

        binding.btnDatePicker.setOnClickListener {
            datePicker()
        }

        binding.btnAddImage.setOnClickListener {

        }

        binding.btnLogout.setOnClickListener {
            val clearData = sharedPref.edit()
            clearData.clear()
            clearData.apply()
            Navigation.findNavController(view).navigate(R.id.action_profileFragment_to_loginFragment)
            Toast.makeText(context, "Logout Successful !", Toast.LENGTH_SHORT)
                .show()
        }

        binding.chooseGender.adapter = ArrayAdapter<String>(this.requireActivity(),android.R.layout.simple_list_item_1,gender)
        binding.chooseGender.onItemSelectedListener = object : AdapterView.OnItemSelectedListener {
            override fun onItemSelected(p0: AdapterView<*>?, p1: View?, position: Int, p3: Long) {
                binding.tvGender.text = gender.get(position)
            }

            override fun onNothingSelected(p0: AdapterView<*>?) {
                binding.tvGender.text = "Gender"
            }

        }
    }

    private fun datePicker() {
        val c = Calendar.getInstance()
        val year = c.get(Calendar.YEAR)
        val month = c.get(Calendar.MONTH)
        val day = c.get(Calendar.DAY_OF_MONTH)

        val dpd = DatePickerDialog(requireActivity(), DatePickerDialog.OnDateSetListener { view, year, monthOfYear, dayOfMonth ->

            // Display Selected date in textbox
            val sdf = SimpleDateFormat("MMM")
            val monthName = sdf.format(c.time)
            binding.tvDate.text = "" + monthName + " " + dayOfMonth + ", " + year

        }, year, month, day)
        dpd.show()
    }
}