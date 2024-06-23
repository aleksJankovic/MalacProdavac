package com.example.batmobile.DTOFromServer

data class CustomerOrder(
    var orderId: Int,
    var username: String,
    var address: String?,
    var date: String,
    var picture: String,
    var orderStatus: Int
)
