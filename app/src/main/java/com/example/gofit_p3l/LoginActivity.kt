package com.example.gofit_p3l

import android.content.Context
import android.content.Intent
import android.content.SharedPreferences
import androidx.appcompat.app.AppCompatActivity
import android.os.Bundle
import android.os.Handler
import android.os.Looper
import android.util.Log
import android.widget.Toast
import com.android.volley.RequestQueue
import com.android.volley.toolbox.Volley
import com.android.volley.AuthFailureError
import com.android.volley.Response
import com.android.volley.toolbox.StringRequest
import com.example.awesomedialog.AwesomeDialog
import com.example.gofit_p3l.Api.Api
import com.example.gofit_p3l.User.User
import com.example.gofit_p3l.databinding.ActivityLoginBinding
import com.google.gson.Gson
import org.json.JSONObject
import java.nio.charset.StandardCharsets
import com.example.awesomedialog.*
import com.google.android.material.textfield.TextInputEditText

class LoginActivity : AppCompatActivity() {
    private lateinit var binding: ActivityLoginBinding
    lateinit var lBundle : Bundle

    private val myPreference = "myPref"
    private val key = "nameKey"
    private val id = "idKey"
    private val name = "nameKey"
    private var access = false
    var sharedPreferences: SharedPreferences? = null
    private var queue: RequestQueue? = null
    var moveHome : Intent? = null

    override fun onCreate(savedInstanceState: Bundle?) {
        supportActionBar?.hide()
        super.onCreate(savedInstanceState)

        queue = Volley.newRequestQueue(this)

        sharedPreferences = getSharedPreferences(myPreference, Context.MODE_PRIVATE)
        binding = ActivityLoginBinding.inflate(layoutInflater)
        val view = binding.root

        setContentView(R.layout.activity_splash_screen)

        Handler(Looper.getMainLooper()).postDelayed({
            setContentView(view)
        }, 2000)

//        kalo mau isi terus untuk splash screen tolong hapusin key nya
//        if(!sharedPreferences!!.contains(key)){
//            val editor: SharedPreferences.Editor = sharedPreferences!!.edit()
//            editor.putString(key, "terisi")
//            editor.apply()
//            setContentView(R.layout.activity_splash_screen)
//
//            Handler(Looper.getMainLooper()).postDelayed({
//                setContentView(view)
//            }, 3000)
//        }else{
//            setContentView(view)
//        }

        moveHome = Intent(this, HomeActivity::class.java)
//        if(intent.getBundleExtra("registerBundle")!=null){
//            lBundle = intent.getBundleExtra("registerBundle")!!
//            binding.inputUsername.setText(lBundle.getString("username"))
//            binding.inputPassword.setText(lBundle.getString("password"))
//        }

        binding.btnLogin.setOnClickListener {

            getUser()

        }
    }

    private fun getUser(){
        binding.layoutUsername.error = null
        binding.layoutPassword.error = null

        val stringRequest: StringRequest = object :
            StringRequest(Method.POST, Api.LOGIN_URL_USER, Response.Listener { response ->
                Log.d("getUser","berhasil login")
                val gson = Gson()

                val jsonObject = JSONObject(response)
                val jsonArray = jsonObject.getJSONObject("user")
                val user = gson.fromJson(jsonArray.toString(), User::class.java)
                val token = jsonObject.getString("access_token")
                val role = jsonObject.getString("role")

                if(role == "admin" || role == "kasir"){
                    AwesomeDialog.build(this)
                        .title("Restrict Access")
                        .body("Please Contact Developer!")
                        .icon(R.drawable.icon_gofit)
                        .onNegative("OK") {

                        }
                        .position(AwesomeDialog.POSITIONS.CENTER)
                }else{
                    val editor: SharedPreferences.Editor = sharedPreferences!!.edit()
                    editor.putString(id, user.id.toString())
                    editor.putString(name, user.username)
                    editor.putString("tokenKey", token)
                    editor.putString("roleKey", role)
                    editor.apply()

                    startActivity(moveHome)
                    finish()
                }
            }, Response.ErrorListener { error ->
                try {
                    Log.d("getUser","berhasil masuk try")
//                    dibawah ini error
                    val responseBody =
                        String(error.networkResponse.data, StandardCharsets.UTF_8)
                    Log.d("getUser","berhasil masuk try 2")
                    val jsonObject = JSONObject(responseBody)
                    val message = jsonObject.getString("message")

                    if(error.networkResponse.statusCode == 409){
                        if(message == "Login Failed Because Membership is expired or not active, Please Contact Cashier"){
                            AwesomeDialog.build(this)
                                .title("Not Member Active")
                                .body("Login Failed Because Membership is expired or not active, Please Contact Cashier")
                                .icon(R.drawable.icon_gofit)
                                .onNegative("OK") {

                                }
                                .position(AwesomeDialog.POSITIONS.CENTER)


                        }else{
                            binding.layoutPassword.setError("Password Salah")
                        }

                    }else if(error.networkResponse.statusCode == 404){
                        binding.layoutUsername.setError("Username Salah")
                        binding.layoutPassword.setError("Password Salah")
                        Toast.makeText(this, message, Toast.LENGTH_SHORT).show()
                    }else{
                        val errors = JSONObject(responseBody)
                        Toast.makeText(this, errors.getString("message"), Toast.LENGTH_SHORT).show()
                    }

                } catch (e: Exception) {
                    Log.d("getUser","berhasil masuk error")
                    Toast.makeText(this, e.message, Toast.LENGTH_SHORT).show()
                }
            }) {
            @Throws(AuthFailureError::class)
            override fun getHeaders(): Map<String, String> {
                val headers = HashMap<String, String>()
                headers["Accept"] = "application/json"
                return headers
            }

            override fun getParams(): Map<String, String> {
                val params = HashMap<String, String>()
                params["username"] = binding.inputUsername.text.toString()
                params["password"] = binding.inputPassword.text.toString()
                return params
            }

        }
        queue!!.add(stringRequest)
    }
}