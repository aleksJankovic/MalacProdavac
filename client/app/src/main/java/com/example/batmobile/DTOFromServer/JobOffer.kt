package com.example.batmobile.DTOFromServer

data class JobOffer(
    var orderId         : Long,
    var buyerImage      : String,
    var buyerUsername   : String,
    var sellerLong      : Double,
    var sellerLat       : Double,
    var buyerAddress    : String,
    var sentOffer       : Boolean
)
