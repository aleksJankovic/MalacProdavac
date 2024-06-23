package com.example.batmobile.DTOFromServer

data class PersonalInformation(
    val id          : Long,
    val name        : String,
    val surname     : String,
    val username    : String,
    val email       : String,
    val picture     : String?,
    val role        : String,
    val numberOfFollows : Int,
    val numberOfOrders  : Int
)
