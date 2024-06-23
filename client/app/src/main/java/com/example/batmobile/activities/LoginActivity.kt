package com.example.batmobile.activities

import android.content.Intent
import android.graphics.drawable.GradientDrawable
import androidx.appcompat.app.AppCompatActivity
import android.os.Bundle
import android.view.View
import android.view.View.OnClickListener
import android.widget.Button
import android.widget.EditText
import android.widget.Toast
import com.example.batmobile.R
import com.example.batmobile.network.ApiClient
import com.example.batmobile.network.Config
import com.example.batmobile.services.JWTService
import com.example.batmobile.services.NotifiactionService
import com.example.batmobile.services.Validator
import com.google.firebase.FirebaseApp
import org.json.JSONObject

class LoginActivity : AppCompatActivity() {
    lateinit var input_username:EditText
    lateinit var input_password:EditText
    lateinit var btn_username:Button
    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        setContentView(R.layout.activity_login)

        val button_switch_Prijavi_se = findViewById<Button>(R.id.switch_Prijavi_se)
        val button_switch_Registruj_se = findViewById<Button>(R.id.switch_Registruj_se)
        setRadiusOnButton(button_switch_Prijavi_se, 12f, 0f , 12f , 0f)
        setRadiusOnButton(button_switch_Registruj_se, 0f, 12f , 0f , 12f)

        this.input_username = findViewById<EditText>(R.id.username)
        this.input_password = findViewById<EditText>(R.id.password)

        this.btn_username = findViewById<Button>(R.id.prijavi_se)
        this.btn_username.setOnClickListener(object : OnClickListener{
            override fun onClick(v: View?) {
                sendLoginRequest()
            }
        })

    }
    fun goToTheMainActivity(view:View){
        val intent: Intent = Intent(this, MainActivity::class.java)
        startActivity(intent)
        finish()
    }
    fun goToRegisterActivity(view:View){
        val intent: Intent = Intent(this, RegisterActivity::class.java)
        startActivity(intent)
        finish()
    }
    fun setRadiusOnButton(button: Button, topLeftRadius:Float, topRightRadius:Float, bottomLeftRadius:Float, bottomRightRadius:Float ){
        val originalDrawable = button.background.constantState?.newDrawable()   // kopiramo originalni drawable
        val shapeDrawable = originalDrawable?.mutate() as? GradientDrawable     // ovde obezbedimo da to bude mutable i kasnije se te izmene nece primeniti na ostale buttone koji dele isti background
        shapeDrawable?.cornerRadii = floatArrayOf(
            topLeftRadius, topLeftRadius,
            topRightRadius, topRightRadius,
            bottomRightRadius, bottomRightRadius,
            bottomLeftRadius, bottomLeftRadius
        )
        button.background = shapeDrawable
    }
    fun onEyeIconClick(view: View){
        Validator.showPassword(view, input_password)
    }
    
//    Send Login
    fun sendLoginRequest(){
        val response = Validator.authenticateLogin(input_username, input_password)
        if (response)
        {
            val apiCall: ApiClient = ApiClient(this)

            var url:String = Config.ip_address+":"+Config.port + "/login"
            val jsonObject: JSONObject = JSONObject()
            jsonObject.put("username",input_username.text.toString())
            jsonObject.put("password",input_password.text.toString())
            println(jsonObject)
            apiCall.sendPostRequestWithJSONObjectWithStringResponse(
                url,
                jsonObject,
                { response ->
                    val bool = JWTService.readJwtToken(response, this)
                    if(bool){
                        Toast.makeText(this, "Uspesno ste se ulogovali", Toast.LENGTH_LONG).show()
                    }
                    else{
                        Toast.makeText(this, "Korisničko ime ili lozinka se ne poklapaju", Toast.LENGTH_LONG).show()
                    }
                    NotifiactionService.sendFCMToken(this)
                },
                { error ->
                    val response = error.networkResponse
                    val jsonError = String(response.data)
                    val responseObject:JSONObject = JSONObject(jsonError)
                    if(responseObject["message"].equals("Invalid login credentials.")){
                        Toast.makeText(this, "Korisničko ime ili lozinka se ne poklapaju", Toast.LENGTH_LONG).show()
                        error.printStackTrace()
                    }
                    else{
                        Toast.makeText(this, "Niste se ulogovali", Toast.LENGTH_LONG).show()
                        error.printStackTrace()
                    }
                }
            )
        }
    }
}