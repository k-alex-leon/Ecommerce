package com.example.ecommerce.fragments.shopping

import android.os.Bundle
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import androidx.fragment.app.Fragment
import com.example.ecommerce.R
import com.example.ecommerce.adapters.HomeViewPagerAdapter
import com.example.ecommerce.databinding.FragmentHomeBinding
import com.example.ecommerce.fragments.categories.*
import com.google.android.material.tabs.TabLayoutMediator

class HomeFragment : Fragment(R.layout.fragment_home) {

    private lateinit var binding: FragmentHomeBinding

    override fun onCreateView(
        inflater: LayoutInflater,
        container: ViewGroup?,
        savedInstanceState: Bundle?
    ): View {
        binding = FragmentHomeBinding.inflate(inflater)
        return binding.root
    }

    override fun onViewCreated(view: View, savedInstanceState: Bundle?) {
        super.onViewCreated(view, savedInstanceState)

        // hacemos una lista de los fragments que se reemplazarán en el homeFragment -
        // son fragments que están dentro de un fragment :O
        val categoriesFragments = arrayListOf<Fragment>(
            MainCategoryFragment(),
            ChairFragment(),
            CupBoardFragment(),
            FurnitureFragment(),
            AccessoryFragment(),
            TableFragment()
        )

        // evita que el usuario se mueva por un scroll lateral
        binding.viewpagerHome.isUserInputEnabled = false

        val viewPager2Adapter = HomeViewPagerAdapter(categoriesFragments, childFragmentManager, lifecycle)
        binding.viewpagerHome.adapter = viewPager2Adapter
        TabLayoutMediator(binding.tabLayout, binding.viewpagerHome){ tab, position ->
            when(position){
                0 -> tab.text = "Main"
                1 -> tab.text = "Chair"
                2 -> tab.text = "Cup board"
                3 -> tab.text = "Furniture"
                4 -> tab.text = "Accessory"
                5 -> tab.text = "Table"
            }
        }.attach()

    }
}