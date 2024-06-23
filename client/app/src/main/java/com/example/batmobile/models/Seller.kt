package com.example.batmobile.models

data class Seller(
    var sellerId: Int,
    var name: String,
    var surname: String,
    var username: String,
    var picture: String,
    var longitude: Double,
    var latitude: Double,
    var numberOfFollowers: Int,
    var numberOdProducts: Int

)
