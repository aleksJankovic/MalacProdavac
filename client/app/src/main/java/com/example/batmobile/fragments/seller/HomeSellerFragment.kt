package com.example.batmobile.fragments.seller

import android.app.Dialog
import android.graphics.Color
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
import android.widget.ScrollView
import android.widget.TextView
import androidx.constraintlayout.widget.ConstraintLayout
import androidx.core.content.ContextCompat
import androidx.core.widget.NestedScrollView
import androidx.fragment.app.Fragment
import androidx.fragment.app.FragmentContainerView
import androidx.navigation.fragment.findNavController
import com.etebarian.meowbottomnavigation.MeowBottomNavigation
import com.example.batmobile.DTOFromServer.GraphPerWeek
import com.example.batmobile.DTOFromServer.NewNews
import com.example.batmobile.DTOFromServer.PostCommentsDTO
import com.example.batmobile.DTOFromServer.PostDetails
import com.example.batmobile.DTOFromServer.ResponseListTemplate
import com.example.batmobile.DTOFromServer.ResponseObjectTemplate
import com.example.batmobile.DTOFromServer.Search
import com.example.batmobile.R
import com.example.batmobile.SellerProfileFragmentDirections
import com.example.batmobile.fragments.CartFragmentDirections
import com.example.batmobile.fragments.ProductViewFragmentDirections
import com.example.batmobile.fragments.ProductsCategoryFragmentDirections
import com.example.batmobile.models.Category
import com.example.batmobile.network.ApiClient
import com.example.batmobile.network.Config
import com.example.batmobile.services.Charts
import com.example.batmobile.services.Comments
import com.example.batmobile.services.Image
import com.example.batmobile.services.JWTService
import com.github.mikephil.charting.charts.BarChart
import com.github.mikephil.charting.charts.LineChart
import com.github.mikephil.charting.components.Description
import com.github.mikephil.charting.components.Legend
import com.github.mikephil.charting.components.XAxis
import com.github.mikephil.charting.components.YAxis
import com.github.mikephil.charting.data.BarData
import com.github.mikephil.charting.data.BarDataSet
import com.github.mikephil.charting.data.BarEntry
import com.github.mikephil.charting.data.Entry
import com.github.mikephil.charting.data.LineData
import com.github.mikephil.charting.data.LineDataSet
import com.github.mikephil.charting.utils.ColorTemplate
import com.google.gson.Gson
import com.google.gson.reflect.TypeToken
import kotlinx.coroutines.CoroutineScope
import kotlinx.coroutines.Dispatchers
import kotlinx.coroutines.async
import kotlinx.coroutines.launch
import org.json.JSONObject

class HomeSellerFragment : Fragment() {

    private lateinit var view: View
    private lateinit var apiClient: ApiClient

    private lateinit var title:        TextView
    private lateinit var cart:                          ImageView
    private lateinit var editTextText: EditText
    private lateinit var search:                        NestedScrollView
    private lateinit var searched_products:             LinearLayout
    private lateinit var not_found_msg:                 TextView
    private lateinit var title_products:                TextView
    private lateinit var title_sellers:                 TextView
    private          var searched_type_products: Boolean = true

    private lateinit var constraint_graph: ConstraintLayout
    private lateinit var container_for_graph: ConstraintLayout
    private lateinit var line_chart: LineChart

    private lateinit var products_category_container: ConstraintLayout
    private lateinit var category_products_list: LinearLayout
    private lateinit var news_container: ConstraintLayout
    private lateinit var list_of_news: HorizontalScrollView

    private lateinit var meowBottomNavigation: MeowBottomNavigation

    fun getAllStuff(){
        apiClient = ApiClient(requireContext())

        title                       = view.findViewById<TextView>(R.id.textView)
        cart                        = view.findViewById<ImageView>(R.id.cart)
        editTextText                = view.findViewById<EditText>(R.id.editTextText)
        search                      = view.findViewById<NestedScrollView>(R.id.search)
        searched_products           = view.findViewById<LinearLayout>(R.id.searched_products)
        not_found_msg               = view.findViewById<TextView>(R.id.not_found_msg)
        title_products              = view.findViewById<TextView>(R.id.title_products)
        title_sellers               = view.findViewById<TextView>(R.id.title_sellers)

        constraint_graph            = view.findViewById<ConstraintLayout>(R.id.constraint_graph)
        container_for_graph         = view.findViewById<ConstraintLayout>(R.id.container_for_graph)
        line_chart                  = view.findViewById<LineChart>(R.id.line_chart)

        products_category_container = view.findViewById<ConstraintLayout>(R.id.products_category_container)
        category_products_list      =   view.findViewById<LinearLayout>(R.id.products_category)

        news_container              = view.findViewById<ConstraintLayout>(R.id.news_container)
        list_of_news                =   view.findViewById<HorizontalScrollView>(R.id.list_of_news)

        meowBottomNavigation            = requireActivity().findViewById<MeowBottomNavigation>(R.id.bottomNavigation)
        meowBottomNavigation.visibility = View.VISIBLE
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
                    constraint_graph.visibility = View.GONE
                    products_category_container.visibility = View.GONE
                    news_container.visibility = View.GONE
                }
                else{
                    search.visibility = View.GONE
                    constraint_graph.visibility = View.VISIBLE
                    products_category_container.visibility = View.VISIBLE
                    news_container.visibility = View.VISIBLE
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
        var url: String = Config.ip_address+":"+ Config.port+"/search/"+query
        apiClient.sendGetRequestEmpty(url,
            {response->
                var gson = Gson()
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

    suspend fun getInformationForGraph(){
        var url:String = Config.ip_address+":"+ Config.port + "/seller/graphic/data"
        apiClient.sendGetRequestEmpty(JWTService.getToken(), url,
            {response->
                var gson = Gson()
                val typeToken = object : TypeToken<ResponseObjectTemplate<GraphPerWeek>>() {}.type
                var postDetails = gson.fromJson<ResponseObjectTemplate<GraphPerWeek>>(response, typeToken)
                renderGraph(postDetails.data)
            },
            {error->
                println(error)
            })
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
        view =  inflater.inflate(R.layout.fragment_home_seller, container, false)

        getAllStuff()
        setAllEventListener()

        val scope = CoroutineScope(Dispatchers.Default)
        scope.launch {
            async { getCategoryProducts();      }
            async { getInformationForGraph();   }
            async { getLastNews(); }
        }
        return view
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

    fun renderGraph(information: GraphPerWeek){

        val entries = arrayListOf<Entry>()

        for ((index, element) in information.currentWeek.withIndex()){
            if(element < 0) break;

            entries.add(Entry(index.toFloat(), element.toFloat()))
        }

        val entries2 = arrayListOf<Entry>()

        for ((index, element) in information.previousWeek.withIndex()){
            entries2.add(Entry(index.toFloat(), element.toFloat()))
        }

        val dataSet1 = LineDataSet(entries, "Trenutna nedelja")
        dataSet1.setColor(resources.getColor(R.color.orange))
        dataSet1.lineWidth = 4f
        dataSet1.mode = LineDataSet.Mode.LINEAR

        val dataSet2 = LineDataSet(entries2, "Prošla nedelja")
        dataSet2.setColor(resources.getColor(R.color.light_blue))
        dataSet2.lineWidth = 2f
        dataSet2.mode = LineDataSet.Mode.LINEAR
        dataSet2.enableDashedLine(30f, 15f, 0f)

        val data = LineData(dataSet1, dataSet2)
        line_chart.data = data

        // Prilagodite X osu
        val xAxis: XAxis = line_chart.xAxis
        xAxis.position = XAxis.XAxisPosition.BOTTOM
        xAxis.setDrawGridLines(true)
        xAxis.valueFormatter = Charts.Companion.DayAxisValueFormatter()
        xAxis.textSize = 12f

        // Prilagodite Y osu
        val yAxisLeft: YAxis = line_chart.axisLeft
        yAxisLeft.setDrawGridLines(true)
        yAxisLeft.textSize = 12f
        yAxisLeft.setGranularity(1f)
        yAxisLeft.axisMinimum = 0f

        line_chart.axisRight.isEnabled = false

        val legend: Legend = line_chart.legend
        legend.textSize = 12f           // Povećajte veličinu teksta legende
        legend.horizontalAlignment = Legend.LegendHorizontalAlignment.CENTER
        legend.verticalAlignment = Legend.LegendVerticalAlignment.TOP
        legend.orientation = Legend.LegendOrientation.HORIZONTAL
        legend.setDrawInside(false)     // Da bi se omogućilo da se prikazuje iznad grafikona
        legend.xEntrySpace = 30f        // Prilagodite razmak između stavki legende
//        legend.xOffset = 10f
        legend.yOffset = 15f

        // Prilagodite naslov
        val description = Description()
        description.text = ""
        line_chart.description = description

        // Osvežite grafik
        line_chart.invalidate()

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
        category_products_list.addView(row)
        for ((index, category) in categoryList.withIndex()) {
            if(index % 4 == 0 && index > 0){
                row = LinearLayout(requireContext())
                row.orientation = LinearLayout.HORIZONTAL
                val layoutParams = LinearLayout.LayoutParams(
                    LinearLayout.LayoutParams.MATCH_PARENT,
                    LinearLayout.LayoutParams.WRAP_CONTENT
                )
                row.layoutParams = layoutParams
                category_products_list.addView(row)
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
                    Image.setImageResource(image, comment.picture, -1)
                    comment_username.text = comment.username

                    itemView.layoutParams = itemLayoutParams
                    row.addView(itemView)
                }
            }

        }
    }

}