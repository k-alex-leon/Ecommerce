package com.example.ecommerce.viewmodel

import android.util.Log
import android.widget.Toast
import androidx.lifecycle.ViewModel
import com.example.ecommerce.data.User
import com.example.ecommerce.util.*
import com.example.ecommerce.util.Constants.USER_COLLECTION
import com.google.firebase.auth.FirebaseAuth
import com.google.firebase.firestore.FirebaseFirestore
import dagger.hilt.android.lifecycle.HiltViewModel
import kotlinx.coroutines.channels.Channel
import kotlinx.coroutines.flow.Flow
import kotlinx.coroutines.flow.MutableStateFlow
import kotlinx.coroutines.flow.receiveAsFlow
import kotlinx.coroutines.runBlocking
import javax.inject.Inject

@HiltViewModel
class RegisterViewModel @Inject constructor(
    private val firebaseAuth: FirebaseAuth,
    private val db: FirebaseFirestore
) : ViewModel() {

    private val _register = MutableStateFlow<Resource<User>>(Resource.Unspecified())
    val mRegister : Flow<Resource<User>> = _register

    private val _validation = Channel<RegisterFieldsState>()
    val mValidation = _validation.receiveAsFlow()

    fun createAccountWithEmailAndPassword(user : User, password : String){

        if (checkValidation(user, password)){

            runBlocking {
                _register.emit(Resource.Loading())
            }
            firebaseAuth.createUserWithEmailAndPassword(user.email, password)
                .addOnSuccessListener { result ->

                    result.user?.let {
                    saveUserInfo(it.uid, user)
                }

            }.addOnFailureListener {
                    fail -> _register.value = Resource.Error(fail.message.toString())
            }

        }else{
            val registerFieldState = RegisterFieldsState(
                validateEmail(user.email),
                validatePassword(password)
            )
            runBlocking {
                _validation.send(registerFieldState)
            }
        }
    }

    private fun saveUserInfo(userId: String, user: User) {
        db.collection(USER_COLLECTION)
            .document(userId)
            .set(user)
            .addOnSuccessListener {
                    _register.value = Resource.Success(user)
            }.addOnFailureListener {
                    _register.value = Resource.Error(it.message.toString())
            }
    }

    private fun checkValidation(user: User, password: String): Boolean {
        // booleans values / utiliza el validation check para validar email y pass
        val emailValidation = validateEmail(user.email)
        val passwordValidation = validatePassword(password)

        // retorna VERDADERO si los dos valores son correctos
        return emailValidation is RegisterValidation.Success && passwordValidation is RegisterValidation.Success
    }
}