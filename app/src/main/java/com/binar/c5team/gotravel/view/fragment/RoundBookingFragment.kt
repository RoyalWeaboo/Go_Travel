package com.binar.c5team.gotravel.view.fragment

import android.annotation.SuppressLint
import android.app.AlertDialog
import android.content.Context
import android.content.SharedPreferences
import android.os.Bundle
import android.util.Log
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
import com.binar.c5team.gotravel.databinding.FragmentBookingBinding
import com.binar.c5team.gotravel.viewmodel.FlightViewModel
import com.google.android.material.bottomnavigation.BottomNavigationView
import com.google.android.material.transition.MaterialSharedAxis
import java.text.SimpleDateFormat
import java.util.*

class RoundBookingFragment : Fragment() {
    private lateinit var binding: FragmentBookingBinding

    //Shared Preferences
    private lateinit var sharedPref: SharedPreferences
    private lateinit var sharedPrefFlight: SharedPreferences
    private lateinit var sharedPrefBooking: SharedPreferences

    private var token: String = ""
    private var userId: Int = 0
    private var flightMode: String = ""
    private var seatCount: Int = 0

    //return data
    private var roundFlightId: Int = 0
    private var roundFlightPrice: Int = 0

    private var returnDate: String = ""

    //progressbar
    var progressView: ViewGroup? = null
    private var isProgressShowing = false

    var bookingIds = ArrayList<Int>()

    //viewmodel
    private lateinit var viewModel: FlightViewModel

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
        binding = FragmentBookingBinding.inflate(inflater, container, false)
        return binding.root
    }

    @SuppressLint("SetTextI18n")
    override fun onViewCreated(view: View, savedInstanceState: Bundle?) {
        super.onViewCreated(view, savedInstanceState)

        val callback: OnBackPressedCallback =
            object : OnBackPressedCallback(true) {
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

        binding.flightModeStatus.text = "Return :"

        //getting user data
        token = sharedPref.getString("token", "").toString()
        userId = sharedPref.getInt("userId", 0)

        val navBar = requireActivity().findViewById<BottomNavigationView>(R.id.bottom_nav)
        navBar.visibility = View.GONE
        val guestNavBar =
            requireActivity().findViewById<BottomNavigationView>(R.id.guest_bottom_nav)
        guestNavBar.visibility = View.GONE

        //getting date
        returnDate = sharedPrefBooking.getString("unparsedReturnDate", "null")!!

        //getting trip mode
        flightMode = sharedPrefFlight.getString("flightMode", "").toString()

        bookingIds = arguments?.getIntegerArrayList("bookingIds") as ArrayList<Int>
        Log.d("Booking ids from args ", bookingIds.toString())

        getSetData()

        binding.btnBack.setOnClickListener {
            val builder = AlertDialog.Builder(context)
            builder.setTitle("Cancel Round Trip Booking ?")
            builder.setMessage("You need to rebook from start again")

            builder.setPositiveButton("Confirm") { _, _ ->
                Navigation.findNavController(view)
                    .navigate(R.id.action_roundBookingFragment_to_homeFragment)
                Toast.makeText(context, "Booking Canceled", Toast.LENGTH_SHORT).show()
            }
            builder.setNegativeButton("Back") { _, _ ->
                //do nothing
            }
            builder.show()
        }

        var count = 1
        var tempCount = 0
        if (seatCount > 1) {
            binding.passengerNumber.visibility = View.VISIBLE
            binding.passengerNumber.text = "Passenger - $count"

            binding.btnToPayment.setOnClickListener {
                showProgressingView()
                binding.passengerNumber.text = "Passenger - $count"

                tempCount++
                count++

                bookNewTicket()
                viewModel.loading.observe(viewLifecycleOwner) {
                    if (!it) {
                        clearInput()
                        hideProgressingView()
                    }
                }

                if (tempCount == seatCount) {
                    showProgressingView()
                    viewModel.loading.observe(viewLifecycleOwner) {
                        if (!it) {
                            val bundle = Bundle()
                            bundle.putIntegerArrayList("bookingIds", bookingIds)
                            hideProgressingView()
                            Log.d("Current booking id array is", bookingIds.toString())
                            Navigation.findNavController(view).navigate(
                                R.id.action_roundBookingFragment_to_paymentFragment,
                                bundle
                            )
                        }
                    }
                }
            }
        } else {
            binding.btnToPayment.setOnClickListener {
                showProgressingView()
                bookNewTicket()
                viewModel.loading.observe(viewLifecycleOwner) {
                    if (!it) {
                        Log.d("and the current booking id array is", bookingIds.toString())

                        val bundle = Bundle()
                        bundle.putIntegerArrayList("bookingIds", bookingIds)
                        hideProgressingView()

                        Navigation.findNavController(view).navigate(
                            R.id.action_roundBookingFragment_to_paymentFragment,
                            bundle
                        )
                    }
                }
            }
        }
    }


    private fun bookNewTicket() {
        val name = binding.inputFullname.editText?.text.toString()
        val baggage = binding.inputBaggage.editText?.text.toString().toInt()
        val food: Boolean
        val foodOpt = binding.inputFood.editText?.text.toString()
        food = foodOpt == "Yes"
        val homePhone = binding.inputEmail.editText?.text.toString()
        val mobilePhone = binding.inputMobileNumber.editText?.text.toString()
        var tripType = ""
        if (flightMode == "oneWay") {
            tripType = "One Way"
        } else if (flightMode == "roundTrip") {
            tripType = "Round Trip"
        }

        bookTicket(
            token,
            roundFlightId,
            userId,
            baggage,
            food,
            name,
            homePhone,
            mobilePhone,
            roundFlightPrice,
            returnDate,
            tripType
        )
    }

    @SuppressLint("SetTextI18n")
    private fun getSetData() {
        //getting flight data
        seatCount = sharedPrefBooking.getInt("totalSeat", 0)
        roundFlightId = sharedPrefBooking.getInt("roundFlightId", 0)
        roundFlightPrice = sharedPrefBooking.getInt("roundFlightPrice", 0)
        val planeName = sharedPrefBooking.getString("roundPlaneName", "Error")
        val fromAirport = sharedPrefBooking.getString("roundFromAirport", "Error")
        val toAirport = sharedPrefBooking.getString("roundToAirport", "Error")
        val departureTime = sharedPrefBooking.getString("roundDepartureTime", "00:00")
        val arrivalTime = sharedPrefBooking.getString("roundArrivalTime", "00:00")
        countTime()

        // for dropdown food option
        val items = listOf("Yes", "No")
        val adapterFood = ArrayAdapter(requireContext(), R.layout.list_item, items)
        (binding.inputFood.editText as? AutoCompleteTextView)?.setAdapter(adapterFood)

        binding.tvAircraftName.text = planeName
        binding.tvFromCity.text = fromAirport
        binding.tvArrivalCity.text = "- $toAirport"
        binding.tvTimeFrom.text = departureTime
        binding.tvTimeTo.text = "- $arrivalTime"
        binding.tvSeatTotal.text = seatCount.toString()
    }


    private fun bookTicket(
        token: String,
        id_flight: Int,
        id_user: Int,
        baggage: Int,
        food: Boolean,
        name: String,
        homephone: String,
        mobilephone: String,
        totalprice: Int,
        departDate: String,
        tripType: String
    ) {
        viewModel.postRoundBookingApi(
            this,
            token,
            id_flight,
            id_user,
            baggage,
            food,
            name,
            homephone,
            mobilephone,
            totalprice,
            departDate,
            tripType
        )
    }


    private fun countTime() {
        //getting booking date and time
        val flightTime = sharedPrefBooking.getString("roundDepartureTime", "00:00")
        val flightArrivalTime = sharedPrefBooking.getString("roundArrivalTime", "00:00")

        val timeFormat = SimpleDateFormat("HH:mm", Locale.getDefault())

        val timeDepart = flightTime?.let { timeFormat.parse(it) }
        val timeArrival = flightArrivalTime?.let { timeFormat.parse(it) }

        val diff = timeDepart!!.time - timeArrival!!.time
        val timeCount =
            "( ${(diff / (1000 * 60 * 60) * -1)} Hours ${(diff % (1000 * 60 * 60) * -1)} Minutes )"

        binding.tvTotalTime.text = timeCount

    }

    private fun clearInput() {
        binding.inputFullname.editText?.text?.clear()
        binding.inputBaggage.editText?.text?.clear()
        binding.inputFood.editText?.text?.clear()
        binding.inputEmail.editText?.text?.clear()
        binding.inputMobileNumber.editText?.text?.clear()
    }


    @SuppressLint("InflateParams")
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

