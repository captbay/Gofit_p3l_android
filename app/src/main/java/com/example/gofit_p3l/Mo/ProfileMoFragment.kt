package com.example.gofit_p3l.Mo

import android.content.Context
import android.content.Intent
import android.content.SharedPreferences
import android.os.Bundle
import android.util.Log
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
import com.example.gofit_p3l.HomeInstrukturActivity
import com.example.gofit_p3l.HomeMoActivity
import com.example.gofit_p3l.LoginActivity
import com.example.gofit_p3l.R
import com.example.gofit_p3l.UpdatePasswordActivity
import com.example.gofit_p3l.databinding.FragmentProfileMoBinding
import com.google.android.material.dialog.MaterialAlertDialogBuilder
import org.json.JSONObject
import java.nio.charset.StandardCharsets

class ProfileMoFragment : Fragment() {
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

    private var _binding: FragmentProfileMoBinding? = null
    private val binding get() = _binding!!
    private var queue: RequestQueue? = null

    private var layoutLoading: ConstraintLayout? = null

    override fun onCreateView(
        inflater: LayoutInflater, container: ViewGroup?,
        savedInstanceState: Bundle?
    ): View {
        _binding = FragmentProfileMoBinding.inflate(inflater, container, false)
        return binding.root
    }

    override fun onViewCreated(view: View, savedInstanceState: Bundle?) {
        super.onViewCreated(view, savedInstanceState)
        //attach this fragment to activity
        val activity = activity as HomeMoActivity

        layoutLoading = view.findViewById(R.id.layout_loading_mo)

        val btnLogOut  = binding.btnLogout

        queue = Volley.newRequestQueue(requireActivity())
        sharedPreferences = activity.getSharedPreferences(myPreference, Context.MODE_PRIVATE)
        tokenType = sharedPreferences!!.getString(tokenTypePref,"").toString()
        accessToken = sharedPreferences!!.getString(accessTokenPref,"").toString()
        username = sharedPreferences!!.getString(usernamePref, "").toString()

        //set nama aja dulu sementara hapus ini nanti ya
        //nanti pake fungsi cari siapa yang login
        binding.nomor.text = username
        binding.username.text = sharedPreferences!!.getString(fullnamePref,"").toString()

        btnLogOut.setOnClickListener {
            activity?.let { it1 ->
                MaterialAlertDialogBuilder(it1, R.style.CustomAlertDialogTheme)
                    .setTitle("Apakah Anda Ingin Keluar ?")
                    .setNegativeButton("No") { dialog, which ->
                        dialog.dismiss()
                    }
                    .setPositiveButton("Yes") { dialog, which ->
                        logout()
                        sharedPreferences?.edit()?.clear()?.apply()
                        val moveLogin = Intent(activity, LoginActivity::class.java)
                        startActivity(moveLogin)
                        activity?.finish()
                    }
                    .show()

//                AwesomeDialog.build(it1)
//                    .title("Are you sure want to logout?")
//                    .icon(R.drawable.icon_gofit)
//                    .onNegative("No") {
//
//                    }
//                    .onPositive("Yes"){
//                        logout()
//                        sharedPreferences?.edit()?.clear()?.apply()
//                        val moveLogin = Intent(activity, LoginActivity::class.java)
//                        startActivity(moveLogin)
//                        activity?.finish()
//                    }
//                    .position(AwesomeDialog.POSITIONS.CENTER)
            }
        }

        binding.btnChangePassword.setOnClickListener {
            val moveUpdatePassword = Intent(activity, UpdatePasswordActivity::class.java)
            val bundle = Bundle()
            bundle.putString("key", "mo")
            moveUpdatePassword.putExtra("keyBundle", bundle)
            startActivity(moveUpdatePassword)
            activity?.finish()
        }

    }

    private fun logout(){
        val stringRequest: StringRequest = object :
            StringRequest(Method.POST, Api.LOGOUT_URL_USER, Response.Listener { response ->
                Log.d("Logout","berhasil logout")
            }, Response.ErrorListener { error ->
                Log.d("Logout","erorr logout")
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

    override fun onStart() {
        super.onStart()
        getUserLogin()
    }

    private fun getUserLogin(){
        setLoading(true)
        val stringRequest : StringRequest = object:
            StringRequest(Method.GET, Api.GET_USER_URL_LOGIN, Response.Listener { response ->

                val jsonObject = JSONObject(response)
                val jsonArray = jsonObject.getJSONObject("dataDiri")

                binding.address.text = jsonArray.getString("address")
                binding.numberPhone.text =jsonArray.getString("number_phone")
                binding.bornDate.text = jsonArray.getString("born_date")
                binding.gender.text =jsonArray.getString("gender")
                if (jsonArray.getString("role") == "mo"){
                    binding.role.text = "Manager Operasional"
                }else{
                    binding.role.text = jsonArray.getString("role")
                }

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