package com.example.gofit_p3l

import android.app.DatePickerDialog
import android.content.Context
import android.content.Intent
import android.content.SharedPreferences
import androidx.appcompat.app.AppCompatActivity
import android.os.Bundle
import android.util.Log
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
import com.example.gofit_p3l.databinding.ActivityAddBookingGymBinding
import org.json.JSONException
import org.json.JSONObject
import java.nio.charset.StandardCharsets
import java.text.SimpleDateFormat
import java.util.Calendar
import java.util.Locale

class AddBookingGymActivity : AppCompatActivity() {
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
    private lateinit var binding: ActivityAddBookingGymBinding

    //buat kembali ke home
    var moveHome: Intent? = null

    //init other?
    private var selectedIdGym: Int = -1
    private var selectedDateBooking: String = ""
    private val idListGym = mutableListOf<Int>()


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

        binding = ActivityAddBookingGymBinding.inflate(layoutInflater)
        val view = binding.root

        setContentView(view)

        moveHome = Intent(this, HomeMemberActivity::class.java)

        getGym()

        binding.btnBack.setOnClickListener {
            val bundle = Bundle()
            bundle.putString("key", "pindahBookingGym")
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
                    addBookingGym()
                }
                .position(AwesomeDialog.POSITIONS.CENTER)
        }

        binding.dropDownGym.onItemSelectedListener = object : AdapterView.OnItemSelectedListener {
            override fun onItemSelected(parent: AdapterView<*>, view: View?, position: Int, id: Long) {
                selectedIdGym= idListGym[position]
                // Use the selected ID for further processing
            }

            override fun onNothingSelected(parent: AdapterView<*>) {
                // Handle the case where nothing is selected
            }
        }

        binding.datePickerButton.setOnClickListener {
            showDatePickerDialog()
        }
    }

    private fun addBookingGym(){
        binding.layoutClassRunning.error = null

        val stringRequest: StringRequest = object :
            StringRequest(Method.POST, Api.POST_GYM_BOOKING_URL, Response.Listener { response ->
                Log.d("BookingGym","berhasil add booking gym")

                val jsonObject = JSONObject(response)
                val message = jsonObject.getString("message")
                Toast.makeText(this, message, Toast.LENGTH_SHORT).show()

//                kalau sudah add bakal balik
                val bundle = Bundle()
                bundle.putString("key", "pindahBookingGym")
                moveHome?.putExtra("keyBundle", bundle)
                startActivity(moveHome)
                this.finish()

            }, Response.ErrorListener { error ->
                Log.d("bookingGym","erorr add booking gym")
                val responseBody =
                    String(error.networkResponse.data, StandardCharsets.UTF_8)
                val jsonObject = JSONObject(responseBody)

                if(error.networkResponse.statusCode == 422){
                    Toast.makeText(this, "Must Have a value or date must after yesterday", Toast.LENGTH_SHORT).show()
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
                params["id_gym"] = selectedIdGym.toString()
                params["id_member"] = idMember.toString()
                params["date_booking"] = selectedDateBooking
                return params
            }

        }
        queue!!.add(stringRequest)
    }

    private fun getGym(){
        val stringRequest: StringRequest = object :
            StringRequest(Method.GET, Api.GET_GYM_URL, Response.Listener { response ->
                // Process the API response here
                try {
                    val jsonObject = JSONObject(response)
                    val dataArray = jsonObject.getJSONArray("data")

                    val nameList = mutableListOf<String>()

                    for (i in 0 until dataArray.length()) {
                        val dataObject = dataArray.getJSONObject(i)
                        val id = dataObject.getInt("id")
//                        val capacity = dataObject.getString("capacity")
                        val start_gym = convertTimeTo12HourFormat(dataObject.getString("start_gym"))
                        val end_gym = convertTimeTo12HourFormat(dataObject.getString("end_gym"))
                        val name = "Start $start_gym - End $end_gym"

                        idListGym.add(id)
                        nameList.add(name)
                    }

                    val adapter = ArrayAdapter(this, android.R.layout.simple_spinner_item, nameList)
                    adapter.setDropDownViewResource(android.R.layout.simple_spinner_dropdown_item)
                    binding.dropDownGym.adapter = adapter


                } catch (e: JSONException) {
                    e.printStackTrace()
                }

            }, Response.ErrorListener { error ->
                Log.d("get gym add booking","erorr add gym")
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

    private fun showDatePickerDialog() {
        // Get the current date
        val calendar = Calendar.getInstance()
        val year = calendar.get(Calendar.YEAR)
        val month = calendar.get(Calendar.MONTH)
        val day = calendar.get(Calendar.DAY_OF_MONTH)

        // Create a DatePickerDialog and set the initial date
        val datePickerDialog = DatePickerDialog(this, { _, selectedYear, selectedMonth, selectedDayOfMonth ->
            // Format the selected date as needed
            val selectedDate = "$selectedYear-${selectedMonth + 1}-$selectedDayOfMonth"
            selectedDateBooking = selectedDate
            binding.datePickerButton.text = selectedDate

        }, year, month, day)

        // Show the DatePickerDialog
        datePickerDialog.show()
    }

//    to get HH:mm only
    private fun convertTimeTo12HourFormat(time: String): String {
        val inputFormat = SimpleDateFormat("HH:mm:ss", Locale.getDefault())
        val outputFormat = SimpleDateFormat("hh:mm a", Locale.getDefault())
        val date = inputFormat.parse(time)
        return outputFormat.format(date)
    }

    override fun onBackPressed() {
        super.onBackPressed()
        // Navigate back to HomeActivity
        val bundle = Bundle()
        bundle.putString("key", "pindahBookingGym")
        moveHome?.putExtra("keyBundle", bundle)
        startActivity(moveHome)
        finish()
    }
}