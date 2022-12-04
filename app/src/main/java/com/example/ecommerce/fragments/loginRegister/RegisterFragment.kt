package com.example.ecommerce.fragments.loginRegister

import android.os.Bundle
import android.util.Log
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import androidx.fragment.app.Fragment
import androidx.fragment.app.viewModels
import androidx.lifecycle.lifecycleScope
import androidx.navigation.fragment.findNavController
import com.example.ecommerce.R
import com.example.ecommerce.data.User
import com.example.ecommerce.databinding.FragmentRegisterBinding
import com.example.ecommerce.util.RegisterValidation
import com.example.ecommerce.util.Resource
import com.example.ecommerce.viewmodel.RegisterViewModel
import dagger.hilt.android.AndroidEntryPoint
import kotlinx.coroutines.Dispatchers
import kotlinx.coroutines.withContext

private val TAG = "RegisterFragment"
@AndroidEntryPoint
class RegisterFragment : Fragment(R.layout.fragment_register) {

    private lateinit var binding : FragmentRegisterBinding
    private val mViewModel by viewModels<RegisterViewModel>()

    override fun onCreateView(
        inflater: LayoutInflater,
        container: ViewGroup?,
        savedInstanceState: Bundle?
    ): View? {
        binding = FragmentRegisterBinding.inflate(inflater)
        return binding.root

    }

    override fun onViewCreated(view: View, savedInstanceState: Bundle?) {
        super.onViewCreated(view, savedInstanceState)

        binding.txtVDoYouHaveAnAccount.setOnClickListener {
            findNavController().navigate(R.id.action_registerFragment_to_loginFragment)
        }

        binding.apply {
            cBtnRegisterRegister.setOnClickListener {

                val user = User(
                    // trim sirve para eliminar los espacios
                    etFirstNameRegister.text.toString().trim(),
                    etLastNameRegister.text.toString().trim(),
                    etEmailRegister.text.toString().trim()

                )

                val password = etPasswordRegister.text.toString()
                mViewModel.createAccountWithEmailAndPassword(user, password)
            }
        }

        lifecycleScope.launchWhenCreated {
            mViewModel.mRegister.collect{
                when(it){
                    is Resource.Loading -> {
                        binding.cBtnRegisterRegister.startAnimation()
                    }
                    is Resource.Success -> {
                        Log.d("test", it.data.toString())
                        binding.cBtnRegisterRegister.revertAnimation()
                    }
                    is Resource.Error -> {
                        Log.d(TAG, it.message.toString())
                        binding.cBtnRegisterRegister.revertAnimation()
                    }
                    else -> Unit
                }
            }
        }

        // comprueba las validaciones de email y password

        lifecycleScope.launchWhenCreated {
            mViewModel.mValidation.collect{ validation ->
                // detecta un tipo equivocado en el email
                if (validation.email is RegisterValidation.Failed){
                    withContext(Dispatchers.Main){
                        // muestra el error en la ui
                        binding.etEmailRegister.apply {
                            requestFocus()
                            error = validation.email.message
                        }
                    }
                }
                // detecta un si existieron errores con la password
                if (validation.password is RegisterValidation.Failed){
                    withContext(Dispatchers.Main){
                        binding.etPasswordRegister.apply {
                            requestFocus()
                            error = validation.password.message
                        }
                    }
                }
            }
        }
    }

}