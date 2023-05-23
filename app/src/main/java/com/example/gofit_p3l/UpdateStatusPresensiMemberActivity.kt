package com.example.gofit_p3l

import android.content.Context
import android.content.Intent
import android.content.SharedPreferences
import androidx.appcompat.app.AppCompatActivity
import android.os.Bundle
import android.util.Log
import android.view.View
import android.widget.AdapterView
import android.widget.ArrayAdapter
import android.widget.Spinner
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
import com.example.gofit_p3l.databinding.ActivityUpdateStartClassBinding
import com.example.gofit_p3l.databinding.ActivityUpdateStatusPresensiMemberBinding
import org.json.JSONObject
import java.nio.charset.StandardCharsets

class UpdateStatusPresensiMemberActivity : AppCompatActivity() {
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
    private lateinit var binding: ActivityUpdateStatusPresensiMemberBinding

    //buat kembali ke home
    var moveHome: Intent? = null

    //init other?
    private var selectedHadirOrNot: Int = 0
    private var selectedIdClassBooking: Int = -1

    //dropdown
    private lateinit var dropdownSpinner: Spinner

    override fun onCreate(savedInstanceState: Bundle?) {
        supportActionBar?.hide()
        super.onCreate(savedInstanceState)

        queue = Volley.newRequestQueue(this)
        //ambil data dari cookies
        sharedPreferences = getSharedPreferences(myPreference, Context.MODE_PRIVATE)
        tokenType = sharedPreferences!!.getString(tokenTypePref, "").toString()
        accessToken = sharedPreferences!!.getString(accessTokenPref, "").toString()
        username = sharedPreferences!!.getString(usernamePref, "").toString()

        binding = ActivityUpdateStatusPresensiMemberBinding.inflate(layoutInflater)
        val view = binding.root

        setContentView(view)

        val bundle = intent.getBundleExtra("keyBundle")
        if (bundle != null) {
            selectedIdClassBooking = bundle.getInt("key",-1)
        }

        moveHome = Intent(this, PresensiMemberByClassActivity::class.java)


        binding.btnBack.setOnClickListener {
//            startActivity(moveHome)
            this.finish()
        }

        binding.btnSubmit.setOnClickListener {
            AwesomeDialog.build(this)
                .title("Are You Sure?")
                .icon(R.drawable.icon_gofit)
                .onNegative("No") {

                }
                .onPositive("Yes") {
                    updatePresensi()
                }
                .position(AwesomeDialog.POSITIONS.CENTER)
        }

        val adapter = ArrayAdapter.createFromResource(
            this,
            R.array.dropdown_options,
            android.R.layout.simple_spinner_item
        )

        // Specify the layout to use when the list of choices appears
        adapter.setDropDownViewResource(android.R.layout.simple_spinner_dropdown_item)

        // Apply the adapter to the spinner
        binding.dropDownHadirOrNot.adapter = adapter

        // Set an item selection listener for the spinner
        binding.dropDownHadirOrNot.onItemSelectedListener = object : AdapterView.OnItemSelectedListener {
            override fun onItemSelected(
                parent: AdapterView<*>?,
                view: View?,
                position: Int,
                id: Long
            ) {
                val selectedItem = parent?.getItemAtPosition(position).toString()
                selectedHadirOrNot = if (selectedItem == "Hadir") 1 else 0
                Log.d("HADIRORNOT",selectedHadirOrNot.toString())
                // Process the value (1 or 0) as per your requirement
            }

            override fun onNothingSelected(parent: AdapterView<*>?) {
                // Do nothing
            }
        }

    }

    private fun updatePresensi(){

        val stringRequest: StringRequest = object :
            StringRequest(Method.POST, Api.POST_UPDATE_PRESENSI_MEMBER_URL + selectedIdClassBooking.toString(), Response.Listener { response ->
                Log.d("task","berhasil update")

                val jsonObject = JSONObject(response)
                val message = jsonObject.getString("message")
                Toast.makeText(this, message, Toast.LENGTH_SHORT).show()

//                kalau sudah add bakal balik
                this.finish()

            }, Response.ErrorListener { error ->
                Log.d("text","erorr update")
                val responseBody =
                    String(error.networkResponse.data, StandardCharsets.UTF_8)
                val jsonObject = JSONObject(responseBody)

                if(error.networkResponse.statusCode == 422){
                    Toast.makeText(this, "Must Have a value", Toast.LENGTH_SHORT).show()
//                    Toast.makeText(this, jsonObject.toString(), Toast.LENGTH_SHORT).show()
                }else if(error.networkResponse.statusCode == 409){
                    val message = jsonObject.getString("message")
                    Toast.makeText(this, message, Toast.LENGTH_SHORT).show()
                }else{
                    Toast.makeText(this, jsonObject.toString(), Toast.LENGTH_SHORT).show()
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
                params["status"] = selectedHadirOrNot.toString()
                return params
            }

        }
        queue!!.add(stringRequest)
    }

    override fun onBackPressed() {
        super.onBackPressed()
        // Navigate back to HomeActivity
//        startActivity(moveHome)
        this.finish()
    }
}