package com.example.gofit_p3l

import android.content.Context
import android.content.Intent
import android.content.SharedPreferences
import androidx.appcompat.app.AppCompatActivity
import android.os.Bundle
import android.widget.Toast
import androidx.recyclerview.widget.LinearLayoutManager
import androidx.swiperefreshlayout.widget.SwipeRefreshLayout
import com.android.volley.AuthFailureError
import com.android.volley.RequestQueue
import com.android.volley.Response
import com.android.volley.toolbox.StringRequest
import com.android.volley.toolbox.Volley
import com.example.gofit_p3l.Api.Api
import com.example.gofit_p3l.User.InstrukturAdapter.InstrukturPresensiMemberAdapter
import com.example.gofit_p3l.User.InstrukturAdapter.InstrukturPresensiMemberByClassAdapter
import com.example.gofit_p3l.User.InstrukturModels.PresensiMemberByClass
import com.example.gofit_p3l.databinding.ActivityPresensiMemberByClassBinding
import com.google.gson.Gson
import org.json.JSONException
import org.json.JSONObject
import java.nio.charset.StandardCharsets

class PresensiMemberByClassActivity : AppCompatActivity() {
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
    private var idInstruktur: Int = 0

    //buat request
    private var queue: RequestQueue? = null

    //buat ambil binding xml
    private lateinit var binding: ActivityPresensiMemberByClassBinding
    private lateinit var adapter: InstrukturPresensiMemberByClassAdapter
    //buat kembali ke home
    var moveHome: Intent? = null

    //init other?
    private var selectedIdClassRunning: Int = -1
    private lateinit var swipeRefreshLayout: SwipeRefreshLayout

    override fun onCreate(savedInstanceState: Bundle?) {
        supportActionBar?.hide()
        super.onCreate(savedInstanceState)

        queue = Volley.newRequestQueue(this)
        //ambil data dari cookies
        sharedPreferences = getSharedPreferences(myPreference, Context.MODE_PRIVATE)
        tokenType = sharedPreferences!!.getString(tokenTypePref, "").toString()
        accessToken = sharedPreferences!!.getString(accessTokenPref, "").toString()
        username = sharedPreferences!!.getString(usernamePref, "").toString()
        idInstruktur = sharedPreferences!!.getInt(idInstrukturPref,0)

        binding = ActivityPresensiMemberByClassBinding.inflate(layoutInflater)
        val view = binding.root

        setContentView(view)

        val bundle = intent.getBundleExtra("keyBundle")
        if (bundle != null) {
            selectedIdClassRunning = bundle.getInt("key",-1)
        }

        moveHome = Intent(this, HomeInstrukturActivity::class.java)

        //ambil data
        displayPresensiMemberByClass(emptyArray())
        getClassBooking(idInstruktur, selectedIdClassRunning)

        //kalo refresh ulang lagi nampilin
        swipeRefreshLayout = binding.refreshRvItem
        swipeRefreshLayout.setOnRefreshListener {
            getClassBooking(idInstruktur, selectedIdClassRunning)
        }

        binding.btnBack.setOnClickListener {
            val bundle = Bundle()
            bundle.putString("key", "pindahPresensiMember")
            moveHome?.putExtra("keyBundle", bundle)
            startActivity(moveHome)
            this.finish()
        }

    }

    private fun getClassBooking(idInstruktur: Int, idClassRunning: Int){
        binding.refreshRvItem.isRefreshing = true

        val stringRequest : StringRequest = object:
            StringRequest(Method.GET, Api.GET_CLASS_BOOKING_BY_INSTRUKTUR_LOGIN_CLASS_RUNNING_SELECTED_URL + idInstruktur.toString() +"/"+ idClassRunning.toString(), Response.Listener { response ->
                try {
                    val gson = Gson()
                    val jsonObject = JSONObject(response)
                    val jsonArrayData = jsonObject.getJSONArray("data")

                    if(jsonArrayData.length() == 0){
                        Toast.makeText(this, "TIDAK ADA DATA", Toast.LENGTH_SHORT).show()
                        displayPresensiMemberByClass(emptyArray())
                        binding.refreshRvItem.isRefreshing = false
                    }else{
                        val presensiMemberByCLassList = mutableListOf<PresensiMemberByClass>()

                        for (i in 0 until jsonArrayData.length()) {
                            val jsonData = jsonArrayData.getJSONObject(i)

                            val id = jsonData.getInt("id")
                            val no_class_booking = jsonData.getString("no_class_booking")
                            val metode_pembayaran = jsonData.getString("metode_pembayaran")
                            val member_name = jsonData.getJSONObject("member").getString("name")
                            var status_presensi = jsonData.getString("status")

                            if(status_presensi == "null"){
                                status_presensi = "Belum Di Presensi"
                            }else if(status_presensi == "1"){
                                status_presensi = "Hadir"
                            }else if(status_presensi == "0"){
                                status_presensi = "Tidak Hadir"
                            }

                            val presensiMemberByClass= PresensiMemberByClass(
                                id,
                                no_class_booking,
                                metode_pembayaran,
                                member_name,
                                status_presensi,
                            )

                            presensiMemberByCLassList.add(presensiMemberByClass)
                        }

                        displayPresensiMemberByClass(presensiMemberByCLassList.toTypedArray())
                        binding.refreshRvItem.isRefreshing = false
                    }

                } catch (e: JSONException) {
                    binding.refreshRvItem.isRefreshing = false
                    Toast.makeText(this, "Error parsing JSON", Toast.LENGTH_SHORT).show()
                }
            }, Response.ErrorListener { error ->
                binding.refreshRvItem.isRefreshing = false

                try {
                    val responseBody =
                        String(error.networkResponse.data, StandardCharsets.UTF_8)
                    val errors = JSONObject(responseBody)
                    Toast.makeText(this, errors.getString("message"), Toast.LENGTH_SHORT).show()
                } catch (e: Exception){
                    Toast.makeText(this, e.message, Toast.LENGTH_SHORT).show()
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

    private fun displayPresensiMemberByClass(data: Array<PresensiMemberByClass>) {
        adapter = InstrukturPresensiMemberByClassAdapter(data, object : InstrukturPresensiMemberByClassAdapter.OnPresensiMemberClickListener {
            override fun onUpdateStatusPresensiMember(position: Int) {
                val booking = adapter.data[position]
                val idBooking = booking.id

                val moveDetail= Intent(this@PresensiMemberByClassActivity, UpdateStatusPresensiMemberActivity::class.java)
                val bundle = Bundle()
                bundle.putInt("key", idBooking)
                moveDetail.putExtra("keyBundle", bundle)
                startActivity(moveDetail)
//                finish()
            }

        })
        binding.rvPresensiMemberByClass.layoutManager = LinearLayoutManager(this)
        binding.rvPresensiMemberByClass.adapter = adapter
        adapter.notifyDataSetChanged()
    }

    override fun onBackPressed() {
        super.onBackPressed()
        // Navigate back to HomeActivity
        val bundle = Bundle()
        bundle.putString("key", "pindahPresensiMember")
        moveHome?.putExtra("keyBundle", bundle)
        startActivity(moveHome)
        this.finish()
    }

    override fun onResume() {
        super.onResume()
        swipeRefreshLayout.isEnabled = true
        getClassBooking(idInstruktur, selectedIdClassRunning)
    }

    override fun onPause() {
        super.onPause()
        swipeRefreshLayout.isEnabled = false
    }
}