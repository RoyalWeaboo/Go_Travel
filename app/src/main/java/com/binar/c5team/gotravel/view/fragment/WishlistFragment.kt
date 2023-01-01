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
import androidx.recyclerview.widget.LinearLayoutManager
import com.binar.c5team.gotravel.R
import com.binar.c5team.gotravel.databinding.FragmentWishlistBinding
import com.binar.c5team.gotravel.model.Whislists
import com.binar.c5team.gotravel.view.adapter.WishlistAdapter
import com.binar.c5team.gotravel.viewmodel.FlightViewModel
import com.google.android.material.bottomnavigation.BottomNavigationView
import com.google.android.material.transition.MaterialSharedAxis

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

    //viewmodel
    lateinit var viewModel: FlightViewModel

    override fun onCreateView(
        inflater: LayoutInflater, container: ViewGroup?,
        savedInstanceState: Bundle?
    ): View {
        //transition anim
        enterTransition = MaterialSharedAxis(MaterialSharedAxis.X,true)
        exitTransition = MaterialSharedAxis(MaterialSharedAxis.X,true)
        reenterTransition = MaterialSharedAxis(MaterialSharedAxis.X,false)
        //vm
        viewModel = ViewModelProvider(this)[FlightViewModel::class.java]
        //inflating layout
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

        val navBar = requireActivity().findViewById<BottomNavigationView>(R.id.bottom_nav)
        navBar.visibility = View.GONE
        val guestNavBar = requireActivity().findViewById<BottomNavigationView>(R.id.guest_bottom_nav)
        guestNavBar.visibility = View.GONE

        viewModel.loading.observe(viewLifecycleOwner) {
            when (it) {
                true -> showProgressingView()
                false -> hideProgressingView()
            }
        }

        //fetch wishlist data by user id with token
        getWishlist(view, token, userId)

        binding.wishlistArrowBack.setOnClickListener {
            Navigation.findNavController(view).popBackStack()
        }


    }

    private fun getWishlist(view : View, token: String, userId: Int) {
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

                adapter.onOrderClick = { wishlist ->
                    if (userId != 0) {
                        val wishlistBookFlightMode = sharedPrefFlight.edit()
                        wishlistBookFlightMode.putString("flightMode", "oneWay")
                        wishlistBookFlightMode.apply()

                        //saving departure flight data to shared pref
                        val wishlistBook = sharedPrefBooking.edit()

                        wishlistBook.putInt("userId", userId)
                        wishlistBook.putInt("flightId", wishlist.flight.id)
                        wishlistBook.putInt("flightPrice", wishlist.flight.price)
                        wishlistBook.putInt("availableSeat", wishlist.flight.availableSeats)
                        wishlistBook.putInt("totalSeat", 1)
                        wishlistBook.putString("planeName", wishlist.flight.plane.name)
                        wishlistBook.putString("fromAirport", wishlist.flight.fromAirport.city)
                        wishlistBook.putString("toAirport", wishlist.flight.toAirport.city)
                        wishlistBook.putString("departureTime", wishlist.flight.departureTime)
                        wishlistBook.putString("arrivalTime", wishlist.flight.arrivalTime)
                        wishlistBook.putString("departureDate", wishlist.flight.flightDate)

                        wishlistBook.apply()
                        Navigation.findNavController(view).navigate(
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
                    val delViewModel = ViewModelProvider(this)[FlightViewModel::class.java]
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