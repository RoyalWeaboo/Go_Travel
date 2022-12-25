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
import androidx.navigation.fragment.findNavController
import androidx.recyclerview.widget.LinearLayoutManager
import com.binar.c5team.gotravel.R
import com.binar.c5team.gotravel.databinding.FragmentWishlistBinding
import com.binar.c5team.gotravel.model.Whislists
import com.binar.c5team.gotravel.view.adapter.WishlistAdapter
import com.binar.c5team.gotravel.viewmodel.FlightViewModel

class WishlistFragment : Fragment() {
    lateinit var binding: FragmentWishlistBinding

    //Shared Preferences
    private lateinit var sharedPref: SharedPreferences
    private lateinit var sharedPrefFlight: SharedPreferences
    private lateinit var sharedPrefBooking: SharedPreferences

    private var userId: Int = 0
    private var token: String = ""

    private lateinit var adapter: WishlistAdapter

    //progressbar
    var progressView: ViewGroup? = null
    private var isProgressShowing = false

    override fun onCreateView(
        inflater: LayoutInflater, container: ViewGroup?,
        savedInstanceState: Bundle?
    ): View {
        binding = FragmentWishlistBinding.inflate(inflater, container, false)
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

        //fetch wishlist data by user id with token
        getWishlist(token, userId)

        binding.wishlistArrowBack.setOnClickListener {
            findNavController().navigate(R.id.action_wishlistFragment_to_homeFragment)
        }


    }

    private fun getWishlist(token: String, userId: Int) {
        val viewModel = ViewModelProvider(requireActivity())[FlightViewModel::class.java]
        viewModel.getWishlistLD().observe(viewLifecycleOwner) {
            if (it != null) {
                binding.rvWishlist.layoutManager = LinearLayoutManager(
                    context, LinearLayoutManager.VERTICAL, false
                )

                val filterWishlist: MutableList<Whislists> = ArrayList()
                if (filterWishlist.isEmpty()) {
                    for (i in it.data.whislists) {
                        if (i.idUser == userId) {
                            filterWishlist.add(i)
                        }
                    }
                }
                adapter = WishlistAdapter(filterWishlist)
                binding.rvWishlist.adapter = adapter

                adapter.onOrderClick = {
                    if (userId != 0) {
                        val wishlistBookFlightMode = sharedPrefFlight.edit()
                        wishlistBookFlightMode.putString("flightMode", "oneWay")
                        wishlistBookFlightMode.apply()

                        //saving departure flight data to shared pref
                        val wishlistBook = sharedPrefBooking.edit()

                        wishlistBook.putInt("userId", userId)
                        wishlistBook.putInt("flightId", it.flight.id)
                        wishlistBook.putInt("flightPrice", it.flight.price)
                        wishlistBook.putInt("availableSeat", it.flight.availableSeats)
                        wishlistBook.putInt("totalSeat", 1)
                        wishlistBook.putString("planeName", it.flight.plane.name)
                        wishlistBook.putString("fromAirport", it.flight.fromAirport.city)
                        wishlistBook.putString("toAirport", it.flight.toAirport.city)
                        wishlistBook.putString("departureTime", it.flight.departureTime)
                        wishlistBook.putString("arrivalTime", it.flight.arrivalTime)
                        wishlistBook.putString("departureDate", it.flight.flightDate)

                        wishlistBook.apply()
                        findNavController().navigate(
                            R.id.action_wishlistFragment_to_bookingFragment
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

                adapter.onDeleteClick = { ids ->
                    val delViewModel = ViewModelProvider(requireActivity())[FlightViewModel::class.java]
                    delViewModel.deleteWishlistLD().observe(viewLifecycleOwner) { its ->
                        if (its != null) {
                            Toast.makeText(
                                requireActivity(),
                                "Ticket Deleted from Wishlist",
                                Toast.LENGTH_SHORT
                            ).show()
                        }else{
                            Toast.makeText(
                                requireActivity(),
                                "Delete Failed",
                                Toast.LENGTH_SHORT
                            ).show()
                        }
                    }
                    delViewModel.callDeleteWishlist(token, ids)
                }
                adapter.notifyDataSetChanged()
            }
        }
        viewModel.callWishlistApi(token)
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