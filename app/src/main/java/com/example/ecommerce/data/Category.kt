package com.example.ecommerce.data

sealed class Category(val category : String){
    object Chair : Category("Chair")
    object CupBoard : Category("CupBoard")
    object Furniture : Category("Furniture")
    object Accessory : Category("Accessory")
    object Table : Category("Table")
}
