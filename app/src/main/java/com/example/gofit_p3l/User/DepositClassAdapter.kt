package com.example.gofit_p3l.User

import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import androidx.recyclerview.widget.RecyclerView
import com.example.gofit_p3l.databinding.RvItemDepositClassBinding

class DepositClassAdapter (
    val data: Array<DepositClass>
) :
    RecyclerView.Adapter<DepositClassAdapter.ViewHolder>() {

    override fun onCreateViewHolder(parent: ViewGroup, viewType: Int): DepositClassAdapter.ViewHolder {
        val binding = RvItemDepositClassBinding.inflate(
            LayoutInflater.from(parent.context),
            parent,
            false
        )
        return ViewHolder(binding)
    }

    override fun onBindViewHolder(holder: DepositClassAdapter.ViewHolder, position: Int) {
        val depositClass = data[position]
        holder.bind(depositClass)
    }

    override fun getItemCount(): Int {
        return data.size
    }

    inner class ViewHolder(private val binding: RvItemDepositClassBinding) :
        RecyclerView.ViewHolder(binding.root) {

        fun bind(depositClass: DepositClass) {
            binding.apply {
                // Set data to the views using view binding
                binding.tvNamaClass.text = depositClass.nama_class
                binding.tvExpiredDate.text = depositClass.expired_date
                binding.tvPackageAmount.text = "Total Paket: "+depositClass.package_amount
            }
        }
    }
}