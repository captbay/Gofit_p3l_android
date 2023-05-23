package com.example.gofit_p3l.User.MemberAdapter

import android.util.Log
import android.view.LayoutInflater
import android.view.ViewGroup
import androidx.recyclerview.widget.RecyclerView
import com.example.awesomedialog.AwesomeDialog
import com.example.awesomedialog.icon
import com.example.awesomedialog.onNegative
import com.example.awesomedialog.onPositive
import com.example.awesomedialog.position
import com.example.awesomedialog.title
import com.example.gofit_p3l.HomeMemberActivity
import com.example.gofit_p3l.R
import com.example.gofit_p3l.User.MemberModels.BookingClass
import com.example.gofit_p3l.User.MemberModels.BookingGym
import com.example.gofit_p3l.databinding.RvItemClassBookingBinding
import com.example.gofit_p3l.databinding.RvItemGymBookingBinding


class MemberBookingGymAdapter (
    val data: Array<BookingGym>,
    val listener: OnBookingGymClickListener
) :
    RecyclerView.Adapter<MemberBookingGymAdapter.ViewHolder>() {

    interface OnBookingGymClickListener {
        fun onBookingGymCancel(position: Int)
//        fun onEdit()
    }

    override fun onCreateViewHolder(parent: ViewGroup, viewType: Int): ViewHolder {
        val binding = RvItemGymBookingBinding.inflate(
            LayoutInflater.from(parent.context),
            parent,
            false
        )
        return ViewHolder(binding)
    }

    override fun onBindViewHolder(holder: ViewHolder, position: Int) {
        val bookingGym = data[position]
        holder.bind(bookingGym)

//        holder.itemView.setOnClickListener {
//            listener?.onBookingGymCancel(position)
//        }
    }

    override fun getItemCount(): Int {
        return data.size
    }

    inner class ViewHolder(private val binding: RvItemGymBookingBinding) :
        RecyclerView.ViewHolder(binding.root) {

        private val context = binding.root.context as HomeMemberActivity

        fun bind(bookingGym: BookingGym) {
//            binding.btnCancle.setOnClickListener {
//                val position = adapterPosition
//                if (position != RecyclerView.NO_POSITION) {
//                    listener.onBookingGymCancel(position)
//                }
//            }
            binding.apply {
                // Set data to the views using view binding
                binding.tvNoGymBooking.text = bookingGym.no_gym_booking
                binding.tvNamaGym.text = bookingGym.nama_gym
                binding.tvTanggalWaktuGym.text = bookingGym.dateTime

                binding.btnCancle.setOnClickListener {
                    val position = adapterPosition
                    if (position != RecyclerView.NO_POSITION) {
                        // Invoke the callback method
                        AwesomeDialog.build(context)
                            .title("Are You Sure?")
                            .icon(R.drawable.icon_gofit)
                            .onNegative("No") {

                            }
                            .onPositive("Yes") {
                                Log.d("DeleteGymBooking", position.toString())
                                listener.onBookingGymCancel(position)
                            }
                            .position(AwesomeDialog.POSITIONS.CENTER)
                            .show()
                    }
                }
            }
        }
    }

}