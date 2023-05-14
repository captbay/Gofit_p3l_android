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
    var left = 0
    var right = 0
    var temp = 0

    override fun onCreate(savedInstanceState: Bundle?) {
        supportActionBar?.hide()
        super.onCreate(savedInstanceState)
        binding = ActivityHomeMoBinding.inflate(layoutInflater)
        setContentView(binding.root)

        if(temp==0){
            left = binding.layoutFragmentMo.paddingLeft
            right = binding.layoutFragmentMo.paddingRight
        }

//        mologin
        val navViewMo  = binding.bottomNavigationMo
        navViewMo.itemActiveIndicatorColor = getColorStateList(R.color.white)

//        get role
        val sharedPreference =  getSharedPreferences(myPreference, Context.MODE_PRIVATE)
        sharedPreference.getString("roleKey","defaultRole")

        val bundle = intent.getBundleExtra("keyBundle")
        if(intent.getBundleExtra("keyBundle")!=null){
            binding.layoutFragmentMo.setPadding(0,0,0,0)
            if(bundle?.getString("key","")=="pindahPresensiInstruktur"){
                binding.layoutFragmentMo.setPadding(left,0,right,0)
                navViewMo.selectedItemId = R.id.presensiInstruktur
                changeFragment(PresensiInstrukturFragment())
            }else if(bundle?.getString("key","")=="pindahProfile"){
                binding.layoutFragmentMo.setPadding(0,0,0,0)
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
                    binding.layoutFragmentMo.setPadding(left,0,right,0)
                    true
                }
                R.id.presensiInstruktur -> {
                    changeFragment(PresensiInstrukturFragment())
                    binding.layoutFragmentMo.setPadding(left,0,right,0)
                    true
                }
                R.id.profileMo ->{

                    changeFragment(ProfileMoFragment())
                    binding.layoutFragmentMo.setPadding(0,0,0,0)
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