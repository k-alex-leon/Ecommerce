package com.example.ecommerce.fragments.categories

import android.os.Bundle
import android.util.Log
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import android.widget.Toast
import androidx.core.widget.NestedScrollView
import androidx.fragment.app.Fragment
import androidx.fragment.app.viewModels
import androidx.lifecycle.lifecycleScope
import androidx.recyclerview.widget.GridLayoutManager
import androidx.recyclerview.widget.LinearLayoutManager
import com.example.ecommerce.R
import com.example.ecommerce.adapters.BestDealsAdapter
import com.example.ecommerce.adapters.BestProductAdapter
import com.example.ecommerce.adapters.SpecialProductsAdapter
import com.example.ecommerce.databinding.FragmentMainCategoryBinding
import com.example.ecommerce.util.Resource
import com.example.ecommerce.viewmodel.MainCategoryViewModel
import dagger.hilt.android.AndroidEntryPoint

private val TAG = "MainCategoryFragment"
@AndroidEntryPoint
class MainCategoryFragment : Fragment(R.layout.fragment_main_category) {

    private lateinit var binding : FragmentMainCategoryBinding
    private lateinit var mSpecialProducsAdapter : SpecialProductsAdapter
    private lateinit var mBestDealsAdapter : BestDealsAdapter
    private lateinit var mBestProductAdapter: BestProductAdapter
    private val mViewModel by viewModels<MainCategoryViewModel>()

    override fun onCreateView(
        inflater: LayoutInflater,
        container: ViewGroup?,
        savedInstanceState: Bundle?
    ): View? {
        binding = FragmentMainCategoryBinding.inflate(inflater)
        return binding.root
    }

    override fun onViewCreated(view: View, savedInstanceState: Bundle?) {
        super.onViewCreated(view, savedInstanceState)

        setupSpecialProductsRv()
        setupBestDealsRv()
        setupBestProductsRv()

        // SPECIAL PRODUCTS
        lifecycleScope.launchWhenCreated {
            mViewModel.mSpecialProducts.collect{
                when(it){
                    is Resource.Loading ->{
                        showLoading()
                    }
                    is Resource.Success -> {
                        mSpecialProducsAdapter.differ.submitList(it.data)
                        hideLoading()
                    }
                    is Resource.Error -> {
                        hideLoading()
                        Log.e(TAG, it.message.toString())
                        Toast.makeText(requireContext(), it.message, Toast.LENGTH_SHORT).show()
                    }
                    else -> Unit
                }
            }
        }

        // BEST DEALS
        lifecycleScope.launchWhenCreated {
            mViewModel.mBestDealsProducts.collect{
                when(it){
                    is Resource.Loading ->{
                        showLoading()
                    }
                    is Resource.Success -> {
                        mBestDealsAdapter.differ.submitList(it.data)
                        hideLoading()
                    }
                    is Resource.Error -> {
                        hideLoading()
                        Log.e(TAG, it.message.toString())
                        Toast.makeText(requireContext(), it.message, Toast.LENGTH_SHORT).show()
                    }
                    else -> Unit
                }
            }
        }

        // BEST PRODUCTS
        lifecycleScope.launchWhenCreated {
            mViewModel.mBestProducts.collect{
                when(it){
                    is Resource.Loading ->{
                        binding.pbBestProduct.visibility = View.VISIBLE
                    }
                    is Resource.Success -> {
                        mBestProductAdapter.differ.submitList(it.data)
                        binding.pbBestProduct.visibility = View.GONE
                    }
                    is Resource.Error -> {
                        binding.pbBestProduct.visibility = View.GONE
                        Log.e(TAG, it.message.toString())
                        Toast.makeText(requireContext(), it.message, Toast.LENGTH_SHORT).show()
                    }
                    else -> Unit
                }
            }
        }

        // comprobando la posicion del scrollview
        binding.nScrollMainCategory
            .setOnScrollChangeListener(NestedScrollView.OnScrollChangeListener{ v, _ , scrollY, _, _ ->
            // si el user se encuentra en la parte inferior
                if (v.getChildAt(0).bottom <= v.height + scrollY){
                    mViewModel.fetchBestProducts()
                }
        })
    }


    private fun hideLoading() {
        binding.pbMainCategoryFragment.visibility = View.GONE
    }

    private fun showLoading() {
        binding.pbMainCategoryFragment.visibility = View.VISIBLE
    }

    private fun setupSpecialProductsRv() {
        mSpecialProducsAdapter = SpecialProductsAdapter()
        binding.rvSpecialProducts.apply {
            layoutManager = LinearLayoutManager(requireContext(), LinearLayoutManager.HORIZONTAL, false)
            adapter = mSpecialProducsAdapter
        }
    }

    private fun setupBestDealsRv() {
        mBestDealsAdapter = BestDealsAdapter()
        binding.rvBestDeals.apply {
            layoutManager = LinearLayoutManager(requireContext(), LinearLayoutManager.HORIZONTAL, false)
            adapter = mBestDealsAdapter
        }
    }

    private fun setupBestProductsRv() {
        mBestProductAdapter = BestProductAdapter()
        binding.rvBestProducts.apply {
            layoutManager = GridLayoutManager(requireContext(), 2, GridLayoutManager.VERTICAL, false)
            adapter = mBestProductAdapter
        }
    }

}