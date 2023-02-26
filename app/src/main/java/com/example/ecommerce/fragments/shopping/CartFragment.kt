package com.example.ecommerce.fragments.shopping

import android.annotation.SuppressLint
import android.os.Bundle
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import android.widget.Toast
import androidx.fragment.app.Fragment
import androidx.fragment.app.activityViewModels
import androidx.lifecycle.lifecycleScope
import androidx.navigation.fragment.findNavController
import androidx.recyclerview.widget.LinearLayoutManager
import androidx.recyclerview.widget.RecyclerView
import com.example.ecommerce.R
import com.example.ecommerce.adapters.CartProductAdapter
import com.example.ecommerce.databinding.FragmentCartBinding
import com.example.ecommerce.dialog.setupDeleteItemDialog
import com.example.ecommerce.firebase.FirebaseCommon
import com.example.ecommerce.util.Resource
import com.example.ecommerce.viewmodel.CartViewModel
import kotlinx.coroutines.flow.collectLatest

class CartFragment : Fragment(R.layout.fragment_cart) {

    private lateinit var binding : FragmentCartBinding
    private val mCartAdapter by lazy { CartProductAdapter() }
    private val mViewModel by activityViewModels<CartViewModel>()

    override fun onCreateView(
        inflater: LayoutInflater,
        container: ViewGroup?,
        savedInstanceState: Bundle?
    ): View? {
        binding = FragmentCartBinding.inflate(inflater)
        return binding.root
    }


    @SuppressLint("SetTextI18n")
    override fun onViewCreated(view: View, savedInstanceState: Bundle?) {
        super.onViewCreated(view, savedInstanceState)

        setupCartRv()

        var totalPrice = 0f

        // obtenemos el total-price del carrito de compras
        lifecycleScope.launchWhenStarted {
            mViewModel.mProductsPrice.collectLatest { price ->
                price?.let {
                    // formato al coste total
                    totalPrice = it
                    binding.txtVTotalPrice.text = "$ ${String.format("%.2f", price)}"
                }
            }
        }



        binding.btnPay.setOnClickListener {
            // navegando al billingFragment
            val action = CartFragmentDirections
                .actionCartFragmentToBillingFragment(totalPrice, mCartAdapter.differ.currentList.toTypedArray())
            findNavController().navigate(action)
        }



        // navega al detalle del producto
        mCartAdapter.onProductClick = {
            val b = Bundle().apply { putParcelable("product", it.product) }
            findNavController().navigate(R.id.action_cartFragment_to_productDetailsFragment, b)
        }

        // INCREASE / DECREASE

        mCartAdapter.onPlusClick = {
            mViewModel.changeQuantity(it, FirebaseCommon.QuantityChanging.INCREASE)
        }

        mCartAdapter.onMinusClick = {
            mViewModel.changeQuantity(it, FirebaseCommon.QuantityChanging.DECREASE)
        }

        // preparados para el evento del alert-dialog
        lifecycleScope.launchWhenStarted {
            mViewModel.mDeleteDialog.collectLatest {
                // custom dialog para eliminar item del cart
                setupDeleteItemDialog {
                    mViewModel.deleteCartProduct(it)
                }
            }
        }

        // lista de productos almacenados en el carrito de compra
        lifecycleScope.launchWhenStarted {
            mViewModel.mCartProducts.collectLatest {
                when(it){
                    is Resource.Loading -> {
                        binding.pbCart.visibility = View.VISIBLE
                    }
                    is Resource.Success -> {
                        binding.pbCart.visibility = View.INVISIBLE

                        // de estar vacio mostramos cambia el estado del img
                        if (it.data!!.isEmpty()) binding.imgVEmptyCart.visibility = View.VISIBLE
                        else {
                            binding.imgVEmptyCart.visibility = View.GONE
                            mCartAdapter.differ.submitList(it.data)
                        }
                    }
                    is Resource.Error -> {
                        binding.pbCart.visibility = View.INVISIBLE
                        Toast.makeText(requireContext(), it.message, Toast.LENGTH_LONG).show()

                    }
                    else -> Unit
                }
            }
        }
    }

    private fun setupCartRv() {
        binding.rvCart.apply {
            layoutManager = LinearLayoutManager(requireContext(), RecyclerView.VERTICAL, false)
            adapter = mCartAdapter
        }
    }

}