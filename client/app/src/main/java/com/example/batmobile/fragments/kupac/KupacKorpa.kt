package com.example.batmobile.fragments.kupac

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
import com.example.batmobile.DTOFromServer.CustomerOrder
import com.example.batmobile.DTOFromServer.OrderInformation
import com.example.batmobile.DTOFromServer.OrdersInformation
import com.example.batmobile.DTOFromServer.ResponseListTemplate
import com.example.batmobile.DTOFromServer.ResponseObjectTemplate
import com.example.batmobile.R
import com.example.batmobile.enums.FilterOrdersType
import com.example.batmobile.enums.Role
import com.example.batmobile.fragments.CartFragmentDirections
import com.example.batmobile.fragments.seller.SellerPurchaseInformationFragmentDirections
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

class KupacKorpa : Fragment() {


    private lateinit var view:              View
    private lateinit var apiClient:         ApiClient
    private lateinit var cart:              ImageView
    private lateinit var all_my_orders:     List<OrdersInformation>

    private lateinit var navigation:        LinearLayout
    private lateinit var switch_my_store:   Button
    private lateinit var switch_my_orders:  Button

    private lateinit var my_orders_container: ConstraintLayout
    private lateinit var my_store_container: ConstraintLayout

    private lateinit var list_of_my_orders: LinearLayout
    private lateinit var list_of_recieved_purchase: LinearLayout
    private var show_type: FilterOrdersType = FilterOrdersType.poslato
    private var show_type_my_store: FilterOrdersType = FilterOrdersType.new_purchase
    private lateinit var empty_message:     TextView

    private lateinit var on_the_way:        Button
    private lateinit var all_orders:        Button
    private lateinit var ordered_order:     Button
    private lateinit var ordered:           Button

//    ===========================
    private lateinit var my_store: List<CustomerOrder>
    private lateinit var new_purchase:          Button
    private lateinit var purchase_on_the_way:   Button
    private lateinit var sended_order:          Button
    private lateinit var all_purchase:          Button

    fun getAllStuff(){
        apiClient = ApiClient(requireContext())

        cart              = view.findViewById<ImageView>(R.id.cart)

        navigation        = view.findViewById<LinearLayout>(R.id.navigation)
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

        new_purchase             = view.findViewById<Button>(R.id.new_purchase)
        purchase_on_the_way      = view.findViewById<Button>(R.id.purchase_on_the_way)
        sended_order             = view.findViewById<Button>(R.id.sended_order)
        all_purchase             = view.findViewById<Button>(R.id.all_purchase)
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

        new_purchase.setOnClickListener{
            if(show_type_my_store != FilterOrdersType.new_purchase){
                show_type_my_store = FilterOrdersType.new_purchase
                setColorOfTypeFilter(FilterOrdersType.new_purchase)
                renderMyStore()
            }
        }
        purchase_on_the_way.setOnClickListener{
            if(show_type_my_store != FilterOrdersType.purchase_on_the_way){
                show_type_my_store = FilterOrdersType.purchase_on_the_way
                setColorOfTypeFilter(FilterOrdersType.purchase_on_the_way)
                renderMyStore()
            }
        }
        sended_order.setOnClickListener{
            if(show_type_my_store != FilterOrdersType.sended_order){
                show_type_my_store = FilterOrdersType.sended_order
                setColorOfTypeFilter(FilterOrdersType.sended_order)
                renderMyStore()
            }
        }
        all_purchase.setOnClickListener{
            if(show_type_my_store != FilterOrdersType.all_purchase){
                show_type_my_store = FilterOrdersType.all_purchase
                setColorOfTypeFilter(FilterOrdersType.all_purchase)
                renderMyStore()
            }
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
            FilterOrdersType.new_purchase           -> {
                new_purchase.setBackgroundResource(R.drawable.full_fill_button); new_purchase.setTextColor( resources.getColor(R.color.white))
                purchase_on_the_way.setBackgroundResource(R.drawable.empty_button);purchase_on_the_way.setTextColor( resources.getColor(R.color.black))
                sended_order.setBackgroundResource(R.drawable.empty_button);sended_order.setTextColor( resources.getColor(R.color.black))
                all_purchase.setBackgroundResource(R.drawable.empty_button);all_purchase.setTextColor( resources.getColor(R.color.black)) }

            FilterOrdersType.purchase_on_the_way    -> {
                new_purchase.setBackgroundResource(R.drawable.empty_button); new_purchase.setTextColor( resources.getColor(R.color.black))
                purchase_on_the_way.setBackgroundResource(R.drawable.full_fill_button);purchase_on_the_way.setTextColor( resources.getColor(R.color.white))
                sended_order.setBackgroundResource(R.drawable.empty_button);sended_order.setTextColor( resources.getColor(R.color.black))
                all_purchase.setBackgroundResource(R.drawable.empty_button);all_purchase.setTextColor( resources.getColor(R.color.black))}

            FilterOrdersType.sended_order           -> {
                new_purchase.setBackgroundResource(R.drawable.empty_button); new_purchase.setTextColor( resources.getColor(R.color.black))
                purchase_on_the_way.setBackgroundResource(R.drawable.empty_button);purchase_on_the_way.setTextColor( resources.getColor(R.color.black))
                sended_order.setBackgroundResource(R.drawable.full_fill_button);sended_order.setTextColor( resources.getColor(R.color.white))
                all_purchase.setBackgroundResource(R.drawable.empty_button);all_purchase.setTextColor( resources.getColor(R.color.black))}

            FilterOrdersType.all_purchase           -> {
                new_purchase.setBackgroundResource(R.drawable.empty_button); new_purchase.setTextColor( resources.getColor(R.color.black))
                purchase_on_the_way.setBackgroundResource(R.drawable.empty_button);purchase_on_the_way.setTextColor( resources.getColor(R.color.black))
                sended_order.setBackgroundResource(R.drawable.empty_button);sended_order.setTextColor( resources.getColor(R.color.black))
                all_purchase.setBackgroundResource(R.drawable.full_fill_button);all_purchase.setTextColor( resources.getColor(R.color.white))}
        }
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
        println("pozvao")
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

    fun getAllInformationForMyStore(){
        val url: String = Config.ip_address+":"+ Config.port+"/seller/get-all-customer-orders"
        apiClient.sendGetRequestEmpty(
            JWTService.getToken(), url,
            {response->
                var gson = Gson()
                val typeToken = object : TypeToken<ResponseListTemplate<CustomerOrder>>() {}.type
                var postDetails = gson.fromJson<ResponseListTemplate<CustomerOrder>>(response, typeToken)
                println(postDetails.data)
                my_store = postDetails.data
                renderMyStore()
            },
            { error->
                println(error)
            }
        )
    }

    private fun setVisibilityPerRole(){
        if(JWTService.getRoleIfExist(requireContext()) == Role.Seller){
            navigation.visibility = View.VISIBLE
            setSwitchVisibility(1)
        }
        else
            setSwitchVisibility(0)
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

    override fun onCreateView(
        inflater: LayoutInflater, container: ViewGroup?,
        savedInstanceState: Bundle?
    ): View? {
        view = inflater.inflate(R.layout.fragment_kupac_korpa, container, false)

        getAllStuff()
        setAllEventListener()
        setVisibilityPerRole()

//        API calls
        val scope = CoroutineScope(Dispatchers.Default)
        scope.launch {
            async { getAllMyOrders() }
            async {
                if(JWTService.getRoleIfExist(requireContext()) == Role.Seller)
                getAllInformationForMyStore()
            }
        }

        return view
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
        if(order_status == 2){
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

//    -================================================
    private fun renderMyStore(){
        val itemLayoutParams : LinearLayout.LayoutParams;
        itemLayoutParams = Comments.itemLayoutParamsForVertical
        itemLayoutParams.setMargins(0, (Comments.marginInDp +20), 0, 0)

        var get_type_status = -1

        when(show_type_my_store){
            FilterOrdersType.new_purchase           -> {get_type_status = 1  } //1 - nova porudzbina, ceka se da joj prodavac promeni stanje
            FilterOrdersType.purchase_on_the_way    -> {get_type_status = 2  } //2 - prodavac promenio stanje i poslao je na neki nacin posiljku
            FilterOrdersType.sended_order           -> {get_type_status = 3  } //3 - posiljke koje su stigle kupcima
            FilterOrdersType.all_purchase           -> {get_type_status = -1 } //4  - posiljke koje cekaju dostavljaca da se javi
            else -> {}
        }

        list_of_recieved_purchase.removeAllViews()

        empty_message.visibility = View.VISIBLE

        for(element: CustomerOrder in my_store){
            if(element.orderStatus == get_type_status || get_type_status == -1){
                val itemView = layoutInflater.inflate(R.layout.component_for_new_order, null)
                val customer_image = itemView.findViewById<CircleImageView>(R.id.customer_image)
                val customer_username = itemView.findViewById<TextView>(R.id.customer_username)
                val customer_address = itemView.findViewById<TextView>(R.id.customer_address)
                val expend_order     = itemView.findViewById<Button>(R.id.expend_order)

                Image.setImageResource(customer_image, element.picture, -1)
                customer_username.text = element.username
                if(element.address != null)
                    customer_address.text = element.address!!.split(",")[0]
                else
                    customer_address.text = "Adresa je null"
                itemView.layoutParams = itemLayoutParams

                itemView.setOnClickListener {
                    val action = SellerPurchaseInformationFragmentDirections.actionPurchaseInformation(element.orderId)
                    findNavController().navigate(action)
                }
                expend_order.setOnClickListener{
                    val action = SellerPurchaseInformationFragmentDirections.actionPurchaseInformation(element.orderId)
                    findNavController().navigate(action)
                }

                empty_message.visibility = View.GONE
                list_of_recieved_purchase.addView(itemView)
            }

        }



    }

}