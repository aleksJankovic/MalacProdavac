package com.example.batmobile.fragments.registracija

import android.app.Activity
import android.content.Intent
import android.content.pm.PackageManager
import android.net.Uri
import android.os.Bundle
import android.provider.MediaStore
import android.text.Editable
import android.text.TextWatcher
import androidx.fragment.app.Fragment
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import android.view.inputmethod.EditorInfo
import android.widget.Button
import android.widget.EditText
import android.widget.FrameLayout
import android.widget.ImageView
import android.widget.LinearLayout
import android.widget.RadioButton
import android.widget.RadioGroup
import android.widget.ScrollView
import android.widget.TextView
import android.widget.Toast
import androidx.core.app.ActivityCompat
import androidx.core.content.ContextCompat
import androidx.fragment.app.activityViewModels
import androidx.navigation.Navigation
import com.example.batmobile.R
import com.example.batmobile.viewModels.UserViewModel
import com.example.batmobile.models.User
import com.example.batmobile.network.ApiClient
import com.google.android.gms.location.FusedLocationProviderClient
import com.google.android.gms.location.LocationServices
import de.hdodenhof.circleimageview.CircleImageView
import org.osmdroid.config.Configuration
import org.osmdroid.tileprovider.tilesource.TileSourceFactory
import org.osmdroid.util.GeoPoint
import org.osmdroid.views.MapView
import org.osmdroid.views.overlay.ItemizedIconOverlay
import org.osmdroid.views.overlay.OverlayItem


class RegisterFragment_step2 : Fragment() {

    private lateinit var imageView5: ImageView
    private lateinit var scrollView5: ScrollView

    private lateinit var back: ImageView

    private lateinit var constraintLayout: LinearLayout

    private lateinit var kupacRadioButton: RadioButton
    private lateinit var prodavacRadioButton: RadioButton
    private lateinit var dostavljacRadioButton: RadioButton

    private lateinit var kupacOptionsLayout: LinearLayout
    private lateinit var prodavacOptionsLayout: LinearLayout
    private lateinit var dostavljacOptionsLayout: LinearLayout

    private lateinit var kupacUserName: TextView
    private lateinit var kupacEmail: TextView
    private lateinit var dostavljacUserName: TextView
    private lateinit var dostavljacEmail: TextView
    private lateinit var prodavacUserName: TextView
    private lateinit var prodavacEmail: TextView


    private lateinit var user: User

    private lateinit var automobil: FrameLayout
    private lateinit var motocikl: FrameLayout
    private lateinit var kombi: FrameLayout
    private lateinit var kamion: FrameLayout


    private lateinit var prodavac_pib: EditText
    private lateinit var bankovni_racun: EditText
    private lateinit var prodavac_lokacija: EditText
    private lateinit var nastaviButton: Button
    private lateinit var nastaviButtonProdavac:Button

    //    Validne vrednosti latitude:[-90, 90] &  longitude:[-180, 180]
    private var live_latitude:Double = -100.0
    private var live_longitude:Double = 200.0

    lateinit var mapView: MapView
    private lateinit var fusedLocationClient: FusedLocationProviderClient
    private val REQUEST_CODE = 123

    lateinit var apiCall: ApiClient

    private lateinit var vehicle: MutableMap<String, Boolean>

    private lateinit var view: View

    private lateinit var import_image_kupac: CircleImageView
    private lateinit var import_image_dostavljac: CircleImageView
    private lateinit var import_image_prodavac: CircleImageView

    override fun onCreateView(
        inflater: LayoutInflater, container: ViewGroup?,
        savedInstanceState: Bundle?
    ): View? {
        // Inflate the layout for this fragment
        view =  inflater.inflate(R.layout.fragment_register_step2, container, false)

        apiCall = ApiClient(requireActivity())

        val userViewModel: UserViewModel by activityViewModels()

        mapView = view.findViewById(R.id.mapView)
        if ((live_latitude >= -90.0 && live_latitude <= 90.0) || (live_longitude >= -180.0 && live_longitude <= 180.0)){ setMap(live_latitude, live_longitude)}
        else setInitMap()

        user = userViewModel.user!!
        println(user)
        vehicle = user.vehicle

        imageView5 = view.findViewById<ImageView>(R.id.imageView5)
        scrollView5 = view.findViewById<ScrollView>(R.id.scrollView5)

        back =                      view.findViewById<ImageView>(R.id.back)
        back.setOnClickListener{ Navigation.findNavController(view).navigate(R.id.navigate_step2ToStep1) }

        constraintLayout = view.findViewById<LinearLayout>(R.id.linearLayout2)
        kupacRadioButton = constraintLayout.findViewById<RadioButton>(R.id.Kupac)
        prodavacRadioButton = constraintLayout.findViewById<RadioButton>(R.id.Prodavac)
        dostavljacRadioButton = constraintLayout.findViewById<RadioButton>(R.id.Dostavljac)

        kupacOptionsLayout = view.findViewById<LinearLayout>(R.id.kupacOptionsLayout)
        prodavacOptionsLayout = view.findViewById<LinearLayout>(R.id.prodavacOptionsLayout)
        dostavljacOptionsLayout = view.findViewById<LinearLayout>(R.id.dostavljacOptionsLayout)

        kupacUserName = view.findViewById<TextView>(R.id.user_usernameKupac)
        kupacEmail = view.findViewById<TextView>(R.id.user_emailKupac)
        dostavljacUserName = view.findViewById<TextView>(R.id.user_usernameDostavljac)
        dostavljacEmail = view.findViewById<TextView>(R.id.user_emailDostavljac)
        prodavacUserName = view.findViewById<TextView>(R.id.user_usernameProdavac)
        prodavacEmail = view.findViewById<TextView>(R.id.user_emailProdavac)

        import_image_kupac = view.findViewById<CircleImageView>(R.id.imageView3)
        import_image_dostavljac = view.findViewById<CircleImageView>(R.id.imageView4)
        import_image_prodavac = view.findViewById<CircleImageView>(R.id.imageViewProdavac)
        addActionForImage()

        if(import_image_kupac!=null && user.profileImageUri!=null){
            import_image_kupac.setImageURI(user.profileImageUri)
        }
        if(import_image_dostavljac!=null && user.profileImageUri!=null){
            import_image_dostavljac.setImageURI(user.profileImageUri)
        }
        if(import_image_prodavac!=null && user.profileImageUri!=null){
            import_image_prodavac.setImageURI(user.profileImageUri)
        }

        if (user != null) {
            kupacUserName.setText(user.username)
            kupacEmail.setText(user.email)
            dostavljacUserName.setText(user.username)
            dostavljacEmail.setText(user.email)
            prodavacUserName.setText(user.username)
            prodavacEmail.setText(user.email)
        }

        automobil = view.findViewById<FrameLayout>(R.id.auto)
        motocikl = view.findViewById<FrameLayout>(R.id.motocikl)
        kombi = view.findViewById<FrameLayout>(R.id.kombi)
        kamion = view.findViewById<FrameLayout>(R.id.kamion)

        automobil.setOnClickListener{setVehicle(automobil)}
        motocikl.setOnClickListener{setVehicle(motocikl)}
        kombi.setOnClickListener{setVehicle(kombi)}
        kamion.setOnClickListener{setVehicle(kamion)}

        prodavac_pib = view.findViewById<EditText>(R.id.pib)
        bankovni_racun = view.findViewById<EditText>(R.id.bankovni_racun)
        prodavac_lokacija = view.findViewById<EditText>(R.id.lokacija)
        nastaviButton = view.findViewById<Button>(R.id.Nastavi)
        nastaviButtonProdavac = view.findViewById<Button>(R.id.NastaviProdavac)

        if(userViewModel.selectedOption!=null){
            setFieldOnStep2(userViewModel, view)
        }

        nastaviButton.setOnClickListener{
            user.pib = prodavac_pib.text.toString()
            user.latitude = live_latitude
            user.longitude = live_longitude
            val radioGroup = view.findViewById<RadioGroup>(R.id.radioGroup)
            val selectedRadioButton = view.findViewById<RadioButton>(radioGroup.checkedRadioButtonId)
            userViewModel.selectedOption = selectedRadioButton.text.toString()
            userViewModel.user = user
            println(user)
            Navigation.findNavController(view).navigate(R.id.navigate_step2ToStep3)
        }

        nastaviButtonProdavac.setOnClickListener{
            user.pib = prodavac_pib.text.toString()
            user.accountNumber = bankovni_racun.text.toString()
            user.latitude = live_latitude
            user.longitude = live_longitude
            val radioGroup = view.findViewById<RadioGroup>(R.id.radioGroup)
            val selectedRadioButton = view.findViewById<RadioButton>(radioGroup.checkedRadioButtonId)
            userViewModel.selectedOption = selectedRadioButton.text.toString()
            userViewModel.user = user
            println(user)
            Navigation.findNavController(view).navigate(R.id.navigate_step2ToStep3)
        }

        prodavac_lokacija.setOnEditorActionListener{ _, actionId, _ ->
            if (actionId == EditorInfo.IME_ACTION_DONE) {
                // Pozvati željenu metodu ili izvršiti akciju kada se pritisne "Done" na tastaturi
                sendRequestForCoordinates()
            }
            false
        }

        kupacRadioButton.setOnClickListener { viewForKupac() }

        prodavacRadioButton.setOnClickListener { viewForProdavac()}
        prodavac_pib.addTextChangedListener(object : TextWatcher {
            override fun beforeTextChanged(s: CharSequence?, start: Int, count: Int, after: Int) {
                // Implementacija pre promene teksta
            }

            override fun onTextChanged(s: CharSequence?, start: Int, before: Int, count: Int) {
                // Implementacija tokom promene teksta
            }

            override fun afterTextChanged(s: Editable?) {
                validateNastaviButtonProdavac() }
        })

        bankovni_racun.addTextChangedListener(object : TextWatcher {
            override fun beforeTextChanged(s: CharSequence?, start: Int, count: Int, after: Int) {
                // Implementacija pre promene teksta
            }

            override fun onTextChanged(s: CharSequence?, start: Int, before: Int, count: Int) {
                // Implementacija tokom promene teksta
            }

            override fun afterTextChanged(s: Editable?) {
                validateNastaviButtonProdavac() }
        })

        dostavljacRadioButton.setOnClickListener{ viewForDostavljac() }

        return view
    }

    fun viewForKupac(){
        scrollView5.visibility = View.GONE
        kupacOptionsLayout.visibility = View.VISIBLE
        dostavljacOptionsLayout.visibility=View.GONE
        prodavacOptionsLayout.visibility = View.GONE
        val drawable = ContextCompat.getDrawable(requireActivity(), R.drawable.full_fill_button)
        nastaviButton = view.findViewById<Button>(R.id.Nastavi)
        nastaviButton.visibility = View.VISIBLE
        imageView5.visibility = View.VISIBLE
        nastaviButton.background = drawable
        nastaviButton.isEnabled = true
    }

    fun viewForDostavljac(){
        scrollView5.visibility = View.GONE
        kupacOptionsLayout.visibility = View.GONE
        dostavljacOptionsLayout.visibility=View.VISIBLE
        prodavacOptionsLayout.visibility = View.GONE
        nastaviButton = view.findViewById<Button>(R.id.Nastavi)
        nastaviButton.visibility = View.VISIBLE
        imageView5.visibility = View.VISIBLE
        validateNastaviButtonDostavljac()
    }

    fun viewForProdavac(){
        scrollView5.visibility = View.VISIBLE
        kupacOptionsLayout.visibility = View.GONE
        dostavljacOptionsLayout.visibility=View.GONE
        prodavacOptionsLayout.visibility = View.VISIBLE
        nastaviButton = view.findViewById<Button>(R.id.Nastavi)
        nastaviButton.visibility = View.GONE
        imageView5.visibility = View.GONE

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
        validateNastaviButtonProdavac()
    }

    fun setFieldOnStep2(userViewModel: UserViewModel, view: View){
        val radioGroup = view.findViewById<RadioGroup>(R.id.radioGroup)
        when (userViewModel.selectedOption) {
            "Kupac" -> {
                val radioButtonToSelect = view.findViewById<RadioButton>(R.id.Kupac)
                radioButtonToSelect.isChecked = true
                radioGroup.check(radioButtonToSelect.id)
                viewForKupac()
            }
            "Dostavljač" -> {
                val radioButtonToSelect = view.findViewById<RadioButton>(R.id.Dostavljac)
                radioButtonToSelect.isChecked = true
                radioGroup.check(radioButtonToSelect.id)
                viewForDostavljac()
                if(vehicle["auto"]==true){automobil= view.findViewById(R.id.auto);automobil.setBackgroundResource(R.drawable.border_background_orange)}
                if(vehicle["motocikl"]==true){motocikl= view.findViewById(R.id.motocikl); motocikl.setBackgroundResource(R.drawable.border_background_orange)}
                if(vehicle["kombi"]==true){kombi= view.findViewById(R.id.kombi); kombi.setBackgroundResource(R.drawable.border_background_orange)}
                if(vehicle["kamion"]==true){kamion= view.findViewById(R.id.kombi); kamion.setBackgroundResource(R.drawable.border_background_orange)}
            }
            "Mali prodavac" -> {
                val radioButtonToSelect = view.findViewById<RadioButton>(R.id.Prodavac)
                radioButtonToSelect.isChecked = true
                radioGroup.check(radioButtonToSelect.id)
                viewForProdavac()
                prodavac_pib.setText(userViewModel.user!!.pib)
                bankovni_racun.setText(userViewModel.user!!.accountNumber)
                if(userViewModel.user!!.latitude != null && userViewModel.user!!.longitude !=null ){
                    setMap(userViewModel.user!!.latitude as Double, userViewModel.user!!.longitude as Double)
                    sendRequestForStringLocation(userViewModel.user!!.latitude as Double, userViewModel.user!!.longitude as Double)
                }
            }
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

    fun setMap(latitude:Double, longitude:Double){
        val newPoint = GeoPoint(latitude, longitude)
        live_latitude = latitude
        live_longitude = longitude
        validateNastaviButtonProdavac()
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
        val address = prodavac_lokacija.text.toString()
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
                    prodavac_lokacija.setText(fullAddress)
                    println("Adresa: $fullAddress")
                }
            },
            {
                // Greška prilikom dobijanja adrese
                println("Greška prilikom dobijanja adrese.")
            })
    }

    private fun validateNastaviButtonDostavljac(){
        if(vehicle["auto"]==false && vehicle["motocikl"]==false && vehicle["kombi"]==false && vehicle["kamion"]==false){
            val drawable = ContextCompat.getDrawable(requireContext(),
                R.drawable.full_fill_button_disabled
            )
            nastaviButton.background = drawable
            nastaviButton.isEnabled = false
        }
        else{
            val drawable = ContextCompat.getDrawable(requireContext(), R.drawable.full_fill_button)
            nastaviButton.background = drawable
            nastaviButton.isEnabled = true
        }
    }
    private fun validateNastaviButtonProdavac(){
        prodavac_pib = view.findViewById<EditText>(R.id.pib)
        if(!(prodavac_pib.text.length == 10)) {
            //tada nije dozvoljeno dalje
            val drawable = ContextCompat.getDrawable(requireContext(),
                R.drawable.full_fill_button_disabled
            )
            nastaviButtonProdavac.background = drawable
            nastaviButtonProdavac.isEnabled = false
        }
        else if(!(bankovni_racun.text.length == 18)){
            val drawable = ContextCompat.getDrawable(requireContext(),
                R.drawable.full_fill_button_disabled
            )
            nastaviButtonProdavac.background = drawable
            nastaviButtonProdavac.isEnabled = false
        }
        else if (!(live_latitude >= -90.0 && live_latitude <= 90.0) || !(live_longitude >= -180.0 && live_longitude <= 180.0)){
            //ni onda nije dozvoljeno
            val drawable = ContextCompat.getDrawable(requireContext(),
                R.drawable.full_fill_button_disabled
            )
            nastaviButtonProdavac.background = drawable
            nastaviButtonProdavac.isEnabled = false
        }
        else{
            // sada je samo dozvoljeno
            val drawable = ContextCompat.getDrawable(requireContext(), R.drawable.full_fill_button)
            nastaviButtonProdavac.background = drawable
            nastaviButtonProdavac.isEnabled = true
        }
    }

    private fun setVehicleInformation(type:String, view:View){
        if(vehicle[type]==true)     {vehicle[type]=false; view.setBackgroundResource(R.drawable.border_background)}
        else if(vehicle[type]==false)    {vehicle[type]=true; view.setBackgroundResource(R.drawable.border_background_orange)}
        validateNastaviButtonDostavljac()
    }

    fun setVehicle(view:View){
        when(view.id){
            R.id.auto ->        {setVehicleInformation("auto", view)}
            R.id.motocikl ->    {setVehicleInformation("motocikl", view)}
            R.id.kombi ->       {setVehicleInformation("kombi", view)}
            R.id.kamion ->      {setVehicleInformation("kamion", view)}
        }
    }
    private val PICK_IMAGE_REQUEST_KUPAC = 1
    private val PICK_IMAGE_REQUEST_DOSTAVLJAC = 2
    fun addActionForImage(){
        println("SETUJEM")
        import_image_kupac.setOnClickListener {
            val intent = Intent(Intent.ACTION_PICK, MediaStore.Images.Media.EXTERNAL_CONTENT_URI)
            startActivityForResult(intent, PICK_IMAGE_REQUEST_KUPAC)
        }
        import_image_dostavljac.setOnClickListener{
            val intent = Intent(Intent.ACTION_PICK, MediaStore.Images.Media.EXTERNAL_CONTENT_URI)
            startActivityForResult(intent, PICK_IMAGE_REQUEST_DOSTAVLJAC)
        }
        import_image_prodavac.setOnClickListener{
            println("usao prodavac")
            val intent = Intent(Intent.ACTION_PICK, MediaStore.Images.Media.EXTERNAL_CONTENT_URI)
            startActivityForResult(intent, 3)
        }
    }
    override fun onActivityResult(requestCode: Int, resultCode: Int, data: Intent?) {
        super.onActivityResult(requestCode, resultCode, data)

        if (resultCode == Activity.RESULT_OK && data != null && data.data != null) {
            val imageUri: Uri = data.data!!

            when (requestCode) {
                PICK_IMAGE_REQUEST_KUPAC -> {
                    val imageViewKupac = view.findViewById<ImageView>(R.id.imageView3)
                    imageViewKupac.setImageURI(imageUri)
                    val imageViewProdavac = view.findViewById<ImageView>(R.id.imageView4)
                    imageViewProdavac.setImageURI(imageUri)
                    val imageViewProdavacc = view.findViewById<ImageView>(R.id.imageViewProdavac)
                    imageViewProdavacc.setImageURI(imageUri)
                    val userViewModel: UserViewModel by activityViewModels()
                    userViewModel.user?.profileImageUri = imageUri
                }
                PICK_IMAGE_REQUEST_DOSTAVLJAC -> {
                    val imageViewKupac = view.findViewById<ImageView>(R.id.imageView3)
                    imageViewKupac.setImageURI(imageUri)
                    val imageViewProdavac = view.findViewById<ImageView>(R.id.imageView4)
                    imageViewProdavac.setImageURI(imageUri)
                    val imageViewProdavacc = view.findViewById<ImageView>(R.id.imageViewProdavac)
                    imageViewProdavacc.setImageURI(imageUri)
                    val userViewModel: UserViewModel by activityViewModels()
                    userViewModel.user?.profileImageUri = imageUri
                }
                3 -> {
                    val imageViewKupac = view.findViewById<ImageView>(R.id.imageView3)
                    imageViewKupac.setImageURI(imageUri)
                    val imageViewProdavac = view.findViewById<ImageView>(R.id.imageView4)
                    imageViewProdavac.setImageURI(imageUri)
                    val imageViewProdavacc = view.findViewById<ImageView>(R.id.imageViewProdavac)
                    imageViewProdavacc.setImageURI(imageUri)
                    val userViewModel: UserViewModel by activityViewModels()
                    userViewModel.user?.profileImageUri = imageUri
                }
            }
        }
    }

}