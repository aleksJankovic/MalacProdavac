package com.example.batmobile

import android.app.Dialog
import android.os.Bundle
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import android.view.Window
import android.widget.Button
import android.widget.ImageView
import android.widget.LinearLayout
import android.widget.TextView
import android.widget.Toast
import androidx.constraintlayout.widget.ConstraintLayout
import androidx.fragment.app.Fragment
import androidx.navigation.fragment.findNavController
import com.example.batmobile.DTOFromServer.JobInformation
import com.example.batmobile.DTOFromServer.OrderInformation
import com.example.batmobile.DTOFromServer.OrdersInformation
import com.example.batmobile.DTOFromServer.ResponseListTemplate
import com.example.batmobile.DTOFromServer.ResponseObjectTemplate
import com.example.batmobile.enums.FilterOrdersType
import com.example.batmobile.fragments.CartFragmentDirections
import com.example.batmobile.fragments.OrderDetailsFragmentDirections
import com.example.batmobile.network.ApiClient
import com.example.batmobile.network.Config
import com.example.batmobile.services.Comments
import com.example.batmobile.services.Image
import com.example.batmobile.services.JWTService
import com.google.gson.Gson
import com.google.gson.reflect.TypeToken
import de.hdodenhof.circleimageview.CircleImageView
import kotlinx.coroutines.CoroutineScope
import kotlinx.coroutines.Dispatchers
import kotlinx.coroutines.async
import kotlinx.coroutines.launch
import org.json.JSONObject
import java.util.Objects


class DelivererKorpaFragment : Fragment() {

    private lateinit var view: View
    private lateinit var apiClient: ApiClient
    private lateinit var cart: ImageView
    private lateinit var all_my_orders:     List<OrdersInformation>
    private lateinit var all_my_jobs:       List<JobInformation>

    private lateinit var switch_my_store: Button
    private lateinit var switch_my_orders: Button

    private lateinit var my_orders_container: ConstraintLayout
    private lateinit var my_store_container: ConstraintLayout

    private lateinit var list_of_my_orders: LinearLayout
    private lateinit var list_of_recieved_purchase: LinearLayout
    private var show_type: FilterOrdersType = FilterOrdersType.poslato
    private var show_type_my_store: FilterOrdersType = FilterOrdersType.new_purchase
    private lateinit var empty_message: TextView

    private lateinit var on_the_way:        Button
    private lateinit var all_orders:        Button
    private lateinit var ordered_order:     Button
    private lateinit var ordered:           Button

    fun getAllStuff(){
        apiClient = ApiClient(requireContext())

        cart              = view.findViewById<ImageView>(R.id.cart)

        switch_my_store   = view.findViewById<Button>(R.id.switch_my_store)
        switch_my_orders   = view.findViewById<Button>(R.id.switch_my_orders)

        my_orders_container = view.findViewById<ConstraintLayout>(R.id.my_orders_container)
        my_store_container  = view.findViewById<ConstraintLayout>(R.id.my_store_container)

        list_of_my_orders = view.findViewById<LinearLayout>(R.id.list_of_my_orders)
        list_of_recieved_purchase = view.findViewById<LinearLayout>(R.id.list_of_recieved_purchase)
        empty_message     = view.findViewById<TextView>(R.id.empty_message)

        on_the_way        = view.findViewById<Button>(R.id.on_the_way)
        all_orders        = view.findViewById<Button>(R.id.all_orders)
        ordered_order     = view.findViewById<Button>(R.id.ordered_order)
        ordered           = view.findViewById<Button>(R.id.ordered)
    }

    fun setAllEventListener(){
        switch_my_store.setOnClickListener{setSwitchVisibility(1)}
        switch_my_orders.setOnClickListener{setSwitchVisibility(0)}

        cart.setOnClickListener{
            val action = CartFragmentDirections.actionCartFragment()
            findNavController().navigate(action)
        }

        on_the_way.setOnClickListener{
            if(show_type != FilterOrdersType.poslato){
                show_type = FilterOrdersType.poslato
                println(show_type)
                setColorOfTypeFilter(FilterOrdersType.poslato)
                renderMyOrders()
            }
        }
        all_orders.setOnClickListener{
            if(show_type != FilterOrdersType.u_potrazi_za_dostavljacem){
                show_type = FilterOrdersType.u_potrazi_za_dostavljacem
                println(show_type)
                setColorOfTypeFilter(FilterOrdersType.u_potrazi_za_dostavljacem)
                renderMyOrders()
            }
        }
        ordered_order.setOnClickListener{
            if(show_type != FilterOrdersType.u_pripremi){
                show_type = FilterOrdersType.u_pripremi
                setColorOfTypeFilter(FilterOrdersType.u_pripremi)
                renderMyOrders()
            }
        }
        ordered.setOnClickListener{
            if(show_type != FilterOrdersType.dostavljeno){
                show_type = FilterOrdersType.dostavljeno
                setColorOfTypeFilter(FilterOrdersType.dostavljeno)
                renderMyOrders()
            }
        }

    }

    fun getOrder(id: Int, order_status: Int){
        val url: String = Config.ip_address+":"+ Config.port+"/order/items?orderId=" + id
        apiClient.sendGetRequestEmpty(
            JWTService.getToken(), url,
            {response->
                var gson = Gson()
                val typeToken = object : TypeToken<ResponseListTemplate<OrderInformation>>() {}.type
                var orderDetails= gson.fromJson<ResponseListTemplate<OrderInformation>>(response, typeToken)
                renderMyProductsInOrder(orderDetails.data, id, order_status)
            },
            { error->
                println(error)
            }
        )
    }

    override fun onCreateView(
        inflater: LayoutInflater, container: ViewGroup?,
        savedInstanceState: Bundle?
    ): View? {
        // Inflate the layout for this fragment
        view =  inflater.inflate(R.layout.fragment_deliverer_korpa, container, false)

        getAllStuff()
        setAllEventListener()

        //        API calls
        val scope = CoroutineScope(Dispatchers.Default)
        scope.launch {
            async { getAllMyOrders() }
            async { getMyJobs() }
        }

        return  view
    }

    private fun setSwitchVisibility(show: Int){
        if(show == 0){
            //my_orders_container
//            switch_my_store.background = resources.getDrawable(R.drawable.empty_button)
            switch_my_store.setBackgroundResource(R.drawable.empty_button)
            switch_my_store.setTextColor(resources.getColor(R.color.black))
            switch_my_orders.setBackgroundResource(R.drawable.full_fill_button)
            switch_my_orders.setTextColor(resources.getColor(R.color.white))

            my_orders_container.visibility = View.VISIBLE
            my_store_container.visibility  = View.GONE
        }
        else if(show == 1) {
            //my_store_container
            switch_my_store.setBackgroundResource(R.drawable.full_fill_button)
            switch_my_store.setTextColor(resources.getColor(R.color.white))
            switch_my_orders.setBackgroundResource(R.drawable.empty_button)
            switch_my_orders.setTextColor(resources.getColor(R.color.black))

            my_orders_container.visibility = View.GONE
            my_store_container.visibility  = View.VISIBLE
        }
    }

    fun setColorOfTypeFilter(selected: FilterOrdersType){
        when(selected){
            FilterOrdersType.poslato       -> {
                on_the_way.setBackgroundResource(R.drawable.full_fill_button);on_the_way.setTextColor( resources.getColor(R.color.white))
                all_orders.setBackgroundResource(R.drawable.empty_button);all_orders.setTextColor( resources.getColor(R.color.black))
                ordered_order.setBackgroundResource(R.drawable.empty_button);ordered_order.setTextColor( resources.getColor(R.color.black))
                ordered.setBackgroundResource(R.drawable.empty_button);ordered.setTextColor( resources.getColor(R.color.black))
            };

            FilterOrdersType.u_potrazi_za_dostavljacem-> {
                on_the_way.setBackgroundResource(R.drawable.empty_button);on_the_way.setTextColor( resources.getColor(R.color.black))
                all_orders.setBackgroundResource(R.drawable.full_fill_button);all_orders.setTextColor( resources.getColor(R.color.white))
                ordered_order.setBackgroundResource(R.drawable.empty_button);ordered_order.setTextColor( resources.getColor(R.color.black))
                ordered.setBackgroundResource(R.drawable.empty_button);ordered.setTextColor( resources.getColor(R.color.black))
            };

            FilterOrdersType.u_pripremi    -> {
                on_the_way.setBackgroundResource(R.drawable.empty_button);on_the_way.setTextColor( resources.getColor(R.color.black))
                all_orders.setBackgroundResource(R.drawable.empty_button);all_orders.setTextColor( resources.getColor(R.color.black))
                ordered_order.setBackgroundResource(R.drawable.full_fill_button);ordered_order.setTextColor( resources.getColor(R.color.white))
                ordered.setBackgroundResource(R.drawable.empty_button);ordered.setTextColor( resources.getColor(R.color.black))
            };

            FilterOrdersType.dostavljeno       -> {
                on_the_way.setBackgroundResource(R.drawable.empty_button);on_the_way.setTextColor( resources.getColor(R.color.black))
                all_orders.setBackgroundResource(R.drawable.empty_button);all_orders.setTextColor( resources.getColor(R.color.black))
                ordered_order.setBackgroundResource(R.drawable.empty_button);ordered_order.setTextColor( resources.getColor(R.color.black))
                ordered.setBackgroundResource(R.drawable.full_fill_button);ordered.setTextColor( resources.getColor(R.color.white))
            };

//            =========================================
            FilterOrdersType.new_purchase           -> {}

            FilterOrdersType.purchase_on_the_way    -> {}

            FilterOrdersType.sended_order           -> {}

            FilterOrdersType.all_purchase           -> {}
        }
    }

    private fun getMyJobs() {
        val url: String = Config.ip_address+":"+ Config.port+"/myJobs"
        apiClient.sendGetRequestEmpty(
            JWTService.getToken(), url,
            {response->
                var gson = Gson()
                val typeToken = object : TypeToken<ResponseListTemplate<JobInformation>>() {}.type
                var ordersDetails= gson.fromJson<ResponseListTemplate<JobInformation>>(response, typeToken)
                println("MYJOBS" + ordersDetails.data)
                all_my_jobs = ordersDetails.data
                renderMyJobs()
            },
            {error->
                println(error)
            }
        )
    }

    fun getAllMyOrders(){
        val url: String = Config.ip_address+":"+ Config.port+"/order/my-cart"
        apiClient.sendGetRequestEmpty(
            JWTService.getToken(), url,
            {response->
                var gson = Gson()
                val typeToken = object : TypeToken<ResponseListTemplate<OrdersInformation>>() {}.type
                var ordersDetails= gson.fromJson<ResponseListTemplate<OrdersInformation>>(response, typeToken)
                println(ordersDetails.data)
                all_my_orders = ordersDetails.data
                renderMyOrders()
            },
            {error->
                println(error)
            }
        )
    }

    fun setOrderStatus(orderId: Int, statusId:Int, dialog: Dialog){
        var jsonObject: JSONObject = JSONObject()
        jsonObject.put("orderId", orderId)
        jsonObject.put("statusId", statusId)
        var url: String = Config.ip_address + ":" + Config.port + "/order/changeOrderStatus"
        apiClient.sendPostRequestWithJSONObjectWithStringResponse(url, JWTService.getToken(),
            jsonObject,
            {response->
                println(response)
                var gson = Gson()
                val typeToken = object : TypeToken<ResponseObjectTemplate<Objects>>() {}.type
                var ordersDetails= gson.fromJson<ResponseObjectTemplate<Objects>>(response, typeToken)
                when(ordersDetails.message){
                    "narudzbina dostavljena!" -> {
                        Toast.makeText(requireContext(), "Uspešno setovan status porudžbine", Toast.LENGTH_LONG).show()
                    }
                }
                getAllMyOrders()
                dialog.dismiss()
            },
            {error->
                println(error)
            })
    }

    private fun renderMyOrders(){
        val itemLayoutParams : LinearLayout.LayoutParams;
        itemLayoutParams = Comments.itemLayoutParamsForVertical
        itemLayoutParams.setMargins(0, (Comments.marginInDp +20), 0, 0)

        var get_order_type = -1

        when(show_type){
            FilterOrdersType.u_pripremi                 -> {get_order_type = 1  }
            FilterOrdersType.poslato                    -> {get_order_type = 2  }
            FilterOrdersType.dostavljeno                -> {get_order_type = 3  }
            FilterOrdersType.u_potrazi_za_dostavljacem  -> {get_order_type = 4 }
            else -> {}
        }
        list_of_my_orders.removeAllViews()

        empty_message.visibility = View.VISIBLE

        for(order: OrdersInformation in all_my_orders){
            if(order.orderStatusId == get_order_type){
                val itemView = layoutInflater.inflate(R.layout.component_order, null)
                val profile_seller  = itemView.findViewById<ImageView>(R.id.profile_seller)
                val seller_username = itemView.findViewById<TextView>(R.id.seller_username)
                val ordered_date    = itemView.findViewById<TextView>(R.id.ordered_date)
                val total_price     = itemView.findViewById<TextView>(R.id.total_price)
                val status_icon     = itemView.findViewById<ImageView>(R.id.status_icon)

                Image.setImageResource(profile_seller, order.sellerImage, -1)
                seller_username.text = order.sellerName
                ordered_date.text = order.orderDate
                total_price.text = order.totalPrice.toString() + " rsd."
                when(order.orderStatusId){
                    4 -> {status_icon.setImageResource(R.drawable.naruceno)}
                    2 -> {status_icon.setImageResource(R.drawable.slanje)}
                    3 -> {status_icon.setImageResource(R.drawable.dostavljeno)}
                    1 -> {status_icon.setImageResource(R.drawable.u_pripremi)}
                }

                itemView.setOnClickListener{ getOrder(order.orderId, order.orderStatusId) }

                itemView.layoutParams = itemLayoutParams
                empty_message.visibility = View.GONE
                list_of_my_orders.addView(itemView)
            }

        }
    }

    private fun renderMyProductsInOrder(order_list: List<OrderInformation>, id: Int, order_status: Int){
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
        var order_id = dialog.findViewById<TextView>(R.id.order_id)
        order_id.text = "# " + id

        var post_comment = dialog.findViewById<Button>(R.id.post_comment)
        if(order_status ==2){
            post_comment.visibility = View.VISIBLE
            post_comment.setOnClickListener {
            setOrderStatus(id, 3, dialog)
            }
        }

        for (element: OrderInformation in order_list){
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

    private fun renderMyJobs() {
        val itemLayoutParams : LinearLayout.LayoutParams;
        itemLayoutParams = Comments.itemLayoutParamsForVertical
        itemLayoutParams.setMargins(0, (Comments.marginInDp +20), 0, 0)


        empty_message.text = "Trenutno nemate posla."
        empty_message.visibility = View.VISIBLE

        for(job: JobInformation in all_my_jobs){

            val itemView = layoutInflater.inflate(R.layout.component_order, null)
            val profile_seller  = itemView.findViewById<ImageView>(R.id.profile_seller)
            val seller_username = itemView.findViewById<TextView>(R.id.seller_username)
            val ordered_date    = itemView.findViewById<TextView>(R.id.ordered_date)
            val total_price_title = itemView.findViewById<TextView>(R.id.total_price_title)
            val total_price     = itemView.findViewById<TextView>(R.id.total_price)
            val status_icon     = itemView.findViewById<ImageView>(R.id.status_icon)
            val total_price_title_delivering = itemView.findViewById<TextView>(R.id.total_price_title_delivering)
            val total_price_delivering = itemView.findViewById<TextView>(R.id.total_price_delivering)

            total_price_title_delivering.visibility = View.VISIBLE
            total_price_delivering.visibility = View.VISIBLE

            Image.setImageResource(profile_seller, job.buyerPicture, -1)
            seller_username.text = job.buyerUsername
            ordered_date.text = job.orderDate
            total_price_title.text = "Cena pošiljke: "
            total_price.text = job.purchasePrice.toInt().toString() + " rsd."
            total_price_delivering.text = job.delivererPrice.toInt().toString() + " rsd."


            itemView.setOnClickListener{
                val action = OrderDetailsFragmentDirections.actionOrderDetailsFragmentt(job.orderId.toLong(), -2)
                findNavController().navigate(action)
            }

            itemView.layoutParams = itemLayoutParams
            empty_message.visibility = View.GONE
            list_of_recieved_purchase.addView(itemView)


        }
    }
}