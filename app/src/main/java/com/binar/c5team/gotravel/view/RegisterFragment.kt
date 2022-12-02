package com.binar.c5team.gotravel.view

import android.app.DatePickerDialog
import android.os.Bundle
import androidx.fragment.app.Fragment
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import android.widget.ArrayAdapter
import android.widget.AutoCompleteTextView
import androidx.navigation.Navigation
import com.binar.c5team.gotravel.R
import com.binar.c5team.gotravel.databinding.FragmentRegisterBinding
import com.google.android.material.bottomnavigation.BottomNavigationView
import java.text.SimpleDateFormat
import java.util.*

class RegisterFragment : Fragment() {
    lateinit var binding : FragmentRegisterBinding
    override fun onCreateView(
        inflater: LayoutInflater, container: ViewGroup?,
        savedInstanceState: Bundle?
    ): View? {
        // Inflate the layout for this fragment
        binding = FragmentRegisterBinding.inflate(inflater, container, false)
        return binding.root
    }

    override fun onViewCreated(view: View, savedInstanceState: Bundle?) {
        super.onViewCreated(view, savedInstanceState)
        val navBar = requireActivity().findViewById<BottomNavigationView>(R.id.bottom_nav)
        navBar.visibility = View.GONE

        // for dropdown menu gender
        val items = listOf("Male", "Female")
        val adapter = ArrayAdapter(requireContext(), R.layout.list_item, items)
        (binding.inputGender.editText as? AutoCompleteTextView)?.setAdapter(adapter)

        binding.inputDate.setOnClickListener {
            openDatePicker()
        }

        binding.btnRegister.setOnClickListener {

            Navigation.findNavController(view).navigate(R.id.action_registerFragment_to_loginFragment)
        }

    }

    private fun openDatePicker() {
        val c = Calendar.getInstance()
        val year = c.get(Calendar.YEAR)
        val month = c.get(Calendar.MONTH)
        val day = c.get(Calendar.DAY_OF_MONTH)

        val dpd = DatePickerDialog(requireActivity(), DatePickerDialog.OnDateSetListener { view, year, monthOfYear, dayOfMonth ->

            // Display Selected date in textbox
            val sdf = SimpleDateFormat("MMM")
            val monthName = sdf.format(c.time)
            binding.inputDate.editText?.setText("" + monthName + " " + dayOfMonth + ", " + year)

        }, year, month, day)
        dpd.show()
    }

}