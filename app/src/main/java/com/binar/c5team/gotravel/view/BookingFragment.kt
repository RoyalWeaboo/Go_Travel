package com.binar.c5team.gotravel.view

import android.app.Notification
import android.app.NotificationChannel
import android.app.NotificationManager
import android.app.PendingIntent
import android.content.Context
import android.content.Intent
import android.content.SharedPreferences
import android.os.Build
import android.os.Bundle
import android.util.Log
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import android.widget.ArrayAdapter
import android.widget.AutoCompleteTextView
import android.widget.Toast
import androidx.core.app.NotificationCompat
import androidx.core.content.ContextCompat.getSystemService
import androidx.fragment.app.Fragment
import androidx.lifecycle.ViewModelProvider
import androidx.navigation.fragment.findNavController
import com.binar.c5team.gotravel.R
import com.binar.c5team.gotravel.databinding.FragmentBookingBinding
import com.binar.c5team.gotravel.model.Flight
import com.binar.c5team.gotravel.viewmodel.FlightViewModel
import com.binar.c5team.gotravel.viewmodel.UserViewModel


class BookingFragment: Fragment() {
    private lateinit var binding : FragmentBookingBinding
    private lateinit var sharedPref: SharedPreferences
    private lateinit var sharedPrefFlight: SharedPreferences

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

        sharedPref = requireActivity().getSharedPreferences("data", Context.MODE_PRIVATE)
        sharedPrefFlight = requireActivity().getSharedPreferences("flightInfo", Context.MODE_PRIVATE)
        val token = sharedPref.getString("token", "-")

        //getting arguments passed from home fragment
        val flightId = arguments?.getInt("flightId")
        val userId = arguments?.getInt("userId")
        val availableSeat = arguments?.getInt("availableSeat")
        val flightPrice = arguments?.getInt("flightPrice")
        val planeName = arguments?.getString("planeName")
        val fromAirport = arguments?.getString("fromAirport")
        val toAirport = arguments?.getString("toAirport")
        val departureTime = arguments?.getString("departureTime")
        val arrivalTime = arguments?.getString("arrivalTime")
        val seatCount = arguments?.getInt("totalSeat")

        // for dropdown food option
        val items = listOf("Yes", "No")
        val adapterFood = ArrayAdapter(requireContext(), R.layout.list_item, items)
        (binding.inputFood.editText as? AutoCompleteTextView)?.setAdapter(adapterFood)

        // for dropdown seat option
        val seatNumber : MutableList<Int> = ArrayList()
        var num = 1
        for (element in 1..availableSeat!!){
            seatNumber.add(num)
            num++
        }
        val adapterSeatNum = ArrayAdapter(requireContext(), R.layout.list_item, seatNumber)
        (binding.inputChair.editText as? AutoCompleteTextView)?.setAdapter(adapterSeatNum)

        binding.tvTicketPrice.text = flightPrice.toString()
        binding.tvAircraftName.text = planeName
        binding.tvFromCity.text = fromAirport
        binding.tvArrivalCity.text = toAirport
        binding.tvTimeFrom.text = departureTime
        binding.tvTimeTo.text = arrivalTime
        binding.tvSeatTotal.text = seatCount.toString()

        binding.btnBack.setOnClickListener {
            findNavController().navigate(R.id.action_bookingFragment_to_flightListFragment)
        }

        binding.btnToPayment.setOnClickListener {
            val name = binding.inputFullname.editText?.text.toString()
            val seat = binding.inputChair.editText?.text.toString().toInt()
            val baggage = binding.inputBaggage.editText?.text.toString().toInt()
            val food: Boolean
            val foodOpt = binding.inputFood.editText?.text.toString()
            food = foodOpt == "Yes"
            val homePhone = binding.inputEmail.editText?.text.toString()
            val mobilePhone = binding.inputMobileNumber.editText?.text.toString()
            
            bookTicket(token!!, flightId!!, userId!!, seat, baggage, food, name, homePhone, mobilePhone, flightPrice!!)
        }

    }

    private fun notification(){
        val mBuilder = NotificationCompat.Builder(requireContext().applicationContext, "1")
        val ii = Intent(requireContext().applicationContext, MainActivity::class.java)
        ii.putExtra("redirect", "historyFragment")
        val pendingIntent = PendingIntent.getActivity(requireContext().applicationContext, 0, ii, 0)

        val bigText = NotificationCompat.BigTextStyle()
        bigText.setBigContentTitle("Booking Succesful !")
        bigText.setSummaryText("Successful Booking")

        mBuilder.setContentIntent(pendingIntent)
        mBuilder.setSmallIcon(R.drawable.ic_baseline_notifications)
        mBuilder.setContentTitle("Booking Succesful !")
        mBuilder.setContentText("Success booking a new ticket, Open History or click this notification to check your history !")
        mBuilder.setStyle(bigText)
        mBuilder.setDefaults(Notification.DEFAULT_ALL)

        val mNotificationManager : NotificationManager  =
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

    private fun bookTicket(token : String, id_flight : Int, id_user : Int, seat : Int, baggage : Int, food : Boolean, name : String, homephone : String, mobilephone : String, totalprice : Int){
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
        viewModel.postBookingApi(token, id_flight, id_user, seat, baggage, food, name, homephone, mobilephone, totalprice)
    }
}
