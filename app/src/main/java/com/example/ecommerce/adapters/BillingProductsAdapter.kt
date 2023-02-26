package com.example.ecommerce.adapters

import android.annotation.SuppressLint
import android.view.LayoutInflater
import android.view.ViewGroup
import androidx.recyclerview.widget.AsyncListDiffer
import androidx.recyclerview.widget.DiffUtil
import androidx.recyclerview.widget.RecyclerView.Adapter
import androidx.recyclerview.widget.RecyclerView.ViewHolder
import com.bumptech.glide.Glide
import com.example.ecommerce.data.CartProduct
import com.example.ecommerce.databinding.BillingProductsItemBinding


class BillingProductsAdapter : Adapter<BillingProductsAdapter.BillingProductsViewHolder>(){


    inner class BillingProductsViewHolder(val binding: BillingProductsItemBinding)
        : ViewHolder(binding.root){

        @SuppressLint("SetTextI18n")
        fun bind(billingProduct : CartProduct) {
            binding.apply {
                Glide.with(itemView).load(billingProduct.product.images[0]).into(imgVCartProduct)
                txtVProductCartName.text = billingProduct.product.name
                txtVProductCartQuantity.text = billingProduct.quantity.toString()

                txtVProductCartPrice.text = "$ ${String.format("%.2f", billingProduct.quantity * billingProduct.product.price)}"
            }
        }

    }

    private val diffUtil = object : DiffUtil.ItemCallback<CartProduct>() {
        override fun areItemsTheSame(oldItem: CartProduct, newItem: CartProduct): Boolean {
            return oldItem == newItem
        }

        override fun areContentsTheSame(oldItem: CartProduct, newItem: CartProduct): Boolean {
            return oldItem == newItem
        }

    }

    val differ = AsyncListDiffer(this, diffUtil)


    override fun onCreateViewHolder(parent: ViewGroup, viewType: Int): BillingProductsViewHolder {
        return BillingProductsViewHolder(
            BillingProductsItemBinding.inflate(LayoutInflater.from(parent.context))
        )
    }

    override fun getItemCount(): Int {
        return differ.currentList.size
    }

    override fun onBindViewHolder(holder: BillingProductsViewHolder, position: Int) {
        val billingProduct = differ.currentList[position]

        holder.bind(billingProduct)
    }

    var onClick : ((CartProduct) -> Unit)? = null
}