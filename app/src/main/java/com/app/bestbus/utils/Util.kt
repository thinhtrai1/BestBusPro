package com.app.bestbus.utils

import java.text.ParseException
import java.util.*

object Util {

    @JvmStatic
    fun formatFloat(input: Float): String {
        return if (input == input.toInt().toFloat())
            input.toInt().toString()
        else
            String.format(Locale.US, "%.2f", input).toFloat().toString()
    }

    @JvmStatic
    fun getEndTime(startTime: String?, duration: Float): String? {
        return try {
            Constant.timeFormat.format(Calendar.getInstance().apply {
                time = Constant.timeFormat.parse(startTime ?: "")!!
                add(Calendar.MINUTE, (duration * 60).toInt())
            }.time)
        } catch (e: ParseException) {
            startTime
        }
    }


    @JvmStatic
    fun getEndDate(startDate: String?, startTime: String?, duration: Float): String? {
        return try {
            Constant.dateFormat.format(Calendar.getInstance().apply {
                time = Constant.dateFormat.parse(startDate ?: "")!!
                Calendar.getInstance().apply {
                    time = Constant.timeFormat.parse(startTime ?: "")!!
                }.let {
                    set(Calendar.HOUR, it.get(Calendar.HOUR_OF_DAY))
                    set(Calendar.MINUTE, it.get(Calendar.MINUTE))
                }
                add(Calendar.MINUTE, (duration * 60).toInt())
            }.time)
        } catch (e: ParseException) {
            startDate
        }
    }

    fun getTicketFileName(startDate: String?, startTime: String?, id: Int): String {
        return try {
            Calendar.getInstance().apply {
                time = Constant.dateFormat.parse(startDate ?: "")!!
                Calendar.getInstance().apply {
                    time = Constant.timeFormat.parse(startTime ?: "")!!
                }.let {
                    set(Calendar.HOUR, it.get(Calendar.HOUR_OF_DAY))
                    set(Calendar.MINUTE, it.get(Calendar.MINUTE))
                }
            }.timeInMillis.toString() + id + ".png"
        } catch (e: ParseException) {
            Calendar.getInstance().timeInMillis.toString() + id + ".png"
        }
    }


//            val cities = arrayOf("Beijing", "Shanghai", "Tokyo", "Osaka", "Seoul", "Taipei", "Hà Nội", "Huế", "Đà Nẵng", "Hội An",
//                "Bangkok", "Jakarta", "Singapore", "Canberra", "TP.HCM", "Cần Thơ", "New Delhi", "Dubai", "Abu Dhabi", "Moskva", "Berlin", "Paris", "London",
//                "Praha", "Rome", "Cairo", "Liverpool", "Manchester", "New York", "Los Angeles", "Washington", "Chicago", "Las Vegas")
//            for (i in 0..9) {
//                val i1 = Random.nextInt(cities.size)
//                val i2 = Random.nextInt(cities.size)
//                val space = if (i1 > i2) i1 - i2 else i2 - i1
//                val time = space + 0.25 * Random.nextInt(20)
//                val price = "${space * 5 + Random.nextInt(200)}.${Random.nextInt(2) * 5}"
//                val seatQuantity = space * 5 + 5 * Random.nextInt(6, 15)
//                Util.apiClient.addTour(
//                    "AC-0$i",
//                    (price.toFloat() + (space / 2 + 1 + 0.5F * Random.nextInt(2)) * Random.nextInt(0, 10)).toString(),
//                    Util.formatFloat(price.toFloat()),
//                    String.format("%02d", Random.nextInt(24)) + ":" + String.format("%02d", 15 * Random.nextInt(4)),
//                    time.toString(),
//                    cities[i1],
//                    cities[i2],
//                    seatQuantity.toString(),
//                    (if (seatQuantity < 50) 2 else if (seatQuantity < 125) 3 else 4).toString(),
//                    String.format(Locale.US, "%.2f", 0.03 + (space / 500F))
//                ).enqueue(object : Callback<ResponseBody> {
//                    override fun onFailure(call: Call<ResponseBody>, t: Throwable) {
//                        showLoading(false)
//                        showToast(t)
//                    }
//
//                    override fun onResponse(call: Call<ResponseBody>, response: Response<ResponseBody>) {
//                        if (!response.isSuccessful) {
//                            showToast(response.errorBody()?.string())
//                        }
//                        if (i == 9) {
//                            showLoading(false)
//                        }
//                    }
//                })
//            }
}