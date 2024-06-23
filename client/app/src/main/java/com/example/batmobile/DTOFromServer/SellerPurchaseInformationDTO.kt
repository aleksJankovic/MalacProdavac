package com.example.batmobile.DTOFromServer

data class SellerPurchaseInformationDTO(
    var orderId: Int,
    var buyerName: String,
    var buyerSurname: String,
    var buyerAddress: String?,
    var buyerEmail: String,
    var buyerPhoneNumber: String,
    var shippingMethodId: Int,
    var paymentMethodId: Int,
    var purchaseItems: List<SellerPurchaseItems>
)
data class SellerPurchaseItems(
    var productCategoryId: Int,
    var productName: String,
    var productImage: String?,
    var productPrice: Double,
    var quantity: Int,
    var measurement: String,
    var measurementValue: String?,
    var productId: Int
)
