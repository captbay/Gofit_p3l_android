package com.example.gofit_p3l.Api

class Api {
    companion object{
        val BASE_URL = "http://192.168.100.175:8000/api/"

        val LOGIN_URL_USER = BASE_URL + "login/"
        val GET_USER_LOGIN = BASE_URL + "users"

    }
}