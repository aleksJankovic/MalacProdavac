package com.example.batmobile.DTOFromServer

data class Korisnik (
    var name        : String,
    var surname    : String,
    var username    : String,
    var email       : String,
    var password    : String,
    var picture     : String,
    var numberOfFollows : Int,
    var numberOfOrders  : Int

)