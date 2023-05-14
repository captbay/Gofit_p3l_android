package com.example.gofit_p3l.User.InstrukturAdapter

import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import android.widget.TextView
import androidx.recyclerview.widget.RecyclerView
import com.example.gofit_p3l.R
import com.example.gofit_p3l.User.InstrukturModels.InstrukturIzin
import com.example.gofit_p3l.databinding.FragmentIzinInstrukturBinding
import com.example.gofit_p3l.databinding.RvItemInstrukturIzinBinding

class InstrukturIzinAdapter(private val data: Array<InstrukturIzin>) :
    RecyclerView.Adapter<InstrukturIzinAdapter.ViewHolder>() {

    override fun onCreateViewHolder(parent: ViewGroup, viewType: Int): ViewHolder {
        val binding = RvItemInstrukturIzinBinding.inflate(
            LayoutInflater.from(parent.context),
            parent,
            false
        )
        return ViewHolder(binding)
    }

    override fun onBindViewHolder(holder: ViewHolder, position: Int) {
        val instrukturIzin = data[position]
        holder.bind(instrukturIzin)
    }

    override fun getItemCount(): Int {
        return data.size
    }

    inner class ViewHolder(private val binding: RvItemInstrukturIzinBinding) :
        RecyclerView.ViewHolder(binding.root) {

        fun bind(instrukturIzin: InstrukturIzin) {
            binding.apply {
                // Set data to the views using view binding
                binding.tvNamaInstruktur.text = instrukturIzin.nama_instruktur
                binding.tvNamaClassRunnning.text = instrukturIzin.nama_class_runnning
                binding.tvNamaInstrukturPengganti.text = instrukturIzin.nama_instruktur_pengganti
                binding.tvAlasan.text = instrukturIzin.alasan
                binding.tvIsConfirm.text = if (instrukturIzin.is_confirm == 0) {
                    "Belum"
                } else {
                    "Sudah"
                }
                binding.tvDate.text = instrukturIzin.date
                // Set any other views as needed
            }
        }
    }
}