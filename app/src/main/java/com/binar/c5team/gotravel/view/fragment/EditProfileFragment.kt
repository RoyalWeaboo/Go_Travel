package com.binar.c5team.gotravel.view.fragment

import android.app.DatePickerDialog
import android.content.Context
import android.content.SharedPreferences
import android.os.Bundle
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import android.widget.ArrayAdapter
import android.widget.AutoCompleteTextView
import android.widget.Toast
import androidx.activity.OnBackPressedCallback
import androidx.fragment.app.Fragment
import androidx.lifecycle.ViewModelProvider
import androidx.navigation.Navigation
import com.binar.c5team.gotravel.R
import com.binar.c5team.gotravel.databinding.FragmentEditProfileBinding
import com.binar.c5team.gotravel.viewmodel.FlightViewModel
import com.google.android.material.bottomnavigation.BottomNavigationView
import dagger.hilt.android.AndroidEntryPoint
import java.text.SimpleDateFormat
import java.util.*

class EditProfileFragment : Fragment() {
    lateinit var binding : FragmentEditProfileBinding

    private var birthDate = ""
    private var token = ""

    //Shared Preferences
    private lateinit var sharedPref: SharedPreferences

    var progressView: ViewGroup? = null
    private var isProgressShowing = false

    override fun onCreateView(
        inflater: LayoutInflater, container: ViewGroup?,
        savedInstanceState: Bundle?
    ): View {
        binding = FragmentEditProfileBinding.inflate(inflater, container, false)
        return binding.root
    }

    override fun onViewCreated(view: View, savedInstanceState: Bundle?) {
        super.onViewCreated(view, savedInstanceState)

        val callback: OnBackPressedCallback =
            object : OnBackPressedCallback(true ) {
                override fun handleOnBackPressed() {
                    Navigation.findNavController(view).navigate(R.id.action_editProfileFragment_to_profileFragment)
                }
            }
        requireActivity().onBackPressedDispatcher.addCallback(requireActivity(), callback)

        sharedPref = requireActivity().getSharedPreferences("data", Context.MODE_PRIVATE)
        //getting token
        token = sharedPref.getString("token", "").toString()

        val navBar = requireActivity().findViewById<BottomNavigationView>(R.id.bottom_nav)
        navBar.visibility = View.GONE
        val guestNavBar = requireActivity().findViewById<BottomNavigationView>(R.id.guest_bottom_nav)
        guestNavBar.visibility = View.GONE

        val viewModel = ViewModelProvider(this)[FlightViewModel::class.java]
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

        binding.tvUsername.text = arguments?.getString("username")
        binding.inputFullname.editText?.setText(arguments?.getString("fullName"))
        binding.inputEmail.editText?.setText(arguments?.getString("email"))
        binding.inputNoKtp.editText?.setText(arguments?.getString("noKtp"))
        binding.inputAddress.editText?.setText(arguments?.getString("address"))
        binding.inputDateofBirth.editText?.setText(arguments?.getString("dateOfBirth"))

        val gend = arguments?.getString("gender")
        if (gend == "Male"){
            (binding.inputGender.editText as? AutoCompleteTextView)?.setText("Male")
        }else{
            (binding.inputGender.editText as? AutoCompleteTextView)?.setText("Female")
        }

        binding.arrowBackEditProfile.setOnClickListener {
            Navigation.findNavController(view).navigate(R.id.action_editProfileFragment_to_profileFragment)
        }

        binding.pickDate.setOnClickListener {
            openDatePicker()
        }

        binding.btnSave.setOnClickListener {
            val noKtp = binding.inputNoKtp.editText?.text.toString()
            val gender = binding.inputGender.editText?.text.toString()
            val dateofBirth = binding.inputDateofBirth.editText?.text.toString()
            val address = binding.inputAddress.editText?.text.toString()
            val email = binding.inputEmail.editText?.text.toString()
            val name = binding.inputFullname.editText?.text.toString()

            var formatGender = ""
            formatGender = if (gender == "Male"){
                "L"
            }else{
                "P"
            }

            putProfileData(view, token, noKtp, formatGender, dateofBirth, address, email, name)
        }

    }

    private fun putProfileData(view : View, token: String, no_ktp : String, gender : String, date_of_birth : String, address : String, email : String, name : String) {
        val viewModel = ViewModelProvider(this)[FlightViewModel::class.java]
        viewModel.putProfileData().observe(viewLifecycleOwner) {
            if (it != null) {
                Toast.makeText(context, "Profile updated !", Toast.LENGTH_SHORT).show()
                Navigation.findNavController(view).navigate(R.id.action_editProfileFragment_to_profileFragment)
            } else {
                Toast.makeText(context, "Failed to read profile data", Toast.LENGTH_SHORT).show()
            }
        }
        viewModel.putProfileData(token, no_ktp, gender, date_of_birth, address, email, name)
    }

    private fun openDatePicker() {
        val c = Calendar.getInstance()
        val year = c.get(Calendar.YEAR)
        val month = c.get(Calendar.MONTH)
        val day = c.get(Calendar.DAY_OF_MONTH)

        val dpd = DatePickerDialog(
            requireActivity(),
            DatePickerDialog.OnDateSetListener { view, year, monthOfYear, dayOfMonth ->

                // Display Selected date in textbox
                val sdf = SimpleDateFormat("MMM", Locale.getDefault())
                val monthName = sdf.format(c.time)
                binding.inputDateofBirth.editText?.setText("" + monthName + " " + dayOfMonth + ", " + year)

                val sdf2 = SimpleDateFormat("yyyy-MM-dd", Locale.getDefault())
                birthDate = sdf2.format(c.time)
            },
            year,
            month,
            day
        )
        dpd.show()
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


}