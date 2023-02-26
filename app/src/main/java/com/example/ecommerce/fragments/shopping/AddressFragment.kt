package com.example.ecommerce.fragments.shopping

import android.os.Bundle
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import android.widget.Toast
import androidx.fragment.app.Fragment
import androidx.fragment.app.viewModels
import androidx.lifecycle.lifecycleScope
import androidx.navigation.fragment.findNavController
import com.example.ecommerce.data.Address
import com.example.ecommerce.databinding.FragmentAddressBinding
import com.example.ecommerce.util.Resource
import com.example.ecommerce.viewmodel.AddressViewModel
import dagger.hilt.android.AndroidEntryPoint
import kotlinx.coroutines.flow.collectLatest

@AndroidEntryPoint
class AddressFragment : Fragment() {

    private lateinit var binding : FragmentAddressBinding
    val mViewModel by viewModels<AddressViewModel>()

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)

        lifecycleScope.launchWhenStarted {
            mViewModel.mNewAddress.collectLatest {
                when(it){
                    is Resource.Loading -> {
                        binding.pbAddress.visibility = View.VISIBLE
                    }
                    is Resource.Success -> {
                        binding.pbAddress.visibility = View.GONE
                        // volver al anterior fragment
                        findNavController().navigateUp()
                    }
                    is Resource.Error -> {
                        binding.pbAddress.visibility = View.GONE
                        Toast.makeText(requireContext(), it.message, Toast.LENGTH_LONG).show()
                    }
                    else -> Unit
                }
            }
        }


        // error en los inputs (validacion de contenida)
        lifecycleScope.launchWhenStarted {
            mViewModel.mError.collectLatest {
                Toast.makeText(requireContext(), it, Toast.LENGTH_SHORT).show()
            }
        }
    }

    override fun onCreateView(
        inflater: LayoutInflater,
        container: ViewGroup?,
        savedInstanceState: Bundle?
    ): View? {
        binding = FragmentAddressBinding.inflate(inflater)
        return binding.root
    }

    override fun onViewCreated(view: View, savedInstanceState: Bundle?) {
        super.onViewCreated(view, savedInstanceState)



        binding.apply {
            btnSaveAddress.setOnClickListener{
                val addressTitle = etAddressLocation.text.toString()
                val fullName = etFullName.text.toString()
                val street = etStreet.text.toString()
                val phone = etPhone.text.toString()
                val city = etCity.text.toString()
                val state = etState.text.toString()

                val address = Address(addressTitle, fullName, street, phone, city, state)


                mViewModel.addAddress(address)
            }

        }
    }

}