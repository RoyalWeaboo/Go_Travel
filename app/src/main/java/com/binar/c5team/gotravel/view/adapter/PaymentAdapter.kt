package com.binar.c5team.gotravel.view.adapter

import android.annotation.SuppressLint
import android.view.LayoutInflater
import android.view.ViewGroup
import androidx.recyclerview.widget.RecyclerView
import com.binar.c5team.gotravel.databinding.ItemDetailPenumpangBinding
import com.binar.c5team.gotravel.model.Booking
import java.text.SimpleDateFormat
import java.util.*

class PaymentAdapter (private var listBooking : List<Booking>): RecyclerView.Adapter<PaymentAdapter.ViewHolder>() {

    class ViewHolder(var binding: ItemDetailPenumpangBinding) :
        RecyclerView.ViewHolder(binding.root)

    override fun onCreateViewHolder(parent: ViewGroup, viewType: Int): ViewHolder {
        val view =
            ItemDetailPenumpangBinding.inflate(LayoutInflater.from(parent.context), parent, false)
        return ViewHolder(view)
    }

    @SuppressLint("SetTextI18n")
    override fun onBindViewHolder(holder: ViewHolder, position: Int) {
        holder.binding.tvDate.text = listBooking[position].bookingDate.toString()

        //counting flight time
        val flightTime = listBooking[position].flight.departureTime
        val flightArrivalTime = listBooking[position].flight.arrivalTime

        val timeFormat = SimpleDateFormat("HH:mm", Locale.getDefault())

        val timeDepart = flightTime.let { timeFormat.parse(it) }
        val timeArrival = flightArrivalTime.let { timeFormat.parse(it) }

        val timeDepartString = timeDepart?.let { timeFormat.format(it) }
        val timeArrivalString = timeArrival?.let { timeFormat.format(it) }

        val diff = timeDepart!!.time - timeArrival!!.time
        val timeCount = "( ${(diff / (1000 * 60 * 60) * -1)} Hours ${(diff % (1000 * 60 * 60) * -1)} Minutes )"
        holder.binding.tvTime.text = "$timeDepartString - $timeArrivalString$timeCount"

        holder.binding.tvTrip.text = listBooking[position].flight.fromAirport.city + "-" + listBooking[position].flight.toAirport.city
        holder.binding.tvPassenger.text = listBooking[position].name
        holder.binding.tvPhoneNumber.text = listBooking[position].mobilephone
        holder.binding.tvPassengerCount.text = "Passenger "+ (position+1).toString()
        holder.binding.tvBaggage.text = listBooking[position].baggage.toString()+"Kg"

        val foodStatus = listBooking[position].food
        if (foodStatus){
            holder.binding.tvFood.text = "Yes"
        }else{
            holder.binding.tvFood.text = "No"
        }
    }

    override fun getItemCount(): Int {
        return listBooking.size
    }

}