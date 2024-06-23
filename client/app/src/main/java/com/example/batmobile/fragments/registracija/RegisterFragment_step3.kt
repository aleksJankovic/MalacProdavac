package com.example.batmobile.fragments.registracija

import android.content.Intent
import android.net.Uri
import android.os.Bundle
import androidx.fragment.app.Fragment
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import android.widget.Button
import android.widget.FrameLayout
import android.widget.ImageView
import android.widget.LinearLayout
import android.widget.TextView
import android.widget.Toast
import androidx.core.content.ContextCompat
import androidx.fragment.app.activityViewModels
import androidx.navigation.Navigation
import androidx.navigation.fragment.findNavController
import com.example.batmobile.R
import com.example.batmobile.activities.LoginActivity
import com.example.batmobile.viewModels.UserViewModel
import com.example.batmobile.models.User
import com.example.batmobile.network.ApiClient
import com.example.batmobile.network.Config
import com.example.batmobile.services.Image
import de.hdodenhof.circleimageview.CircleImageView
import org.json.JSONArray
import org.json.JSONObject
import org.osmdroid.tileprovider.util.StreamUtils
import org.osmdroid.util.GeoPoint
import org.osmdroid.views.MapView
import org.osmdroid.views.overlay.ItemizedIconOverlay
import org.osmdroid.views.overlay.OverlayItem

class RegisterFragment_step3 : Fragment() {

    private lateinit var back: ImageView

    private lateinit var selectedOption:String

    private lateinit var image: CircleImageView
    private lateinit var name: TextView
    private lateinit var lastname: TextView
    private lateinit var username: TextView
    private lateinit var email: TextView
    private lateinit var password: TextView

    private lateinit var eye: ImageView

    private lateinit var vehicle_auto: FrameLayout
    private lateinit var vehicle_motocikl: FrameLayout
    private lateinit var vehicle_kombi: FrameLayout
    private lateinit var vehicle_kamion: FrameLayout

    private lateinit var mapView: MapView
    private lateinit var pib_prodavac: TextView

    private lateinit var btn_registruj_se: Button

    private lateinit var user: User

    private lateinit var apiCall: ApiClient

    override fun onCreateView(
        inflater: LayoutInflater, container: ViewGroup?,
        savedInstanceState: Bundle?
    ): View? {
        // Inflate the layout for this fragment

        val view =  inflater.inflate(R.layout.fragment_register_step3, container, false)

        val userViewModel: UserViewModel by activityViewModels()

        println(userViewModel.user)
        println(userViewModel.selectedOption)

        user = userViewModel.user!!
        selectedOption = userViewModel.selectedOption!!

        apiCall = ApiClient(requireContext())

        back = view.findViewById<ImageView>(R.id.back)
        back.setOnClickListener{
            Navigation.findNavController(view).navigate(R.id.navigate_step3ToStep2)
        }

        when (selectedOption) {
            "Kupac" -> {
                // Prikazi layout za kupca
                val kupacLayout = view.findViewById<LinearLayout>(R.id.kupacLayout)
                kupacLayout.visibility = View.VISIBLE
                image =     view.findViewById<CircleImageView>(R.id.imageViewKupac)
                name =      view.findViewById<TextView>(R.id.user_nameKupac)
                lastname =  view.findViewById<TextView>(R.id.user_lastnameKupac)
                username =  view.findViewById<TextView>(R.id.user_usernameKupac)
                email =     view.findViewById<TextView>(R.id.user_emailKupac)
                password =  view.findViewById<TextView>(R.id.user_passwordKupac)
                btn_registruj_se = view.findViewById<Button>(R.id.registruj_seKupac)
                eye = view.findViewById<ImageView>(R.id.password_eye)
                eye.setOnClickListener{setPassword()}
                setPreview()
            }
            "Dostavljač" -> {
                // Prikazi layout za dostavljača
                val dostavljacLayout = view.findViewById<LinearLayout>(R.id.dostavljacLayout)
                dostavljacLayout.visibility = View.VISIBLE
                image =     view.findViewById<CircleImageView>(R.id.imageViewDostavljac)
                name =      view.findViewById<TextView>(R.id.user_nameDostavljac)
                lastname =  view.findViewById<TextView>(R.id.user_lastnameDostavljac)
                username =  view.findViewById<TextView>(R.id.user_usernameDostavljac)
                email =     view.findViewById<TextView>(R.id.user_emailDostavljac)
                password =  view.findViewById<TextView>(R.id.user_passwordDostavljac)
                vehicle_auto = view.findViewById<FrameLayout>(R.id.auto)
                vehicle_motocikl = view.findViewById<FrameLayout>(R.id.motocikl)
                vehicle_kombi = view.findViewById<FrameLayout>(R.id.kombi)
                vehicle_kamion = view.findViewById<FrameLayout>(R.id.kamion)
                btn_registruj_se = view.findViewById<Button>(R.id.registruj_seDostavljac)
                eye = view.findViewById<ImageView>(R.id.password_eye2)
                eye.setOnClickListener{setPassword()}
                setPreview()
                setVehicle()
            }
            "Mali prodavac" -> {
                // Prikazi layout za malog prodavca
                val prodavacLayout = view.findViewById<LinearLayout>(R.id.prodavacLayout)
                prodavacLayout.visibility = View.VISIBLE
                image =     view.findViewById<CircleImageView>(R.id.imageViewProdavac)
                name =      view.findViewById<TextView>(R.id.user_nameProdavac)
                lastname =  view.findViewById<TextView>(R.id.user_lastnameProdavac)
                username =  view.findViewById<TextView>(R.id.user_usernameProdavac)
                email =     view.findViewById<TextView>(R.id.user_emailProdavac)
                password =  view.findViewById<TextView>(R.id.user_passwordProdavac)
                mapView =   view.findViewById<MapView>(R.id.mapView)
                btn_registruj_se = view.findViewById<Button>(R.id.registruj_seProdavac)
                pib_prodavac = view.findViewById<TextView>(R.id.user_pibProdavac)
                pib_prodavac.setText(userViewModel.user!!.pib)
                eye = view.findViewById<ImageView>(R.id.password_eye3)
                eye.setOnClickListener{setPassword()}
                setPreview()
                setMap(user.latitude as Double, user.longitude as Double)
            }
        }

        if(image!=null && user.profileImageUri!=null){
            image.setImageURI(user.profileImageUri)
        }

        if(btn_registruj_se!=null){
            btn_registruj_se.setOnClickListener{
                sendRequestForRegistration(btn_registruj_se)
            }
        }

        return view
    }
    var passIsShow: Boolean = false
    fun setPassword(){
        when(passIsShow){
            true -> {
                passIsShow = false
                password.setText(user.password)
                eye.setImageResource(R.drawable.icons8_visible_96___)
            }
            false -> {
                passIsShow = true
                password.setText(changePasswordView(user.password))
                eye.setImageResource(R.drawable.icons8_not_visible_96___)
            }
        }
    }
    fun changePasswordView(text: String):String{
        return "*".repeat(text.length)
    }

    fun setPreview(){
        name.text = user.name
        lastname.text = user.lastname
        username.text = user.username
        email.text = user.email
        password.text = changePasswordView(user.password)
    }

    fun setVehicle(){
        val onlyVehicle:MutableMap<String, Boolean> = user.vehicle
        println()
        println(onlyVehicle)
        if(onlyVehicle["auto"] == true)
            vehicle_auto.visibility = View.VISIBLE
        if(onlyVehicle["motocikl"] == true)
            vehicle_motocikl.visibility = View.VISIBLE
        if(onlyVehicle["kombi"] == true)
            vehicle_kombi.visibility = View.VISIBLE
        if(onlyVehicle["kamion"] == true)
            vehicle_kamion.visibility = View.VISIBLE
    }

    fun setMap(latitude:Double, longitude:Double){
        val newPoint = GeoPoint(latitude, longitude)
        mapView.controller.setCenter(newPoint)
        mapView.controller.setZoom(13.0)
        // Dodavanje pina na tacnu lokaciju
        val items = ArrayList<OverlayItem>()
        val overlayItem = OverlayItem("Lokacija", "Lokacija domacinstva", newPoint)
        overlayItem.setMarker(ContextCompat.getDrawable(requireContext(), R.drawable.location_pin))
        items.add(overlayItem)

        val overlay = ItemizedIconOverlay<OverlayItem>(items, null, requireContext())
        mapView.overlays.clear()
        mapView.overlays.add(overlay)
    }

    fun getUserVehicle(): JSONArray {
        val licenceCategories = JSONArray()
        if(user.vehicle["auto"]==true)
            licenceCategories.put(1)
        if(user.vehicle["motocikl"]==true)
            licenceCategories.put(2)
        if(user.vehicle["kombi"]==true)
            licenceCategories.put(3)
        if(user.vehicle["kamion"]==true)
            licenceCategories.put(4)
        return licenceCategories
    }

    fun sendRequestForRegistration(view: View){
        val jsonObject: JSONObject = JSONObject()
        jsonObject.put("name",user.name)
        jsonObject.put("surname",user.lastname)
        jsonObject.put("username",user.username)
        jsonObject.put("password",user.password)
        jsonObject.put("email",user.email)
        if(user.profileImageUri!=null)
        {
            val img_in_base_x64_format = Image.uriToBase64(requireContext(), user.profileImageUri as Uri)
            jsonObject.put("picture", img_in_base_x64_format)
        }


        when(selectedOption){
            "Kupac" -> {
                jsonObject.put("role","User")
            }
            "Dostavljač" -> {
                jsonObject.put("role","Deliverer")
                jsonObject.put("licenceCategories", getUserVehicle())
            }
            "Mali prodavac" -> {
                jsonObject.put("role","Seller")
                jsonObject.put("accountNumber", user.accountNumber)
                jsonObject.put("pib", user.pib)
                jsonObject.put("longitude", user.longitude)
                jsonObject.put("latitude", user.latitude)
            }
        }

        println(jsonObject)
        var url:String = Config.ip_address+":"+ Config.port + "/registration"
        println(url)
        apiCall.sendPostRequestWithJSONObjectWithStringResponse(
            url,
            jsonObject,
            { response -> println(response)
                val intent: Intent = Intent(requireActivity(), LoginActivity::class.java)
                startActivity(intent)
                requireActivity().finish()
                Toast.makeText(requireContext(), "Usepšno registrovan korisnik", Toast.LENGTH_LONG).show()
            },
            { error ->

                val response = error.networkResponse
                val jsonError = String(response.data)
                val responseObject:JSONObject = JSONObject(jsonError)
                println(responseObject)
                if(responseObject["code"] == 409){
                    val data = responseObject["data"] as JSONObject
                    if(responseObject["message"].equals("PIB already in use"))
                        Toast.makeText(requireContext(), "Domaćinstvo sa ovim PIB-om je registrovano", Toast.LENGTH_LONG).show()
                    else if(data["usernameTaken"]==true){
                        Toast.makeText(requireContext(), "Korisničko ime je zauzeto", Toast.LENGTH_LONG).show()
                    }
                    else if(data["emailTaken"]==true){
                        Toast.makeText(requireContext(), "Email je zauzet", Toast.LENGTH_LONG).show()
                    }
                }
                else{
                    Toast.makeText(requireContext(), "korisnik je vec registrovan", Toast.LENGTH_LONG).show()
                    error.printStackTrace()
                    println(error.message)
                }
            }
        )
    }

}