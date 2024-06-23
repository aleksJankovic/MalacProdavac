package com.example.batmobile.fragments

import android.os.Build
import android.os.Bundle
import androidx.fragment.app.Fragment
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import android.widget.Button
import android.widget.ImageView
import android.widget.LinearLayout
import android.widget.TextView
import androidx.annotation.RequiresApi
import androidx.navigation.fragment.findNavController
import com.etebarian.meowbottomnavigation.MeowBottomNavigation
import com.example.batmobile.R
import com.example.batmobile.models.OrderItems
import com.example.batmobile.models.Purchase
import com.example.batmobile.services.Cart
import com.example.batmobile.services.Comments
import com.example.batmobile.services.Image
import de.hdodenhof.circleimageview.CircleImageView

class CartFragment : Fragment() {

    private lateinit var view:  View
    private lateinit var meowBottomNavigation: MeowBottomNavigation

    private lateinit var close:         ImageView
    private lateinit var empty_message: TextView
    private lateinit var buy_button:    Button

    private fun getAllStuff(){
        close                           = view.findViewById<ImageView>(R.id.close)
        meowBottomNavigation            = requireActivity().findViewById<MeowBottomNavigation>(R.id.bottomNavigation)
        meowBottomNavigation.visibility = View.GONE
        empty_message                   = view.findViewById<TextView>(R.id.empty_message)
        buy_button                      = view.findViewById<Button>(R.id.buy_button)
    }

    private fun setAllActionListener(){
        close.setOnClickListener{
            meowBottomNavigation.visibility = View.VISIBLE
            findNavController().navigateUp()
        }

        buy_button.setOnClickListener{
            val action = CheckoutFragmentDirections.actionCheckoutFragment()
            findNavController().navigate(action)
        }

    }

    @RequiresApi(Build.VERSION_CODES.O)
    override fun onCreateView(
        inflater: LayoutInflater, container: ViewGroup?,
        savedInstanceState: Bundle?
    ): View? {
        view =  inflater.inflate(R.layout.fragment_cart, container, false)

        getAllStuff()
        setAllActionListener()

        renderCart()

        return view
    }

    @RequiresApi(Build.VERSION_CODES.O)
    private fun renderCart(){

        var cart:List<Purchase> = Cart.getCart()

        if(cart.size > 0){ empty_message.visibility = View.GONE; buy_button.isEnabled = true; buy_button.setBackgroundResource(R.drawable.full_fill_button);  }
        else{   empty_message.visibility = View.VISIBLE; buy_button.isEnabled = false; buy_button.setBackgroundResource(R.drawable.full_fill_button_disabled) }

        // for title
        val layoutText = LinearLayout.LayoutParams(
            LinearLayout.LayoutParams.WRAP_CONTENT,
            LinearLayout.LayoutParams.WRAP_CONTENT
        )
        layoutText.topMargin = Comments.marginInDp +14 // Adjust the value as needed

        // for item
        val itemLayoutParams : LinearLayout.LayoutParams;
        itemLayoutParams = Comments.itemLayoutParamsForVertical
        itemLayoutParams.setMargins(0, (Comments.marginInDp +14), 0, 0)

        var row = view.findViewById<LinearLayout>(R.id.cart)
        row.removeAllViews()
        var total_purchase_price: Int = 0

        for(purchase: Purchase in cart){
            val textView = TextView(requireContext())
                textView.layoutParams = layoutText
                textView.text = "Prodavac: " + purchase.username
                textView.typeface = resources.getFont(R.font.inter_medium)
                textView.setTextColor(resources.getColor(R.color.black))
                textView.textSize = 21f
            row.addView(textView)

            var total_price_per_seller = 0

            for( orderItem: OrderItems in purchase.orderItems ){
                val itemView = layoutInflater.inflate(R.layout.component_item_in_cart, null)
                    val item_name       = itemView.findViewById<TextView>(R.id.item_name)
                    val item_quantity   = itemView.findViewById<TextView>(R.id.item_quantity)
                    val item_image      = itemView.findViewById<CircleImageView>(R.id.item_image)
                    val item_remove_from_cart = itemView.findViewById<ImageView>(R.id.remove_from_cart)
                    item_name.text      = orderItem.product_name
                    item_quantity.text  =  orderItem.unit_measurmnent +" " + orderItem.price_per_one_quantity + "Rsd x " + orderItem.quantity +" = " + orderItem.price_per_one_quantity * orderItem.quantity + " rsd."
                    Image.setImageResource(item_image, orderItem.product_image, orderItem.category_id)
                    item_remove_from_cart.setOnClickListener{Cart.removeFromCart(purchase.sellerId, orderItem.productId, requireActivity()); renderCart()}
                itemView.layoutParams = itemLayoutParams
                row.addView(itemView)
                total_price_per_seller += orderItem.price_per_one_quantity * orderItem.quantity
            }

            val totalPriceView = layoutInflater.inflate(R.layout.component_total_price_for_cart, null)
                val total_price = totalPriceView.findViewById<TextView>(R.id.total_price)
                total_price.text = total_price_per_seller.toString() + " rsd."

            total_purchase_price += total_price_per_seller
            row.addView(totalPriceView)
        }

        val totalPurchase = layoutInflater.inflate(R.layout.component_total_price_for_cart, null)
            val total_price = totalPurchase.findViewById<TextView>(R.id.total_price)
            val line        = totalPurchase.findViewById<ImageView>(R.id.imageView15)
            val total_tile  = totalPurchase.findViewById<TextView>(R.id.total_tile)
            line.setBackgroundResource(R.color.orange)
            total_tile.typeface = resources.getFont(R.font.inter_medium)
            total_tile.setTextColor(resources.getColor(R.color.black))
            total_price.text = total_purchase_price.toString() + " rsd."
        row.addView(totalPurchase)
        //===============================
//        val textView1 = TextView(requireContext())
//        textView1.layoutParams = LinearLayout.LayoutParams(
//            LinearLayout.LayoutParams.WRAP_CONTENT,
//            LinearLayout.LayoutParams.WRAP_CONTENT
//        )
//        textView1.text = "Prodavac: Pera peric"
//        textView1.typeface = resources.getFont(R.font.inter)
//        textView1.setTextColor(resources.getColor(R.color.black))
//        textView1.textSize = 21f
//
//        textView1.layoutParams = itemLayoutParams
//        row.addView(textView1)
//
//        val itemView1 = layoutInflater.inflate(R.layout.component_item_in_cart, null)
//        itemView.layoutParams = itemLayoutParams
//        row.addView(itemView1)

    }

}