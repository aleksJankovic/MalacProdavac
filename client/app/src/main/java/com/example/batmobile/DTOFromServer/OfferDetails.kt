package com.example.batmobile.DTOFromServer

data class OfferDetails(
    var offerId             : Long,
    var orderId             : Long,
    var delivererId         : Int,
    var orderDate           : String,
    var delivererPrice      : Double,
    var orderPrice          : Double,
    var offerStatusId       : Int,
    var delivererName       : String,
    var delivererSurname    : String,
    var delivererUsername   : String,
    var delivererPicture    : String?
)
