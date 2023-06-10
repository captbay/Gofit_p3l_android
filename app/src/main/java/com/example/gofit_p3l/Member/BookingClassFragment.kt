package com.example.gofit_p3l.Member

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
import com.example.gofit_p3l.AddBookingClassActivity
import com.example.gofit_p3l.Api.Api
import com.example.gofit_p3l.HomeMemberActivity
import com.example.gofit_p3l.R
import com.example.gofit_p3l.User.MemberAdapter.MemberBookingClassAdapter
import com.example.gofit_p3l.User.MemberModels.BookingClass
import com.example.gofit_p3l.databinding.FragmentBookingClassBinding
import com.google.gson.Gson
import org.json.JSONException
import org.json.JSONObject
import java.nio.charset.StandardCharsets
import java.text.SimpleDateFormat
import java.util.Locale

class BookingClassFragment : Fragment() {
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

    private var _binding: FragmentBookingClassBinding? = null
    private val binding get() = _binding!!

    private lateinit var adapter: MemberBookingClassAdapter
    private lateinit var layoutManager: LinearLayoutManager
    private lateinit var rvBookingClass: RecyclerView

    override fun onCreateView(
        inflater: LayoutInflater, container: ViewGroup?,
        savedInstanceState: Bundle?
    ): View? {
        _binding = FragmentBookingClassBinding.inflate(inflater, container, false)
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
        displayBookingClass(emptyArray())
        getClassBooking(idMember)

        //kalo refresh ulang lagi nampilin
        binding.refreshRvItem.setOnRefreshListener { getClassBooking(idMember)}

        binding.btnAdd.setOnClickListener {
            val moveAddBookingClass= Intent(activity, AddBookingClassActivity::class.java)
            startActivity(moveAddBookingClass)
            activity?.finish()
        }
    }

    private fun getClassBooking(idMember: Int){
        binding.refreshRvItem.isRefreshing = true

        val stringRequest : StringRequest = object:
            StringRequest(Method.GET, Api.GET_CLASS_BOOKING_BY_ID_MEMBER_URL+ idMember.toString(), Response.Listener { response ->
                try {
                    val gson = Gson()
                    val jsonObject = JSONObject(response)
                    val jsonArrayData = jsonObject.getJSONArray("data")

                    if(jsonArrayData.length() == 0){
                        Toast.makeText(requireActivity(), "TIDAK ADA DATA", Toast.LENGTH_SHORT).show()
                        displayBookingClass(emptyArray())
                        binding.refreshRvItem.isRefreshing = false
                    }else{
                        val classBookingList = mutableListOf<BookingClass>()

                        for (i in 0 until jsonArrayData.length()) {
                            val jsonData = jsonArrayData.getJSONObject(i)

                            val id = jsonData.getInt("id")
                            val no_class_booking = jsonData.getString("no_class_booking")
                            val nama_class = jsonData.getJSONObject("class_running").getJSONObject("jadwal_umum").getJSONObject("class_detail").getString("name")
                            val date = jsonData.getJSONObject("class_running").getString("date")
                            val time = convertTimeTo12HourFormat(jsonData.getJSONObject("class_running").getJSONObject("jadwal_umum").getString("start_class"))
                            val dateTime = "$date / $time"

                            val classBooking = BookingClass(
                                id,
                                no_class_booking,
                                nama_class,
                                dateTime,
                            )

                            classBookingList.add(classBooking)
                        }

                        displayBookingClass(classBookingList.toTypedArray())
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

    private fun displayBookingClass(data: Array<BookingClass>) {
        adapter = MemberBookingClassAdapter(data, object : MemberBookingClassAdapter.OnBookingClassClickListener {
            override fun onBookingClassCancel(position: Int) {
                bookingClassCancel(position)
            }
//            override fun onEdit() {
//
//            }
        })
        binding.rvClassBooking.layoutManager = LinearLayoutManager(activity?.applicationContext)
        binding.rvClassBooking.adapter = adapter
        adapter.notifyDataSetChanged()
    }


    fun bookingClassCancel(position: Int) {
        val bookingClass = adapter.data[position]
        val bookingClassId = bookingClass.id

        Log.d("onBookingClassCancel", bookingClassId.toString())

        val stringRequest : StringRequest = object:
            StringRequest(Method.DELETE, Api.DELETE_CLASS_BOOKING_URL+ bookingClassId.toString(), Response.Listener { response ->

                getClassBooking(idMember)
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