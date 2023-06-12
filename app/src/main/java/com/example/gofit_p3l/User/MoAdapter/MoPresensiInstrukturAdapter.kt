package com.example.gofit_p3l.User.MoAdapter

import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import androidx.recyclerview.widget.RecyclerView
import com.example.awesomedialog.AwesomeDialog
import com.example.awesomedialog.icon
import com.example.awesomedialog.onNegative
import com.example.awesomedialog.onPositive
import com.example.awesomedialog.position
import com.example.awesomedialog.title
import com.example.gofit_p3l.HomeMoActivity
import com.example.gofit_p3l.R
import com.example.gofit_p3l.User.MoModels.PresensiInstruktur
import com.example.gofit_p3l.databinding.RvItemPresensiInstrukturBinding

class MoPresensiInstrukturAdapter (
    val data: Array<PresensiInstruktur>,
    val listener: OnPresensiInstrukturClickListener
) :
    RecyclerView.Adapter<MoPresensiInstrukturAdapter.ViewHolder>() {

    interface OnPresensiInstrukturClickListener {
        fun onPresensiInstrukturUpdateStartClass(position: Int)
        fun onPresensiInstrukturUpdateEndClass(position: Int)
    }

    override fun onCreateViewHolder(parent: ViewGroup, viewType: Int): MoPresensiInstrukturAdapter.ViewHolder {
        val binding = RvItemPresensiInstrukturBinding.inflate(
            LayoutInflater.from(parent.context),
            parent,
            false
        )
        return ViewHolder(binding)
    }

    override fun onBindViewHolder(holder: MoPresensiInstrukturAdapter.ViewHolder, position: Int) {
        val presensiInstruktur = data[position]
        holder.bind(presensiInstruktur)

//        holder.itemView.setOnClickListener {
//            listener?.onBookingClassCancel(position)
//        }
    }

    override fun getItemCount(): Int {
        return data.size
    }

    inner class ViewHolder(private val binding: RvItemPresensiInstrukturBinding) :
        RecyclerView.ViewHolder(binding.root) {

        private val context = binding.root.context as HomeMoActivity

        fun bind(presensiInstruktur: PresensiInstruktur) {
            binding.apply {
                // Set data to the views using view binding
                binding.tvNamaKelasRunning.text = presensiInstruktur.nama_class
                binding.tvJamKelasRunning.text = presensiInstruktur.startEnd_class
                binding.tvHariKelas.text = presensiInstruktur.day_name

                binding.tvNamaInstrukturRunning.text = presensiInstruktur.nama_instruktur
                binding.tvTanggalKelasRunning.text =  presensiInstruktur.date

                if(presensiInstruktur.status == "" || presensiInstruktur.status == "null"){
                    // Hide the status card view
                    binding.statusSchedule.visibility = View.GONE
                }else{
                    binding.statusSchedule.visibility = View.VISIBLE
                    binding.tvStatusSchedule.text = presensiInstruktur.status
                }

                if(presensiInstruktur.statusClassRunning == ""){
                    // Hide the status card view
                    binding.statusClassRunning.visibility = View.GONE
                }else{
                    binding.statusClassRunning.visibility = View.VISIBLE
                    binding.tvStatusClassRunning.text = presensiInstruktur.statusClassRunning
                }

                binding.btnUpdateStartClass.setOnClickListener {
                    val position = adapterPosition
                    if (position != RecyclerView.NO_POSITION) {
                        // Invoke the callback method
                        AwesomeDialog.build(context)
                            .title("Are You Sure?")
                            .icon(R.drawable.icon_gofit)
                            .onNegative("No") {

                            }
                            .onPositive("Yes") {
                                listener.onPresensiInstrukturUpdateStartClass(position)
                            }
                            .position(AwesomeDialog.POSITIONS.CENTER)
                            .show()
                    }
                }

                binding.btnUpdateEndClass.setOnClickListener {
                    val position = adapterPosition
                    if (position != RecyclerView.NO_POSITION) {
                        // Invoke the callback method
                        AwesomeDialog.build(context)
                            .title("Are You Sure?")
                            .icon(R.drawable.icon_gofit)
                            .onNegative("No") {

                            }
                            .onPositive("Yes") {
                                listener.onPresensiInstrukturUpdateEndClass(position)
                            }
                            .position(AwesomeDialog.POSITIONS.CENTER)
                            .show()
                    }
                }
            }
        }
    }
}