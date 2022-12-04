package com.example.ecommerce.viewmodel

import android.content.SharedPreferences
import androidx.lifecycle.ViewModel
import androidx.lifecycle.viewModelScope
import com.example.ecommerce.R
import com.example.ecommerce.util.Constants.INTRODUCTION_KEY
import com.google.firebase.auth.FirebaseAuth
import dagger.hilt.android.lifecycle.HiltViewModel
import kotlinx.coroutines.flow.MutableStateFlow
import kotlinx.coroutines.flow.StateFlow
import kotlinx.coroutines.launch
import javax.inject.Inject

@HiltViewModel
class IntroductionViewModel @Inject constructor(
    private val sharedPreferences: SharedPreferences,
    private val firebaseAuth: FirebaseAuth
) : ViewModel() {

    private val _navigate = MutableStateFlow(0)
    val mNavigate : StateFlow<Int> = _navigate

    companion object {
        const val SHOPPING_ACTIVITY = 11
        const val ACCOUNT_OPTIONS_FRAGMENT = R.id.action_introductionFragment_to_accountOptionsFragment
    }

    init {
        // guardaremos el estado del boton de bienvenida para no volver a mostrarlo
        val isButtonCLicked = sharedPreferences.getBoolean(INTRODUCTION_KEY, false)
        // revisa si el user ya tiene cuenta activa
        val user = firebaseAuth.currentUser

        // si el user esta activo lo envia directo al shopping_activity
        if(user != null){
            viewModelScope.launch {
                _navigate.emit(SHOPPING_ACTIVITY)
            }
        }else if (isButtonCLicked){
            viewModelScope.launch {
                _navigate.emit(ACCOUNT_OPTIONS_FRAGMENT)
            }
        }else Unit// no queremos hacer nada
    }

    // guarda el estado del boton al hacer click en las sharedP del disp
    fun startButtonClick(){
        sharedPreferences.edit().putBoolean(INTRODUCTION_KEY, true).apply()
    }
}