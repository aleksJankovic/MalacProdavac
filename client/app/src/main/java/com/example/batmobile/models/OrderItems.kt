package com.example.batmobile.models

data class OrderItems(
    var productId:              Int,
    var quantity:               Int,
    var price_per_one_quantity: Int,
    var product_name:           String,
    var unit_measurmnent:       String,
    var product_image:          String?,
    var category_id:            Int
)
