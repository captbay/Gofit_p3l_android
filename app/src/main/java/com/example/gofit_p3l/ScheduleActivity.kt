package com.example.gofit_p3l

import android.annotation.SuppressLint
import androidx.appcompat.app.AppCompatActivity
import android.os.Bundle
import android.util.Log
import android.widget.Toast
import androidx.recyclerview.widget.LinearLayoutManager
import com.android.volley.AuthFailureError
import com.android.volley.RequestQueue
import com.android.volley.Response
import com.android.volley.toolbox.StringRequest
import com.android.volley.toolbox.Volley
import com.example.gofit_p3l.Api.Api
import com.example.gofit_p3l.User.ClassRunning
import com.example.gofit_p3l.User.ClassRunningAdapter
import com.example.gofit_p3l.databinding.ActivityScheduleBinding
import org.json.JSONException
import org.json.JSONObject
import java.nio.charset.StandardCharsets

class ScheduleActivity : AppCompatActivity() {
    //buat request
    private var queue: RequestQueue? = null

    private lateinit var binding: ActivityScheduleBinding

    private lateinit var adapter: ClassRunningAdapter

    override fun onCreate(savedInstanceState: Bundle?) {
        supportActionBar?.hide()
        super.onCreate(savedInstanceState)

        queue = Volley.newRequestQueue(this)

        binding = ActivityScheduleBinding.inflate(layoutInflater)
        val view = binding.root

        setContentView(view)

        displaySchedule(emptyArray())
        getClassRunning()

        binding.refreshRvItem.setOnRefreshListener { getClassRunning() }
    }

    private fun getClassRunning(){
        binding.refreshRvItem.isRefreshing = true

        val stringRequest: StringRequest = object :
            StringRequest(Method.GET, Api.GET_CLASS_RUNNING_URL, Response.Listener { response ->
                // Process the API response here
                try {
                    val jsonObject = JSONObject(response)
                    val dataArray = jsonObject.getJSONArray("data")

                    val classRunningList = mutableListOf<ClassRunning>()

                    if(dataArray.length() == 0){
                        Toast.makeText(this, "TIDAK ADA DATA", Toast.LENGTH_SHORT).show()
                        displaySchedule(emptyArray())
                        binding.refreshRvItem.isRefreshing = false
                    }else{
                        for (i in 0 until dataArray.length()) {
                            val dataObject = dataArray.getJSONObject(i)

                            val id = dataObject.getInt("id")
                            val day_name = dataObject.getString("date")
                            val date = dataObject.getString("day_name")
                            val startClass = dataObject.getJSONObject("jadwal_umum").getString("start_class")
                            val classDetail = dataObject.getJSONObject("jadwal_umum").getJSONObject("class_detail")
                            val nameClass = classDetail.getString("name")
                            val nameInstruktur = dataObject.getJSONObject("instruktur").getString("name")
                            val status = dataObject.getString("status")
                            val price = dataObject.getJSONObject("jadwal_umum").getJSONObject("class_detail").getString("price")
//                            if(status == ""){
//                                status = "Berjalan"
//                            }

                            val classRunning = ClassRunning(
                                id,
                                startClass,
                                nameClass,
                                nameInstruktur,
                                status,
                                day_name,
                                date,
                                price,
                            )

                            classRunningList.add(classRunning)
                        }

                        displaySchedule(classRunningList.toTypedArray())
                        binding.refreshRvItem.isRefreshing = false
                    }
                } catch (e: JSONException) {
                    binding.refreshRvItem.isRefreshing = false
                    e.printStackTrace()
                }

            }, Response.ErrorListener { error ->
                Log.d("Logout","erorr add class running")
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
                return headers
            }

        }
        queue!!.add(stringRequest)
    }

    @SuppressLint("NotifyDataSetChanged")
    private fun displaySchedule(data: Array<ClassRunning>) {
        adapter = ClassRunningAdapter(data)
        binding.rvJadwalHarian.layoutManager = LinearLayoutManager(this)
        binding.rvJadwalHarian.adapter = adapter
        adapter.notifyDataSetChanged()
    }
}