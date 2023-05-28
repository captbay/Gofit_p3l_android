package com.example.gofit_p3l

import android.content.Context
import androidx.appcompat.app.AppCompatActivity
import android.os.Bundle
import androidx.fragment.app.Fragment
import com.example.gofit_p3l.Instruktur.HistoryActivityInstrukturFragment
import com.example.gofit_p3l.Instruktur.HomeInstrukturFragment
import com.example.gofit_p3l.Instruktur.IzinInstrukturFragment
import com.example.gofit_p3l.Instruktur.PresensiMemberFragment
import com.example.gofit_p3l.Instruktur.ProfileInstrukturFragment
import com.example.gofit_p3l.databinding.ActivityHomeInstrukturBinding

class HomeInstrukturActivity : AppCompatActivity() {
    private val myPreference = "myPref"
    private lateinit var binding: ActivityHomeInstrukturBinding

    override fun onCreate(savedInstanceState: Bundle?) {
        supportActionBar?.hide()
        super.onCreate(savedInstanceState)
        binding = ActivityHomeInstrukturBinding.inflate(layoutInflater)
        setContentView(binding.root)

//        instrukturlogin
        val navViewInstruktur  = binding.bottomNavigationInstruktur
        navViewInstruktur.itemActiveIndicatorColor = getColorStateList(R.color.white)

//        get role
        val sharedPreference =  getSharedPreferences(myPreference, Context.MODE_PRIVATE)
        sharedPreference.getString("roleKey","defaultRole")

        val bundle = intent.getBundleExtra("keyBundle")
        if(intent.getBundleExtra("keyBundle")!=null){
            if(bundle?.getString("key","")=="pindahPresensiMember"){
                navViewInstruktur.selectedItemId = R.id.presensiMember
                changeFragment(PresensiMemberFragment())
            }else if(bundle?.getString("key","")=="pindahIzinInstruktur"){
                navViewInstruktur.selectedItemId = R.id.izinInstruktur
                changeFragment(IzinInstrukturFragment())
            }else if(bundle?.getString("key","")=="pindahHistoryActivityInstruktur"){
                navViewInstruktur.selectedItemId = R.id.historyActivityInstruktur
                changeFragment(HistoryActivityInstrukturFragment())
            }else if(bundle?.getString("key","")=="pindahProfile"){
                navViewInstruktur.selectedItemId = R.id.profileInstruktur
                changeFragment(ProfileInstrukturFragment())
            }else{
                navViewInstruktur.selectedItemId = R.id.homeInstruktur
                changeFragment(HomeInstrukturFragment())
            }
        }else{
            changeFragment(HomeInstrukturFragment())
        }

        navViewInstruktur.setOnItemSelectedListener { item ->
            when(item.itemId) {
                R.id.homeInstruktur -> {
                    changeFragment(HomeInstrukturFragment())
                    true
                }
                R.id.presensiMember -> {
                    changeFragment(PresensiMemberFragment())
                    true
                }
                R.id.izinInstruktur -> {

                    changeFragment(IzinInstrukturFragment())
                    true
                }
                R.id.historyActivityInstruktur -> {
                    changeFragment(HistoryActivityInstrukturFragment())
                    true
                }
                R.id.profileInstruktur ->{

                    changeFragment(ProfileInstrukturFragment())
                    true
                }
                else -> false
            }
        }
    }

    fun changeFragment(fragment: Fragment?){
        if(fragment!=null) {
            supportFragmentManager.beginTransaction().replace(R.id.layout_fragment_instruktur, fragment).commit()
        }
    }
}