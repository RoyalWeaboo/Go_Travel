package com.binar.c5team.gotravel.view.fragment

import android.content.Context
import android.content.SharedPreferences
import android.os.Bundle
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import android.widget.Toast
import androidx.activity.OnBackPressedCallback
import androidx.fragment.app.Fragment
import androidx.lifecycle.ViewModelProvider
import androidx.navigation.Navigation
import androidx.navigation.fragment.findNavController
import androidx.recyclerview.widget.LinearLayoutManager
import com.binar.c5team.gotravel.R
import com.binar.c5team.gotravel.databinding.FragmentTicketListBinding
import com.binar.c5team.gotravel.model.Flight
import com.binar.c5team.gotravel.view.adapter.FlightAdapter
import com.binar.c5team.gotravel.viewmodel.FlightViewModel
import com.google.android.material.bottomnavigation.BottomNavigationView


class RoundTripFlightListFragment : Fragment() {

    lateinit var binding: FragmentTicketListBinding
    private lateinit var adapter: FlightAdapter

    private lateinit var sharedPref: SharedPreferences
    private lateinit var sharedPrefFlight: SharedPreferences
    private lateinit var sharedPrefBooking: SharedPreferences

    private var session: String = ""
    private var userId: Int = 0
    private var token: String = ""
    private var fromAirportId: Int = 0
    private var toAirportId: Int = 0
    private var adultCount: Int = 0
    private var fligtMode: String = ""

    //progressbar
    var progressView: ViewGroup? = null
    private var isProgressShowing = false


    override fun onCreateView(
        inflater: LayoutInflater, container: ViewGroup?,
        savedInstanceState: Bundle?
    ): View {
        binding = FragmentTicketListBinding.inflate(inflater, container, false)
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

        //letting user know that this is for return ticket
        Toast.makeText(context, "Choose Return Ticket", Toast.LENGTH_LONG).show()

        //getting user data
        session = sharedPref.getString("session", "").toString()
        token = sharedPref.getString("token", "").toString()
        userId = sharedPref.getInt("userId", 0)

        val navBar = requireActivity().findViewById<BottomNavigationView>(R.id.bottom_nav)
        navBar.visibility = View.GONE
        val guestNavBar = requireActivity().findViewById<BottomNavigationView>(R.id.guest_bottom_nav)
        guestNavBar.visibility = View.GONE

        val viewModel = ViewModelProvider(this)[FlightViewModel::class.java]
        viewModel.loading.observe(viewLifecycleOwner) {
            when (it) {
                true -> showProgressingView()
                false -> hideProgressingView()
            }
        }

        //getting flight data then set the data to layout
        getSetData(view)

        //fetch flight data by airport id with token
        getFlight(view, token, fromAirportId, toAirportId)

        binding.ticketListArrowBack.setOnClickListener {
            Navigation.findNavController(view).navigate(R.id.action_flightListFragment_to_homeFragment)
        }

    }

    private fun getSetData(view : View) {
        //getting flight data
        fligtMode = sharedPrefFlight.getString("flightMode", " ").toString()
        fromAirportId = sharedPrefFlight.getInt("toId", 0)
        toAirportId = sharedPrefFlight.getInt("fromId", 0)
        adultCount = sharedPrefFlight.getString("adultCount", "")!!.toInt()

        val fromAirportCity = sharedPrefFlight.getString("toAirport", "")
        val toAirportCity = sharedPrefFlight.getString("fromAirport", "")
        val departDate = sharedPrefFlight.getString("departDate", "")
        val childrenCount = sharedPrefFlight.getString("childCount", "")

        //checking passenger count
        //adult count
        if (adultCount > 1) {
            binding.adultTotalCount.text = adultCount.toString() + " Adults"
        } else if (adultCount == 1) {
            binding.adultTotalCount.text = adultCount.toString() + " Adult"
        } else {
            binding.adultTotalCount.text = "- 0 Adult"
            Toast.makeText(context, "Error : Cannot read passenger count !", Toast.LENGTH_SHORT)
                .show()
            Navigation.findNavController(view).navigate(R.id.action_flightListFragment_to_homeFragment)
        }
        //child count
        if (childrenCount!!.toInt() > 1) {
            binding.childTotalCount.text = "- " + childrenCount + " Children"
        } else if (childrenCount.toInt() == 1) {
            binding.childTotalCount.text = "- " + childrenCount + " Child"
        } else if (childrenCount.toInt() == 0){
            binding.childTotalCount.text = "- 0 Child"
        }else {
            Toast.makeText(context, "Error : Cannot read passenger count !", Toast.LENGTH_SHORT)
                .show()
            Navigation.findNavController(view).navigate(R.id.action_flightListFragment_to_homeFragment)

        }

        binding.fromCity.text = fromAirportCity
        binding.toCity.text = toAirportCity
        binding.departureDateText.text = departDate
    }

    private fun getFlight(view: View, token: String, fromAirportId: Int, toAirportId: Int) {
        val viewModel = ViewModelProvider(this)[FlightViewModel::class.java]

        viewModel.getFlightListData().observe(viewLifecycleOwner) { it ->
            if (it != null) {
                binding.rvTicketList.layoutManager = LinearLayoutManager(
                    context, LinearLayoutManager.VERTICAL, false
                )

                val filterTicket: MutableList<Flight> = ArrayList()
                for (i in it.data.flights) {
                    if (i.fromAirportId == fromAirportId && i.toAirportId == toAirportId) {
                        filterTicket.add(i)
                    }
                }

                adapter = FlightAdapter(filterTicket)
                binding.rvTicketList.adapter = adapter

                adapter.onOrderClick = {
                    if (session == "true") {
                        if (userId != 0) {
                            //saving departure flight data to shared pref
                            val departureBookingData = sharedPrefBooking.edit()

                            departureBookingData.putInt("roundFlightId", it.id)
                            departureBookingData.putInt("roundFlightPrice", it.price)
                            departureBookingData.putInt("roundAvailableSeat", it.availableSeats)

                            departureBookingData.putString("roundPlaneName", it.plane.name)
                            departureBookingData.putString("roundFromAirport", it.fromAirport.city)
                            departureBookingData.putString("roundToAirport", it.toAirport.city)
                            departureBookingData.putString("roundDepartureTime", it.departureTime)
                            departureBookingData.putString("roundArrivalTime", it.arrivalTime)

                            departureBookingData.apply()

                            Navigation.findNavController(view).navigate(
                                R.id.action_roundTripFlightListFragment_to_bookingFragment
                            )
                        } else {
                            Toast.makeText(
                                context,
                                "Error : Cannot Read User Id",
                                Toast.LENGTH_SHORT
                            )
                                .show()
                        }
                    }
                }
                adapter.onWishlistClick = {
                    if(session == "true"){
                        if (userId != 0) {
                            addNewWishlist(token, userId, it.id)
                        } else {
                            Toast.makeText(
                                context,
                                "Error : Cannot Read User Id",
                                Toast.LENGTH_SHORT
                            )
                                .show()
                        }
                    }
                }

            } else {
                Toast.makeText(
                    requireActivity(),
                    "No Data",
                    Toast.LENGTH_SHORT
                ).show()
            }
        }
        viewModel.callFlightApi(token)
    }

    private fun addNewWishlist(token: String, id_flight : Int, id_user : Int){
        val viewModel = ViewModelProvider(this)[FlightViewModel::class.java]

        viewModel.postWishlistLD().observe(viewLifecycleOwner) {
            if (it != null) {
                Toast.makeText(
                    context,
                    "Added to Wishlist !",
                    Toast.LENGTH_SHORT
                )
                    .show()
            }else{
                Toast.makeText(
                    requireActivity(),
                    "Failed adding ticket to Wishlist",
                    Toast.LENGTH_SHORT
                ).show()
            }
        }
        viewModel.postWishlistApi(token, id_flight, id_user)
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