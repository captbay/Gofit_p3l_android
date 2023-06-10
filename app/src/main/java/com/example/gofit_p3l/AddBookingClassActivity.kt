package com.example.gofit_p3l

import android.annotation.SuppressLint
import android.content.Context
import android.content.Intent
import android.content.SharedPreferences
import androidx.appcompat.app.AppCompatActivity
import android.os.Bundle
import android.util.Log
import android.view.MotionEvent
import android.view.View
import android.widget.AdapterView
import android.widget.ArrayAdapter
import android.widget.Toast
import com.android.volley.AuthFailureError
import com.android.volley.RequestQueue
import com.android.volley.Response
import com.android.volley.toolbox.StringRequest
import com.android.volley.toolbox.Volley
import com.example.awesomedialog.AwesomeDialog
import com.example.awesomedialog.icon
import com.example.awesomedialog.onNegative
import com.example.awesomedialog.onPositive
import com.example.awesomedialog.position
import com.example.awesomedialog.title
import com.example.gofit_p3l.Api.Api
import com.example.gofit_p3l.databinding.ActivityAddBookingClassBinding
import org.json.JSONException
import org.json.JSONObject
import java.nio.charset.StandardCharsets
import java.text.SimpleDateFormat
import java.util.Locale

class AddBookingClassActivity : AppCompatActivity() {
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
    private lateinit var binding: ActivityAddBookingClassBinding

    //buat kembali ke home
    var moveHome: Intent? = null

    //init other?
    private var selectedIdClassRunning: Int = -1
    private val idListClassRunning = mutableListOf<Int>()

    @SuppressLint("ClickableViewAccessibility")
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

        binding = ActivityAddBookingClassBinding.inflate(layoutInflater)
        val view = binding.root

        setContentView(view)

        moveHome = Intent(this, HomeMemberActivity::class.java)

        getClassRunning()

        binding.btnBack.setOnClickListener {
            val bundle = Bundle()
            bundle.putString("key", "pindahBookingClass")
            moveHome?.putExtra("keyBundle", bundle)
            startActivity(moveHome)
            this.finish()
        }

        binding.btnSubmit.setOnClickListener {
            AwesomeDialog.build(this)
                .title("Are You Sure?")
                .icon(R.drawable.icon_gofit)
                .onNegative("No") {

                }
                .onPositive("Yes") {
                    addBookingClass()
                }
                .position(AwesomeDialog.POSITIONS.CENTER)
        }

        binding.dropDownClassRunning.onItemSelectedListener = object : AdapterView.OnItemSelectedListener {
            override fun onItemSelected(parent: AdapterView<*>, view: View?, position: Int, id: Long) {
                selectedIdClassRunning= idListClassRunning[position]
                // Use the selected ID for further processing
            }

            override fun onNothingSelected(parent: AdapterView<*>) {
                // Handle the case where nothing is selected
            }
        }

        binding.dropDownClassRunning.setOnTouchListener { _, event ->
            if (event.action == MotionEvent.ACTION_UP) {
                // Menggeser posisi dropdown ke kiri sejauh 16dp
                val dropdownOffset = 20 * resources.displayMetrics.density // Mengubah dp menjadi px
                binding.dropDownClassRunning.dropDownHorizontalOffset = -dropdownOffset.toInt()
            }
            false
        }
    }

    private fun addBookingClass(){
        binding.layoutClassRunning.error = null

        val stringRequest: StringRequest = object :
            StringRequest(Method.POST, Api.POST_CLASS_BOOKING_URL, Response.Listener { response ->
                Log.d("bookingClass","berhasil add booking class")

                val jsonObject = JSONObject(response)
                val message = jsonObject.getString("message")
                Toast.makeText(this, message, Toast.LENGTH_SHORT).show()

//                kalau sudah add bakal balik
                val bundle = Bundle()
                bundle.putString("key", "pindahBookingClass")
                moveHome?.putExtra("keyBundle", bundle)
                startActivity(moveHome)
                this.finish()

            }, Response.ErrorListener { error ->
                Log.d("bookingClass","erorr add booking class")
                val responseBody =
                    String(error.networkResponse.data, StandardCharsets.UTF_8)
                val jsonObject = JSONObject(responseBody)

                if(error.networkResponse.statusCode == 422){
                    Toast.makeText(this, "Must Have a value", Toast.LENGTH_SHORT).show()
                }else if(error.networkResponse.statusCode == 409){
                    val message = jsonObject.getString("message")
                    Toast.makeText(this, message, Toast.LENGTH_SHORT).show()
                }
            }) {
            @Throws(AuthFailureError::class)
            override fun getHeaders(): Map<String, String> {
                val headers = HashMap<String, String>()
                headers["Accept"] = "application/json"
                headers["Authorization"] = "$tokenType $accessToken"
                return headers
            }

            override fun getParams(): Map<String, String> {
                val params = HashMap<String, String>()
                params["id_class_running"] = selectedIdClassRunning.toString()
                params["id_member"] = idMember.toString()
                return params
            }

        }
        queue!!.add(stringRequest)
    }

    private fun getClassRunning(){
        val stringRequest: StringRequest = object :
            StringRequest(Method.GET, Api.GET_CLASS_RUNNING_URL, Response.Listener { response ->
                // Process the API response here
                try {
                    val jsonObject = JSONObject(response)
                    val dataArray = jsonObject.getJSONArray("data")

                    val nameList = mutableListOf<String>()

                    for (i in 0 until dataArray.length()) {
                        val dataObject = dataArray.getJSONObject(i)
                        val id = dataObject.getInt("id")
                        val day_name = dataObject.getString("day_name")
                        val startClass = convertTimeTo12HourFormat(dataObject.getJSONObject("jadwal_umum").getString("start_class"))
                        val classDetail = dataObject.getJSONObject("jadwal_umum").getJSONObject("class_detail")
                        val name = classDetail.getString("name") + '-' + day_name + '-' + startClass

                        idListClassRunning.add(id)
                        nameList.add(name)
                    }

                    val adapter = ArrayAdapter(this, android.R.layout.simple_spinner_item, nameList)
                    adapter.setDropDownViewResource(android.R.layout.simple_spinner_dropdown_item)
                    binding.dropDownClassRunning.adapter = adapter


                } catch (e: JSONException) {
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

    @Deprecated("Deprecated in Java")
    override fun onBackPressed() {
        super.onBackPressed()
        // Navigate back to HomeActivity
        val bundle = Bundle()
        bundle.putString("key", "pindahBookingClass")
        moveHome?.putExtra("keyBundle", bundle)
        startActivity(moveHome)
        finish()
    }
}