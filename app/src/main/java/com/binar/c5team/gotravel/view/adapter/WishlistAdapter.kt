package com.binar.c5team.gotravel.view.adapter

import android.view.LayoutInflater
import android.view.ViewGroup
import androidx.recyclerview.widget.RecyclerView
import com.binar.c5team.gotravel.databinding.ItemWishlistBinding
import com.binar.c5team.gotravel.model.Whislists

class WishlistAdapter (private var wishlist : List<Whislists>): RecyclerView.Adapter<WishlistAdapter.ViewHolder>() {
    var onOrderClick : ((Whislists)->Unit)? = null
    var onDeleteClick : ((Int)->Unit)? = null

    class ViewHolder(var binding : ItemWishlistBinding): RecyclerView.ViewHolder(binding.root)

    override fun onCreateViewHolder(parent: ViewGroup, viewType: Int): ViewHolder {
        val view = ItemWishlistBinding.inflate(LayoutInflater.from(parent.context), parent, false)
        return  ViewHolder(view)
    }

    override fun onBindViewHolder(holder: ViewHolder, position: Int) {
        //setting time
        val dt = wishlist[position].flight.departureTime
        val at = wishlist[position].flight.arrivalTime

        holder.binding.planeClass.text = wishlist[position].flight.kelas
        holder.binding.timeFrom.text = dt
        holder.binding.timeTo.text = at
        holder.binding.planeName.text = wishlist[position].flight.plane.name
        holder.binding.price.text = "Rp."+ wishlist[position].flight.price.toString()
        holder.binding.btnOrder.setOnClickListener {
            onOrderClick?.invoke(wishlist[position])
        }

        holder.binding.delWishlist.setOnClickListener {
            onDeleteClick?.invoke(wishlist[position].id)
        }

    }

    override fun getItemCount(): Int {
        return wishlist.size
    }
}