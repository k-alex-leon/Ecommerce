package com.example.ecommerce.viewmodel

import androidx.lifecycle.ViewModel
import androidx.lifecycle.viewModelScope
import com.example.ecommerce.data.Product
import com.example.ecommerce.util.Resource
import com.google.firebase.firestore.FirebaseFirestore
import dagger.hilt.android.lifecycle.HiltViewModel
import kotlinx.coroutines.flow.MutableStateFlow
import kotlinx.coroutines.flow.StateFlow
import kotlinx.coroutines.launch
import javax.inject.Inject

@HiltViewModel
class MainCategoryViewModel @Inject constructor(
    private val firestore : FirebaseFirestore
) : ViewModel() {

    private val _specialProducts = MutableStateFlow<Resource<List<Product>>>(Resource.Unspecified())
    val mSpecialProducts : StateFlow<Resource<List<Product>>> = _specialProducts

    private val _bestDealsProducts = MutableStateFlow<Resource<List<Product>>>(Resource.Unspecified())
    val mBestDealsProducts : StateFlow<Resource<List<Product>>> = _bestDealsProducts

    private val _bestProducts = MutableStateFlow<Resource<List<Product>>>(Resource.Unspecified())
    val mBestProducts : StateFlow<Resource<List<Product>>> = _bestProducts

    private val pagingInfo = PagingInfo()
    init {
        fetchSpecialProducts()
        fetchBestDealsProducs()
        fetchBestProducts()
    }

    fun fetchSpecialProducts(){
        viewModelScope.launch {
            _specialProducts.emit(Resource.Loading())
        }

        firestore.collection("Products")
            .whereEqualTo("category" ,"Special products").get().addOnSuccessListener { result ->
                val specialProductsList = result.toObjects(Product::class.java)
                viewModelScope.launch {
                    _specialProducts.emit(Resource.Success(specialProductsList))
                }
            }.addOnFailureListener {
                viewModelScope.launch {
                    _specialProducts.emit(Resource.Error(it.message.toString()))
                }
            }
    }

    fun fetchBestDealsProducs(){
        viewModelScope.launch {
            _bestDealsProducts.emit(Resource.Loading())
        }

        firestore.collection("Products")
            .whereEqualTo("category", "Best deals").get().addOnSuccessListener { result ->
                val bestDealsList = result.toObjects(Product::class.java)
                viewModelScope.launch {
                    _bestDealsProducts.emit(Resource.Success(bestDealsList))
                }
            }.addOnFailureListener {
                viewModelScope.launch {
                    _bestDealsProducts.emit(Resource.Error(it.message.toString()))
                }
            }
    }

    fun fetchBestProducts(){
        // mientras sea false se seguira realizando esta consulta
        if (!pagingInfo.isPagingEnd){

            viewModelScope.launch {
                _bestProducts.emit(Resource.Loading())
            }

            // .limit determina el num de results que recibe de la bd
            firestore.collection("Products")
                .limit(pagingInfo.baseProductsPage * 10).get().addOnSuccessListener { result ->

                    val bestProductsList = result.toObjects(Product::class.java)
                    // compara la informacion obtenida de la bd / ESTA PARTE ES IMPORTANTE - evita que se consulte a fb dependiendo la info
                    pagingInfo.isPagingEnd = bestProductsList == pagingInfo.oldBestProducts
                    // si la info no es la misma se asigna al oldlist y se realiza la consulta nuevamente
                    pagingInfo.oldBestProducts = bestProductsList

                    viewModelScope.launch {
                        _bestProducts.emit(Resource.Success(bestProductsList))
                    }

                    // incrementa a medida que se hace nuevas consultas
                    pagingInfo.baseProductsPage++
                }.addOnFailureListener {
                    viewModelScope.launch {
                        _bestProducts.emit(Resource.Error(it.message.toString()))
                    }
                }

        }
    }

    // esto se encarga de la cantidad de datos pedidos a la bd -
    // dependiendo de la position del user en la pantalla y la cantidad de info obtenida
    internal data class PagingInfo(
        var baseProductsPage : Long = 1,
        var oldBestProducts : List<Product> = emptyList(),
        var isPagingEnd : Boolean = false
    )

}