package com.example.batmobile.models

import android.net.Uri
import android.os.Parcel
import android.os.Parcelable

data class User(
    val name: String,
    val lastname: String,
    val username: String,
    val email: String,
    val password: String,
    var vehicle: MutableMap<String, Boolean> = mutableMapOf("auto" to false, "motocikl" to false, "kombi" to false, "kamion" to false),
    var pib: String? = null,
    var latitude: Double? = null,
    var longitude: Double? = null,
    var profileImageUri: Uri? = null,
    var accountNumber: String? = null
): Parcelable {
    constructor(parcel: Parcel) : this(
        parcel.readString()!!,
        parcel.readString()!!,
        parcel.readString()!!,
        parcel.readString()!!,
        parcel.readString()!!
    ){
        val vehicleData = mutableMapOf<String, Boolean>()
        parcel.readMap(vehicleData, javaClass.classLoader)
        vehicle = vehicleData
        pib = parcel.readString()
        latitude = parcel.readDouble()
        longitude = parcel.readDouble()
        profileImageUri = parcel.readParcelable(Uri::class.java.classLoader)
        accountNumber = parcel.readString()
    }

    override fun writeToParcel(parcel: Parcel, flags: Int) {
        parcel.writeString(name)
        parcel.writeString(lastname)
        parcel.writeString(username)
        parcel.writeString(email)
        parcel.writeString(password)
        parcel.writeMap(vehicle)
        parcel.writeString(pib)
        parcel.writeDouble(latitude ?: -100.0)
        parcel.writeDouble(longitude ?: 200.0)
        parcel.writeParcelable(profileImageUri, flags)
        parcel.writeString(accountNumber)
    }

    override fun describeContents(): Int {
        return 0
    }

    companion object CREATOR : Parcelable.Creator<User> {
        override fun createFromParcel(parcel: Parcel): User {
            return User(parcel)
        }

        override fun newArray(size: Int): Array<User?> {
            return arrayOfNulls(size)
        }
    }
}
