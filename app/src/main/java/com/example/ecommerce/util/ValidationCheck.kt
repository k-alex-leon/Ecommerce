package com.example.ecommerce.util

import android.util.Patterns

// se usara este file en register y login

fun validateEmail(email : String) : RegisterValidation{
    if (email.isEmpty())
        return RegisterValidation.Failed("Email cannot be empty")
    if (!Patterns.EMAIL_ADDRESS.matcher(email).matches())
        return RegisterValidation.Failed("Wrong email format")

    return RegisterValidation.Success
}

fun validatePassword(password : String) : RegisterValidation{
    if (password.isEmpty())
        return RegisterValidation.Failed("Email cannot be empty")

    if (password.length < 6)
        return RegisterValidation.Failed("Password should contains 6 chars")

    return RegisterValidation.Success
}