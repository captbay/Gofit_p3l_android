package com.example.gofit_p3l.Api

class Api {
    companion object{
        private const val BASE_URL = "http://192.168.100.175:8000/api/" //kontrakan

        const val LOGIN_URL_USER = BASE_URL + "login"
        const val LOGOUT_URL_USER = BASE_URL + "logout"

        const val GET_USER_URL_LOGIN = BASE_URL + "users"
        const val UPDATEPASS_URL_USER = BASE_URL + "users/updatePassword"

    }
}