package com.binar.c5team.gotravel.view.fragment

import android.app.DatePickerDialog
import android.content.Context
import android.content.SharedPreferences
import android.graphics.Color
import android.os.Bundle
import android.transition.Slide
import android.transition.TransitionManager
import android.util.Log
import android.view.Gravity
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import android.widget.ArrayAdapter
import android.widget.Toast
import androidx.activity.OnBackPressedCallback
import androidx.fragment.app.Fragment
import androidx.lifecycle.ViewModelProvider
import androidx.navigation.fragment.findNavController
import com.binar.c5team.gotravel.R
import com.binar.c5team.gotravel.databinding.FragmentHomeBinding
import com.binar.c5team.gotravel.viewmodel.AirportViewModel
import com.binar.c5team.gotravel.viewmodel.FlightViewModel
import com.binar.c5team.gotravel.viewmodel.UserViewModel
import com.bumptech.glide.Glide
import com.google.android.material.bottomnavigation.BottomNavigationView
import java.text.SimpleDateFormat
import java.util.*


class HomeFragment : Fragment() {
    private lateinit var binding: FragmentHomeBinding

    //Shared Preferences
    private lateinit var sharedPref: SharedPreferences
    private lateinit var sharedPrefFlight: SharedPreferences
    private lateinit var sharedPrefBooking: SharedPreferences

    private var token : String = ""

    private val listSpinner: MutableList<String> = ArrayList()
    private val listCity: MutableList<String> = ArrayList()

    private var defaultDepartDate : String = ""
    private var defaultReturnDate : String = ""

    //progressbar
    var progressView: ViewGroup? = null
    private var isProgressShowing = false

    override fun onCreateView(
        inflater: LayoutInflater, container: ViewGroup?,
        savedInstanceState: Bundle?
    ): View {
        binding = FragmentHomeBinding.inflate(inflater, container, false)
        return binding.root
    }

    override fun onViewCreated(view: View, savedInstanceState: Bundle?) {
        super.onViewCreated(view, savedInstanceState)

        val navBar = requireActivity().findViewById<BottomNavigationView>(R.id.bottom_nav)
        navBar.visibility = View.VISIBLE

        val guestNavBar = requireActivity().findViewById<BottomNavigationView>(R.id.guest_bottom_nav)
        guestNavBar.visibility = View.GONE

        val callback: OnBackPressedCallback =
            object : OnBackPressedCallback(true ) {
                override fun handleOnBackPressed() {
                    //do nothing
                }
            }
        requireActivity().onBackPressedDispatcher.addCallback(requireActivity(), callback)

        //SharedPref for user data
        sharedPref = requireActivity().getSharedPreferences("data", Context.MODE_PRIVATE)
        //SharedPref for FlightData
        sharedPrefFlight =
            requireActivity().getSharedPreferences("flightInfo", Context.MODE_PRIVATE)
        //SharedPref for booking data
        sharedPrefBooking =
            requireActivity().getSharedPreferences("bookingInfo", Context.MODE_PRIVATE)

        token = sharedPref.getString("token", "").toString()

        val viewModelUser = ViewModelProvider(requireActivity())[UserViewModel::class.java]
        viewModelUser.loading.observe(viewLifecycleOwner) {
            when (it) {
                true -> showProgressingView()
                false -> hideProgressingView()
            }
        }

        val viewModelAirport = ViewModelProvider(requireActivity())[AirportViewModel::class.java]
        viewModelAirport.loading.observe(viewLifecycleOwner) {
            when (it) {
                true -> showProgressingView()
                false -> hideProgressingView()
            }
        }

        binding.tvUsername.text = sharedPref.getString("username", "User")
        getProfileImage(token)

        disableReturnCard()
        getDate()
        callAirportList()

        binding.wishlist.setOnClickListener {
            findNavController().navigate(R.id.action_homeFragment_to_wishlistFragment)
        }

        binding.userImageProfile.setOnClickListener {
            findNavController().navigate(R.id.action_homeFragment_to_profileFragment)
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

            val sdfDefault = SimpleDateFormat("YYYY-MM-dd", Locale.getDefault())
            defaultDepartDate = sdfDefault.format(pickedDepartDate)
            defaultReturnDate = sdfDefault.format(pickedReturnDate)

            var flightMode = ""
            flightMode = if (binding.lineOptionOneWay.visibility == View.VISIBLE){
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

            findNavController().navigate(R.id.action_homeFragment_to_flightListFragment)
        }

    }

    private fun getProfileImage(token : String){
        val viewModel = ViewModelProvider(requireActivity())[UserViewModel::class.java]
        viewModel.callProfileApi(token)
        viewModel.getProfileData().observe(viewLifecycleOwner) {
            if (it.image != "") {
                Glide
                    .with(requireContext())
                    .load(it.image)
                    .centerCrop()
                    .into(binding.userImageProfile)
            } else {
                Toast.makeText(context, "No Profile Image Found", Toast.LENGTH_SHORT).show()
                Log.d("Profile Image Response :", it.toString())
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

        val dpd = DatePickerDialog(
            requireActivity(),
            DatePickerDialog.OnDateSetListener { view, year, monthOfYear, dayOfMonth ->

                // Display Selected date in textbox
                val sdf = SimpleDateFormat("MMM")
                val monthName = sdf.format(c.time)
                binding.departDateText.text = "" + monthName + " " + dayOfMonth + ", " + year
            },
            year,
            month,
            day
        )
        dpd.show()
    }

    private fun openDatePickerReturn() {
        val c = Calendar.getInstance()
        val year = c.get(Calendar.YEAR)
        val month = c.get(Calendar.MONTH)
        val day = c.get(Calendar.DAY_OF_MONTH)

        val dpd = DatePickerDialog(
            requireActivity(),
            DatePickerDialog.OnDateSetListener { view, year, monthOfYear, dayOfMonth ->
                // Display Selected date in textbox
                val sdf = SimpleDateFormat("MMM")
                val monthName = sdf.format(c.time)
                binding.returnDateText.text = "" + monthName + " " + dayOfMonth + ", " + year
            },
            year,
            month,
            day
        )
        dpd.show()
    }

    private fun callAirportList() {
        val viewModel = ViewModelProvider(requireActivity())[AirportViewModel::class.java]
        viewModel.getAirportListData().observe(viewLifecycleOwner) {
            if (it != null) {

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

                    binding.spinnerFrom.setSelection(0)
                    binding.spinnerTo.setSelection(1)
                }

            } else {
                Toast.makeText(
                    requireActivity(),
                    "No airport data found",
                    Toast.LENGTH_SHORT
                ).show()
            }
        }
        viewModel.callAirportApi()
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