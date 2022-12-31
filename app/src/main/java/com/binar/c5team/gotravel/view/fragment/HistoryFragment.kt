package com.binar.c5team.gotravel.view.fragment

import android.content.Context
import android.content.SharedPreferences
import android.net.ConnectivityManager
import android.os.Bundle
import androidx.fragment.app.Fragment
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import android.widget.Toast
import androidx.lifecycle.ViewModelProvider
import androidx.navigation.Navigation
import androidx.recyclerview.widget.LinearLayoutManager
import com.binar.c5team.gotravel.R
import com.binar.c5team.gotravel.databinding.FragmentHistoryBinding
import com.binar.c5team.gotravel.model.Booking
import com.binar.c5team.gotravel.view.adapter.HistoryAdapter
import com.binar.c5team.gotravel.viewmodel.FlightViewModel
import com.google.android.material.bottomnavigation.BottomNavigationView
import com.google.android.material.transition.MaterialFadeThrough
import com.google.android.material.transition.MaterialSharedAxis
import dagger.hilt.android.AndroidEntryPoint

class HistoryFragment : Fragment() {
    lateinit var binding : FragmentHistoryBinding

    //Shared Preferences
    private lateinit var sharedPref: SharedPreferences
    private lateinit var sharedPrefBooking: SharedPreferences

    private lateinit var adapter : HistoryAdapter

    private var userId: Int = 0
    private var token: String = ""
    private var session: String = ""

    //progressbar
    var progressView: ViewGroup? = null
    private var isProgressShowing = false

    //connection
    var connection : Boolean = false

    override fun onCreateView(
        inflater: LayoutInflater, container: ViewGroup?,
        savedInstanceState: Bundle?
    ): View {
        binding = FragmentHistoryBinding.inflate(inflater, container, false)
        enterTransition = MaterialFadeThrough()
        exitTransition = MaterialFadeThrough()
        return binding.root
    }

    override fun onViewCreated(view: View, savedInstanceState: Bundle?) {
        super.onViewCreated(view, savedInstanceState)

        //SharedPref for user data
        sharedPref = requireActivity().getSharedPreferences("data", Context.MODE_PRIVATE)
        //SharedPref for booking data
        sharedPrefBooking = requireActivity().getSharedPreferences("bookingInfo", Context.MODE_PRIVATE)

        //getting user data
        session
        token = sharedPref.getString("token", "").toString()
        userId = sharedPref.getInt("userId", 0)
        session = sharedPref.getString("session", "").toString()

        checkConnection()

        val viewModel = ViewModelProvider(this)[FlightViewModel::class.java]
        viewModel.loading.observe(viewLifecycleOwner) {
            when (it) {
                true -> showProgressingView()
                false -> hideProgressingView()
            }
        }

        if (connection) {
            getHistory(view, token, userId)
        }else{
            Toast.makeText(context, "No internet connection", Toast.LENGTH_SHORT).show()
        }
    }

    private fun getHistory(view : View, token: String, userId: Int) {
        val viewModel = ViewModelProvider(this)[FlightViewModel::class.java]
        viewModel.getBookingLD().observe(viewLifecycleOwner) {
            if (it != null) {
                binding.rvHistory.layoutManager = LinearLayoutManager(
                    context, LinearLayoutManager.VERTICAL, false
                )

                val filterHistory: MutableList<Booking> = ArrayList()
                if (filterHistory.isEmpty()) {
                    for (i in it.data.bookings) {
                        if (i.idUser == userId) {
                            filterHistory.add(i)
                        }
                    }
                }
                adapter = HistoryAdapter(filterHistory)
                binding.rvHistory.adapter = adapter

                adapter.onStatusClick = { bookingData->
                    val ticketId = ArrayList<Int>()
                    ticketId.add(0, bookingData.id)

                    val bund = Bundle()
                    bund.putIntegerArrayList("bookingIds", ticketId)
                    Navigation.findNavController(view).navigate(R.id.action_historyFragment_to_paymentDialog, bund)
                }

                adapter.onTicketClick = { detail ->
                    if (session == "true") {
                        if (userId != 0) {
                            //saving departure flight data to shared pref
                            val historyBundle = Bundle()

                            historyBundle.putString("planeNameDetail", detail.flight.plane.name)
                            historyBundle.putInt("bookingIdDetail", detail.id)
                            historyBundle.putString("flightModeDetail", detail.tripType)
                            historyBundle.putInt("flightIdDetail", detail.flight.id)
                            historyBundle.putInt("availableSeatDetail", detail.flight.availableSeats)
                            historyBundle.putString("fromAirportCityDetail", detail.flight.fromAirport.city)
                            historyBundle.putString("fromAirportCityCodeDetail", detail.flight.fromAirport.code)
                            historyBundle.putString("toAirportCityDetail", detail.flight.toAirport.city)
                            historyBundle.putString("toAirportCityCodeDetail", detail.flight.toAirport.code)
                            historyBundle.putString("departureTimeDetail", detail.flight.departureTime)
                            historyBundle.putString("arrivalTimeDetail", detail.flight.arrivalTime)
                            historyBundle.putString("flightDateDetail", detail.flight.flightDate)
                            historyBundle.putString("classDetail", detail.flight.kelas)

                            Navigation.findNavController(view).navigate(R.id.action_historyFragment_to_ticketDetailFragment, historyBundle)
                        }else{
                            Toast.makeText(context, "Cannot Read User Id", Toast.LENGTH_SHORT).show()
                            Navigation.findNavController(view).navigate(R.id.action_historyFragment_to_loginFragment)
                        }
                    }else{
                        Toast.makeText(context, "Session is empty", Toast.LENGTH_SHORT).show()
                        Navigation.findNavController(view).navigate(R.id.action_historyFragment_to_loginFragment)
                    }
                }
            }else{
                Toast.makeText(context, "No History Found !", Toast.LENGTH_SHORT).show()
            }
        }
        viewModel.callBookingApi(token)
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

    private fun checkConnection() {
        val cm = requireActivity().getSystemService(Context.CONNECTIVITY_SERVICE) as ConnectivityManager
        val res = cm.activeNetwork
        connection = res != null
    }

}