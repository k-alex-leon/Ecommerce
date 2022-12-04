package com.example.ecommerce.fragments.loginRegister

import android.content.Intent
import android.os.Bundle
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import androidx.fragment.app.Fragment
import androidx.fragment.app.viewModels
import androidx.lifecycle.lifecycleScope
import androidx.navigation.fragment.findNavController
import com.example.ecommerce.R
import com.example.ecommerce.activities.ShoppingActivity
import com.example.ecommerce.databinding.FragmentIntroductionBinding
import com.example.ecommerce.viewmodel.IntroductionViewModel
import com.example.ecommerce.viewmodel.IntroductionViewModel.Companion.ACCOUNT_OPTIONS_FRAGMENT
import com.example.ecommerce.viewmodel.IntroductionViewModel.Companion.SHOPPING_ACTIVITY
import dagger.hilt.android.AndroidEntryPoint
import kotlinx.coroutines.flow.collect

@AndroidEntryPoint
class IntroductionFragment : Fragment(R.layout.fragment_introduction) {
    private lateinit var binding : FragmentIntroductionBinding
    private val mViewModel by viewModels<IntroductionViewModel>()

    override fun onCreateView(
        inflater: LayoutInflater,
        container: ViewGroup?,
        savedInstanceState: Bundle?
    ): View? {
        binding = FragmentIntroductionBinding.inflate(inflater)
        return binding.root
    }

    override fun onViewCreated(view: View, savedInstanceState: Bundle?) {
        super.onViewCreated(view, savedInstanceState)

        lifecycleScope.launchWhenStarted {
            mViewModel.mNavigate.collect{
                when(it){
                    SHOPPING_ACTIVITY -> {
                        Intent(requireContext(), ShoppingActivity::class.java).also { intent ->
                            intent.addFlags(Intent.FLAG_ACTIVITY_CLEAR_TASK or Intent.FLAG_ACTIVITY_NEW_TASK)
                            startActivity(intent)
                        }
                    }
                    // envia al usuario al siguiente fragment "Account options"
                    ACCOUNT_OPTIONS_FRAGMENT -> {
                        findNavController().navigate(it)
                    }

                    else -> Unit
                }
            }
        }

        binding.aBtnStart.setOnClickListener {
            // usamos "startButtonClick" para cambiar el estado del boton y enviarlo al sharedPreferences
            mViewModel.startButtonClick()
            findNavController().navigate(R.id.action_introductionFragment_to_accountOptionsFragment)
        }
    }
}