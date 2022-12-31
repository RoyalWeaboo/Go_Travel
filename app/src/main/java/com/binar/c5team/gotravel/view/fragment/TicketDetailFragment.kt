package com.binar.c5team.gotravel.view.fragment

import android.os.Bundle
import androidx.fragment.app.Fragment
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import androidx.navigation.Navigation
import com.binar.c5team.gotravel.databinding.FragmentTicketDetailBinding
import java.text.SimpleDateFormat
import java.util.*

class TicketDetailFragment : Fragment() {
    lateinit var binding : FragmentTicketDetailBinding

    override fun onCreateView(
        inflater: LayoutInflater, container: ViewGroup?,
        savedInstanceState: Bundle?
    ): View {
        binding = FragmentTicketDetailBinding.inflate(inflater, container, false)
        return binding.root
    }

    override fun onViewCreated(view: View, savedInstanceState: Bundle?) {
        super.onViewCreated(view, savedInstanceState)

        binding.detailArrowBack.setOnClickListener {
            Navigation.findNavController(view).popBackStack()
        }

        getSetData()
    }

    private fun getSetData() {
        val planeName = arguments?.getString("planeNameDetail", "")
        val bookingId = arguments?.getInt("bookingIdDetail", 0)
        val flightMode = arguments?.getString("flightModeDetail", "")
        val flightId = arguments?.getInt("flightIdDetail", 0)
        val seat = arguments?.getInt("availableSeatDetail", 0)
        val fromCity = arguments?.getString("fromAirportCityDetail", "")
        val fromCityCode = arguments?.getString("fromAirportCityCodeDetail", "")
        val toCity = arguments?.getString("toAirportCityDetail", "")
        val toCityCode = arguments?.getString("toAirportCityCodeDetail", "")
        val departureTime = arguments?.getString("departureTimeDetail", "")
        val arrivalTime = arguments?.getString("arrivalTimeDetail", "")
        val flightDate = arguments?.getString("flightDateDetail", "")
        val flightClass = arguments?.getString("classDetail", "")

        //date sdf
        binding.planeName.text = planeName
        binding.flightMode.text = flightMode
        binding.BookingId.text = bookingId.toString()
        binding.cityFrom.text = "$fromCity, $fromCityCode"
        binding.cityFrom2.text = fromCityCode

        val sdfToDate = SimpleDateFormat("yyyy-MM-dd", Locale.getDefault())
        val sdfToString = SimpleDateFormat("MMM dd", Locale.getDefault())

        val parseToDate = sdfToDate.parse(flightDate)
        val formattedDate = sdfToString.format(parseToDate)

        binding.timeFrom.text = "$formattedDate, $departureTime"
        binding.timeTo.text = "$formattedDate, $arrivalTime"
        binding.cityTo.text = "$toCity, $toCityCode"
        binding.cityTo2.text = toCityCode
        binding.flightId2.text = flightId.toString()
        binding.seat2.text = seat.toString()
        binding.flightClass2.text = flightClass

    }

}