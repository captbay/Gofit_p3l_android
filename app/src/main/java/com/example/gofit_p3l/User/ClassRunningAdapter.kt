package com.example.gofit_p3l.User

import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import androidx.recyclerview.widget.RecyclerView
import com.example.gofit_p3l.databinding.RvItemJadwalHarianBinding

class ClassRunningAdapter (
    val data: Array<ClassRunning>
) :
    RecyclerView.Adapter<ClassRunningAdapter.ViewHolder>() {

    override fun onCreateViewHolder(parent: ViewGroup, viewType: Int): ClassRunningAdapter.ViewHolder {
        val binding = RvItemJadwalHarianBinding.inflate(
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

    inner class ViewHolder(private val binding: RvItemJadwalHarianBinding) :
        RecyclerView.ViewHolder(binding.root) {

        fun bind(classRunning: ClassRunning) {
            binding.apply {
                // Set data to the views using view binding
                binding.tvHariKelas.text = classRunning.day_name
                binding.tvTanggalKelasSchedule.text = classRunning.date
                binding.tvJamMulaiKelasSchedule.text = classRunning.start_class
                binding.tvNamaKelasSchedule.text = classRunning.nama_class
                binding.tvNamaInstrukturSchedule.text = classRunning.nama_instruktur
                binding.tvPriceClassRunning.text = "Rp." + classRunning.price
                if(classRunning.status == ""){
                    // Hide the status card view
                    statusSchedule.visibility = View.GONE
                }else{
                    binding.tvStatusSchedule.text = classRunning.status
                }
            }
        }
    }

}