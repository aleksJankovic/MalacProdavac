package com.example.batmobile.network

import android.content.Context
import android.location.Geocoder
import com.android.volley.Request
import com.android.volley.RequestQueue
import com.android.volley.Response
import com.android.volley.VolleyError
import com.android.volley.toolbox.StringRequest
import com.android.volley.toolbox.Volley
import com.android.volley.toolbox.JsonObjectRequest
import okhttp3.OkHttpClient
import org.json.JSONArray
import org.json.JSONException
import org.json.JSONObject
import java.io.IOException
import java.lang.reflect.Method
import java.nio.charset.Charset

class ApiClient(private val context: Context): ApiInterface {

    private val queue: RequestQueue = Volley.newRequestQueue(context)
    override fun sendPostRequestWithParams(
        url: String,
        params: Map<String, String>,
        onSuccess: (String) -> Unit,
        onError: (VolleyError) -> Unit
    )
    {
        val stringRequest = object: StringRequest(
            Request.Method.POST,
            url,
            Response.Listener<String> { response ->
                onSuccess(response)
            },
            Response.ErrorListener { error ->
                onError(error)
            })
            {
                override fun getParams(): MutableMap<String, String> {
                    return params.toMutableMap()
                }
            }
        queue.add(stringRequest)
    }

    override fun sendPostRequestWithToken(
        token: String,
        url: String,
        onSuccess: (String) -> Unit,
        onError: (VolleyError) -> Unit
    )
    {
        val stringRequest = object: StringRequest(
            Request.Method.POST,
            url,
            Response.Listener<String> { response ->
                onSuccess(response)
            },
            Response.ErrorListener { error ->
                onError(error)
            })
            {
            override fun getHeaders(): MutableMap<String, String> {
                val headers = HashMap<String, String>()
                headers["Authorization"] = token
                return headers
            }
            }
        queue.add(stringRequest)
    }

    override fun sendDeleteRequestWithToken(
        token: String,
        url: String,
        onSuccess: (String) -> Unit,
        onError: (VolleyError) -> Unit
    ) {
        val stringRequest = object: StringRequest(
            Request.Method.DELETE,
            url,
            Response.Listener<String> { response ->
                onSuccess(response)
            },
            Response.ErrorListener { error ->
                onError(error)
            })
        {
            override fun getHeaders(): MutableMap<String, String> {
                val headers = HashMap<String, String>()
                headers["Authorization"] = token
                return headers
            }
        }
        queue.add(stringRequest)
    }

    override fun sendPostRequestWithJSONObjectWithJsonResponse(
        url: String,
        jsonObject: JSONObject,
        onSuccess: (JSONObject) -> Unit,
        onError: (VolleyError) -> Unit
    ) {
        val jsonObjectRequest = JsonObjectRequest(
            Request.Method.POST,
            url,
            jsonObject,
            {response -> onSuccess(response)},
            {error -> onError(error)}
        )
        queue.add(jsonObjectRequest)
    }

    override fun sendPostRequestWithJSONObjectWithStringResponse(
        url: String,
        jsonObject: JSONObject,
        onSuccess: (String) -> Unit,
        onError: (VolleyError) -> Unit
    ) {
        val request = object : StringRequest(
            Method.POST,
            url,
            Response.Listener<String> { response -> onSuccess(response) },
            Response.ErrorListener { error -> onError(error) }
        ) {
            override fun getBodyContentType(): String {
                // Postavite Content-Type zaglavlje na application/json
                return "application/json"
            }

            override fun getBody(): ByteArray {
                // Postavite JSON podatke kao body zahteva
                return jsonObject.toString().toByteArray(Charset.forName("UTF-8"))
            }

            override fun getHeaders(): MutableMap<String, String> {
                // Postavite zaglavlja ako je potrebno (na primer, token za autentifikaciju)
                val headers = HashMap<String, String>()
                headers["Content-Type"] = "application/json"
                return headers
            }
        }
        queue.add(request)
    }

    override fun sendPostRequestWithJSONObjectWithStringResponse(
        url: String,
        token: String,
        jsonObject: JSONObject,
        onSuccess: (String) -> Unit,
        onError: (VolleyError) -> Unit
    ) {
        val request = object : StringRequest(
            Method.POST,
            url,
            Response.Listener<String> { response -> onSuccess(response) },
            Response.ErrorListener { error -> onError(error) }
        ) {
            override fun getBodyContentType(): String {
                // Postavite Content-Type zaglavlje na application/json
                return "application/json"
            }

            override fun getBody(): ByteArray {
                // Postavite JSON podatke kao body zahteva
                return jsonObject.toString().toByteArray(Charset.forName("UTF-8"))
            }

            override fun getHeaders(): MutableMap<String, String> {
                // Postavite zaglavlja ako je potrebno (na primer, token za autentifikaciju)
                val headers = HashMap<String, String>()
                headers["Content-Type"] = "application/json"
                headers["Authorization"] = token
                return headers
            }
        }
        queue.add(request)
    }

    override fun getCoordinatesForAddress(
        address: String,
        callback: (Double, Double) -> Unit,
        errorCallback: () -> Unit
    ) {
        val client = OkHttpClient()
        val baseUrl = "https://nominatim.openstreetmap.org/search"
        val formattedAddress = address.replace(" ", "+")
        val url = "$baseUrl?q=$formattedAddress&format=json"

        val request = okhttp3.Request.Builder()
            .url(url)
            .build()

        client.newCall(request).enqueue(object : okhttp3.Callback {
            override fun onResponse(call: okhttp3.Call, response: okhttp3.Response) {
                try {
                    val jsonResponse = response.body?.string()
                    val jsonArray = JSONArray(jsonResponse)
                    if (jsonArray.length() > 0) {
                        val firstResult = jsonArray.getJSONObject(0)
                        val latitude = firstResult.getString("lat").toDouble()
                        val longitude = firstResult.getString("lon").toDouble()
                        callback(latitude, longitude)
                    } else {
                        errorCallback()
                    }
                } catch (e: JSONException) {
                    errorCallback()
                }
            }

            override fun onFailure(call: okhttp3.Call, e: IOException) {
                errorCallback()
            }
        })
    }

    override fun getAddressFromCoordinates(
        context: Context,
        latitude: Double,
        longitude: Double,
        callback: (String) -> Unit,
        errorCallback: () -> Unit
    ) {
        val geocoder = Geocoder(context)
        val location = android.location.Location("")
        location.latitude = latitude
        location.longitude = longitude

        try {
            val addresses = geocoder.getFromLocation(location.latitude, location.longitude, 1)
            if (addresses != null) {
                if (addresses.isNotEmpty()) {
                    val address = addresses[0]
                    val city = address.locality ?: ""
                    val street = address.thoroughfare ?: ""
                    val fullAddress = "$city, $street"
                    callback(fullAddress)
                } else {
                    errorCallback()
                }
            }
        } catch (e: Exception) {
            errorCallback()
        }
    }
    override fun sendGetRequestEmpty(
        url: String,
        onSuccess: (String) -> Unit,
        onError: (VolleyError) -> Unit
    ){
        val request = StringRequest(
            Request.Method.GET,
            url,
            { response -> onSuccess(String(response.toByteArray(Charsets.ISO_8859_1), Charsets.UTF_8)) },
            { error -> onError(error) }
        )
        queue.add(request)
    }

    override fun sendGetRequestEmpty(
        token: String,
        url: String,
        onSuccess: (String) -> Unit,
        onError: (VolleyError) -> Unit
    ) {
        val request = object:StringRequest(
            Request.Method.GET,
            url,
            { response -> onSuccess(String(response.toByteArray(Charsets.ISO_8859_1), Charsets.UTF_8)) },
            { error -> onError(error) }
        ){
            override fun getHeaders(): MutableMap<String, String> {
                val headers = HashMap<String, String>()
                headers["Authorization"] = token
                return headers
            }
        }
        queue.add(request)
    }

    override fun sendPostRequestWithBody(
        url: String,
        body: String,
        onSuccess: (String) -> Unit,
        onError: (VolleyError) -> Unit
    ) {
        val request = object : StringRequest(
            Method.POST,
            url,
            { response -> onSuccess(response) },
            { error -> onError(error) }
        ) {
            override fun getBody(): ByteArray {
                return body.toByteArray(Charsets.UTF_8)
            }

            override fun getBodyContentType(): String {
                return "application/json; charset=utf-8"
            }
        }
        queue.add(request)
    }
    override fun sendPostRequestWithBody(
        token: String,
        url: String,
        body: String,
        onSuccess: (String) -> Unit,
        onError: (VolleyError) -> Unit
    ) {
        val request = object : StringRequest(
            Method.POST,
            url,
            { response -> onSuccess(response) },
            { error -> onError(error) }
        ) {
            override fun getBody(): ByteArray {
                // Postavite JSON podatke kao body zahteva
                return body.toString().toByteArray(Charset.forName("UTF-8"))
            }
            override fun getBodyContentType(): String {
                return "application/json; charset=utf-8"
            }
            override fun getHeaders(): MutableMap<String, String> {
                val headers = HashMap<String, String>()
                headers["Authorization"] = token
                return headers
            }
        }
        queue.add(request)
    }

}