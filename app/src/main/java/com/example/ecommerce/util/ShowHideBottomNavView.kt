package com.example.ecommerce.util

import android.view.View
import androidx.fragment.app.Fragment
import com.example.ecommerce.R
import com.example.ecommerce.activities.ShoppingActivity
import com.google.android.material.bottomnavigation.BottomNavigationView

fun Fragment.hideBottomNav(){
    val bottomNavigationView =
        (activity as ShoppingActivity).findViewById<BottomNavigationView>(R.id.bottomNavigationShopping)

    bottomNavigationView.visibility = View.GONE
}

fun Fragment.showBottomNav(){
    val bottomNavigationView =
        (activity as ShoppingActivity).findViewById<BottomNavigationView>(R.id.bottomNavigationShopping)

    bottomNavigationView.visibility = View.VISIBLE
}