package com.example.batmobile.DTOFromServer

data class OrdersInformation(
    var orderId: Int,
    var orderDate: String,
    var totalPrice: Double,
    var orderStatusId: Int,
    var sellerName: String,
    var sellerImage: String
)

data class JobInformation(
    var orderId: Int,
    var orderDate: String,
    var delivererPrice: Double,
    var purchasePrice: Double,
    var offerStatusId: Int,
    var buyerName: String,
    var buyerSurname: String,
    var buyerUsername:String,
    var buyerPicture: String,
    var offerId: Int
)

data class OrderInformation(
    var productCategoryId: Int,
    var productName: String,
    var productImage: String?,
    var productPrice: Double,
    var quantity: Int,
    var measurement: String
//    var measurementValue: null
)