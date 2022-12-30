package com.binar.c5team.gotravel.view.guestfragment

import android.annotation.SuppressLint
import android.app.DatePickerDialog
import android.content.Context
import android.content.SharedPreferences
import android.graphics.Color
import android.os.Bundle
import android.transition.Slide
import android.transition.TransitionManager
import android.view.Gravity
import androidx.fragment.app.Fragment
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import android.widget.ArrayAdapter
import android.widget.Toast
import androidx.lifecycle.ViewModelProvider
import androidx.navigation.Navigation
import androidx.navigation.fragment.findNavController
import com.binar.c5team.gotravel.R
import com.binar.c5team.gotravel.databinding.FragmentGuestHomeBinding
import com.binar.c5team.gotravel.view.MainActivity
import com.binar.c5team.gotravel.viewmodel.FlightViewModel
import dagger.hilt.android.AndroidEntryPoint
import java.text.SimpleDateFormat
import java.util.*

class GuestHomeFragment : Fragment() {
    private lateinit var binding : FragmentGuestHomeBinding

    //Shared Preferences
    private lateinit var sharedPref: SharedPreferences
    private lateinit var sharedPrefFlight: SharedPreferences
    private lateinit var sharedPrefBooking: SharedPreferences

    private val listSpinner: MutableList<String> = ArrayList()
    private val listCity: MutableList<String> = ArrayList()

    private var defaultDepartDate : String = ""
    private var defaultReturnDate : String = ""

    private lateinit var viewModel: FlightViewModel

    override fun onCreateView(
        inflater: LayoutInflater, container: ViewGroup?,
        savedInstanceState: Bundle?
    ): View {
        binding = FragmentGuestHomeBinding.inflate(inflater, container, false)
        //set bottom nav
        (activity as MainActivity?)?.setUpGuestNavigation()
        viewModel = ViewModelProvider(requireActivity())[FlightViewModel::class.java]
        return binding.root
    }

    override fun onViewCreated(view: View, savedInstanceState: Bundle?) {
        super.onViewCreated(view, savedInstanceState)

        //SharedPref for user data
        sharedPref = requireActivity().getSharedPreferences("data", Context.MODE_PRIVATE)

        //SharedPref for user data
        sharedPref = requireActivity().getSharedPreferences("data", Context.MODE_PRIVATE)
        //SharedPref for FlightData
        sharedPrefFlight =
            requireActivity().getSharedPreferences("flightInfo", Context.MODE_PRIVATE)
        //SharedPref for booking data
        sharedPrefBooking =
            requireActivity().getSharedPreferences("bookingInfo", Context.MODE_PRIVATE)

        binding.tvUsername.text = sharedPref.getString("username", "User")

        disableReturnCard()
        getDate()
        callAirportList()

        binding.guestLogin.setOnClickListener {
            Navigation.findNavController(view).navigate(R.id.action_guestHomeFragment_to_loginFragment)
        }

        binding.guestSignUp.setOnClickListener {
            Navigation.findNavController(view).navigate(R.id.action_guestHomeFragment_to_registerFragment)
        }

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
            if (adultCountTotal < 99) {
                adultCountTotal++
                binding.adultCount.text = adultCountTotal.toString()
            }
        }

        binding.minAdult.setOnClickListener {
            var adultCountTotal = binding.adultCount.text.toString().toInt()
            if (adultCountTotal > 1) {
                adultCountTotal--
                binding.adultCount.text = adultCountTotal.toString()
            }
        }

        binding.plusChildren.setOnClickListener {
            var childCountTotal = binding.childrenCount.text.toString().toInt()
            if (childCountTotal < 99) {
                childCountTotal++
                binding.childrenCount.text = childCountTotal.toString()
            }
        }

        binding.minChildren.setOnClickListener {
            var childCountTotal = binding.childrenCount.text.toString().toInt()
            if (childCountTotal > 0) {
                childCountTotal--
                binding.childrenCount.text = childCountTotal.toString()
            }
        }

        binding.btnSearchFlight.setOnClickListener {
            val from = binding.spinnerFrom.selectedItemId
            val to = binding.spinnerTo.selectedItemId
            val fromCity = listCity[from.toInt()]
            val toCity = listCity[to.toInt()]
            val fromCityId = from.toInt()+1
            val toCityId = to.toInt()+1
            val depDate = binding.departDateText.text.toString()
            val retDate = binding.returnDateText.text.toString()

            //setting date to default
            val departDate = binding.departDateText.text.toString()
            val returnDate = binding.returnDateText.text.toString()

            val parseToDate = SimpleDateFormat("MMM dd, yyyy", Locale.getDefault())
            val pickedDepartDate = parseToDate.parse(departDate)
            val pickedReturnDate = parseToDate.parse(returnDate)

            val sdfDefault = SimpleDateFormat("yyyy-MM-dd", Locale.getDefault())
            defaultDepartDate = pickedDepartDate?.let { it1 -> sdfDefault.format(it1) }!!
            defaultReturnDate = pickedReturnDate?.let { it1 -> sdfDefault.format(it1) }!!

            val flightMode: String = if (binding.lineOptionOneWay.visibility == View.VISIBLE){
                "oneWay"
            }else{
                "roundTrip"
            }
            val adultCountTotal = binding.adultCount.text.toString()
            val childCountTotal = binding.childrenCount.text.toString()

            //save flight info to shared preferences
            val saveFlightInfo = sharedPrefFlight.edit()
            saveFlightInfo.putInt("fromId", fromCityId)
            saveFlightInfo.putInt("toId", toCityId)
            saveFlightInfo.putString("fromAirport", fromCity)
            saveFlightInfo.putString("toAirport", toCity)
            saveFlightInfo.putString("adultCount", adultCountTotal)
            saveFlightInfo.putString("childCount", childCountTotal)
            saveFlightInfo.putString("departDate", depDate)
            saveFlightInfo.putString("returnDate", retDate)
            saveFlightInfo.putString("flightMode", flightMode)
            saveFlightInfo.apply()

            //save default date to sharedpref booking
            val saveBookingInfo = sharedPrefBooking.edit()
            saveBookingInfo.putString("unparsedDepartDate", defaultDepartDate)
            saveBookingInfo.putString("unparsedReturnDate", defaultDepartDate)
            saveBookingInfo.apply()

            findNavController().navigate(R.id.action_guestHomeFragment_to_flightListFragment)
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

    @SuppressLint("SimpleDateFormat", "SetTextI18n")
    private fun openDatePickerDepart() {
        val c = Calendar.getInstance()
        val year = c.get(Calendar.YEAR)
        val month = c.get(Calendar.MONTH)
        val day = c.get(Calendar.DAY_OF_MONTH)

        val dpd = DatePickerDialog(
            requireActivity(),
            { _, y, _, dayOfMonth ->

                // Display Selected date in textbox
                val sdf = SimpleDateFormat("MMM")
                val monthName = sdf.format(c.time)
                binding.departDateText.text = "$monthName $dayOfMonth, $y"
            },
            year,
            month,
            day
        )
        dpd.show()
    }

    @SuppressLint("SimpleDateFormat", "SetTextI18n")
    private fun openDatePickerReturn() {
        val c = Calendar.getInstance()
        val year = c.get(Calendar.YEAR)
        val month = c.get(Calendar.MONTH)
        val day = c.get(Calendar.DAY_OF_MONTH)

        val dpd = DatePickerDialog(
            requireActivity(),
            { _, y, _, dayOfMonth ->

                // Display Selected date in textbox
                val sdf = SimpleDateFormat("MMM")
                val monthName = sdf.format(c.time)
                binding.returnDateText.text = "$monthName $dayOfMonth, $y"
            },
            year,
            month,
            day
        )
        dpd.show()
    }

    private fun callAirportList() {
        viewModel.getAirportListData().observe(viewLifecycleOwner) {
            if (it != null) {
//                Log.d("Airport Data", it.toString())

                //set json to arraylist
                if(listSpinner.isEmpty()){
                    for (element in it.data.airports) {
                        listSpinner.add(element.name)
                        listCity.add(element.city)
                    }
                    // Set result to spinner
                    val adapter = context?.let { it1 ->
                        ArrayAdapter(
                            it1,
                            R.layout.custom_airport_spinner_item, listSpinner
                        )
                    }
                    adapter?.setDropDownViewResource(R.layout.simple_spinner_item)
                    binding.spinnerFrom.adapter = adapter
                    binding.spinnerTo.adapter = adapter
                }

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