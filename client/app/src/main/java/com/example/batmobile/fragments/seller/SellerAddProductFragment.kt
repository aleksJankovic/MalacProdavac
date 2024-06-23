package com.example.batmobile.fragments.seller

import android.app.Activity
import android.app.Dialog
import android.content.Intent
import android.net.Uri
import android.os.Bundle
import android.provider.MediaStore
import android.text.Editable
import android.text.TextWatcher
import androidx.fragment.app.Fragment
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import android.view.Window
import android.widget.AdapterView
import android.widget.ArrayAdapter
import android.widget.Button
import android.widget.EditText
import android.widget.ImageView
import android.widget.RadioGroup
import android.widget.Spinner
import android.widget.TextView
import android.widget.Toast
import androidx.constraintlayout.widget.ConstraintLayout
import androidx.core.widget.addTextChangedListener
import androidx.fragment.app.activityViewModels
import androidx.navigation.fragment.findNavController
import com.example.batmobile.DTOFromServer.NewNews
import com.example.batmobile.DTOFromServer.Product
import com.example.batmobile.DTOFromServer.ProductViewResponse
import com.example.batmobile.DTOFromServer.ResponseListTemplate
import com.example.batmobile.DTOFromServer.ResponseObjectTemplate
import com.example.batmobile.R
import com.example.batmobile.SellerProfileFragmentArgs
import com.example.batmobile.SellerProfileFragmentDirections
import com.example.batmobile.fragments.ProductViewFragmentDirections
import com.example.batmobile.models.NewProuct
import com.example.batmobile.network.ApiClient
import com.example.batmobile.network.Config
import com.example.batmobile.services.Image
import com.example.batmobile.services.JWTService
import com.example.batmobile.viewModels.UserViewModel
import com.google.gson.Gson
import com.google.gson.reflect.TypeToken
import com.makeramen.roundedimageview.RoundedImageView
import org.json.JSONObject

class SellerAddProductFragment : Fragment() {

    private lateinit var view: View
    private lateinit var apiCall: ApiClient

    private lateinit var planets_spinner: Spinner
    private lateinit var category_image : ImageView

    private lateinit var product_model: NewProuct

    private lateinit var product_image:                 RoundedImageView
    private lateinit var back:                          ImageView
    private lateinit var name:                          EditText
    private lateinit var description:                   EditText
    private lateinit var radioGroup:                    RadioGroup
    private lateinit var price:                         EditText
    private lateinit var peace_description_container:   ConstraintLayout
    private lateinit var peace_description:             EditText
    private lateinit var add_button:                    Button
    private          var add_button_isEnabled:          Boolean = false
    private lateinit var error_msg:                     TextView

    private fun getAllStuff(){
        apiCall                     = ApiClient(requireContext())

        planets_spinner             = view.findViewById<Spinner>(R.id.planets_spinner)
        category_image              = view.findViewById<ImageView>(R.id.category_image)

        product_model               = NewProuct()
        product_image               = view.findViewById<RoundedImageView>(R.id.product_image)

        back                        = view.findViewById<ImageView>(R.id.back)
        name                        = view.findViewById<EditText>(R.id.name)
        description                 = view.findViewById<EditText>(R.id.description)
        radioGroup                  = view.findViewById<RadioGroup>(R.id.radioGroup2)
        price                       = view.findViewById<EditText>(R.id.price)
        peace_description_container = view.findViewById<ConstraintLayout>(R.id.peace_description_container)
        peace_description           = view.findViewById<EditText>(R.id.peace_description)
        add_button                  = view.findViewById<Button>(R.id.add_button)
        error_msg                   = view.findViewById<TextView>(R.id.error_msg)

    }

    private fun setAllEvenetListener(){
        addActionForImage()
        back.setOnClickListener{findNavController().navigateUp()}

        name.addTextChangedListener(object : TextWatcher {
            override fun beforeTextChanged(s: CharSequence?, start: Int, before: Int, count: Int) {  }
            override fun onTextChanged(s: CharSequence?, start: Int, before: Int, count: Int) {}
            override fun afterTextChanged(s: Editable?) { product_model.product_name = s.toString(); if (product_model.validateForNextStep()) canAddProduct() else cantAddProduct() }
        })

        description.addTextChangedListener(object : TextWatcher {
            override fun beforeTextChanged(s: CharSequence?, start: Int, before: Int, count: Int) {  }
            override fun onTextChanged(s: CharSequence?, start: Int, before: Int, count: Int) {}
            override fun afterTextChanged(s: Editable?) { product_model.description = s.toString(); if (product_model.validateForNextStep()) canAddProduct() else cantAddProduct() }
        })

        price.addTextChangedListener(object : TextWatcher {
            override fun beforeTextChanged(s: CharSequence?, start: Int, before: Int, count: Int) {  }
            override fun onTextChanged(s: CharSequence?, start: Int, before: Int, count: Int) {}
            override fun afterTextChanged(s: Editable?) {
                try{
                    product_model.price = Integer.parseInt(s.toString())
                    if (product_model.validateForNextStep()) canAddProduct() else cantAddProduct()
                }
                catch (exception: Exception){}
            }
        })

        peace_description.addTextChangedListener(object : TextWatcher {
            override fun beforeTextChanged(s: CharSequence?, start: Int, before: Int, count: Int) {  }
            override fun onTextChanged(s: CharSequence?, start: Int, before: Int, count: Int) {}
            override fun afterTextChanged(s: Editable?) { product_model.measurement_value = s.toString(); if (product_model.validateForNextStep()) canAddProduct() else cantAddProduct() }
        })

        radioGroup.setOnCheckedChangeListener{grouup, checkedId ->
            run {
                when (checkedId) {
                    R.id.kilogram -> {
                        product_model.measurement_id = 3
                        peace_description_container.visibility = View.GONE
                        if (product_model.validateForNextStep()) canAddProduct() else cantAddProduct()
                    }

                    R.id.litar -> {
                        product_model.measurement_id = 2
                        peace_description_container.visibility = View.GONE
                        if (product_model.validateForNextStep()) canAddProduct() else cantAddProduct()
                    }

                    R.id.komad -> {
                        product_model.measurement_id = 1
                        peace_description_container.visibility = View.VISIBLE
                        if (product_model.validateForNextStep()) canAddProduct() else cantAddProduct()
                    }
                }
            }
        }
        if(product_id == -1){
            add_button.setOnClickListener{
                if(add_button_isEnabled == false){ getMoreInformationForError() }
                else{
                    error_msg.visibility = View.GONE
                    if (product_model.validateForNextStep())
                        addNewProductApi()
                    else
                        cantAddProduct()
                }
            }
        }
        else{
            add_button.text = "Sačuvaj promene"
            add_button.setBackground(resources.getDrawable(R.drawable.full_fill_button))
            add_button_isEnabled = true
            add_button.setOnClickListener{
                println("STANJE: " + product_model)
                overlayEdit()
            }
        }

    }

    private fun addNewProductApi(){
        val jsonObject: JSONObject = JSONObject()
        jsonObject.put("product_name", product_model.product_name)
        jsonObject.put("category_id", product_model.category_id)
        jsonObject.put("measurement_id", product_model.measurement_id)
        if(product_model.measurement_id == 3 && product_model.measurement_value.length > 0 )
            jsonObject.put("measurement_value", product_model.measurement_value)
        else
            jsonObject.put("measurement_value", null)
        jsonObject.put("picture",
            product_model.picture?.let { Image.uriToBase64(requireContext(), it) })
        jsonObject.put("price", product_model.price)
        jsonObject.put("description", product_model.description)
        var url:String = Config.ip_address+":"+ Config.port + "/addNewProduct"
        apiCall.sendPostRequestWithJSONObjectWithStringResponse(url, JWTService.getToken(), jsonObject,
            {response->
                val jsonObject: JSONObject = JSONObject(response)
                try{
                    if(jsonObject["message"].equals("Product added successfully.")){
                        Toast.makeText(requireContext(), "Uspešno dodat proizvod", Toast.LENGTH_SHORT).show()
                        val action = SellerProfileFragmentDirections.actionProfileSeller(-1)
                        findNavController().navigate(action)
                    }
                    else{
                        Toast.makeText(requireContext(), "Došlo je do greške", Toast.LENGTH_SHORT).show()
                    }
                }
                catch (exception: Exception){
                    Toast.makeText(requireContext(), "Došlo je do greške", Toast.LENGTH_SHORT).show()
                }
            },
            {error->
                println(error)
            }
        )

    }

    private fun setUpSpinner() {

        val items = resources.getStringArray(R.array.product_category)
        val spinnerAdapter = object :
            ArrayAdapter<String>(requireContext(), android.R.layout.simple_spinner_item, items) {

            override fun isEnabled(position: Int): Boolean {
                // Disable the first item from Spinner
                // First item will be used for hint
                return position != 0
            }

            override fun getDropDownView(
                position: Int,
                convertView: View?,
                parent: ViewGroup
            ): View {
                val view: TextView =
                    super.getDropDownView(position, convertView, parent) as TextView
                //set the color of first item in the drop down list to gray
                if (position == 0) {
                    view.setTextColor(resources.getColor(R.color.grey))
                } else {
                    //here it is possible to define color for other items by
                    view.setTextColor(resources.getColor(R.color.grey))
                }
                return view
            }
        }
        spinnerAdapter.setDropDownViewResource(android.R.layout.simple_spinner_dropdown_item)
        planets_spinner.adapter = spinnerAdapter

        planets_spinner.onItemSelectedListener = object: AdapterView.OnItemSelectedListener{
            override fun onNothingSelected(parent: AdapterView<*>?) {
            }

            override fun onItemSelected(
                parent: AdapterView<*>?,
                view: View?,
                position: Int,
                id: Long
            ) {
                val value = parent!!.getItemAtPosition(position).toString()
                if(value == items[0]){
                    (view as TextView).setTextColor(resources.getColor(R.color.grey))
                }
                else{
                    (view as TextView).setTextColor(resources.getColor(R.color.black))
                }
                when(position){
                    1->{product_model.category_id = 1 ; category_image.setImageResource(R.drawable.mlecni_proizvodi)}
                    2->{product_model.category_id = 2 ; category_image.setImageResource(R.drawable.voce_i_povrce)}
                    3->{product_model.category_id = 3 ; category_image.setImageResource(R.drawable.mesne_preradjevine)}
                    4->{product_model.category_id = 4 ; category_image.setImageResource(R.drawable.meso)}
                    5->{product_model.category_id = 5 ; category_image.setImageResource(R.drawable.zitarice)}
                    6->{product_model.category_id = 6 ; category_image.setImageResource(R.drawable.napici)}
                    7->{product_model.category_id = 7 ; category_image.setImageResource(R.drawable.biljna_ulja)}
                    8->{product_model.category_id = 8 ; category_image.setImageResource(R.drawable.namazi)}
                }
                if (product_model.validateForNextStep()) canAddProduct() else cantAddProduct()
            }

        }

    }
    private lateinit var product_view_response:  ProductViewResponse
    private fun getProductInformation(){
        var url:String = Config.ip_address+":"+ Config.port + "/getProduct/"+product_id
        apiCall.sendGetRequestEmpty(JWTService.getToken(), url,
            {response->
                val gson = Gson()
                val sellersResponse = gson.fromJson(response, ProductViewResponse::class.java)
                product_view_response = sellersResponse
                renderProduct(sellersResponse.productDTO)

            },
            {error->
                println(error)
            }
        )
    }

    private var product_id : Int = -1
    override fun onCreateView(
        inflater: LayoutInflater, container: ViewGroup?,
        savedInstanceState: Bundle?
    ): View? {
        view =  inflater.inflate(R.layout.fragment_seller_add_product, container, false)

        getAllStuff()

        try{
            val args: SellerAddProductFragmentArgs = SellerAddProductFragmentArgs.fromBundle(requireArguments())
            product_id = args.productId
        }
        catch (exception:Exception){}

        setAllEvenetListener()

        if(product_id >= 0){
            getProductInformation()
        }

        setUpSpinner()

        return view
    }

    private val PICK_IMAGE_REQUEST_PRODUCT = 1
    fun addActionForImage(){
        product_image.setOnClickListener {
            val intent = Intent(Intent.ACTION_PICK, MediaStore.Images.Media.EXTERNAL_CONTENT_URI)
            startActivityForResult(intent, PICK_IMAGE_REQUEST_PRODUCT)
        }
    }
    override fun onActivityResult(requestCode: Int, resultCode: Int, data: Intent?) {
        super.onActivityResult(requestCode, resultCode, data)

        if (resultCode == Activity.RESULT_OK && data != null && data.data != null) {
            val imageUri: Uri = data.data!!

            when (requestCode) {
                PICK_IMAGE_REQUEST_PRODUCT -> {
                    product_image.setImageURI(imageUri)
                    product_model.picture = imageUri
                    if (product_model.validateForNextStep()) canAddProduct() else cantAddProduct()
                }
            }
        }
    }

    private fun canAddProduct(){
        if(product_id == -1){
            add_button_isEnabled = true
            add_button.setBackgroundResource(R.drawable.full_fill_button)
        }
    }
    private fun cantAddProduct(){
        if(product_id == -1){
            add_button_isEnabled = false
            add_button.setBackgroundResource(R.drawable.full_fill_button_disabled)
        }
    }

    private fun getMoreInformationForError(){
        if(product_model.picture == null){ error_msg.visibility = View.VISIBLE; error_msg.text = "* Morate uneti sliku proizvoda." }
        else if(product_model.product_name.length == 0 ){ error_msg.visibility = View.VISIBLE; error_msg.text = "* Morate uneti naziv proizvoda." }
        else if(product_model.category_id == -1 ){ error_msg.visibility = View.VISIBLE; error_msg.text = "* Morate uneti kategoriju proizvoda." }
        else if(product_model.measurement_id == -1 ){ error_msg.visibility = View.VISIBLE; error_msg.text = "* Morate uneti mernu jedinicu proizvoda." }
        else if (product_model.price <= 0) { error_msg.visibility = View.VISIBLE; error_msg.text = "* Morate uneti cenu proizvoda." }
    }
    fun renderProduct(productInformation: Product){
        Image.setImageResource(product_image, productInformation.picture, productInformation.category_id)
        name.setText(productInformation.productName)
        description.setText(productInformation.description)
        planets_spinner.setSelection(productInformation.category_id)
        when(productInformation.measurement){
            "KG"->{
                radioGroup.check(R.id.kilogram)
            }
            "LITAR"->{
                radioGroup.check(R.id.litar)
            }
            "KOMAD"->{
                radioGroup.check(R.id.komad)
            }
        }
        price.setText(productInformation.price.toInt().toString())
        var remove_button = view.findViewById<Button>(R.id.remove_button)
        remove_button.visibility = View.VISIBLE
        remove_button.setOnClickListener { overlayDelete() }

    }

    fun deleteProduct(dialog: Dialog){
        var url:String = Config.ip_address+":"+ Config.port + "/delete-product?productId="+product_id

        apiCall.sendDeleteRequestWithToken(JWTService.getToken(), url,
            {response->
                var jsonObject: JSONObject = JSONObject(response)
                if(jsonObject["code"] == 200){
                    val action = SellerProfileFragmentDirections.actionProfileSeller(-1)
                    findNavController().navigate(action)
                    dialog.dismiss()
                    Toast.makeText(requireContext(), "Uspešno obrisan proizvod", Toast.LENGTH_LONG)
                }
            },
            {error->
                println(error)
            })
    }

    fun ubdateProduct(dialog: Dialog){
        var url:String = Config.ip_address+":"+ Config.port + "/edit-product?productId="+product_id
        val jsonObject: JSONObject = JSONObject()
        jsonObject.put("product_name", product_model.product_name)
        jsonObject.put("category_id", product_model.category_id)
        jsonObject.put("measurement_id", product_model.measurement_id)
        if(product_model.measurement_id == 3 && product_model.measurement_value.length > 0 )
            jsonObject.put("measurement_value", product_model.measurement_value)
        else
            jsonObject.put("measurement_value", null)
        jsonObject.put("picture",
            product_model.picture?.let { Image.uriToBase64(requireContext(), it) })
        jsonObject.put("price", product_model.price)
        jsonObject.put("description", product_model.description)
        apiCall.sendPostRequestWithJSONObjectWithStringResponse(url, JWTService.getToken(),
            jsonObject,
            {response->
                println(response)
                dialog.dismiss()
            },
            {error->
                println(error)
            })
    }

    private fun overlayDelete(){
        val dialog = Dialog(requireContext())
        dialog.requestWindowFeature(Window.FEATURE_NO_TITLE)
        dialog.setCancelable(true)
        dialog.setContentView(R.layout.overlay_alert)

        val width = (resources.displayMetrics.widthPixels * 1.0).toInt()
        dialog.window?.setLayout(width, ViewGroup.LayoutParams.WRAP_CONTENT)

        val title = dialog.findViewById<TextView>(R.id.textView43)
        title.text = "Da li ste sigurni da želite da obrišete ovaj proizvod?"
        val button_yes = dialog.findViewById<TextView>(R.id.button_yes)
        val button_no = dialog.findViewById<TextView>(R.id.button_no)

        button_yes.setOnClickListener { deleteProduct(dialog) }
        button_no.setOnClickListener{dialog.dismiss()}

        dialog.show()
    }

    private fun overlayEdit(){
        val dialog = Dialog(requireContext())
        dialog.requestWindowFeature(Window.FEATURE_NO_TITLE)
        dialog.setCancelable(true)
        dialog.setContentView(R.layout.overlay_alert)

        val width = (resources.displayMetrics.widthPixels * 1.0).toInt()
        dialog.window?.setLayout(width, ViewGroup.LayoutParams.WRAP_CONTENT)

        val title = dialog.findViewById<TextView>(R.id.textView43)
        title.text = "Da li ste sigurni da želite da sačuvate ove podatke o proizvodu?"
        val button_yes = dialog.findViewById<TextView>(R.id.button_yes)
        val button_no = dialog.findViewById<TextView>(R.id.button_no)

        button_yes.setOnClickListener { ubdateProduct(dialog) }
        button_no.setOnClickListener{dialog.dismiss()}

        dialog.show()
    }

}