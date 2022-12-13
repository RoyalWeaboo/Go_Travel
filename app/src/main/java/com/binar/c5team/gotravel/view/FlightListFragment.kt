package com.binar.c5team.gotravel.view

import android.content.Context
import android.content.SharedPreferences
import android.os.Bundle
import android.util.Log
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import android.widget.Toast
import androidx.fragment.app.Fragment
import androidx.lifecycle.ViewModelProvider
import androidx.navigation.fragment.findNavController
import androidx.recyclerview.widget.LinearLayoutManager
import com.binar.c5team.gotravel.R
import com.binar.c5team.gotravel.databinding.FragmentTicketListBinding
import com.binar.c5team.gotravel.model.Flight
import com.binar.c5team.gotravel.view.adapter.FlightAdapter
import com.binar.c5team.gotravel.viewmodel.FlightViewModel

class FlightListFragment : Fragment() {
    lateinit var binding: FragmentTicketListBinding
    private lateinit var adapter: FlightAdapter
    private lateinit var sharedPref: SharedPreferences
    private lateinit var sharedPrefFlight: SharedPreferences
    var userId: Int = 0

    override fun onCreateView(
        inflater: LayoutInflater, container: ViewGroup?,
        savedInstanceState: Bundle?
    ): View {
        binding = FragmentTicketListBinding.inflate(inflater, container, false)
        return binding.root
    }

    override fun onViewCreated(view: View, savedInstanceState: Bundle?) {
        super.onViewCreated(view, savedInstanceState)
        sharedPref = requireActivity().getSharedPreferences("data", Context.MODE_PRIVATE)
        sharedPrefFlight = requireActivity().getSharedPreferences("flightInfo", Context.MODE_PRIVATE)
        val token = sharedPref.getString("token", "")
        userId = sharedPref.getInt("userId", 0)

        //getting arguments passed from home fragment
        val fligtMode = sharedPrefFlight.getString("flightMode", "")
        val fromAirportId = sharedPrefFlight.getInt("fromId", 0)
        val toAirportId = sharedPrefFlight.getInt("toId", 0)
        val departDate = sharedPrefFlight.getString("departDate", "")
        val returnDate = sharedPrefFlight.getString("returnDate", "")
        val fromAirportCity = sharedPrefFlight.getString("fromAirport", "")
        val toAirportCity = sharedPrefFlight.getString("toAirport", "")
        val adultCount = sharedPrefFlight.getString("adultCount", "")
        val childrenCount = sharedPrefFlight.getString("childCount", "")

        if (fligtMode == "oneWay") {
            binding.returnDateCard.visibility = View.GONE
        } else {
            binding.returnDateCard.visibility = View.VISIBLE
        }

        binding.fromCity.text = fromAirportCity
        binding.toCity.text = toAirportCity
        binding.departureDateText.text = departDate
        binding.returnDateText.text = returnDate
        if (adultCount!!.toInt() > 1) {
            binding.adultTotalCount.text = adultCount + " Adults"
        } else if (adultCount.toInt() == 1) {
            binding.adultTotalCount.text = adultCount + " Adult"
        } else {
            binding.adultTotalCount.text = "- 0 Adult"
        }
        if (childrenCount!!.toInt() > 1) {
            binding.childTotalCount.text = "- " + childrenCount + " Children"
        } else if (childrenCount.toInt() == 1) {
            binding.childTotalCount.text = "- " + childrenCount + " Child"
        } else {
            binding.childTotalCount.text = "- 0 Child"
        }

        val countTotal = adultCount.toInt()+childrenCount.toInt()

        getFlight(token!!, fromAirportId, toAirportId, countTotal)

        binding.ticketListArrowBack.setOnClickListener {
            findNavController().navigate(R.id.action_flightListFragment_to_homeFragment)
        }

    }

    private fun getFlight(token: String, fromAirportId: Int, toAirportId: Int, countTotal : Int) {
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
                Log.d("Id", fromAirportId.toString() + toAirportId.toString())
                adapter = FlightAdapter(filterTicket)
                binding.rvTicketList.adapter = adapter

                adapter.onOrderClick = {
                    val orderInfo = Bundle()
                    if (userId != 0) {
                        orderInfo.putInt("userId", userId)
                        orderInfo.putInt("flightId", it.id)
                        orderInfo.putInt("flightPrice", it.price)
                        orderInfo.putString("planeName", it.plane.name)
                        orderInfo.putString("fromAirport", it.fromAirport.city)
                        orderInfo.putString("toAirport", it.toAirport.city)
                        orderInfo.putString("departureTime", it.departureTime)
                        orderInfo.putString("arrivalTime", it.arrivalTime)
                        orderInfo.putInt("availableSeat", it.availableSeats)
                        orderInfo.putInt("totalSeat", countTotal)
                        findNavController().navigate(
                            R.id.action_flightListFragment_to_bookingFragment,
                            orderInfo
                        )
                    } else {
                        Toast.makeText(context, "Error : Cannot Read User Id", Toast.LENGTH_SHORT)
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


}