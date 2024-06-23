package com.example.batmobile.models

data class Purchase(
    var sellerId:       Int,
    var username:       String,
    var sellserAdress:  String,
    var orderItems:     MutableList<OrderItems>,
)
