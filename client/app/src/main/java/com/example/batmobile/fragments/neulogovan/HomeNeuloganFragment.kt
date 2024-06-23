package com.example.batmobile.fragments.neulogovan

import android.annotation.SuppressLint
import android.app.Dialog
import android.content.Intent
import android.os.Bundle
import android.text.Editable
import android.text.Spannable
import android.text.SpannableString
import android.text.TextWatcher
import android.text.style.ForegroundColorSpan
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
import androidx.constraintlayout.widget.ConstraintLayout
import androidx.core.content.ContextCompat
import androidx.core.widget.NestedScrollView
import androidx.fragment.app.Fragment
import androidx.navigation.fragment.findNavController
import com.etebarian.meowbottomnavigation.MeowBottomNavigation
import com.example.batmobile.DTOFromServer.NewNews
import com.example.batmobile.DTOFromServer.PostCommentsDTO
import com.example.batmobile.DTOFromServer.PostDetails
import com.example.batmobile.DTOFromServer.ResponseListTemplate
import com.example.batmobile.DTOFromServer.ResponseObjectTemplate
import com.example.batmobile.DTOFromServer.Search
import com.example.batmobile.R
import com.example.batmobile.SellerProfileFragmentDirections
import com.example.batmobile.activities.LoginActivity
import com.example.batmobile.enums.Role
import com.example.batmobile.fragments.CartFragmentDirections
import com.example.batmobile.fragments.DeliveryOfferFragmentDirections
import com.example.batmobile.fragments.ProductViewFragmentDirections
import com.example.batmobile.fragments.ProductsCategoryFragmentDirections
import com.example.batmobile.models.Category
import com.example.batmobile.models.Seller
import com.example.batmobile.models.TopProduct
import com.example.batmobile.network.ApiClient
import com.example.batmobile.network.Config
import com.example.batmobile.services.Comments
import com.example.batmobile.services.Image
import com.example.batmobile.services.JWTService
import com.google.gson.Gson
import com.google.gson.reflect.TypeToken
import com.makeramen.roundedimageview.RoundedImageView
import kotlinx.coroutines.CoroutineScope
import kotlinx.coroutines.Dispatchers
import kotlinx.coroutines.async
import kotlinx.coroutines.launch
import org.json.JSONObject

class HomeNeuloganFragment : Fragment() {

    private lateinit var view: View
    private lateinit var apiClient: ApiClient
    private          var isLogged: Boolean = false
    private lateinit var gson: Gson

    private lateinit var title:                         TextView
    private lateinit var container_category_products:   LinearLayout
    private lateinit var horizontal_top_seller:         HorizontalScrollView
    private lateinit var horizontal_top_products:       HorizontalScrollView

    private lateinit var register_card:                 LinearLayout
    private lateinit var cart:                          ImageView
    private lateinit var offer:                         ImageView
    private lateinit var news_container:                ConstraintLayout
    private lateinit var list_of_news:                  HorizontalScrollView

    private lateinit var editTextText:                  EditText
    private lateinit var search:                        NestedScrollView
    private lateinit var searched_products:             LinearLayout
    private lateinit var not_found_msg:                 TextView
    private lateinit var title_products:                TextView
    private lateinit var title_sellers:                 TextView
    private          var searched_type_products: Boolean = true

    private lateinit var meowBottomNavigation: MeowBottomNavigation

    @SuppressLint("ResourceAsColor")
    fun setColorForRegistrujSe() {
        val text = "Mala proizvodnja velika zajednica. Postani i ti deo iste - Registruj se"
        val spannableString = SpannableString(text)
        val startIndex = text.indexOf("Registruj se")
        val endIndex = startIndex + "Registruj se".length

        val orangeColor = ContextCompat.getColor(requireContext(), R.color.orange)

        spannableString.setSpan(ForegroundColorSpan(orangeColor), startIndex, endIndex, Spannable.SPAN_EXCLUSIVE_EXCLUSIVE)

        val textView = view.findViewById<TextView>(R.id.ponuda_za_registraciju)
        textView.text = spannableString
    }

    fun getAllStuff(){
        apiClient = ApiClient(requireContext())
        gson = Gson()

        title                       = view.findViewById<TextView>(R.id.textView)
        editTextText                = view.findViewById<EditText>(R.id.editTextText)
        search                      = view.findViewById<NestedScrollView>(R.id.search)
        searched_products           = view.findViewById<LinearLayout>(R.id.searched_products)
        not_found_msg               = view.findViewById<TextView>(R.id.not_found_msg)
        title_products              = view.findViewById<TextView>(R.id.title_products)
        title_sellers               = view.findViewById<TextView>(R.id.title_sellers)

        container_category_products =   view.findViewById<LinearLayout>(R.id.products_category)
        horizontal_top_seller       =   view.findViewById<HorizontalScrollView>(R.id.top_sellers)
        horizontal_top_products     =   view.findViewById<HorizontalScrollView>(R.id.top_products)

        register_card               =   view.findViewById<LinearLayout>(R.id.linearLayout6)
        cart                        =   view.findViewById<ImageView>(R.id.cart)
        offer                       =   view.findViewById<ImageView>(R.id.offer)
        news_container              =   view.findViewById<ConstraintLayout>(R.id.news_container)
        list_of_news                =   view.findViewById<HorizontalScrollView>(R.id.list_of_news)

        meowBottomNavigation            = requireActivity().findViewById<MeowBottomNavigation>(R.id.bottomNavigation)
        meowBottomNavigation.visibility = View.VISIBLE
    }

    fun setAllEventListener(){
        register_card.setOnClickListener{
            var intent: Intent? = null
            intent = Intent(context, LoginActivity::class.java)
            startActivity(intent)
            requireActivity().finish()
        }
        cart.setOnClickListener{
            val action = CartFragmentDirections.actionCartFragment()
            findNavController().navigate(action)
        }

        offer.setOnClickListener {
            val action = DeliveryOfferFragmentDirections.actionDeliveryOfferFragment()
            findNavController().navigate(action)
        }

        title.setOnClickListener{
            editTextText.setText("")
            searched_products.removeAllViews()

            searched_type_products = true
            title_sellers.setTextColor(ContextCompat.getColor(requireContext(), R.color.black))
            title_products.setTextColor(ContextCompat.getColor(requireContext(), R.color.orange))

            search.visibility = View.GONE
            horizontal_top_products.visibility  = View.VISIBLE
            horizontal_top_seller.visibility    = View.VISIBLE
        }

        editTextText.addTextChangedListener (object:TextWatcher{
            override fun beforeTextChanged(s: CharSequence?, start: Int, count: Int, after: Int) {
            }

            override fun onTextChanged(s: CharSequence?, start: Int, before: Int, count: Int) {
            }

            override fun afterTextChanged(s: Editable?) {
                if(s.toString().length > 0){
                    not_found_msg.visibility            = View.VISIBLE
                    searching(s.toString())
                    search.visibility                   = View.VISIBLE
                    horizontal_top_products.visibility  = View.GONE
                    horizontal_top_seller.visibility    = View.GONE
                }
                else{
                    search.visibility = View.GONE
                    horizontal_top_products.visibility  = View.VISIBLE
                    horizontal_top_seller.visibility    = View.VISIBLE
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

    suspend fun getCategoryProducts(){
        var url:String = Config.ip_address+":"+ Config.port + "/allCategories"
        apiClient.sendGetRequestEmpty(url,
            { response ->
                var gson = Gson()
                var categoryList = gson.fromJson(response, Array<Category>::class.java).toList()
                renderCategoryProducts(categoryList)
            },
            { error ->
                println(error)
            }
        )
    }

    suspend fun getTopSellers(){
        var url:String = Config.ip_address+":"+ Config.port + "/top3/sellers"
        apiClient.sendGetRequestEmpty(url,
            { response ->
                var gson = Gson()
                var sellerList = gson.fromJson(response, Array<Seller>::class.java).toList()
                renderHorizontalTopSellers(sellerList)
            },
            { error ->
                println(error)
            }
        )
    }
    suspend fun getTopProducts(){
        var url:String = Config.ip_address+":"+ Config.port + "/top3/products"
        apiClient.sendGetRequestEmpty(url,
            { response ->
                var gson = Gson()
                var productList = gson.fromJson(response, Array<TopProduct>::class.java).toList()
                renderHorizontalTopProducts(productList)
            },
            { error ->
                println(error)
            }
        )
    }

    suspend fun getLastNews(){
        var url:String = Config.ip_address+":"+ Config.port + "/lastNews"
        apiClient.sendGetRequestEmpty(JWTService.getToken(),url,
            { response ->
                var gson = Gson()
                val typeToken = object : TypeToken<ResponseListTemplate<NewNews>>() {}.type
                var new_news_list = gson.fromJson<ResponseListTemplate<NewNews>>(response, typeToken)
                renderLastNews(new_news_list.data)
            },
            { error ->
                println(error)
            }
        )
    }

    fun getPostDetails(id: Int, dialog: Dialog){
        var url:String = Config.ip_address+":"+ Config.port + "/postDetails"+"/"+id
        apiClient.sendGetRequestEmpty(JWTService.getToken(),url,
            { response ->
                var gson = Gson()
                val typeToken = object : TypeToken<ResponseObjectTemplate<PostDetails>>() {}.type
                var postDetails = gson.fromJson<ResponseObjectTemplate<PostDetails>>(response, typeToken)
                renderDialogForNewNews(postDetails.data,dialog, id)
            },
            { error ->
                println(error)
            }
        )
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
                if(render){
                    val scope = CoroutineScope(Dispatchers.Default)
                    scope.launch {
                        async { getLastNews() }
                    }
                }
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
                val scope = CoroutineScope(Dispatchers.Default)
                scope.launch {
                    async { getLastNews() }
                }
            },
            {error->

            }
        )
    }

    override fun onCreateView(
        inflater: LayoutInflater, container: ViewGroup?,
        savedInstanceState: Bundle?
    ): View? {

        view =  inflater.inflate(R.layout.fragment_home_neulogan, container, false)

        getAllStuff()
        setVisibilityPerRole()
        setColorForRegistrujSe()
        setAllEventListener()

        val scope = CoroutineScope(Dispatchers.Default)
        scope.launch {
            async { getCategoryProducts(); }
            async { getTopSellers(); }
            async { getTopProducts(); }
            (if (isLogged) async { getLastNews() } else null)
//            getCategoryProducts()
//            getTopSellers()
//            getTopProducts()
//            if (isLogged) getLastNews()
        }

        return view
    }

    fun setVisibilityPerRole(){
        if(JWTService.getTokenIfExist(requireContext()) is Role){
            isLogged = true
            register_card.visibility        = View.GONE
            news_container.visibility       = View.VISIBLE
            cart.visibility                 = View.VISIBLE
            offer.visibility                = View.VISIBLE
        }
        if(JWTService.getTokenIfExist(requireContext()) == Role.User){
            offer.visibility                = View.VISIBLE
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

    fun renderCategoryProducts(categoryList: List<Category>){
        var row: LinearLayout
        row = LinearLayout(requireContext())
        row.orientation = LinearLayout.HORIZONTAL
        val layoutParams = LinearLayout.LayoutParams(
            LinearLayout.LayoutParams.MATCH_PARENT,
            LinearLayout.LayoutParams.WRAP_CONTENT
        )
        row.layoutParams = layoutParams
        container_category_products.addView(row)
        for ((index, category) in categoryList.withIndex()) {
            if(index % 4 == 0 && index > 0){
                row = LinearLayout(requireContext())
                row.orientation = LinearLayout.HORIZONTAL
                val layoutParams = LinearLayout.LayoutParams(
                    LinearLayout.LayoutParams.MATCH_PARENT,
                    LinearLayout.LayoutParams.WRAP_CONTENT
                )
                row.layoutParams = layoutParams
                container_category_products.addView(row)
            }


            val itemView = layoutInflater.inflate(R.layout.component_item_category, null)
                var categoryImage = itemView.findViewById<ImageView>(R.id.categoryImage)
                val categoryText = itemView.findViewById<TextView>(R.id.categoryText)
                when(category.categoryId){
                    1 -> { categoryImage.setImageResource(R.drawable.mlecni_proizvodi) }
                    2 -> { categoryImage.setImageResource(R.drawable.voce_i_povrce) }
                    3 -> { categoryImage.setImageResource(R.drawable.mesne_preradjevine) }
                    4 -> { categoryImage.setImageResource(R.drawable.meso) }
                    5 -> { categoryImage.setImageResource(R.drawable.zitarice) }
                    6 -> { categoryImage.setImageResource(R.drawable.napici) }
                    7 -> { categoryImage.setImageResource(R.drawable.biljna_ulja) }
                    8 -> { categoryImage.setImageResource(R.drawable.namazi) }
                }
                categoryText.text = category.name

            itemView.setOnClickListener{
                val action = ProductsCategoryFragmentDirections.actionProductsCategoryFragment(category.categoryId)
                findNavController().navigate(action)
            }

            val marginInDp = 4
            val marginInPx = (marginInDp * resources.displayMetrics.density).toInt()

            val itemLayoutParams = LinearLayout.LayoutParams(
                0,
                LinearLayout.LayoutParams.MATCH_PARENT,
                1f
            )
            itemLayoutParams.setMargins(marginInPx, (marginInDp+14), marginInPx, 0)

            itemView.layoutParams = itemLayoutParams
            row.addView(itemView)
        }
    }

    fun renderHorizontalTopSellers(sellersList: List<Seller>){
        var row: LinearLayout
        row = LinearLayout(requireContext())
        row.orientation = LinearLayout.HORIZONTAL
        val layoutParams = LinearLayout.LayoutParams(
            LinearLayout.LayoutParams.MATCH_PARENT,
            LinearLayout.LayoutParams.WRAP_CONTENT
        )
        row.layoutParams = layoutParams

        val marginInDp = 4
        val marginInPx = (marginInDp * resources.displayMetrics.density).toInt()
        val itemLayoutParams = LinearLayout.LayoutParams(
            0,
            LinearLayout.LayoutParams.MATCH_PARENT,
            1f
        )

        for((index, seller) in sellersList.withIndex()){
            val itemView = layoutInflater.inflate(R.layout.component_top_seller, null)
                var sellerImage                 = itemView.findViewById<ImageView>(R.id.imageViewSeller)
                var sellerUsername              = itemView.findViewById<TextView>(R.id.seller_username)
                var sellerAddress               = itemView.findViewById<TextView>(R.id.seller_address)
                var sellerAvailableProducts     = itemView.findViewById<TextView>(R.id.available_products)
                var sellerFollowers             = itemView.findViewById<TextView>(R.id.followers)
                Image.setImageResource(sellerImage, seller.picture, -1)
                sellerUsername.text = seller.username
                apiClient.getAddressFromCoordinates(requireContext(),seller.latitude, seller.longitude,
                    {response-> sellerAddress.text = response.split(",")[0] }, {  })
                sellerAvailableProducts.text = seller.numberOdProducts.toString()
                sellerFollowers.text = seller.numberOfFollowers.toString()

                itemView.setOnClickListener{
                    var action = SellerProfileFragmentDirections.actionProfileSeller(seller.sellerId)
                    findNavController().navigate(action)
                }
            itemLayoutParams.setMargins(marginInPx, (marginInDp), marginInPx, 0)
            itemView.layoutParams = itemLayoutParams
            row.addView(itemView)
        }
        horizontal_top_seller.addView(row)
    }

    fun renderHorizontalTopProducts(productList: List<TopProduct>){
        var row: LinearLayout
        row = LinearLayout(requireContext())
        row.orientation = LinearLayout.HORIZONTAL
        val layoutParams = LinearLayout.LayoutParams(
            LinearLayout.LayoutParams.MATCH_PARENT,
            LinearLayout.LayoutParams.WRAP_CONTENT
        )
        row.layoutParams = layoutParams

        val marginInDp = 4
        val marginInPx = (marginInDp * resources.displayMetrics.density).toInt()
        val itemLayoutParams = LinearLayout.LayoutParams(
            0,
            LinearLayout.LayoutParams.MATCH_PARENT,
            1f
        )

        for((index, product) in productList.withIndex()){
            val itemView = layoutInflater.inflate(R.layout.component_top_product, null)
                val product_name    = itemView.findViewById<TextView>(R.id.product_name)
                val product_star    = itemView.findViewById<TextView>(R.id.product_star)
                val product_seller  = itemView.findViewById<TextView>(R.id.product_seller)
                val product_location = itemView.findViewById<TextView>(R.id.product_loaction)
                val image            = itemView.findViewById<RoundedImageView>(R.id.product_image)
                product_name.text   = product.productName
                if(product.averageGrade != null)
                    product_star.text   = "%.2f".format(product.averageGrade)
                else
                    product_star.text   = "Neocenjeno"
                product_seller.text = product.sellerUsername
                apiClient.getAddressFromCoordinates(requireContext(),product.latitude, product.longitude,
                {response->
                    val splited_response = response.split(",")
                    if(splited_response.isNotEmpty())
                        product_location.text = splited_response[0]
                }, {  })

                Image.setImageResource(image, product.productPicture, product.categoryId)

            itemLayoutParams.setMargins(marginInPx, (marginInDp), marginInPx, 0)
            itemView.layoutParams = itemLayoutParams
            itemView.setOnClickListener{
                val action = ProductViewFragmentDirections.actionProductViewFragment(product.productId)
                findNavController().navigate(action)
            }
            row.addView(itemView)
        }
        horizontal_top_products.addView(row)
    }

    fun renderLastNews(new_news_list: List<NewNews>){
        var row: LinearLayout
        row = LinearLayout(requireContext())
        row.orientation = LinearLayout.HORIZONTAL
        val layoutParams = LinearLayout.LayoutParams(
            LinearLayout.LayoutParams.MATCH_PARENT,
            LinearLayout.LayoutParams.WRAP_CONTENT
        )
        row.layoutParams = layoutParams

        val marginInDp = 4
        val marginInPx = (marginInDp * resources.displayMetrics.density).toInt()
        val itemLayoutParams = LinearLayout.LayoutParams(
            0,
            LinearLayout.LayoutParams.MATCH_PARENT,
            1f
        )
        itemLayoutParams.setMargins(marginInPx, (marginInDp), marginInPx, 0)

        if(new_news_list.size != 0){
            list_of_news.removeAllViews()
            list_of_news.addView(row)
            for(new_new in new_news_list){
                val itemView            = layoutInflater.inflate(R.layout.component_new_news, row, false)
                val seller_username     = itemView.findViewById<TextView>(R.id.seller_username)
                val time                = itemView.findViewById<TextView>(R.id.time)
                val new_news_text       = itemView.findViewById<TextView>(R.id.new_news_text)
                val num_of_likes        = itemView.findViewById<TextView>(R.id.num_of_likes)
                val num_of_comments     = itemView.findViewById<TextView>(R.id.num_of_comments)
                val like                = itemView.findViewById<ConstraintLayout>(R.id.like)
                val like_img            = itemView.findViewById<ImageView>(R.id.imageView7)

                seller_username.text    = "@" + new_new.usernameSeller
                try{
                    time.text               = new_new.dateTime.toString().split("T")[0]
                }
                catch (exception: Exception){}
                new_news_text.text      = new_new.text
                num_of_likes.text       = new_new.likesNumber.toString()
                num_of_comments.text    = new_new.commentsNumber.toString()

                if(new_new.likedPost)
                    like_img.setImageResource(R.drawable.liked)

                like.setOnClickListener { likePost(new_new.id, like_img, num_of_likes, false ) }

                itemView.layoutParams = itemLayoutParams
                itemView.setOnClickListener{showDialogForNewNews(new_new.id)}
                row.addView(itemView)
            }
        }
    }

    private fun showDialogForNewNews(id: Int){

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
    private fun renderDialogForNewNews(postDetails: PostDetails?, dialog: Dialog, id: Int){
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

            comment_on_post.addTextChangedListener(object:TextWatcher{
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
                        println("Slika komentara posta:" + comment.picture)
                        Image.setImageResource(image, comment.picture, -1)
                        comment_username.text = comment.username

                    itemView.layoutParams = itemLayoutParams
                    row.addView(itemView)
                }
            }

        }
    }

}