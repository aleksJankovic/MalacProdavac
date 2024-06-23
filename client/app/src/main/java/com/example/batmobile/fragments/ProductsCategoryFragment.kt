package com.example.batmobile.fragments

import android.os.Bundle
import androidx.fragment.app.Fragment
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import android.widget.ImageView
import android.widget.LinearLayout
import android.widget.TextView
import androidx.core.widget.NestedScrollView
import androidx.navigation.fragment.findNavController
import com.example.batmobile.DTOFromServer.Product
import com.example.batmobile.DTOFromServer.ResponseListTemplate
import com.example.batmobile.R
import com.example.batmobile.network.ApiClient
import com.google.gson.Gson
import com.google.gson.reflect.TypeToken
import de.hdodenhof.circleimageview.CircleImageView

class ProductsCategoryFragment : Fragment() {

    private lateinit var view                   : View
    private          var productId              : Int = -1
    private lateinit var apiClient              : ApiClient

    private lateinit var category_image         : CircleImageView
    private lateinit var category_name          : TextView
    private lateinit var close                  : ImageView
    private lateinit var container_of_products  : NestedScrollView
    private lateinit var linear_container       : LinearLayout

    private fun getAllStuff(){
        apiClient               = ApiClient(requireContext())

        category_image          = view.findViewById<CircleImageView>(R.id.category_image)
        category_name           = view.findViewById<TextView>(R.id.category_name)
        close                   = view.findViewById<ImageView>(R.id.close)
        container_of_products   = view.findViewById<NestedScrollView>(R.id.container_of_products)
        linear_container        = view.findViewById<LinearLayout>(R.id.linear_container)
    }

    fun getProductsPerCategory(category_id: Int){
        var url:String = com.example.batmobile.network.Config.ip_address+":"+ com.example.batmobile.network.Config.port + "/getProductsByCategory/"+category_id
        apiClient.sendGetRequestEmpty(url,
            {response->
                println(response)
                var gson = Gson()
                val typeToken = object : TypeToken<ResponseListTemplate<Product>>() {}.type
                var category_product_list = gson.fromJson<ResponseListTemplate<Product>>(response, typeToken)
                renderListOfProducts(category_product_list.data)
            },
            {error->
                println(error)
            })
    }

    fun setStaticInfo(){
        when(productId){
            1->{category_image.setImageResource(R.drawable.mlecni_proizvodi); category_name.text = "Mlečni proizvodi"}
            2->{category_image.setImageResource(R.drawable.voce_i_povrce); category_name.text = "Voće i povrće"}
            3->{category_image.setImageResource(R.drawable.mesne_preradjevine); category_name.text = "Mesne prerađevine"}
            4->{category_image.setImageResource(R.drawable.meso); category_name.text = "Sveže meso"}
            5->{category_image.setImageResource(R.drawable.zitarice); category_name.text = "Žitarice"}
            6->{category_image.setImageResource(R.drawable.napici); category_name.text = "Napici"}
            7->{category_image.setImageResource(R.drawable.biljna_ulja); category_name.text = "Biljna ulja"}
            8->{category_image.setImageResource(R.drawable.namazi); category_name.text = "Namazi"}
        }
    }

    fun setActionListener(){
        close.setOnClickListener{ findNavController().navigateUp() }
    }

    override fun onCreateView(
        inflater: LayoutInflater, container: ViewGroup?,
        savedInstanceState: Bundle?
    ): View? {
        view = inflater.inflate(R.layout.fragment_products_category, container, false)


        val args: ProductsCategoryFragmentArgs = ProductsCategoryFragmentArgs.fromBundle(requireArguments())
        productId = args.productsCategory

        getAllStuff()
        setActionListener()
        setStaticInfo()

        getProductsPerCategory(productId)

        return view
    }

    fun renderListOfProducts(list_of_products: List<Product>){
        var row: LinearLayout
        row = LinearLayout(requireContext())
        row.orientation = LinearLayout.HORIZONTAL
        val layoutParams = LinearLayout.LayoutParams(
            LinearLayout.LayoutParams.MATCH_PARENT,
            LinearLayout.LayoutParams.WRAP_CONTENT
        )
        row.layoutParams = layoutParams
        linear_container.addView(row)

        for ((i, product: Product) in list_of_products.withIndex()) {
            if ((i - 1) % 3 == 0) {
                row = LinearLayout(requireContext())
                row.orientation = LinearLayout.HORIZONTAL
                val layoutParams = LinearLayout.LayoutParams(
                    LinearLayout.LayoutParams.MATCH_PARENT,
                    LinearLayout.LayoutParams.WRAP_CONTENT
                )
                layoutParams.setMargins(0, 0, 0, 20)
                row.layoutParams = layoutParams
                linear_container.addView(row)
            }
            val itemView = layoutInflater.inflate(R.layout.component_explore_product, null)
            val itemLayoutParams = LinearLayout.LayoutParams(
                0,
                LinearLayout.LayoutParams.WRAP_CONTENT,
                1f
            )
                val image = itemView.findViewById<ImageView>(R.id.product_image)
                val product_name = itemView.findViewById<TextView>(R.id.product_name)
                com.example.batmobile.services.Image.setImageResource(image,product.picture, product.category_id)
                product_name.text = product.productName

            itemView.setOnClickListener{
                val action = ProductViewFragmentDirections.actionProductViewFragment(product.id)
                findNavController().navigate(action)
            }

            val marginInPx = 8
            itemLayoutParams.setMargins(marginInPx, 0, marginInPx, 0)
            itemView.setPadding(marginInPx, marginInPx, marginInPx, marginInPx)
            itemView.layoutParams = itemLayoutParams
            row.addView(itemView)
        }
    }   

}