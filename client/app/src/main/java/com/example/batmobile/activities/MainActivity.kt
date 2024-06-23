package com.example.batmobile.activities

import android.content.Intent
import androidx.appcompat.app.AppCompatActivity
import android.os.Bundle
import android.view.View
import com.example.batmobile.R
import com.example.batmobile.services.JWTService
import com.google.firebase.FirebaseApp

class MainActivity : AppCompatActivity() {
    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        setContentView(R.layout.activity_main)
        JWTService.redirectIfIsLogged(this)
        FirebaseApp.initializeApp(this)
    }

    fun goToLogIn(view:View){
        val intent: Intent = Intent(this, LoginActivity::class.java)
        startActivity(intent)
        finish()
    }

    fun goToUnloggedUser(view: View){
        val intent: Intent = Intent(this, NeulogovanActivity::class.java)
        startActivity(intent)
        finish()
    }

    fun goToRegister(view: View){
        val intent: Intent = Intent(this, RegisterActivity::class.java)
        startActivity(intent)
        finish()
    }

}