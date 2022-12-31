package com.binar.c5team.gotravel.view.fragment

import android.os.Bundle
import androidx.fragment.app.Fragment
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import androidx.lifecycle.ViewModelProvider
import androidx.navigation.Navigation
import com.binar.c5team.gotravel.R
import com.binar.c5team.gotravel.databinding.FragmentTicketDetailBinding
import com.binar.c5team.gotravel.viewmodel.FlightViewModel
import com.google.android.material.bottomnavigation.BottomNavigationView
import com.google.android.material.transition.MaterialSharedAxis
import java.text.SimpleDateFormat
import java.util.*

class TicketDetailFragment : Fragment() {
    lateinit var binding : FragmentTicketDetailBinding

    override fun onCreateView(
        inflater: LayoutInflater, container: ViewGroup?,
        savedInstanceState: Bundle?
    ): View {
        //transition anim
        enterTransition = MaterialSharedAxis(MaterialSharedAxis.X,true)
        exitTransition = MaterialSharedAxis(MaterialSharedAxis.X,true)
        reenterTransition = MaterialSharedAxis(MaterialSharedAxis.X,false)
        //inflating layout
        binding = FragmentTicketDetailBinding.inflate(inflater, container, false)
        return binding.root
    }

    override fun onViewCreated(view: View, savedInstanceState: Bundle?) {
        super.onViewCreated(view, savedInstanceState)

        val navBar = requireActivity().findViewById<BottomNavigationView>(R.id.bottom_nav)
        navBar.visibility = View.GONE
        val guestNavBar = requireActivity().findViewById<BottomNavigationView>(R.id.guest_bottom_nav)
        guestNavBar.visibility = View.GONE

        binding.detailArrowBack.setOnClickListener {
            Navigation.findNavController(view).popBackStack()
        }

        getSetData()
    }

    private fun getSetData() {
        val name = arguments?.getString("bookingName", "")
        val planeName = arguments?.getString("planeNameDetail", "")
        val bookingId = arguments?.getInt("bookingIdDetail", 0)
        val flightMode = arguments?.getString("flightModeDetail", "")
        val food = arguments?.getString("foodDetail", "")
        val baggage = arguments?.getInt("baggageDetail", 0)
        val fromCity = arguments?.getString("fromAirportCityDetail", "")
        val fromCityCode = arguments?.getString("fromAirportCityCodeDetail", "")
        val toCity = arguments?.getString("toAirportCityDetail", "")
        val toCityCode = arguments?.getString("toAirportCityCodeDetail", "")
        val departureTime = arguments?.getString("departureTimeDetail", "")
        val arrivalTime = arguments?.getString("arrivalTimeDetail", "")
        val flightDate = arguments?.getString("bookingDateDetail", "")
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

        binding.name2.text = name
        binding.baggage2.text = "$baggage Kg"
        binding.timeFrom.text = "$formattedDate, $departureTime"
        binding.timeTo.text = "$formattedDate, $arrivalTime"
        binding.cityTo.text = "$toCity, $toCityCode"
        binding.cityTo2.text = toCityCode

        val kelas: String = when (flightClass) {
            "Economy Class" -> {
                "Economy"
            }
            "First Class" -> {
                "Executive"
            }
            "Business Class" -> {
                "Business"
            }
            else -> {
                flightClass!!
            }
        }
        binding.flightClass2.text = kelas

        if(food == "true"){
            binding.food2.text = "Yes"
        }else{
            binding.food2.text = "No"
        }


    }

}