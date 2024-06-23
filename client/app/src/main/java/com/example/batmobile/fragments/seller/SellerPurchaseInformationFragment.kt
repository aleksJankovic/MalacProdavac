package com.example.batmobile.fragments.seller

import android.app.Dialog
import android.os.Bundle
import androidx.fragment.app.Fragment
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import android.widget.Button
import android.widget.ImageView
import android.widget.LinearLayout
import android.widget.TextView
import android.widget.Toast
import androidx.navigation.fragment.findNavController
import com.example.batmobile.DTOFromServer.ResponseObjectTemplate
import com.example.batmobile.DTOFromServer.SellerPurchaseInformationDTO
import com.example.batmobile.DTOFromServer.SellerPurchaseItems
import com.example.batmobile.R
import com.example.batmobile.fragments.ProductViewFragmentArgs
import com.example.batmobile.fragments.ProductViewFragmentDirections
import com.example.batmobile.models.Category
import com.example.batmobile.models.OrderItems
import com.example.batmobile.network.ApiClient
import com.example.batmobile.network.Config
import com.example.batmobile.services.Cart
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

class SellerPurchaseInformationFragment : Fragment() {

    private lateinit var view: View
    private var purchaseId: Int = -1
    private lateinit var apiClient: ApiClient

    private lateinit var close: Button

    private lateinit var purchase_id: TextView
    private lateinit var name: TextView
    private lateinit var surname: TextView
    private lateinit var purchase_address: TextView
    private lateinit var purchase_email: TextView
    private lateinit var purhase_licno: TextView
    private lateinit var purchase_organizovano: TextView
    private lateinit var purchase_kurirska:TextView
    private lateinit var purchase_racun: TextView
    private lateinit var purchase_pouzecem: TextView
    private lateinit var purchase_licno:TextView
    private lateinit var list_of_items: LinearLayout
    private lateinit var accept_order: Button


    private fun getAllStuff(){
        apiClient = ApiClient(requireContext())

        close = view.findViewById<Button>(R.id.close)

        purchase_id = view.findViewById<TextView>(R.id.purchase_id)
        name = view.findViewById<TextView>(R.id.textView3)
        surname = view.findViewById<TextView>(R.id.textView5)
        purchase_address = view.findViewById<TextView>(R.id.purchase_address)
        purchase_email = view.findViewById<TextView>(R.id.purchase_email)
        purhase_licno = view.findViewById<TextView>(R.id.purhase_licno)
        purchase_organizovano = view.findViewById<TextView>(R.id.purchase_organizovano)
        purchase_kurirska = view.findViewById<TextView>(R.id.purchase_kurirska)
        purchase_racun = view.findViewById<TextView>(R.id.purchase_racun)
        purchase_pouzecem = view.findViewById<TextView>(R.id.purchase_pouzecem)
        purchase_licno = view.findViewById<TextView>(R.id.purchase_licno)
        list_of_items = view.findViewById<LinearLayout>(R.id.list_of_items)
        accept_order = view.findViewById<Button>(R.id.accept_order)

    }

    private fun setAllListeners(){
        close.setOnClickListener{
            findNavController().navigateUp()
        }
        accept_order.setOnClickListener {
            setOrderStatus()
        }
    }

    fun setOrderStatus(){
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
                Toast.makeText(requireContext(), "Uspešno setovan status porudžbine", Toast.LENGTH_LONG).show()
                findNavController().navigateUp()
            },
            {error->
                println(error)
            })
    }

    suspend fun getPurchaseDetails(){
        var url:String = Config.ip_address+":"+ Config.port +"/order/order-details?orderId="+purchaseId
        apiClient.sendGetRequestEmpty(JWTService.getToken(),url,
            { response ->
                var gson = Gson()
                var typeToken = object : TypeToken<ResponseObjectTemplate<SellerPurchaseInformationDTO>>(){}.type
                var response_obj = gson.fromJson<ResponseObjectTemplate<SellerPurchaseInformationDTO>>(response, typeToken)
                renderInformation(response_obj.data)
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
        view = inflater.inflate(R.layout.fragment_seller_purchase_information, container, false)

        val args: SellerPurchaseInformationFragmentArgs = SellerPurchaseInformationFragmentArgs.fromBundle(requireArguments())
        purchaseId = args.purchaseId

        getAllStuff()
        setAllListeners()

        val scope = CoroutineScope(Dispatchers.Default)
        scope.launch {
            async { getPurchaseDetails(); }
        }


        return view
    }

    var orderId: Int = -1
    var statusId: Int = -1

    private fun renderInformation(information: SellerPurchaseInformationDTO){

        purchase_id.text = "# " + information.orderId
        orderId = information.orderId
        name.text = information.buyerName
        surname.text = information.buyerSurname
        if(information.buyerAddress == null){
            purchase_address.text = "Simulirana adresa"
        }
        else{
            purchase_address.text = information.buyerAddress
        }
        purchase_email.text = information.buyerEmail
        when(information.shippingMethodId){
            1->{
                purhase_licno.setTextColor(resources.getColor(R.color.black))
                purchase_organizovano.setTextColor(resources.getColor(R.color.orange))
                purchase_kurirska.setTextColor(resources.getColor(R.color.black))
                statusId = 4
            }
            2->{
                purhase_licno.setTextColor(resources.getColor(R.color.black))
                purchase_organizovano.setTextColor(resources.getColor(R.color.black))
                purchase_kurirska.setTextColor(resources.getColor(R.color.orange))
                statusId = 2
            }
            3->{
                purhase_licno.setTextColor(resources.getColor(R.color.orange))
                purchase_organizovano.setTextColor(resources.getColor(R.color.black))
                purchase_kurirska.setTextColor(resources.getColor(R.color.black))
                statusId = 2
            }
        }
        when(information.paymentMethodId){
            1->{
                purchase_racun.setTextColor(resources.getColor(R.color.orange))
                purchase_pouzecem.setTextColor(resources.getColor(R.color.black))
                purchase_licno.setTextColor(resources.getColor(R.color.black))
            }
            2->{
                purchase_racun.setTextColor(resources.getColor(R.color.black))
                purchase_pouzecem.setTextColor(resources.getColor(R.color.orange))
                purchase_licno.setTextColor(resources.getColor(R.color.black))
            }
            3->{
                purchase_racun.setTextColor(resources.getColor(R.color.black))
                purchase_pouzecem.setTextColor(resources.getColor(R.color.black))
                purchase_licno.setTextColor(resources.getColor(R.color.orange))
            }
        }

        val itemLayoutParams : LinearLayout.LayoutParams;
        itemLayoutParams = Comments.itemLayoutParamsForVertical
        itemLayoutParams.setMargins(0, (Comments.marginInDp +20), 0, 0)

        for( orderItem: SellerPurchaseItems in information.purchaseItems ){
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
            list_of_items.addView(itemView)
        }

    }

}