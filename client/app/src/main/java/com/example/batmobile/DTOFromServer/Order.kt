package com.example.batmobile.DTOFromServer

data class Order(
    var orderId             : Long,
    var buyerName           : String,
    var buyerSurname        : String,
    var buyerAddress        : String,
    var buyerLong           : Double,
    var buyerLat            : Double,
    var buyerEmail          : String,
    var buyerPhoneNumber    : String,
    var shippingMethodId    : Int,
    var paymentMethodId     : Int,
    var sellerLong          : Double,
    var sellerLat           : Double,
    var purchaseItems       : List<SellerPurchaseItems>,
    var comment             : String,
    var date_time           : String,
    var price               : Double?
)
