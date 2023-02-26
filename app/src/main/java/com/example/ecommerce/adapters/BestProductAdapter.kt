package com.example.ecommerce.adapters

import android.annotation.SuppressLint
import android.graphics.Paint
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import androidx.recyclerview.widget.AsyncListDiffer
import androidx.recyclerview.widget.DiffUtil
import androidx.recyclerview.widget.RecyclerView
import com.bumptech.glide.Glide
import com.example.ecommerce.data.Product
import com.example.ecommerce.databinding.BestDealsRvItemBinding
import com.example.ecommerce.databinding.ProductRvItemBinding

class BestProductAdapter : RecyclerView.Adapter<BestProductAdapter.BestProductViewHolder>() {

    inner class BestProductViewHolder(private val binding : ProductRvItemBinding) : RecyclerView.ViewHolder(binding.root){
        @SuppressLint("SetTextI18n")
        fun bind(product : Product){
            binding.apply {

                Glide.with(itemView)
                    .load(product.images[0])
                    .into(imgProduct)

                txtVName.text = product.name
                txtVPrice.text = "$ ${product.price}"

                // verifica si existe oferta de descuento
                product.offerPercentage?.let {
                    val remainingPricePercentage = 1f - it
                    val priceAfterOffer = remainingPricePercentage * product.price
                    // String format toma 2 numeros despues del .
                    tvNewPrice.text = "$ ${String.format("%.2f", priceAfterOffer)}"
                    // esto pinta una linea horizontal al precio anterior
                    txtVPrice.paintFlags = Paint.STRIKE_THRU_TEXT_FLAG
                }
                // de no existir data de la oferta quita el txtV
                if (product.offerPercentage == null)
                    tvNewPrice.visibility = View.GONE
            }

        }
    }

    private val differCallBack = object : DiffUtil.ItemCallback<Product>(){
        override fun areItemsTheSame(oldItem: Product, newItem: Product): Boolean {
            return oldItem.id == newItem.id
        }

        override fun areContentsTheSame(oldItem: Product, newItem: Product): Boolean {
            return oldItem == newItem
        }

    }

    val differ = AsyncListDiffer(this, differCallBack)

    override fun onCreateViewHolder(parent: ViewGroup, viewType: Int): BestProductViewHolder {
        return BestProductViewHolder(
            ProductRvItemBinding.inflate(
            LayoutInflater.from(parent.context)
        ))
    }

    override fun onBindViewHolder(holder: BestProductViewHolder, position: Int) {
        val product = differ.currentList[position]
        holder.bind(product)

        holder.itemView.setOnClickListener {
            onClick?.invoke(product)
        }
    }

    override fun getItemCount(): Int {
        return differ.currentList.size
    }

    // agregando click-action
    var onClick : ((Product)-> Unit)? = null
}