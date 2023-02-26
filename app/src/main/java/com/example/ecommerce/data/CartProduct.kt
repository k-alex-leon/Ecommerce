package com.example.ecommerce.data

import android.os.Parcelable
import kotlinx.parcelize.Parcelize

@Parcelize
data class CartProduct(
    val product: Product,
    val quantity : Int,
    val selectSize : String? = null,
    val selectColor : Int? = null
) : Parcelable {
    constructor() : this(Product(), 1, null, null)
}
