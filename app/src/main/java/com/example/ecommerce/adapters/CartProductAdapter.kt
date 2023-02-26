package com.example.ecommerce.adapters

import android.annotation.SuppressLint
import android.graphics.drawable.ColorDrawable
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import androidx.recyclerview.widget.AsyncListDiffer
import androidx.recyclerview.widget.DiffUtil
import androidx.recyclerview.widget.RecyclerView
import com.bumptech.glide.Glide
import com.example.ecommerce.data.CartProduct
import com.example.ecommerce.data.Product
import com.example.ecommerce.databinding.CartProductItemBinding

class CartProductAdapter : RecyclerView.Adapter<CartProductAdapter.CartProductsViewHolder>() {

    inner class CartProductsViewHolder(val binding : CartProductItemBinding)
        : RecyclerView.ViewHolder(binding.root){

            @SuppressLint("SetTextI18n")
            fun bind(cartProduct: CartProduct){
                // TODO debe cambiar el price del producto dependiendo del % de oferta
                binding.apply {
                    Glide.with(itemView).load(cartProduct.product.images[0]).into(imgVProductCart)
                    txtVProductCartTitle.text = cartProduct.product.name
                    txtVCartProductQuantity.text = cartProduct.quantity.toString()
                    // txtVProductCartPrice.text = "$ ${cartProduct.quantity * cartProduct.product.price}"
                    txtVProductCartPrice.text = "$ ${String.format("%.2f", cartProduct.quantity * cartProduct.product.price)}"

                    cartProduct.selectSize?.let {
                        txtVProductCartSize.visibility = View.VISIBLE
                        txtVProductCartSize.text = it
                    }

                    cartProduct.selectColor?.let {
                        val imageDrawable = ColorDrawable(it)
                        imgVProductCartColor.visibility = View.VISIBLE
                        imgVProductCartColor.setImageDrawable(imageDrawable)
                    }
                }
            }

    }

    private val diffCallback = object : DiffUtil.ItemCallback<CartProduct>(){
        override fun areItemsTheSame(oldItem: CartProduct, newItem: CartProduct): Boolean {
            return oldItem.product.id == newItem.product.id
        }

        override fun areContentsTheSame(oldItem: CartProduct, newItem: CartProduct): Boolean {
            return oldItem == newItem
        }

    }

    val differ = AsyncListDiffer(this, diffCallback)


    override fun onCreateViewHolder(parent: ViewGroup, viewType: Int): CartProductsViewHolder {
        return CartProductsViewHolder(
            CartProductItemBinding.inflate(LayoutInflater.from(parent.context))
        )
    }

    override fun getItemCount(): Int {
        return differ.currentList.size
    }

    override fun onBindViewHolder(holder: CartProductsViewHolder, position: Int) {
        val cartProduct = differ.currentList[position]

        holder.bind(cartProduct)

        holder.itemView.setOnClickListener {
            onProductClick?.invoke(cartProduct)
        }

        holder.binding.imgVProductCartAdd.setOnClickListener {
            onPlusClick?.invoke(cartProduct)
        }

        holder.binding.imgVProductCartLess.setOnClickListener {
            onMinusClick?.invoke(cartProduct)
        }
    }

    var onProductClick : ((CartProduct) -> Unit)? = null
    var onPlusClick : ((CartProduct) -> Unit)? = null
    var onMinusClick : ((CartProduct) -> Unit)? = null
}