package com.binar.c5team.gotravel.view.fragment

import android.content.Context
import android.content.SharedPreferences
import android.os.Bundle
import android.util.Log
import androidx.fragment.app.Fragment
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import android.widget.Toast
import androidx.lifecycle.ViewModelProvider
import androidx.navigation.Navigation
import androidx.navigation.fragment.findNavController
import androidx.recyclerview.widget.LinearLayoutManager
import com.binar.c5team.gotravel.R
import com.binar.c5team.gotravel.databinding.FragmentHistoryBinding
import com.binar.c5team.gotravel.model.Booking
import com.binar.c5team.gotravel.view.adapter.HistoryAdapter
import com.binar.c5team.gotravel.viewmodel.FlightViewModel
import com.google.android.material.bottomnavigation.BottomNavigationView

class HistoryFragment : Fragment() {
    lateinit var binding : FragmentHistoryBinding

    //Shared Preferences
    private lateinit var sharedPref: SharedPreferences

    private lateinit var adapter : HistoryAdapter

    private var userId: Int = 0
    private var token: String = ""

    //progressbar
    var progressView: ViewGroup? = null
    private var isProgressShowing = false

    override fun onCreateView(
        inflater: LayoutInflater, container: ViewGroup?,
        savedInstanceState: Bundle?
    ): View {
        binding = FragmentHistoryBinding.inflate(inflater, container, false)
        return binding.root
    }

    override fun onViewCreated(view: View, savedInstanceState: Bundle?) {
        super.onViewCreated(view, savedInstanceState)

        //SharedPref for user data
        sharedPref = requireActivity().getSharedPreferences("data", Context.MODE_PRIVATE)

        //getting user data
        token = sharedPref.getString("token", "").toString()
        userId = sharedPref.getInt("userId", 0)

        val navBar = requireActivity().findViewById<BottomNavigationView>(R.id.bottom_nav)
        navBar.visibility = View.VISIBLE

        val guestNavBar = requireActivity().findViewById<BottomNavigationView>(R.id.guest_bottom_nav)
        guestNavBar.visibility = View.GONE

        val viewModel = ViewModelProvider(this)[FlightViewModel::class.java]
        viewModel.loading.observe(viewLifecycleOwner) {
            when (it) {
                true -> showProgressingView()
                false -> hideProgressingView()
            }
        }

        getHistory(view, token, userId)
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

}