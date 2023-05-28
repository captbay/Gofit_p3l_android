package com.example.gofit_p3l

import android.content.Context
import androidx.appcompat.app.AppCompatActivity
import android.os.Bundle
import androidx.fragment.app.Fragment
import com.example.gofit_p3l.Mo.HomeMoFragment
import com.example.gofit_p3l.Mo.PresensiInstrukturFragment
import com.example.gofit_p3l.Mo.ProfileMoFragment
import com.example.gofit_p3l.databinding.ActivityHomeMoBinding

class HomeMoActivity : AppCompatActivity() {
    private val myPreference = "myPref"
    private lateinit var binding: ActivityHomeMoBinding

    override fun onCreate(savedInstanceState: Bundle?) {
        supportActionBar?.hide()
        super.onCreate(savedInstanceState)
        binding = ActivityHomeMoBinding.inflate(layoutInflater)
        setContentView(binding.root)

//        mologin
        val navViewMo  = binding.bottomNavigationMo
        navViewMo.itemActiveIndicatorColor = getColorStateList(R.color.white)

//        get role
        val sharedPreference =  getSharedPreferences(myPreference, Context.MODE_PRIVATE)
        sharedPreference.getString("roleKey","defaultRole")

        val bundle = intent.getBundleExtra("keyBundle")
        if(intent.getBundleExtra("keyBundle")!=null){
            if(bundle?.getString("key","")=="pindahPresensiInstruktur"){
                navViewMo.selectedItemId = R.id.presensiInstruktur
                changeFragment(PresensiInstrukturFragment())
            }else if(bundle?.getString("key","")=="pindahProfile"){
                navViewMo.selectedItemId = R.id.profileMo
                changeFragment(ProfileMoFragment())
            }else {
                navViewMo.selectedItemId = R.id.homeMo
                changeFragment(HomeMoFragment())
            }

        }else{
            changeFragment(HomeMoFragment())
        }

        navViewMo.setOnItemSelectedListener { item ->
            when(item.itemId) {
                R.id.homeMo -> {
                    changeFragment(HomeMoFragment())
                    true
                }
                R.id.presensiInstruktur -> {
                    changeFragment(PresensiInstrukturFragment())
                    true
                }
                R.id.profileMo ->{

                    changeFragment(ProfileMoFragment())
                    true
                }
                else -> false
            }
        }
    }

    fun changeFragment(fragment: Fragment?){
        if(fragment!=null) {
            supportFragmentManager.beginTransaction().replace(R.id.layout_fragment_mo, fragment).commit()
        }
    }
}