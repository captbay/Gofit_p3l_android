package com.example.gofit_p3l.Instruktur

import android.content.Context
import android.content.Intent
import android.content.SharedPreferences
import android.os.Bundle
import androidx.fragment.app.Fragment
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import com.example.gofit_p3l.HomeInstrukturActivity
import com.example.gofit_p3l.databinding.ActivityHomeInstrukturBinding
import com.example.gofit_p3l.databinding.FragmentHomeInstrukturBinding

class HomeInstrukturFragment : Fragment() {
    private val myPreference = "myPref"
    private val usernamePref = "usernameKey"
    private val fullnamePref = "fullnameKey"

    var sharedPreferences: SharedPreferences? = null
    private var _binding: FragmentHomeInstrukturBinding? = null
    private val binding get() = _binding!!
    private lateinit var bindingHome: ActivityHomeInstrukturBinding


    override fun onCreateView(
        inflater: LayoutInflater, container: ViewGroup?,
        savedInstanceState: Bundle?
    ): View {
        // Inflate the layout for this fragment
        _binding = FragmentHomeInstrukturBinding.inflate(inflater, container, false)
        return binding.root
    }

    override fun onViewCreated(view: View, savedInstanceState: Bundle?) {
        super.onViewCreated(view, savedInstanceState)
        bindingHome = ActivityHomeInstrukturBinding.inflate(layoutInflater)
        sharedPreferences = activity?.getSharedPreferences(myPreference, Context.MODE_PRIVATE)
        val intent = Intent(activity, HomeInstrukturActivity::class.java)

        binding.nomor.text = sharedPreferences!!.getString(usernamePref,"")
        binding.fullname.text = sharedPreferences!!.getString(fullnamePref,"")

        binding.btnBookingGym.setOnClickListener {
            val bundle = Bundle()
            bundle.putString("key", "pindahBookingGym")
            intent.putExtra("keyBundle", bundle)
            startActivity(intent)
            activity?.finish()
        }

        binding.btnBookingClass.setOnClickListener {
            val bundle = Bundle()
            bundle.putString("key", "pindahBookingClass")
            intent.putExtra("keyBundle", bundle)
            startActivity(intent)
            activity?.finish()
        }

        binding.btnProfile.setOnClickListener {
            val bundle = Bundle()
            bundle.putString("key", "pindahProfile")
            intent.putExtra("keyBundle", bundle)
            startActivity(intent)
            activity?.finish()


        }


    }
}