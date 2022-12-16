package com.binar.c5team.gotravel.view

import android.app.Notification
import android.app.NotificationChannel
import android.app.NotificationManager
import android.app.PendingIntent
import android.content.Context
import android.content.Intent
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
import androidx.core.app.NotificationCompat
import androidx.fragment.app.Fragment
import androidx.lifecycle.ViewModelProvider
import androidx.navigation.fragment.findNavController
import com.binar.c5team.gotravel.R
import com.binar.c5team.gotravel.databinding.FragmentBookingBinding
import com.binar.c5team.gotravel.viewmodel.FlightViewModel


class BookingFragment : Fragment() {
    private lateinit var binding: FragmentBookingBinding

    //Shared Preferences
    private lateinit var sharedPref: SharedPreferences
    private lateinit var sharedPrefFlight: SharedPreferences
    private lateinit var sharedPrefBooking: SharedPreferences

    private var token: String = ""
    private var userId: Int = 0
    private var fligtMode: String = ""
    private var seatCount: Int = 0

    //departure data
    private var flightId: Int = 0
    private var flightPrice: Int = 0

    private var departDate : String = ""

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

        //getting date
        departDate = sharedPrefBooking.getString("unparsedDepartDate", "null")!!

        //getting trip mode
        fligtMode = sharedPrefFlight.getString("flightMode", "").toString()

        getSetData()

        binding.btnBack.setOnClickListener {
            findNavController().navigate(R.id.action_bookingFragment_to_flightListFragment)
        }

        if (fligtMode == "oneWay") {
            if (seatCount > 1) {
                binding.passengerNumber.visibility = View.VISIBLE
                var oneWaytempDataCount = 1
                binding.btnToPayment.setOnClickListener {
                    oneWaytempDataCount++
                    binding.passengerNumber.text = "Passenger - " + oneWaytempDataCount.toString()
                    bookNewTicket()
                    clearInput()
                    if (oneWaytempDataCount > seatCount) {
                        findNavController().navigate(R.id.action_bookingFragment_to_homeFragment)
                    }
                }
            } else {
                binding.btnToPayment.setOnClickListener {
                    bookNewTicket()
                    findNavController().navigate(R.id.action_bookingFragment_to_homeFragment)
                }
            }
        } else if (fligtMode == "roundTrip") {
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
        binding.inputChair.editText?.text?.clear()
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
        val availableSeat = sharedPrefBooking.getInt("availableSeat", 0)
        val planeName = sharedPrefBooking.getString("planeName", "Error")
        val fromAirport = sharedPrefBooking.getString("fromAirport", "Error")
        val toAirport = sharedPrefBooking.getString("toAirport", "Error")
        val departureTime = sharedPrefBooking.getString("departureTime", "00:00")
        val arrivalTime = sharedPrefBooking.getString("arrivalTime", "00:00")

        // for dropdown food option
        val items = listOf("Yes", "No")
        val adapterFood = ArrayAdapter(requireContext(), R.layout.list_item, items)
        (binding.inputFood.editText as? AutoCompleteTextView)?.setAdapter(adapterFood)

        // for dropdown seat option
        val seatNumber: MutableList<Int> = ArrayList()
        var num = 1
        for (element in 1..availableSeat) {
            seatNumber.add(num)
            num++
        }
        val adapterSeatNum = ArrayAdapter(requireContext(), R.layout.list_item, seatNumber)
        (binding.inputChair.editText as? AutoCompleteTextView)?.setAdapter(adapterSeatNum)

        //total flight price
        val total = seatCount * flightPrice

        //setting the textview
        binding.tvTicketPrice.text = total.toString()
        binding.tvAircraftName.text = planeName
        binding.tvFromCity.text = fromAirport
        binding.tvArrivalCity.text = toAirport
        binding.tvTimeFrom.text = departureTime
        binding.tvTimeTo.text = arrivalTime
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
        viewModel.getBookingLD().observe(viewLifecycleOwner) {
            if (it != null) {
                Log.d("Booking Response :", it.toString())
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

}
