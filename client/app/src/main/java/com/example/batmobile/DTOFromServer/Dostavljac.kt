package com.example.batmobile.DTOFromServer

data class Dostavljac(
    var id              : Int,
    var name            : String,
    var surname         : String,
    var username        : String,
    var email           : String,
    var picture         : String,
    var role            : String,
    var location        : String,
    var longitude       : Double,
    var latitude        : Double,
    var avgGrade        : Double,
    var password        : String,
    var owner           : Boolean
)
