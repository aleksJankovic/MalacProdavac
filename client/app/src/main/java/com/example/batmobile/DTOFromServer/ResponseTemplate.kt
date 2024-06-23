package com.example.batmobile.DTOFromServer

import java.util.Objects

data class ResponseListTemplate<T>(
    val code: Int,
    val status: String,
    val success: Boolean,
    val message: String,
    val data: List<T>,
)

data class ResponseObjectTemplate<T>(
    val code: Int,
    val status: String,
    val success: Boolean,
    val message: String,
    val data: T,
)
