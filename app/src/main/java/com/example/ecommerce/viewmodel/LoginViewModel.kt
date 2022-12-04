package com.example.ecommerce.viewmodel

import androidx.lifecycle.ViewModel
import androidx.lifecycle.viewModelScope
import com.example.ecommerce.data.User
import com.example.ecommerce.util.*
import com.google.firebase.auth.FirebaseAuth
import com.google.firebase.auth.FirebaseUser
import dagger.hilt.android.lifecycle.HiltViewModel
import kotlinx.coroutines.flow.MutableSharedFlow
import kotlinx.coroutines.flow.asSharedFlow
import kotlinx.coroutines.launch
import kotlinx.coroutines.runBlocking
import javax.inject.Inject

@HiltViewModel
class LoginViewModel @Inject constructor(
    private val firebaseAuth : FirebaseAuth
) : ViewModel() {

    private val _login = MutableSharedFlow<Resource<FirebaseUser>>()
    val mLogin = _login.asSharedFlow()

    private val _resetPassword = MutableSharedFlow<Resource<String>>()
    val MResetPassword = _resetPassword.asSharedFlow()

    fun login(email : String, password : String){
        if (checkValidation(email, password)) {

            viewModelScope.launch { _login.emit(Resource.Loading()) }

            firebaseAuth.signInWithEmailAndPassword(email, password)
                .addOnSuccessListener {
                    viewModelScope.launch {
                        it.user?.let {
                            _login.emit(Resource.Success(it))
                        }
                    }
                }.addOnFailureListener {
                    viewModelScope.launch {
                        _login.emit(Resource.Error(it.message.toString()))
                    }
                }
        }
    }

    fun resetPassword(email: String){
        viewModelScope.launch {
            _resetPassword.emit(Resource.Loading())
        }

        firebaseAuth.sendPasswordResetEmail(email)
            .addOnSuccessListener {
                viewModelScope.launch {
                    _resetPassword.emit(Resource.Success(email))
                }
            }
            .addOnFailureListener {
                viewModelScope.launch {
                    _resetPassword.emit(Resource.Error(it.message.toString()))
                }
            }

    }

    private fun checkValidation(email: String, password: String): Boolean {
        // booleans values / utiliza el validation check para validar email y pass
        val emailValidation = validateEmail(email)
        val passwordValidation = validatePassword(password)

        // retorna VERDADERO si los dos valores son correctos
        return emailValidation is RegisterValidation.Success && passwordValidation is RegisterValidation.Success
    }
}