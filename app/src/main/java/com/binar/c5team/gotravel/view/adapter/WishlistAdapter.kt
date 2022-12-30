package com.binar.c5team.gotravel.view.adapter

import android.annotation.SuppressLint
import android.view.LayoutInflater
import android.view.ViewGroup
import androidx.recyclerview.widget.RecyclerView
import com.binar.c5team.gotravel.databinding.ItemWishlistBinding
import com.binar.c5team.gotravel.model.Whislists
import java.text.SimpleDateFormat
import java.util.*

class WishlistAdapter (private var wishlist : List<Whislists>): RecyclerView.Adapter<WishlistAdapter.ViewHolder>() {
    var onOrderClick : ((Whislists)->Unit)? = null
    var onDeleteClick : ((Int)->Unit)? = null

    class ViewHolder(var binding : ItemWishlistBinding): RecyclerView.ViewHolder(binding.root)

    override fun onCreateViewHolder(parent: ViewGroup, viewType: Int): ViewHolder {
        val view = ItemWishlistBinding.inflate(LayoutInflater.from(parent.context), parent, false)
        return  ViewHolder(view)
    }

    @SuppressLint("SetTextI18n")
    override fun onBindViewHolder(holder: ViewHolder, position: Int) {
        //setting time
        val dt = wishlist[position].flight.departureTime
        val at = wishlist[position].flight.arrivalTime

        val kelas: String = when (wishlist[position].flight.kelas) {
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
                wishlist[position].flight.kelas
            }
        }

        //setting date
        val date = wishlist[position].flight.flightDate //string
        val flightDateSdf = SimpleDateFormat("yyyy-MM-dd", Locale.getDefault())
        val xflightDateSdf = SimpleDateFormat("MMM dd, yyyy", Locale.getDefault())
        val xflightDate = flightDateSdf.parse(date) //to date
        val formattedflightDate =
            xflightDate?.let { xflightDateSdf.format(it) }//to string with second format

        holder.binding.date.text = formattedflightDate

        holder.binding.planeClass.text = kelas
        holder.binding.timeFrom.text = dt
        holder.binding.timeTo.text = at
        holder.binding.planeName.text = wishlist[position].flight.plane.name
        holder.binding.price.text = "Rp."+ wishlist[position].flight.price.toString()
        holder.binding.cardWishlist.setOnClickListener {
            onOrderClick?.invoke(wishlist[position])
        }

        holder.binding.deleteWishlist.setOnClickListener {
            onDeleteClick?.invoke(wishlist[position].id)
        }

        holder.binding.tvDelete.setOnClickListener {
            onDeleteClick?.invoke(wishlist[position].id)
        }

    }

    override fun getItemCount(): Int {
        return wishlist.size
    }
}