package com.example.batmobile.services

import com.github.mikephil.charting.formatter.ValueFormatter

class Charts {

    companion object{
         class  DayAxisValueFormatter : ValueFormatter() {
            private val days = arrayOf("Pon", "Uto", "Sre", "Čet", "Pet", "Sub", "Ned")

            override fun getFormattedValue(value: Float): String {
                return if (value.toInt() >= 0 && value.toInt() < days.size) {
                    days[value.toInt()]
                } else {
                    ""
                }
            }
        }
    }

}