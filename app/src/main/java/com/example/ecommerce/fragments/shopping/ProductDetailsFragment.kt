package com.example.ecommerce.fragments.shopping

import android.annotation.SuppressLint
import android.graphics.drawable.ColorDrawable
import android.os.Bundle
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import android.widget.Toast
import androidx.compose.ui.graphics.Color
import androidx.core.view.size
import androidx.fragment.app.Fragment
import androidx.fragment.app.viewModels
import androidx.lifecycle.lifecycleScope
import androidx.lifecycle.viewmodel.compose.viewModel
import androidx.navigation.fragment.findNavController
import androidx.navigation.fragment.navArgs
import androidx.recyclerview.widget.LinearLayoutManager
import com.example.ecommerce.R
import com.example.ecommerce.activities.ShoppingActivity
import com.example.ecommerce.adapters.ColorsAdapter
import com.example.ecommerce.adapters.SizesAdapter
import com.example.ecommerce.adapters.ViewPager2ImagesAdapter
import com.example.ecommerce.data.CartProduct
import com.example.ecommerce.databinding.FragmentProductDetailsBinding
import com.example.ecommerce.util.Resource
import com.example.ecommerce.util.hideBottomNav
import com.example.ecommerce.viewmodel.DetailsViewModel
import com.google.android.material.bottomnavigation.BottomNavigationView
import dagger.hilt.android.AndroidEntryPoint
import kotlinx.coroutines.flow.collect

@AndroidEntryPoint
class ProductDetailsFragment: Fragment() {

    // obtiene args requeridos en el navigation de shopping
    private val args by navArgs<ProductDetailsFragmentArgs>()

    private lateinit var binding : FragmentProductDetailsBinding

    private val viewPagerAdapter by lazy { ViewPager2ImagesAdapter() }
    private val sizesAdapter by lazy{ SizesAdapter() }
    private val colorsAdapter by lazy{ ColorsAdapter() }
    private var selectedColor : Int? = null
    private var selectedSize : String? = null
    private val viewModel by viewModels<DetailsViewModel>()


    override fun onCreateView(
        inflater: LayoutInflater,
        container: ViewGroup?,
        savedInstanceState: Bundle?
    ): View? {

        // oculta el bottomNavView desde el file en utils
        hideBottomNav()

        binding = FragmentProductDetailsBinding.inflate(inflater)
        return binding.root
    }

    @SuppressLint("SetTextI18n")
    override fun onViewCreated(view: View, savedInstanceState: Bundle?) {
        super.onViewCreated(view, savedInstanceState)

        // obtenemos el producto almacenado en los argumentos
        val product = args.product

        setupSizesRv()
        setupColorsRv()
        setupViewPager()

        binding.imgVCloseProductDetails.setOnClickListener {
            //navigateUp retorna al fragment anterior
            findNavController().navigateUp()
        }

        sizesAdapter.onItemClick = {
            selectedSize = it
        }

        colorsAdapter.onItemClick = {
         selectedColor = it
        }

        binding.btnAddToCart.setOnClickListener {
        // TODO validar la seleccion de color y tamaño antes de agregar al carrito
            viewModel.addUpdateProductInCart(CartProduct(product, 1, selectedSize, selectedColor))
        }

        lifecycleScope.launchWhenCreated {
            viewModel.mAddToCart.collect{
                when(it){
                    is Resource.Loading -> {
                        // TODO mejorar esta parte del codigo (hacer una pequena fun)
                        binding.pbProductDetails.visibility = View.VISIBLE
                        binding.btnAddToCart.isEnabled = false
                        binding.btnAddToCart.setBackgroundColor(resources.getColor(R.color.g_gray700))
                    }
                    is Resource.Success -> {
                        binding.pbProductDetails.visibility = View.GONE
                        binding.btnAddToCart.isEnabled = true
                        binding.btnAddToCart.setBackgroundColor(resources.getColor(R.color.g_blue))
                        Toast.makeText(requireContext(), "Product added successfully", Toast.LENGTH_LONG).show()
                    }
                    is Resource.Error -> {
                        binding.pbProductDetails.visibility = View.GONE
                        binding.btnAddToCart.isEnabled = true
                        binding.btnAddToCart.setBackgroundColor(resources.getColor(R.color.g_blue))
                        Toast.makeText(requireContext(), "Cannot be added!", Toast.LENGTH_LONG).show()
                    }
                    else -> Unit
                }
            }
        }

        binding.apply {
            txtVProductDetailName.text = product.name
            txtVProductDetailPrice.text = "$ ${product.price}"
            txtVDescriptionProductDetail.text = product.description
            if (product.colors.isNullOrEmpty()){
                txtVColorsTitle.visibility = View.GONE
            }
            if (product.sizes.isNullOrEmpty()){
                txtVSizeTitle.visibility = View.GONE
            }
        }

        // actualizando listas de adapters
        viewPagerAdapter.differ.submitList(product.images)
        product.colors?.let {
            colorsAdapter.differ.submitList(it)
        }

        product.sizes?.let {
            sizesAdapter.differ.submitList(it)
        }
    }

    private fun setupSizesRv() {
        binding.rvSizesProductDetail.apply {
            adapter = sizesAdapter
            layoutManager = LinearLayoutManager(requireContext(), LinearLayoutManager.HORIZONTAL, false)
        }
    }

    private fun setupColorsRv() {
        binding.rvColorsProductDetail.apply {
            adapter = colorsAdapter
            layoutManager = LinearLayoutManager(requireContext(), LinearLayoutManager.HORIZONTAL, false)
        }
    }

    private fun setupViewPager() {
        binding.apply {
            viewPagerProductImages.adapter = viewPagerAdapter
        }
    }
}