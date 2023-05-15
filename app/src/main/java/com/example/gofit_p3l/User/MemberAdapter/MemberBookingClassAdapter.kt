package com.example.gofit_p3l.User.MemberAdapter

import android.util.Log
import android.view.LayoutInflater
import android.view.ViewGroup
import androidx.recyclerview.widget.RecyclerView
import com.example.awesomedialog.*
import com.example.gofit_p3l.HomeMemberActivity
import com.example.gofit_p3l.User.MemberModels.BookingClass
import com.example.gofit_p3l.databinding.RvItemClassBookingBinding
import com.example.gofit_p3l.R

class MemberBookingClassAdapter (
    val data: Array<BookingClass>,
    val listener: OnBookingClassClickListener
) :
    RecyclerView.Adapter<MemberBookingClassAdapter.ViewHolder>() {

    interface OnBookingClassClickListener {
        fun onBookingClassCancel(position: Int)
    }

//    // Create a variable to hold the listener
//    private var listener: OnBookingClassClickListener? = null
//
//    // Setter method to set the listener
//    fun setOnBookingClassClickListener(listener: OnBookingClassClickListener) {
//        this.listener = listener
//    }

    override fun onCreateViewHolder(parent: ViewGroup, viewType: Int): ViewHolder {
        val binding = RvItemClassBookingBinding.inflate(
            LayoutInflater.from(parent.context),
            parent,
            false
        )
        return ViewHolder(binding)
    }

    override fun onBindViewHolder(holder: ViewHolder, position: Int) {
        val instrukturIzin = data[position]
        holder.bind(instrukturIzin)

        holder.itemView.setOnClickListener {
            listener?.onBookingClassCancel(position)
        }
    }

    override fun getItemCount(): Int {
        return data.size
    }

    inner class ViewHolder(private val binding: RvItemClassBookingBinding) :
        RecyclerView.ViewHolder(binding.root) {

        private val context = binding.root.context as HomeMemberActivity

        fun bind(bookingClass: BookingClass) {
            binding.btnCancle.setOnClickListener {
                val position = adapterPosition
                if (position != RecyclerView.NO_POSITION) {
                    listener.onBookingClassCancel(position)
                }
            }
            binding.apply {
                // Set data to the views using view binding
                binding.tvNoClassBooking.text = bookingClass.no_class_booking
                binding.tvNamaClass.text = bookingClass.nama_class
                binding.tvTanggalWaktuClass.text = bookingClass.dateTime

                binding.btnCancle.setOnClickListener {
                    val position = adapterPosition
                    if (position != RecyclerView.NO_POSITION) {
                        val canceledBookingClass = data[position]
                        // Invoke the callback method
                        AwesomeDialog.build(context)
                            .title("Are You Sure?")
                            .icon(R.drawable.icon_gofit)
                            .onNegative("No") {

                            }
                            .onPositive("Yes") {
                                Log.d("DeleteClassBooking", position.toString())
                                listener.onBookingClassCancel(position)
                            }
                            .position(AwesomeDialog.POSITIONS.CENTER)
                            .show()
                    }
                }
            }
        }
    }
}