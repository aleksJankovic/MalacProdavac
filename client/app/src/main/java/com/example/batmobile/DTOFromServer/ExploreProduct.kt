package com.example.batmobile.DTOFromServer

data class ExploreProduct(
    val categoryId     : Long,
    val productId      : Long,
    val productName    : String,
    val picture        : String?
)
