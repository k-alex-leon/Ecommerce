package com.example.ecommerce

import android.app.Application
import dagger.hilt.android.HiltAndroidApp

// pasamos esta clase dentro del manifest
@HiltAndroidApp
class EcommerceApplication : Application() {

}
