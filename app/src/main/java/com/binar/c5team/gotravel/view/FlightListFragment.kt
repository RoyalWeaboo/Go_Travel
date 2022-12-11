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
    lateinit var sharedPref: SharedPreferences

    override fun onCreateView(
        inflater: LayoutInflater, container: ViewGroup?,
        savedInstanceState: Bundle?
    ): View? {
        binding = FragmentTicketListBinding.inflate(inflater, container, false)
        return binding.root
    }

    override fun onViewCreated(view: View, savedInstanceState: Bundle?) {
        super.onViewCreated(view, savedInstanceState)
        sharedPref = requireActivity().getSharedPreferences("data", Context.MODE_PRIVATE)
        val token =  sharedPref.getString("token", "-")

        //getting arguments passed from home fragment
        val fligtMode = arguments?.getString("flightMode")
        val fromAirportId = arguments?.getInt("fromId")
        val toAirportId = arguments?.getInt("toId")
        val fromAirportCity = arguments?.getString("fromCity")
        val toAirportCity = arguments?.getString("toCity")
        val departDate = arguments?.getString("departDate")
        val returnDate = arguments?.getString("returnDate")
        val adultCount = arguments?.getString("adult")
        val childrenCount = arguments?.getString("child")

        if(fligtMode == "oneWay"){
            binding.returnDateCard.visibility = View.GONE
        }else{
            binding.returnDateCard.visibility = View.VISIBLE
        }

        binding.fromCity.text = fromAirportCity
        binding.toCity.text = toAirportCity
        binding.departureDateText.text = departDate
        binding.returnDateText.text = returnDate
        if(adultCount!!.toInt() > 1){
            binding.adultTotalCount.text = adultCount+" Adults"
        }else{
            binding.adultTotalCount.text = adultCount+" Adult"
        }
        if (childrenCount!!.toInt()>1){
            binding.childTotalCount.text = childrenCount+" Children"
        }else{
            binding.childTotalCount.text = childrenCount+" Child"
        }

        getFlight(token!!, fromAirportId!!, toAirportId!!)

        binding.ticketListArrowBack.setOnClickListener {
            findNavController().navigate(R.id.action_flightListFragment_to_homeFragment)
        }
    }

    private fun getFlight(token : String, fromAirportId : Int, toAirportId : Int) {
        val viewModel = ViewModelProvider(requireActivity())[FlightViewModel::class.java]

        viewModel.getFlightListData().observe(viewLifecycleOwner) {
            if (it != null) {
                binding.rvTicketList.layoutManager = LinearLayoutManager(
                    context, LinearLayoutManager.VERTICAL, false
                )

                val filterTicket : MutableList<Flight> = ArrayList()
                for(i in it.data.flights){
                    if(i.fromAirportId==fromAirportId && i.toAirportId==toAirportId){
                        filterTicket.add(i)
                    }
                }

                adapter = FlightAdapter(filterTicket)
                binding.rvTicketList.adapter = adapter

//                adapter.onAddFavorites = { it ->
////                    Toast.makeText(requireActivity(), it.toString(), Toast.LENGTH_SHORT).show()
//                    binding.homeProgressBar.visibility = View.VISIBLE
//                    val favViewModel =
//                        ViewModelProvider(requireActivity())[FavoritesViewModel::class.java]
//                    favViewModel.callPostFavMovie(
//                        it.posterPath,
//                        it.originalTitle,
//                        it.voteAverage.toString(),
//                        it.releaseDate,
//                        it.originalLanguage,
//                        it.overview
//                    )
//                    favViewModel.postFavMovie().observe(viewLifecycleOwner) {
//                        if (it != null) {
//                            binding.homeProgressBar.visibility = View.GONE
//                            Toast.makeText(
//                                requireActivity(),
//                                context?.getString(R.string.tambah_film_fav),
//                                Toast.LENGTH_SHORT
//                            ).show()
//                        }
//                        binding.homeProgressBar.visibility = View.GONE
//                    }
//                }

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