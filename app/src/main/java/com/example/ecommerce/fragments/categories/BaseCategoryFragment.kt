package com.example.ecommerce.fragments.categories

import android.os.Bundle
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import androidx.core.widget.NestedScrollView
import androidx.fragment.app.Fragment
import androidx.navigation.fragment.findNavController
import androidx.recyclerview.widget.GridLayoutManager
import androidx.recyclerview.widget.LinearLayoutManager
import androidx.recyclerview.widget.RecyclerView
import com.example.ecommerce.R
import com.example.ecommerce.adapters.BestDealsAdapter
import com.example.ecommerce.adapters.BestProductAdapter
import com.example.ecommerce.databinding.FragmentBaseCategoryBinding
import com.example.ecommerce.util.showBottomNav

open class BaseCategoryFragment : Fragment(R.layout.fragment_base_category) {

    private lateinit var binding : FragmentBaseCategoryBinding
    protected val mOfferAdapter: BestProductAdapter by lazy { BestProductAdapter() }
    protected val mBestProductAdapter: BestProductAdapter by lazy { BestProductAdapter() }

    override fun onCreateView(
        inflater: LayoutInflater,
        container: ViewGroup?,
        savedInstanceState: Bundle?
    ): View? {
        binding = FragmentBaseCategoryBinding.inflate(inflater)
        return binding.root
    }

    override fun onViewCreated(view: View, savedInstanceState: Bundle?) {
        super.onViewCreated(view, savedInstanceState)

        setupOfferRv()
        setupBestProducts()


        mBestProductAdapter.onClick = {
            // "product" es la key que asignamos en el navgraph (args)
            val b = Bundle().apply { putParcelable("product", it) }
            // pasamos el producto
            findNavController().navigate(R.id.action_homeFragment_to_productDetailsFragment, b)
        }

        mOfferAdapter.onClick = {
            // "product" es la key que asignamos en el navgraph (args)
            val b = Bundle().apply { putParcelable("product", it) }
            // pasamos el producto
            findNavController().navigate(R.id.action_homeFragment_to_productDetailsFragment, b)
        }


        // dependiendo la pos del user realiza una nueva consulta para agregar data
        binding.rvOffer.addOnScrollListener(object : RecyclerView.OnScrollListener(){
            override fun onScrolled(recyclerView: RecyclerView, dx: Int, dy: Int) {
                super.onScrolled(recyclerView, dx, dy)

                // scroll horizontal
                if (!recyclerView.canScrollVertically(1) && dx != 0){
                    onOfferPagingRequest()
                }

            }
        })

        // dependiendo la posi del user realiza una nueva consulta para agregar data
        binding.nestedScrollBaseCategory.setOnScrollChangeListener(NestedScrollView.OnScrollChangeListener { v, scrollX, scrollY, oldScrollX, oldScrollY ->
            if (v.getChildAt(0).bottom <= v.height + scrollY){
                onBestProductsPagingRequest()
            }
        })

    }

    fun showOfferLoading(){
        binding.pbOffer.visibility = View.VISIBLE
    }
    fun hideOfferLoading(){
        binding.pbOffer.visibility = View.GONE
    }
    fun showBestProductsLoading(){
        binding.pbBestProductCategory.visibility = View.VISIBLE
    }
    fun hideBestProductsLoading(){
        binding.pbBestProductCategory.visibility = View.GONE
    }

    open fun onOfferPagingRequest(){}

    open fun onBestProductsPagingRequest(){}


    private fun setupOfferRv() {
        binding.rvOffer.apply {
            layoutManager = LinearLayoutManager(requireContext(), LinearLayoutManager.HORIZONTAL, false)
            adapter = mOfferAdapter
        }
    }

    private fun setupBestProducts() {
        binding.rvBestCategoryDeals.apply {
            layoutManager = GridLayoutManager(requireContext(), 2, GridLayoutManager.VERTICAL, false)
            adapter = mBestProductAdapter
        }
    }

    override fun onResume() {
        super.onResume()
        showBottomNav()
    }


    }