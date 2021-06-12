package com.app.bestbus.models

class Ticket {
    val id = 0
//    val userId = 0
    val name: String? = null
    val email: String? = null
    val phone: String? = null
//    val tourId = 0
    val date: String? = null
    val seatList = ArrayList<Int>()
    val paymentMethod: String? = null
//    val paymentInformation: String? = null
    val totalAmount = 0F
    val qrCode: String? = null
    val tourData: Tour? = null
}