package com.example.ecommerce.fragments.categories

import android.os.Bundle
import android.view.View
import androidx.fragment.app.viewModels
import androidx.lifecycle.lifecycleScope
import com.example.ecommerce.data.Category
import com.example.ecommerce.util.Resource
import com.example.ecommerce.viewmodel.CategoryViewModel
import com.example.ecommerce.viewmodel.factory.BaseCategoryViewModelFactory
import com.google.android.material.snackbar.Snackbar
import com.google.firebase.firestore.FirebaseFirestore
import dagger.hilt.android.AndroidEntryPoint
import javax.inject.Inject

@AndroidEntryPoint
class CupBoardFragment : BaseCategoryFragment() {

    @Inject
    lateinit var firestore : FirebaseFirestore

    val mViewModel by viewModels<CategoryViewModel> {
        BaseCategoryViewModelFactory(firestore, Category.CupBoard)
    }

    override fun onViewCreated(view: View, savedInstanceState: Bundle?) {
        super.onViewCreated(view, savedInstanceState)

        lifecycleScope.launchWhenCreated {

            mViewModel.mOfferProducts.collect{

                when(it){
                    is Resource.Loading -> {
                        // fun del baseCategoryFragment
                        showOfferLoading()
                    }
                    is Resource.Success -> {
                        hideOfferLoading()
                        mOfferAdapter.differ.submitList(it.data)
                    }
                    is Resource.Error -> {
                        // en caso de error pequeño snackbar
                        hideOfferLoading()
                        Snackbar.make(requireView(), it.message.toString(), Snackbar.LENGTH_SHORT).show()
                    }
                    else -> Unit
                }
            }
        }


        lifecycleScope.launchWhenCreated {
            mViewModel.mBestProducts.collect{

                when(it){
                    is Resource.Loading -> {
                       showOfferLoading()
                    }
                    is Resource.Success -> {
                        hideOfferLoading()
                        mBestProductAdapter.differ.submitList(it.data)
                    }
                    is Resource.Error -> {
                        hideOfferLoading()
                        Snackbar.make(requireView(), it.message.toString(), Snackbar.LENGTH_SHORT).show()
                    }

                    else -> Unit
                }
            }
        }
    }
}