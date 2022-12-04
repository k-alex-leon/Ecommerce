package com.example.ecommerce.activities

import androidx.appcompat.app.AppCompatActivity
import android.os.Bundle
import androidx.navigation.findNavController
import androidx.navigation.ui.setupWithNavController
import com.example.ecommerce.R
import com.example.ecommerce.databinding.ActivityShoppingBinding
import dagger.hilt.android.AndroidEntryPoint

@AndroidEntryPoint
class ShoppingActivity : AppCompatActivity() {

    private lateinit var binding : ActivityShoppingBinding

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        binding = ActivityShoppingBinding.inflate(layoutInflater)
        setContentView(binding.root)

        // pasa el nav para la view del activity
        val navController = findNavController(R.id.fragmentContainerShopping)
        binding.bottomNavigationShopping.setupWithNavController(navController)
    }
}