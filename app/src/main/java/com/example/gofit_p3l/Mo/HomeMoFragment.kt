package com.example.gofit_p3l.Mo

import android.content.Context
import android.content.Intent
import android.content.SharedPreferences
import android.os.Bundle
import androidx.fragment.app.Fragment
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import com.example.gofit_p3l.HomeMoActivity
import com.example.gofit_p3l.databinding.ActivityHomeMoBinding
import com.example.gofit_p3l.databinding.FragmentHomeMoBinding

class HomeMoFragment : Fragment() {
    private val myPreference = "myPref"
    private val usernamePref = "usernameKey"
    private val fullnamePref = "fullnameKey"

    var sharedPreferences: SharedPreferences? = null
    private var _binding: FragmentHomeMoBinding? = null
    private val binding get() = _binding!!
    private lateinit var bindingHome: ActivityHomeMoBinding


    override fun onCreateView(
        inflater: LayoutInflater, container: ViewGroup?,
        savedInstanceState: Bundle?
    ): View {
        // Inflate the layout for this fragment
        _binding = FragmentHomeMoBinding.inflate(inflater, container, false)
        return binding.root
    }


    override fun onViewCreated(view: View, savedInstanceState: Bundle?) {
        super.onViewCreated(view, savedInstanceState)
        bindingHome = ActivityHomeMoBinding.inflate(layoutInflater)
        sharedPreferences = activity?.getSharedPreferences(myPreference, Context.MODE_PRIVATE)
        val intent = Intent(activity, HomeMoActivity::class.java)

        val inputString = sharedPreferences!!.getString(fullnamePref,"").toString()
        val words = inputString.split(" ") // Memisahkan string menjadi array kata-kata

        val numberOfWordsToShow = 3 // Jumlah kata yang ingin ditampilkan
        val selectedWords = words.take(numberOfWordsToShow) // Mengambil kata-kata yang akan ditampilkan

        binding.fullname.text =selectedWords.joinToString(" ")


    }
}