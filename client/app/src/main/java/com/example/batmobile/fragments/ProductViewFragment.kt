package com.example.batmobile.fragments

import android.app.Dialog
import android.os.Bundle
import android.text.Editable
import android.text.TextWatcher
import androidx.fragment.app.Fragment
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import android.view.Window
import android.widget.Button
import android.widget.EditText
import android.widget.HorizontalScrollView
import android.widget.ImageView
import android.widget.LinearLayout
import android.widget.ScrollView
import android.widget.TextView
import android.widget.Toast
import androidx.cardview.widget.CardView
import androidx.constraintlayout.widget.ConstraintLayout
import androidx.core.content.ContextCompat
import androidx.navigation.fragment.findNavController
import com.example.batmobile.DTOFromServer.Product
import com.example.batmobile.DTOFromServer.ProductComment
import com.example.batmobile.DTOFromServer.ProductViewResponse
import com.example.batmobile.DTOFromServer.Seller
import com.example.batmobile.R
import com.example.batmobile.SellerNavigationDirections
import com.example.batmobile.SellerProfileFragmentDirections
import com.example.batmobile.enums.Role
import com.example.batmobile.fragments.seller.SellerAddProductFragmentDirections
import com.example.batmobile.network.ApiClient
import com.example.batmobile.network.Config
import com.example.batmobile.services.Cart
import com.example.batmobile.services.Comments
import com.example.batmobile.services.Image
import com.example.batmobile.services.JWTService
import com.example.batmobile.services.Map
import com.google.gson.Gson
import com.google.gson.reflect.TypeToken
import com.makeramen.roundedimageview.RoundedImageView
import org.json.JSONObject
import org.osmdroid.views.MapView

class ProductViewFragment : Fragment() {
    private lateinit var view               : View
    private lateinit var horizontal_comments: HorizontalScrollView
    private lateinit var no_comment         : TextView
    private lateinit var back_button        : ImageView
    private lateinit var apiClient          : ApiClient
    private          var product_id         : Int = -1

    private lateinit var edit               : ImageView

    private lateinit var header_title       : TextView
    private lateinit var product_image      : RoundedImageView
    private lateinit var header_stars       : TextView
    private lateinit var product_description: TextView
    private lateinit var product_unit       : TextView
    private lateinit var product_price      : TextView

    private lateinit var non_owner_available: TextView
    private lateinit var owner_available_container: LinearLayout
    private lateinit var owner_available_image: ImageView
    private lateinit var owner_available: TextView

    private lateinit var person_image       : ImageView
    private lateinit var person_household   : TextView
    private lateinit var person_username    : TextView
    private lateinit var person_location    : TextView
    private lateinit var visit_household    : Button

    private lateinit var more_comments      : TextView

    private lateinit var product_view_response:  ProductViewResponse
    private lateinit var seller_location    : String

    private lateinit var list_of_comments   : List<ProductComment>

    private var          isLogged           : Boolean = false
    private lateinit var buy_field          : ConstraintLayout
    private lateinit var reduce_quantity    : ImageView
    private          var quantity_num       : Int = 1
    private lateinit var quantity           : TextView
    private lateinit var increase_quantity  : ImageView
    private lateinit var buy_button_price   : TextView

    private lateinit var add_image: ImageView
    private lateinit var add_text: TextView

    private          var num_of_star: Int = -1


    fun getAllStuff(){
        apiClient = ApiClient(requireContext())
        horizontal_comments = view.findViewById<HorizontalScrollView>(R.id.horizontal_layout_comments)
        no_comment          = view.findViewById<TextView>(R.id.no_comment)
        back_button         = view.findViewById<ImageView>(R.id.back)

        edit                = view.findViewById<ImageView>(R.id.edit)

        header_title        = view.findViewById<TextView>(R.id.header_title)
        product_image       = view.findViewById<RoundedImageView>(R.id.roundedImageView)
        header_stars        = view.findViewById<TextView>(R.id.header_stars)
        product_description = view.findViewById<TextView>(R.id.product_description)
        product_unit        = view.findViewById<TextView>(R.id.product_unit)
        product_price       = view.findViewById<TextView>(R.id.product_price)

        non_owner_available = view.findViewById<TextView>(R.id.non_owner_available)
        owner_available_container = view.findViewById<LinearLayout>(R.id.owner_available_container)
        owner_available_image = view.findViewById<ImageView>(R.id.owner_available_image)
        owner_available = view.findViewById<TextView>(R.id.owner_available)

        person_image        = view.findViewById<ImageView>(R.id.person_image)
        person_household    = view.findViewById<TextView>(R.id.person_household)
        person_username     = view.findViewById<TextView>(R.id.person_username)
        person_location     = view.findViewById<TextView>(R.id.person_location)
        visit_household     = view.findViewById<Button>(R.id.visit_household)

        more_comments       = view.findViewById<TextView>(R.id.more_comments)

        buy_field           = view.findViewById<ConstraintLayout>(R.id.buy_field)

        add_image           = view.findViewById<ImageView>(R.id.add_image)
        add_text            = view.findViewById<TextView>(R.id.add_text)

    }

    fun getInformationOfProduct(product_id: Int){
        var url:String = Config.ip_address+":"+ Config.port + "/getProduct/"+product_id
        if(!JWTService.getToken().equals("null")){
            apiClient.sendGetRequestEmpty(JWTService.getToken(),url,
                {response->
                    val gson = Gson()
                    val sellersResponse = gson.fromJson(response, ProductViewResponse::class.java)
                    product_view_response = sellersResponse
                    renderProduct(sellersResponse.productDTO)
                    renderHousehold(sellersResponse.sellerDTO)
                    renderComments(sellersResponse.productCommentList)
                    if(isLogged)
                    {
                        if(sellersResponse.productDTO.available){
                            buy_button_price.setOnClickListener{
                                Cart.addToCart(sellersResponse.sellerDTO.seller_id, sellersResponse.sellerDTO.username, "" ,product_id, sellersResponse.productDTO.productName, sellersResponse.productDTO.measurement ,sellersResponse.productDTO.picture , sellersResponse.productDTO.category_id ,quantity_num, sellersResponse.productDTO.price.toInt() ,requireActivity())
                                val toast = Toast.makeText(requireContext(), "Uspesno dodat proizvod u korpu", Toast.LENGTH_LONG)
                                toast.show()
                            }
                        }
                        else{
                            val buy_button_price_container = view.findViewById<CardView>(R.id.buy_button_price_container)
                            buy_button_price_container.setBackgroundResource(R.drawable.full_fill_button_disabled)
                            buy_button_price.setOnClickListener{
                                Toast.makeText(requireContext(),"Proizvod trenutno nije dostupan", Toast.LENGTH_LONG).show()
                            }
                        }
                    }
                },
                {error->
                    println(error)
                })
        }
        else{
            apiClient.sendGetRequestEmpty(url,
                {response->
                    val gson = Gson()
                    val sellersResponse = gson.fromJson(response, ProductViewResponse::class.java)
                    product_view_response = sellersResponse
                    renderProduct(sellersResponse.productDTO)
                    renderHousehold(sellersResponse.sellerDTO)
                    renderComments(sellersResponse.productCommentList)
                    if(isLogged)
                    {
                        buy_button_price.setOnClickListener{
                            Cart.addToCart(sellersResponse.sellerDTO.seller_id, sellersResponse.sellerDTO.username, "" ,product_id, sellersResponse.productDTO.productName, sellersResponse.productDTO.measurement ,sellersResponse.productDTO.picture , sellersResponse.productDTO.category_id ,quantity_num, sellersResponse.productDTO.price.toInt() ,requireActivity())
                            val toast = Toast.makeText(requireContext(), "Uspesno dodat proizvod u korpu", Toast.LENGTH_LONG)
                            toast.show()
                        }
                    }
                },
                {error->
                    println(error)
                })
        }
    }

    fun sendComment(text: String, grade: Int, dialog: Dialog){
        var url:String = Config.ip_address+":"+ Config.port+"/productComment"
        var jsonObject: JSONObject = JSONObject()

        jsonObject.put("productId", product_id)
        jsonObject.put("text", text)
        jsonObject.put("grade", grade)

        apiClient.sendPostRequestWithJSONObjectWithStringResponse(url, JWTService.getToken(), jsonObject,
            {response->
                var json_response = JSONObject(response)
                if(json_response["code"] == 200){
                    val toast = Toast.makeText(requireContext(), "Uspešno dodata ocena proizvoda", Toast.LENGTH_LONG)
                    toast.show()
                    dialog.dismiss()
                }
            },
            {error->
                println(error)
            }
        )

    }

    fun setAvailability(productId: Int, dialog: Dialog, owner_available:TextView){
        var url: String = Config.ip_address+":"+Config.port+"/changeAvailability/"+productId
        apiClient.sendPostRequestWithToken(JWTService.getToken(),url,
            {response->
                var jsonObject = JSONObject(response)
                if(jsonObject["message"].equals("Product is not available!")){
                    owner_available.text = "Nedostupno"
                    owner_available.setTextColor(resources.getColor(R.color.red))
                }
                else{
                    owner_available.text = "Dostupno"
                    owner_available.setTextColor(resources.getColor(R.color.green))
                }
                dialog.dismiss()
            },
            {error->
                println(error)
            })
    }

    fun setAllEventListener(){
        back_button     .setOnClickListener{ findNavController().navigateUp() }
        person_location .setOnClickListener{ showOverlayDialogForLocation() }
        more_comments   .setOnClickListener{ showOverlayDialogForMoreComments()  }
        add_image.setOnClickListener{ openOverlayForComment() }
        add_text.setOnClickListener{openOverlayForComment() }
    }

    fun setVisibilityPerRole(){
        if(JWTService.getTokenIfExist(requireContext()) is Role){
            isLogged = true

            reduce_quantity     = view.findViewById<ImageView>(R.id.reduce_quantity)
            quantity            = view.findViewById<TextView>(R.id.quantity)
            increase_quantity   = view.findViewById<ImageView>(R.id.increase_quantity)
            buy_button_price    = view.findViewById<TextView>(R.id.buy_button_price)

            reduce_quantity.setOnClickListener{reduceQuantity()}
            quantity.text         = quantity_num.toString()
            increase_quantity.setOnClickListener{increaseQuantity()}

        }
    }

    override fun onCreateView(
        inflater: LayoutInflater, container: ViewGroup?,
        savedInstanceState: Bundle?
    ): View? {
        view = inflater.inflate(R.layout.fragment_product_view, container, false)

        val args: ProductViewFragmentArgs = ProductViewFragmentArgs.fromBundle(requireArguments())
        product_id = args.productId
        getAllStuff()
        setAllEventListener()
        setVisibilityPerRole()

        getInformationOfProduct(product_id)

        return view
    }

    fun renderProduct(product_information:Product){
        header_title            .setText(product_information.productName)
        if(product_information.averageGrade != null)
            header_stars            .setText("%.2f".format(product_information.averageGrade))
        else
            header_stars.setText("Neocenjeno")
        product_description     .setText(product_information.description)
        product_unit            .setText("Cena po "+product_information.measurement)
        product_price           .setText(product_information.price.toString() +" rsd.")
        if(isLogged)
            buy_button_price.text = "Kupi: " + product_information.price.toString() + " rsd."
        Image.setImageResource(product_image, product_information.picture, product_information.category_id)
        if(product_information.owner == false && isLogged){
            add_text.visibility = View.VISIBLE
            add_image.visibility = View.VISIBLE
            buy_field.visibility = View.VISIBLE
        }

        if(product_information.owner){
            if(product_information.available){
                owner_available.text = "Dostupno"
                owner_available.setTextColor(resources.getColor(R.color.green))
            }
            else{
                owner_available.text = "Nedostupno"
                owner_available.setTextColor(resources.getColor(R.color.red))
            }
            owner_available_image.setOnClickListener{overlayForChangingAvailability(product_information.id, owner_available)}
            owner_available.setOnClickListener { overlayForChangingAvailability(product_information.id, owner_available) }
            owner_available_container.visibility = View.VISIBLE
            edit.visibility = View.VISIBLE
            edit.setOnClickListener { reditectToChange() }
        }
        else{
            if(product_information.available){
                non_owner_available.text = "Dostupno"
                non_owner_available.setTextColor(resources.getColor(R.color.green))
            }
            else{
                non_owner_available.text = "Nedostupno"
                non_owner_available.setTextColor(resources.getColor(R.color.red))
            }
            non_owner_available.visibility = View.VISIBLE
        }

    }

    fun reditectToChange(){
        val action = SellerAddProductFragmentDirections.actionAddProductSeller(product_id)
        findNavController().navigate(action)
    }

    fun overlayForChangingAvailability(productId: Int, owner_available: TextView){
        val dialog = Dialog(requireContext())
        dialog.requestWindowFeature(Window.FEATURE_NO_TITLE)
        dialog.setCancelable(true)
        dialog.setContentView(R.layout.overlay_alert)

        val width = (resources.displayMetrics.widthPixels * 1.0).toInt()
        dialog.window?.setLayout(width, ViewGroup.LayoutParams.WRAP_CONTENT)

            val button_yes = dialog.findViewById<Button>(R.id.button_yes)
            val button_no = dialog.findViewById<Button>(R.id.button_no)

            button_yes.setOnClickListener { setAvailability(productId, dialog, owner_available) }
            button_no.setOnClickListener { dialog.dismiss() }

        dialog.show()
    }

    fun increaseQuantity(){
        quantity_num += 1
        quantity.text         = quantity_num.toString()
        val price = product_view_response.productDTO.price * quantity_num
        buy_button_price.text = "Kupi: " + price.toString() + " rsd."
    }

    fun reduceQuantity(){
        if(quantity_num > 1){
            quantity_num -= 1
            quantity.text         = quantity_num.toString()
            val price = product_view_response.productDTO.price * quantity_num
            buy_button_price.text = "Kupi: " + price.toString() + " rsd."
        }
    }

    fun renderHousehold(seller_information: Seller){
//        person_image
        if(seller_information.picture != null)
            Image.setImageResource(person_image, seller_information.picture, -1)
        else
            Image.setImageResource(person_image, null, -1)
        person_household.setText("Domaćinstvo "+seller_information.surname)
        person_username.setText("@ "+seller_information.username)
        apiClient.getAddressFromCoordinates(requireContext(),seller_information.latitude, seller_information.longitude,
            {response-> seller_location = response ; person_location.text = response }, {  })
        visit_household.setOnClickListener{
            var action = SellerProfileFragmentDirections.actionProfileSeller(seller_information.seller_id)
            findNavController().navigate(action)
        }
    }

    fun renderComments(product_comments: List<ProductComment>){
        var row: LinearLayout
        row = LinearLayout(requireContext())
        row.orientation = LinearLayout.HORIZONTAL
        val layoutParams = LinearLayout.LayoutParams(
            LinearLayout.LayoutParams.MATCH_PARENT,
            LinearLayout.LayoutParams.WRAP_CONTENT
        )
        row.layoutParams = layoutParams
        horizontal_comments.addView(row)

        if(product_comments.size == 0){
            no_comment.visibility       = View.VISIBLE
            more_comments.visibility    = View.GONE
        }
        else
            Comments.renderComments(product_comments, row,"HORIZONTAL",requireActivity(), requireContext())
    }

    private fun showOverlayDialogForLocation() {
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

        val person_image                        = dialog.findViewById<ImageView>(R.id.person_image)
        Image.setImageResource(person_image, product_view_response.sellerDTO.picture, -1)

        overlay_closeButton.setOnClickListener { dialog.dismiss() }

        overlay_person_household.text = "Domaćinstvo "  +   product_view_response.sellerDTO.surname
        overlay_person_username.text  = "@ "            +   product_view_response.sellerDTO.username
        overlay_person_location.text  = seller_location
        Map.setMap(overlay_map, product_view_response.sellerDTO.latitude, product_view_response.sellerDTO.longitude, requireActivity(), requireContext())
        dialog.show()
    }

    private fun setCommentsFilterInOverlayDialogForMoreComments(button_list: List<TextView>, clicked: TextView){
        for(element in button_list){
            element.setBackgroundResource(R.drawable.empty_button)
            element.setTextColor(ContextCompat.getColor(requireContext(), R.color.black))
        }
        clicked.setBackgroundResource(R.drawable.full_fill_button)
        clicked.setTextColor(ContextCompat.getColor(requireContext(), R.color.white))

        val resourceName = resources.getResourceEntryName(clicked.id)
        when(resourceName){
            "overlay_comments_button_all" -> {
                row_in_all_comments.removeAllViews()
                Comments.renderComments(list_of_comments, row_in_all_comments,"VERTICAL" ,requireActivity(), requireContext())
            }
            "overlay_comments_button_5"   -> {
                row_in_all_comments.removeAllViews()
                val filteredComments = list_of_comments.filter { it.grade == 5 }
                Comments.renderComments(filteredComments, row_in_all_comments,"VERTICAL" ,requireActivity(), requireContext())
            }
            "overlay_comments_button_4"   -> {
                row_in_all_comments.removeAllViews()
                val filteredComments = list_of_comments.filter { it.grade == 4 }
                Comments.renderComments(filteredComments, row_in_all_comments,"VERTICAL" ,requireActivity(), requireContext())
            }
            "overlay_comments_button_3"   -> {
                row_in_all_comments.removeAllViews()
                val filteredComments = list_of_comments.filter { it.grade == 3 }
                Comments.renderComments(filteredComments, row_in_all_comments,"VERTICAL" ,requireActivity(), requireContext())
            }
            "overlay_comments_button_2"   -> {
                row_in_all_comments.removeAllViews()
                val filteredComments = list_of_comments.filter { it.grade == 2 }
                Comments.renderComments(filteredComments, row_in_all_comments,"VERTICAL" ,requireActivity(), requireContext())
            }
            "overlay_comments_button_1"   -> {
                row_in_all_comments.removeAllViews()
                val filteredComments = list_of_comments.filter { it.grade == 1 }
                Comments.renderComments(filteredComments, row_in_all_comments,"VERTICAL" ,requireActivity(), requireContext())
            }

        }

    }

    private lateinit var row_in_all_comments: LinearLayout
    private fun showOverlayDialogForMoreComments() {

        val dialog = Dialog(requireContext())
        dialog.requestWindowFeature(Window.FEATURE_NO_TITLE)
        dialog.setCancelable(true)
        dialog.setContentView(R.layout.overlay_product_all_comments)

        val width = (resources.displayMetrics.widthPixels * 1.0).toInt()
        val height = (resources.displayMetrics.heightPixels * 0.8).toInt()
        dialog.window?.setLayout(width, height)

        val overlay_closeButton         : ImageView      = dialog.findViewById<ImageView>(R.id.overlay_comments_close)
        val overlay_comments_button_all : TextView       = dialog.findViewById<TextView>(R.id.overlay_comments_button_all)
        val overlay_comments_button_5   : TextView       = dialog.findViewById<TextView>(R.id.overlay_comments_button_5)
        val overlay_comments_button_4   : TextView       = dialog.findViewById<TextView>(R.id.overlay_comments_button_4)
        val overlay_comments_button_3   : TextView       = dialog.findViewById<TextView>(R.id.overlay_comments_button_3)
        val overlay_comments_button_2   : TextView       = dialog.findViewById<TextView>(R.id.overlay_comments_button_2)
        val overlay_comments_button_1   : TextView       = dialog.findViewById<TextView>(R.id.overlay_comments_button_1)

        val overlay_comment_list    = dialog.findViewById<ScrollView>(R.id.overlay_comment_list)
        var list_of_buttons_filter  = mutableListOf<TextView>(overlay_comments_button_all, overlay_comments_button_5, overlay_comments_button_4, overlay_comments_button_3, overlay_comments_button_2, overlay_comments_button_1 )

        for(element in list_of_buttons_filter){
            element.setOnClickListener{ setCommentsFilterInOverlayDialogForMoreComments(list_of_buttons_filter, element) }
        }
        overlay_closeButton.setOnClickListener { dialog.dismiss() }



        row_in_all_comments = LinearLayout(requireContext())
        row_in_all_comments.orientation = LinearLayout.VERTICAL
        val layoutParams = LinearLayout.LayoutParams(
            LinearLayout.LayoutParams.MATCH_PARENT,
            LinearLayout.LayoutParams.MATCH_PARENT
        )
        row_in_all_comments.layoutParams = layoutParams
        overlay_comment_list.addView(row_in_all_comments)


        var url:String = Config.ip_address+":"+ Config.port + "/getComments/"+product_id
        apiClient.sendGetRequestEmpty(url,
            {response->
                val gson = Gson()
                val listType = object : TypeToken<List<ProductComment>>() {}.type
                list_of_comments = gson.fromJson(response, listType)

                Comments.renderComments(list_of_comments, row_in_all_comments,"VERTICAL" ,requireActivity(), requireContext())
            },
            {error->
                println(error)
            })

        dialog.show()
    }

    private fun openOverlayForComment(){
        val dialog = Dialog(requireContext())
        dialog.requestWindowFeature(Window.FEATURE_NO_TITLE)
        dialog.setCancelable(true)
        dialog.setContentView(R.layout.overlay_add_comment_on_product)

        val width = (resources.displayMetrics.widthPixels * 1.0).toInt()
        dialog.window?.setLayout(width, ViewGroup.LayoutParams.WRAP_CONTENT)

        var close = dialog.findViewById<ImageView>(R.id.close)
        close.setOnClickListener{dialog.dismiss()}

        var star_1 = dialog.findViewById<ImageView>(R.id.star_1)
        var star_2 = dialog.findViewById<ImageView>(R.id.star_2)
        var star_3 = dialog.findViewById<ImageView>(R.id.star_3)
        var star_4 = dialog.findViewById<ImageView>(R.id.star_4)
        var star_5 = dialog.findViewById<ImageView>(R.id.star_5)

        var star_list = listOf<ImageView>(star_1,star_2,star_3,star_4,star_5)

        var add_comment = dialog.findViewById<Button>(R.id.add_comment)
        var editText = dialog.findViewById<EditText>(R.id.editText)

        for(i in 0..star_list.size-1){
            star_list[i].setOnClickListener{setStar(star_list, i); validateAddText(editText.text.toString(),add_comment)}
        }

        editText.addTextChangedListener(object : TextWatcher {
            override fun beforeTextChanged(s: CharSequence?, start: Int, count: Int, after: Int) {}
            override fun onTextChanged(s: CharSequence?, start: Int, before: Int, count: Int) {}
            override fun afterTextChanged(s: Editable?) {
                validateAddText(s.toString(), add_comment)
            }
        })

        add_comment.setOnClickListener{
            sendComment(editText.text.toString(), num_of_star, dialog)
        }

        dialog.show()
    }

    private fun setStar(star_list:List<ImageView>, clicked: Int){
        num_of_star = clicked + 1
        for (i in 0..clicked){
            star_list[i].setImageResource(R.drawable.star_filled)
        }
        for (i in clicked+1..star_list.size-1){
            star_list[i].setImageResource(R.drawable.star)
        }
    }

    private fun validateAddText(text: String, add_comment_button: Button){
        if(num_of_star > 0 && text.length > 0)
        {
            add_comment_button.isEnabled = true
            add_comment_button.setBackgroundResource(R.drawable.full_fill_button)
        }
        else{
            add_comment_button.isEnabled = false
            add_comment_button.setBackgroundResource(R.drawable.full_fill_button_disabled)
        }
    }

}