package com.example.batmobile

import android.annotation.SuppressLint
import android.app.Activity
import android.app.AlertDialog
import android.app.Dialog
import android.app.TimePickerDialog
import android.content.Intent
import android.content.pm.PackageManager
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
import androidx.navigation.fragment.findNavController
import com.example.batmobile.DTOFromServer.NewNews
import com.example.batmobile.DTOFromServer.PostCommentsDTO
import com.example.batmobile.DTOFromServer.PostDetails
import com.example.batmobile.DTOFromServer.Prodavac
import com.example.batmobile.DTOFromServer.ProductAll
import com.example.batmobile.DTOFromServer.ResponseListTemplate
import com.example.batmobile.DTOFromServer.ResponseObjectTemplate
import com.example.batmobile.DTOFromServer.WorkingTime
import com.example.batmobile.enums.Role
import com.example.batmobile.fragments.ProductViewFragmentDirections
import com.example.batmobile.fragments.seller.SellerAddProductFragmentDirections
import com.example.batmobile.network.ApiClient
import com.example.batmobile.network.Config
import com.example.batmobile.services.Comments
import com.example.batmobile.services.Image
import com.example.batmobile.services.JWTService
import com.example.batmobile.services.Map
import com.example.batmobile.services.Validator
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
import java.text.SimpleDateFormat
import java.util.Calendar

//treba da se uradi da kada se klikne na sliku da moze da je izmeni

class SellerProfileFragment : Fragment() {

    //aleksaleks
    //Grosnica04
    private var isLogged                                        : Boolean = false
    private var originalProfileContent                          : View? = null
    private lateinit var view                                   : View
    private lateinit var apiClient                              : ApiClient
    private          var sellerId                               : Int = -1
    lateinit var seller_info                                    : Prodavac
    lateinit var seller_info_new                                : Prodavac
    lateinit var moj_profil                                     : TextView
    //lateinit var working_time                                   : WorkingTime

    private lateinit var profile_information                    : ConstraintLayout
    private lateinit var privremeni_logout                      : Button
    private lateinit var close_fragment                         : Button
    private lateinit var edit_profile                           : Button
    private lateinit var lokacija                               : ImageView

    lateinit var image                                          : CircleImageView
    lateinit var name                                           : EditText
    lateinit var lastname                                       : EditText
    lateinit var username                                       : EditText
    lateinit var email                                          : EditText
    lateinit var old_password                                   : EditText
    lateinit var password                                       : EditText
    lateinit var password_confirm                               : EditText

    lateinit var error_name                                     : TextView
    lateinit var error_lastname                                 : TextView
    lateinit var error_username                                 : TextView
    lateinit var error_email                                    : TextView
    lateinit var error_password_old                             : TextView
    lateinit var error_password                                 : TextView
    lateinit var error_password_confirm                         : TextView

    lateinit var btn_nastavi                                    : Button
    lateinit var close                                          : ImageView

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
    lateinit var error_radno                                    : TextView

    //deo za objave i proizvode
    lateinit var dugme_proizvodi                                : Button
    lateinit var dugme_objave                                   : Button
    lateinit var menu_kategorije                                : LinearLayout
    lateinit var search                                         : EditText
    lateinit var dugme_add                                      : ImageView
    lateinit var text_add                                       : TextView
    lateinit var list_of_posts                                  : List<NewNews>
    lateinit var list_of_objects                                : ConstraintLayout
    //lateinit var scroll_products                                : ConstraintLayout
    lateinit var grid_of_products                               : GridLayout
    lateinit var dugme0                                         : Button
    lateinit var dugme1                                         : Button
    lateinit var dugme2                                         : Button
    lateinit var dugme3                                         : Button
    lateinit var dugme4                                         : Button
    lateinit var dugme5                                         : Button
    lateinit var dugme6                                         : Button
    lateinit var dugme7                                         : Button
    lateinit var dugme8                                         : Button
    lateinit var radno_vreme                                    : ConstraintLayout
    lateinit var naslov_radno                                   : TextView

    lateinit var products                                       : MutableList<ProductAll>
    lateinit var products_filtered                              : MutableList<ProductAll>
    lateinit var product                                        : ProductAll
    lateinit var tekst_dodavanje                                : TextView
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

    lateinit var seller_username                                : TextView
    lateinit var post_text                                      : EditText
    lateinit var close_edit_post                                : ImageView
    lateinit var dodaj_post                                     : Button


    lateinit var textView                                       : TextView
    lateinit var close_working_time                             : ImageView
    lateinit var ponedeljak                                     : Button
    lateinit var utorak                                         : Button
    lateinit var sreda                                          : Button
    lateinit var cetvrtak                                       : Button
    lateinit var petak                                          : Button
    lateinit var subota                                         : Button
    lateinit var nedelja                                        : Button
    lateinit var izmeni_working_time                            : Button
    lateinit var resetuj_working_time                           : Button
    lateinit var start_time                                     : Button
    lateinit var vreme_start                                    : TextView
    lateinit var end_time                                       : Button
    lateinit var vreme_end                                      : TextView
    var startFlag                                               : Boolean = false
    var endFlag                                                 : Boolean = false
    lateinit var dan                                            : TextView
    lateinit var confirm_new_working_time                       : Button

    private fun getAllStuff(){
        products = mutableListOf()
        products_filtered = mutableListOf()
        seller_info = Prodavac(0,"","","","","","","","",0.0,0.0,0,0,0.0, false, false, 0)
        product = ProductAll(0,"","","",0.0,"","",0)
        apiClient = ApiClient(requireContext())
        profile_information =           view.findViewById(R.id.profile_info)
        lokacija =                      view.findViewById<ImageView>(R.id.broj_lajkova)
        privremeni_logout =             view.findViewById(R.id.privremeni_logout)
        close_fragment =                view.findViewById<Button>(R.id.close_fragment)
        edit_profile =                  view.findViewById(R.id.edit_profile)
        moj_profil =                    view.findViewById(R.id.textView5)
        error_radno =                   view.findViewById(R.id.error_radno)
        naslov_radno =                  view.findViewById(R.id.naslov_radno)
        //working_time = WorkingTime("","","","","","","")

        radno_vreme =                   view.findViewById(R.id.radno_vreme)

        dugme_proizvodi =               view.findViewById(R.id.proizvodi)
        dugme_objave =                  view.findViewById(R.id.objave)

        search =                        view.findViewById(R.id.search)
        dugme_add =                     view.findViewById(R.id.add_dugme)
        text_add =                      view.findViewById(R.id.add_text)
        list_of_objects =               view.findViewById(R.id.scroll)
        menu_kategorije =               view.findViewById(R.id.slider)
        grid_of_products =              view.findViewById(R.id.grid_of_products)
        tekst_dodavanje =               view.findViewById(R.id.add_text)

        dugme0 = view.findViewById(R.id.sve)
        dugme1 = view.findViewById(R.id.sveze_meso)
        dugme2 = view.findViewById(R.id.mlecni)
        dugme3 = view.findViewById(R.id.preradjevine)
        dugme4 = view.findViewById(R.id.namazi)
        dugme5 = view.findViewById(R.id.biljna_ulja)
        dugme6 = view.findViewById(R.id.zitarice)
        dugme7 = view.findViewById(R.id.napici)
        dugme8 = view.findViewById(R.id.voce_povrce)
    }

    //EDIT INFORMACIJA
    private fun showEditProfileOverlay(seller_podaci : Prodavac) {
        val inflater = layoutInflater
        val dialogView = inflater.inflate(R.layout.overlay_change_profile_info, null)
        val alertDialogBuilder = AlertDialog.Builder(requireContext())
        alertDialogBuilder.setView(dialogView)
        val alertDialog: AlertDialog = alertDialogBuilder.create()
        originalProfileContent = profile_information.getChildAt(0)

        seller_info_new = Prodavac(0,"","","","","","","","",0.0,0.0,0,0,0.0, false, false,0)
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

        name.setHint(seller_podaci.name)
        name.setText("")
        lastname.setHint(seller_podaci.surname)
        lastname.setText("")
        username.setHint(seller_podaci.username)
        username.setText("")
        email.setHint(seller_podaci.email)
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
            checkNewInformations(seller_info_new)
            //slanje nove lozinke na server na proveru
            sendPasswordDataToServer(seller_info_new.password)
            if(!seller_info_new.username.equals(""))
            {
                Toast.makeText(context, "Morate se ponovo ulogovati", Toast.LENGTH_LONG).show()
                JWTService.logOut(requireActivity())
                seller_info_new = Prodavac(0,"","","","","","","","",0.0,0.0,0,0,0.0, true, false,0)
            }
            //zatvaranje dijaloga nakon uspešnog slanja podataka
            alertDialog.dismiss()
        }

        name.setOnFocusChangeListener { _, hasFocus ->
            if (!hasFocus) {
                if (!name.text.toString().equals("")) {
                    seller_info_new.name = name.text.toString()
                    validatorUnosa(seller_info_new.name, "name", error_name)
                }
                else flagName = true
            }
        }
        name.setOnEditorActionListener { _, actionId, _ ->
            if (actionId == EditorInfo.IME_ACTION_DONE) {
                if (!name.text.toString().equals("")) {
                    seller_info_new.name = name.text.toString()
                    validatorUnosa(seller_info_new.name, "name", error_name)
                }
                else flagName = true
            }
            false
        }

        lastname.setOnFocusChangeListener { _, hasFocus ->
            if (!hasFocus) {
                if (!lastname.text.toString().equals("")) {
                    seller_info_new.surname = lastname.text.toString()
                    validatorUnosa(seller_info_new.surname, "lastname", error_lastname)
                }
                else flagLastname = true
            }
        }
        lastname.setOnEditorActionListener { _, actionId, _ ->
            if (actionId == EditorInfo.IME_ACTION_DONE) {
                if (!lastname.text.toString().equals("")) {
                    seller_info_new.surname = lastname.text.toString()
                    validatorUnosa(seller_info_new.surname, "lastname", error_lastname)
                }
                else flagLastname = true
            }
            false
        }

        username.setOnFocusChangeListener { _, hasFocus ->
            if (!hasFocus) {
                if (!username.text.equals("")) {
                    seller_info_new.username = username.text.toString()
                    validatorUnosa(seller_info_new.username, "username", error_username)
                    checkExisting("username", seller_info_new.username)
                }
                else flagUsername = true
            }
        }
        username.setOnEditorActionListener { _, actionId, _ ->
            if (actionId == EditorInfo.IME_ACTION_DONE) {
                if (!username.text.toString().equals("")) {
                    seller_info_new.username = username.text.toString()
                    validatorUnosa(seller_info_new.username, "username", error_username)
                    checkExisting("username", seller_info_new.username)
                }
                else flagUsername = true
            }
            false
        }

        email.setOnFocusChangeListener { _, hasFocus ->
            if (!hasFocus) {
                if (!email.text.toString().equals("")) {
                    seller_info_new.email = email.text.toString()
                    validateEmail(seller_info_new.email, error_email)
                    checkExisting("email", seller_info_new.email)
                }
                else flagEmail = true
            }
        }
        email.setOnEditorActionListener { _, actionId, _ ->
            if (actionId == EditorInfo.IME_ACTION_DONE) {
                if (!email.text.toString().equals("")) {
                    seller_info_new.email = email.text.toString()
                    validateEmail(seller_info_new.email, error_email)
                    checkExisting("email", seller_info_new.email)
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
                    seller_info_new.password = password.text.toString()
                    validatePassword(password, error_password)
                }
                else flagPassword = true
            }
        }
        password.setOnEditorActionListener { _, actionId, _ ->
            if (actionId == EditorInfo.IME_ACTION_DONE) {
                if (!password.text.toString().equals("")) {
                    seller_info_new.password = password.text.toString()
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
            "true"->                                                            { flagPassword = true; error_text.setText(response); error_text.visibility = View.INVISIBLE; seller_info_new.password = text.text.toString()}
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
                    Log.d("TAG", response)
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
                    Log.e("TAG", "Error: $error")
                }
            )
        }
    }

    private fun checkNewInformations(seller_new: Prodavac)
    {
        val jsonObject: JSONObject = JSONObject()
        var ime = seller_new.name
        var prezime = seller_new.surname
        var username = seller_new.username
        var email = seller_new.email
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
    //DUGMICI

    private fun setAllEventListenet(){
        privremeni_logout.setOnClickListener{ JWTService.logOut(requireActivity())}



        lokacija.setImageResource(R.drawable.maps)
        lokacija.setOnClickListener{
            openMapsOverlay(seller_info)
        }

        dugme_objave.setOnClickListener{
            tekst_dodavanje.setText("Dodaj objavu")
            promeniBojuDugmeta(dugme_objave)
            list_of_objects.visibility = View.VISIBLE
            dugme_add.setOnClickListener { showPostAddOverlay() }
            tekst_dodavanje.setOnClickListener{ showPostAddOverlay() }
            menu_kategorije.visibility = View.INVISIBLE
            grid_of_products.visibility = View.INVISIBLE
            search.visibility = View.INVISIBLE
            showSellersPosts()
        }
        dugme_proizvodi.setOnClickListener{
            dugme0.performClick()
            tekst_dodavanje.setText("Dodaj proizvod")
            dugme0 = view.findViewById(R.id.sve)
            promeniBojuDugmeta(dugme_proizvodi)
            list_of_objects.visibility = View.INVISIBLE
            menu_kategorije.visibility = View.VISIBLE
            grid_of_products.visibility = View.VISIBLE
            search.visibility = View.VISIBLE
            dugme_add.setOnClickListener{
                val action = SellerAddProductFragmentDirections.actionAddProductSeller(-1)
                findNavController().navigate(action)
            }
            text_add.setOnClickListener{
                val action = SellerAddProductFragmentDirections.actionAddProductSeller(-1)
                findNavController().navigate(action)
            }
            showMenu()
        }

        search.addTextChangedListener(object : TextWatcher {
            override fun beforeTextChanged(s: CharSequence?, start: Int, count: Int, after: Int) {
                // Pre promene teksta
            }

            override fun onTextChanged(s: CharSequence?, start: Int, before: Int, count: Int) {
                val searchText = s.toString()
                println(searchText)
                println(products)
                findProduct(searchText)
            }

            override fun afterTextChanged(s: Editable?) {
                val searchText = s.toString()
                println(searchText)
                findProduct(searchText)
            }
        })
    }

    @SuppressLint("MissingInflatedId")
    private fun openEditWorkingTimeOverlay(working_time: WorkingTime) {
        val inflater = layoutInflater
        val workingTimeDialog = inflater.inflate(R.layout.overlay_change_working_time, null)
        val alertDialogBuilder = AlertDialog.Builder(requireContext())
        alertDialogBuilder.setView(workingTimeDialog)
        val alertDialog: AlertDialog = alertDialogBuilder.create()

        textView                    = workingTimeDialog.findViewById(R.id.timeTv)
        close_working_time          = workingTimeDialog.findViewById(R.id.close)
        ponedeljak                  = workingTimeDialog.findViewById(R.id.pon)
        utorak                      = workingTimeDialog.findViewById(R.id.uto)
        sreda                       = workingTimeDialog.findViewById(R.id.sre)
        cetvrtak                    = workingTimeDialog.findViewById(R.id.cet)
        petak                       = workingTimeDialog.findViewById(R.id.pet)
        subota                      = workingTimeDialog.findViewById(R.id.sub)
        nedelja                     = workingTimeDialog.findViewById(R.id.ned)
        dan                         = workingTimeDialog.findViewById(R.id.dan)
        izmeni_working_time         = workingTimeDialog.findViewById(R.id.izmeni)
        resetuj_working_time        = workingTimeDialog.findViewById(R.id.reset)

        start_time                  = workingTimeDialog.findViewById(R.id.start_time)
        vreme_start                 = workingTimeDialog.findViewById(R.id.vreme_start)
        end_time                    = workingTimeDialog.findViewById(R.id.end_time)
        vreme_end                   = workingTimeDialog.findViewById(R.id.vreme_end)
        confirm_new_working_time    = workingTimeDialog.findViewById(R.id.potvrdi_edit)

        confirm_new_working_time.setBackgroundResource(R.drawable.full_fill_button_disabled)
        confirm_new_working_time.isEnabled = false
        start_time.setBackgroundResource(R.drawable.full_fill_button_disabled)
        start_time.isEnabled = false
        end_time.setBackgroundResource(R.drawable.full_fill_button_disabled)
        end_time.isEnabled = false
        resetuj_working_time.setBackgroundResource(R.drawable.full_fill_button_disabled)
        resetuj_working_time.isEnabled = false

        println("PRVI " + working_time)

        ponedeljak.setOnClickListener   { changeWorkingTime("Ponedeljak",working_time)
                                            promeniBojuDugmetaNakonKlika(ponedeljak)}
        utorak.setOnClickListener       { changeWorkingTime("Utorak",working_time)
                                            promeniBojuDugmetaNakonKlika(utorak)}
        sreda.setOnClickListener        { changeWorkingTime("Sreda",working_time)
                                            promeniBojuDugmetaNakonKlika(sreda)}
        cetvrtak.setOnClickListener     { changeWorkingTime("Četvrtak",working_time)
                                            promeniBojuDugmetaNakonKlika(cetvrtak)}
        petak.setOnClickListener        { changeWorkingTime("Petak",working_time)
                                            promeniBojuDugmetaNakonKlika(petak)}
        subota.setOnClickListener       { changeWorkingTime("Subota",working_time)
                                            promeniBojuDugmetaNakonKlika(subota)}
        nedelja.setOnClickListener      { changeWorkingTime("Nedelja",working_time)
                                            promeniBojuDugmetaNakonKlika(nedelja)}

        confirm_new_working_time.setOnClickListener {
            println("Za server" + working_time)
            if(working_time.monday    == "") working_time.monday    = "ZATVORENO"
            if(working_time.tuesday   == "") working_time.tuesday   = "ZATVORENO"
            if(working_time.wednesday == "") working_time.wednesday = "ZATVORENO"
            if(working_time.thursday  == "") working_time.thursday  = "ZATVORENO"
            if(working_time.friday    == "") working_time.friday    = "ZATVORENO"
            if(working_time.saturday  == "") working_time.saturday  = "ZATVORENO"
            if(working_time.sunday    == "") working_time.sunday    = "ZATVORENO"
            println("Za server sredjeno " + working_time)
            sendNewWorkingTimeToServer(working_time)
            renderWorkingTime(working_time)
            alertDialog.dismiss()
        }

        close_working_time.setOnClickListener { alertDialog.dismiss() }

        alertDialog.show()
    }

    private fun sendNewWorkingTimeToServer(new_working_time: WorkingTime) {
        var url:String = Config.ip_address+":"+ Config.port + "/setWorkingTime"

        val new_time = JSONObject().apply{
            put("monday"    , new_working_time.monday)
            put("tuesday"   , new_working_time.tuesday  )
            put("wednesday" , new_working_time.wednesday)
            put("thursday"  , new_working_time.thursday )
            put("friday"    , new_working_time.friday   )
            put("saturday"  , new_working_time.saturday )
            put("sunday"    , new_working_time.sunday   )
        }
        apiClient.sendPostRequestWithJSONObjectWithStringResponse(
            url,
            JWTService.getToken(),
            new_time,
            { response ->
                println(response)
                val gson = Gson()
                val typeToken = object : TypeToken<ResponseObjectTemplate<Object>>() {}.type
                var json_response = gson.fromJson<ResponseObjectTemplate<Object>>(response, typeToken)

                when(json_response.message){
                    "Radno vreme uspesno setovano!" -> {Toast.makeText(context, "Radno vreme uspešno setovano!", Toast.LENGTH_LONG).show()}
                }
            },
            { error ->
                println(error)
            })

    }

    private fun promeniBojuDugmetaNakonKlika(dugme: Button) {
        ponedeljak.setBackgroundResource(R.drawable.empty_button)
        ponedeljak.setTextColor(ContextCompat.getColor(requireContext(), R.color.black))
        utorak.setBackgroundResource(R.drawable.empty_button)
        utorak.setTextColor(ContextCompat.getColor(requireContext(), R.color.black))
        sreda.setBackgroundResource(R.drawable.empty_button)
        sreda.setTextColor(ContextCompat.getColor(requireContext(), R.color.black))
        cetvrtak.setBackgroundResource(R.drawable.empty_button)
        cetvrtak.setTextColor(ContextCompat.getColor(requireContext(), R.color.black))
        petak.setBackgroundResource(R.drawable.empty_button)
        petak.setTextColor(ContextCompat.getColor(requireContext(), R.color.black))
        subota.setBackgroundResource(R.drawable.empty_button)
        subota.setTextColor(ContextCompat.getColor(requireContext(), R.color.black))
        nedelja.setBackgroundResource(R.drawable.empty_button)
        nedelja.setTextColor(ContextCompat.getColor(requireContext(), R.color.black))

        dugme.setBackgroundResource(R.drawable.full_fill_button)
        dugme.setTextColor(ContextCompat.getColor(requireContext(), R.color.white))
    }

    private fun changeWorkingTime(dan_u_nedelji : String, working_time : WorkingTime)
    {
        resetuj_working_time.setBackgroundResource(R.drawable.full_fill_button)
        resetuj_working_time.isEnabled = true
        start_time.setBackgroundResource(R.drawable.full_fill_button)
        start_time.isEnabled = true
        end_time.setBackgroundResource(R.drawable.full_fill_button)
        end_time.isEnabled = true
        when(dan_u_nedelji)
        {
            "Ponedeljak"        -> { dan.text      = "Radno vreme: " + working_time.monday   }
            "Utorak"            -> { dan.text      = "Radno vreme: " + working_time.tuesday  }
            "Sreda"             -> { dan.text      = "Radno vreme: " + working_time.wednesday}
            "Četvrtak"          -> { dan.text      = "Radno vreme: " + working_time.thursday }
            "Petak"             -> { dan.text      = "Radno vreme: " + working_time.friday   }
            "Subota"            -> { dan.text      = "Radno vreme: " + working_time.saturday }
            "Nedelja"           -> { dan.text      = "Radno vreme: " + working_time.sunday   }
        }
        vreme_start.text = "Početak rada:"
        vreme_end.text = "Završetak rada:"
        izmeni_working_time.setBackgroundResource(R.drawable.full_fill_button_disabled)
        izmeni_working_time.isEnabled = false
        startFlag = false
        endFlag = false
        start_time.setOnClickListener {
            val cal = Calendar.getInstance()
            val timeSetListener = TimePickerDialog.OnTimeSetListener { timePicker, hour, minute ->
                cal.set(Calendar.HOUR_OF_DAY, hour)
                cal.set(Calendar.MINUTE, minute)
                val formattedTime = SimpleDateFormat("HH:mm").format(cal.time)
                textView.text = formattedTime
                vreme_start.text = "Početak rada: $formattedTime"
                startFlag = true
                checkFlagsAndUpdateButton()
            }
            TimePickerDialog(
                requireContext(),
                timeSetListener,
                cal.get(Calendar.HOUR_OF_DAY),
                cal.get(Calendar.MINUTE),
                true
            ).show()
        }

        end_time.setOnClickListener {
            val cal = Calendar.getInstance()
            val timeSetListener = TimePickerDialog.OnTimeSetListener { timePicker, hour, minute ->
                cal.set(Calendar.HOUR_OF_DAY, hour)
                cal.set(Calendar.MINUTE, minute)
                val formattedTime = SimpleDateFormat("HH:mm").format(cal.time)
                textView.text = formattedTime
                vreme_end.text = "Završetak rada: $formattedTime" // Ažuriranje prikaza vremena
                endFlag = true
                checkFlagsAndUpdateButton()
            }
            TimePickerDialog(
                requireContext(),
                timeSetListener,
                cal.get(Calendar.HOUR_OF_DAY),
                cal.get(Calendar.MINUTE),
                true
            ).show()
        }

        resetuj_working_time.setOnClickListener {

            vreme_start.text = "Početak rada:"
            vreme_end.text = "Završetak rada:"
            dan.text  = "Radno vreme: "
            startFlag = false
            endFlag = false
            izmeni_working_time.performClick()
            checkFlagsAndUpdateButton()
        }

        izmeni_working_time.setOnClickListener {
            val novoVremeStart = vreme_start.text.toString().removePrefix("Početak rada: ")
            val novoVremeEnd = vreme_end.text.toString().removePrefix("Završetak rada: ")

            if((novoVremeEnd != "") && (novoVremeEnd != ""))
            {
                when(dan_u_nedelji)
                {
                "Ponedeljak"        -> { working_time.monday     = novoVremeStart + "-" + novoVremeEnd; dan.text  = "Radno vreme: " + working_time.monday    }
                "Utorak"            -> { working_time.tuesday    = novoVremeStart + "-" + novoVremeEnd; dan.text  = "Radno vreme: " + working_time.tuesday   }
                "Sreda"             -> { working_time.wednesday  = novoVremeStart + "-" + novoVremeEnd; dan.text  = "Radno vreme: " + working_time.wednesday }
                "Četvrtak"          -> { working_time.thursday   = novoVremeStart + "-" + novoVremeEnd; dan.text  = "Radno vreme: " + working_time.thursday  }
                "Petak"             -> { working_time.friday     = novoVremeStart + "-" + novoVremeEnd; dan.text  = "Radno vreme: " + working_time.friday    }
                "Subota"            -> { working_time.saturday   = novoVremeStart + "-" + novoVremeEnd; dan.text  = "Radno vreme: " + working_time.saturday  }
                "Nedelja"           -> { working_time.sunday     = novoVremeStart + "-" + novoVremeEnd; dan.text  = "Radno vreme: " + working_time.sunday    }
                }
            }

            else
            {
                when(dan_u_nedelji)
                {
                "Ponedeljak"        -> { working_time.monday     = dan.text.toString().removePrefix("Radno vreme: ")}
                "Utorak"            -> { working_time.tuesday    = dan.text.toString().removePrefix("Radno vreme: ") }
                "Sreda"             -> { working_time.wednesday  = dan.text.toString().removePrefix("Radno vreme: ") }
                "Četvrtak"          -> { working_time.thursday   = dan.text.toString().removePrefix("Radno vreme: ") }
                "Petak"             -> { working_time.friday     = dan.text.toString().removePrefix("Radno vreme: ") }
                "Subota"            -> { working_time.saturday   = dan.text.toString().removePrefix("Radno vreme: ") }
                "Nedelja"           -> { working_time.sunday     = dan.text.toString().removePrefix("Radno vreme: ") }
                }
            }

            confirm_new_working_time.setBackgroundResource(R.drawable.full_fill_button_dark_blue)
            confirm_new_working_time.isEnabled = true
            println(working_time)
        }
    }

    private fun checkFlagsAndUpdateButton() {
        if (startFlag && endFlag) {
            izmeni_working_time.setBackgroundResource(R.drawable.full_fill_button)
            izmeni_working_time.isEnabled = true
        } else {
            izmeni_working_time.setBackgroundResource(R.drawable.full_fill_button_disabled)
            izmeni_working_time.isEnabled = false
        }
    }


    private fun findProduct(tekst: String) {
        products_filtered.clear()

        if(show_category_type == 0){
            for (product in products) {
                if (product.productName.contains(tekst, ignoreCase = true)) {
                    products_filtered.add(product)
                }
            }
        }
        else{
            for (product in products) {
                if (product.productName.contains(tekst, ignoreCase = true) && product.category_id == show_category_type) {
                    products_filtered.add(product)
                }
            }
        }

        println("Pronađeni proizvodi: $products_filtered")
        renderMyProducts(products_filtered)
    }

    private fun filterProducts(){
        products_filtered.clear()

        if(show_category_type > 0){
            if(search.text.trim().equals("")){
                for (product in products) {
                    if (product.category_id == show_category_type) {
                        products_filtered.add(product)
                    }
                }
            }
            else{
                for (product in products) {
                    println("uporedjujem" + product.category_id + show_category_type)
                    if (product.productName.contains(search.text, ignoreCase = true) && product.category_id == show_category_type) {
                        products_filtered.add(product)
                    }
                }
            }
        }
        else{
            if(search.text.trim().equals("")){
                for (product in products) {
                    products_filtered.add(product)
                }
            }
            else{
                for (product in products) {
                    if (product.productName.contains(search.text, ignoreCase = true)) {
                        products_filtered.add(product)
                    }
                }
            }
        }
        println("Pronađeni proizvodi: $products_filtered")
        renderMyProducts(products_filtered)
    }

    @SuppressLint("MissingInflatedId")
    private fun showPostAddOverlay() {
        val inflater = layoutInflater
        val post_dialog = inflater.inflate(R.layout.overlay_add_post, null)
        val alertDialogBuilder = AlertDialog.Builder(requireContext())
        alertDialogBuilder.setView(post_dialog)
        val alertDialog: AlertDialog = alertDialogBuilder.create()

        seller_username =       post_dialog.findViewById(R.id.seller_username)
        post_text =             post_dialog.findViewById(R.id.post_text)
        close_edit_post =       post_dialog.findViewById(R.id.close)
        dodaj_post =            post_dialog.findViewById(R.id.dodaj_post)

        dodaj_post.setOnClickListener{
            if(!post_text.text.isEmpty())
            {
                alertDialog.dismiss()
                dodajObjavu(post_text.text.toString())
            }
        }
        close_edit_post.setOnClickListener {
            alertDialog.dismiss()
        }

        alertDialog.show()
    }

    fun dodajObjavu(objava: String) {
        val url: String = Config.ip_address + ":" + Config.port + "/addPost/"
        val send_url = "$url$objava"
        println(url)


        apiClient.sendPostRequestWithToken(JWTService.getToken(),send_url,
            { response ->

                val gson = Gson()
                val typeToken = object : TypeToken<ResponseObjectTemplate<Object>>() {}.type
                var json_response = gson.fromJson<ResponseObjectTemplate<Object>>(response, typeToken)

                when(json_response.message){
                    "Post added successfully."      -> {Toast.makeText(requireContext(),"Uspešno ste dodali objavu", Toast.LENGTH_LONG).show(); showSellersPosts() }
                }
            },
            { error ->
                println(error)
            }
        )
    }
    private var show_category_type = 0
    private fun showMenu() {
        dugme0.setOnClickListener{show_category_type = 0 ; getAllProducts() ; changeColor(dugme0);}
        dugme1.setOnClickListener{show_category_type = 4 ; filterProducts(); changeColor(dugme1) ;}
        dugme2.setOnClickListener{show_category_type = 1 ; filterProducts(); changeColor(dugme2) ;}
        dugme3.setOnClickListener{show_category_type = 3 ; filterProducts(); changeColor(dugme3) ; }
        dugme4.setOnClickListener{show_category_type = 8 ; filterProducts(); changeColor(dugme4) ; }
        dugme5.setOnClickListener{show_category_type = 7 ; filterProducts(); changeColor(dugme5) ; }
        dugme6.setOnClickListener{show_category_type = 5 ; filterProducts(); changeColor(dugme6) ; }
        dugme7.setOnClickListener{show_category_type = 6 ; filterProducts(); changeColor(dugme7) ; }
        dugme8.setOnClickListener{show_category_type = 2 ; filterProducts(); changeColor(dugme8) ; }
    }

    private fun promeniBojuDugmeta(dugme : Button) {
        dugme_objave.setBackgroundResource(R.drawable.empty_button)
        dugme_objave.setTextColor(ContextCompat.getColor(requireContext(), R.color.black))
        dugme_proizvodi.setBackgroundResource(R.drawable.empty_button)
        dugme_proizvodi.setTextColor(ContextCompat.getColor(requireContext(), R.color.black))

        dugme.setBackgroundResource(R.drawable.full_fill_button)
        dugme.setTextColor(ContextCompat.getColor(requireContext(), R.color.white))
    }

    private fun changeColor(dugme: Button) {
        dugme0.setBackgroundResource(R.drawable.empty_button)
        dugme0.setTextColor(ContextCompat.getColor(requireContext(), R.color.black))
        dugme1.setBackgroundResource(R.drawable.empty_button)
        dugme1.setTextColor(ContextCompat.getColor(requireContext(), R.color.black))
        dugme2.setBackgroundResource(R.drawable.empty_button)
        dugme2.setTextColor(ContextCompat.getColor(requireContext(), R.color.black))
        dugme3.setBackgroundResource(R.drawable.empty_button)
        dugme3.setTextColor(ContextCompat.getColor(requireContext(), R.color.black))
        dugme4.setBackgroundResource(R.drawable.empty_button)
        dugme4.setTextColor(ContextCompat.getColor(requireContext(), R.color.black))
        dugme5.setBackgroundResource(R.drawable.empty_button)
        dugme5.setTextColor(ContextCompat.getColor(requireContext(), R.color.black))
        dugme6.setBackgroundResource(R.drawable.empty_button)
        dugme6.setTextColor(ContextCompat.getColor(requireContext(), R.color.black))
        dugme7.setBackgroundResource(R.drawable.empty_button)
        dugme7.setTextColor(ContextCompat.getColor(requireContext(), R.color.black))
        dugme8.setBackgroundResource(R.drawable.empty_button)
        dugme8.setTextColor(ContextCompat.getColor(requireContext(), R.color.black))

        dugme.setBackgroundResource(R.drawable.full_fill_button)
        dugme.setTextColor(ContextCompat.getColor(requireContext(), R.color.white))
    }

    // SVI PROIZVODI
    fun getAllProducts() {
        if (!products.isEmpty()) products.clear()
        if(sellerId >= 0){
            val url: String = Config.ip_address + ":" + Config.port + "/seller/allProducts?id=" + sellerId

            apiClient.sendGetRequestEmpty(url,
                { response ->
                    val gson = Gson()
                    val jsonResponse = JSONObject(response)
                    val jsonArray = jsonResponse.getJSONArray("data")
                    //val products = mutableListOf<ProductAll>()
                    for (i in 0 until jsonArray.length()) {
                        val productObject = jsonArray.getJSONObject(i)
                        val product = gson.fromJson(productObject.toString(), ProductAll::class.java)
                        products.add(product)
                    }

                    renderMyProducts(products.toList())
                },
                { error -> println(error) }
            )
        }
        else{
            val url: String = Config.ip_address + ":" + Config.port + "/seller/allProducts"

            apiClient.sendGetRequestEmpty(
                JWTService.getToken(), url,
                { response ->
                    val gson = Gson()
                    val jsonResponse = JSONObject(response)
                    val jsonArray = jsonResponse.getJSONArray("data")
                    //val products = mutableListOf<ProductAll>()
                    for (i in 0 until jsonArray.length()) {
                        val productObject = jsonArray.getJSONObject(i)
                        val product = gson.fromJson(productObject.toString(), ProductAll::class.java)
                        products.add(product)
                    }

                    renderMyProducts(products.toList())
                },
                { error -> println(error) }
            )
        }
    }


    fun followOrUnfollow(sellerId: Int, follow: TextView){
        val url: String = Config.ip_address+":"+ Config.port+"/follow/"+sellerId
        apiClient.sendPostRequestWithToken(JWTService.getToken(),url,
            {response->
                var gson = Gson()
                val typeToken = object : TypeToken<ResponseObjectTemplate<Object>>() {}.type
                var json_response = gson.fromJson<ResponseObjectTemplate<Object>>(response, typeToken)
                if(json_response.message.equals("Seller unfollowed successfully!")){
                    Toast.makeText(context, "Uspešno ste otpratili korisnika", Toast.LENGTH_LONG).show()

                    follow.setBackgroundResource(R.drawable.full_fill_button)
                    follow.setTextColor(resources.getColor(R.color.white))
                    follow.text = "Zaprati"

                }
                else if (json_response.message.equals("Seller followed successfully!")){
                    Toast.makeText(context, "Uspešno ste zapratili korisnika", Toast.LENGTH_LONG).show()

                    follow.setBackgroundResource(R.drawable.empty_button)
                    follow.setTextColor(resources.getColor(R.color.black))
                    follow.text = "Otprati"
                }
            },
            {error->})
    }

    //OBJAVE
    fun showSellersPosts() {
        if(sellerId >= 0){
            val url: String = Config.ip_address+":"+ Config.port+"/seller/allPosts?id="+sellerId
            apiClient.sendGetRequestEmpty(url,
                {   response->
                    var gson = Gson()
                    val typeToken = object : TypeToken<ResponseListTemplate<NewNews>>() {}.type
                    var postDetails = gson.fromJson<ResponseListTemplate<NewNews>>(response, typeToken)
                    list_of_posts = postDetails.data
                    renderMyPosts(list_of_posts)
                },
                {error -> println(error) }
            )
        }
        else{
            val url: String = Config.ip_address+":"+ Config.port+"/seller/allPosts"
            apiClient.sendGetRequestEmpty(
                JWTService.getToken(), url,
                {   response->
                    var gson = Gson()
                    val typeToken = object : TypeToken<ResponseListTemplate<NewNews>>() {}.type
                    var postDetails = gson.fromJson<ResponseListTemplate<NewNews>>(response, typeToken)
                    list_of_posts = postDetails.data
                    renderMyPosts(list_of_posts)
                },
                {error -> println(error) }
            )
        }


    }

    private suspend fun getPersonalInformation() {
        var url:String
        if(sellerId >= 0)
            url= Config.ip_address+":"+ Config.port + "/seller/personalInformation?id=" + sellerId
        else
            url = Config.ip_address + ":" + Config.port + "/seller/personalInformation"


        if(!JWTService.getToken().equals("null")){
            apiClient.sendGetRequestEmpty(JWTService.getToken(), url,
                {response ->
                    var gson = Gson()
                    val jsonObject = JSONObject(response)
                    val code = jsonObject.getInt("code")
                    val success = jsonObject.getBoolean("success")

                    if (code == 200 && success) {
                        val dataObject = jsonObject.getJSONObject("data")
                        seller_info.apply {
                            seller_id = dataObject.getInt("seller_id")
                            username = dataObject.getString("username")
                            name = dataObject.getString("name")
                            surname = dataObject.getString("surname")
                            picture = dataObject.getString("picture")
                            pib = dataObject.getString("pib")
                            longitude = dataObject.getDouble("longitude")
                            latitude = dataObject.getDouble("latitude")
                            followed = dataObject.getBoolean("followed")

                            apiClient.getAddressFromCoordinates(requireContext(),latitude, longitude,
                                {response-> address = response}, {  })

                            numberOfFollows = dataObject.getInt("numberOfFollowers")
                            numberOfPosts = dataObject.getInt("numberOfPosts")
                            avgGrade = dataObject.getDouble("avgGrade")
                            profileOwner = dataObject.getBoolean("profileOwner")
                            numberOfProducts = dataObject.getInt("numberOfProducts")
                        }
                    }
                    if(!seller_info.profileOwner)
                        setVisibilityPerAuthorization()
                    renderProfileInformation(seller_info, true)
                },
                {error -> println(error)}
            )
        }
        else{
            apiClient.sendGetRequestEmpty(url,
                {response ->
                    var gson = Gson()
                    val jsonObject = JSONObject(response)
                    val code = jsonObject.getInt("code")
                    val success = jsonObject.getBoolean("success")

                    if (code == 200 && success) {
                        val dataObject = jsonObject.getJSONObject("data")
                        seller_info.apply {
                            seller_id = dataObject.getInt("seller_id")
                            username = dataObject.getString("username")
                            name = dataObject.getString("name")
                            surname = dataObject.getString("surname")
                            picture = dataObject.getString("picture")
                            pib = dataObject.getString("pib")
                            longitude = dataObject.getDouble("longitude")
                            latitude = dataObject.getDouble("latitude")

                            apiClient.getAddressFromCoordinates(requireContext(),latitude, longitude,
                                {response-> address = response}, {  })

                            numberOfFollows = dataObject.getInt("numberOfFollowers")
                            numberOfPosts = dataObject.getInt("numberOfPosts")
                            avgGrade = dataObject.getDouble("avgGrade")
                            profileOwner = dataObject.getBoolean("profileOwner")
                            followed = dataObject.getBoolean("followed")
                            numberOfProducts = dataObject.getInt("numberOfProducts")
                        }
                    }
                    if(!seller_info.profileOwner)
                        setVisibilityPerAuthorization()
                    renderProfileInformation(seller_info, false)
                },
                {error -> println(error)}
            )
        }

    }

    private fun setVisibilityPerAuthorization(){
        moj_profil.text = "Profil korisnika: "
        edit_profile.visibility = View.GONE
        privremeni_logout.visibility = View.GONE
        close_fragment.setOnClickListener{findNavController().navigateUp()}
        close_fragment.visibility = View.VISIBLE
        dugme_add.visibility = View.GONE
        text_add.visibility = View.GONE
        naslov_radno.isEnabled = false
        moj_profil.isEnabled = false
    }

    private fun getWorkingTimeInformations() {
        if(sellerId >= 0){
            var url:String = Config.ip_address+":"+ Config.port + "/getWorkingTime?id="+sellerId
            apiClient.sendGetRequestEmpty(url,
                {response ->
                    val jsonObject = JSONObject(response)

                    try{
                        val dataObject = jsonObject.getJSONObject("data")
                        val monday = dataObject.getString("monday")
                        val tuesday = dataObject.getString("tuesday")
                        val wednesday = dataObject.getString("wednesday")
                        val thursday = dataObject.getString("thursday")
                        val friday = dataObject.getString("friday")
                        val saturday = dataObject.getString("saturday")
                        val sunday = dataObject.getString("sunday")

                        val workingTime = WorkingTime(
                            monday, tuesday, wednesday, thursday, friday, saturday, sunday
                        )
                        //working_time = workingTime
                        renderWorkingTime(workingTime)
                    }
                    catch (eception:Exception){
                        val workingTime = WorkingTime(
                            "", "", "", "", "", "", ""
                        )
                        renderWorkingTime(workingTime)
                    }
                },
                {error -> println(error)}
            )
        }
        else{
            var url:String = Config.ip_address+":"+ Config.port + "/getWorkingTime"
            apiClient.sendGetRequestEmpty(JWTService.getToken(), url,
                {response ->
                    val jsonObject = JSONObject(response)
                    try{
                        val dataObject = jsonObject.getJSONObject("data")
                        val monday = dataObject.getString("monday")
                        val tuesday = dataObject.getString("tuesday")
                        val wednesday = dataObject.getString("wednesday")
                        val thursday = dataObject.getString("thursday")
                        val friday = dataObject.getString("friday")
                        val saturday = dataObject.getString("saturday")
                        val sunday = dataObject.getString("sunday")

                        val workingTime = WorkingTime(
                            monday, tuesday, wednesday, thursday, friday, saturday, sunday
                        )
                        renderWorkingTime(workingTime)
                    }
                    catch (eception:Exception){
                        val workingTime = WorkingTime(
                            "", "", "", "", "", "", ""
                        )
                        renderWorkingTime(workingTime)
                    }
                },
                {error -> println(error)}
            )
        }
    }

    //MAPA
    private fun openMapsOverlay(sellerInfo: Prodavac) {
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
        val person_image                        = dialog.findViewById<ImageView>(R.id.person_image)

        Image.setImageResource(person_image, sellerInfo.picture, -1)

        overlay_closeButton.setOnClickListener{
            dialog.dismiss()
        }
        if(sellerInfo.profileOwner == true){
            edit_map.visibility = View.VISIBLE
            edit_map.setOnClickListener{
                showMapEdiOverlay()
                dialog.dismiss()
            }
        }
        overlay_person_household.text = "Domaćinstvo " + sellerInfo.surname
        overlay_person_username.text  = "@" + sellerInfo.username
        overlay_person_location.text  = sellerInfo.address
        Map.setMap(overlay_map, sellerInfo.latitude, sellerInfo.longitude,requireActivity(), requireContext())
        dialog.show()
    }

    @SuppressLint("MissingInflatedId")
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

        person_household.text = "Domaćinstvo " + seller_info.surname
        person_username.text = "@" + seller_info.username
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
            sendNewCordinates(seller_info.latitude,seller_info.longitude)
            alertDialog.dismiss()
            openMapsOverlay(seller_info)
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
                        {response-> seller_info.address = response}, {  })
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
        seller_info.longitude = longitude
        seller_info.latitude = latitude
        apiClient.getAddressFromCoordinates(requireContext(),seller_info.latitude, seller_info.longitude,
            {response-> seller_info.address = response}, {  })
        // Dodavanje pina na tacnu lokaciju
        val items = ArrayList<OverlayItem>()
        val overlayItem = OverlayItem("Lokacija", "Lokacija domacinstva", newPoint)
        overlayItem.setMarker(ContextCompat.getDrawable(requireContext(), R.drawable.location_pin))
        items.add(overlayItem)

        val overlay = ItemizedIconOverlay<OverlayItem>(items, null, requireContext())
        mapView.overlays.clear()
        mapView.overlays.add(overlay)
    }

    private fun showDialogForPost(id: Int) {
        val dialog = Dialog(requireContext())
        getPostDetails(id,dialog)
        dialog.requestWindowFeature(Window.FEATURE_NO_TITLE)
        dialog.setCancelable(true)
        dialog.setContentView(R.layout.overlay_new_news)
        val close = dialog.findViewById<ImageView>(R.id.overlay_household_location_close)
        close.setOnClickListener{dialog.dismiss()}

        val width = (resources.displayMetrics.widthPixels * 1.0).toInt()
        dialog.window?.setLayout(width, ViewGroup.LayoutParams.WRAP_CONTENT)
        dialog.show()
    }

    //OBJAVE
    fun getPostDetails(id: Int, dialog: Dialog){
        var url:String = Config.ip_address+":"+ Config.port + "/postDetails"+"/"+id
        apiClient.sendGetRequestEmpty(JWTService.getToken(),url,
            { response ->
                var gson = Gson()
                val typeToken = object : TypeToken<ResponseObjectTemplate<PostDetails>>() {}.type
                var postDetails = gson.fromJson<ResponseObjectTemplate<PostDetails>>(response, typeToken)
                renderDialogForPost(postDetails.data,dialog, id)
            },
            { error ->
                println(error)
            }
        )
    }

    fun commentPost(id: Int, text: String, dialog: Dialog){
        var jsonObject: JSONObject = JSONObject()

        jsonObject.put("postId", id)
        jsonObject.put("text", text)

        var url: String = Config.ip_address+":"+ Config.port + "/postComment"
        apiClient.sendPostRequestWithJSONObjectWithStringResponse(url, JWTService.getToken(), jsonObject,
            {response->
                getPostDetails(id, dialog)
            },
            {error->
            }
        )
    }

    override fun onCreateView(
        inflater: LayoutInflater, container: ViewGroup?,
        savedInstanceState: Bundle?
    ): View? {
        view = inflater.inflate(R.layout.fragment_seller_profile, container, false)
        getAllStuff()
        setAllEventListenet()

        try{
            val args: SellerProfileFragmentArgs = SellerProfileFragmentArgs.fromBundle(requireArguments())
            sellerId = args.sellerId
            println("seller: " + sellerId)
        }
        catch (exception:Exception){}


        val scope = CoroutineScope(Dispatchers.Default)
        scope.launch {
            async { getPersonalInformation() }
            async { getWorkingTimeInformations() }
//            async { getAllProducts() }
//            async { getLastNews() }
        }

        dugme_proizvodi.performClick()
        dugme0.performClick()
        return view
    }

    fun setVisibilityPerRole(){
        if(JWTService.getTokenIfExist(requireContext()) is Role){
            isLogged = true
            edit_info.visibility = View.GONE
        }
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

    private lateinit var image_place: ImageView
    @SuppressLint("MissingInflatedId")
    private fun renderProfileInformation(sellerInfo: Prodavac, checkFollow:Boolean)
    {
        var row : LinearLayout
        row = LinearLayout(requireContext())
        row.orientation = LinearLayout.HORIZONTAL
        val layoutParams = LinearLayout.LayoutParams(
            LinearLayout.LayoutParams.MATCH_PARENT,
            LinearLayout.LayoutParams.WRAP_CONTENT
        )
        row.layoutParams = layoutParams
        profile_information.addView(row)

        println(sellerInfo.followed)

        val itemView = layoutInflater.inflate(R.layout.component_profile_info,null)
        var profileImage = itemView.findViewById<CircleImageView>(R.id.profile_pic)
        var name_surname = itemView.findViewById<TextView>(R.id.name)
        var username = itemView.findViewById<TextView>(R.id.username)
        var broj_pracenja = itemView.findViewById<TextView>(R.id.broj_porudzbina)
        var text_pracenja = itemView.findViewById<TextView>(R.id.orders_text)
        var broj_objava = itemView.findViewById<TextView>(R.id.broj_pracenja)
        var text_objava = itemView.findViewById<TextView>(R.id.follow_text)
        var srednja_ocena = itemView.findViewById<TextView>(R.id.srednja_ocena)
        var ocena_text = itemView.findViewById<TextView>(R.id.ocena_text)
        var ocena = itemView.findViewById<ConstraintLayout>(R.id.ocena)
        var follow = itemView.findViewById<Button>(R.id.follow)
        var podaci_kupca = itemView.findViewById<ConstraintLayout>(R.id.podaci_kupca)

        var constraintLayout122 = itemView.findViewById<ConstraintLayout>(R.id.constraintLayout122)
        var broj                = itemView.findViewById<TextView>(R.id.broj)
        var follow_textt        = itemView.findViewById<TextView>(R.id.follow_textt)

        podaci_kupca.visibility = View.VISIBLE
        ocena.visibility = View.VISIBLE
        constraintLayout122.visibility= View.VISIBLE

        //var lokacija = itemView.findViewById<ImageView>(R.id.broj_lajkova)

        //follow.setOnClickListener{}

        text_pracenja.text  = "Objava"
        text_objava.text    = "Proizvodi"
        ocena_text.text     = "Praćenja"
        follow_textt.text   = "Ocena"

        if(checkFollow && sellerInfo.profileOwner==false){
            if(sellerInfo.followed){
                follow.setBackgroundResource(R.drawable.empty_button)
                follow.setTextColor(resources.getColor(R.color.black))
                follow.text = "Otprati"
            }
            else{
                follow.setBackgroundResource(R.drawable.full_fill_button)
                follow.setTextColor(resources.getColor(R.color.white))
                follow.text = "Zaprati"
            }
            follow.setOnClickListener {
                followOrUnfollow(seller_info.seller_id, follow)
            }
        }
        else{
            follow.visibility = View.GONE
        }

        name_surname.text = sellerInfo.name + " " + sellerInfo.surname
        username.text = "@" + sellerInfo.username
        broj_pracenja.text = sellerInfo.numberOfPosts.toString() //sellerInfo.numberOfFollows.toString()
        broj_objava.text = sellerInfo.numberOfProducts.toString()
        srednja_ocena.text = sellerInfo.numberOfFollows.toString()
        if(sellerInfo.avgGrade < 0)
        {
            broj.text = "-"
        }
        else broj.setText("%.2f".format(sellerInfo.avgGrade))

//        lokacija.setImageResource(R.drawable.maps)
//        lokacija.setOnClickListener{
//            openMapsOverlay(sellerInfo)
//        }
//        profileImage.layoutParams.width = 150
//        profileImage.layoutParams.height = 150
        if(sellerInfo.picture.equals("null"))
            Image.setImageResource(profileImage, null, -1)
        else
            Image.setImageResource(profileImage, sellerInfo.picture, -1)

        image_place = profileImage
        if(sellerInfo.profileOwner)
            profileImage.setOnClickListener{eventForChangeProfileImage()}

        edit_profile.setOnClickListener { showEditProfileOverlay(sellerInfo) }

        moj_profil.setOnClickListener { showEditProfileOverlay(sellerInfo) }

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

    private fun renderMyPosts(list: List<NewNews>) {
        val list_of_posts = list.reversed()
        val row = LinearLayout(requireContext())
        row.orientation = LinearLayout.VERTICAL
        val layoutParams = LinearLayout.LayoutParams(
            LinearLayout.LayoutParams.MATCH_PARENT,
            LinearLayout.LayoutParams.MATCH_PARENT
        )
        row.layoutParams = layoutParams
        list_of_objects.addView(row)

        var marginInDp = 4
        var itemLayoutParamsForVertical = LinearLayout.LayoutParams(
            LinearLayout.LayoutParams.MATCH_PARENT,
            LinearLayout.LayoutParams.MATCH_PARENT
        )
        val marginInPx = (Comments.marginInDp * resources.displayMetrics.density).toInt()
        val itemLayoutParams : LinearLayout.LayoutParams;
        itemLayoutParams = Comments.itemLayoutParamsForVertical
        itemLayoutParams.setMargins(0, (Comments.marginInDp +14), 0, 0)

        val widthInDp = 15
        val heightInDp = 15

        val density = resources.displayMetrics.density

        val widthInPx = (widthInDp * density).toInt()
        val heightInPx = (heightInDp * density).toInt()

        var postParams = LinearLayout.LayoutParams(widthInPx, heightInPx)
        postParams.rightMargin = marginInPx

        for (post in list_of_posts) {
            val itemView = layoutInflater.inflate(R.layout.component_new_news, null) as ConstraintLayout

            val seller_username = itemView.findViewById<TextView>(R.id.seller_username)
            val time = itemView.findViewById<TextView>(R.id.time)
            val new_news_text = itemView.findViewById<TextView>(R.id.new_news_text)
            val num_of_likes = itemView.findViewById<TextView>(R.id.num_of_likes)
            val num_of_comments = itemView.findViewById<TextView>(R.id.num_of_comments)
            val like = itemView.findViewById<ConstraintLayout>(R.id.like)
            val like_img = itemView.findViewById<ImageView>(R.id.imageView7)
            val comment_img = itemView.findViewById<ImageView>(R.id.imageView8)

            seller_username.text = "@" + seller_info.username
            time.text = post.dateTime.split("T")[0]
            new_news_text.text = post.text
            num_of_likes.text = post.likesNumber.toString()
            num_of_comments.text = post.commentsNumber.toString()
            if(post.likedPost){
                like_img.setImageResource(R.drawable.liked)
            }

            like.visibility = View.GONE
            like_img.visibility = View.GONE
            num_of_comments.visibility = View.GONE
             comment_img.visibility= View.GONE

            like.setOnClickListener { likePost(post.id, like_img, num_of_likes, false ) }

            itemView.layoutParams = itemLayoutParams
            itemView.setOnClickListener{showDialogForPost(post.id)}
            row.addView(itemView)
        }
    }

    fun likePost(id: Int, image: ImageView, num_of_likes: TextView, render: Boolean){
        var url: String = Config.ip_address+":"+ Config.port + "/like"+"/"+id
        apiClient.sendPostRequestWithToken(JWTService.getToken(),url,
            { response ->

                val gson = Gson()
                val typeToken = object : TypeToken<ResponseObjectTemplate<Object>>() {}.type
                var json_response = gson.fromJson<ResponseObjectTemplate<Object>>(response, typeToken)

                when(json_response.message){
                    "Post liked successfully!"      -> {image.setImageResource(R.drawable.liked);  num_of_likes.text = (Integer.parseInt(num_of_likes.text.toString()) + 1).toString() }
                    "Post unliked successfully!"    -> {image.setImageResource(R.drawable.like); num_of_likes.text = (Integer.parseInt(num_of_likes.text.toString()) - 1).toString()}
                }
//                if(render){
//                    val scope = CoroutineScope(Dispatchers.Default)
//                    scope.launch {
//                        async { getLastNews() } .await()
//                    }
//                }
            },
            { error ->
                println(error)
            }
        )
    }

//    suspend fun getLastNews(){
//        var url:String = Config.ip_address+":"+ Config.port + "/lastNews"
//        apiClient.sendGetRequestEmpty(JWTService.getToken(),url,
//            { response ->
//                var gson = Gson()
//                val typeToken = object : TypeToken<ResponseListTemplate<NewNews>>() {}.type
//                var posts = gson.fromJson<ResponseListTemplate<NewNews>>(response, typeToken)
//                renderMyPosts(posts.data)
//            },
//            { error ->
//                println(error)
//            }
//        )
//    }

    private fun renderDialogForPost(postDetails: PostDetails?, dialog: Dialog, id: Int){
        if(postDetails!=null){
            val person_image        = dialog.findViewById<ImageView>(R.id.person_image)
            val person_household    = dialog.findViewById<TextView>(R.id.person_household)
            val person_username     = dialog.findViewById<TextView>(R.id.person_username)
            val person_location     = dialog.findViewById<TextView>(R.id.person_location)
            val post_text           = dialog.findViewById<TextView>(R.id.post_text)
            val num_of_likes        = dialog.findViewById<TextView>(R.id.num_of_likes)
            val num_of_comments     = dialog.findViewById<TextView>(R.id.num_of_comments)
            val like                = dialog.findViewById<ConstraintLayout>(R.id.constraintLayout8)
            val like_img            = dialog.findViewById<ImageView>(R.id.imageView7)
            var comment_on_post     = dialog.findViewById<EditText>(R.id.comment_on_post)
            var post_comment        = dialog.findViewById<Button>(R.id.post_comment)

            Image.setImageResource(person_image, postDetails.picture, -1)

            person_household.text   = "Domaćinstvo "+postDetails.surname
            person_username.text    = "@"+postDetails.username
            apiClient.getAddressFromCoordinates(requireContext(),postDetails.latitude, postDetails.longitude,
                {response->  person_location.text = response }, {  })
            post_text.text          = postDetails.text
            num_of_likes.text       = postDetails.likesNumber.toString()
            num_of_comments.text    = postDetails.commentsNumber.toString()

            if(postDetails.likedPost){
                like_img.setImageResource(R.drawable.liked)
            }

            like.setOnClickListener { likePost(id, like_img, num_of_likes, true); }

            comment_on_post.addTextChangedListener(object: TextWatcher {
                override fun beforeTextChanged(s: CharSequence?, start: Int, count: Int, after: Int) {}

                override fun onTextChanged(s: CharSequence?, start: Int, before: Int, count: Int) {}

                override fun afterTextChanged(s: Editable?) {
                    if (s != null) {
                        if(s.length > 0){
                            post_comment.isEnabled = true
                            post_comment.setBackgroundResource(R.color.orange)
                        } else{
                            post_comment.isEnabled = false
                            post_comment.setBackgroundResource(R.color.grey)
                        }
                    }
                }
            })

            post_comment.setOnClickListener{
                commentPost(id,comment_on_post.text.toString(), dialog)
                comment_on_post.setText("")
            }

            if(postDetails.postCommentDTOList.size > 0){
                val scroll = dialog.findViewById<ScrollView>(R.id.scrollView3)
                scroll.removeAllViews()

                val row = LinearLayout(requireContext())
                row.orientation = LinearLayout.VERTICAL
                val layoutParams = LinearLayout.LayoutParams(
                    LinearLayout.LayoutParams.MATCH_PARENT,
                    LinearLayout.LayoutParams.MATCH_PARENT
                )
                row.layoutParams = layoutParams

                val marginInPx = (Comments.marginInDp * resources.displayMetrics.density).toInt()
                val itemLayoutParams : LinearLayout.LayoutParams;
                itemLayoutParams = Comments.itemLayoutParamsForVertical
                itemLayoutParams.setMargins(0, (Comments.marginInDp +14), 0, 0)

                scroll.addView(row)

                for(comment: PostCommentsDTO in postDetails.postCommentDTOList){
                    val itemView = layoutInflater.inflate(R.layout.component_comment, null)
                    val stars           = itemView.findViewById<LinearLayout>(R.id.horizontal_layout_star)
                    val comment_comment = itemView.findViewById<TextView>(R.id.comment_comment)
                    val image           = itemView.findViewById<ImageView>(R.id.comment_image)
                    val comment_username = itemView.findViewById<TextView>(R.id.comment_username)

                    stars.visibility = View.GONE
                    comment_comment.ellipsize = null
                    comment_comment.maxLines = Int.MAX_VALUE
                    comment_comment.setText(comment.text)
                    image.layoutParams.width = 100
                    image.layoutParams.height = 100
                    Image.setImageResource(image, comment.picture, -1)
                    comment_username.text = comment.username

                    itemView.layoutParams = itemLayoutParams
                    row.addView(itemView)
                }
            }

        }
    }

    //PROIZVODI
    fun renderMyProducts(products: List<ProductAll>) {
        grid_of_products.removeAllViews()

        val rowsCount = (products.size + 1) / 2
        grid_of_products.rowCount = rowsCount
        val width = grid_of_products.width
        val columnCount = 2 // Broj kolona
        val columnWidth = width / columnCount - 20

        for (i in 0 until rowsCount) {
            for (j in 0 until 2) {
                val index = i * 2 + j
                if (index < products.size) {
                    val product = products[index]
                    val gridItem = layoutInflater.inflate(R.layout.component_product, null)

                    val productName = gridItem.findViewById<TextView>(R.id.product_name)
                    val productImage = gridItem.findViewById<ImageView>(R.id.product_image)
                    val productPrice = gridItem.findViewById<TextView>(R.id.cena)
                    val measurment = gridItem.findViewById<TextView>(R.id.kolicina)
                    productName.text = product.productName
                    productPrice.text = product.price.toString()
                    measurment.text = product.measurement

                    Image.setImageResource(productImage, product.picture, product.category_id)

                    gridItem.setOnClickListener{
                        val action = ProductViewFragmentDirections.actionProductViewFragment(product.id.toInt())
                        findNavController().navigate(action)
                    }
                    val layoutParams = GridLayout.LayoutParams()
                    layoutParams.rowSpec = GridLayout.spec(i, GridLayout.START, 1f)
                    layoutParams.columnSpec = GridLayout.spec(j, GridLayout.START, 1f)
                    layoutParams.width = columnWidth
                    gridItem.layoutParams = layoutParams

                    grid_of_products.addView(gridItem)
                }
            }
        }
    }

    @SuppressLint("MissingInflatedId")
    private fun renderWorkingTime(data: WorkingTime) {
        if(data.monday.equals("") && data.thursday.equals("") && data.wednesday.equals("")&&
            data.thursday.equals("") && data.friday.equals("") && data.saturday.equals("") &&
            data.sunday.equals(""))
        {
            error_radno.visibility = View.VISIBLE
        }
        else{
            radno_vreme.removeAllViews()
            error_radno.visibility = View.GONE
            val itemView = layoutInflater.inflate(R.layout.component_radno_vreme, null)

            val pon_time = itemView.findViewById<TextView>(R.id.pon_time)
            val uto_time = itemView.findViewById<TextView>(R.id.uto_time)
            val sre_time = itemView.findViewById<TextView>(R.id.sre_time)
            val cet_time = itemView.findViewById<TextView>(R.id.cet_time)
            val pet_time = itemView.findViewById<TextView>(R.id.pet_time)
            val sub_time = itemView.findViewById<TextView>(R.id.sub_time)
            val ned_time = itemView.findViewById<TextView>(R.id.ned_time)

            val pon = itemView.findViewById<TextView>(R.id.pon)
            val uto = itemView.findViewById<TextView>(R.id.uto)
            val sre = itemView.findViewById<TextView>(R.id.sre)
            val cet = itemView.findViewById<TextView>(R.id.cet)
            val pet = itemView.findViewById<TextView>(R.id.pet)
            val sub = itemView.findViewById<TextView>(R.id.sub)
            val ned = itemView.findViewById<TextView>(R.id.ned)

            val pon_ned = itemView.findViewById<TextView>(R.id.pon_ned)
            val pon_ned_time = itemView.findViewById<TextView>(R.id.pon_ned_time)
            pon_ned.visibility = View.GONE
            pon_ned_time.visibility = View.GONE

            pon_time.text = data.monday
            uto_time.text = data.tuesday
            sre_time.text = data.wednesday
            cet_time.text = data.thursday
            pet_time.text = data.friday
            sub_time.text = data.saturday
            ned_time.text = data.sunday

            if(data.monday.equals("")) {       pon.visibility = View.GONE
                pon_time.visibility = View.GONE }

            if(data.tuesday.equals("")) {      uto.visibility = View.GONE
                uto_time.visibility = View.GONE }

            if(data.wednesday.equals("")) {    sre.visibility = View.GONE
                sre_time.visibility = View.GONE }

            if(data.thursday.equals("")) {     cet.visibility = View.GONE
                cet_time.visibility = View.GONE }

            if(data.friday.equals("")) {       pet.visibility = View.GONE
                pet_time.visibility = View.GONE }

            if(data.saturday.equals("")) {     sub.visibility = View.GONE
                sub_time.visibility = View.GONE }

            if(data.sunday.equals("")) {       ned.visibility = View.GONE
                ned_time.visibility = View.GONE }


            if(areAllFieldsEqual(data))
            {
                pon.visibility = View.GONE
                pon_time.visibility = View.GONE
                uto.visibility = View.GONE
                uto_time.visibility = View.GONE
                sre.visibility = View.GONE
                sre_time.visibility = View.GONE
                cet.visibility = View.GONE
                cet_time.visibility = View.GONE
                pet.visibility = View.GONE
                pet_time.visibility = View.GONE
                sub.visibility = View.GONE
                sub_time.visibility = View.GONE
                ned.visibility = View.GONE
                ned_time.visibility = View.GONE

                pon_ned_time.text = data.monday + "H"
                pon_ned.visibility = View.VISIBLE
                pon_ned_time.visibility = View.VISIBLE
            }

            radno_vreme.addView(itemView)
        }
        naslov_radno.setOnClickListener{
            println(data)
            openEditWorkingTimeOverlay(data)

        }
    }


    fun areAllFieldsEqual(workingTime: WorkingTime): Boolean {
        return workingTime.monday == workingTime.tuesday &&
                workingTime.tuesday == workingTime.wednesday &&
                workingTime.wednesday == workingTime.thursday &&
                workingTime.thursday == workingTime.friday &&
                workingTime.friday == workingTime.saturday &&
                workingTime.saturday == workingTime.sunday
    }

}

