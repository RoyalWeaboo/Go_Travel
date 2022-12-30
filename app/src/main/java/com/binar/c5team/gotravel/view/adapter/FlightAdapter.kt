package com.binar.c5team.gotravel.view.adapter

import android.annotation.SuppressLint
import android.view.LayoutInflater
import android.view.ViewGroup
import androidx.recyclerview.widget.RecyclerView
import com.binar.c5team.gotravel.databinding.ItemFlightBinding
import com.binar.c5team.gotravel.model.Flight

class FlightAdapter (private var listFlight : List<Flight>): RecyclerView.Adapter<FlightAdapter.ViewHolder>() {
    var onOrderClick : ((Flight)->Unit)? = null
    var onWishlistClick : ((Flight)->Unit)? = null

    class ViewHolder(var binding : ItemFlightBinding): RecyclerView.ViewHolder(binding.root)

    override fun onCreateViewHolder(parent: ViewGroup, viewType: Int): ViewHolder {
        val view = ItemFlightBinding.inflate(LayoutInflater.from(parent.context), parent, false)
        return  ViewHolder(view)
    }

    @SuppressLint("SetTextI18n")
    override fun onBindViewHolder(holder: ViewHolder, position: Int) {
        //setting time
        val dt = listFlight[position].departureTime
        val at = listFlight[position].arrivalTime

        val kelas: String = when (listFlight[position].kelas) {
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
                listFlight[position].kelas
            }
        }

        holder.binding.planeClass.text = kelas
        holder.binding.timeFrom.text = dt
        holder.binding.timeTo.text = at
        holder.binding.planeName.text = listFlight[position].plane.name
        holder.binding.price.text = "Rp."+ listFlight[position].price.toString()
        holder.binding.codeFrom.text = listFlight[position].fromAirport.code
        holder.binding.codeTo.text = listFlight[position].toAirport.code
        holder.binding.cardFlight.setOnClickListener {
            onOrderClick?.invoke(listFlight[position])
        }
        holder.binding.addWishlist.setOnClickListener {
            onWishlistClick?.invoke(listFlight[position])
        }

    }

    override fun getItemCount(): Int {
        return listFlight.size
    }
}