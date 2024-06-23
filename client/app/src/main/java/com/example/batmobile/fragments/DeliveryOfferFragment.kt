package com.example.batmobile.fragments

import android.os.Bundle
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import android.view.animation.AnimationUtils
import android.widget.Button
import android.widget.FrameLayout
import android.widget.ImageView
import android.widget.LinearLayout
import android.widget.TextView
import androidx.constraintlayout.widget.ConstraintLayout
import androidx.core.widget.NestedScrollView
import androidx.fragment.app.Fragment
import androidx.loader.app.LoaderManager
import androidx.navigation.fragment.findNavController
import com.example.batmobile.DTOFromServer.OfferDetails
import com.example.batmobile.DTOFromServer.ResponseListTemplate
import com.example.batmobile.R
import com.example.batmobile.network.ApiClient
import com.example.batmobile.network.Config
import com.example.batmobile.services.Comments
import com.example.batmobile.services.Image
import com.example.batmobile.services.JWTService
import com.google.gson.Gson
import com.google.gson.reflect.TypeToken

class DeliveryOfferFragment : Fragment() {
    private lateinit var view                           : View
    private lateinit var apiClient                      : ApiClient
    private lateinit var close                          : ImageView
    private lateinit var offer_information              : OfferDetails
    private lateinit var scroll_offers                  : NestedScrollView
    private lateinit var loader_place                   : FrameLayout
    private fun getAllStuff() {
        apiClient               = ApiClient(requireContext())
        close =                 view.findViewById(R.id.close)
        scroll_offers =         view.findViewById(R.id.scroll_offers)
        loader_place    = view.findViewById<FrameLayout>(R.id.loader_place)
    }

    private fun getOfferDetails() {
        var url: String = Config.ip_address+":"+ Config.port + "/list-off-all-offers"
        apiClient.sendGetRequestEmpty(
            JWTService.getToken(),url,
            { response ->
                var gson = Gson()
                val typeToken = object : TypeToken<ResponseListTemplate<OfferDetails>>() {}.type
                var offers = gson.fromJson<ResponseListTemplate<OfferDetails>>(response, typeToken)
                println(offers.data)
                renderOffers(offers.data)
                loader_place.visibility = View.GONE
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
        view = inflater.inflate(R.layout.fragment_delivery_offer, container, false)
        getAllStuff()
        getOfferDetails()
        close.setOnClickListener{ findNavController().navigateUp() }
        addLoader()
        return view
    }


    private fun addLoader(){
        val loader = requireActivity().layoutInflater.inflate(R.layout.loader_component, null)
        val rotateAnimation = AnimationUtils.loadAnimation(requireContext(), R.anim.rotate)
        val cube_purple = loader.findViewById<ImageView>(R.id.cube_purple)
        val cube_green = loader.findViewById<ImageView>(R.id.cube_green)
        val cube_red = loader.findViewById<ImageView>(R.id.cube_red)
        val cube_light_blue = loader.findViewById<ImageView>(R.id.cube_light_blue)
        val cube_pink = loader.findViewById<ImageView>(R.id.cube_pink)

        cube_purple.startAnimation(rotateAnimation)
        cube_green.startAnimation(rotateAnimation)
        cube_red.startAnimation(rotateAnimation)
        cube_light_blue.startAnimation(rotateAnimation)
        cube_pink.startAnimation(rotateAnimation)

        loader_place.addView(loader)
    }

    private fun renderOffers(offers: List<OfferDetails>) {
        val row = LinearLayout(requireContext())
        row.orientation = LinearLayout.VERTICAL
        val layoutParams = LinearLayout.LayoutParams(
            LinearLayout.LayoutParams.MATCH_PARENT,
            LinearLayout.LayoutParams.MATCH_PARENT
        )
        row.layoutParams = layoutParams
        scroll_offers.addView(row)

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

        if(offers.size != 0)
        {
            scroll_offers.removeAllViews()
            scroll_offers.addView(row)
            for(offer in offers)
            {
                val itemView            = layoutInflater.inflate(R.layout.component_offer, null) as ConstraintLayout
                val buyerImage          = itemView.findViewById<ImageView>(R.id.buyer_image)
                val naslov              = itemView.findViewById<TextView>(R.id.item_name)
                val cena                = itemView.findViewById<TextView>(R.id.cena)
                val datum               = itemView.findViewById<TextView>(R.id.datum)
                val offer_id             = itemView.findViewById<TextView>(R.id.offer_id)
                val profile_btn         = itemView.findViewById<Button>(R.id.profile)
                val detalji             = itemView.findViewById<Button>(R.id.details)

                Image.setImageResource(buyerImage, offer.delivererPicture,-1)
                naslov.text = "Dostavljač " + offer.delivererName + " " + offer.delivererSurname
                cena.text = offer.delivererPrice.toString()
                datum.text = offer.orderDate
                offer_id.text = "#" + offer.offerId.toString()

                profile_btn.setOnClickListener{
//                    var action = DelivererProfileFragmentDirections.actionDelivererProfile(offer.delivererId)
//                    findNavController().navigate(action)
                }
                detalji.setOnClickListener {
                    val action = OrderDetailsFragmentDirections.actionOrderDetailsFragmentt(offer.orderId, offer.offerId)
                    findNavController().navigate(action)
                }

                itemView.layoutParams = itemLayoutParams
                row.addView(itemView)
            }
        }
    }
}