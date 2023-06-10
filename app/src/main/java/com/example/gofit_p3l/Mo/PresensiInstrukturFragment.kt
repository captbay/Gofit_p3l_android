package com.example.gofit_p3l.Mo

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
import androidx.recyclerview.widget.RecyclerView
import com.android.volley.AuthFailureError
import com.android.volley.RequestQueue
import com.android.volley.Response
import com.android.volley.toolbox.StringRequest
import com.android.volley.toolbox.Volley
import com.example.gofit_p3l.AddBookingClassActivity
import com.example.gofit_p3l.Api.Api
import com.example.gofit_p3l.HomeMemberActivity
import com.example.gofit_p3l.HomeMoActivity
import com.example.gofit_p3l.R
import com.example.gofit_p3l.UpdateEndClassActivity
import com.example.gofit_p3l.UpdateStartClassActivity
import com.example.gofit_p3l.User.MoAdapter.MoPresensiInstrukturAdapter
import com.example.gofit_p3l.User.MoModels.PresensiInstruktur
import com.example.gofit_p3l.databinding.FragmentPresensiInstrukturBinding
import com.google.gson.Gson
import org.json.JSONException
import org.json.JSONObject
import java.nio.charset.StandardCharsets

class PresensiInstrukturFragment : Fragment() {
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
//    private var idMember: Int = 0

    //buat request
    private var queue: RequestQueue? = null

    private var _binding: FragmentPresensiInstrukturBinding? = null
    private val binding get() = _binding!!

    private lateinit var adapter: MoPresensiInstrukturAdapter

    override fun onCreateView(
        inflater: LayoutInflater, container: ViewGroup?,
        savedInstanceState: Bundle?
    ): View? {
        _binding = FragmentPresensiInstrukturBinding.inflate(inflater, container, false)
        val view = binding.root
        return view
    }

    override fun onViewCreated(view: View, savedInstanceState: Bundle?) {
        super.onViewCreated(view, savedInstanceState)
        //attach this fragment to activity
        val activity = activity as HomeMoActivity


        queue = Volley.newRequestQueue(requireActivity())
        sharedPreferences = activity.getSharedPreferences(myPreference, Context.MODE_PRIVATE)
        tokenType = sharedPreferences!!.getString(tokenTypePref,"").toString()
        accessToken = sharedPreferences!!.getString(accessTokenPref,"").toString()
        username = sharedPreferences!!.getString(usernamePref, "").toString()
//        idMember = sharedPreferences!!.getInt(idMemberPref,0)

        //ambil data
        displayPresensiInstruktur(emptyArray())
        getPresensiInstruktur()

        //kalo refresh ulang lagi nampilin
        binding.refreshRvItem.setOnRefreshListener { getPresensiInstruktur()}

    }

    private fun getPresensiInstruktur(){
        binding.refreshRvItem.isRefreshing = true

        val stringRequest : StringRequest = object:
            StringRequest(Method.GET, Api.GET_PRESENSI_INSTRUKTUR_URL, Response.Listener { response ->
                try {
                    val gson = Gson()
                    val jsonObject = JSONObject(response)
                    val jsonArrayData = jsonObject.getJSONArray("data")

                    if(jsonArrayData.length() == 0){
                        Toast.makeText(requireActivity(), "TIDAK ADA DATA", Toast.LENGTH_SHORT).show()
                        displayPresensiInstruktur(emptyArray())
                        binding.refreshRvItem.isRefreshing = false
                    }else{
                        val presensiInstrukturList = mutableListOf<PresensiInstruktur>()

                        for (i in 0 until jsonArrayData.length()) {
                            val jsonData = jsonArrayData.getJSONObject(i)

                            val id = jsonData.getInt("id")
                            val nama_class = jsonData.getJSONObject("class_running").getJSONObject("jadwal_umum").getJSONObject("class_detail").getString("name")
                            val nama_instruktur = jsonData.getJSONObject("instruktur").getString("name")
                            val start_class = getTimeOnly(jsonData.getJSONObject("class_running").getString("start_class"))
                            val end_class = getTimeOnly(jsonData.getJSONObject("class_running").getString("end_class"))
                            val startEnd_class = "$start_class - $end_class"
                            val day_name = jsonData.getJSONObject("class_running").getString("day_name")
                            val date = jsonData.getJSONObject("class_running").getString("date")
                            var status = jsonData.getString("status_class")
                            val statusClassRunning = jsonData.getJSONObject("class_running").getString("status")

                            status = if(status == "1"){
                                "Hadir"
                            }else{
                                ""
                            }

                            val presensiInstruktur= PresensiInstruktur(
                                id,
                                nama_class,
                                nama_instruktur,
                                startEnd_class,
                                day_name,
                                date,
                                status,
                                statusClassRunning,
                            )

                            presensiInstrukturList.add(presensiInstruktur)
                        }

                        displayPresensiInstruktur(presensiInstrukturList.toTypedArray())
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
    fun getTimeOnly(timeString: String): String {
        val parts = timeString.split(":")
        if (parts.size >= 2) {
            val hours = parts[0]
            val minutes = parts[1]
            return "$hours:$minutes"
        }
        return ""
    }

    private fun displayPresensiInstruktur(data: Array<PresensiInstruktur>) {
        adapter = MoPresensiInstrukturAdapter(data, object : MoPresensiInstrukturAdapter.OnPresensiInstrukturClickListener {
            override fun onPresensiInstrukturUpdateStartClass(position: Int) {
//                move to activity update start
//                get id presensi instruktur
                val presensiInstruktur = adapter.data[position]
                val id = presensiInstruktur.id

                val moveUpdateStartClass= Intent(activity, UpdateStartClassActivity::class.java)
                val bundle = Bundle()
                bundle.putInt("key", id)
                moveUpdateStartClass?.putExtra("keyBundle", bundle)
                startActivity(moveUpdateStartClass)
                activity?.finish()
            }
            override fun onPresensiInstrukturUpdateEndClass(position: Int) {
//                move to activity update end
//                get id
                val presensiInstruktur = adapter.data[position]
                val id = presensiInstruktur.id

                val moveUpdateEndClass= Intent(activity, UpdateEndClassActivity::class.java)
                val bundle = Bundle()
                bundle.putInt("key", id)
                moveUpdateEndClass?.putExtra("keyBundle", bundle)
                startActivity(moveUpdateEndClass)
                activity?.finish()
            }

        })
        binding.rvPresensiInstruktur.layoutManager = LinearLayoutManager(activity?.applicationContext)
        binding.rvPresensiInstruktur.adapter = adapter
        adapter.notifyDataSetChanged()
    }
}