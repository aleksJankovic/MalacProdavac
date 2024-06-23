package com.example.batmobile.services

import android.content.Context
import com.example.batmobile.network.ApiClient
import com.example.batmobile.network.Config
import com.google.firebase.messaging.FirebaseMessaging
import org.json.JSONObject


class NotifiactionService {
    companion object{
        fun sendFCMToken(context: Context){
            FirebaseMessaging.getInstance().token.addOnCompleteListener { task ->
                if (task.isSuccessful) {
                    val fcmToken = task.result
                    println("FCM Token: $fcmToken")

                    val url: String = Config.ip_address+":"+Config.port+"/setFCM"
                    var jsonObject: JSONObject = JSONObject();
                    jsonObject.put("token",fcmToken)
                    val apiClient = ApiClient(context)
                    apiClient.sendPostRequestWithJSONObjectWithStringResponse(url, JWTService.getToken(),
                        jsonObject,
                        {response->
                            println(response)
                        },
                        {error->
                            println(error)
                        })
                } else {
                    // Ako postoji greška pri dobijanju FCM tokena
                    println("Error getting FCM token: ${task.exception?.message}")
                }
            }
        }
    }
}