package com.binar.c5team.gotravel.view.fragment

import android.content.Context
import android.content.SharedPreferences
import android.os.Bundle
import androidx.fragment.app.Fragment
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import androidx.lifecycle.ViewModelProvider
import androidx.navigation.Navigation
import androidx.recyclerview.widget.LinearLayoutManager
import com.binar.c5team.gotravel.R
import com.binar.c5team.gotravel.databinding.FragmentNotificationBinding
import com.binar.c5team.gotravel.model.Notification
import com.binar.c5team.gotravel.view.adapter.NotificationAdapter
import com.binar.c5team.gotravel.viewmodel.FlightViewModel
import com.google.android.material.bottomnavigation.BottomNavigationView
import com.google.android.material.transition.MaterialSharedAxis
import java.util.ArrayList

class NotificationFragment : Fragment() {
    lateinit var binding : FragmentNotificationBinding
    private lateinit var adapter : NotificationAdapter

    //shared preferences
    lateinit var sharedPref: SharedPreferences

    //user token
    private var token: String = ""
    private var userId: Int = 0

    //viewmodel
    private lateinit var viewModel : FlightViewModel

    //progressbar
    var progressView: ViewGroup? = null
    private var isProgressShowing = false

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
        binding = FragmentNotificationBinding.inflate(layoutInflater, container, false)
        return binding.root
    }

    override fun onViewCreated(view: View, savedInstanceState: Bundle?) {
        super.onViewCreated(view, savedInstanceState)

        val navBar = requireActivity().findViewById<BottomNavigationView>(R.id.bottom_nav)
        navBar.visibility = View.GONE
        val guestNavBar = requireActivity().findViewById<BottomNavigationView>(R.id.guest_bottom_nav)
        guestNavBar.visibility = View.GONE

        //SharedPref for user data
        sharedPref = requireActivity().getSharedPreferences("data", Context.MODE_PRIVATE)

        //getting user data
        token = sharedPref.getString("token", "").toString()
        userId = sharedPref.getInt("userId", 0)

        viewModel.loading.observe(viewLifecycleOwner) {
            when (it) {
                true -> showProgressingView()
                false -> hideProgressingView()
            }
        }

        binding.notificationArrowBack.setOnClickListener {
            Navigation.findNavController(view).popBackStack()
        }

        getNotification(token)

    }

    private fun getNotification(token: String) {
        viewModel.getNotificationData().observe(viewLifecycleOwner){
            if (it != null){
                binding.rvNotification.layoutManager = LinearLayoutManager(
                    context, LinearLayoutManager.VERTICAL, false
                )

                val filterNotification: MutableList<Notification> = ArrayList()
                filterNotification.clear()
                for(i in it.data.notifications){
                    if (i.idUser == userId){
                        filterNotification.add(i)
                    }
                }

                adapter = NotificationAdapter(filterNotification)
                binding.rvNotification.adapter = adapter
            }
        }
        viewModel.callNotificationApi(token)
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