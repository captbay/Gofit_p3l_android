package com.example.gofit_p3l.Api

class Api {
    companion object{
//        private const val BASE_URL = "http://192.168.100.175:8000/api/" //kontrakan
//        private const val BASE_URL = "http://192.168.92.246:8000/api/" //kampus
//        private const val BASE_URL = "http://172.20.10.3:8000/api/" // hotspot ages iphone
//        private const val BASE_URL = "http://192.168.50.57:8000/api/" // kopi layr sentuh
        private const val BASE_URL = "https://ages.ppcdeveloper.com/api/" // final api

        const val LOGIN_URL_USER = BASE_URL + "login"
        const val LOGOUT_URL_USER = BASE_URL + "logout"

        const val GET_USER_URL_LOGIN = BASE_URL + "users"
        const val UPDATEPASS_URL_USER = BASE_URL + "users/updatePassword"

//        deposit package
        const val GET_COUNT_DEPOSIT_PACKAGE_BY_ID_MEMBER_URL = BASE_URL + "deposit_package/countPackageByIdMember/"
        const val GET_DEPOSIT_PACKAGE_BY_ID_MEMBER_URL = BASE_URL + "deposit_package/byIdMember/"

//        intruktur izin
        const val POST_IZIN_URL = BASE_URL + "instruktur_izin"
        const val GET_IZIN_BY_USERNAME = BASE_URL + "instrukturIzin/byUsername/"

//        intruktur
        const val GET_INSTRUKTUR_URL = BASE_URL + "instruktur"

//        classrunning
        const val GET_CLASS_RUNNING_URL = BASE_URL + "class_running"
        const val GET_CLASS_RUNNING_BY_ID_INSTRUKTUR_URL = BASE_URL +"class_running/byIdInstruktur/"

//        GYM
        const val GET_GYM_URL = BASE_URL + "gym"

//        gym booking
        const val POST_GYM_BOOKING_URL = BASE_URL + "gym_booking"
        const val GET_GYM_BOOKING_BY_ID_MEMBER_URL = BASE_URL + "gym_booking/byIdMember/"
        const val DELETE_GYM_BOOKING_URL = BASE_URL + "gym_booking/"

//        class booking
        const val POST_CLASS_BOOKING_URL = BASE_URL + "class_booking"
        const val DELETE_CLASS_BOOKING_URL = BASE_URL + "class_booking/"
        const val GET_CLASS_BOOKING_BY_ID_MEMBER_URL = BASE_URL + "class_booking/byIdMember/"
        const val GET_CLASS_BOOKING_BY_INSTRUKTUR_LOGIN_CLASS_RUNNING_SELECTED_URL = BASE_URL + "class_booking/instrukturLogin/"

//        presensi instruktur
        const val GET_PRESENSI_INSTRUKTUR_URL = BASE_URL + "instruktur_presensi"
        const val POST_UPDATE_START_CLASS_URL = BASE_URL + "instruktur_presensi/startClassUpdate/"
        const val POST_UPDATE_END_CLASS_URL = BASE_URL + "instruktur_presensi/endClassUpdate/"

//        Presensi Member
        const val POST_UPDATE_PRESENSI_MEMBER_URL = BASE_URL + "class_booking/presensiMember/"

//        member activities
        const val GET_MEMBER_ACTIVITIES_BY_ID_MEMBER_URL = BASE_URL + "member_activity/byIdMember/"

//        instruktur activities
        const val GET_INSTRUKTUR_ACTIVITIES_BY_ID_INSTRUKTUR_URL = BASE_URL + "instruktur_activity/byIdInstruktur/"
    }
}