package com.example.gofit_p3l.User.InstrukturAdapter

import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import androidx.recyclerview.widget.RecyclerView
import com.example.gofit_p3l.HomeInstrukturActivity
import com.example.gofit_p3l.User.InstrukturModels.PresensiMember
import com.example.gofit_p3l.databinding.RvItemPresensiMemberBinding

class InstrukturPresensiMemberAdapter (
    val data: Array<PresensiMember>,
    val listener: OnPresensiMemberClickListener
) :
    RecyclerView.Adapter<InstrukturPresensiMemberAdapter.ViewHolder>() {

    interface OnPresensiMemberClickListener {
        fun onPresensiMemberPresensi(position: Int)
    }

    override fun onCreateViewHolder(parent: ViewGroup, viewType: Int): InstrukturPresensiMemberAdapter.ViewHolder {
        val binding = RvItemPresensiMemberBinding.inflate(
            LayoutInflater.from(parent.context),
            parent,
            false
        )
        return ViewHolder(binding)
    }

    override fun onBindViewHolder(holder: InstrukturPresensiMemberAdapter.ViewHolder, position: Int) {
        val presensiMember = data[position]
        holder.bind(presensiMember)

//        holder.itemView.setOnClickListener {
//            listener?.onBookingClassCancel(position)
//        }
    }

    override fun getItemCount(): Int {
        return data.size
    }

    inner class ViewHolder(private val binding: RvItemPresensiMemberBinding) :
        RecyclerView.ViewHolder(binding.root) {

//        private val context = binding.root.context as HomeInstrukturActivity

        fun bind(presensiMember: PresensiMember) {
            binding.apply {
                // Set data to the views using view binding
                binding.tvNamaKelasRunning.text = presensiMember.nama_class
                binding.tvJamKelasRunning.text = presensiMember.startEnd_class
                binding.tvHariKelas.text = presensiMember.day_name
                binding.tvTanggalKelasRunning.text =  presensiMember.date

                if(presensiMember.statusClassRunning == ""){
                    // Hide the status card view
                    binding.statusClassRunning.visibility = View.GONE
                }else{
                    binding.tvStatusClassRunning.text = presensiMember.statusClassRunning
                }

                if (presensiMember.statusPresensi == "1"){
                    binding.tvPresensiStatus.text = "Anda Sudah Dipresensi"
                }else{
                    binding.tvPresensiStatus.text = "Anda Belum Dipresensi"
                }

                binding.btnPresensi.setOnClickListener {
                    val position = adapterPosition
                    if (position != RecyclerView.NO_POSITION) {
                        // Invoke the callback method
                        listener.onPresensiMemberPresensi(position)
                    }
                }
            }
        }
    }
}