package com.example.gofit_p3l.User.MemberAdapter

import android.view.LayoutInflater
import android.view.ViewGroup
import androidx.recyclerview.widget.RecyclerView
import com.example.gofit_p3l.User.MemberModels.MemberActivities
import com.example.gofit_p3l.databinding.RvItemHistoryActivityMemberBinding


class MemberHistoryActivityAdapter (
    val data: Array<MemberActivities>
) :
    RecyclerView.Adapter<MemberHistoryActivityAdapter.ViewHolder>() {

    override fun onCreateViewHolder(parent: ViewGroup, viewType: Int): MemberHistoryActivityAdapter.ViewHolder {
        val binding = RvItemHistoryActivityMemberBinding.inflate(
            LayoutInflater.from(parent.context),
            parent,
            false
        )
        return ViewHolder(binding)
    }

    override fun onBindViewHolder(holder: MemberHistoryActivityAdapter.ViewHolder, position: Int) {
        val history = data[position]
        holder.bind(history)

//        holder.itemView.setOnClickListener {
//            listener?.onBookingGymCancel(position)
//        }
    }

    override fun getItemCount(): Int {
        return data.size
    }

    inner class ViewHolder(private val binding: RvItemHistoryActivityMemberBinding) :
        RecyclerView.ViewHolder(binding.root) {

        fun bind(memberActivities: MemberActivities) {
            binding.apply {
                // Set data to the views using view binding
                binding.tvNamaActivity.text = memberActivities.namaActivity
                binding.tvDateTime.text = memberActivities.dateTime
                binding.tvNoAktivitas.text = memberActivities.noAktivitas
                binding.tvUangAtauPaket.text = memberActivities.valueActivity
            }
        }
    }
}