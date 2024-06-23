package com.example.batmobile

import android.annotation.SuppressLint
import android.app.Activity
import android.app.AlertDialog
import android.app.Dialog
import android.content.Intent
import android.content.pm.PackageManager
import android.graphics.Color
import android.graphics.Typeface
import android.net.Uri
import android.os.Bundle
import android.provider.MediaStore
import android.text.Editable
import android.text.TextWatcher
import android.util.Log
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import android.view.Window
import android.view.inputmethod.EditorInfo
import android.widget.Button
import android.widget.EditText
import android.widget.FrameLayout
import android.widget.GridLayout
import android.widget.ImageView
import android.widget.LinearLayout
import android.widget.ScrollView
import android.widget.TextView
import android.widget.Toast
import androidx.constraintlayout.widget.ConstraintLayout
import androidx.core.app.ActivityCompat
import androidx.core.content.ContextCompat
import androidx.fragment.app.Fragment
import com.example.batmobile.DTOFromServer.Dostavljac
import com.example.batmobile.DTOFromServer.ResponseObjectTemplate
import com.example.batmobile.DTOFromServer.Vozila
import com.example.batmobile.network.ApiClient
import com.example.batmobile.network.Config
import com.example.batmobile.services.Image
import com.example.batmobile.services.JWTService
import com.example.batmobile.services.Map
import com.example.batmobile.services.Validator
import com.github.mikephil.charting.charts.PieChart
import com.github.mikephil.charting.data.PieData
import com.github.mikephil.charting.data.PieDataSet
import com.github.mikephil.charting.data.PieEntry
import com.github.mikephil.charting.formatter.PercentFormatter
import com.github.mikephil.charting.utils.MPPointF
import com.google.android.gms.location.FusedLocationProviderClient
import com.google.gson.Gson
import com.google.gson.reflect.TypeToken
import de.hdodenhof.circleimageview.CircleImageView
import kotlinx.coroutines.CoroutineScope
import kotlinx.coroutines.Dispatchers
import kotlinx.coroutines.async
import kotlinx.coroutines.launch
import org.json.JSONObject
import org.osmdroid.config.Configuration
import org.osmdroid.tileprovider.tilesource.TileSourceFactory
import org.osmdroid.util.GeoPoint
import org.osmdroid.views.MapView
import org.osmdroid.views.overlay.ItemizedIconOverlay
import org.osmdroid.views.overlay.OverlayItem


class DelivererProfileFragment : Fragment() {

    //username i password: Peki034
    private lateinit var apiClient                              : ApiClient
    private lateinit var view                                   : View
    private          var delivererId                               : Int = -1
    lateinit var image                                          : CircleImageView
    lateinit var name                                           : EditText
    lateinit var lastname                                       : EditText
    lateinit var username                                       : EditText
    lateinit var email                                          : EditText
    lateinit var old_password                                   : EditText
    lateinit var password                                       : EditText
    lateinit var password_confirm                               : EditText
    lateinit var  lokacija                                      : ImageView

    lateinit var error_name                                     : TextView
    lateinit var error_lastname                                 : TextView
    lateinit var error_username                                 : TextView
    lateinit var error_email                                    : TextView
    lateinit var error_password_old                             : TextView
    lateinit var error_password                                 : TextView
    lateinit var error_password_confirm                         : TextView

    lateinit var btn_nastavi                                    : Button
    lateinit var close                                          : ImageView

    private lateinit var profile_information                    : ConstraintLayout
    private lateinit var privremeni_logout                      : Button
    private lateinit var edit_profile                           : Button

    var flagName                                                : Boolean = true
    var flagLastname                                            : Boolean = true
    var flagUsername                                            : Boolean = true
    var flagUsernameExisting                                    : Boolean = true
    var flagEmail                                               : Boolean = true
    var flagEmailExisting                                       : Boolean = true
    var flagPasswordOld                                         : Boolean = false
    var flagPassword                                            : Boolean = true
    var flagPassword_confirm                                    : Boolean = true

    lateinit var eye_password_old                               : ImageView
    lateinit var eye_password                                   : ImageView
    lateinit var eye_password_confirm                           : ImageView

    lateinit var edit_info                                      : ScrollView
    lateinit var moj_profil                                     : TextView
    lateinit var dostavljac_info                                : Dostavljac
    lateinit var dostavljac_info_new                            : Dostavljac

    lateinit var lista_vozila                                   : GridLayout
    lateinit var prevozna_sredstva                              : TextView

    //MAPA
    lateinit var person_household                               : TextView
    lateinit var person_username                                : TextView
    lateinit var prodavac_lokacija                              : EditText
    lateinit var mapView                                        : MapView
    lateinit var confirm                                        : Button
    lateinit var close_edit_maps                                : ImageView
    private var live_latitude:Double = -100.0
    private var live_longitude:Double = 200.0
    private lateinit var fusedLocationClient: FusedLocationProviderClient
    private val REQUEST_CODE = 123

    private lateinit var automobil                              : FrameLayout
    private lateinit var motocikl                               : FrameLayout
    private lateinit var kombi                                  : FrameLayout
    private lateinit var kamion                                 : FrameLayout
    private lateinit var close_overlay                          : ImageView
    private lateinit var confirm_vozilo                         : Button

    //PIE CHART
    lateinit var pieChart                                       : PieChart
    lateinit var error_piechart                                 : TextView

    private fun getAllStuff() {
        apiClient = ApiClient(requireContext())
        profile_information =           view.findViewById(R.id.profile_info)
        privremeni_logout =             view.findViewById(R.id.privremeni_logout)
        edit_profile =                  view.findViewById(R.id.edit_profile)
        moj_profil =                    view.findViewById(R.id.textView5)
        lokacija =                      view.findViewById<ImageView>(R.id.deliverer_map)
        dostavljac_info = Dostavljac(0,"","","","","","","",0.0,0.0,0.0,"", false)

        lista_vozila =                  view.findViewById(R.id.lista_vozila)
        prevozna_sredstva =             view.findViewById(R.id.naslov1)

        //PIE CHART
        pieChart =                      view.findViewById(R.id.pieChart)
        error_piechart =                view.findViewById(R.id.error_piechart)
    }

    private fun getPersonalInformation() {
        var url:String

        if(delivererId >= 0)
            url = Config.ip_address+":"+ Config.port + "/delivererPersonalInformation?id=" + delivererId

        else
            url = Config.ip_address+":"+ Config.port + "/delivererPersonalInformation"

        if(!JWTService.getToken().equals("null")){
            apiClient.sendGetRequestEmpty(
                JWTService.getToken(), url,
                {response ->
                    var gson = Gson()
                    val jsonObject = JSONObject(response)
                    val code = jsonObject.getInt("code")
                    val success = jsonObject.getBoolean("success")

                    if (code == 200 && success) {
                        val dataObject = jsonObject.getJSONObject("data")
                        dostavljac_info.apply {
                            id              = dataObject.getInt("id")
                            name            = dataObject.getString("name")
                            surname         = dataObject.getString("surname")
                            username        = dataObject.getString("username")
                            email           = dataObject.getString("email")
                            picture         = dataObject.getString("picture")
                            role            = dataObject.getString("role")
                            location        = dataObject.getString("location")
                            longitude       = dataObject.getDouble("longitude")
                            latitude        = dataObject.getDouble("latitude")
                            avgGrade        = dataObject.getDouble("avgGrade")
                            owner           = dataObject.getBoolean("owner")
                        }
                    }
                    println(dostavljac_info)
                    if(!dostavljac_info.owner)
                        setiVisibilityPerAuthorization()
                    renderProfileInformation(dostavljac_info)
                },
                {error -> println(error)}
            )
        }
        else{
            apiClient.sendGetRequestEmpty( url,
                {response ->
                    var gson = Gson()
                    val jsonObject = JSONObject(response)
                    val code = jsonObject.getInt("code")
                    val success = jsonObject.getBoolean("success")

                    if (code == 200 && success) {
                        val dataObject = jsonObject.getJSONObject("data")
                        dostavljac_info.apply {
                            id              = dataObject.getInt("id")
                            name            = dataObject.getString("name")
                            surname         = dataObject.getString("surname")
                            username        = dataObject.getString("username")
                            email           = dataObject.getString("email")
                            picture         = dataObject.getString("picture")
                            role            = dataObject.getString("role")
                            location        = dataObject.getString("location")
                            longitude       = dataObject.getDouble("longitude")
                            latitude        = dataObject.getDouble("latitude")
                            avgGrade        = dataObject.getDouble("avgGrade")
                            owner           = dataObject.getBoolean("owner")
                        }
                    }
                    println(dostavljac_info)
                    if(!dostavljac_info.owner)
                        setiVisibilityPerAuthorization()
                    renderProfileInformation(dostavljac_info)
                },
                {error -> println(error)}
            )
        }

    }

    //EDIT PROFILA POCETAK
    private fun showEditProfileOverlay(dostavljac_podaci: Dostavljac) {
        val inflater = layoutInflater
        val dialogView = inflater.inflate(R.layout.overlay_change_profile_info, null)
        val alertDialogBuilder = AlertDialog.Builder(requireContext())
        alertDialogBuilder.setView(dialogView)
        val alertDialog: AlertDialog = alertDialogBuilder.create()

        dostavljac_info_new = Dostavljac(0,"","","","","","","",0.0,0.0,0.0,"",false)
        edit_info =                 dialogView.findViewById<ScrollView>(R.id.edit_info)

        name =                      dialogView.findViewById<EditText>(R.id.name)
        lastname =                  dialogView.findViewById<EditText>(R.id.lastname)
        username =                  dialogView.findViewById<EditText>(R.id.username)
        email =                     dialogView.findViewById<EditText>(R.id.email)
        old_password =              dialogView.findViewById<EditText>(R.id.password_old)
        password =                  dialogView.findViewById<EditText>(R.id.password)
        password_confirm =          dialogView.findViewById<EditText>(R.id.password_confirm)

        error_name =                dialogView.findViewById<TextView>(R.id.error_name)
        error_lastname =            dialogView.findViewById<TextView>(R.id.error_lastname)
        error_username =            dialogView.findViewById<TextView>(R.id.error_username)
        error_email =               dialogView.findViewById<TextView>(R.id.error_email)
        error_password_old =        dialogView.findViewById<TextView>(R.id.error_password_old)
        error_password_old.setText("* Ovo je obavezno polje")
        error_password_old.visibility = View.VISIBLE
        error_password =            dialogView.findViewById<TextView>(R.id.error_password)
        error_password_confirm =    dialogView.findViewById<TextView>(R.id.error_password_confirm)

        eye_password_old =          dialogView.findViewById<ImageView>(R.id.eye_password_old)
        eye_password =              dialogView.findViewById<ImageView>(R.id.eye_password)
        eye_password_confirm =      dialogView.findViewById<ImageView>(R.id.eye_password_confirm)

        btn_nastavi =               dialogView.findViewById<Button>(R.id.nastavi)
        close =                     dialogView.findViewById<ImageView>(R.id.close)

        name.setHint(dostavljac_podaci.name)
        name.setText("")
        lastname.setHint(dostavljac_podaci.surname)
        lastname.setText("")
        username.setHint(dostavljac_podaci.username)
        username.setText("")
        email.setHint(dostavljac_podaci.email)
        email.setText("")

        old_password.setText("")
        password.setText("")
        password_confirm.setText("")

        alertDialog.show()

        close.setOnClickListener{
            alertDialog.dismiss()
        }

        btn_nastavi.setOnClickListener{
            //provera
            checkNewInformations(dostavljac_info_new)
            //slanje nove lozinke na server na proveru
            sendPasswordDataToServer(dostavljac_info_new.password)

            println("pre gasenja" + dostavljac_info_new)
            if(!dostavljac_info_new.username.equals(""))
            {
                Toast.makeText(context, "Morate se ponovo ulogovati", Toast.LENGTH_LONG).show()
                JWTService.logOut(requireActivity())
                dostavljac_info_new = Dostavljac(0,"","","","","","","",0.0,0.0,0.0,"", false)
            }
            alertDialog.dismiss()
        }

        name.setOnFocusChangeListener { _, hasFocus ->
            if (!hasFocus) {
                if (!name.text.toString().equals("")) {
                    dostavljac_info_new.name = name.text.toString()
                    validatorUnosa(dostavljac_info_new.name, "name", error_name)
                }
                else flagName = true
            }
        }
        name.setOnEditorActionListener { _, actionId, _ ->
            if (actionId == EditorInfo.IME_ACTION_DONE) {
                if (!name.text.toString().equals("")) {
                    dostavljac_info_new.name = name.text.toString()
                    validatorUnosa(dostavljac_info_new.name, "name", error_name)
                }
                else flagName = true
            }
            false
        }

        lastname.setOnFocusChangeListener { _, hasFocus ->
            if (!hasFocus) {
                if (!lastname.text.toString().equals("")) {
                    dostavljac_info_new.surname = lastname.text.toString()
                    validatorUnosa(dostavljac_info_new.surname, "lastname", error_lastname)
                }
                else flagLastname = true
            }
        }
        lastname.setOnEditorActionListener { _, actionId, _ ->
            if (actionId == EditorInfo.IME_ACTION_DONE) {
                if (!lastname.text.toString().equals("")) {
                    dostavljac_info_new.surname = lastname.text.toString()
                    validatorUnosa(dostavljac_info_new.surname, "lastname", error_lastname)
                }
                else flagLastname = true
            }
            false
        }

        username.setOnFocusChangeListener { _, hasFocus ->
            if (!hasFocus) {
                if (!username.text.equals("")) {
                    dostavljac_info_new.username = username.text.toString()
                    validatorUnosa(dostavljac_info_new.username, "username", error_username)
                    checkExisting("username", dostavljac_info_new.username)
                }
                else flagUsername = true
            }
        }
        username.setOnEditorActionListener { _, actionId, _ ->
            if (actionId == EditorInfo.IME_ACTION_DONE) {
                if (!username.text.toString().equals("")) {
                    dostavljac_info_new.username = username.text.toString()
                    validatorUnosa(dostavljac_info_new.username, "username", error_username)
                    checkExisting("username", dostavljac_info_new.username)
                }
                else flagUsername = true
            }
            false
        }

        email.setOnFocusChangeListener { _, hasFocus ->
            if (!hasFocus) {
                if (!email.text.toString().equals("")) {
                    dostavljac_info_new.email = email.text.toString()
                    validateEmail(dostavljac_info_new.email, error_email)
                    checkExisting("email", dostavljac_info_new.email)
                }
                else flagEmail = true
            }
        }
        email.setOnEditorActionListener { _, actionId, _ ->
            if (actionId == EditorInfo.IME_ACTION_DONE) {
                if (!email.text.toString().equals("")) {
                    dostavljac_info_new.email = email.text.toString()
                    validateEmail(dostavljac_info_new.email, error_email)
                    checkExisting("email", dostavljac_info_new.email)
                }
                else flagEmail = true
            }
            false
        }

        //stara sifra je obavezno polje
        old_password.setOnFocusChangeListener { _, hasFocus ->
            if (!hasFocus) {
                validateOldPassword(old_password.text.toString(), error_password_old)
                validateAllInputs()
            }
            else
            {
                error_password_old.visibility = View.INVISIBLE
            }
        }
        old_password.setOnEditorActionListener { _, actionId, _ ->
            if (actionId == EditorInfo.IME_ACTION_DONE) {
                validateOldPassword(old_password.text.toString(), error_password_old)
                validateAllInputs()
                return@setOnEditorActionListener true
            }
            return@setOnEditorActionListener true
        }


        old_password.addTextChangedListener(object : TextWatcher {
            override fun beforeTextChanged(s: CharSequence?, start: Int, count: Int, after: Int) {
                // Nije potrebno implementirati
            }

            override fun onTextChanged(s: CharSequence?, start: Int, before: Int, count: Int) {
                // Nije potrebno implementirati
            }

            override fun afterTextChanged(s: Editable?) {
                validateOldPassword(s.toString(), error_password_old)
                validateAllInputs()
            }
        })

        password.setOnFocusChangeListener { _, hasFocus ->
            if (!hasFocus) {
                if (!password.text.toString().equals("")) {
                    dostavljac_info_new.password = password.text.toString()
                    validatePassword(password, error_password)
                }
                else flagPassword = true
            }
        }
        password.setOnEditorActionListener { _, actionId, _ ->
            if (actionId == EditorInfo.IME_ACTION_DONE) {
                if (!password.text.toString().equals("")) {
                    dostavljac_info_new.password = password.text.toString()
                    validatePassword(password, error_password)
                }
                else flagPassword = true
            }
            false
        }

        password_confirm.setOnFocusChangeListener { _, hasFocus ->
            if (!hasFocus) {
                if (!password_confirm.text.toString().equals("")) {
                    validateConfirmPassword(password, password_confirm, error_password_confirm)
                }
                else flagPassword_confirm = true
            }
        }
        password_confirm.setOnEditorActionListener { _, actionId, _ ->
            if (actionId == EditorInfo.IME_ACTION_DONE) {
                if (!password_confirm.text.toString().equals("")) {
                    validateConfirmPassword(password, password_confirm, error_password_confirm)
                }
                else flagPassword_confirm = true
            }
            false
        }

        eye_password_old.setOnClickListener({showPasswordOg(eye_password_old)})
        eye_password.setOnClickListener{showPasswordOg(eye_password)}
        eye_password_confirm.setOnClickListener{showPasswordOg(eye_password_confirm)}
    }

    fun showPasswordOg(view: View){
        if(view.id.equals(R.id.eye_password))
            Validator.showPassword(view, password)
        else if (view.id.equals(R.id.eye_password_old))
            Validator.showPassword(view, old_password)
        else
            Validator.showPassword(view, password_confirm)
    }

    private fun validateAllInputs() {
        if(!flagName || !flagLastname || !flagUsername || !flagEmail || !flagPasswordOld || !flagPassword || !flagPassword_confirm)
        {
            btn_nastavi.isEnabled = false
            btn_nastavi.setBackgroundResource(R.drawable.full_fill_button_disabled)
        }
        else
        {
            btn_nastavi.isEnabled = true
            btn_nastavi.setBackgroundResource(R.drawable.full_fill_button)
        }
    }

    fun validateConfirmPassword(password: EditText, password_configm: EditText,error_text: TextView)
    {
        if(password.text.toString().length > 0){
            if(!password_configm.text.toString().equals(password.text.toString())){ error_text.visibility = View.VISIBLE ; flagPassword_confirm = false }
            else{ error_text.visibility = View.INVISIBLE ; flagPassword_confirm = true }
        }
        else{ error_text.visibility = View.INVISIBLE ; flagPassword_confirm = false }
        validateAllInputs()
    }

    fun validatePassword(text: EditText, error_text: TextView)
    {
        val response = Validator.validatePassword(text.text.toString())
        when(response){
            "* Morate uneti neku šifru"->                                        { flagPassword = false; error_text.setText(response); error_text.visibility = View.VISIBLE}
            "* Šifra mora sadržati 8+ znakova, velika i mala slova, brojeve"->   { flagPassword = false; error_text.setText(response); error_text.visibility = View.VISIBLE}
            "true"->                                                            { flagPassword = true; error_text.setText(response); error_text.visibility = View.INVISIBLE; dostavljac_info_new.password = text.text.toString()}
        }
        validateAllInputs()
    }

    fun checkExisting(type: String, value: String)
    {
        val jsonObject: JSONObject = JSONObject()
        jsonObject.put("username", "")
        jsonObject.put("email", "")

        if(type.equals("username"))
            jsonObject.put("username", value)
        if(type.equals("email"))
            jsonObject.put("email", value)

        var url:String = Config.ip_address+":"+ Config.port + "/registration/step1"

        apiClient.sendPostRequestWithJSONObjectWithJsonResponse(
            url,
            jsonObject,
            { response ->
                println(response)
                when (type)
                {
                    "username"->
                    {
                        if(flagUsernameExisting == false)
                        {
                            flagUsernameExisting = true
                            flagUsername = true;
                            error_username.setText("");
                            error_username.visibility = View.INVISIBLE
                        }
                    }
                    "email" ->
                    {
                        if(flagEmailExisting == false)
                        {
                            flagEmailExisting = true
                            flagEmail = true;
                            error_email.setText("");
                            error_email.visibility = View.INVISIBLE
                        }
                    }
                }
                validateAllInputs()
            },
            { error ->
                if(error.networkResponse!=null)
                {
                    val response = error.networkResponse
                    val jsonError = String(response.data)
                    val responseObject: JSONObject = JSONObject(jsonError)
                    println(responseObject)
                    if(responseObject["code"] == 409){
                        val data = responseObject["data"] as JSONObject
                        if(data["usernameTaken"]==true)
                        {
                            flagUsernameExisting = false
                            flagUsername = false;
                            error_username.setText("*Korisničko ime se već koristi");
                            error_username.visibility = View.VISIBLE
                        }
                        else if(data["emailTaken"]==true)
                        {
                            flagEmailExisting = false
                            flagEmail = false;
                            error_email.setText("*Ovaj email se već koristi");
                            error_email.visibility = View.VISIBLE
                        }
                    }
                    validateAllInputs()
                }
            }
        )
    }

    private fun validateOldPassword(oldPassword: String, error_text: TextView)
    {
        val url: String = Config.ip_address + ":" + Config.port + "/updatePersonalInformation/validateOldPassword/"
        val finalUrl = "$url$oldPassword"
        apiClient.sendGetRequestEmpty(JWTService.getToken(), finalUrl,
            { response ->
                Log.d("TAG", response)
                //println(response)
                val gson = Gson()
                val typeToken = object : TypeToken<ResponseObjectTemplate<Object>>() {}.type
                val jsonResponse = gson.fromJson<ResponseObjectTemplate<Object>>(response, typeToken)
                println(jsonResponse.message)
                when (jsonResponse.message) {
                    "Old password is not valid." -> {
                        flagPasswordOld = false
                        error_text.text = "*Trenutna lozinka nije validna"
                        error_text.visibility = View.VISIBLE
                    }
                    "Old password is valid." -> {
                        flagPasswordOld = true
                        error_text.text = "*Trenutna lozinka je validna"
                        error_text.visibility = View.INVISIBLE
                    }
                }
            },
            { error ->
                println(error)
            }
        )
        validateAllInputs()
    }

    private fun validatorUnosa(input: String, tip: String, error_text: TextView?)
    {
        if(input.length >= 2) {
            when(tip)
            {
                "name" ->       { flagName      = true ;            error_text!!.visibility = View.INVISIBLE }
                "lastname" ->   { flagLastname  = true ;            error_text!!.visibility = View.INVISIBLE }
                "username" ->   { flagUsername  = true ;            error_text!!.visibility = View.INVISIBLE }
            }
        }
        else
        {
            when(tip){

                "name" ->       { flagName      = false ;           error_text!!.visibility = View.VISIBLE  }
                "lastname" ->   { flagLastname  = false ;           error_text!!.visibility = View.VISIBLE  }
                "username" ->   { flagUsername  = false ;           error_text!!.visibility = View.VISIBLE  }
            }
        }

        if(tip.equals("username"))
        {
            if(!Validator.validateUsername(input))
            {
                flagUsername = false ;
                error_text!!.visibility = View.VISIBLE
            }
        }
        validateAllInputs()
    }

    private fun sendPasswordDataToServer(password: String)
    {
        if (!password.equals("")) {
            val url: String = "${Config.ip_address}:${Config.port}/updatePersonalInformation/changePassword/"
            val finalUrl = "$url$password"
            apiClient.sendPostRequestWithToken(JWTService.getToken(), finalUrl,
                { response ->
                    println("Uspesno poslato: $response")
                    val gson = Gson()
                    val typeToken = object : TypeToken<ResponseObjectTemplate<Object>>() {}.type
                    val jsonResponse = gson.fromJson<ResponseObjectTemplate<Object>>(response, typeToken)

                    when {
                        jsonResponse.message.equals("New password is valid.", ignoreCase = true) -> {
                            Toast.makeText(context, "Uspesno ste se izmenili lozinku", Toast.LENGTH_LONG).show()
                        }
                        jsonResponse.message.equals("New password is not valid.", ignoreCase = true) -> {
                            Toast.makeText(context, "Niste izmenili lozinku", Toast.LENGTH_LONG).show()
                        }
                        jsonResponse.message.equals("New password is same as the old password.", ignoreCase = true) -> {
                            Toast.makeText(context, "Niste izmenili lozinku", Toast.LENGTH_LONG).show()
                        }
                    }
                },
                { error ->
                    println("Greska prilikom slanja: $error")
                }
            )
        }
    }
    fun validateEmail(email: String, error_text: TextView){
        if(!Validator.regexEmailValidationPattern(email))
        {
            error_text.setText("* Uneta email adresa nije ispravna")
            error_text.visibility = View.VISIBLE
        }
        else {
            error_text.visibility = View.INVISIBLE
        }
        validateAllInputs()
    }

    private fun checkNewInformations(dostavljac_new: Dostavljac)
    {
        val jsonObject: JSONObject = JSONObject()
        var ime = dostavljac_new.name
        var prezime = dostavljac_new.surname
        var username = dostavljac_new.username
        var email = dostavljac_new.email
        val url: String = "${Config.ip_address}:${Config.port}/updatePersonalInformation"
        val userProfile = JSONObject().apply{
            put("name", ime)
            put("surname", prezime)
            put("username", username)
            put("email", email)
        }
        var check : Boolean = false
        apiClient.sendPostRequestWithJSONObjectWithStringResponse(
            url,
            JWTService.getToken(),
            userProfile,
            { response ->
                println("Uspesno poslato: $response")
                val gson = Gson()
                val typeToken = object : TypeToken<ResponseObjectTemplate<Object>>() {}.type
                val jsonResponse = gson.fromJson<ResponseObjectTemplate<Object>>(response, typeToken)
                when
                {
                    jsonResponse.message.equals("Personal information changed successfully!", ignoreCase = true) -> {
                        Toast.makeText(context, "Uspesno ste se izmenili personalne informacije", Toast.LENGTH_LONG).show()
                    }
                    //nije validno
                    jsonResponse.message.equals("New name is not valid.", ignoreCase = true) -> {
                        Toast.makeText(context, "Niste uspesno izmenili personalne informacije", Toast.LENGTH_LONG).show()
                    }
                    jsonResponse.message.equals("New surname is not valid.", ignoreCase = true) -> {
                        Toast.makeText(context, "Niste uspesno izmenili personalne informacije", Toast.LENGTH_LONG).show()
                    }
                    jsonResponse.message.equals("New username is not valid.", ignoreCase = true) -> {
                        Toast.makeText(context, "Niste uspesno izmenili personalne informacije", Toast.LENGTH_LONG).show()
                    }
                    jsonResponse.message.equals("New email is not valid.", ignoreCase = true) -> {
                        Toast.makeText(context, "Niste uspesno izmenili personalne informacije", Toast.LENGTH_LONG).show()
                    }
                    //zauzeto
                    jsonResponse.message.equals("This username is already taken.", ignoreCase = true) -> {
                        Toast.makeText(context, "Niste uspesno izmenili personalne informacije", Toast.LENGTH_LONG).show()
                    }
                    jsonResponse.message.equals("This email is already taken.", ignoreCase = true) -> {
                        Toast.makeText(context, "Niste uspesno izmenili personalne informacije", Toast.LENGTH_LONG).show()
                    }
                }
            },
            { error ->
                println("Greska prilikom slanja: $error")
            }
        )
    }

    //EDIT PROFILA KRAJ

    //PRIKAZ I EDIT MAPE POCETAK

    private fun openMapsOverlay(dostavljacInfo: Dostavljac) {
        val dialog = Dialog(requireContext())
        dialog.requestWindowFeature(Window.FEATURE_NO_TITLE)
        dialog.setCancelable(true)
        dialog.setContentView(R.layout.overlay_household_location)

        val width = (resources.displayMetrics.widthPixels * 1.0).toInt()
        dialog.window?.setLayout(width, ViewGroup.LayoutParams.WRAP_CONTENT)
        val overlay_closeButton: ImageView      = dialog.findViewById<ImageView>(R.id.overlay_household_location_close)
        val overlay_person_household            = dialog.findViewById<TextView>(R.id.person_household)
        val overlay_person_username             = dialog.findViewById<TextView>(R.id.person_username)
        val overlay_person_location             = dialog.findViewById<TextView>(R.id.person_location)
        val overlay_map                         = dialog.findViewById<MapView>(R.id.mapView)
        val edit_map                            = dialog.findViewById<Button>(R.id.edit_maps)

        overlay_closeButton.setOnClickListener{
            dialog.dismiss()
        }

        if(dostavljacInfo.owner == true){
            edit_map.visibility = View.VISIBLE
            edit_map.setOnClickListener{
                showMapEdiOverlay()
                dialog.dismiss()
            }
        }
        edit_map.visibility = View.VISIBLE
        edit_map.setOnClickListener{
            showMapEdiOverlay()
            dialog.dismiss()
        }

        overlay_person_household.text = "Dostavljač " + dostavljacInfo.surname
        overlay_person_username.text  = "@" + dostavljacInfo.username
        overlay_person_location.text  = dostavljacInfo.location
        Map.setMap(overlay_map, dostavljacInfo.latitude, dostavljacInfo.longitude,requireActivity(), requireContext())
        dialog.show()
    }

    private fun showMapEdiOverlay() {
        val inflater = layoutInflater
        val dialogViewMap = inflater.inflate(R.layout.overlay_edit_map, null)
        val alertDialogBuilder = AlertDialog.Builder(requireContext())
        alertDialogBuilder.setView(dialogViewMap)
        val alertDialog: AlertDialog = alertDialogBuilder.create()

        person_household =              dialogViewMap.findViewById(R.id.person_household)
        person_username =               dialogViewMap.findViewById(R.id.person_username)
        prodavac_lokacija =             dialogViewMap.findViewById(R.id.lokacija)
        mapView =                       dialogViewMap.findViewById(R.id.mapView)
        confirm =                       dialogViewMap.findViewById(R.id.gotovo)
        close_edit_maps =               dialogViewMap.findViewById(R.id.close_edit_maps)

        person_household.text = "Dostavljač " + dostavljac_info.surname
        person_username.text = "@" + dostavljac_info.username
        setInitMap()

        prodavac_lokacija.setOnEditorActionListener{ _, actionId, _ ->
            if (actionId == EditorInfo.IME_ACTION_DONE) {
                // Pozvati željenu metodu ili izvršiti akciju kada se pritisne "Done" na tastaturi
                sendRequestForCoordinates()
            }
            false
        }

        alertDialog.show()
        close_edit_maps.setOnClickListener{
            alertDialog.dismiss()
        }
        confirm.setOnClickListener{
            sendNewCordinates(dostavljac_info.latitude,dostavljac_info.longitude)
            alertDialog.dismiss()
            openMapsOverlay(dostavljac_info)
        }
    }

    private fun sendNewCordinates(latitude: Double, longitude: Double)
    {
        var url:String = Config.ip_address+":"+ Config.port + "/changeAddress"

        val new_location = JSONObject().apply{
            put("latitude", latitude)
            put("longitude", longitude)
        }
        apiClient.sendPostRequestWithJSONObjectWithStringResponse(
            url,
            JWTService.getToken(),
            new_location,
            { response ->
                val gson = Gson()
                val typeToken = object : TypeToken<ResponseObjectTemplate<Object>>() {}.type
                var json_response = gson.fromJson<ResponseObjectTemplate<Object>>(response, typeToken)

                when(json_response.message){
                    "Address changed successfully!" -> {Toast.makeText(context, "Uspešno ste izmenili adresu", Toast.LENGTH_LONG).show()}
                }
            },
            { error ->
                println(error)
            })
    }

    fun sendRequestForCoordinates(){
        val address = prodavac_lokacija.text.toString()
        apiClient.getCoordinatesForAddress(address,
            { latitude, longitude ->
                requireActivity().runOnUiThread{
                    // Dobijene su koordinate (latitude i longitude)
                    setMap(latitude,longitude)
                    apiClient.getAddressFromCoordinates(requireContext(),latitude, longitude,
                        {response-> dostavljac_info.location = response}, {  })
                    println("Latitude: $latitude, Longitude: $longitude")
                }
            },
            {
                // Greška prilikom dobijanja koordinata
                println("Greška prilikom dobijanja koordinata.")
            })
    }

    override fun onRequestPermissionsResult(requestCode: Int, permissions: Array<out String>, grantResults: IntArray) {
        super.onRequestPermissionsResult(requestCode, permissions, grantResults)
        if (requestCode == REQUEST_CODE) {
            if (grantResults.isNotEmpty() && grantResults[0] == PackageManager.PERMISSION_GRANTED) {
                // Korisnik je odobrio pristup lokaciji
                getLocation()
            } else {
                // Korisnik je odbio pristup lokaciji
            }
        }
    }

    fun sendRequestForStringLocation(latitude: Double, longitude: Double){
        apiClient.getAddressFromCoordinates(requireContext(), latitude, longitude,
            { fullAddress ->
                requireActivity().runOnUiThread{
                    // Dobijena je adresa u obliku "Grad, Ulica"
                    prodavac_lokacija.setText(fullAddress)
                    println("Adresa: $fullAddress")
                }
            },
            {
                // Greška prilikom dobijanja adrese
                println("Greška prilikom dobijanja adrese.")
            })
    }

    private fun getLocation() {
        if (ContextCompat.checkSelfPermission(requireContext(), android.Manifest.permission.ACCESS_FINE_LOCATION) == PackageManager.PERMISSION_GRANTED) {
            fusedLocationClient.lastLocation.addOnSuccessListener { location ->
                if (location != null) {
                    val latitude = location.latitude
                    val longitude = location.longitude
                    println("Lokacija " + latitude + ":" + longitude)
                    sendRequestForStringLocation(latitude, longitude)
                    setMap(latitude,longitude)
                    Toast.makeText(requireContext(),"Vase koordinate: latitude: " + latitude + " longitude: " + longitude, Toast.LENGTH_LONG).show()
                } else {
                    // Ako je lokacija null, znači da nije dostupna
                    Toast.makeText(requireContext(),"Uključite lokacije na vašem uređaju, unesite ručno", Toast.LENGTH_LONG).show()
                }
            }
                .addOnFailureListener { exception ->
                    // Neuspeh pri dobijanju lokacije
                    println("Lokacija nije dostupna")
                }
        } else {
            // Ako nemate dozvolu, zatražite je od korisnika
            ActivityCompat.requestPermissions(requireActivity(), arrayOf(android.Manifest.permission.ACCESS_FINE_LOCATION), REQUEST_CODE)
        }
    }

    fun setMap(latitude:Double, longitude:Double){
        val newPoint = GeoPoint(latitude, longitude)
        live_latitude = latitude
        live_longitude = longitude
        mapView.controller.setCenter(newPoint)
        mapView.controller.setZoom(13.0)
        dostavljac_info.longitude = longitude
        dostavljac_info.latitude = latitude
        apiClient.getAddressFromCoordinates(requireContext(),dostavljac_info.latitude, dostavljac_info.longitude,
            {response-> dostavljac_info.location = response}, {  })
        // Dodavanje pina na tacnu lokaciju
        val items = ArrayList<OverlayItem>()
        val overlayItem = OverlayItem("Lokacija", "Lokacija domacinstva", newPoint)
        overlayItem.setMarker(ContextCompat.getDrawable(requireContext(), R.drawable.location_pin))
        items.add(overlayItem)

        val overlay = ItemizedIconOverlay<OverlayItem>(items, null, requireContext())
        mapView.overlays.clear()
        mapView.overlays.add(overlay)
    }

    fun setInitMap(){
        Configuration.getInstance().userAgentValue = requireActivity().packageName
        // Postavite parametre mape
        mapView.setTileSource(TileSourceFactory.MAPNIK)
        mapView.setBuiltInZoomControls(true)
        mapView.controller.setZoom(9.0)

        // Postavite početnu tačku mape (npr. Beograd)
        val startPoint = GeoPoint(44.7866, 20.4489)
        mapView.controller.setCenter(startPoint)

    }

    //PRIKAZ I EDIT MAPE KRAJ

    //PIE CHART
    private fun podaciPieChart() {
        val url: String

        if(delivererId >= 0)
            url = Config.ip_address + ":" + Config.port + "/accepted-rejected-offers?id=" + delivererId
        else
            url = Config.ip_address + ":" + Config.port + "/accepted-rejected-offers"

        if(!JWTService.getToken().equals("null")){
            apiClient.sendGetRequestEmpty(
                JWTService.getToken(), url,
                { response ->
                    val gson = Gson()
                    val jsonObject = JSONObject(response)
                    val dataObject = jsonObject.getJSONObject("data")

                    if(dataObject != null)
                    {
                        val rejected = dataObject.getInt("rejected")
                        val accepted = dataObject.getInt("accepted")
                        if(rejected > 0 && accepted > 0)
                            renderPieChart(accepted, rejected)
                        else
                        {
                            error_piechart.visibility = View.VISIBLE
                            pieChart.visibility = View.VISIBLE
                        }
                    }

                },
                { error -> println(error) }
            )
        }

        else{
            apiClient.sendGetRequestEmpty(url,
                { response ->
                    val gson = Gson()
                    val jsonObject = JSONObject(response)
                    val dataObject = jsonObject.getJSONObject("data")

                    if(dataObject != null)
                    {
                        val rejected = dataObject.getInt("rejected")
                        val accepted = dataObject.getInt("accepted")
                        if(rejected > 0 && accepted > 0)
                            renderPieChart(accepted, rejected)
                        else
                        {
                            error_piechart.visibility = View.VISIBLE
                            pieChart.visibility = View.VISIBLE
                        }
                    }

                },
                { error -> println(error) }
            )
        }


    }


    private fun getVehicleData() {
        val url: String

        if(delivererId >= 0)
            url = Config.ip_address + ":" + Config.port + "/deliverer/driving-licenses?id=" + delivererId
        else
            url = Config.ip_address + ":" + Config.port + "/deliverer/driving-licenses"

        if(!JWTService.getToken().equals("null")){
            apiClient.sendGetRequestEmpty(
                JWTService.getToken(), url,
                { response ->
                    println(response)
                    var gson = Gson()
                    val typeToken = object : TypeToken<ResponseObjectTemplate<Vozila>>() {}.type
                    val jsonResponse = gson.fromJson<ResponseObjectTemplate<Vozila>>(response, typeToken)
                    val vehicle = jsonResponse.data
                    println(vehicle)
                    renderVozila(vehicle)
                },
                { error -> println(error) }
            )
        }

        else
        {
            apiClient.sendGetRequestEmpty(url,
                { response ->
                    println(response)
                    var gson = Gson()
                    val typeToken = object : TypeToken<ResponseObjectTemplate<Vozila>>() {}.type
                    val jsonResponse = gson.fromJson<ResponseObjectTemplate<Vozila>>(response, typeToken)
                    val vehicle = jsonResponse.data
                    println(vehicle)
                    renderVozila(vehicle)
                },
                { error -> println(error) }
            )
        }

    }

    @SuppressLint("MissingInflatedId")
    private fun openVehicleEditOverlay(vozilo : Vozila) {
        val inflater = layoutInflater
        val vehicle_dialog = inflater.inflate(R.layout.overlay_edit_vehicle, null)
        val alertDialogBuilder = AlertDialog.Builder(requireContext())
        alertDialogBuilder.setView(vehicle_dialog)
        val alertDialog: AlertDialog = alertDialogBuilder.create()

        automobil       = vehicle_dialog.findViewById<FrameLayout>(R.id.auto)
        motocikl        = vehicle_dialog.findViewById<FrameLayout>(R.id.motocikl)
        kombi           = vehicle_dialog.findViewById<FrameLayout>(R.id.kombi)
        kamion          = vehicle_dialog.findViewById<FrameLayout>(R.id.kamion)
        close_overlay   = vehicle_dialog.findViewById<ImageView>(R.id.close)
        confirm_vozilo  = vehicle_dialog.findViewById(R.id.gotovo)
        confirm_vozilo.isEnabled = false
        confirm_vozilo.setBackgroundResource(R.drawable.full_fill_button_disabled)

        alertDialog.show()

        close_overlay.setOnClickListener{ alertDialog.dismiss() }

        setVehicle("auto",vozilo.CAR)
        setVehicle("motocikl",vozilo.MOTORCYCLE)
        setVehicle("kombi",vozilo.VAN)
        setVehicle("kamion",vozilo.TRUCK)

        automobil.setOnClickListener{
            if(vozilo.CAR) vozilo.CAR = false
            else vozilo.CAR = true
            setVehicle("auto",vozilo.CAR)
            confirm_vozilo.isEnabled = true
            confirm_vozilo.setBackgroundResource(R.drawable.full_fill_button)}

        motocikl.setOnClickListener{
            if(vozilo.MOTORCYCLE) vozilo.MOTORCYCLE = false
            else vozilo.MOTORCYCLE = true
            setVehicle("motocikl",vozilo.MOTORCYCLE)
            confirm_vozilo.isEnabled = true
            confirm_vozilo.setBackgroundResource(R.drawable.full_fill_button)}

        kombi.setOnClickListener{
            if(vozilo.VAN) vozilo.VAN = false
            else vozilo.VAN = true
            setVehicle("kombi",vozilo.VAN)
            confirm_vozilo.isEnabled = true
            confirm_vozilo.setBackgroundResource(R.drawable.full_fill_button)}

        kamion.setOnClickListener{
            if(vozilo.TRUCK) vozilo.TRUCK = false
            else vozilo.TRUCK = true
            setVehicle("kamion",vozilo.TRUCK)
            confirm_vozilo.isEnabled = true
            confirm_vozilo.setBackgroundResource(R.drawable.full_fill_button)}

        confirm_vozilo.setOnClickListener {
            println("Provera " + vozilo)
            if (vozilo.VAN == false && vozilo.TRUCK == false && vozilo.MOTORCYCLE == false && vozilo.CAR == false)
                Toast.makeText(context, "Morate izabrati bar jedno vozilo", Toast.LENGTH_LONG).show()

            else
            {
                sendNewVehicleChoice(vozilo)
                alertDialog.dismiss()
            }
        }
        
        alertDialog.setOnDismissListener{ renderVozila(vozilo)}
    }


    private fun sendNewVehicleChoice(vozilo: Vozila) {
        val url: String = Config.ip_address + ":" + Config.port + "/deliverer/update-driving-licenses"
        val objekat = JSONObject().apply{
            put("VAN"        , vozilo.VAN)
            put("CAR"        , vozilo.CAR)
            put("TRUCK"      , vozilo.TRUCK)
            put("MOTORCYCLE" , vozilo.MOTORCYCLE)
        }
        apiClient.sendPostRequestWithJSONObjectWithStringResponse(
            url,
            JWTService.getToken(),
            objekat,
            { response ->
                println("Uspesno poslato: $response")
                val gson = Gson()
                val typeToken = object : TypeToken<ResponseObjectTemplate<Object>>() {}.type
                val jsonResponse = gson.fromJson<ResponseObjectTemplate<Object>>(response, typeToken)
                when
                {
                    jsonResponse.message.equals("Vozacke dozvole za dostavljaca", ignoreCase = true) -> {
                        Toast.makeText(context, "Uspesno ste se izmenili vaša vozila", Toast.LENGTH_LONG).show()
                    }
                }
            },
            { error ->
                println("Greska prilikom slanja: $error")
            }
        )
    }

    fun setVehicle(vozilo : String, flag: Boolean){
        when(vozilo){
            "auto"          ->          {   if(flag) automobil.setBackgroundResource(R.drawable.border_background_orange)
                                            else automobil.setBackgroundResource(R.drawable.border_background)}
            "motocikl"      ->          {   if(flag) motocikl.setBackgroundResource(R.drawable.border_background_orange)
                                            else motocikl.setBackgroundResource(R.drawable.border_background)}
            "kombi"         ->          {   if(flag) kombi.setBackgroundResource(R.drawable.border_background_orange)
                                            else kombi.setBackgroundResource(R.drawable.border_background)}
            "kamion"        ->          {   if(flag) kamion.setBackgroundResource(R.drawable.border_background_orange)
                                            else kamion.setBackgroundResource(R.drawable.border_background)}
        }
    }


    override fun onCreateView(
        inflater: LayoutInflater, container: ViewGroup?,
        savedInstanceState: Bundle?
    ): View? {
        view = inflater.inflate(R.layout.fragment_deliverer_profile, container, false)
        getAllStuff()

//        try{
//            val args: DelivererProfileFragmentArgs = DelivererProfileFragmentArgs.fromBundle(requireArguments())
//            delivererId = args.delivererId
//            println("deliverer: " + delivererId)
//        }
//        catch (exception:Exception){}

        privremeni_logout.setOnClickListener{ JWTService.logOut(requireActivity())}
        lokacija.setImageResource(R.drawable.maps)
        lokacija.setOnClickListener{
            openMapsOverlay(dostavljac_info)
        }

        val scope = CoroutineScope(Dispatchers.Default)
        scope.launch {
            async { getPersonalInformation() }
            async { getVehicleData() }
            async { podaciPieChart() }
        }

        return view
    }

    private fun setiVisibilityPerAuthorization() {
        moj_profil.isEnabled = false
        edit_profile.visibility = View.GONE
        privremeni_logout.visibility = View.GONE
        prevozna_sredstva.isEnabled = false

    }

    private lateinit var image_place: ImageView
    @SuppressLint("MissingInflatedId")
    private fun renderProfileInformation(dostavljacInfo: Dostavljac) {

        var row : LinearLayout
        row = LinearLayout(requireContext())
        row.orientation = LinearLayout.HORIZONTAL
        val layoutParams = LinearLayout.LayoutParams(
            LinearLayout.LayoutParams.MATCH_PARENT,
            LinearLayout.LayoutParams.WRAP_CONTENT
        )
        row.layoutParams = layoutParams
        profile_information.addView(row)

        val itemView = layoutInflater.inflate(R.layout.component_profile_info,null)
        var profileImage = itemView.findViewById<CircleImageView>(R.id.profile_pic)
        var name_surname = itemView.findViewById<TextView>(R.id.name)
        var username = itemView.findViewById<TextView>(R.id.username)
        var broj_pracenja = itemView.findViewById<TextView>(R.id.broj_porudzbina)
        broj_pracenja.visibility = View.GONE
        var text_pracenja = itemView.findViewById<TextView>(R.id.orders_text)
        text_pracenja.visibility = View.GONE
        var srednja_ocena = itemView.findViewById<TextView>(R.id.broj_pracenja)
        var text_ocena = itemView.findViewById<TextView>(R.id.follow_text)

        name_surname.text = dostavljacInfo.name + " " + dostavljacInfo.surname
        username.text = "@" + dostavljacInfo.username

        if(dostavljacInfo.avgGrade < 0)
        {
            srednja_ocena.text = "-"
        }
        else srednja_ocena.setText("%.2f".format(dostavljacInfo.avgGrade))
        text_ocena.text = "Ocena"

//        profileImage.layoutParams.width = 150
//        profileImage.layoutParams.height = 150
        if(dostavljacInfo.picture.equals("null")){
            Image.setImageResource(profileImage, null, -1)
        }
        else{
            Image.setImageResource(profileImage, dostavljacInfo.picture, -1)
        }

        image_place = profileImage
        profileImage.setOnClickListener{eventForChangeProfileImage()}

        edit_profile.setOnClickListener { showEditProfileOverlay(dostavljacInfo) }
        moj_profil.setOnClickListener { showEditProfileOverlay(dostavljacInfo) }

        val marginInPx = 4
        val itemLayoutParams = LinearLayout.LayoutParams(
            0,
            LinearLayout.LayoutParams.WRAP_CONTENT,
            1f
        )
        itemLayoutParams.setMargins(marginInPx, 0, marginInPx, 0)
        itemView.setPadding(marginInPx, marginInPx, marginInPx, marginInPx)
        itemView.layoutParams = itemLayoutParams
        row.addView(itemView)

        if (row.parent == null) {
            profile_information.addView(row)
        }
    }


    private fun eventForChangeProfileImage(){
        image_place.setOnClickListener {
            val intent = Intent(Intent.ACTION_PICK, MediaStore.Images.Media.EXTERNAL_CONTENT_URI)
            startActivityForResult(intent, 1)
        }
    }

    override fun onActivityResult(requestCode: Int, resultCode: Int, data: Intent?) {
        super.onActivityResult(requestCode, resultCode, data)

        if (resultCode == Activity.RESULT_OK && data != null && data.data != null) {
            val imageUri: Uri = data.data!!

            when (requestCode) {
                1 -> {
                    image_place.setImageURI(imageUri)
                    var jasonObject:JSONObject = JSONObject()
                    jasonObject.put("image",Image.uriToBase64(requireContext(), imageUri) )
                    var url: String = Config.ip_address+":"+Config.port+"/profile-image-change"

                    apiClient.sendPostRequestWithJSONObjectWithStringResponse(url, JWTService.getToken(),
                        jasonObject,
                        {response->
                            println(response)
                        },
                        {error->
                            println(error)
                        })
                }
            }
        }
    }

    private fun renderPieChart(accepted: Int, rejected: Int) {
        pieChart.setUsePercentValues(true)
        pieChart.getDescription().setEnabled(false)
        pieChart.setExtraOffsets(5f, 10f, 5f, 5f)
        pieChart.setDragDecelerationFrictionCoef(0.95f)

        pieChart.setDrawHoleEnabled(true)
        pieChart.setHoleColor(Color.WHITE)

        pieChart.setTransparentCircleColor(Color.WHITE)
        pieChart.setTransparentCircleAlpha(110)

        pieChart.setHoleRadius(58f)
        pieChart.setTransparentCircleRadius(61f)

        pieChart.setDrawCenterText(true)
        pieChart.setRotationAngle(0f)
        pieChart.setRotationEnabled(true)
        pieChart.setHighlightPerTapEnabled(true)

        pieChart.legend.isEnabled = false
        pieChart.setEntryLabelColor(Color.WHITE)
        pieChart.setEntryLabelTextSize(12f)

        val entries: ArrayList<PieEntry> = ArrayList()
        entries.add(PieEntry(accepted.toFloat()))
        entries.add(PieEntry(rejected.toFloat()))
        //hardkodovano
        //entries.add(PieEntry(20f))
        //entries.add(PieEntry(40f))

        val dataSet = PieDataSet(entries, "Deliverer")

        dataSet.setDrawIcons(false)

        dataSet.sliceSpace = 3f
        dataSet.iconsOffset = MPPointF(0f, 40f)
        dataSet.selectionShift = 5f

        val colors: ArrayList<Int> = ArrayList()
        colors.add(resources.getColor(R.color.orange))
        colors.add(resources.getColor(R.color.dark_blue))

        dataSet.colors = colors

        val data = PieData(dataSet)
        data.setValueFormatter(PercentFormatter())
        data.setValueTextSize(15f)
        data.setValueTypeface(Typeface.DEFAULT_BOLD)
        data.setValueTextColor(Color.WHITE)
        pieChart.setData(data)

        pieChart.highlightValues(null)
        pieChart.invalidate()
    }

    @SuppressLint("MissingInflatedId")
    private fun renderVozila(vehicle: Vozila) {
        lista_vozila.removeAllViews()
        prevozna_sredstva.setOnClickListener { openVehicleEditOverlay(vehicle) }

        val vozilaList = listOf(
            Pair("Kombi", vehicle.VAN),
            Pair("Auto", vehicle.CAR),
            Pair("Kamion", vehicle.TRUCK),
            Pair("Motocikl", vehicle.MOTORCYCLE)
        )

        println("Vozila list: "+ vozilaList)
        val trueCount = vozilaList.count { it.second }
        lista_vozila.columnCount = trueCount

        for ((vozilo, dostupnost) in vozilaList) {
            if (dostupnost) {
                println(vozilo)
                val voziloView = layoutInflater.inflate(R.layout.component_vehicle, null)
                val productName = voziloView.findViewById<TextView>(R.id.product_name)
                val productImage = voziloView.findViewById<ImageView>(R.id.product_image)
                productName.text = vozilo
                productImage.layoutParams.width = 150
                productImage.layoutParams.height = 150
                when (vozilo) {
                    "Kombi" -> {
                        productImage.setImageResource(R.drawable.kombi)
                    }
                    "Auto" -> {
                        productImage.setImageResource(R.drawable.auto)
                    }
                    "Kamion" -> {
                        productImage.setImageResource(R.drawable.kamion)
                    }
                    "Motocikl" -> {
                        productImage.setImageResource(R.drawable.motocikl)
                    }
                }

                val params = GridLayout.LayoutParams()
                params.width = GridLayout.LayoutParams.WRAP_CONTENT
                params.height = GridLayout.LayoutParams.WRAP_CONTENT
                params.setMargins(8, 8, 8, 20)
                voziloView.layoutParams = params

                lista_vozila.addView(voziloView)
            }
        }
    }
}

