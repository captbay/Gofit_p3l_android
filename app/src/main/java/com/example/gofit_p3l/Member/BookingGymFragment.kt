package com.example.gofit_p3l.Member

import android.annotation.SuppressLint
import android.content.Context
import android.content.Intent
import android.content.SharedPreferences
import android.os.Bundle
import android.util.Log
import androidx.fragment.app.Fragment
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import android.widget.Toast
import androidx.recyclerview.widget.LinearLayoutManager
import androidx.recyclerview.widget.RecyclerView
import com.android.volley.AuthFailureError
import com.android.volley.RequestQueue
import com.android.volley.Response
import com.android.volley.toolbox.StringRequest
import com.android.volley.toolbox.Volley
import com.example.gofit_p3l.AddBookingGymActivity
import com.example.gofit_p3l.Api.Api
import com.example.gofit_p3l.HomeMemberActivity
import com.example.gofit_p3l.R
import com.example.gofit_p3l.User.MemberAdapter.MemberBookingGymAdapter
import com.example.gofit_p3l.User.MemberModels.BookingGym
import com.example.gofit_p3l.databinding.FragmentBookingGymBinding
import com.google.gson.Gson
import org.json.JSONException
import org.json.JSONObject
import java.nio.charset.StandardCharsets
import java.text.SimpleDateFormat
import java.util.Locale

class BookingGymFragment : Fragment() {
    //buat cookies
    private val myPreference = "myPref"
    private val idPref = "idKey"
    private val usernamePref = "usernameKey"
    private val idMemberPref = "idMemberKey"
    private val idInstrukturPref = "idInstrukturKey"
    private val idMoPref = "idMoKey"
    private val fullnamePref = "fullnameKey"
    private val rolePref = "roleKey"
    private val tokenTypePref = "token_typeKey"
    private val accessTokenPref = "access_tokenKey"
    var sharedPreferences: SharedPreferences? = null

    //untuk naruh di header setap connect ke back end soalnya udah login
    private var username: String = ""
    private var tokenType: String = ""
    private var accessToken: String = ""
    private var idMember: Int = 0

    //buat request
    private var queue: RequestQueue? = null

    private var _binding: FragmentBookingGymBinding? = null
    private val binding get() = _binding!!

    private lateinit var adapter: MemberBookingGymAdapter

    override fun onCreateView(
        inflater: LayoutInflater, container: ViewGroup?,
        savedInstanceState: Bundle?
    ): View? {
        _binding = FragmentBookingGymBinding.inflate(inflater, container, false)
        val view = binding.root
        return view
    }

    override fun onViewCreated(view: View, savedInstanceState: Bundle?) {
        super.onViewCreated(view, savedInstanceState)
        //attach this fragment to activity
        val activity = activity as HomeMemberActivity


        queue = Volley.newRequestQueue(requireActivity())
        sharedPreferences = activity.getSharedPreferences(myPreference, Context.MODE_PRIVATE)
        tokenType = sharedPreferences!!.getString(tokenTypePref,"").toString()
        accessToken = sharedPreferences!!.getString(accessTokenPref,"").toString()
        username = sharedPreferences!!.getString(usernamePref, "").toString()
        idMember = sharedPreferences!!.getInt(idMemberPref,0)

        //ambil data
        displayBookingGym(emptyArray())
        getGymBooking(idMember)

        //kalo refresh ulang lagi nampilin
        binding.refreshRvItem.setOnRefreshListener { getGymBooking(idMember)}

        binding.btnAdd.setOnClickListener {
            val moveAddBookingGym= Intent(activity, AddBookingGymActivity::class.java)
            startActivity(moveAddBookingGym)
            activity?.finish()
        }

    }

    private fun getGymBooking(idMember: Int){
        binding.refreshRvItem.isRefreshing = true

        val stringRequest : StringRequest = object:
            StringRequest(Method.GET, Api.GET_GYM_BOOKING_BY_ID_MEMBER_URL+ idMember.toString(), Response.Listener { response ->
                try {

                    val jsonObject = JSONObject(response)
                    val jsonArrayData = jsonObject.getJSONArray("data")

                    if(jsonArrayData.length() == 0){
                        Toast.makeText(requireActivity(), "TIDAK ADA DATA", Toast.LENGTH_SHORT).show()
                        displayBookingGym(emptyArray())
                        binding.refreshRvItem.isRefreshing = false
                    }else{
                        val gymBookingList = mutableListOf<BookingGym>()

                        for (i in 0 until jsonArrayData.length()) {
                            val jsonData = jsonArrayData.getJSONObject(i)

                            val id = jsonData.getInt("id")
                            val no_gym_booking = jsonData.getString("no_gym_booking")
                            val start_gym = convertTimeTo12HourFormat(jsonData.getJSONObject("gym").getString("start_gym"))
                            val end_gym = convertTimeTo12HourFormat(jsonData.getJSONObject("gym").getString("end_gym"))
                            val nama_gym = "$start_gym - $end_gym"
                            val date = jsonData.getString("date_booking")

                            val gymBooking = BookingGym(
                                id,
                                no_gym_booking,
                                nama_gym,
                                date,
                            )

                            gymBookingList.add(gymBooking)
                        }

                        displayBookingGym(gymBookingList.toTypedArray())
                        binding.refreshRvItem.isRefreshing = false
                    }

                } catch (e: JSONException) {
                    binding.refreshRvItem.isRefreshing = false
                    Toast.makeText(requireActivity(), "Error parsing JSON", Toast.LENGTH_SHORT).show()
                }
            }, Response.ErrorListener { error ->
                binding.refreshRvItem.isRefreshing = false

                try {
                    val responseBody =
                        String(error.networkResponse.data, StandardCharsets.UTF_8)
                    val errors = JSONObject(responseBody)
                    Toast.makeText(requireActivity(), errors.getString("message"), Toast.LENGTH_SHORT).show()
                } catch (e: Exception){
                    Toast.makeText(requireActivity(), e.message, Toast.LENGTH_SHORT).show()
                }
            }) {
            @Throws(AuthFailureError::class)
            override fun getHeaders(): Map<String, String> {
                val headers = HashMap<String, String>()
                headers["Accept"] = "application/json"
                headers["Authorization"] = "$tokenType $accessToken"
                return headers
            }

        }
        queue!!.add(stringRequest)
    }

    private fun convertTimeTo12HourFormat(time: String): String {
        val inputFormat = SimpleDateFormat("HH:mm:ss", Locale.getDefault())
        val outputFormat = SimpleDateFormat("hh:mm a", Locale.getDefault())
        val date = inputFormat.parse(time)
        return outputFormat.format(date)
    }

    @SuppressLint("NotifyDataSetChanged")
    private fun displayBookingGym(data: Array<BookingGym>) {
        adapter = MemberBookingGymAdapter(data, object : MemberBookingGymAdapter.OnBookingGymClickListener {
            override fun onBookingGymCancel(position: Int) {
                bookingGymCancel(position)
            }
        })
        binding.rvGymBooking.layoutManager = LinearLayoutManager(activity?.applicationContext)
        binding.rvGymBooking.adapter = adapter
        adapter.notifyDataSetChanged()
    }

    fun bookingGymCancel(position: Int) {
        val bookingGym = adapter.data[position]
        val bookingGymId = bookingGym.id

        Log.d("onBookingGymCancel", bookingGymId.toString())

        val stringRequest : StringRequest = object:
            StringRequest(Method.DELETE, Api.DELETE_GYM_BOOKING_URL+ bookingGymId.toString(), Response.Listener { response ->

                getGymBooking(idMember)
                Toast.makeText(requireActivity(), "Booking class canceled.", Toast.LENGTH_SHORT).show()
            }, Response.ErrorListener { error ->
                binding.refreshRvItem.isRefreshing = false

                try {
                    val responseBody =
                        String(error.networkResponse.data, StandardCharsets.UTF_8)
                    val errors = JSONObject(responseBody)
                    Toast.makeText(requireActivity(), errors.getString("message"), Toast.LENGTH_SHORT).show()
                } catch (e: Exception){
                    Toast.makeText(requireActivity(), e.message, Toast.LENGTH_SHORT).show()
                }
            }) {
            @Throws(AuthFailureError::class)
            override fun getHeaders(): Map<String, String> {
                val headers = HashMap<String, String>()
                headers["Accept"] = "application/json"
                headers["Authorization"] = "$tokenType $accessToken"
                return headers
            }

        }
        queue!!.add(stringRequest)
    }

}