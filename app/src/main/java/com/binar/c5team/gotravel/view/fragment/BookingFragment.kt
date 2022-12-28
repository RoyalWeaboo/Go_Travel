package com.binar.c5team.gotravel.view.fragment

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
import androidx.navigation.fragment.findNavController
import com.binar.c5team.gotravel.R
import com.binar.c5team.gotravel.databinding.FragmentBookingBinding
import com.binar.c5team.gotravel.viewmodel.FlightViewModel
import com.google.android.material.bottomnavigation.BottomNavigationView
import java.text.SimpleDateFormat
import java.util.*


class BookingFragment : Fragment() {
    private lateinit var binding: FragmentBookingBinding

    //Shared Preferences
    private lateinit var sharedPref: SharedPreferences
    private lateinit var sharedPrefFlight: SharedPreferences
    private lateinit var sharedPrefBooking: SharedPreferences

    private var token: String = ""
    private var userId: Int = 0
    private var flightMode: String = ""
    private var seatCount: Int = 0

    //departure data
    private var flightId: Int = 0
    private var flightPrice: Int = 0

    private var departDate: String = ""

    //progressbar
    var progressView: ViewGroup? = null
    private var isProgressShowing = false

    //saving booking id
    private var bookingIds = ArrayList<Int>()

    //viewmodel
    private lateinit var viewModel: FlightViewModel


    override fun onCreateView(
        inflater: LayoutInflater, container: ViewGroup?,
        savedInstanceState: Bundle?
    ): View {
        // Inflate the layout for this fragment
        binding = FragmentBookingBinding.inflate(inflater, container, false)
        return binding.root
    }

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

        //getting user data
        token = sharedPref.getString("token", "").toString()
        userId = sharedPref.getInt("userId", 0)

        viewModel = ViewModelProvider(this)[FlightViewModel::class.java]

        val navBar = requireActivity().findViewById<BottomNavigationView>(R.id.bottom_nav)
        navBar.visibility = View.GONE
        val guestNavBar =
            requireActivity().findViewById<BottomNavigationView>(R.id.guest_bottom_nav)
        guestNavBar.visibility = View.GONE

        //getting date
        departDate = sharedPrefBooking.getString("unparsedDepartDate", "null")!!

        //getting trip mode
        flightMode = sharedPrefFlight.getString("flightMode", "").toString()

        getSetData()

        binding.btnBack.setOnClickListener {
            if (flightMode == "oneWay") {
                Navigation.findNavController(view).navigate(R.id.action_bookingFragment_to_flightListFragment)
            } else {
                val builder = AlertDialog.Builder(context)
                builder.setTitle("Cancel Round Trip Booking ?")
                builder.setMessage("You need to rebook from start again")

                builder.setPositiveButton("Confirm") { _, _ ->
                    Navigation.findNavController(view).navigate(R.id.action_bookingFragment_to_homeFragment)
                    Toast.makeText(context, "Booking Canceled", Toast.LENGTH_SHORT).show()
                }
                builder.setNegativeButton("Back") { _, _ ->
                    //do nothing
                }
                builder.show()
            }
        }

        if (flightMode == "oneWay") {
            var tempCount = 0
            if (seatCount > 1) {
                binding.passengerNumber.visibility = View.VISIBLE

                binding.btnToPayment.setOnClickListener {
                    showProgressingView()
                    tempCount++

                    binding.passengerNumber.text = "Passenger - " + tempCount.toString()

                    bookNewTicket()
                    clearInput()
                    hideProgressingView()

                    if (tempCount == seatCount) {
                        viewModel.getPostBookingLD().observe(viewLifecycleOwner) {
                            if (it != null) {
                                bookingIds.add(bookingIds.size, it.data.id)
                                hideProgressingView()

                                val bundle = Bundle()
                                bundle.putIntegerArrayList("bookingIds", bookingIds)
                                Navigation.findNavController(view).navigate(
                                    R.id.action_bookingFragment_to_paymentFragment,
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

                    viewModel.getPostBookingLD().observe(viewLifecycleOwner) {
                        if (it != null) {
                            bookingIds.add(bookingIds.size, it.data.id)
                            hideProgressingView()

                            val bundle = Bundle()
                            bundle.putIntegerArrayList("bookingIds", bookingIds)

                            Navigation.findNavController(view).navigate(
                                R.id.action_bookingFragment_to_paymentFragment,
                                bundle
                            )
                        }
                    }
                }
            }
        } else if (flightMode == "roundTrip") {
            var tempCount = 0
            if (seatCount > 1) {
                binding.passengerNumber.visibility = View.VISIBLE

                binding.btnToPayment.setOnClickListener {
                    showProgressingView()
                    tempCount++

                    binding.passengerNumber.text = "Passenger - " + tempCount.toString()

                    bookNewTicket()
                    clearInput()
                    hideProgressingView()

                    if (tempCount == seatCount) {
                        viewModel.getPostBookingLD().observe(viewLifecycleOwner) {
                            if (it != null) {
                                bookingIds.add(bookingIds.size, it.data.id)
                                hideProgressingView()

                                val bundle = Bundle()
                                bundle.putIntegerArrayList("bookingIds", bookingIds)
                                Navigation.findNavController(view).navigate(
                                    R.id.action_bookingFragment_to_paymentFragment,
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

                    viewModel.getPostBookingLD().observe(viewLifecycleOwner) {
                        if (it != null) {
                            bookingIds.add(bookingIds.size, it.data.id)
                            hideProgressingView()

                            val bundle = Bundle()
                            bundle.putIntegerArrayList("bookingIds", bookingIds)

                            Navigation.findNavController(view).navigate(
                                R.id.action_bookingFragment_to_roundBookingFragment,
                                bundle
                            )
                        }
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

        bookTicket(
            token,
            flightId,
            userId,
            baggage,
            food,
            name,
            homePhone,
            mobilePhone,
            flightPrice,
            departDate
        )
    }

    private fun clearInput() {
        binding.inputFullname.editText?.text?.clear()
        binding.inputBaggage.editText?.text?.clear()
        binding.inputFood.editText?.text?.clear()
        binding.inputEmail.editText?.text?.clear()
        binding.inputMobileNumber.editText?.text?.clear()
    }

    private fun getSetData() {
        //getting flight data
        seatCount = sharedPrefBooking.getInt("totalSeat", 0)
        flightId = sharedPrefBooking.getInt("flightId", 0)
        flightPrice = sharedPrefBooking.getInt("flightPrice", 0)
        val planeName = sharedPrefBooking.getString("planeName", "Error")
        val fromAirport = sharedPrefBooking.getString("fromAirport", "Error")
        val toAirport = sharedPrefBooking.getString("toAirport", "Error")
        val departureTime = sharedPrefBooking.getString("departureTime", "00:00")
        val arrivalTime = sharedPrefBooking.getString("arrivalTime", "00:00")
        countTime()

        // for dropdown food option
        val items = listOf("Yes", "No")
        val adapterFood = ArrayAdapter(requireContext(), R.layout.list_item, items)
        (binding.inputFood.editText as? AutoCompleteTextView)?.setAdapter(adapterFood)

        //setting the textview
        binding.tvAircraftName.text = planeName
        binding.tvFromCity.text = fromAirport
        binding.tvArrivalCity.text = toAirport
        binding.tvTimeFrom.text = departureTime
        binding.tvTimeTo.text = "- " + arrivalTime
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
        departDate: String
    ) {
        viewModel.postBookingApi(
            token,
            id_flight,
            id_user,
            baggage,
            food,
            name,
            homephone,
            mobilephone,
            totalprice,
            departDate
        )

    }

    private fun countTime() {
        //getting booking date and time
        val flightTime = sharedPrefBooking.getString("departureTime", "00:00")
        val flightArrivalTime = sharedPrefBooking.getString("arrivalTime", "00:00")

        val timeFormat = SimpleDateFormat("HH:mm", Locale.getDefault())

        val timeDepart = flightTime?.let { timeFormat.parse(it) }
        val timeArrival = flightArrivalTime?.let { timeFormat.parse(it) }

        val diff = timeDepart!!.time - timeArrival!!.time
        val timeCount =
            "( ${(diff / (1000 * 60 * 60) * -1)} Hours ${(diff % (1000 * 60 * 60) * -1)} Minutes )"

        binding.tvTotalTime.text = timeCount

    }


    fun showProgressingView() {
        if (!isProgressShowing) {
            isProgressShowing = true
            progressView = layoutInflater.inflate(R.layout.progress_bar, null) as ViewGroup
            val v: View = requireView().rootView
            val viewGroup = v as ViewGroup
            viewGroup.addView(progressView)
        }
    }

    fun hideProgressingView() {
        val v: View = requireView().rootView
        val viewGroup = v as ViewGroup
        viewGroup.removeView(progressView)
        isProgressShowing = false
    }

}
