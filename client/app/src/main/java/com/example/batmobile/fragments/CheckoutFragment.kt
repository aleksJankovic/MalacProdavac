package com.example.batmobile.fragments

import android.content.pm.PackageManager
import android.os.Bundle
import android.provider.MediaStore.Audio.Radio
import android.text.Editable
import android.text.TextWatcher
import androidx.fragment.app.Fragment
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import android.view.inputmethod.EditorInfo
import android.widget.Button
import android.widget.EditText
import android.widget.ImageView
import android.widget.RadioButton
import android.widget.RadioGroup
import android.widget.Toast
import androidx.core.app.ActivityCompat
import androidx.core.content.ContextCompat
import androidx.navigation.fragment.findNavController
import com.example.batmobile.R
import com.example.batmobile.enums.Role
import com.example.batmobile.models.Order
import com.example.batmobile.network.ApiClient
import com.example.batmobile.network.Config
import com.example.batmobile.services.Cart
import com.example.batmobile.services.JWTService
import com.example.batmobile.services.Validator
import com.google.android.gms.location.FusedLocationProviderClient
import com.google.android.gms.location.LocationServices
import com.google.gson.Gson
import org.json.JSONObject
import org.osmdroid.config.Configuration
import org.osmdroid.tileprovider.tilesource.TileSourceFactory
import org.osmdroid.util.GeoPoint
import org.osmdroid.views.MapView
import org.osmdroid.views.overlay.ItemizedIconOverlay
import org.osmdroid.views.overlay.OverlayItem

class CheckoutFragment : Fragment() {

    private lateinit var view: View

    private lateinit var close: ImageView
    private lateinit var phone_num: EditText
    private lateinit var buy_button: Button

//    ================
    private lateinit var location_input: EditText

    private var live_latitude:Double = -100.0
    private var live_longitude:Double = 200.0

    lateinit var mapView: MapView
    private lateinit var fusedLocationClient: FusedLocationProviderClient
    private val REQUEST_CODE = 123

    lateinit var apiCall: ApiClient

//    ================

    private lateinit var nacin_dostave: RadioGroup
    private          var seted_nacin_dostave = -1
    private lateinit var nacin_placanja: RadioGroup
    private          var seted_nacin_placanja = -1
    private lateinit var licno:          RadioButton

    private fun getAllStuff(){
        close                           = view.findViewById<ImageView>(R.id.close)
        apiCall                         = ApiClient(requireActivity())
        location_input                  = view.findViewById<EditText>(R.id.lokacija)
        mapView                         = view.findViewById<MapView>(R.id.mapView)
        phone_num                       = view.findViewById<EditText>(R.id.phone_num)
        buy_button                      = view.findViewById<Button>(R.id.buy_button)

        nacin_dostave                   = view.findViewById<RadioGroup>(R.id.nacin_dostave)
        nacin_placanja                  = view.findViewById<RadioGroup>(R.id.nacin_placanja)
        licno                           = view.findViewById<RadioButton>(R.id.licno)
    }

    private fun setAllActionListener(){
        close.setOnClickListener{
            findNavController().navigateUp()
        }

        buy_button.setOnClickListener{
            sendOrder()
        }

        location_input.setOnEditorActionListener{ _, actionId, _ ->
            validateForBuy()
            if (actionId == EditorInfo.IME_ACTION_DONE) {
                sendRequestForCoordinates()
            }
            false
        }

        location_input.setOnFocusChangeListener { v, hasFocus -> validateForBuy() }

        phone_num.addTextChangedListener(object : TextWatcher {
            override fun beforeTextChanged(s: CharSequence?, start: Int, count: Int, after: Int) {
                // Implementacija pre promene teksta
            }

            override fun onTextChanged(s: CharSequence?, start: Int, before: Int, count: Int) {
                // Implementacija tokom promene teksta
            }

            override fun afterTextChanged(s: Editable?) {
                validateForBuy() }
        })

        nacin_dostave.setOnCheckedChangeListener{grouup, checkedId ->
            run {
                when (checkedId) {
                    R.id.organizovani_transport -> { seted_nacin_dostave = 1; licno.visibility = View.GONE}
                    R.id.kurirska_sluzba        -> { seted_nacin_dostave = 2; licno.visibility = View.GONE}
                    R.id.licno_preuzimanje      -> { seted_nacin_dostave = 3; licno.visibility = View.VISIBLE}
                }
                validateForBuy()
            }
        }
        nacin_placanja.setOnCheckedChangeListener{grouup, checkedId ->
            run {
                when (checkedId) {
                    R.id.uplata_na_racun        -> { seted_nacin_placanja = 1}
                    R.id.placanje_pouzecem      -> { seted_nacin_placanja = 2}
                    R.id.licno                  -> { seted_nacin_placanja = 3}
                }
                validateForBuy()
            }
        }

    }

    private fun sendOrder(){
        var url:String = Config.ip_address+":"+ Config.port + "/order"
        var order: Order = Order(live_latitude, live_longitude, location_input.text.toString(), phone_num.text.toString(), Cart.getCart(), seted_nacin_placanja, seted_nacin_dostave)

        var gson = Gson()
        var gson_obj = gson.toJson(order)
        var json_obj: JSONObject = JSONObject(gson_obj)

        println(json_obj)

        apiCall.sendPostRequestWithJSONObjectWithStringResponse(url, JWTService.getToken(),json_obj ,
            {response->
                val toast = Toast.makeText(requireContext(), "Uspesno obavljena kupovina", Toast.LENGTH_LONG)
                toast.show()
                Cart.dropCart(requireActivity())
                if(JWTService.getTokenIfExist(requireContext()) == Role.User)
                    findNavController().navigate(R.id.action_kupacHomeOld)
                else if(JWTService.getTokenIfExist(requireContext()) == Role.Seller)
                    findNavController().navigate(R.id.action_kupacHome)
                else if (JWTService.getTokenIfExist(requireContext()) == Role.Deliverer)
                    findNavController().navigate(R.id.action_HomeDelivererFragment)
            },
            {error->
                println(error)
            }
        )
    }

    override fun onCreateView(
        inflater: LayoutInflater, container: ViewGroup?,
        savedInstanceState: Bundle?
    ): View? {
        view =  inflater.inflate(R.layout.fragment_checkout, container, false)

        getAllStuff()

        setAllActionListener()

        setAll()

        return view
    }

    fun validateForBuy(){
        if(location_input.text.toString() != "" &&
            ((live_latitude >= -90.0 && live_latitude <= 90.0) || (live_longitude >= -180.0 && live_longitude <= 180.0))
            && Validator.validatePhoneNum(phone_num.text.toString())
            && seted_nacin_dostave > 0 && seted_nacin_placanja > 0 && !(seted_nacin_dostave != 3 && seted_nacin_placanja == 3)){
                buy_button.isEnabled = true;
                buy_button.setBackgroundResource(R.drawable.full_fill_button);
        }
        else{
            buy_button.isEnabled = false;
            buy_button.setBackgroundResource(R.drawable.full_fill_button_disabled)
        }
    }

//    ==============================

    fun setAll(){
        fusedLocationClient = LocationServices.getFusedLocationProviderClient(requireActivity())
        if (ContextCompat.checkSelfPermission(requireActivity(), android.Manifest.permission.ACCESS_FINE_LOCATION) == PackageManager.PERMISSION_GRANTED) {
            // Imate dozvolu za pristup lokaciji, možete zatražiti lokaciju
            getLocation()
        } else {
            // Ako nemate dozvolu, zatražite je od korisnika
            ActivityCompat.requestPermissions(requireActivity(), arrayOf(android.Manifest.permission.ACCESS_FINE_LOCATION), REQUEST_CODE)
        }
        if ((live_latitude >= -90.0 && live_latitude <= 90.0) || (live_longitude >= -180.0 && live_longitude <= 180.0)){ setMap(live_latitude, live_longitude)}
        else setInitMap()
        validateForBuy()
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

    fun setMap(latitude:Double, longitude:Double){
        val newPoint = GeoPoint(latitude, longitude)
        live_latitude = latitude
        live_longitude = longitude
        validateForBuy()
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
    fun sendRequestForCoordinates(){
        println("usao")
        val address = location_input.text.toString()
        apiCall.getCoordinatesForAddress(address,
            { latitude, longitude ->
                requireActivity().runOnUiThread{
                    // Dobijene su koordinate (latitude i longitude)
                    setMap(latitude,longitude)
                    println("Latitude: $latitude, Longitude: $longitude")
                }
            },
            {
                // Greška prilikom dobijanja koordinata
                println("Greška prilikom dobijanja koordinata.")
            })
    }

    fun sendRequestForStringLocation(latitude: Double, longitude: Double){
        apiCall.getAddressFromCoordinates(requireContext(), latitude, longitude,
            { fullAddress ->
                requireActivity().runOnUiThread{
                    // Dobijena je adresa u obliku "Grad, Ulica"
                    location_input.setText(fullAddress)
                    println("Adresa: $fullAddress")
                }
            },
            {
                // Greška prilikom dobijanja adrese
                println("Greška prilikom dobijanja adrese.")
            })
    }

}