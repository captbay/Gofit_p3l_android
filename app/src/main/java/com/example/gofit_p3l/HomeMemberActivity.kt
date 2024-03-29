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

    override fun onCreate(savedInstanceState: Bundle?) {
        supportActionBar?.hide()
        super.onCreate(savedInstanceState)
        binding = ActivityHomeMemberBinding.inflate(layoutInflater)
        setContentView(binding.root)

//        memberlogin
        val navViewMember  = binding.bottomNavigationMember
        navViewMember.itemActiveIndicatorColor = getColorStateList(R.color.white)

//        get role
        val sharedPreference =  getSharedPreferences(myPreference,Context.MODE_PRIVATE)
        sharedPreference.getString("roleKey","")

        val bundle = intent.getBundleExtra("keyBundle")
        if(intent.getBundleExtra("keyBundle")!=null){
            if(bundle?.getString("key","")=="pindahBookingGym"){
                navViewMember.selectedItemId = R.id.bookingGym
                changeFragment(BookingGymFragment())
            }else if(bundle?.getString("key","")=="pindahBookingClass"){
                navViewMember.selectedItemId = R.id.bookingClass
                changeFragment(BookingClassFragment())
            }else {
                navViewMember.selectedItemId = R.id.profileMember
                changeFragment(ProfileMemberFragment())
            }

        }else{
            changeFragment(HomeMemberFragment())
        }

        navViewMember.setOnItemSelectedListener { item ->
            when(item.itemId) {
                R.id.homeMember -> {
                    changeFragment(HomeMemberFragment())
                    true
                }
                R.id.bookingGym -> {
                    changeFragment(BookingGymFragment())
                    true
                }
                R.id.bookingClass -> {

                    changeFragment(BookingClassFragment())
                    true
                }
                R.id.historyActivityMember -> {
                    changeFragment(HistoryActivityMemberFragment())
                    true
                }
                R.id.profileMember ->{

                    changeFragment(ProfileMemberFragment())
                    true
                }
                else -> false
            }
        }
    }

    private fun changeFragment(fragment: Fragment?){
        if(fragment != null) {
            supportFragmentManager.beginTransaction().replace(R.id.layout_fragment_member, fragment).commit()
        }
    }
}