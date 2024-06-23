package com.example.batmobile.DTOFromServer

data class RandomProductResponse(
    val randomProducts: List<ExploreProduct>,
    val listOfIDs: List<Long>
)
