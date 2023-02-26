package com.example.ecommerce.adapters

import android.graphics.drawable.ColorDrawable
import android.view.LayoutInflater
import android.view.ViewGroup
import androidx.compose.ui.graphics.Color
import androidx.recyclerview.widget.AsyncListDiffer
import androidx.recyclerview.widget.DiffUtil
import androidx.recyclerview.widget.RecyclerView.Adapter
import androidx.recyclerview.widget.RecyclerView.ViewHolder
import com.example.ecommerce.R
import com.example.ecommerce.data.Address
import com.example.ecommerce.databinding.AddressItemBinding

class AddressAdapter : Adapter<AddressAdapter.AddressViewHolder>(){

    inner class AddressViewHolder(val binding : AddressItemBinding)
        : ViewHolder(binding.root){

            fun bind(address: Address, isSelected : Boolean){
                binding.apply {
                    btnAddress.text = address.addressTitle
                    if (isSelected)
                        btnAddress.background =
                        ColorDrawable(itemView.context.resources.getColor(R.color.g_blue))
                    else btnAddress.background =
                        ColorDrawable(itemView.context.resources.getColor(R.color.g_white))
                }
            }


    }

    private val differCallBack = object : DiffUtil.ItemCallback<Address>() {
        override fun areItemsTheSame(oldItem: Address, newItem: Address): Boolean {
            return oldItem.addressTitle == newItem.addressTitle && oldItem.fullName == newItem.fullName
        }

        override fun areContentsTheSame(oldItem: Address, newItem: Address): Boolean {
            return oldItem == newItem
        }

    }

    val differ = AsyncListDiffer(this, differCallBack)


    override fun onCreateViewHolder(parent: ViewGroup, viewType: Int): AddressViewHolder {
        return AddressViewHolder(
            AddressItemBinding.inflate(LayoutInflater.from(parent.context))
        )
    }

    override fun getItemCount(): Int {
        return differ.currentList.size
    }

    var selectedAddress = -1

    override fun onBindViewHolder(holder: AddressViewHolder, position: Int) {
        val address = differ.currentList[position]
        holder.bind(address, selectedAddress == position)

        holder.binding.btnAddress.setOnClickListener {
            if(selectedAddress >= 0)
                notifyItemChanged(selectedAddress)
            selectedAddress = holder.adapterPosition
            notifyItemChanged(selectedAddress)
            onClick?.invoke(address)
        }
    }


    var onClick : ((Address) -> Unit)? = null


}