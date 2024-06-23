package com.example.batmobile.fragments.neulogovan

import android.annotation.SuppressLint
import android.app.Dialog
import android.content.Context
import android.graphics.drawable.BitmapDrawable
import android.os.Bundle
import android.text.Editable
import android.text.TextWatcher
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import android.view.Window
import android.widget.Button
import android.widget.EditText
import android.widget.HorizontalScrollView
import android.widget.ImageView
import android.widget.LinearLayout
import android.widget.TextView
import androidx.cardview.widget.CardView
import androidx.constraintlayout.widget.ConstraintLayout
import androidx.core.content.ContextCompat
import androidx.core.widget.NestedScrollView
import androidx.fragment.app.Fragment
import androidx.navigation.fragment.findNavController
import com.example.batmobile.DTOFromServer.ExploreProduct
import com.example.batmobile.DTOFromServer.JobOffer
import com.example.batmobile.DTOFromServer.RandomProductResponse
import com.example.batmobile.DTOFromServer.ResponseListTemplate
import com.example.batmobile.DTOFromServer.Search
import com.example.batmobile.DTOFromServer.Seller
import com.example.batmobile.R
import com.example.batmobile.SellerProfileFragmentDirections
import com.example.batmobile.enums.Role
import com.example.batmobile.fragments.CartFragmentDirections
import com.example.batmobile.fragments.OrderDetailsFragmentDirections
import com.example.batmobile.fragments.ProductViewFragmentDirections
import com.example.batmobile.network.ApiClient
import com.example.batmobile.network.Config
import com.example.batmobile.services.Comments
import com.example.batmobile.services.Image
import com.example.batmobile.services.JWTService
import com.example.batmobile.services.Map
import com.google.gson.Gson
import com.google.gson.JsonSyntaxException
import com.google.gson.reflect.TypeToken
import de.hdodenhof.circleimageview.CircleImageView
import kotlinx.coroutines.CoroutineScope
import kotlinx.coroutines.Dispatchers
import kotlinx.coroutines.async
import kotlinx.coroutines.launch
import org.json.JSONArray
import org.json.JSONObject
import org.osmdroid.util.GeoPoint
import org.osmdroid.views.MapView
import org.osmdroid.views.overlay.ItemizedIconOverlay
import org.osmdroid.views.overlay.OverlayItem


class ExploreNeulogovanFragment : Fragment() {

    private lateinit var view: View
    private lateinit var apiClient: ApiClient
    private lateinit var scroll_products : NestedScrollView
    private lateinit var linear_container : LinearLayout

    private lateinit var gson: Gson

    private lateinit var title:                         TextView
    private lateinit var cart:                          ImageView
    private lateinit var editTextText: EditText
    private lateinit var search:                        NestedScrollView
    private lateinit var searched_products:             LinearLayout
    private lateinit var not_found_msg:                 TextView
    private lateinit var title_products:                TextView
    private lateinit var title_sellers:                 TextView
    private          var searched_type_products: Boolean = true

    private lateinit var horizontalScrollView3:         HorizontalScrollView
    private lateinit var dugme1:                        Button
    private lateinit var dugme2:                        Button
    private lateinit var dugme3:                        Button

    private lateinit var mapViewContainer:              CardView
    private lateinit var mapView:                       MapView
    private lateinit var poslovi_ponude:                 NestedScrollView

//    private lateinit var input_cena:                            EditText
//    private lateinit var input_datum:                           EditText
//    private lateinit var editText:                              EditText
//    private lateinit var send_offer:                            Button
//    private lateinit var close:                                 ImageView
//    private lateinit var order_id:                              TextView
//
//    private lateinit var close_overlay                    : ImageView
//    private lateinit var id_order                         : TextView
//    private lateinit var ime                              : TextView
//    private lateinit var prezime                          : TextView
//    private lateinit var od_prodavca                      : TextView
//    private lateinit var do_kupca                         : TextView
//    private lateinit var placanje                         : TextView
//    private lateinit var mapViewRecomended                : MapView
//    private lateinit var list_of_orders                   : ConstraintLayout
//    private lateinit var confirm_offer                    : Button

    fun getAllStuff(){
        apiClient = ApiClient(requireContext())
        scroll_products = view.findViewById(R.id.scroll_products)
        linear_container = view.findViewById(R.id.linear_container)
        poslovi_ponude = view.findViewById(R.id.scroll_jobs)

        gson = Gson()

        title                       = view.findViewById<TextView>(R.id.textView)
        cart                        = view.findViewById<ImageView>(R.id.cart)
        editTextText                = view.findViewById<EditText>(R.id.editTextText)
        search                      = view.findViewById<NestedScrollView>(R.id.search)
        searched_products           = view.findViewById<LinearLayout>(R.id.searched_products)
        not_found_msg               = view.findViewById<TextView>(R.id.not_found_msg)
        title_products              = view.findViewById<TextView>(R.id.title_products)
        title_sellers               = view.findViewById<TextView>(R.id.title_sellers)

        horizontalScrollView3       = view.findViewById<HorizontalScrollView>(R.id.horizontalScrollView3)
        dugme1                      = view.findViewById<Button>(R.id.dugme1)
        dugme2                      = view.findViewById<Button>(R.id.dugme2)
        dugme3                      = view.findViewById<Button>(R.id.dugme3)
        mapViewContainer            = view.findViewById<CardView>(R.id.mapViewContainer)
        mapView                     = view.findViewById<MapView>(R.id.mapView)
    }

    fun setAllEventListener(){

        cart.setOnClickListener{
            val action = CartFragmentDirections.actionCartFragment()
            findNavController().navigate(action)
        }

        title.setOnClickListener{
            editTextText.setText("")
            searched_products.removeAllViews()

            searched_type_products = true
            title_sellers.setTextColor(ContextCompat.getColor(requireContext(), R.color.black))
            title_products.setTextColor(ContextCompat.getColor(requireContext(), R.color.orange))

            search.visibility = View.GONE
            setScroll()
//            scroll_products.visibility = View.VISIBLE
        }

        editTextText.addTextChangedListener (object: TextWatcher {
            override fun beforeTextChanged(s: CharSequence?, start: Int, count: Int, after: Int) {
            }

            override fun onTextChanged(s: CharSequence?, start: Int, before: Int, count: Int) {
            }

            override fun afterTextChanged(s: Editable?) {
                if(s.toString().length > 0){
                    not_found_msg.visibility            = View.VISIBLE
                    searching(s.toString())
                    search.visibility                   = View.VISIBLE
                    poslovi_ponude.visibility = View.GONE
                    scroll_products.visibility  = View.GONE
                    mapViewContainer.visibility = View.GONE
                    horizontalScrollView3.visibility = View.GONE
                }
                else{
                    search.visibility = View.GONE
                    horizontalScrollView3.visibility = View.VISIBLE
                    setScroll()
                }
            }

        })

        title_products.setOnClickListener{
            if(searched_type_products == false){

                searched_type_products = true
                title_sellers.setTextColor(ContextCompat.getColor(requireContext(), R.color.black))
                title_products.setTextColor(ContextCompat.getColor(requireContext(), R.color.orange))

                searching(editTextText.text.toString())
            }
        }
        title_sellers.setOnClickListener{
            if(searched_type_products == true){

                searched_type_products = false

                title_sellers.setTextColor(ContextCompat.getColor(requireContext(), R.color.orange))
                title_products.setTextColor(ContextCompat.getColor(requireContext(), R.color.black))

                searching(editTextText.text.toString())
            }
        }

    }

    fun searching(query: String){
        var url: String = Config.ip_address+":"+Config.port+"/search/"+query
        apiClient.sendGetRequestEmpty(url,
            {response->
                var searched = gson.fromJson(response, Search::class.java)
                renderSearched(searched)
            },
            {error->
                println(error)
            })
    }

    var listOfIds: MutableList<Long> = mutableListOf()
    fun getRandomProducts() {
        val url: String = Config.ip_address + ":" + Config.port + "/explore/random/products"

        val requestBody: JSONObject = if (listOfIds.isEmpty()) {
            JSONObject().apply {
                put("excludedIds", JSONArray())
            }
        } else {
            JSONObject().apply {
                put("excludedIds", JSONArray(listOfIds))
            }
        }

        apiClient.sendPostRequestWithJSONObjectWithJsonResponse(
            url,
            requestBody,
            { response ->

                try {
                    val gson = Gson()
                    val productExplore = gson.fromJson(response.toString(), RandomProductResponse::class.java)

                    val listOfProducts: List<ExploreProduct> = productExplore.randomProducts
                    listOfIds.addAll(productExplore.listOfIDs)
                    renderRandomProducts(listOfProducts)
                } catch (e: JsonSyntaxException) {
                    println("Greška pri parsiranju JSON-a: ${e.message}")
                }
            },
            { error ->
                println("Greška prilikom slanja zahteva: $error")
            }
        )
    }

    private fun loadMoreProducts() {
        scroll_products.setOnScrollChangeListener { v: NestedScrollView?, _: Int, _: Int, _: Int, _: Int ->
            if (v?.getChildAt(v.childCount - 1) != null &&
                (v.scrollY >= (v.getChildAt(v.childCount - 1).measuredHeight - v.measuredHeight)) &&
                v.scrollY > 0
            ) {
                getRandomProducts()
            }
        }
    }

    fun getSellerCoordinates(){
        val url: String = Config.ip_address + ":" + Config.port + "/getAllSellers"
        apiClient.sendGetRequestEmpty(url,
            { response->
                var gson = Gson()
                val typeToken = object : TypeToken<ResponseListTemplate<Seller>>() {}.type
                var sellers_list = gson.fromJson<ResponseListTemplate<Seller>>(response, typeToken)

                val pin = ContextCompat.getDrawable(requireContext(), R.drawable.location_pin)

                setSellersOnMap(sellers_list.data, mapView, requireContext())
            },
            {error->
                println(error)
            })
    }

    fun getSellerInfo(seller_id: Int, dialog: Dialog){
        val url: String = Config.ip_address + ":" + Config.port + "/getSeller/" + seller_id
        apiClient.sendGetRequestEmpty(url,
            {response ->
                println(response)
                var gson = Gson()
                var seller_info = gson.fromJson(response, Seller::class.java)
                renderSellerInfoInDialog(seller_info, dialog)
            }
            ,
            { error-> }
        )
    }

    private fun getJobOffers() {
        var url: String = Config.ip_address+":"+ Config.port + "/deliverer/get-available-jobs"
        apiClient.sendGetRequestEmpty(
            JWTService.getToken(),url,
            { response ->
                var gson = Gson()
                val typeToken = object : TypeToken<ResponseListTemplate<JobOffer>>() {}.type
                var jobs = gson.fromJson<ResponseListTemplate<JobOffer>>(response, typeToken)
                println(jobs.data)
                renderJobOffers(jobs.data)
            },
            { error ->
                println(error)
            }
        )
    }



    override fun onCreateView(
        inflater: LayoutInflater, container: ViewGroup?,
        savedInstanceState: Bundle?
    ): View? {
        view = inflater.inflate(R.layout.fragment_explore_neulogovan, container, false)


        getAllStuff()
        setAllEventListener()
        setScroll()

        val scope = CoroutineScope(Dispatchers.Default)
        scope.launch {
            async { Map.setInitMap(mapView, requireContext()); getSellerCoordinates() }
            async { getRandomProducts() }
            async { loadMoreProducts() }
            async { getJobOffers() }
        }

        return view
    }

    private fun setScroll(){
        val role:Role?= JWTService.getRoleIfExist(requireContext())
        if(role != null && role == Role.Deliverer){
            setViewFromScroll(1)
        }
        else{
            dugme1.visibility = View.GONE
            setViewFromScroll(2)
        }
        if(JWTService.getToken().equals("null"))
            cart.visibility = View.GONE
        dugme1.setOnClickListener{setViewFromScroll(1)}
        dugme2.setOnClickListener{setViewFromScroll(2)}
        dugme3.setOnClickListener{setViewFromScroll(3)}

    }

    private fun setViewFromScroll(chosen: Int){
//        views
        scroll_products.visibility              = View.GONE
        mapViewContainer.visibility             = View.GONE
        poslovi_ponude.visibility               = View.GONE

        setButtonInScroll(chosen)

        when(chosen){
            1->{ poslovi_ponude.visibility = View.VISIBLE}
            2->{ scroll_products.visibility = View.VISIBLE   }
            3->{ mapViewContainer.visibility = View.VISIBLE  }
        }
    }
    private fun setButtonInScroll(chosen: Int){

        dugme1.setBackgroundResource(R.drawable.empty_button)
        dugme1.setTextColor(resources.getColor(R.color.black))

        dugme2.setBackgroundResource(R.drawable.empty_button)
        dugme2.setTextColor(resources.getColor(R.color.black))

        dugme3.setBackgroundResource(R.drawable.empty_button)
        dugme3.setTextColor(resources.getColor(R.color.black))

        when(chosen){
            1->{
                dugme1.setBackgroundResource(R.drawable.full_fill_button)
                dugme1.setTextColor(resources.getColor(R.color.white))
            }
            2->{
                dugme2.setBackgroundResource(R.drawable.full_fill_button)
                dugme2.setTextColor(resources.getColor(R.color.white))
            }
            3->{
                dugme3.setBackgroundResource(R.drawable.full_fill_button)
                dugme3.setTextColor(resources.getColor(R.color.white))
            }
        }
    }

    private fun renderRandomProducts(productExplore: List<ExploreProduct>) {
        val rows = mutableListOf<LinearLayout>()

        for ((index, product) in productExplore.withIndex()) {
            if (index % 3 == 0) {
                val row = LinearLayout(requireContext())
                row.orientation = LinearLayout.HORIZONTAL
                val layoutParams = LinearLayout.LayoutParams(
                    LinearLayout.LayoutParams.MATCH_PARENT,
                    LinearLayout.LayoutParams.WRAP_CONTENT
                )
                layoutParams.setMargins(0, 0, 0, 20)
                row.layoutParams = layoutParams
                linear_container.addView(row)
                rows.add(row)
            }

            val itemView = layoutInflater.inflate(R.layout.component_explore_product, null)
            val productName = itemView.findViewById<TextView>(R.id.product_name)
            var productImage = itemView.findViewById<ImageView>(R.id.product_image)
            //productImage.setImageResource(R.drawable.paprike)
            productName.text = product.productName
            when(product.categoryId){
                1L -> { productImage.setImageResource(R.drawable.dairy_products) }
                2L -> { productImage.setImageResource(R.drawable.fruits_and_vegetables) }
                3L -> { productImage.setImageResource(R.drawable.meet_products) }
                4L -> { productImage.setImageResource(R.drawable.fresh_meet) }
                5L -> { productImage.setImageResource(R.drawable.cereals) }
                6L -> { productImage.setImageResource(R.drawable.drinks) }
                7L -> { productImage.setImageResource(R.drawable.vegetable_oil) }
                8L -> { productImage.setImageResource(R.drawable.spread) }
            }
            val itemLayoutParams = LinearLayout.LayoutParams(
                0,
                LinearLayout.LayoutParams.WRAP_CONTENT,
                1f
            )
            val marginInPx = 8
            itemLayoutParams.setMargins(marginInPx, 0, marginInPx, 0)
            itemView.setPadding(marginInPx, marginInPx, marginInPx, marginInPx)
            itemView.layoutParams = itemLayoutParams

            itemView.setOnClickListener{
                val action = ProductViewFragmentDirections.actionProductViewFragment(product.productId.toInt())
                findNavController().navigate(action)
            }

            val row = rows.last()

            row.addView(itemView)
        }
    }

    fun renderSearched(searched: Search){

        val itemLayoutParams : LinearLayout.LayoutParams;
        itemLayoutParams = Comments.itemLayoutParamsForVertical
        itemLayoutParams.setMargins(0, (Comments.marginInDp +14), 0, 0)

        //products:
        if(searched_type_products){
            if(searched.products.size > 0)
                not_found_msg.visibility            = View.GONE

            searched_products.removeAllViews()
            for(product in searched.products){
                val itemView = layoutInflater.inflate(R.layout.component_comment, null)
                val stars               = itemView.findViewById<LinearLayout>(R.id.horizontal_layout_star)
                val comment_comment     = itemView.findViewById<TextView>(R.id.comment_comment)
                val image               = itemView.findViewById<ImageView>(R.id.comment_image)
                val comment_username    = itemView.findViewById<TextView>(R.id.comment_username)
                val price               = itemView.findViewById<TextView>(R.id.price)

                stars.visibility = View.GONE
                comment_comment.ellipsize = null
                comment_comment.maxLines = Int.MAX_VALUE
                comment_comment.setText(product.sellerName)
                image.layoutParams.width = 130
                image.layoutParams.height = 130
                price.text = product.price.toString() + " rsd."
                price.visibility = View.VISIBLE


                Image.setImageResource(image, product.picture, product.category_id)
                comment_username.text = product.productName

                itemView.setOnClickListener{
                    val action = ProductViewFragmentDirections.actionProductViewFragment(product.id)
                    findNavController().navigate(action)
                }

                itemView.layoutParams = itemLayoutParams
                searched_products.addView(itemView)
            }
        }
        //sellers
        else{
            if(searched.seller.size > 0)
                not_found_msg.visibility            = View.GONE

            searched_products.removeAllViews()

            for(seller in searched.seller){
                val itemView = layoutInflater.inflate(R.layout.component_comment, null)
                val stars               = itemView.findViewById<LinearLayout>(R.id.horizontal_layout_star)
                val comment_comment     = itemView.findViewById<TextView>(R.id.comment_comment)
                val image               = itemView.findViewById<ImageView>(R.id.comment_image)
                val comment_username    = itemView.findViewById<TextView>(R.id.comment_username)
                val price               = itemView.findViewById<TextView>(R.id.price)

                stars.visibility = View.GONE
                comment_comment.ellipsize = null
                comment_comment.maxLines = Int.MAX_VALUE
                comment_comment.setText(seller.username)
                image.layoutParams.width = 130
                image.layoutParams.height = 130
                price.text = "Poseti"
                price.visibility = View.VISIBLE

                itemView.setOnClickListener{
                    var action = SellerProfileFragmentDirections.actionProfileSeller(seller.seller_id)
                    findNavController().navigate(action)
                }

                Image.setImageResource(image, seller.picture, -1)
                comment_username.text = seller.name + seller.surname

                itemView.layoutParams = itemLayoutParams
                searched_products.addView(itemView)
            }
        }
    }

    fun setSellersOnMap(seller_list:List<Seller>, mapView: MapView, context: Context){
//            val markerOverlay = ItemizedIconOverlay<OverlayItem>(context, ArrayList(), null)


        val markerOverlay = ItemizedIconOverlay<OverlayItem>(context, ArrayList(), object : ItemizedIconOverlay.OnItemGestureListener<OverlayItem> {
            override fun onItemSingleTapUp(index: Int, item: OverlayItem?): Boolean {
                // Implementacija koja se poziva kada se jednom pritisne na OverlayItem
                if (item != null) {
                    println(""+item.title)

                    val dialog = Dialog(requireContext())

                    dialog.requestWindowFeature(Window.FEATURE_NO_TITLE)
                    dialog.setCancelable(true)
                    dialog.setContentView(R.layout.overlay_seller_pin_info)
                        getSellerInfo(Integer.parseInt(item.title), dialog)
                    val close = dialog.findViewById<Button>(R.id.button)
                    close.setOnClickListener{dialog.dismiss()}

                    val width = (resources.displayMetrics.widthPixels * 1.0).toInt()
                    dialog.window?.setLayout(width, ViewGroup.LayoutParams.WRAP_CONTENT)

                    dialog.show()
                };
                return true
            }

            override fun onItemLongPress(index: Int, item: OverlayItem?): Boolean {
                // Implementacija koja se poziva kada se dugo pritisne na OverlayItem
                return true
            }
        })

        for((index, element) in seller_list.withIndex()){
            val overlayItem = OverlayItem(element.seller_id.toString(), "user: " + element.username, GeoPoint(element.latitude, element.longitude))
            if(element.picture == null)
                overlayItem.setMarker(context.resources.getDrawable(R.drawable.location_pin))
            else{
                val img_from_base = Image.base64ToDrawable(element.picture);
                val bitmap = (img_from_base as BitmapDrawable).bitmap
                val roundedBitmap = Image.getResizedRoundedBitmap(bitmap, 100, 100)
                val roundedDrawable = BitmapDrawable(context.resources, roundedBitmap)
                overlayItem.setMarker(roundedDrawable)
            }

            markerOverlay.addItem(overlayItem)
        }

        mapView.overlays.add(markerOverlay)


    }

    fun renderSellerInfoInDialog(seller_info: Seller, dialog: Dialog){
        var person_image    = dialog.findViewById<CircleImageView>(R.id.person_image)
        var title           = dialog.findViewById<TextView>(R.id.title)
        var username        = dialog.findViewById<TextView>(R.id.person_username)
        var person_location = dialog.findViewById<TextView>(R.id.person_location)
        var ocena           = dialog.findViewById<TextView>(R.id.ocena)
        var pratioci        = dialog.findViewById<TextView>(R.id.pratioci)
        var objave          = dialog.findViewById<TextView>(R.id.objave)
        var visit_household = dialog.findViewById<Button>(R.id.visit_household)

        apiClient.getAddressFromCoordinates(requireContext(), seller_info.latitude, seller_info.longitude,
            {resposne -> person_location.text = resposne },
            { })
        title.text = "Domaćinstvo " + seller_info.surname
        username.text = "@ " + seller_info.username
        if(seller_info.avgGrade >= 0)
            ocena.text = String.format("%.1f", seller_info.avgGrade)
        else
            ocena.text = "-"
        pratioci.text = seller_info.numberOfFollowers.toString()
        objave.text = seller_info.numberOfPosts.toString()
        Image.setImageResource(person_image, seller_info.picture, -1)

        visit_household.setOnClickListener{
            val action = SellerProfileFragmentDirections.actionProfileSeller(seller_info.seller_id)
            findNavController().navigate(action)
            dialog.dismiss()
        }

    }

    @SuppressLint("MissingInflatedId")
    private fun renderJobOffers(job_offers: List<JobOffer>) {
        println("Usao sam ovde")
        val row = LinearLayout(requireContext())
        row.orientation = LinearLayout.VERTICAL
        val layoutParams = LinearLayout.LayoutParams(
            LinearLayout.LayoutParams.MATCH_PARENT,
            LinearLayout.LayoutParams.MATCH_PARENT
        )
        row.layoutParams = layoutParams
        poslovi_ponude.addView(row)

        val marginInDp = 20 // Promenjeno na 20dp za marginu između komponenti
        val marginInPx = (marginInDp * resources.displayMetrics.density).toInt()
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

        itemLayoutParams.setMargins(0, 0, 0, 20)
        if(job_offers.size!=0){
            poslovi_ponude.removeAllViews()
            poslovi_ponude.addView(row)
            for(job in job_offers){
                if(job.sentOffer == false){
                    val itemView            = layoutInflater.inflate(R.layout.component_ponuda_za_posao, null) as ConstraintLayout
                    val buyerImage          = itemView.findViewById<CircleImageView>(R.id.buyer_image)
                    val sellerAdress        = itemView.findViewById<TextView>(R.id.od_location)
                    val buyerAddress        = itemView.findViewById<TextView>(R.id.do_location)
                    val detaljnije          = itemView.findViewById<Button>(R.id.details)

                    Image.setImageResource(buyerImage, job.buyerImage,-1)

                    apiClient.getAddressFromCoordinates(requireContext(),job.sellerLat, job.sellerLong,
                        {response-> sellerAdress.text = response}, {  })

                    buyerAddress.text = job.buyerAddress


                    detaljnije.setOnClickListener{
                        val action = OrderDetailsFragmentDirections.actionOrderDetailsFragmentt(job.orderId, -1)
                        findNavController().navigate(action)
                    }

                    itemView.layoutParams = itemLayoutParams
                    row.addView(itemView)
                }
            }
        }
    }
}

