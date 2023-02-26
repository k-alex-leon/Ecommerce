package com.example.ecommerce.viewmodel

import androidx.lifecycle.ViewModel
import androidx.lifecycle.viewModelScope
import com.example.ecommerce.data.CartProduct
import com.example.ecommerce.firebase.FirebaseCommon
import com.example.ecommerce.util.Resource
import com.google.firebase.auth.FirebaseAuth
import com.google.firebase.firestore.DocumentSnapshot
import com.google.firebase.firestore.FirebaseFirestore
import dagger.hilt.android.lifecycle.HiltViewModel
import kotlinx.coroutines.flow.MutableSharedFlow
import kotlinx.coroutines.flow.MutableStateFlow
import kotlinx.coroutines.flow.asSharedFlow
import kotlinx.coroutines.flow.map
import kotlinx.coroutines.launch
import javax.inject.Inject

@HiltViewModel
class CartViewModel @Inject constructor(
    private val firestore: FirebaseFirestore,
    private val auth: FirebaseAuth,
    private val firebaseCommon: FirebaseCommon
) : ViewModel() {

    private val _cartProducts = MutableStateFlow<Resource<List<CartProduct>>>(Resource.Unspecified())
    val mCartProducts = _cartProducts

    private var cartProductsDocuments = emptyList<DocumentSnapshot>()

    val mProductsPrice = mCartProducts.map {
        when(it){
            is Resource.Success -> {
                calculateTotalPrice(it.data!!)
            }
            else -> null
        }
    }


    private fun calculateTotalPrice(data: List<CartProduct>): Float {
        return data.sumByDouble { cartProduct ->
            if (cartProduct.product.offerPercentage != null){
                val remainingPricePercentage = 1f - cartProduct.product.offerPercentage
                (remainingPricePercentage * cartProduct.product.price).times(cartProduct.quantity).toDouble()
            }else {
                (cartProduct.product.price.times(cartProduct.quantity)).toDouble()
            }

        }.toFloat()
    }

    init {
        getCartProducts()
    }



    private fun getCartProducts(){
        viewModelScope.launch {
            _cartProducts.emit(Resource.Loading())
        }

        firestore.collection("user")
            .document(auth.uid!!).collection("cart").addSnapshotListener{ value, error ->
                if (error != null || value == null){
                    viewModelScope.launch {
                        _cartProducts.emit(Resource.Error(error?.message.toString()))
                    }
                }else{
                    cartProductsDocuments = value.documents

                    val cartProducts = value.toObjects(CartProduct::class.java)
                    viewModelScope.launch { _cartProducts.emit(Resource.Success(cartProducts)) }
                }


            }
    }


    fun changeQuantity(cartProduct: CartProduct, quantityChanging: FirebaseCommon.QuantityChanging){

        val index = mCartProducts.value.data?.indexOf(cartProduct)

        if (index != null && index != -1){
            val documentId = cartProductsDocuments[index].id
            when(quantityChanging){
                FirebaseCommon.QuantityChanging.INCREASE ->{
                    viewModelScope.launch { _cartProducts.emit(Resource.Loading()) }
                    increaseQuantity(documentId)
                }
                FirebaseCommon.QuantityChanging.DECREASE ->{
                    // evitar que el user baje a menos de la cantidad requerida
                    if (cartProduct.quantity == 1) {
                        viewModelScope.launch { _deleteDialog.emit(cartProduct) }
                        return
                    }

                    viewModelScope.launch { _cartProducts.emit(Resource.Loading()) }
                    decreaseQuantity(documentId)
                }
            }
        }

    }

    private fun increaseQuantity(documentId: String) {
        firebaseCommon.increaseQuantity(documentId){ result, exception ->
            if (exception != null){
                viewModelScope.launch{ _cartProducts.emit(Resource.Error(exception.message.toString()))}
            }
        }

    }

    private fun decreaseQuantity(documentId: String) {
        firebaseCommon.decreaseQuantity(documentId){ result, exception ->
            if (exception != null){
                viewModelScope.launch{ _cartProducts.emit(Resource.Error(exception.message.toString()))}
            }
        }

    }

    // dialog de alerta para el user
    private val _deleteDialog = MutableSharedFlow<CartProduct>()
    val mDeleteDialog = _deleteDialog.asSharedFlow()

    fun deleteCartProduct(cartProduct: CartProduct) {

        val index = mCartProducts.value.data?.indexOf(cartProduct)

        if (index != null && index != -1) {
            val documentId = cartProductsDocuments[index].id
            firestore.collection("user").document(auth.uid!!).collection("cart")
                .document(documentId).delete()
        }

    }









}