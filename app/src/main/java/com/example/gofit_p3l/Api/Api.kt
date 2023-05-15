package com.example.gofit_p3l.Api

class Api {
    companion object{
        private const val BASE_URL = "http://192.168.100.175:8000/api/" //kontrakan

        const val LOGIN_URL_USER = BASE_URL + "login"
        const val LOGOUT_URL_USER = BASE_URL + "logout"

        const val GET_USER_URL_LOGIN = BASE_URL + "users"
        const val UPDATEPASS_URL_USER = BASE_URL + "users/updatePassword"

//        intruktur izin
        const val POST_IZIN_URL = BASE_URL + "instruktur_izin"
        const val GET_IZIN_BY_USERNAME = BASE_URL + "instrukturIzin/byUsername/"

//        intruktur
        const val GET_INSTRUKTUR_URL = BASE_URL + "instruktur"

//        classrunning
        const val GET_CLASS_RUNNING_URL = BASE_URL + "class_running"
        const val GET_CLASS_RUNNING_BY_ID_INSTRUKTUR_URL = BASE_URL +"class_running/byIdInstruktur/"

//        class booking
        const val POST_CLASS_BOOKING_URL = BASE_URL + "class_booking"
        const val DELETE_CLASS_BOOKING_URL = BASE_URL + "class_booking/"
        const val GET_CLASS_BOOKING_BY_ID_MEMBER_URL = BASE_URL + "class_booking/byIdMember/"
    }
}