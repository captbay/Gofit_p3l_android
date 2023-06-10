package com.example.gofit_p3l.Instruktur

import android.content.Context
import android.content.Intent
import android.content.SharedPreferences
import android.os.Bundle
import androidx.fragment.app.Fragment
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import android.widget.SearchView
import android.widget.Toast
import androidx.recyclerview.widget.LinearLayoutManager
import androidx.recyclerview.widget.RecyclerView
import androidx.swiperefreshlayout.widget.SwipeRefreshLayout
import com.android.volley.AuthFailureError
import com.android.volley.RequestQueue
import com.android.volley.Response
import com.android.volley.toolbox.StringRequest
import com.android.volley.toolbox.Volley
import com.example.gofit_p3l.AddInstrukturIzinActivity
import com.example.gofit_p3l.Api.Api
import com.example.gofit_p3l.HomeInstrukturActivity
import com.example.gofit_p3l.R
import com.example.gofit_p3l.UpdatePasswordActivity
import com.example.gofit_p3l.User.InstrukturAdapter.InstrukturIzinAdapter
import com.example.gofit_p3l.User.InstrukturModels.InstrukturIzin
import com.example.gofit_p3l.databinding.ActivityUpdatePasswordBinding
import com.example.gofit_p3l.databinding.FragmentIzinInstrukturBinding
import com.google.gson.Gson
import org.json.JSONException
import org.json.JSONObject
import java.nio.charset.StandardCharsets
import java.text.SimpleDateFormat
import java.util.Locale

class IzinInstrukturFragment : Fragment() {
    //buat cookies
    private val myPreference = "myPref"
    private val idPref = "idKey"
    private val usernamePref = "usernameKey"
    private val fullnamePref = "fullnameKey"
    private val rolePref= "roleKey"
    private val tokenTypePref = "token_typeKey"
    private val accessTokenPref = "access_tokenKey"
    var sharedPreferences: SharedPreferences? = null

    //untuk naruh di header setap connect ke back end soalnya udah login
    private var username: String = ""
    private var tokenType: String = ""
    private var accessToken: String = ""

    //buat request
    private var queue: RequestQueue? = null

    private var _binding: FragmentIzinInstrukturBinding? = null
    private val binding get() = _binding!!

    private lateinit var adapter: InstrukturIzinAdapter
    private lateinit var layoutManager: LinearLayoutManager
    private lateinit var rvIzin: RecyclerView

    override fun onCreateView(
        inflater: LayoutInflater, container: ViewGroup?,
        savedInstanceState: Bundle?
    ): View? {
        _binding = FragmentIzinInstrukturBinding.inflate(inflater, container, false)
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

        val bundle = Bundle()
        
        //lakukan setting untuk rv biar terconnet adapter dengan baik dan set layoutnya
        layoutManager = LinearLayoutManager(activity?.applicationContext)
        adapter = InstrukturIzinAdapter(emptyArray())
        rvIzin = binding.rvIzinInstruktur
        rvIzin.adapter = adapter // Memasang adapter sebelum mengatur layoutManager
        rvIzin.layoutManager = layoutManager
        rvIzin.setHasFixedSize(true)

        //ambil data izin
        getInstrukturIzin(username)

        //kalo refresh ulang lagi nampilin
        binding.refreshRvItem.setOnRefreshListener { getInstrukturIzin(username) }

        binding.btnAdd.setOnClickListener {
            val moveAddInstrukturIzin = Intent(activity, AddInstrukturIzinActivity::class.java)
            startActivity(moveAddInstrukturIzin)
            activity?.finish()
        }

    }

    private fun getInstrukturIzin(username: String){
        binding.refreshRvItem.isRefreshing = true

        val stringRequest : StringRequest = object:
            StringRequest(Method.GET, Api.GET_IZIN_BY_USERNAME+ username, Response.Listener { response ->
                try {
                    val gson = Gson()
                    val jsonObject = JSONObject(response)
                    val jsonArrayData = jsonObject.getJSONArray("data")

                    if(jsonArrayData.length() == 0){
                        Toast.makeText(requireActivity(), "TIDAK ADA DATA", Toast.LENGTH_SHORT).show()
                        binding.refreshRvItem.isRefreshing = false
                    }else{
                        val instrukturIzinList = mutableListOf<InstrukturIzin>()

                        for (i in 0 until jsonArrayData.length()) {
                            val jsonData = jsonArrayData.getJSONObject(i)

                            val id = jsonData.getInt("id")
                            val namaInstrukturPengganti = jsonData.getJSONObject("instruktur_pengganti").getString("name")
                            val namaClass = jsonData.getJSONObject("class_running").getJSONObject("jadwal_umum").getJSONObject("class_detail").getString("name")
                            val dayClass = jsonData.getJSONObject("class_running").getString("day_name")
                            val startClass = convertTimeTo12HourFormat(jsonData.getJSONObject("class_running").getString("start_class"))
                            val namaClassRunning = namaClass +" - "+ dayClass +" - "+ startClass
                            val alasan = jsonData.getString("alasan")
                            val isConfirm = jsonData.getInt("is_confirm")
                            val date = jsonData.getString("date")

                            val instrukturIzin = InstrukturIzin(
                                id,
                                namaInstrukturPengganti,
                                namaClassRunning,
                                alasan,
                                isConfirm,
                                date
                            )

                            instrukturIzinList.add(instrukturIzin)
                        }

                        displayInstrukturIzin(instrukturIzinList.toTypedArray())
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

    private fun displayInstrukturIzin(data: Array<InstrukturIzin>) {
        adapter = InstrukturIzinAdapter(data)
        binding.rvIzinInstruktur.layoutManager = LinearLayoutManager(activity?.applicationContext)
        binding.rvIzinInstruktur.adapter = adapter
        adapter.notifyDataSetChanged()
    }
}