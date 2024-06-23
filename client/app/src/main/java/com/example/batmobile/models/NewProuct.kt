package com.example.batmobile.models

import android.net.Uri

data class NewProuct(
    var product_name: String = "",
    var category_id: Int = -1,
    var measurement_id: Int = -1,
    var picture: Uri? = null,
    var price: Int = -1,
    var description: String = "",
    var measurement_value: String = ""
){
    fun validateForNextStep():Boolean{
        if(product_name != "" && category_id > -1 && measurement_id > -1 &&
            picture!=null && price > 0)
            return true
        return false
    }
}