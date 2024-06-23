package com.example.batmobile.DTOFromServer

data class PostDetails(
    val name                : String,
    val surname             : String,
    val username            : String,
    val picture             : String?,
    val longitude           : Double,
    val latitude            : Double,
    val dateTime            : String,
    val text                : String,
    val likesNumber         : Int,
    val commentsNumber      : Int,
    val postCommentDTOList  : List<PostCommentsDTO>,
    val likedPost           : Boolean
)
