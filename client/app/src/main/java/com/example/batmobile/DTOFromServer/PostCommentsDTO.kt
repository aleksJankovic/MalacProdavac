package com.example.batmobile.DTOFromServer

data class PostCommentsDTO(
    val text: String,
    val dateTime: String,
    val username: String,
    val name: String,
    val surname: String,
    val picture: String?
)
