package com.example.batmobile.DTOFromServer

data class ProductAll(
    var id                 : Int,
    var productName        : String,
    var picture            : String?,
    var description        : String,
    var price              : Double,
    var category           : String,
    var measurement        : String,
    var category_id        : Int
)
