package com.example.batmobile.DTOFromServer

data class Prodavac(
    var seller_id           : Int,
    var name                : String,
    var surname             : String,
    var username            : String,
    var email               : String,
    var picture             : String?,
    var pib                 : String,
    var address             : String,
    var password            : String,
    var longitude           : Double,
    var latitude            : Double,
    var numberOfFollows     : Int,
    var numberOfPosts       : Int,
    var avgGrade            : Double,
    var profileOwner        : Boolean,
    var followed            : Boolean,
    var numberOfProducts    : Int
)
