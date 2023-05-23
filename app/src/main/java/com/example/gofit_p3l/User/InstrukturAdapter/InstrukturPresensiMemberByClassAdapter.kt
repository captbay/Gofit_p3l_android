package com.example.gofit_p3l.User.InstrukturAdapter

import android.view.LayoutInflater
import android.view.ViewGroup
import androidx.recyclerview.widget.RecyclerView
import com.example.awesomedialog.AwesomeDialog
import com.example.awesomedialog.icon
import com.example.awesomedialog.onNegative
import com.example.awesomedialog.onPositive
import com.example.awesomedialog.position
import com.example.awesomedialog.title
import com.example.gofit_p3l.PresensiMemberByClassActivity
import com.example.gofit_p3l.R
import com.example.gofit_p3l.User.InstrukturModels.PresensiMemberByClass
import com.example.gofit_p3l.databinding.RvItemPresensiMemberPerClassBinding

class InstrukturPresensiMemberByClassAdapter (
    val data: Array<PresensiMemberByClass>,
    val listener: OnPresensiMemberClickListener
) :
    RecyclerView.Adapter<InstrukturPresensiMemberByClassAdapter.ViewHolder>() {

    interface OnPresensiMemberClickListener {
        fun onUpdateStatusPresensiMember(position: Int)
    }

    override fun onCreateViewHolder(parent: ViewGroup, viewType: Int): InstrukturPresensiMemberByClassAdapter.ViewHolder {
        val binding = RvItemPresensiMemberPerClassBinding.inflate(
            LayoutInflater.from(parent.context),
            parent,
            false
        )
        return ViewHolder(binding)
    }

    override fun onBindViewHolder(holder: InstrukturPresensiMemberByClassAdapter.ViewHolder, position: Int) {
        val presensiMember = data[position]
        holder.bind(presensiMember)

//        holder.itemView.setOnClickListener {
//            listener?.onBookingClassCancel(position)
//        }
    }

    override fun getItemCount(): Int {
        return data.size
    }

    inner class ViewHolder(private val binding: RvItemPresensiMemberPerClassBinding) :
        RecyclerView.ViewHolder(binding.root) {

        private val context = binding.root.context as PresensiMemberByClassActivity

        fun bind(presensiMemberByClass: PresensiMemberByClass) {
            binding.apply {
                // Set data to the views using view binding
                binding.tvNoCLassBookingMember.text = presensiMemberByClass.no_class_booking
                binding.tvMetodeBayar.text = presensiMemberByClass.metode_pembayaran
                binding.tvNamaMember.text = presensiMemberByClass.member_name
                binding.tvStatusPresensi.text =  presensiMemberByClass.status_presensi

                binding.btnPresensi.setOnClickListener {
                    val position = adapterPosition
                    if (position != RecyclerView.NO_POSITION) {
                        // Invoke the callback method
                        AwesomeDialog.build(context)
                            .title("Are You Sure?")
                            .icon(R.drawable.icon_gofit)
                            .onNegative("No") {

                            }
                            .onPositive("Yes") {
                                listener.onUpdateStatusPresensiMember(position)
                            }
                            .position(AwesomeDialog.POSITIONS.CENTER)
                            .show()
                    }
                }
            }
        }
    }
}