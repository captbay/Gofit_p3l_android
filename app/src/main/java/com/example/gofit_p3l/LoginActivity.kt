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
import com.example.gofit_p3l.User.Instruktur
import com.example.gofit_p3l.User.Member
import com.example.gofit_p3l.User.Mo

class LoginActivity : AppCompatActivity() {
    private lateinit var binding: ActivityLoginBinding

    private val myPreference = "myPref"
    private val idPref = "idKey"
    private val usernamePref = "usernameKey"
    private val fullnamePref = "fullnameKey"
    private val idMemberPref = "idMemberKey"
    private val idInstrukturPref = "idInstrukturKey"
    private val idMoPref = "idMoKey"
    private val rolePref= "roleKey"
    private val tokenTypePref = "token_typeKey"
    private val accessTokenPref = "access_tokenKey"

    var sharedPreferences: SharedPreferences? = null
    private var queue: RequestQueue? = null
    var moveHomeMember : Intent? = null
    var moveHomeInstruktur : Intent? = null
    var moveHomeMo : Intent? = null
    var moveSchedule : Intent? = null
    private var role: String = ""

    override fun onCreate(savedInstanceState: Bundle?) {
        supportActionBar?.hide()
        super.onCreate(savedInstanceState)

        queue = Volley.newRequestQueue(this)

        sharedPreferences = getSharedPreferences(myPreference, Context.MODE_PRIVATE)
        role = sharedPreferences!!.getString(rolePref, "").toString()

        moveHomeMember = Intent(this, HomeMemberActivity::class.java)
        moveHomeInstruktur = Intent(this, HomeInstrukturActivity::class.java)
        moveHomeMo = Intent(this, HomeMoActivity::class.java)
//        move to shedule without login
        moveSchedule = Intent(this, ScheduleActivity::class.java)

//        membuat kalo udah login ga harus login terus
        if(role == "instruktur"){
            startActivity(moveHomeInstruktur)
            finish()
        }else if (role == "member"){
            startActivity(moveHomeMember)
            finish()
        }else if (role == "mo"){
            startActivity(moveHomeMo)
            finish()
        }else{
            binding = ActivityLoginBinding.inflate(layoutInflater)
            val view = binding.root
//            selalu nampilin splash screen
            setContentView(R.layout.activity_splash_screen)
            Handler(Looper.getMainLooper()).postDelayed({
                setContentView(view)
            }, 1000)

            binding.btnLogin.setOnClickListener {

                Login()

            }

            binding.btnShowClass.setOnClickListener{
                startActivity(moveSchedule)
            }
        }
    }

    private fun Login(){
        binding.layoutUsername.error = null
        binding.layoutPassword.error = null

        val stringRequest: StringRequest = object :
            StringRequest(Method.POST, Api.LOGIN_URL_USER, Response.Listener { response ->
                Log.d("Login","berhasil login")

//              get Response
                val gson = Gson()
                val jsonObject = JSONObject(response)
//                get users
                val jsonArrayUser = jsonObject.getJSONObject("user")
                val user = gson.fromJson(jsonArrayUser.toString(), User::class.java)
//                get token
                val accessToken = jsonObject.getString("access_token")
                val tokenType = jsonObject.getString("token_type")
//                get role

                when (val role = jsonObject.getString("role")) {
                    "member" -> {
                        //                get data diri member
                        val jsonArrayMember = jsonObject.getJSONObject("member")
                        val member = gson.fromJson(jsonArrayMember.toString(), Member::class.java)

                        val editor: SharedPreferences.Editor = sharedPreferences!!.edit()
                        editor.putInt(idPref, user.id)
                        editor.putString(usernamePref, user.username)
                        editor.putString(fullnamePref, member.name)
                        editor.putInt(idMemberPref, member.id)
                        editor.putString(accessTokenPref, accessToken)
                        editor.putString(tokenTypePref, tokenType)
                        editor.putString(rolePref, role)
                        editor.apply()

                        startActivity(moveHomeMember)
                        //buat ga balik lagi ke login, kalau dari home balik jadinya selesai app
                        finish()
                    }
                    "instruktur" -> {
                        //                get data diri instruktur
                        val jsonArrayInstruktur = jsonObject.getJSONObject("instruktur")
                        val instruktur = gson.fromJson(jsonArrayInstruktur.toString(), Instruktur::class.java)

                        val editor: SharedPreferences.Editor = sharedPreferences!!.edit()
                        editor.putInt(idPref, user.id)
                        editor.putString(usernamePref, user.username)
                        editor.putString(fullnamePref, instruktur.name)
                        editor.putInt(idInstrukturPref, instruktur.id)
                        editor.putString(accessTokenPref, accessToken)
                        editor.putString(tokenTypePref, tokenType)
                        editor.putString(rolePref, role)
                        editor.apply()

                        startActivity(moveHomeInstruktur)
                        finish()
                    }
                    "mo" -> {
                        //                get data diri pegawai mo
                        val jsonArrayMo = jsonObject.getJSONObject("pegawai")
                        val mo = gson.fromJson(jsonArrayMo.toString(), Mo::class.java)

                        val editor: SharedPreferences.Editor = sharedPreferences!!.edit()
                        editor.putInt(idPref, user.id)
                        editor.putString(usernamePref, user.username)
                        editor.putString(fullnamePref, mo.name)
                        editor.putInt(idMoPref, mo.id)
                        editor.putString(accessTokenPref, accessToken)
                        editor.putString(tokenTypePref, tokenType)
                        editor.putString(rolePref, role)
                        editor.apply()

                        startActivity(moveHomeMo)
                        finish()
                    }
                    else -> {
                        AwesomeDialog.build(this)
                            .title("Restrict Access")
                            .body("Please Contact Developer!")
                            .icon(R.drawable.icon_gofit)
                            .onNegative("OK") {

                            }
                            .position(AwesomeDialog.POSITIONS.CENTER)
                    }
                }

            }, Response.ErrorListener { error ->
                try {
                    Log.d("Login","berhasil masuk try")
//                    dibawah ini error
                    val responseBody =
                        String(error.networkResponse.data, StandardCharsets.UTF_8)
                    Log.d("Login","berhasil masuk try 2")
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
                        binding.layoutUsername.error = "Username Salah"
                        binding.layoutPassword.error = "Password Salah"
                        Toast.makeText(this, message, Toast.LENGTH_SHORT).show()
                    }else{
                        val errors = JSONObject(responseBody)
                        Toast.makeText(this, errors.getString("message"), Toast.LENGTH_SHORT).show()
                    }

                } catch (e: Exception) {
                    Log.d("Login","berhasil masuk error")
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