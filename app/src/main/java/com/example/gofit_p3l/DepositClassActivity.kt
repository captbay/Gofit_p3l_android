package com.example.gofit_p3l

import android.annotation.SuppressLint
import android.content.Context
import android.content.SharedPreferences
import android.os.Bundle
import android.util.Log
import android.widget.Toast
import androidx.appcompat.app.AppCompatActivity
import androidx.recyclerview.widget.LinearLayoutManager
import androidx.swiperefreshlayout.widget.SwipeRefreshLayout
import com.android.volley.AuthFailureError
import com.android.volley.RequestQueue
import com.android.volley.Response
import com.android.volley.toolbox.StringRequest
import com.android.volley.toolbox.Volley
import com.example.gofit_p3l.Api.Api
import com.example.gofit_p3l.User.DepositClass
import com.example.gofit_p3l.User.DepositClassAdapter
import com.example.gofit_p3l.databinding.ActivityDepositClassBinding
import org.json.JSONException
import org.json.JSONObject
import java.nio.charset.StandardCharsets

class DepositClassActivity : AppCompatActivity() {
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

    //buat ambil binding xml
    private lateinit var binding: ActivityDepositClassBinding

    private lateinit var adapter: DepositClassAdapter

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
        idMember = sharedPreferences!!.getInt(idMemberPref,0)

        binding = ActivityDepositClassBinding.inflate(layoutInflater)
        val view = binding.root

        setContentView(view)

        displayDepositPackage(emptyArray())
        getDepositPackage()

        swipeRefreshLayout = binding.refreshRvItem
        swipeRefreshLayout.setOnRefreshListener {
            getDepositPackage()
        }

        binding.btnBack.setOnClickListener {
            this.finish()
        }

    }

    private fun getDepositPackage(){
        binding.refreshRvItem.isRefreshing = true

        val stringRequest: StringRequest = object :
            StringRequest(Method.GET, Api.GET_DEPOSIT_PACKAGE_BY_ID_MEMBER_URL + idMember.toString(), Response.Listener { response ->
                // Process the API response here
                try {
                    val jsonObject = JSONObject(response)
                    val dataArray = jsonObject.getJSONArray("data")

                    val depositPackageList = mutableListOf<DepositClass>()

                    if(dataArray.length() == 0){
                        Toast.makeText(this, "TIDAK ADA DATA", Toast.LENGTH_SHORT).show()
                        displayDepositPackage(emptyArray())
                        binding.refreshRvItem.isRefreshing = false
                    }else{
                        for (i in 0 until dataArray.length()) {
                            val dataObject = dataArray.getJSONObject(i)

                            val id = dataObject.getInt("id")
                            val packageAmount = dataObject.getString("package_amount")
                            val expiredDate = dataObject.getString("expired_date")
                            val nameClass = dataObject.getJSONObject("class_detail").getString("name")

                            val depositPackage = DepositClass(
                                id,
                                nameClass,
                                packageAmount,
                                expiredDate,
                            )

                            depositPackageList.add(depositPackage)
                        }

                        displayDepositPackage(depositPackageList.toTypedArray())
                        binding.refreshRvItem.isRefreshing = false
                    }
                } catch (e: JSONException) {
                    binding.refreshRvItem.isRefreshing = false
                    e.printStackTrace()
                }

            }, Response.ErrorListener { error ->
                Log.d("depositpackage","erorr add deposit package")
                Log.d("AAAAAA", error.networkResponse.statusCode.toString())
                val responseBody =
                    String(error.networkResponse.data, StandardCharsets.UTF_8)
                val jsonObject = JSONObject(responseBody)
                Toast.makeText(this, jsonObject.toString(), Toast.LENGTH_SHORT).show()
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
    private fun displayDepositPackage(data: Array<DepositClass>) {
        adapter = DepositClassAdapter(data)
        binding.rvDepositClass.layoutManager = LinearLayoutManager(this)
        binding.rvDepositClass.adapter = adapter
        adapter.notifyDataSetChanged()
    }

    @Deprecated("Deprecated in Java")
    override fun onBackPressed() {
        super.onBackPressed()
        this.finish()
    }
}