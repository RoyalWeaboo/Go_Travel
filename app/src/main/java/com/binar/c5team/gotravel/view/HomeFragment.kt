package com.binar.c5team.gotravel.view

import android.R
import android.app.DatePickerDialog
import android.content.Context
import android.content.SharedPreferences
import android.graphics.Color
import android.os.Bundle
import android.transition.Slide
import android.transition.TransitionManager
import android.view.Gravity
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import android.widget.ArrayAdapter
import android.widget.Toast
import androidx.fragment.app.Fragment
import androidx.lifecycle.ViewModelProvider
import androidx.navigation.fragment.findNavController
import androidx.navigation.ui.NavigationUI
import com.binar.c5team.gotravel.databinding.FragmentHomeBinding
import com.binar.c5team.gotravel.viewmodel.AirportViewModel
import java.text.SimpleDateFormat
import java.util.*


class HomeFragment : Fragment() {
    private lateinit var binding: FragmentHomeBinding
    lateinit var sharedPref : SharedPreferences
    override fun onCreateView(
        inflater: LayoutInflater, container: ViewGroup?,
        savedInstanceState: Bundle?
    ): View? {
        binding = FragmentHomeBinding.inflate(inflater, container, false)
        return binding.root
    }

    override fun onViewCreated(view: View, savedInstanceState: Bundle?) {
        super.onViewCreated(view, savedInstanceState)
        sharedPref = requireActivity().getSharedPreferences("data", Context.MODE_PRIVATE)
        binding.tvUsername.text = sharedPref.getString("username", "User")


        disableReturnCard()
        getDate()
        callAirportList()

        binding.menuOneWay.setOnClickListener {
            val mSlideRight = Slide()
            mSlideRight.slideEdge = Gravity.END
            TransitionManager.beginDelayedTransition(binding.linearOption, mSlideRight)
            binding.lineOptionOneWay.visibility = View.VISIBLE
            binding.lineOptionRound.visibility = View.GONE
            disableReturnCard()
        }

        binding.menuRoundTrip.setOnClickListener {
            binding.returnDateText.setTextColor(Color.parseColor("#000000"))
            val mSlideLeft = Slide()
            mSlideLeft.slideEdge = Gravity.START
            TransitionManager.beginDelayedTransition(binding.linearOption, mSlideLeft)
            binding.lineOptionRound.visibility = View.VISIBLE
            binding.lineOptionOneWay.visibility = View.GONE
            binding.cardDateReturn.isEnabled = true
        }

        binding.cardDateDepart.setOnClickListener {
            openDatePickerDepart()
        }

        binding.cardDateReturn.setOnClickListener {
            openDatePickerReturn()
        }

        binding.plusAdult.setOnClickListener {
            var adultCountTotal = binding.adultCount.text.toString().toInt()
            if (adultCountTotal<99) {
                adultCountTotal++
                binding.adultCount.text = adultCountTotal.toString()
            }
        }

        binding.minAdult.setOnClickListener {
            var adultCountTotal = binding.adultCount.text.toString().toInt()
            if (adultCountTotal>1){
                adultCountTotal--
                binding.adultCount.text = adultCountTotal.toString()
            }
        }

        binding.plusChildren.setOnClickListener {
            var childCountTotal = binding.childrenCount.text.toString().toInt()
            if (childCountTotal<99) {
                childCountTotal++
                binding.childrenCount.text = childCountTotal.toString()
            }
        }

        binding.minChildren.setOnClickListener {
            var childCountTotal = binding.childrenCount.text.toString().toInt()
            if (childCountTotal>0) {
                childCountTotal--
                binding.childrenCount.text = childCountTotal.toString()
            }
        }
    }



    private fun disableReturnCard() {
        binding.returnDateText.setTextColor(Color.parseColor("#808080"))
        binding.cardDateReturn.isEnabled = false
    }

    private fun getDate() {
        val currDate = Calendar.getInstance().time

        val df = SimpleDateFormat("MMM dd, yyyy", Locale.getDefault())
        val formattedDate = df.format(currDate)
        binding.returnDateText.text = formattedDate
        binding.departDateText.text = formattedDate

    }

    private fun openDatePickerDepart() {
        val c = Calendar.getInstance()
        val year = c.get(Calendar.YEAR)
        val month = c.get(Calendar.MONTH)
        val day = c.get(Calendar.DAY_OF_MONTH)

        val dpd = DatePickerDialog(requireActivity(), DatePickerDialog.OnDateSetListener { view, year, monthOfYear, dayOfMonth ->

            // Display Selected date in textbox
            val sdf = SimpleDateFormat("MMM")
            val monthName = sdf.format(c.time)
            binding.departDateText.text = "" + monthName + " " + dayOfMonth + ", " + year

        }, year, month, day)
        dpd.show()
    }

    private fun openDatePickerReturn() {
        val c = Calendar.getInstance()
        val year = c.get(Calendar.YEAR)
        val month = c.get(Calendar.MONTH)
        val day = c.get(Calendar.DAY_OF_MONTH)

        val dpd = DatePickerDialog(requireActivity(), DatePickerDialog.OnDateSetListener { view, year, monthOfYear, dayOfMonth ->
            // Display Selected date in textbox
            val sdf = SimpleDateFormat("MMM")
            val monthName = sdf.format(c.time)
            binding.returnDateText.text = "" + monthName + " " + dayOfMonth + ", " + year
        }, year, month, day)
        dpd.show()
    }

    private fun callAirportList() {
        val viewModel = ViewModelProvider(requireActivity())[AirportViewModel::class.java]
        viewModel.getAirportListData().observe(viewLifecycleOwner) {
            if (it != null) {
//                Log.d("Airport Data", it.toString())

                //set json to arraylist
                val listSpinner: MutableList<String> = ArrayList()
                for (element in it) {
                    listSpinner.add(element.iATA + " (" + element.iCAO + ")")
                }
                // Set result to spinner
                val adapter = context?.let { it1 ->
                    ArrayAdapter(
                        it1,
                        com.binar.c5team.gotravel.R.layout.custom_airport_spinner_item, listSpinner
                    )
                }
                adapter?.setDropDownViewResource(com.binar.c5team.gotravel.R.layout.simple_spinner_item)
                binding.spinnerFrom.adapter = adapter
                binding.spinnerTo.adapter = adapter
            } else {
                Toast.makeText(
                    requireActivity(),
                    "No Data",
                    Toast.LENGTH_SHORT
                ).show()
            }
        }
        viewModel.callAirportApi()
    }
}