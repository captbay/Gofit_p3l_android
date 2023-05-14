package com.example.gofit_p3l

import android.content.Context
import androidx.appcompat.app.AppCompatActivity
import android.os.Bundle
import androidx.fragment.app.Fragment
import com.example.gofit_p3l.Member.BookingClassFragment
import com.example.gofit_p3l.Member.BookingGymFragment
import com.example.gofit_p3l.Member.HistoryActivityMemberFragment
import com.example.gofit_p3l.Member.HomeMemberFragment
import com.example.gofit_p3l.Member.ProfileMemberFragment
import com.example.gofit_p3l.databinding.ActivityHomeMemberBinding

class HomeMemberActivity : AppCompatActivity() {
//    var sharedPreferences: SharedPreferences? = null
    private val myPreference = "myPref"
    private lateinit var binding: ActivityHomeMemberBinding
    var left = 0
    var right = 0
    var temp = 0

    override fun onCreate(savedInstanceState: Bundle?) {
        getSupportActionBar()?.hide()
        super.onCreate(savedInstanceState)
        binding = ActivityHomeMemberBinding.inflate(layoutInflater)
        setContentView(binding.root)

        if(temp==0){
            left = binding.layoutFragmentMember.paddingLeft
            right = binding.layoutFragmentMember.paddingRight
        }

//        memberlogin
        val navViewMember  = binding.bottomNavigationMember
        navViewMember.itemActiveIndicatorColor = getColorStateList(R.color.white)

//        get role
        val sharedPreference =  getSharedPreferences(myPreference,Context.MODE_PRIVATE)
        sharedPreference.getString("roleKey","")

        val bundle = intent.getBundleExtra("keyBundle")
        if(intent.getBundleExtra("keyBundle")!=null){
            binding.layoutFragmentMember.setPadding(0,0,0,0)
            if(bundle?.getString("key","")=="pindahBookingGym"){
                binding.layoutFragmentMember.setPadding(left,0,right,0)
                navViewMember.selectedItemId = R.id.bookingGym
                changeFragment(BookingGymFragment())
            }else if(bundle?.getString("key","")=="pindahBookingClass"){
                binding.layoutFragmentMember.setPadding(left,0,right,0)
                navViewMember.selectedItemId = R.id.bookingClass
                changeFragment(BookingClassFragment())
            }else {
                navViewMember.selectedItemId = R.id.bookingClass
                changeFragment(BookingClassFragment())
            }

        }else{
            changeFragment(HomeMemberFragment())
        }

        navViewMember.setOnItemSelectedListener { item ->
            when(item.itemId) {
                R.id.homeMember -> {
                    changeFragment(HomeMemberFragment())
                    binding.layoutFragmentMember.setPadding(left,0,right,0)
                    true
                }
                R.id.bookingGym -> {
                    changeFragment(BookingGymFragment())
                    binding.layoutFragmentMember.setPadding(left,0,right,0)
                    true
                }
                R.id.bookingClass -> {

                    changeFragment(BookingClassFragment())
                    binding.layoutFragmentMember.setPadding(0,0,0,0)
                    true
                }
                R.id.historyActivityMember -> {
                    changeFragment(HistoryActivityMemberFragment())
                    binding.layoutFragmentMember.setPadding(0,0,0,0)
                    true
                }
                R.id.profileMember ->{

                    changeFragment(ProfileMemberFragment())
                    binding.layoutFragmentMember.setPadding(0,0,0,0)
                    true
                }
                else -> false
            }
        }
    }

    private fun changeFragment(fragment: Fragment?){
        if(fragment!=null) {
            supportFragmentManager.beginTransaction().replace(R.id.layout_fragment_member, fragment).commit()
        }
    }
}