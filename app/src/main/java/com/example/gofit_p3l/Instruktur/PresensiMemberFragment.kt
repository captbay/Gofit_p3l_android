package com.example.gofit_p3l.Instruktur

import android.content.Context
import android.content.Intent
import android.content.SharedPreferences
import android.os.Bundle
import androidx.fragment.app.Fragment
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import android.widget.Toast
import androidx.recyclerview.widget.LinearLayoutManager
import com.android.volley.AuthFailureError
import com.android.volley.RequestQueue
import com.android.volley.Response
import com.android.volley.toolbox.StringRequest
import com.android.volley.toolbox.Volley
import com.example.awesomedialog.AwesomeDialog
import com.example.awesomedialog.body
import com.example.awesomedialog.icon
import com.example.awesomedialog.onNegative
import com.example.awesomedialog.position
import com.example.awesomedialog.title
import com.example.gofit_p3l.Api.Api
import com.example.gofit_p3l.HomeInstrukturActivity
import com.example.gofit_p3l.PresensiMemberByClassActivity
import com.example.gofit_p3l.R
import com.example.gofit_p3l.User.InstrukturAdapter.InstrukturPresensiMemberAdapter
import com.example.gofit_p3l.User.InstrukturModels.PresensiMember
import com.example.gofit_p3l.databinding.FragmentPresensiMemberBinding
import com.google.gson.Gson
import org.json.JSONException
import org.json.JSONObject
import java.nio.charset.StandardCharsets
import java.text.SimpleDateFormat
import java.util.Locale

class PresensiMemberFragment : Fragment() {
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

    private var _binding: FragmentPresensiMemberBinding? = null
    private val binding get() = _binding!!

    private lateinit var adapter: InstrukturPresensiMemberAdapter

    override fun onCreateView(
        inflater: LayoutInflater, container: ViewGroup?,
        savedInstanceState: Bundle?
    ): View? {
        _binding = FragmentPresensiMemberBinding.inflate(inflater, container, false)
        val view = binding.root
        return view
    }

    override fun onViewCreated(view: View, savedInstanceState: Bundle?) {
        super.onViewCreated(view, savedInstanceState)
        //attach this fragment to activity
        val activity = activity as HomeInstrukturActivity


        queue = Volley.newRequestQueue(requireActivity())
        sharedPreferences = activity.getSharedPreferences(myPreference, Context.MODE_PRIVATE)
        tokenType = sharedPreferences!!.getString(tokenTypePref,"").toString()
        accessToken = sharedPreferences!!.getString(accessTokenPref,"").toString()
        username = sharedPreferences!!.getString(usernamePref, "").toString()
        idInstruktur = sharedPreferences!!.getInt(idInstrukturPref,0)

        //ambil data
        displayPresensiMember(emptyArray())
        getPresensiMember(idInstruktur)

        //kalo refresh ulang lagi nampilin
        binding.refreshRvItem.setOnRefreshListener { getPresensiMember(idInstruktur)}

    }

    private fun getPresensiMember(idInstruktur: Int){
        binding.refreshRvItem.isRefreshing = true

        val stringRequest : StringRequest = object:
            StringRequest(Method.GET, Api.GET_CLASS_RUNNING_BY_ID_INSTRUKTUR_URL + idInstruktur.toString(), Response.Listener { response ->
                try {
                    val gson = Gson()
                    val jsonObject = JSONObject(response)
                    val jsonArrayData = jsonObject.getJSONArray("data")

                    if(jsonArrayData.length() == 0){
                        Toast.makeText(requireActivity(), "TIDAK ADA DATA", Toast.LENGTH_SHORT).show()
                        displayPresensiMember(emptyArray())
                        binding.refreshRvItem.isRefreshing = false
                    }else{
                        val presensiMemberList = mutableListOf<PresensiMember>()

                        for (i in 0 until jsonArrayData.length()) {
                            val jsonData = jsonArrayData.getJSONObject(i)

                            val id = jsonData.getInt("id")
                            val nama_class = jsonData.getJSONObject("jadwal_umum").getJSONObject("class_detail").getString("name")
                            val start_class = convertTimeTo12HourFormat(jsonData.getString("start_class"))
                            val end_class = convertTimeTo12HourFormat(jsonData.getString("end_class"))
                            val startEnd_class = "$start_class - $end_class"
                            val day_name = jsonData.getString("day_name")
                            val date = jsonData.getString("date")
                            val statusClassRunning = jsonData.getString("status")
                            val statusPresensi = jsonData.getJSONObject("presensi").getString("status_class")
                            val presensiMember= PresensiMember(
                                id,
                                nama_class,
                                startEnd_class,
                                day_name,
                                date,
                                statusClassRunning,
                                statusPresensi,
                            )

                            presensiMemberList.add(presensiMember)
                        }

                        displayPresensiMember(presensiMemberList.toTypedArray())
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

    //    to get HH:mm only
    private fun convertTimeTo12HourFormat(time: String): String {
        val inputFormat = SimpleDateFormat("HH:mm:ss", Locale.getDefault())
        val outputFormat = SimpleDateFormat("hh:mm a", Locale.getDefault())
        val date = inputFormat.parse(time)
        return outputFormat.format(date)
    }

    private fun displayPresensiMember(data: Array<PresensiMember>) {
        adapter = InstrukturPresensiMemberAdapter(data, object : InstrukturPresensiMemberAdapter.OnPresensiMemberClickListener {
            override fun onPresensiMemberPresensi(position: Int) {
//                move to activity update start
//                get id presensi instruktur
                val presensiMember = adapter.data[position]
                val idClassRunning = presensiMember.id

                if(presensiMember.statusPresensi == "null"){
                    AwesomeDialog.build(requireActivity())
                        .title("Restrict Access")
                        .body("Anda Belum Di Presensi Oleh MO!")
                        .icon(R.drawable.icon_gofit)
                        .onNegative("OK") {

                        }
                        .position(AwesomeDialog.POSITIONS.CENTER)
                }else{
                    val moveDetail= Intent(activity, PresensiMemberByClassActivity::class.java)
                    val bundle = Bundle()
                    bundle.putInt("key", idClassRunning)
                    moveDetail?.putExtra("keyBundle", bundle)
                    startActivity(moveDetail)
                    activity?.finish()
                }
            }

        })
        binding.rvPresensiMember.layoutManager = LinearLayoutManager(activity?.applicationContext)
        binding.rvPresensiMember.adapter = adapter
        adapter.notifyDataSetChanged()
    }
}