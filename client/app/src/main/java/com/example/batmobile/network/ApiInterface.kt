package com.example.batmobile.network

import android.content.Context
import com.android.volley.VolleyError
import org.json.JSONObject

interface ApiInterface {
    fun sendPostRequestWithParams(url:String,
                                  params:       Map<String,String>,
                                  onSuccess:    (String) ->Unit,
                                  onError:      (VolleyError) -> Unit)
    fun sendPostRequestWithToken(token: String,
                                url:String,
                                 onSuccess:    (String) ->Unit,
                                 onError:      (VolleyError) -> Unit)

    fun sendDeleteRequestWithToken(token: String,
                                 url:String,
                                 onSuccess:    (String) ->Unit,
                                 onError:      (VolleyError) -> Unit)
    fun sendPostRequestWithJSONObjectWithJsonResponse(url:String,
                                      jsonObject:   JSONObject,
                                      onSuccess:    (JSONObject) ->Unit,
                                      onError:      (VolleyError) -> Unit)
    fun sendPostRequestWithJSONObjectWithStringResponse(
        url: String,
        jsonObject: JSONObject,
        onSuccess: (String) -> Unit,
        onError: (VolleyError) -> Unit
    )
    fun sendPostRequestWithJSONObjectWithStringResponse(
        url: String,
        token: String,
        jsonObject: JSONObject,
        onSuccess: (String) -> Unit,
        onError: (VolleyError) -> Unit
    )
    fun sendGetRequestEmpty(
        url: String,
        onSuccess: (String) -> Unit,
        onError: (VolleyError) -> Unit
    )
    fun sendGetRequestEmpty(
        token:String,
        url: String,
        onSuccess: (String) -> Unit,
        onError: (VolleyError) -> Unit
    )
    fun getCoordinatesForAddress(address:       String,
                                 callback:      (Double, Double) -> Unit,
                                 errorCallback: () -> Unit)
    fun getAddressFromCoordinates(context:          Context,
                                  latitude:         Double,
                                  longitude:        Double,
                                  callback:         (String) -> Unit,
                                  errorCallback:    () -> Unit)

    fun sendPostRequestWithBody(
        url: String,
        body: String,
        onSuccess: (String) -> Unit,
        onError: (VolleyError) -> Unit
    )
    fun sendPostRequestWithBody(
        token: String,
        url: String,
        body: String,
        onSuccess: (String) -> Unit,
        onError: (VolleyError) -> Unit
    )
}