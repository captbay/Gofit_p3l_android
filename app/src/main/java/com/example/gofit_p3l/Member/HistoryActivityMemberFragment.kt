package com.example.gofit_p3l.Member

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
import com.example.gofit_p3l.HomeMemberActivity
import com.example.gofit_p3l.User.MemberAdapter.MemberHistoryActivityAdapter
import com.example.gofit_p3l.User.MemberModels.MemberActivities
import com.example.gofit_p3l.databinding.FragmentHistoryActivityMemberBinding
import org.json.JSONException
import org.json.JSONObject
import java.nio.charset.StandardCharsets


class HistoryActivityMemberFragment : Fragment() {
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

    private var _binding: FragmentHistoryActivityMemberBinding? = null
    private val binding get() = _binding!!

    private lateinit var adapter: MemberHistoryActivityAdapter

    override fun onCreateView(
        inflater: LayoutInflater, container: ViewGroup?,
        savedInstanceState: Bundle?
    ): View? {
        _binding = FragmentHistoryActivityMemberBinding.inflate(inflater, container, false)
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
        displayActivities(emptyArray())
        getMemberActivities(idMember)

        //kalo refresh ulang lagi nampilin
        binding.refreshRvItem.setOnRefreshListener { getMemberActivities(idMember)}
    }

    private fun getMemberActivities(idMember: Int){
        binding.refreshRvItem.isRefreshing = true

        val stringRequest : StringRequest = object:
            StringRequest(Method.GET, Api.GET_MEMBER_ACTIVITIES_BY_ID_MEMBER_URL+ idMember.toString(), Response.Listener { response ->
                try {

                    val jsonObject = JSONObject(response)
                    val jsonArrayData = jsonObject.getJSONArray("data")

                    if(jsonArrayData.length() == 0){
                        Toast.makeText(requireActivity(), "TIDAK ADA DATA", Toast.LENGTH_SHORT).show()
                        displayActivities(emptyArray())
                        binding.refreshRvItem.isRefreshing = false
                    }else{
                        val tempList = mutableListOf<MemberActivities>()

                        for (i in 0 until jsonArrayData.length()) {
                            val jsonData = jsonArrayData.getJSONObject(i)

                            val id = jsonData.getInt("id")
                            val name_activity = jsonData.getString("name_activity")
                            val no_activity = jsonData.getString("no_activity")
                            val price_activity = jsonData.getString("price_activity")
                            val date_time = jsonData.getString("date_time")

                            val memberActivities = MemberActivities(
                                id,
                                name_activity,
                                price_activity,
                                no_activity,
                                date_time,
                            )

                            tempList.add(memberActivities)
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
    private fun displayActivities(data: Array<MemberActivities>) {
        adapter = MemberHistoryActivityAdapter(data)
        binding.rvHistoryActivity.layoutManager = LinearLayoutManager(activity?.applicationContext)
        binding.rvHistoryActivity.adapter = adapter
        adapter.notifyDataSetChanged()
    }
}