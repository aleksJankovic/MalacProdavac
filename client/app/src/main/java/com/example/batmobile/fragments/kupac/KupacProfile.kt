package com.example.batmobile.fragments.kupac

import android.annotation.SuppressLint
import android.app.Activity
import android.app.AlertDialog
import android.content.Intent
import android.graphics.Color
import android.net.Uri
import android.os.Bundle
import android.provider.MediaStore
import android.text.Editable
import android.text.TextWatcher
import android.util.Log
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import android.view.inputmethod.EditorInfo
import android.widget.Button
import android.widget.EditText
import android.widget.ImageView
import android.widget.LinearLayout
import android.widget.ScrollView
import android.widget.TextView
import android.widget.Toast
import androidx.constraintlayout.widget.ConstraintLayout
import androidx.fragment.app.Fragment
import androidx.fragment.app.activityViewModels
import com.example.batmobile.DTOFromServer.Korisnik
import com.example.batmobile.DTOFromServer.PersonalInformation
import com.example.batmobile.DTOFromServer.PieChartProduct
import com.example.batmobile.DTOFromServer.ResponseObjectTemplate
import com.example.batmobile.R
import com.example.batmobile.enums.Role
import com.example.batmobile.network.ApiClient
import com.example.batmobile.network.Config
import com.example.batmobile.services.Image
import com.example.batmobile.services.JWTService
import com.example.batmobile.services.Validator
import com.example.batmobile.viewModels.UserViewModel
import com.google.gson.Gson
import com.google.gson.reflect.TypeToken
import de.hdodenhof.circleimageview.CircleImageView
import org.eazegraph.lib.charts.PieChart
import org.eazegraph.lib.models.PieModel
import org.json.JSONObject


class KupacProfile : Fragment() {


    private var originalProfileContent                          : View? = null
    lateinit var edit_info                                      : ScrollView

    private lateinit var view                                   : View
    private lateinit var apiClient                              : ApiClient

    private lateinit var profile_information                    : ConstraintLayout
    private lateinit var privremeni_logout                      : Button
    private lateinit var edit_profile                           : Button
    private lateinit var moj_profil                             : TextView

    lateinit var name                                           : EditText
    lateinit var surname                                       : EditText
    lateinit var username                                       : EditText
    lateinit var email                                          : EditText
    lateinit var old_password                                   : EditText
    lateinit var password                                       : EditText
    lateinit var password_confirm                               : EditText

    lateinit var error_name                                     : TextView
    lateinit var error_surname                                 : TextView
    lateinit var error_username                                 : TextView
    lateinit var error_email                                    : TextView
    lateinit var error_password_old                             : TextView
    lateinit var error_password                                 : TextView
    lateinit var error_password_confirm                         : TextView

    lateinit var btn_nastavi                                    : Button
    lateinit var close                                          : ImageView

    var flagName                                                : Boolean = true
    var flagsurname                                            : Boolean = true
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

    var personal_information_object                             : PersonalInformation? = null
    lateinit var kupac_info                                     : Korisnik
    lateinit var kupac_nove_info                                : Korisnik

    //PIECHART
    lateinit var analiza_text                                   : TextView
    lateinit var analiza_podaci                                 : ConstraintLayout
    lateinit var product_list                                   : MutableList<PieChartProduct>

    fun getAllStuff(){
        kupac_info = Korisnik("","","","","","",0,0)
        kupac_nove_info = Korisnik("","","","","","",0,0)
        apiClient = ApiClient(requireContext())
        profile_information =           view.findViewById(R.id.profile_info)
        privremeni_logout =             view.findViewById(R.id.privremeni_logout)
        edit_profile =                  view.findViewById(R.id.edit_profile)
        analiza_podaci =                view.findViewById(R.id.piechart_podaci)
        analiza_text =                  view.findViewById(R.id.analiza)
        moj_profil =                    view.findViewById(R.id.textView5)
        product_list = mutableListOf()
    }

    private fun getPieChartStuff() {
        if(!product_list.isEmpty()) product_list.clear()
        val url: String = Config.ip_address + ":" + Config.port + "/user/graphic/data"
        apiClient.sendGetRequestEmpty(JWTService.getToken(), url,
            { response ->
                val gson = Gson()
                val jsonResponse = JSONObject(response)
                val jsonArray = jsonResponse.getJSONArray("data")

                //val products = mutableListOf<ProductAll>()
                for (i in 0 until jsonArray.length()) {
                    val productObject = jsonArray.getJSONObject(i)
                    val product = gson.fromJson(productObject.toString(), PieChartProduct::class.java)
                    product_list.add(product)
                }
                println(product_list)
                renderPieChart(product_list.toList())
            },
            { error ->
                println(error)
            }
        )
    }


    private fun showEditProfileOverlay(kupac : Korisnik) {
        val inflater = layoutInflater
        val dialogView = inflater.inflate(R.layout.overlay_change_profile_info, null)
        val alertDialogBuilder = AlertDialog.Builder(requireContext())
        alertDialogBuilder.setView(dialogView)
        val alertDialog: AlertDialog = alertDialogBuilder.create()
        originalProfileContent = profile_information.getChildAt(0)

        //podaci
        kupac_nove_info = Korisnik("","","","","","",0,0)
        edit_info =                 dialogView.findViewById<ScrollView>(R.id.edit_info)


        name =                      dialogView.findViewById<EditText>(R.id.name)
        surname =                  dialogView.findViewById<EditText>(R.id.lastname)
        username =                  dialogView.findViewById<EditText>(R.id.username)
        email =                     dialogView.findViewById<EditText>(R.id.email)
        old_password =              dialogView.findViewById<EditText>(R.id.password_old)
        password =                  dialogView.findViewById<EditText>(R.id.password)
        password_confirm =          dialogView.findViewById<EditText>(R.id.password_confirm)

        error_name =                dialogView.findViewById<TextView>(R.id.error_name)
        error_surname =            dialogView.findViewById<TextView>(R.id.error_lastname)
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


        println(kupac)
        name.hint = kupac.name
        name.setText("")
        surname.hint = kupac.surname
        surname.setText("")
        username.hint = kupac.username
        username.setText("")
        email.hint = kupac.email
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
            checkNewInformations(kupac_nove_info)
            //slanje nove lozinke na server na proveru
            sendPasswordDataToServer(kupac_nove_info.password)

            if(!kupac_nove_info.username.equals(""))
            {
                Toast.makeText(context, "Morate se ponovo ulogovati", Toast.LENGTH_LONG).show()
                JWTService.logOut(requireActivity())
                kupac_nove_info = Korisnik("","","","","","",0,0)
            }
            //zatvaranje dijaloga nakon uspešnog slanja podataka
            alertDialog.dismiss()
        }

        //ukoliko se ne unese nista pamtimo prazan string
        //ukoliko imamo input onda se vrsi provera
        name.setOnFocusChangeListener { _, hasFocus ->
            if (!hasFocus) {
                if (!name.text.toString().equals("")) {
                    kupac_nove_info.name = name.text.toString()
                    validatorUnosa(kupac_nove_info.name, "name", error_name)
                }
                else flagName = true
            }
        }
        name.setOnEditorActionListener { _, actionId, _ ->
            if (actionId == EditorInfo.IME_ACTION_DONE) {
                if (!name.text.toString().equals("")) {
                    kupac_nove_info.name = name.text.toString()
                    validatorUnosa(kupac_nove_info.name, "name", error_name)
                }
                else flagName = true
            }
            false
        }

        surname.setOnFocusChangeListener { _, hasFocus ->
            if (!hasFocus) {
                if (!surname.text.toString().equals("")) {
                    kupac_nove_info.surname = surname.text.toString()
                    validatorUnosa(kupac_nove_info.surname, "surname", error_surname)
                }
                else flagsurname = true
            }
        }
        surname.setOnEditorActionListener { _, actionId, _ ->
            if (actionId == EditorInfo.IME_ACTION_DONE) {
                if (!surname.text.toString().equals("")) {
                    kupac_nove_info.surname = surname.text.toString()
                    validatorUnosa(kupac_nove_info.surname, "surname", error_surname)
                }
                else flagsurname = true
            }
            false
        }

        username.setOnFocusChangeListener { _, hasFocus ->
            if (!hasFocus) {
                if (!username.text.equals("")) {
                    kupac_nove_info.username = username.text.toString()
                    validatorUnosa(kupac_nove_info.username, "username", error_username)
                    checkExisting("username", kupac_nove_info.username)
                }
                else flagUsername = true
            }
        }
        username.setOnEditorActionListener { _, actionId, _ ->
            if (actionId == EditorInfo.IME_ACTION_DONE) {
                if (!username.text.toString().equals("")) {
                    kupac_nove_info.username = username.text.toString()
                    validatorUnosa(kupac_nove_info.username, "username", error_username)
                    checkExisting("username", kupac_nove_info.username)
                }
                else flagUsername = true
            }
            false
        }

        email.setOnFocusChangeListener { _, hasFocus ->
            if (!hasFocus) {
                if (!email.text.toString().equals("")) {
                    kupac_nove_info.email = email.text.toString()
                    validateEmail(kupac_nove_info.email, error_email)
                    checkExisting("email", kupac_nove_info.email)
                }
                else flagEmail = true
            }
        }
        email.setOnEditorActionListener { _, actionId, _ ->
            if (actionId == EditorInfo.IME_ACTION_DONE) {
                if (!email.text.toString().equals("")) {
                    kupac_nove_info.email = email.text.toString()
                    validateEmail(kupac_nove_info.email, error_email)
                    checkExisting("email", kupac_nove_info.email)
                }
                else flagEmail = true
            }
            false
        }

        //stara sifra je obavezno polje
        old_password.setOnFocusChangeListener { _, hasFocus ->
            if (!hasFocus) {
                validateOldPassword(old_password.text.toString(), error_password_old)
                validateAllInputs(kupac_nove_info)

            }
            else
            {
                error_password_old.visibility = View.INVISIBLE
            }
        }
        old_password.setOnEditorActionListener { _, actionId, _ ->
            if (actionId == EditorInfo.IME_ACTION_DONE) {
                validateOldPassword(old_password.text.toString(), error_password_old)
                validateAllInputs(kupac_nove_info)
            }
            false
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
                validateAllInputs(kupac_nove_info)
            }
        })


        password.setOnFocusChangeListener { _, hasFocus ->
            if (!hasFocus) {
                if (!password.text.toString().equals("")) {
                    kupac_nove_info.password = password.text.toString()
                    validatePassword(password, error_password)
                }
                else flagPassword = true
            }
        }
        password.setOnEditorActionListener { _, actionId, _ ->
            if (actionId == EditorInfo.IME_ACTION_DONE) {
                if (!password.text.toString().equals("")) {
                    kupac_nove_info.password = password.text.toString()
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

    fun validateEmail(email: String, error_text: TextView){
        if(!Validator.regexEmailValidationPattern(email))
        {
            error_text.setText("* Uneta email adresa nije ispravna")
            error_text.visibility = View.VISIBLE
        }
        else {
            error_text.visibility = View.INVISIBLE
        }
        validateAllInputs(kupac_nove_info)
    }

    private fun validatorUnosa(input: String, tip: String, error_text: TextView?)
    {
        if(input.length >= 2) {
            when(tip)
            {
                "name" ->       { flagName      = true ;            error_text!!.visibility = View.INVISIBLE }
                "surname" ->   { flagsurname  = true ;            error_text!!.visibility = View.INVISIBLE }
                "username" ->   { flagUsername  = true ;            error_text!!.visibility = View.INVISIBLE }
            }
        }
        else
        {
            when(tip){

                "name" ->       { flagName      = false ;           error_text!!.visibility = View.VISIBLE  }
                "surname" ->   { flagsurname  = false ;           error_text!!.visibility = View.VISIBLE  }
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
        validateAllInputs(kupac_nove_info)
    }

    fun sendPasswordDataToServer(password: String)
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

    private fun validateOldPassword(oldPassword: String, error_text: TextView) {
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
        validateAllInputs(kupac_nove_info)
    }

    fun validateConfirmPassword(password: EditText, password_configm: EditText,error_text: TextView){
        if(password.text.toString().length > 0){
            if(!password_configm.text.toString().equals(password.text.toString())){ error_text.visibility = View.VISIBLE ; flagPassword_confirm = false }
            else{ error_text.visibility = View.INVISIBLE ; flagPassword_confirm = true }
        }
        else{ error_text.visibility = View.INVISIBLE ; flagPassword_confirm = false }
        validateAllInputs(kupac_nove_info)
    }

    fun validatePassword(text: EditText, error_text: TextView){
        val response = Validator.validatePassword(text.text.toString())
        when(response){
            "* Morate uneti neku šifru"->                                        { flagPassword = false; error_text.setText(response); error_text.visibility = View.VISIBLE}
            "* Šifra mora sadržati 8+ znakova, velika i mala slova, brojeve"->   { flagPassword = false; error_text.setText(response); error_text.visibility = View.VISIBLE}
            "true"->                                                            { flagPassword = true; error_text.setText(response); error_text.visibility = View.INVISIBLE; kupac_nove_info.password = text.text.toString()}
        }
        validateAllInputs(kupac_nove_info)
    }

    fun showPasswordOg(view: View){
        if(view.id.equals(R.id.eye_password))
            Validator.showPassword(view, password)
        else if (view.id.equals(R.id.eye_password_old))
            Validator.showPassword(view, old_password)
        else
            Validator.showPassword(view, password_confirm)
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
                validateAllInputs(kupac_nove_info)
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
                    validateAllInputs(kupac_nove_info)
                }
            }
        )
    }

    private fun validateAllInputs(kupac : Korisnik) {
        print("name " + flagName + ", surname " + flagsurname + ", username " + flagUsername + ", old pass " + flagPasswordOld + ", new pass " + flagPassword + ", confrim " + flagPassword_confirm)
        if(!flagName || !flagsurname || !flagUsername || !flagEmail || !flagPasswordOld || !flagPassword || !flagPassword_confirm)
        {
            btn_nastavi.isEnabled = false
            btn_nastavi.setBackgroundResource(R.drawable.full_fill_button_disabled)
        }
        else
        {
            //if(!kupac_nove_info.name.equals("") || !kupac_nove_info.surname.equals("") || !kupac_nove_info.username.equals("") || !kupac_nove_info.email.equals("") || !kupac_nove_info.password.equals(""))
            //{
                btn_nastavi.isEnabled = true
                btn_nastavi.setBackgroundResource(R.drawable.full_fill_button)
            //}
        }
    }

    //provera da li je sve ok kad posaljem podatke serveru
    fun checkNewInformations(kupac : Korisnik)
    {
        val jsonObject: JSONObject = JSONObject()
        var ime = kupac.name
        var prezime = kupac.surname
        var username = kupac.username
        var email = kupac.email

        val url: String = "${Config.ip_address}:${Config.port}/updatePersonalInformation"
        val userProfile = JSONObject().apply{
            put("name", ime)
            put("surname", prezime)
            put("username", username)
            put("email", email)
            //put("picture", "" )
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

    fun setVisibilityPerRole(){
        if(JWTService.getTokenIfExist(requireContext()) is Role){
            privremeni_logout.visibility    = View.VISIBLE
            privremeni_logout.setOnClickListener{JWTService.logOut(requireActivity())}
        }
    }

    fun getPersonalInformation()
    {
        var url:String = Config.ip_address+":"+ Config.port + "/personalInformation"
        apiClient.sendGetRequestEmpty(JWTService.getToken(), url,
            {response ->
                var gson = Gson()
                var kupac_info = gson.fromJson(response, Korisnik::class.java)
                println(kupac_info)
                renderProfileInformation(kupac_info)

            },
            {error -> println(error)}
        )
    }

    override fun onCreateView(
        inflater: LayoutInflater, container: ViewGroup?,
        savedInstanceState: Bundle?
    ): View? {
        view = inflater.inflate(R.layout.fragment_kupac_profile, container, false)
        getAllStuff()
        getPersonalInformation()
        setVisibilityPerRole()
        getPieChartStuff()

        return view
    }
    private lateinit var image_place: ImageView
    fun renderProfileInformation(personalInformation: Korisnik)
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

        val itemView = layoutInflater.inflate(R.layout.component_profile_info,null)
        var profileImage = itemView.findViewById<CircleImageView>(R.id.profile_pic)
        var name_surname = itemView.findViewById<TextView>(R.id.name)
        var username = itemView.findViewById<TextView>(R.id.username)
        var broj_pracenja = itemView.findViewById<TextView>(R.id.broj_pracenja)
        var broj_porudzbina = itemView.findViewById<TextView>(R.id.broj_porudzbina)
        var text_pracenja = itemView.findViewById<TextView>(R.id.follow_text)
        var text_porudzbina = itemView.findViewById<TextView>(R.id.orders_text)

//        profileImage.layoutParams.width = 150
//        profileImage.layoutParams.height = 150

        Image.setImageResource(profileImage, personalInformation.picture, -1)

        image_place = profileImage
        profileImage.setOnClickListener{eventForChangeProfileImage()}

        text_pracenja.text = "Praćenja"
        text_porudzbina.text = "Porudžbina"
        name_surname.text = personalInformation.name + " " + personalInformation.surname
        username.text = "@" + personalInformation.username
        broj_pracenja.text = personalInformation.numberOfFollows.toString()
        broj_porudzbina.text = personalInformation.numberOfOrders.toString()

        edit_profile.setOnClickListener {
            showEditProfileOverlay(personalInformation)
        }

        moj_profil.setOnClickListener { showEditProfileOverlay(personalInformation) }

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

    //PIECHART
    @SuppressLint("MissingInflatedId")
    private fun renderPieChart(products_piechart: List<PieChartProduct>) {
        analiza_podaci.removeAllViews()

        val itemView = layoutInflater.inflate(R.layout.component_piechart, null)
        val pie_chart = itemView.findViewById<PieChart>(R.id.piechart)

        val mlecni_proizvodi_boja           = itemView.findViewById<View>(R.id.mlecni_proizvodi_boja)
        val voce_povrce_boja                = itemView.findViewById<View>(R.id.voce_povrce_boja)
        val mesne_preradjevine_boja         = itemView.findViewById<View>(R.id.mesne_preradjevine_boja)
        val sveze_meso_boja                 = itemView.findViewById<View>(R.id.sveze_meso_boja)
        val zitarice_boja                   = itemView.findViewById<View>(R.id.zitarice_boja)
        val napici_boja                     = itemView.findViewById<View>(R.id.napici_boja)
        val biljna_ulja_boja                = itemView.findViewById<View>(R.id.biljna_ulja_boja)
        val namazi_boja                     = itemView.findViewById<View>(R.id.namazi_boja)

        val categoryMap = mutableMapOf<Int, TextView>().apply{
            put(1, itemView.findViewById(R.id.mlecni_proizvodi))
            put(2, itemView.findViewById(R.id.voce_povrce))
            put(3, itemView.findViewById(R.id.mesne_preradjevine))
            put(4, itemView.findViewById(R.id.sveze_meso))
            put(5, itemView.findViewById(R.id.zitarice))
            put(6, itemView.findViewById(R.id.napici))
            put(7, itemView.findViewById(R.id.biljna_ulja))
            put(8, itemView.findViewById(R.id.namazi))
        }

        mlecni_proizvodi_boja.setBackgroundColor(Color.parseColor(getCategoryColor(1)))
        voce_povrce_boja.setBackgroundColor(Color.parseColor(getCategoryColor(2)))
        mesne_preradjevine_boja.setBackgroundColor(Color.parseColor(getCategoryColor(3)))
        sveze_meso_boja.setBackgroundColor(Color.parseColor(getCategoryColor(4)))
        zitarice_boja.setBackgroundColor(Color.parseColor(getCategoryColor(5)))
        napici_boja.setBackgroundColor(Color.parseColor(getCategoryColor(6)))
        biljna_ulja_boja.setBackgroundColor(Color.parseColor(getCategoryColor(7)))
        namazi_boja.setBackgroundColor(Color.parseColor(getCategoryColor(8)))

        for ((index, product) in products_piechart.withIndex()) {
            categoryMap[product.categoryId]?.text = product.numberOfOrders.toString()
            pie_chart.addPieSlice(
                PieModel(
                    getCategoryName(product.categoryId),
                    product.numberOfOrders.toFloat(),
                    Color.parseColor(getCategoryColor(product.categoryId))
                )
            )
        }

        pie_chart.startAnimation()
        analiza_podaci.addView(itemView)
    }

    private fun getCategoryColor(categoryId: Int): String {
        return when (categoryId) {
            1 -> "#FFA726"
            2 -> "#66BB6A"
            3 -> "#EF5350"
            4 -> "#29B6F6"
            5 -> "#AB47BC"
            6 -> "#FF7043"
            7 -> "#5C6BC0"
            8 -> "#9CCC65"
            else -> ""
        }
    }

    private fun getCategoryName(categoryId: Int): String {
        return when (categoryId) {
            1 -> "Mlečni proizvodi"
            2 -> "Voće i povrće"
            3 -> "Mesne prerađevine"
            4 -> "Sveže meso"
            5 -> "Žitarice"
            6 -> "Napici"
            7 -> "Biljna ulja"
            8 -> "Namazi"
            else -> ""
        }
    }

}