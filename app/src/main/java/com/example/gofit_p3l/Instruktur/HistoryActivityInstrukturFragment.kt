package com.example.gofit_p3l.Instruktur

import android.annotation.SuppressLint
import android.content.Context
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
import com.example.gofit_p3l.Api.Api
import com.example.gofit_p3l.HomeInstrukturActivity
import com.example.gofit_p3l.User.InstrukturAdapter.InstrukturHistoryActivityAdapter
import com.example.gofit_p3l.User.InstrukturModels.InstrukturActivities
import com.example.gofit_p3l.databinding.FragmentHistoryActivityInstrukturBinding
import org.json.JSONException
import org.json.JSONObject
import java.nio.charset.StandardCharsets


class HistoryActivityInstrukturFragment : Fragment() {
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

    private var _binding: FragmentHistoryActivityInstrukturBinding? = null
    private val binding get() = _binding!!

    private lateinit var adapter: InstrukturHistoryActivityAdapter

    override fun onCreateView(
        inflater: LayoutInflater, container: ViewGroup?,
        savedInstanceState: Bundle?
    ): View? {
        _binding = FragmentHistoryActivityInstrukturBinding.inflate(inflater, container, false)
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
        displayActivities(emptyArray())
        getInstrukturActivities(idInstruktur)

        //kalo refresh ulang lagi nampilin
        binding.refreshRvItem.setOnRefreshListener { getInstrukturActivities(idInstruktur)}
    }

    private fun getInstrukturActivities(idInstruktur: Int){
        binding.refreshRvItem.isRefreshing = true

        val stringRequest : StringRequest = object:
            StringRequest(Method.GET, Api.GET_INSTRUKTUR_ACTIVITIES_BY_ID_INSTRUKTUR_URL+ idInstruktur.toString(), Response.Listener { response ->
                try {

                    val jsonObject = JSONObject(response)
                    val jsonArrayData = jsonObject.getJSONArray("data")

                    if(jsonArrayData.length() == 0){
                        Toast.makeText(requireActivity(), "TIDAK ADA DATA", Toast.LENGTH_SHORT).show()
                        displayActivities(emptyArray())
                        binding.refreshRvItem.isRefreshing = false
                    }else{
                        val tempList = mutableListOf<InstrukturActivities>()

                        for (i in 0 until jsonArrayData.length()) {
                            val jsonData = jsonArrayData.getJSONObject(i)

                            val id = jsonData.getInt("id")
                            val name_activity = jsonData.getString("name_activity")
                            val description_activity = jsonData.getString("description_activity")
                            val date_time = jsonData.getString("date_time")

                            val instrukturActivities = InstrukturActivities(
                                id,
                                name_activity,
                                description_activity,
                                date_time,
                            )

                            tempList.add(instrukturActivities)
                        }

                        displayActivities(tempList.toTypedArray())
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

    @SuppressLint("NotifyDataSetChanged")
    private fun displayActivities(data: Array<InstrukturActivities>) {
        adapter = InstrukturHistoryActivityAdapter(data)
        binding.rvHistoryActivity.layoutManager = LinearLayoutManager(activity?.applicationContext)
        binding.rvHistoryActivity.adapter = adapter
        adapter.notifyDataSetChanged()
    }
}