package com.example.gofit_p3l

import android.content.Context
import android.content.Intent
import android.content.SharedPreferences
import android.os.Bundle
import android.util.Log
import android.widget.Toast
import androidx.appcompat.app.AppCompatActivity
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
import com.example.gofit_p3l.databinding.ActivityUpdatePasswordBinding
import org.json.JSONObject
import java.nio.charset.StandardCharsets

class UpdatePasswordActivity : AppCompatActivity() {
    //buat cookies
    private val myPreference = "myPref"
    private val idPref = "idKey"
    private val usernamePref = "usernameKey"
    private val fullnamePref = "fullnameKey"
    private val rolePref= "roleKey"
    private val tokenTypePref = "token_typeKey"
    private val accessTokenPref = "access_tokenKey"
    var sharedPreferences: SharedPreferences? = null

    //untuk naruh di header setap connect ke back end soalnya udah login
    private var username: String = ""
    private var tokenType: String = ""
    private var accessToken: String = ""

    //buat request
    private var queue: RequestQueue? = null

    //buat ambil binding xml
    private lateinit var binding: ActivityUpdatePasswordBinding

    //buat kembali ke instruktur atau mo
    var moveHome: Intent? = null

    override fun onCreate(savedInstanceState: Bundle?) {
        supportActionBar?.hide()
        super.onCreate(savedInstanceState)

        queue = Volley.newRequestQueue(this)
        //ambil data dari cookies
        sharedPreferences = getSharedPreferences(myPreference, Context.MODE_PRIVATE)
        tokenType = sharedPreferences!!.getString(tokenTypePref,"").toString()
        accessToken = sharedPreferences!!.getString(accessTokenPref,"").toString()
        username = sharedPreferences!!.getString(usernamePref,"").toString()

        binding = ActivityUpdatePasswordBinding.inflate(layoutInflater)
        val view = binding.root

        setContentView(view)

        val bundleUP = intent.getBundleExtra("keyBundle")
        if(intent.getBundleExtra("keyBundle")!=null){
            if(bundleUP?.getString("key","")=="instruktur"){
                moveHome = Intent(this, HomeInstrukturActivity::class.java)
            }else{
                moveHome = Intent(this, HomeMoActivity::class.java)
            }
        }

        binding.btnBack.setOnClickListener {
            val bundle = Bundle()
            bundle.putString("key", "pindahProfile")
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
                .onPositive("Yes"){
                    updatePassword()
                }
                .position(AwesomeDialog.POSITIONS.CENTER)
        }
    }

    private fun updatePassword(){
        binding.layoutPasswordNew.error = null
        binding.layoutPasswordOld.error = null

        val stringRequest: StringRequest = object :
            StringRequest(Method.POST, Api.UPDATEPASS_URL_USER, Response.Listener { response ->
                Log.d("Logout","berhasil ganti password")

                val jsonObject = JSONObject(response)
                val message = jsonObject.getString("message")
                Toast.makeText(this, message, Toast.LENGTH_SHORT).show()

                val bundle = Bundle()
                bundle.putString("key", "pindahProfile")
                moveHome?.putExtra("keyBundle", bundle)
                startActivity(moveHome)
                this.finish()

            }, Response.ErrorListener { error ->
                Log.d("Logout","erorr ganti password")
                val responseBody =
                    String(error.networkResponse.data, StandardCharsets.UTF_8)
                val jsonObject = JSONObject(responseBody)

                if(error.networkResponse.statusCode == 422){
                    binding.layoutPasswordNew.error = "This input must have a value"
                    binding.layoutPasswordOld.error = "This input must have a value"
                }else if(error.networkResponse.statusCode == 409){
                    val message = jsonObject.getString("message")
                    binding.layoutPasswordOld.error = "Old Password False"
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
                params["username"] = username
                params["passwordOld"] = binding.inputPasswordOld.text.toString()
                params["passwordNew"] = binding.inputPasswordNew.text.toString()
                return params
            }

        }
        queue!!.add(stringRequest)
    }

    override fun onBackPressed() {
        super.onBackPressed()
        // Navigate back to HomeActivity
        val bundle = Bundle()
        bundle.putString("key", "pindahProfile")
        moveHome?.putExtra("keyBundle", bundle)
        startActivity(moveHome)
        finish()
    }

}