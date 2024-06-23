package com.example.batmobile.models

data class TopProduct(
    var productId: Int,
    var categoryId: Int,
    var productName: String,
    var productPicture: String?,
    var sellerUsername: String,
    var longitude: Double,
    var latitude: Double,
    var averageGrade: Double?
)
