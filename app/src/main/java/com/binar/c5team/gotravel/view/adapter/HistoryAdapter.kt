package com.binar.c5team.gotravel.view.adapter

import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import androidx.recyclerview.widget.RecyclerView
import com.binar.c5team.gotravel.databinding.ItemHistoryBinding
import com.binar.c5team.gotravel.model.Booking
import java.text.SimpleDateFormat
import java.util.*

class HistoryAdapter(private var listBooking: List<Booking>) :
    RecyclerView.Adapter<HistoryAdapter.ViewHolder>() {
    var onTicketClick: ((Booking) -> Unit)? = null
    var onStatusClick: ((Booking) -> Unit)? = null

    class ViewHolder(var binding: ItemHistoryBinding) : RecyclerView.ViewHolder(binding.root)

    override fun onCreateViewHolder(parent: ViewGroup, viewType: Int): ViewHolder {
        val view = ItemHistoryBinding.inflate(LayoutInflater.from(parent.context), parent, false)
        return ViewHolder(view)
    }

    override fun onBindViewHolder(holder: ViewHolder, position: Int) {
        //getting booking date and time
        val bookingDate = listBooking[position].bookingDate.toString()
        val flightTime = listBooking[position].flight.departureTime
        val flightArrivalTime = listBooking[position].flight.arrivalTime

        //simple date format
        val bDate1 = SimpleDateFormat("YYYY-MM-dd", Locale.getDefault())
        val bDate2 = SimpleDateFormat("MMM dd, YYYY", Locale.getDefault())
        val bDateinDate = bDate1.parse(bookingDate)
        val bDateFormatted = bDate2.format(bDateinDate)

        val bookingYear = SimpleDateFormat("YYYY", Locale.getDefault())
        val bookingMonth = SimpleDateFormat("MM", Locale.getDefault())
        val bookingDay = SimpleDateFormat("dd", Locale.getDefault())
        val bookingHour = SimpleDateFormat("HH", Locale.getDefault())
        val bookingMinute = SimpleDateFormat("mm", Locale.getDefault())
        val bookYear = bookingYear.parse(bookingDate)
        val bookMonth = bookingMonth.parse(bookingDate)
        val bookDay = bookingDay.parse(bookingDate)
        val bookHour = bookingHour.parse(flightTime)
        val bookMinute = bookingMinute.parse(flightTime)
        val bookHourArrival = bookingHour.parse(flightArrivalTime)
        val bookMinuteArrival = bookingMinute.parse(flightArrivalTime)

        val bookYearInt = bookYear?.let { bookingYear.format(it) }!!.toInt()
        val bookMonthInt = bookMonth?.let { bookingMonth.format(it) }!!.toInt()
        val bookDayInt = bookDay?.let { bookingDay.format(it) }!!.toInt()
        val bookHourInt = bookHour?.let { bookingHour.format(it) }!!.toInt()
        val bookMinuteInt = bookMinute?.let { bookingMinute.format(it) }!!.toInt()
        val bookHourArrivalInt = bookHourArrival?.let { bookingHour.format(it) }!!.toInt()
        val bookMinuteArrivalInt = bookMinuteArrival?.let { bookingMinute.format(it) }!!.toInt()

        //getting today`s date
        val currDate = Calendar.getInstance().time
        val year = SimpleDateFormat("YYYY", Locale.getDefault())
        val month = SimpleDateFormat("MM", Locale.getDefault())
        val day = SimpleDateFormat("dd", Locale.getDefault())
        val hour = SimpleDateFormat("HH", Locale.getDefault())
        val minute = SimpleDateFormat("mm", Locale.getDefault())
        val todaysYear = year.format(currDate).toInt()
        val todaysMonth = month.format(currDate).toInt()
        val todaysDay = day.format(currDate).toInt()
        val todaysHour = hour.format(currDate).toInt()
        val todaysMinute = minute.format(currDate).toInt()

        //first of all, check whether the ticket has been paid or not
        //if its not paid yet
        if (listBooking[position].confirmation.isNullOrEmpty()) {
            holder.binding.cvStatusNotPaid.visibility = View.VISIBLE
            holder.binding.cvStatusNotPaid.setOnClickListener {
                onStatusClick?.invoke(listBooking[position])
            }
        }
        //if its paid
        else {
            holder.binding.cvStatusNotPaid.setOnClickListener {
                //do nothing
            }
            holder.binding.cardHistory.setOnClickListener {
                onTicketClick?.invoke(listBooking[position])
            }
            //check if its approved yet
            if (listBooking[position].approved) {
                //check the time to change status between active, on board, or boarded
                //check year first
                if (bookYearInt > todaysYear) {
                    //then check the month
                    if (bookMonthInt >= todaysMonth) {
                        //then check the day
                        if (bookDayInt >= todaysDay) {
                            //then check the hour
                            if (bookHourInt <= todaysHour) {
                                ///finally check the minute
                                //check with range more than or less than
                                if (bookMinuteInt < todaysMinute) {
                                    holder.binding.cvStatusActive.visibility = View.VISIBLE
                                } else {
                                    holder.binding.cvStatusBoarded.visibility = View.VISIBLE
                                }
                                //now if its within the ticket departure and arrival time
                            } else if (todaysHour in bookHourArrivalInt..bookHourInt) {
                                if (todaysMinute in bookMinuteArrivalInt..bookMinuteInt) {
                                    holder.binding.cvStatusOnBoard.visibility = View.VISIBLE
                                }
                            } else {
                                //if the flight is on after today's day
                                holder.binding.cvStatusBoarded.visibility = View.VISIBLE
                            }
                        } else {
                            //if the flight is on after today's month
                            holder.binding.cvStatusBoarded.visibility = View.VISIBLE
                        }
                    } else {
                        holder.binding.cvStatusBoarded.visibility = View.VISIBLE
                    }
                } else {
                    holder.binding.cvStatusBoarded.visibility = View.VISIBLE
                }
            } else {
                holder.binding.cvStatusWaiting.visibility = View.VISIBLE
            }
        }


        //setting time
        val dt = listBooking[position].flight.departureTime
        val at = listBooking[position].flight.arrivalTime

        holder.binding.planeName.text = listBooking[position].flight.plane.name
        holder.binding.codeText.text = "Code : "+ listBooking[position].id.toString()
        holder.binding.timeFrom.text = dt
        holder.binding.timeTo.text = at
        holder.binding.date.text = bDateFormatted
        holder.binding.cardHistory.setOnClickListener {
            onTicketClick?.invoke(listBooking[position])
        }

    }

    override fun getItemCount(): Int {
        return listBooking.size
    }
}