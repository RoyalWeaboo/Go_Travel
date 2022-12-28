package com.binar.c5team.gotravel.view.fragment

import android.app.AlertDialog
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
import androidx.navigation.fragment.findNavController
import androidx.recyclerview.widget.LinearLayoutManager
import com.binar.c5team.gotravel.R
import com.binar.c5team.gotravel.databinding.FragmentPaymentBinding
import com.binar.c5team.gotravel.model.Booking
import com.binar.c5team.gotravel.view.adapter.PaymentAdapter
import com.binar.c5team.gotravel.viewmodel.FlightViewModel
import com.google.android.material.bottomnavigation.BottomNavigationView

class PaymentFragment : Fragment() {
    lateinit var binding: FragmentPaymentBinding

    private var bookingIds = ArrayList<Int>()

    //Shared Preferences
    private lateinit var sharedPref: SharedPreferences
    private lateinit var sharedPrefFlight: SharedPreferences
    private lateinit var sharedPrefBooking: SharedPreferences

    private lateinit var adapter: PaymentAdapter

    private var token: String = ""

    private var seatCount: Int = 0
    private var flightPrice: Int = 0
    private var roundFlightPrice: Int = 0
    private var flightMode: String = ""

    //progressbar
    var progressView: ViewGroup? = null
    private var isProgressShowing = false

    //viewmodel
    private lateinit var viewModel: FlightViewModel

    override fun onCreateView(
        inflater: LayoutInflater, container: ViewGroup?,
        savedInstanceState: Bundle?
    ): View {
        viewModel = ViewModelProvider(this)[FlightViewModel::class.java]

        binding = FragmentPaymentBinding.inflate(inflater, container, false)
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

        bookingIds = arguments?.getIntegerArrayList("bookingIds") as ArrayList<Int>

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

        //setting navbar
        val navBar = requireActivity().findViewById<BottomNavigationView>(R.id.bottom_nav)
        navBar.visibility = View.GONE
        val guestNavBar =
            requireActivity().findViewById<BottomNavigationView>(R.id.guest_bottom_nav)
        guestNavBar.visibility = View.GONE

        //getting flight and booking data
        seatCount = sharedPrefBooking.getInt("totalSeat", 0)
        flightPrice = sharedPrefBooking.getInt("flightPrice", 0)
        roundFlightPrice = sharedPrefBooking.getInt("roundFlightPrice", 0)
        flightMode = sharedPrefFlight.getString("flightMode", "").toString()

        //setting flight and booking data
        if (flightMode == "oneWay") {
            binding.tvQuantity.text = seatCount.toString()
            binding.tvTicketCost.text = "Rp. " + flightPrice.toString()
            val departureTotal = seatCount * flightPrice
            binding.totalPay.text = "Rp. " + departureTotal.toString()

        } else if (flightMode == "roundTrip") {
            binding.tvQuantity.text = seatCount.toString()
            binding.tvTicketCost.text = "Rp. " + flightPrice.toString()

            binding.textView3return.visibility = View.VISIBLE
            binding.textView4return.visibility = View.VISIBLE
            binding.tvQuantityreturn.visibility = View.VISIBLE
            binding.tvTicketCostReturn.visibility = View.VISIBLE

            binding.tvQuantityreturn.text = seatCount.toString()
            binding.tvTicketCostReturn.text = "Rp. " + roundFlightPrice.toString()
            val departureTotal = seatCount * flightPrice
            val returnTotal = seatCount * roundFlightPrice
            val roundTotal = departureTotal + returnTotal

            binding.totalPay.text = "Rp. " + roundTotal.toString()
        }

        getLoadingLD()

        getBookingByIds(token)

        //alert dialog for payment instruction
        val builder = AlertDialog.Builder(context)
        builder.setTitle("Payment Instruction")
        builder.setIcon(R.drawable.ic_baseline_payment_24)
        builder.setMessage("Please send the fee as stated to the bank account that is already listed.")

        builder.setPositiveButton("Confirm") { _, _ ->
            //do nothing
        }
        builder.show()

        binding.btnPay.setOnClickListener {
            val builderPay = AlertDialog.Builder(context)
            builderPay.setTitle("Uploading Payment Proof")
            builderPay.setMessage("Please send a picture of proof of payment for admin verification")

            builderPay.setPositiveButton("Upload Proof") { _, _ ->
                val bundle = Bundle()
                bundle.putIntegerArrayList("bookingIds", bookingIds)

                Navigation.findNavController(view).navigate(R.id.action_paymentFragment_to_paymentDialog, bundle)
            }
            builderPay.show()
        }

        binding.btnBack.setOnClickListener {
            val builderBack = AlertDialog.Builder(context)
            builderBack.setTitle("Cancel Uploading Payment Proof")
            builderBack.setMessage("Cancel sending payment proof ? you can continue to pay via the history menu before the ticket is forfeited")

            builderBack.setPositiveButton("Confirm") { _, _ ->
                Navigation.findNavController(view).navigate(R.id.action_paymentFragment_to_historyFragment)
            }
            builderBack.show()
        }

    }

    private fun getLoadingLD() {
        viewModel.loading.observe(viewLifecycleOwner) {
            when (it) {
                true -> showProgressingView()
                false -> hideProgressingView()
            }
        }
    }

    private fun getBookingByIds(token: String) {
        val viewModel = ViewModelProvider(this)[FlightViewModel::class.java]
        viewModel.getBookingLD().observe(viewLifecycleOwner) {
            if (it != null) {
                binding.rvPenumpang.layoutManager = LinearLayoutManager(
                    context, LinearLayoutManager.VERTICAL, false
                )
                val filterBookingByID: MutableList<Booking> = ArrayList()
                if (filterBookingByID.isEmpty()) {
                    val idCount = bookingIds.size
                    var idCountTemp = 0
                    while (idCountTemp < idCount) {
                        for (element in it.data.bookings)
                            if (element.id == bookingIds[idCountTemp]) {
                                filterBookingByID.add(element)
                            }
                        idCountTemp++
                    }
                }
                adapter = PaymentAdapter(filterBookingByID)
                binding.rvPenumpang.adapter = adapter
            } else {
                Toast.makeText(context, "No Data Found !", Toast.LENGTH_SHORT).show()
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