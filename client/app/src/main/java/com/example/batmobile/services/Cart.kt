package com.example.batmobile.services

import android.app.Activity
import android.content.Context
import android.content.SharedPreferences
import com.example.batmobile.models.OrderItems
import com.example.batmobile.models.Purchase
import com.google.gson.Gson
import com.google.gson.reflect.TypeToken

class Cart {
    companion object{

        private var sharedPreferences: SharedPreferences?        = null
        private var editor:            SharedPreferences.Editor? = null
        private val gson = Gson()
        fun initializeSharedPreferences(activity: Activity){
            if(Cart.sharedPreferences == null || Cart.editor == null){
                Cart.sharedPreferences = activity.getSharedPreferences("CartPreferences", Context.MODE_PRIVATE)
                Cart.editor = Cart.sharedPreferences?.edit()
                var cart: MutableList<Purchase> = mutableListOf()
                Cart.editor?.putString("cart", gson.toJson(cart))?.apply()
            }
        }
        fun getCart():List<Purchase>{
            val cartJson = sharedPreferences?.getString("cart","")
            val type = object : TypeToken<List<Purchase>>(){}.type
            return gson.fromJson(cartJson, type)?: emptyList()
        }

        private fun getSellerIfExist(cart: MutableList<Purchase>, seller_id: Int): Purchase?{
            for (purchase in cart ){
                if(purchase.sellerId == seller_id)
                    return purchase
            }
            return null
        }

        private fun getProductIfExist(cart: MutableList<OrderItems>, product_id: Int): OrderItems?{
            for (item in cart){
                if(item.productId == product_id)
                    return item
            }
            return null
        }

        private fun saveCart(cart: MutableList<Purchase>, activity: Activity){
            if(sharedPreferences == null) { initializeSharedPreferences(activity) }
            Cart.editor?.putString("cart", gson.toJson(cart))?.apply()
        }

        fun addToCart(seller_id: Int,seller_username: String ,sellserAddress: String, product_id: Int,product_name: String , unit_measurmnent: String, product_image: String?, category_id:Int , quantity: Int, price_per_one_quantity: Int,activity: Activity){

            var cart:MutableList<Purchase> = getCart().toMutableList()

            var purchase: Purchase? = getSellerIfExist(cart, seller_id)
            var orderItems:OrderItems = OrderItems(product_id, quantity, price_per_one_quantity, product_name, unit_measurmnent, product_image, category_id)
            if(purchase == null){
                var order_list: MutableList<OrderItems> = mutableListOf()
                order_list.add(orderItems)
                var new_purchase: Purchase = Purchase(seller_id,seller_username ,sellserAddress, order_list)
                cart.add(new_purchase)
            }
            else{
                var orderItem = getProductIfExist(purchase.orderItems,product_id)
                if(orderItem == null)
                    purchase.orderItems.add(orderItems)
                else
                    orderItem.quantity += quantity
            }
            saveCart(cart, activity)
        }
        fun removeFromCart(seller_id: Int, product_id: Int, activity: Activity){
            var cart:MutableList<Purchase> = getCart().toMutableList()
            for(purchase in cart){
                if(purchase.sellerId == seller_id){
                    if(purchase.orderItems.size == 1){
                        cart.remove(purchase)
                    }
                    else{
                        for(item in purchase.orderItems){
                            if(item.productId == product_id){
                                purchase.orderItems.remove(item)
                                break
                            }
                        }
                    }
                    break
                }
            }
            saveCart(cart, activity)
        }
        fun dropCart(activity: Activity){
            Cart.sharedPreferences = activity.getSharedPreferences("CartPreferences", Context.MODE_PRIVATE)
            Cart.editor = Cart.sharedPreferences?.edit()
            var cart: MutableList<Purchase> = mutableListOf()
            Cart.editor?.putString("cart", gson.toJson(cart))?.apply()
        }

    }
}