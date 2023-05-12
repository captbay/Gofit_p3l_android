package com.example.gofit_p3l

import android.content.Context
import androidx.appcompat.app.AppCompatActivity
import android.os.Bundle
import android.widget.Button
import androidx.fragment.app.Fragment
import com.example.gofit_p3l.Fragment.HomeFragment
import com.example.gofit_p3l.databinding.ActivityHomeBinding

class HomeActivity : AppCompatActivity() {
    private val myPreference = "myPref"
    private lateinit var binding: ActivityHomeBinding
    private lateinit var btnLogout: Button
    var left = 0
    var right = 0
    var temp = 0

    override fun onCreate(savedInstanceState: Bundle?) {
        getSupportActionBar()?.hide()
        super.onCreate(savedInstanceState)
        binding = ActivityHomeBinding.inflate(layoutInflater)
        setContentView(binding.root)

        if(temp==0){
            left = binding.layoutFragment.paddingLeft
            right = binding.layoutFragment.paddingRight
        }

//        memberlogin
//        val navViewMember  = binding.bottomNavigationMember
//        navViewMember.itemActiveIndicatorColor = getColorStateList(R.color.white)

//        get role
        val sharedPreference =  getSharedPreferences(myPreference,Context.MODE_PRIVATE)
        sharedPreference.getString("roleKey","defaultRole")

//        val bundle = intent.getBundleExtra("keyBundle")
//        if(intent.getBundleExtra("keyBundle")!=null){
//            binding.layoutFragment.setPadding(0,0,0,0)
//            if(bundle?.getString("key","")=="pindahLowongan"){
//                binding.layoutFragment.setPadding(left,0,right,0)
//                navViewMember.selectedItemId = R.id.bookingGym
////                changeFragment(FragmentLowongan())
//            }else {
//                navViewMember.selectedItemId = R.id.profile
////                changeFragment(ProfileFragment())
//            }
//
//        }else{
//            changeFragment(HomeFragment())
//        }

//        navViewMember.setOnItemSelectedListener { item ->
//            when(item.itemId) {
//                R.id.home -> {
//                    changeFragment(HomeFragment())
//                    binding.layoutFragment.setPadding(left,0,right,0)
//                    true
//                }
//                R.id.bookingGym -> {
////                    changeFragment(FragmentLowongan())
//                    binding.layoutFragment.setPadding(left,0,right,0)
//                    true
//                }
//                R.id.bookingClass -> {
//
////                    changeFragment(FavoriteFragment())
//                    binding.layoutFragment.setPadding(0,0,0,0)
//                    true
//                }
//                R.id.historyActivityMember -> {
////                    changeFragment(ProfileFragment())
//                    binding.layoutFragment.setPadding(0,0,0,0)
//                    true
//                }
//                R.id.profileMember ->{
//
////                    changeFragment(SkillFragment())
//                    binding.layoutFragment.setPadding(0,0,0,0)
//                    true
//                }
//                else -> false
//            }
//        }
    }

    fun changeFragment(fragment: Fragment?){
        if(fragment!=null) {
            getSupportFragmentManager().beginTransaction().replace(R.id.layout_fragment, fragment).commit()
        }
    }
}