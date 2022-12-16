package com.binar.c5team.gotravel.view

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
import androidx.navigation.fragment.findNavController
import androidx.recyclerview.widget.LinearLayoutManager
import com.binar.c5team.gotravel.R
import com.binar.c5team.gotravel.databinding.FragmentTicketListBinding
import com.binar.c5team.gotravel.model.Flight
import com.binar.c5team.gotravel.view.adapter.FlightAdapter
import com.binar.c5team.gotravel.viewmodel.FlightViewModel

class RoundTripFlightListFragment : Fragment() {
    lateinit var binding: FragmentTicketListBinding
    private lateinit var adapter: FlightAdapter

    private lateinit var sharedPref: SharedPreferences
    private lateinit var sharedPrefFlight: SharedPreferences
    private lateinit var sharedPrefBooking: SharedPreferences

    private var userId: Int = 0
    private var token: String = ""
    private var fromAirportId: Int = 0
    private var toAirportId: Int = 0
    private var adultCount: Int = 0
    private var fligtMode: String = ""


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
        token = sharedPref.getString("token", "").toString()
        userId = sharedPref.getInt("userId", 0)

        //getting flight data then set the data to layout
        getSetData()

        //fetch flight data by airport id with token
        getFlight(token, fromAirportId, toAirportId)

        binding.ticketListArrowBack.setOnClickListener {
            findNavController().navigate(R.id.action_flightListFragment_to_homeFragment)
        }

    }

    private fun getSetData() {
        //getting flight data
        fligtMode = sharedPrefFlight.getString("flightMode", " ").toString()
        fromAirportId = sharedPrefFlight.getInt("fromId", 0)
        toAirportId = sharedPrefFlight.getInt("toId", 0)
        adultCount = sharedPrefFlight.getString("adultCount", "")!!.toInt()

        val fromAirportCity = sharedPrefFlight.getString("toAirport", "")
        val toAirportCity = sharedPrefFlight.getString("fromAirport", "")
        val departDate = sharedPrefFlight.getString("departDate", "")
        val returnDate = sharedPrefFlight.getString("returnDate", "")
        val childrenCount = sharedPrefFlight.getString("childCount", "")

        //checking flight mode
        if (fligtMode == "oneWay") {
            binding.returnDateCard.visibility = View.GONE
        } else {
            binding.returnDateCard.visibility = View.VISIBLE
        }

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
            findNavController().navigate(R.id.action_flightListFragment_to_homeFragment)
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
            findNavController().navigate(R.id.action_flightListFragment_to_homeFragment)

        }

        binding.fromCity.text = fromAirportCity
        binding.toCity.text = toAirportCity
        binding.departureDateText.text = departDate
        binding.returnDateText.text = returnDate
    }

    private fun getFlight(token: String, fromAirportId: Int, toAirportId: Int) {
        val viewModel = ViewModelProvider(requireActivity())[FlightViewModel::class.java]

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

                        findNavController().navigate(
                            R.id.action_roundTripFlightListFragment_to_bookingFragment
                        )
                    } else {
                        Toast.makeText(context, "Error : Cannot Read User Id", Toast.LENGTH_SHORT)
                            .show()
                    }
                }
                adapter.onWishlistClick = {
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
        val viewModel = ViewModelProvider(requireActivity())[FlightViewModel::class.java]

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

}