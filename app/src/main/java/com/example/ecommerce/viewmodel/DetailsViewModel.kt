package com.example.ecommerce.viewmodel

import androidx.lifecycle.ViewModel
import androidx.lifecycle.viewModelScope
import com.example.ecommerce.data.CartProduct
import com.example.ecommerce.data.Product
import com.example.ecommerce.firebase.FirebaseCommon
import com.example.ecommerce.util.Resource
import com.google.firebase.auth.FirebaseAuth
import com.google.firebase.firestore.FirebaseFirestore
import dagger.hilt.android.lifecycle.HiltViewModel
import kotlinx.coroutines.flow.MutableStateFlow
import kotlinx.coroutines.launch
import javax.inject.Inject

@HiltViewModel
class DetailsViewModel @Inject constructor(
    private val firestore: FirebaseFirestore,
    private val firebaseAuth: FirebaseAuth,
    private val firebaseCommon: FirebaseCommon
) : ViewModel() {

    private val _addToCart = MutableStateFlow<Resource<CartProduct>>(Resource.Unspecified())
    val mAddToCart = _addToCart

    // realizamos validaciones antes de agregar el proucto al cart
    fun addUpdateProductInCart(cartProduct: CartProduct){

        // cambiamos el estado
        viewModelScope.launch { _addToCart.emit(Resource.Loading()) }

        firestore.collection("user").document(firebaseAuth.uid!!).collection("cart")
            .whereEqualTo("product.id", cartProduct.product.id)
            .get().addOnSuccessListener {
                it.documents.let {
                    if (it.isEmpty()){
                        // no existe el producto en el cart del user
                        addNewProduct(cartProduct)

                    }else{
                        // obtenemos producto para incrementar su cantidad
                        val product = it.first().toObject(CartProduct::class.java)
                        if (product == cartProduct){
                            // incrementar la cantidad
                            val documentId = it.first().id
                            increaseQuantity(documentId, cartProduct)

                        }else{
                            // agregar nuevo producto
                            addNewProduct(cartProduct)
                        }
                    }
                }
            }.addOnFailureListener {
                viewModelScope.launch { _addToCart.emit(Resource.Error(it.message.toString())) }
            }
    }

    private fun addNewProduct(cartProduct: CartProduct){
        firebaseCommon.addProductToCart(cartProduct){ addedProduct, e ->
            viewModelScope.launch {
                if (e == null)
                    _addToCart.emit(Resource.Success(addedProduct!!))
                else
                    _addToCart.emit(Resource.Error(e.message.toString()))
            }
        }
    }

    private fun increaseQuantity(documentId : String, cartProduct: CartProduct){
        firebaseCommon.increaseQuantity(documentId){ _ , e ->
            viewModelScope.launch {
                if (e == null)
                    _addToCart.emit(Resource.Success(cartProduct))
                else
                    _addToCart.emit(Resource.Error(e.message.toString()))
            }
        }
    }
}