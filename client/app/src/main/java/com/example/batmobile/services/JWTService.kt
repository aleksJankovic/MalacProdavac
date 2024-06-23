package com.example.batmobile.services

import android.app.Activity
import android.content.Context
import android.content.Intent
import android.content.SharedPreferences
import com.auth0.android.jwt.JWT
import com.example.batmobile.activities.DelivererActivity
import com.example.batmobile.activities.KupacActivity
import com.example.batmobile.activities.MainActivity
import com.example.batmobile.activities.SellerActivity
import com.example.batmobile.enums.Role

class JWTService() {
    companion object{

        private var sharedPreferences: SharedPreferences?        = null
        private var editor:            SharedPreferences.Editor? = null

        private fun initializeSharedPreferences(context: Context){
            if(sharedPreferences == null || editor == null){
                sharedPreferences = context.getSharedPreferences("MyPreferences", Context.MODE_PRIVATE)
                editor = sharedPreferences?.edit()
            }
        }

        fun readJwtToken(token: String, activity: Activity):Boolean{
            var jwt: JWT = JWT(token)
            var username    = jwt.getClaim("sub").asString()
            val role = jwt.getClaim("role").asString()
            var roleTypeRole: Role? = null
            when(role){
                "User"      ->  roleTypeRole = Role.User
                "Deliverer" ->  roleTypeRole = Role.Deliverer
                "Seller"    ->  roleTypeRole = Role.Seller
            }
            if(username != null && role != null) {Cart.initializeSharedPreferences(activity); saveToken(token, activity);saveRole(roleTypeRole as Role,activity); redirectIfAllIsCorret(activity,roleTypeRole); return true}
            return false
        }
        private fun saveToken(token:String,context: Context){
            initializeSharedPreferences(context)
            editor?.putString("authToken", token)
            editor?.apply()
        }
        private fun saveRole(role: Role, activity: Activity){
            initializeSharedPreferences(activity)
            editor?.putString("role", role.toString())
            editor?.apply()
        }
//        --------------------------------------------------
        private fun tokenIsNotExpired(token:String?):Boolean{
            if(token != null){
                try{
                    var jwt: JWT = JWT(token)
                    var exp_time = jwt.getClaim("exp").asLong()
                    return exp_time != null && exp_time * 1000 > System.currentTimeMillis()
                }
                catch (e: Exception){return false }
            }
            return false
        }
        fun getTokenIfExist(context: Context): Role?{
            initializeSharedPreferences(context)
            val tokenString = sharedPreferences?.getString("authToken", null).toString()
            var role = getRoleIfExist(context)
            if(tokenIsNotExpired(tokenString) && role!=null){
                return role
            }
            return null
        }
        fun getRoleIfExist(context: Context):Role?{
            initializeSharedPreferences(context)
            val roleString = sharedPreferences?.getString("role", null)
            return when (roleString) {
                "User" -> Role.User
                "Deliverer" -> Role.Deliverer
                "Seller" -> Role.Seller
                else -> null
            }
        }
        private fun redirectIfAllIsCorret(activity: Activity, role: Role){
            var intent: Intent? = null
            when(role){
                Role.User ->        { intent = Intent(activity, KupacActivity::class.java) }
                Role.Deliverer ->   { intent = Intent(activity, DelivererActivity::class.java)}
                Role.Seller ->      { intent = Intent(activity, SellerActivity::class.java)}
                else ->             {}
            }
            activity.startActivity(intent)
            activity.finish()
        }

        fun logOut(activity: Activity){
            initializeSharedPreferences(activity)
            editor?.clear()
            editor?.apply()
            var intent: Intent? = Intent(activity, MainActivity::class.java)
            activity.startActivity(intent)
            activity.finish()
        }

        fun redirectIfIsLogged(activiry:Activity){
            val r = JWTService.getTokenIfExist(activiry)
            if(r is Role){
                var intent: Intent? = null
                when(r){
                    Role.User ->        { intent = Intent(activiry, KupacActivity::class.java) }
                    Role.Deliverer ->   { intent = Intent(activiry, DelivererActivity::class.java)}
                    Role.Seller ->      { intent = Intent(activiry, SellerActivity::class.java)}
                    else ->             {}
                }
                activiry.startActivity(intent)
                activiry.finish()
            }
        }
        fun getToken(): String{
            return sharedPreferences?.getString("authToken", null).toString()
        }
    }

}