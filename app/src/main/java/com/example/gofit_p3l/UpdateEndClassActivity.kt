package com.example.gofit_p3l

import android.app.TimePickerDialog
import android.content.Context
import android.content.Intent
import android.content.SharedPreferences
import androidx.appcompat.app.AppCompatActivity
import android.os.Bundle
import android.util.Log
import android.widget.TimePicker
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
import com.example.gofit_p3l.databinding.ActivityUpdateEndClassBinding
import org.json.JSONObject
import java.nio.charset.StandardCharsets
import java.text.SimpleDateFormat
import java.util.Calendar
import java.util.Locale

class UpdateEndClassActivity : AppCompatActivity() {
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
    private lateinit var binding: ActivityUpdateEndClassBinding

    //buat kembali ke home
    var moveHome: Intent? = null

    //init other?
    private var selectedIdPresensiInstruktur: Int = -1
    private var selectedTimeBooking: String = ""

    override fun onCreate(savedInstanceState: Bundle?) {
        supportActionBar?.hide()
        super.onCreate(savedInstanceState)

        queue = Volley.newRequestQueue(this)
        //ambil data dari cookies
        sharedPreferences = getSharedPreferences(myPreference, Context.MODE_PRIVATE)
        tokenType = sharedPreferences!!.getString(tokenTypePref, "").toString()
        accessToken = sharedPreferences!!.getString(accessTokenPref, "").toString()
        username = sharedPreferences!!.getString(usernamePref, "").toString()

        binding = ActivityUpdateEndClassBinding.inflate(layoutInflater)
        val view = binding.root

        setContentView(view)

        val bundle = intent.getBundleExtra("keyBundle")
        if (bundle != null) {
            selectedIdPresensiInstruktur = bundle.getInt("key",-1)
        }

        moveHome = Intent(this, HomeMoActivity::class.java)

        binding.btnBack.setOnClickListener {
            val bundle = Bundle()
            bundle.putString("key", "pindahPresensiInstruktur")
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
                    updateEndClass()
                }
                .position(AwesomeDialog.POSITIONS.CENTER)
        }

        binding.timePickerButton.setOnClickListener {
            showTimePickerDialog()
        }
    }

    private fun updateEndClass(){

        val stringRequest: StringRequest = object :
            StringRequest(Method.POST, Api.POST_UPDATE_END_CLASS_URL + selectedIdPresensiInstruktur.toString(), Response.Listener { response ->
                Log.d("presensiInstruktur","berhasil update")

                val jsonObject = JSONObject(response)
                val message = jsonObject.getString("message")
                Toast.makeText(this, message, Toast.LENGTH_SHORT).show()

//                kalau sudah add bakal balik
                val bundle = Bundle()
                bundle.putString("key", "pindahPresensiInstruktur")
                moveHome?.putExtra("keyBundle", bundle)
                startActivity(moveHome)
                this.finish()

            }, Response.ErrorListener { error ->
                Log.d("presensiInstruktur","erorr update")
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
                params["end_class"] = selectedTimeBooking
                return params
            }

        }
        queue!!.add(stringRequest)
    }

    private fun showTimePickerDialog() {
        val calendar = Calendar.getInstance()
        val hour = calendar.get(Calendar.HOUR_OF_DAY)
        val minute = calendar.get(Calendar.MINUTE)

        val timePickerDialog = TimePickerDialog(this,
            { _: TimePicker?, hourOfDay: Int, minute: Int ->
                val selectedTime = Calendar.getInstance()
                selectedTime.set(Calendar.HOUR_OF_DAY, hourOfDay)
                selectedTime.set(Calendar.MINUTE, minute)

                val timeFormat = SimpleDateFormat("HH:mm", Locale.getDefault())
                val formattedTime = timeFormat.format(selectedTime.time)
                //set
                binding.timePickerButton.text = formattedTime
                selectedTimeBooking = formattedTime
            }, hour, minute, true)

        timePickerDialog.show()
    }

    override fun onBackPressed() {
        super.onBackPressed()
        // Navigate back to HomeActivity
        val bundle = Bundle()
        bundle.putString("key", "pindahPresensiInstruktur")
        moveHome?.putExtra("keyBundle", bundle)
        startActivity(moveHome)
        this.finish()
    }
}