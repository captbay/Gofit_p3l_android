package com.example.gofit_p3l.User.InstrukturAdapter

import android.view.LayoutInflater
import android.view.ViewGroup
import androidx.recyclerview.widget.RecyclerView
import com.example.gofit_p3l.User.InstrukturModels.InstrukturActivities
import com.example.gofit_p3l.databinding.RvItemHistoryActivityInstrukturBinding

class InstrukturHistoryActivityAdapter (
    val data: Array<InstrukturActivities>
) :
    RecyclerView.Adapter<InstrukturHistoryActivityAdapter.ViewHolder>() {

    override fun onCreateViewHolder(parent: ViewGroup, viewType: Int): InstrukturHistoryActivityAdapter.ViewHolder {
        val binding = RvItemHistoryActivityInstrukturBinding.inflate(
            LayoutInflater.from(parent.context),
            parent,
            false
        )
        return ViewHolder(binding)
    }

    override fun onBindViewHolder(holder: InstrukturHistoryActivityAdapter.ViewHolder, position: Int) {
        val history = data[position]
        holder.bind(history)

//        holder.itemView.setOnClickListener {
//            listener?.onBookingGymCancel(position)
//        }
    }

    override fun getItemCount(): Int {
        return data.size
    }

    inner class ViewHolder(private val binding: RvItemHistoryActivityInstrukturBinding) :
        RecyclerView.ViewHolder(binding.root) {

        fun bind(instrukturActivities: InstrukturActivities) {
            binding.apply {
                // Set data to the views using view binding
                binding.tvNamaActivity.text = instrukturActivities.namaActivity
                binding.tvDescriptionActivity.text = instrukturActivities.descriptionActivity
                binding.tvDateTimeInstruktur.text = instrukturActivities.dateTime
            }
        }
    }
}