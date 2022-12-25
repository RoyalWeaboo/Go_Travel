package com.binar.c5team.gotravel.view.fragment

import android.app.*
import android.content.Context
import android.content.Intent
import android.content.SharedPreferences
import android.os.Bundle
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import android.widget.ArrayAdapter
import android.widget.AutoCompleteTextView
import android.widget.Toast
import androidx.activity.OnBackPressedCallback
import androidx.core.app.NotificationCompat
import androidx.fragment.app.Fragment
import androidx.lifecycle.ViewModelProvider
import androidx.navigation.fragment.findNavController
import com.binar.c5team.gotravel.R
import com.binar.c5team.gotravel.databinding.FragmentBookingBinding
import com.binar.c5team.gotravel.view.MainActivity
import com.binar.c5team.gotravel.viewmodel.FlightViewModel
import com.binar.c5team.gotravel.viewmodel.UserViewModel
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

    private var departDate : String = ""

    var progressView: ViewGroup? = null
    private var isProgressShowing = false

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

        val viewModel = ViewModelProvider(requireActivity())[FlightViewModel::class.java]
        viewModel.loading.observe(viewLifecycleOwner) {
            when (it) {
                true -> showProgressingView()
                false -> hideProgressingView()
            }
        }

        //getting date
        departDate = sharedPrefBooking.getString("unparsedDepartDate", "null")!!

        //getting trip mode
        flightMode = sharedPrefFlight.getString("flightMode", "").toString()

        getSetData()

        binding.btnBack.setOnClickListener {
            if (flightMode == "oneWay")
            findNavController().navigate(R.id.action_bookingFragment_to_flightListFragment)
            else{
                findNavController().navigate(R.id.action_bookingFragment_to_homeFragment)
                Toast.makeText(context, "Booking Canceled", Toast.LENGTH_SHORT).show()
            }
        }

        if (flightMode == "oneWay") {
            if (seatCount > 1) {
                binding.passengerNumber.visibility = View.VISIBLE
                var oneWaytempDataCount = 1
                binding.btnToPayment.setOnClickListener {
                    oneWaytempDataCount++
                    binding.passengerNumber.text = "Passenger - " + oneWaytempDataCount.toString()
                    bookNewTicket()
                    clearInput()
                    if (oneWaytempDataCount > seatCount) {
                        openPaymentDialog()
                    }
                }
            } else {
                binding.btnToPayment.setOnClickListener {
                    bookNewTicket()
                    openPaymentDialog()
                }
            }
        } else if (flightMode == "roundTrip") {
            if (seatCount > 1) {
                binding.passengerNumber.visibility = View.VISIBLE
                var departuretempDataCount = 1
                binding.btnToPayment.setOnClickListener {
                    departuretempDataCount++
                    binding.passengerNumber.text = "Passenger - " + departuretempDataCount.toString()
                    bookNewTicket()
                    clearInput()
                    if (departuretempDataCount > seatCount) {

                        findNavController().navigate(
                            R.id.action_bookingFragment_to_roundBookingFragment
                        )
                    }
                }
            } else {
                binding.btnToPayment.setOnClickListener {
                    bookNewTicket()
                    findNavController().navigate(
                        R.id.action_bookingFragment_to_roundBookingFragment
                    )
                }
            }
        }
    }

    private fun clearInput() {
        binding.inputFullname.editText?.text?.clear()
        binding.inputBaggage.editText?.text?.clear()
        binding.inputFood.editText?.text?.clear()
        binding.inputEmail.editText?.text?.clear()
        binding.inputMobileNumber.editText?.text?.clear()
    }

    private fun bookNewTicket() {
        val name = binding.inputFullname.editText?.text.toString()
        val baggage = binding.inputBaggage.editText?.text.toString().toInt()
        val food: Boolean
        val foodOpt = binding.inputFood.editText?.text.toString()
        food = foodOpt == "Yes"
        val homePhone = binding.inputEmail.editText?.text.toString()
        val mobilePhone = binding.inputMobileNumber.editText?.text.toString()

//        Log.d("data", flightId.toString() + userId.toString() + baggage.toString() + food + name + homePhone + mobilePhone + flightPrice + departDate)

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

        //total flight price
        val total = seatCount * flightPrice

        //setting the textview
        binding.tvTicketPrice.text = total.toString()
        binding.tvAircraftName.text = planeName
        binding.tvFromCity.text = fromAirport
        binding.tvArrivalCity.text = toAirport
        binding.tvTimeFrom.text = departureTime
        binding.tvTimeTo.text = "- "+arrivalTime
        binding.tvSeatTotal.text = seatCount.toString()
    }

    private fun notification() {
        val mBuilder = NotificationCompat.Builder(requireContext().applicationContext, "1")
        val ii = Intent(requireContext().applicationContext, MainActivity::class.java)
        ii.putExtra("redirect", "historyFragment")
        val pendingIntent =
            PendingIntent.getActivity(requireContext().applicationContext, 0, ii, 0)

        val bigText = NotificationCompat.BigTextStyle()
        bigText.setBigContentTitle("Booking Succesful !")
        bigText.setSummaryText("Successful Booking")

        mBuilder.setContentIntent(pendingIntent)
        mBuilder.setSmallIcon(R.drawable.airplane_icon)
        mBuilder.setContentTitle("Booking Succesful !")
        mBuilder.setContentText("Success booking a new ticket, Open History to check your ticket !")
        mBuilder.setStyle(bigText)
        mBuilder.setDefaults(Notification.DEFAULT_ALL)

        val mNotificationManager: NotificationManager =
            requireContext().applicationContext.getSystemService(Context.NOTIFICATION_SERVICE) as NotificationManager

        val channelId = "1"
        val channel = NotificationChannel(
            channelId,
            "Success Booking Notification",
            NotificationManager.IMPORTANCE_HIGH
        )
        mNotificationManager.createNotificationChannel(channel)
        mBuilder.setChannelId(channelId)

        mNotificationManager.notify(0, mBuilder.build())
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
        departDate : String
    ) {
        val viewModel = ViewModelProvider(requireActivity())[FlightViewModel::class.java]
        viewModel.getPostBookingLD().observe(viewLifecycleOwner) {
            if (it != null) {

                notification()
            } else {
                Toast.makeText(
                    requireActivity(),
                    "Booking Failed !",
                    Toast.LENGTH_SHORT
                ).show()
            }
        }
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

    private fun countTime(){
        //getting booking date and time
        val flightTime = sharedPrefBooking.getString("departureTime", "00:00")
        val flightArrivalTime = sharedPrefBooking.getString("arrivalTime", "00:00")

        val timeFormat = SimpleDateFormat("HH:mm", Locale.getDefault())

        val timeDepart = flightTime?.let { timeFormat.parse(it) }
        val timeArrival = flightArrivalTime?.let { timeFormat.parse(it) }

        val diff = timeDepart!!.time - timeArrival!!.time
        val timeCount = "( ${(diff / (1000 * 60 * 60) * -1)} Hours ${(diff % (1000 * 60 * 60) * -1)} Minutes )"

        binding.tvTotalTime.text = timeCount

    }

    private fun openPaymentDialog(){
        val builder = AlertDialog.Builder(context)
        builder.setTitle("Booking Success !")
        builder.setMessage("You've succesfully booked ticket, Please proceed to pay the ticket by clicking the Pay Now Button, or you can pay later within 2 Hours before the ticket become invalid")

        builder.setPositiveButton("Pay Now") { dialog, which ->
            findNavController().navigate(R.id.action_bookingFragment_to_paymentDialog)
        }

        builder.setNegativeButton("Later") { dialog, which ->
            findNavController().navigate(R.id.action_bookingFragment_to_homeFragment)
        }
        builder.show()
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
