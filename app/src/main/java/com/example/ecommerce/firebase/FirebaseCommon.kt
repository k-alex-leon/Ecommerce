package com.example.ecommerce.firebase

import com.example.ecommerce.data.CartProduct
import com.google.firebase.auth.FirebaseAuth
import com.google.firebase.firestore.FirebaseFirestore

class FirebaseCommon(
    private val firestore: FirebaseFirestore,
    private val auth: FirebaseAuth
) {

    private val cartCollection = firestore.collection("user").document(auth.uid!!).collection("cart")

    // onResult se encarga de informar el estado de la fun
    fun addProductToCart(cartProduct: CartProduct, onResult : (CartProduct?, Exception?) -> Unit){
        cartCollection.document().set(cartProduct).addOnSuccessListener {
            onResult(cartProduct, null)
        }.addOnFailureListener {
            onResult(null, it)
        }
    }

    // incrementar elementos
    fun increaseQuantity(documentId : String, onResult: (String?, Exception?) -> Unit){
        // runTransaction permite leer y editar al mismo tiempo
        firestore.runTransaction{ transaction ->

            val documentRef = cartCollection.document(documentId)
            val document = transaction.get(documentRef)
            val productObject = document.toObject(CartProduct::class.java)

            productObject?.let { cartProduct ->
                val newQuantity = cartProduct.quantity + 1
                // copy nos permite generar una copia y asi editar libremente elementos del objeto
                val newProductObject = cartProduct.copy(quantity = newQuantity)
                transaction.set(documentRef, newProductObject)
            }


        }.addOnSuccessListener {
            onResult(documentId, null)
        }.addOnFailureListener {
            onResult(null, it)
        }
    }

    // restar la cantidad de elementos
    fun decreaseQuantity(documentId : String, onResult: (String?, Exception?) -> Unit){
        // runTransaction permite leer y editar al mismo tiempo
        firestore.runTransaction{ transaction ->

            val documentRef = cartCollection.document(documentId)
            val document = transaction.get(documentRef)
            val productObject = document.toObject(CartProduct::class.java)

            productObject?.let { cartProduct ->
                val newQuantity = cartProduct.quantity - 1
                // copy nos permite generar una copia y asi editar libremente elementos del objeto
                val newProductObject = cartProduct.copy(quantity = newQuantity)
                transaction.set(documentRef, newProductObject)
            }


        }.addOnSuccessListener {
            onResult(documentId, null)
        }.addOnFailureListener {
            onResult(null, it)
        }
    }

    enum class QuantityChanging{
        INCREASE, DECREASE
    }
}