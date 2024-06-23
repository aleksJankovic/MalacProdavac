package com.example.batmobile.models

data class Order(
    var buyerLongitude: Double,
    var buyerLatitude: Double,
    var buyerAddress: String,
    var phoneNumber: String,
    var purchase: List<Purchase>,
    var paymentMethodId: Int,
    var shippingMethodId: Int
)
