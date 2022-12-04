package com.example.ecommerce.fragments.loginRegister

import android.content.Intent
import android.os.Bundle
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import android.widget.Toast
import androidx.fragment.app.Fragment
import androidx.fragment.app.viewModels
import androidx.lifecycle.lifecycleScope
import androidx.navigation.fragment.findNavController
import com.example.ecommerce.R
import com.example.ecommerce.activities.ShoppingActivity
import com.example.ecommerce.databinding.FragmentLoginBinding
import com.example.ecommerce.dialog.setupBottomSheetDialog
import com.example.ecommerce.util.Resource
import com.example.ecommerce.viewmodel.LoginViewModel
import com.google.android.material.snackbar.Snackbar
import dagger.hilt.android.AndroidEntryPoint
import kotlinx.coroutines.flow.collect

@AndroidEntryPoint
class LoginFragment : Fragment(R.layout.fragment_login) {

    private lateinit var binding: FragmentLoginBinding
    private val mViewModel by viewModels<LoginViewModel>()

    override fun onCreateView(
        inflater: LayoutInflater,
        container: ViewGroup?,
        savedInstanceState: Bundle?
    ): View? {
        binding = FragmentLoginBinding.inflate(inflater)
        return binding.root
    }

    override fun onViewCreated(view: View, savedInstanceState: Bundle?) {
        super.onViewCreated(view, savedInstanceState)

        binding.txtVDontHaveAccount.setOnClickListener {
            findNavController().navigate(R.id.action_loginFragment_to_registerFragment)
        }

        binding.apply {
            cBtnLoginLogin.setOnClickListener {
                val email = etEmailLogin.text.toString().trim()
                val password = etPasswordLogin.text.toString()
                mViewModel.login(email, password)
            }
        }

        binding.txtVForgotPasswordLogin.setOnClickListener {
            setupBottomSheetDialog { email ->
                mViewModel.resetPassword(email)
            }
        }

        lifecycleScope.launchWhenStarted {
            mViewModel.MResetPassword.collect{
                when(it){
                    is Resource.Loading -> {}
                    is Resource.Success -> {
                        Snackbar.make(requireView(), "Reset link was send to your email", Snackbar.LENGTH_LONG).show()
                    }
                    is Resource.Error -> {
                        // si algo sale mal :s
                        Snackbar.make(requireView(), "Error: ${it.message.toString()}", Snackbar.LENGTH_LONG).show()
                    }
                    else -> Unit
                }
            }
        }

        lifecycleScope.launchWhenStarted {
            mViewModel.mLogin.collect {
                when(it){
                    is Resource.Loading -> {
                        // empieza la animacion de carga
                        binding.cBtnLoginLogin.startAnimation()
                    }
                    is Resource.Success -> {
                        // detiene la anim
                        binding.cBtnLoginLogin.revertAnimation()
                        // enviamos al user al activity principal
                        Intent(requireContext(), ShoppingActivity::class.java).also { intent ->
                            intent.addFlags(Intent.FLAG_ACTIVITY_CLEAR_TASK or Intent.FLAG_ACTIVITY_NEW_TASK)
                            startActivity(intent)
                        }
                    }
                    is Resource.Error -> {
                        binding.cBtnLoginLogin.revertAnimation()
                        // si algo sale mal :s
                        Toast.makeText(requireContext(), it.message, Toast.LENGTH_LONG).show()
                    }
                    else -> Unit
                }
            }
        }
    }
}