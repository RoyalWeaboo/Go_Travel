package com.binar.c5team.gotravel.view

import android.app.DatePickerDialog
import android.content.Context
import android.content.Intent
import android.content.SharedPreferences
import android.net.Uri
import android.os.Bundle
import android.util.Log
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import android.widget.AdapterView
import android.widget.ArrayAdapter
import androidx.fragment.app.Fragment
import androidx.lifecycle.ViewModelProvider
import androidx.navigation.Navigation
import com.binar.c5team.gotravel.R
import com.binar.c5team.gotravel.databinding.FragmentProfileBinding
import com.binar.c5team.gotravel.viewmodel.UserViewModel
import java.text.SimpleDateFormat
import java.util.*


class ProfileFragment : Fragment() {

    private lateinit var binding: FragmentProfileBinding
    lateinit var sharedPref : SharedPreferences
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
        sharedPref = requireActivity().getSharedPreferences("data", Context.MODE_PRIVATE)
        profileData()
        binding.btnCustomerService.setOnClickListener {
            startSupportChat()
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

        binding.chooseGender.adapter = ArrayAdapter<String>(this.requireActivity(),android.R.layout.simple_list_item_1,gender)
        binding.chooseGender.onItemSelectedListener = object : AdapterView.OnItemSelectedListener {
            override fun onItemSelected(p0: AdapterView<*>?, p1: View?, position: Int, p3: Long) {
                binding.tvGender.text = gender[position]
            }

            override fun onNothingSelected(p0: AdapterView<*>?) {
                val viewModel = ViewModelProvider(requireActivity())[UserViewModel::class.java]
                val token = sharedPref.getString("token","").toString()
                viewModel.callProfileApi(token)
                viewModel.getProfileData().observe(viewLifecycleOwner) {
                    if (it != null) {
                        Log.d("Profile Response :", it.toString())
                        binding.tvGender.text = it.sex
                    } else {
                        Log.d("Profile Response :", it.toString())
                    }
                }
            }

        }

        binding.btnLogout.setOnClickListener {
            val saveData = sharedPref.edit()
            saveData.clear()
            saveData.apply()
            Navigation.findNavController(view).navigate(R.id.action_profileFragment_to_loginFragment)
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

    private fun profileData() {
        val viewModel = ViewModelProvider(requireActivity())[UserViewModel::class.java]
        val token = sharedPref.getString("token","").toString()
        viewModel.callProfileApi(token)
        viewModel.getProfileData().observe(this.requireActivity()) {
            if (it != null) {
                Log.d("Profile Response :", it.toString())
                binding.tvUsername.setText(it.username)
                binding.tvDate.setText(it.dateOfBirth)
                binding.tvEmail.setText(it.email)
            } else {
                Log.d("Profile Response :", it.toString())
            }
        }

    }

    private fun startSupportChat() {
        try {
            val headerReceiver = "Halo Saya Butuh Bantuan" // Replace with your message.
            val bodyMessageFormal = " Mengenai Aplikasi Go Travel" // Replace with your message.
            val whatsappContain = headerReceiver + bodyMessageFormal
            val trimToNumner = "+6281280524466" //10 digit number
            val intent = Intent(Intent.ACTION_VIEW)
            intent.data = Uri.parse("https://wa.me/$trimToNumner/?text=$whatsappContain")
            startActivity(intent)
        } catch (e: Exception) {
            e.printStackTrace()
        }
    }
}