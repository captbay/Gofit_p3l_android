package com.example.gofit_p3l.Member

import android.content.Context
import android.content.Intent
import android.content.SharedPreferences
import android.os.Bundle
import androidx.fragment.app.Fragment
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import android.view.WindowManager
import android.widget.Toast
import androidx.constraintlayout.widget.ConstraintLayout
import com.android.volley.AuthFailureError
import com.android.volley.RequestQueue
import com.android.volley.Response
import com.android.volley.toolbox.StringRequest
import com.android.volley.toolbox.Volley
import com.example.gofit_p3l.Api.Api
import com.example.gofit_p3l.DepositClassActivity
import com.example.gofit_p3l.HomeMemberActivity
import com.example.gofit_p3l.R
import com.example.gofit_p3l.databinding.ActivityHomeMemberBinding
import com.example.gofit_p3l.databinding.FragmentHomeMemberBinding
import org.json.JSONObject
import java.nio.charset.StandardCharsets

class HomeMemberFragment : Fragment() {
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

    private var _binding: FragmentHomeMemberBinding? = null
    private var queue: RequestQueue? = null
    private val binding get() = _binding!!
    private lateinit var bindingHome: ActivityHomeMemberBinding
    private var layoutLoading: ConstraintLayout? = null


    override fun onCreateView(
        inflater: LayoutInflater, container: ViewGroup?,
        savedInstanceState: Bundle?
    ): View {
        // Inflate the layout for this fragment
        _binding = FragmentHomeMemberBinding.inflate(inflater, container, false)
        return binding.root
    }

    override fun onViewCreated(view: View, savedInstanceState: Bundle?) {
        super.onViewCreated(view, savedInstanceState)
        //attach this fragment to activity
        val activity = activity as HomeMemberActivity
        bindingHome = ActivityHomeMemberBinding.inflate(layoutInflater)

        layoutLoading = view.findViewById(R.id.layout_loading_member)

        val moveDepositClass = Intent(activity, DepositClassActivity::class.java)

        queue = Volley.newRequestQueue(requireActivity())
        sharedPreferences = activity.getSharedPreferences(myPreference, Context.MODE_PRIVATE)
        tokenType = sharedPreferences!!.getString(tokenTypePref,"").toString()
        accessToken = sharedPreferences!!.getString(accessTokenPref,"").toString()
        username = sharedPreferences!!.getString(usernamePref, "").toString()
        idMember = sharedPreferences!!.getInt(idMemberPref,0)

        val inputString = sharedPreferences!!.getString(fullnamePref,"").toString()
        val words = inputString.split(" ") // Memisahkan string menjadi array kata-kata

        val numberOfWordsToShow = 3 // Jumlah kata yang ingin ditampilkan
        val selectedWords = words.take(numberOfWordsToShow) // Mengambil kata-kata yang akan ditampilkan

        binding.fullname.text =selectedWords.joinToString(" ")

        binding.depositClass.setOnClickListener {
            startActivity(moveDepositClass)
        }

    }

    override fun onStart() {
        super.onStart()
        getUserLogin()
        getCountPackage()
    }

    private fun getUserLogin(){
        setLoading(true)
        val stringRequest : StringRequest = object:
            StringRequest(Method.GET, Api.GET_USER_URL_LOGIN, Response.Listener { response ->

                val jsonObject = JSONObject(response)
                val jsonArray = jsonObject.getJSONObject("dataDiri")

                binding.depositUang.text = jsonArray.getString("jumlah_deposit_reguler")

                setLoading(false)

            }, Response.ErrorListener { error ->
                setLoading(false)
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

    private fun getCountPackage(){
        setLoading(true)
        val stringRequest : StringRequest = object:
            StringRequest(Method.GET, Api.GET_COUNT_DEPOSIT_PACKAGE_BY_ID_MEMBER_URL + idMember.toString(), Response.Listener { response ->

                val jsonObject = JSONObject(response)
                val count = jsonObject.getString("data")

                binding.depositClass.text = count + " Class"

                setLoading(false)

            }, Response.ErrorListener { error ->
                setLoading(false)
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

    private fun setLoading(isLoading: Boolean){
        if(isLoading){
            activity?.window?.setFlags(
                WindowManager.LayoutParams.FLAG_NOT_TOUCHABLE,
                WindowManager.LayoutParams.FLAG_NOT_TOUCHABLE
            )

            layoutLoading!!.animate().alpha(0f).setDuration(500).withEndAction {
                layoutLoading!!.visibility = View.VISIBLE
            }

        }else{
            activity?.window?.clearFlags(WindowManager.LayoutParams.FLAG_NOT_TOUCHABLE)
            layoutLoading!!.animate().alpha(0f).setDuration(250).withEndAction {
                layoutLoading!!.visibility = View.GONE
            }

        }
    }
}