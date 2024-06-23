package com.example.batmobile.DTOFromServer

data class NewNews(
    val id:             Int,
    val usernameSeller: String,
    val dateTime:       String,
    val text:           String,
    val likesNumber:    Int,
    val commentsNumber: Int,
    val likedPost:      Boolean
)
