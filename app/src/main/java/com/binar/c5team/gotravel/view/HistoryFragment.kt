package com.binar.c5team.gotravel.view

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
import androidx.navigation.fragment.findNavController
import androidx.recyclerview.widget.LinearLayoutManager
import com.binar.c5team.gotravel.R
import com.binar.c5team.gotravel.databinding.FragmentHistoryBinding
import com.binar.c5team.gotravel.model.Booking
import com.binar.c5team.gotravel.model.Whislists
import com.binar.c5team.gotravel.view.adapter.HistoryAdapter
import com.binar.c5team.gotravel.view.adapter.WishlistAdapter
import com.binar.c5team.gotravel.viewmodel.FlightViewModel
import com.google.android.material.bottomnavigation.BottomNavigationView

class HistoryFragment : Fragment() {
    lateinit var binding : FragmentHistoryBinding

    //Shared Preferences
    private lateinit var sharedPref: SharedPreferences

    private lateinit var adapter : HistoryAdapter

    private var userId: Int = 0
    private var token: String = ""

    override fun onCreateView(
        inflater: LayoutInflater, container: ViewGroup?,
        savedInstanceState: Bundle?
    ): View? {
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

        getHistory(token, userId)
    }

    private fun getHistory(token: String, userId: Int) {
        val viewModel = ViewModelProvider(requireActivity())[FlightViewModel::class.java]
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
            }else{
                Toast.makeText(context, "No History Found !", Toast.LENGTH_SHORT).show()
            }
        }
        viewModel.callBookingApi(token)
    }

}