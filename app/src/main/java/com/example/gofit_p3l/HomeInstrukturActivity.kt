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
    var left = 0
    var right = 0
    var temp = 0

    override fun onCreate(savedInstanceState: Bundle?) {
        getSupportActionBar()?.hide()
        super.onCreate(savedInstanceState)
        binding = ActivityHomeInstrukturBinding.inflate(layoutInflater)
        setContentView(binding.root)

        if(temp==0){
            left = binding.layoutFragmentInstruktur.paddingLeft
            right = binding.layoutFragmentInstruktur.paddingRight
        }

//        instrukturlogin
        val navViewInstruktur  = binding.bottomNavigationInstruktur
        navViewInstruktur.itemActiveIndicatorColor = getColorStateList(R.color.white)

//        get role
        val sharedPreference =  getSharedPreferences(myPreference, Context.MODE_PRIVATE)
        sharedPreference.getString("roleKey","defaultRole")

//        val bundle = intent.getBundleExtra("keyBundle")
//        if(intent.getBundleExtra("keyBundle")!=null){
//            binding.layoutFragmentInstruktur.setPadding(0,0,0,0)
//            if(bundle?.getString("key","")=="pindahBookingGym"){
//                binding.layoutFragmentInstruktur.setPadding(left,0,right,0)
//                navViewInstruktur.selectedItemId = R.id.bookingGym
//                changeFragment(BookingGymFragment())
//            }else if(bundle?.getString("key","")=="pindahBookingClass"){
//                binding.layoutFragmentInstruktur.setPadding(left,0,right,0)
//                navViewInstruktur.selectedItemId = R.id.bookingClass
//                changeFragment(BookingClassFragment())
//            }else {
//                navViewInstruktur.selectedItemId = R.id.bookingClass
//                changeFragment(BookingClassFragment())
//            }
//        }else{
//            changeFragment(HomeInstrukturFragment())
//        }
        changeFragment(HomeInstrukturFragment())

        navViewInstruktur.setOnItemSelectedListener { item ->
            when(item.itemId) {
                R.id.homeInstruktur -> {
                    changeFragment(HomeInstrukturFragment())
                    binding.layoutFragmentInstruktur.setPadding(left,0,right,0)
                    true
                }
                R.id.presensiMember -> {
                    changeFragment(PresensiMemberFragment())
                    binding.layoutFragmentInstruktur.setPadding(left,0,right,0)
                    true
                }
                R.id.izinInstruktur -> {

                    changeFragment(IzinInstrukturFragment())
                    binding.layoutFragmentInstruktur.setPadding(0,0,0,0)
                    true
                }
                R.id.historyActivityInstruktur -> {
                    changeFragment(HistoryActivityInstrukturFragment())
                    binding.layoutFragmentInstruktur.setPadding(0,0,0,0)
                    true
                }
                R.id.profileInstruktur ->{

                    changeFragment(ProfileInstrukturFragment())
                    binding.layoutFragmentInstruktur.setPadding(0,0,0,0)
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