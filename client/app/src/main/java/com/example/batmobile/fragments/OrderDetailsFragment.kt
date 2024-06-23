package com.example.batmobile.fragments

import android.annotation.SuppressLint
import android.app.AlertDialog
import android.app.DatePickerDialog
import android.app.Dialog
import android.os.Build
import android.os.Bundle
import android.text.Editable
import android.text.TextWatcher
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import android.view.Window
import android.widget.Button
import android.widget.EditText
import android.widget.ImageView
import android.widget.LinearLayout
import android.widget.TextView
import android.widget.Toast
import androidx.annotation.RequiresApi
import androidx.constraintlayout.widget.ConstraintLayout
import androidx.fragment.app.Fragment
import androidx.navigation.Navigation
import androidx.navigation.fragment.findNavController
import com.example.batmobile.DTOFromServer.Order
import com.example.batmobile.DTOFromServer.ResponseObjectTemplate
import com.example.batmobile.DTOFromServer.SellerPurchaseItems
import com.example.batmobile.R
import com.example.batmobile.enums.Role
import com.example.batmobile.fragments.neulogovan.HomeNeuloganFragmentDirections
import com.example.batmobile.network.ApiClient
import com.example.batmobile.network.Config
import com.example.batmobile.services.Comments
import com.example.batmobile.services.Image
import com.example.batmobile.services.JWTService
import com.example.batmobile.services.Map
import com.google.gson.Gson
import com.google.gson.reflect.TypeToken
import de.hdodenhof.circleimageview.CircleImageView
import org.json.JSONObject
import org.osmdroid.util.GeoPoint
import org.osmdroid.views.MapView
import java.text.SimpleDateFormat
import java.time.LocalDate
import java.time.format.DateTimeFormatter
import java.util.Calendar
import java.util.Locale

class OrderDetailsFragment : Fragment() {

    private lateinit var view: View
    private lateinit var apiClient: ApiClient

    private lateinit var close                    : ImageView
    private lateinit var id_order                         : TextView
    private lateinit var ime                              : TextView
    private lateinit var prezime                          : TextView
    private lateinit var od_prodavca                      : TextView
    private lateinit var do_kupca                         : TextView
    private lateinit var placanje                         : TextView

    private lateinit var offer_info                       : ConstraintLayout
    private lateinit var datum                            : TextView
    private lateinit var cena_dostave                     : TextView
    private lateinit var komentar                         : TextView

    private lateinit var mapViewRecomended                : MapView
    private lateinit var list_of_orders                   : ConstraintLayout
    private lateinit var confirm_offer                    : Button
    private lateinit var accept                           : Button
    private lateinit var reject                           : Button

    private lateinit var input_cena:                            EditText
    private lateinit var input_datum:                           Button
    private lateinit var tvSelectedDate:                        TextView
    private lateinit var editText:                              EditText
    private lateinit var send_offer:                            Button
    private lateinit var close_overlay:                         ImageView
    private lateinit var order_id:                              TextView
    private lateinit var see_purchase:                          Button
    private val calendar = Calendar.getInstance()

    var flagPrice                                         : Boolean = false
    var flagDate                                          : Boolean = false
    var flagEmptyPrice                                    : Boolean = true
    var flagEmptyDate                                     : Boolean = true
    lateinit var error                                    : TextView
    lateinit var error_datum                              : TextView
    lateinit var error_cena                               : TextView

    private          var orderId              : Long = -1
    private          var offerId              : Long = -1

    private fun getAllStuff() {
        apiClient               = ApiClient(requireContext())

        close = view.findViewById(R.id.overlay_close)

        id_order                = view.findViewById(R.id.order_id)
        ime                     = view.findViewById(R.id.ime)
        prezime                 = view.findViewById(R.id.prezime)
        od_prodavca             = view.findViewById(R.id.od_prodavca)
        do_kupca                = view.findViewById(R.id.do_kupca)
        placanje                = view.findViewById(R.id.placanje)

        offer_info = view.findViewById<ConstraintLayout>(R.id.offer_info)
        datum = view.findViewById<TextView>(R.id.datum)
        cena_dostave = view.findViewById<TextView>(R.id.cena_dostave)
        komentar = view.findViewById<TextView>(R.id.komentar)

        mapViewRecomended       = view.findViewById(R.id.mapView)
        list_of_orders          = view.findViewById(R.id.list_of_orders)
        confirm_offer           = view.findViewById(R.id.confirm_offer)
        see_purchase            = view.findViewById<Button>(R.id.see_purchase)
        accept                  = view.findViewById(R.id.accept)
        reject                  = view.findViewById(R.id.reject)
        see_purchase.setOnClickListener { renderMyProductsInOrder() }

    }

    @RequiresApi(Build.VERSION_CODES.O)
    @SuppressLint("MissingInflatedId")
    private fun openJobConfimation(job: Long) {

        val inflater = layoutInflater
        val dialogOffer = inflater.inflate(R.layout.overlay_ponuda_za_posao, null)
        val alertDialogBuilder = AlertDialog.Builder(requireContext())
        alertDialogBuilder.setView(dialogOffer)
        val alertDialog: AlertDialog = alertDialogBuilder.create()

        input_cena          = dialogOffer.findViewById<EditText>(R.id.input_cena)
        input_datum         = dialogOffer.findViewById<Button>(R.id.input_datum)
        editText            = dialogOffer.findViewById<EditText>(R.id.editText)
        send_offer          = dialogOffer.findViewById<Button>(R.id.send_offer)
        close_overlay       = dialogOffer.findViewById<ImageView>(R.id.close)
        order_id            = dialogOffer.findViewById<TextView>(R.id.order_id)
        error               = dialogOffer.findViewById<TextView>(R.id.error)
        error_cena          = dialogOffer.findViewById<TextView>(R.id.error_cena)
        error_datum         = dialogOffer.findViewById<TextView>(R.id.error_datum)
        tvSelectedDate      = dialogOffer.findViewById<TextView>(R.id.tvSelectedDate)


        error.setText("Nisu uneseni svi obavezni zahtevi!")

        order_id.text = "Porudžbina #" + job

        alertDialog.show()
        close_overlay.setOnClickListener {
            alertDialog.dismiss()
        }

        send_offer.setOnClickListener {
            val izabrani_datum = tvSelectedDate.text.toString().removePrefix("Izabrani datum: ")
            sendConfirmationToServer(input_cena.text.toString(),izabrani_datum,editText.text.toString(),job)
            alertDialog.dismiss()
        }

        input_cena.addTextChangedListener(object : TextWatcher {
            override fun beforeTextChanged(s: CharSequence?, start: Int, count: Int, after: Int) {

            }

            override fun onTextChanged(s: CharSequence?, start: Int, before: Int, count: Int) {
            }

            override fun afterTextChanged(s: Editable?) {
                validateCena(s.toString(),error)
                validateAllInputs()
            }
        })

        input_datum.setOnClickListener { showDatePicker() }
    }

    private fun showDatePicker() {
        val datePickerDialog = DatePickerDialog(
            requireContext(), {DatePicker, year: Int, monthOfYear: Int, dayOfMonth: Int ->
                val selectedDate = Calendar.getInstance()
                selectedDate.set(year, monthOfYear, dayOfMonth)
                val dateFormat = SimpleDateFormat("yyyy-MM-dd", Locale.getDefault())
                val formattedDate = dateFormat.format(selectedDate.time)
                tvSelectedDate.text = "Izabrani datum: $formattedDate"
                flagDate = true
                validateAllInputs()

            },
            calendar.get(Calendar.YEAR),
            calendar.get(Calendar.MONTH),
            calendar.get(Calendar.DAY_OF_MONTH)
        )
        datePickerDialog.show()
    }

    @RequiresApi(Build.VERSION_CODES.O)
    private fun sendConfirmationToServer(
        cena_dostave: String,
        datum_dostave: String,
        editText: String,
        job: Long
    ) {
        var cena = cena_dostave
        var date = datum_dostave

        println("Datum pre slanja  "+ date)

//        println("Datum " + date)
//        var datum = formatirajDatum(date)
//        println("Datum formatiran " + datum)

        var komentar = editText
        var id = job
        val url: String = Config.ip_address + ":" + Config.port + "/deliverer/send-job-offer"
        val orderDetails = JSONObject().apply{
            put("orderId"  , id)
            put("price"     , cena)
            put("date"      , date)
            put("comment"   , komentar)
        }
        println("DETALJI:" + orderDetails)
        apiClient.sendPostRequestWithJSONObjectWithStringResponse(
            url,
            JWTService.getToken(),
            orderDetails,
            { response ->
                println("Uspesno poslato: $response")
                val gson = Gson()
                val typeToken = object : TypeToken<ResponseObjectTemplate<Object>>() {}.type
                val jsonResponse = gson.fromJson<ResponseObjectTemplate<Object>>(response, typeToken)
                if(jsonResponse.message.equals("Tip for job sent!"))
                {
                    Toast.makeText(context, "Uspešno ste se poslali zahtev za posao", Toast.LENGTH_LONG).show()

                }
//                Navigation.findNavController(requireActivity(), R.id.fragmentContainerNeulogovan).navigate(R.id.action_HomeNeulogovanFragment)
                findNavController().navigateUp()
            },
            { error ->
                println("Greska prilikom slanja: $error")
            }
        )

    }

    @RequiresApi(Build.VERSION_CODES.O)
    fun formatirajDatum(datum: String): String {
        val originalniFormat = DateTimeFormatter.ofPattern("dd-MM-yyyy")
        val noviFormat = DateTimeFormatter.ofPattern("yyyy-MM-dd")

        val localDate = LocalDate.parse(datum, originalniFormat)
        return localDate.format(noviFormat)
    }

    fun validateAllInputs()
    {
        if(flagDate && flagPrice)
        {
            error.visibility = View.GONE
            send_offer.isEnabled = true
            send_offer.setBackgroundResource(R.drawable.full_fill_button)
        }
        else
        {
            error.visibility = View.VISIBLE
            send_offer.isEnabled = false
            send_offer.setBackgroundResource(R.drawable.full_fill_button_disabled)
        }
    }

    fun validateCena(cena: String, error: TextView) {
        val cenaPattern = Regex("^[0-9]+(\\.[0-9]{1,2})?$") // Dozvoljen je unos brojeva sa opcionalnim decimalnim mestom (do dve decimale)

        if (cena.isEmpty()) {
            flagPrice = false
        } else if (!cena.matches(cenaPattern)) {
            flagPrice = false
        } else {
            flagPrice = true
        }
    }

    private fun offerConfirmation(flag: Boolean) {
        var url:String = Config.ip_address + ":" + Config.port + "/change-delivery-offer-status"
        println("FLAG:" + flag)
        val send_url = "$url?offerId=$offerId&offerAccepted=$flag"
        apiClient.sendPostRequestWithToken(JWTService.getToken(), send_url,
            { response ->
                println("Uspesno poslato: $response")
                val gson = Gson()
                val typeToken = object : TypeToken<ResponseObjectTemplate<Object>>() {}.type
                val jsonResponse = gson.fromJson<ResponseObjectTemplate<Object>>(response, typeToken)
                print(jsonResponse)

            },
            { error ->
                println("Greska prilikom slanja: $error")
            }
        )

    }

    @RequiresApi(Build.VERSION_CODES.O)
    override fun onCreateView(
        inflater: LayoutInflater, container: ViewGroup?,
        savedInstanceState: Bundle?
    ): View? {
        view = inflater.inflate(R.layout.fragment_product_in_order, container, false)
        val args: OrderDetailsFragmentArgs = OrderDetailsFragmentArgs.fromBundle(requireArguments())
        orderId = args.orderId
        offerId = args.offerId

        getAllStuff()
        getOrderDetails(orderId)
        setVisibility()
        Map.setInitMap(mapViewRecomended,requireContext())

        close.setOnClickListener{ findNavController().navigateUp() }

        if (offerId == -2L){
            confirm_offer.visibility = View.GONE
        }
        else if(offerId == -1L){
            confirm_offer.setOnClickListener { openJobConfimation(orderId) }
        }

        reject.setOnClickListener {
            offerConfirmation(true)
            findNavController().navigateUp()
        }
        accept.setOnClickListener {
            offerConfirmation(false)
            findNavController().navigateUp()
        }

        return view
    }

    private fun setVisibility() {
        if(JWTService.getTokenIfExist(requireContext()) == Role.User){
            confirm_offer.visibility                = View.GONE
            accept.visibility                       = View.VISIBLE
            reject.visibility                       = View.VISIBLE
        }
        if(JWTService.getTokenIfExist(requireContext()) == Role.Deliverer){
            confirm_offer.visibility                = View.VISIBLE
            accept.visibility                       = View.GONE
            reject.visibility                       = View.GONE
        }
    }

    private lateinit var order_information: Order
    private fun getOrderDetails(job: Long) {
        var url: String = Config.ip_address+":"+ Config.port + "/order/order-details"
        var send_url = "$url?orderId=$job"
        apiClient.sendGetRequestEmpty(JWTService.getToken(),send_url,
            { response ->
                var gson = Gson()
                var typeToken = object : TypeToken<ResponseObjectTemplate<Order>>(){}.type
                var response_obj = gson.fromJson<ResponseObjectTemplate<Order>>(response, typeToken)
                println("STIGLO:" + response_obj.data)
                renderOrderInformation(response_obj.data)
                order_information = response_obj.data

                if(!order_information.buyerAddress.contains("(S)")){
                    val startPoint = GeoPoint(order_information.sellerLat, order_information.sellerLong)
                    println("->"+order_information.buyerLat + " "+ order_information.buyerLong)
                    val endPoint = GeoPoint(order_information.buyerLat, order_information.buyerLong)

                    val task = Map.Companion.RoutingTask(requireContext(), mapViewRecomended, startPoint, endPoint)
                    task.execute()
                }
                else{
                    apiClient.getCoordinatesForAddress(order_information.buyerAddress,
                        {long,lat->
                            val startPoint = GeoPoint(order_information.sellerLat, order_information.sellerLong)
                            val endPoint = GeoPoint(long, lat)

                            val task = Map.Companion.RoutingTask(requireContext(), mapViewRecomended, startPoint, endPoint)
                            task.execute()
                        },
                        {})
                }
            },
            { error ->
                println(error)
            }
        )
    }

    private fun renderOrderInformation(data: Order) {
        id_order.text = "#" + data.orderId.toString()
        ime.text = data.buyerName
        prezime.text = data.buyerSurname

        if(data.date_time != null){
            datum.text = data.date_time.split("T")[0]
            cena_dostave.text = data.price.toString() + " rsd."
            komentar.text = data.comment
            offer_info.visibility = View.VISIBLE
        }

        if(data.buyerAddress == null)
            do_kupca.text = "Simulirana adresa"
        else
            do_kupca.text = data.buyerAddress
        apiClient.getAddressFromCoordinates(requireContext(),data.sellerLat, data.sellerLong,
            {response-> od_prodavca.text = response.toString();}, {
                val toast = Toast.makeText(requireContext(), "OSM ne može naći adresu na osnovu koordinata", Toast.LENGTH_LONG)
                toast.show()
            })

        when(data.paymentMethodId){
            1 -> { placanje.setText("Plaćanje unapred") }
            2 -> { placanje.setText("Plaćanje pozećem") }
            3 -> { placanje.setText("Lično plaćanje") }
        }

        val itemLayoutParams : LinearLayout.LayoutParams;
        itemLayoutParams = Comments.itemLayoutParamsForVertical
        itemLayoutParams.setMargins(0, (Comments.marginInDp +20), 0, 0)

        for( orderItem: SellerPurchaseItems in data.purchaseItems ){
            val itemView = layoutInflater.inflate(R.layout.component_item_in_cart, null)
            val item_name       = itemView.findViewById<TextView>(R.id.item_name)
            val item_quantity   = itemView.findViewById<TextView>(R.id.item_quantity)
            val item_image      = itemView.findViewById<CircleImageView>(R.id.item_image)
            val item_remove_from_cart = itemView.findViewById<ImageView>(R.id.remove_from_cart)
            item_name.text      = orderItem.productName
            item_quantity.text  =  orderItem.measurement +" " + orderItem.productPrice + "Rsd x " + orderItem.quantity +" = " + orderItem.productPrice * orderItem.quantity + " rsd."
            Image.setImageResource(item_image, orderItem.productImage, orderItem.productCategoryId)
            itemView.layoutParams = itemLayoutParams
            item_remove_from_cart.visibility = View.GONE
            itemView.setOnClickListener{
                val action = ProductViewFragmentDirections.actionProductViewFragment(orderItem.productId)
                findNavController().navigate(action)
            }
            list_of_orders.addView(itemView)
        }
    }

    private fun renderMyProductsInOrder(){
        val itemLayoutParams : LinearLayout.LayoutParams;
        itemLayoutParams = Comments.itemLayoutParamsForVertical
        itemLayoutParams.setMargins(0, (Comments.marginInDp +14), 0, 0)

        val dialog = Dialog(requireContext())
        dialog.requestWindowFeature(Window.FEATURE_NO_TITLE)
        dialog.setCancelable(true)
        dialog.setContentView(R.layout.overlay_product_in_order)

        val close = dialog.findViewById<ImageView>(R.id.overlay_close)
        close.setOnClickListener{dialog.dismiss()}
        var row = dialog.findViewById<LinearLayout>(R.id.product_list)

        var title = dialog.findViewById<TextView>(R.id.textView23)
        title.text = "Porudžbenica:"

        var titl_id  = dialog.findViewById<TextView>(R.id.order_id)
        titl_id.visibility = View.GONE


        for (element: SellerPurchaseItems in order_information.purchaseItems){
            println(element)
            val itemView = layoutInflater.inflate(R.layout.component_item_in_cart, null)
            val item_name       = itemView.findViewById<TextView>(R.id.item_name)
            val item_quantity   = itemView.findViewById<TextView>(R.id.item_quantity)
            val item_image      = itemView.findViewById<CircleImageView>(R.id.item_image)
            val item_remove_from_cart = itemView.findViewById<ImageView>(R.id.remove_from_cart)
            val feedback_for_product = itemView.findViewById<TextView>(R.id.feedback_for_product)
            item_remove_from_cart.visibility = View.GONE
//                feedback_for_product.visibility = View.VISIBLE
            item_name.text      = element.productName
            item_quantity.text  =  element.measurement +" " + element.productPrice + "Rsd x " + element.quantity +" = " + element.productPrice * element.quantity + " rsd."
            Image.setImageResource(item_image, element.productImage, element.productCategoryId)
            itemView.layoutParams = itemLayoutParams
            row.addView(itemView)
        }

        val width = (resources.displayMetrics.widthPixels * 1.0).toInt()
        val height = (resources.displayMetrics.heightPixels * 0.85).toInt()
        dialog.window?.setLayout(width, height)

        dialog.show()
    }
}