package com.example.ecommerce.adapters

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

class BestDealsAdapter : RecyclerView.Adapter<BestDealsAdapter.BestDealsViewHolder>() {

    inner class BestDealsViewHolder(private val binding : BestDealsRvItemBinding) : RecyclerView.ViewHolder(binding.root){
        fun bind(product : Product){
            binding.apply {

                Glide.with(itemView)
                    .load(product.images[0])
                    .into(imgBestDeal)

                txtVDealProductName.text = product.name
                txtVOldPrice.text = "$ ${product.price.toString()}"

                // verifica si existe oferta de descuento
                product.offerPercentage?.let {
                    val remainingPeicePercentage = 1f - it
                    val priceAfterOffer = remainingPeicePercentage * product.price
                    // String format toma 2 numeros despues del .
                    txtVNewPrice.text = "$ ${String.format("%.2f", priceAfterOffer)}"
                    // esto pinta una linea horizontal al precio anterior
                    txtVOldPrice.paintFlags = Paint.STRIKE_THRU_TEXT_FLAG
                }

                if (product.offerPercentage == null)
                    txtVNewPrice.visibility = View.GONE
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

    override fun onCreateViewHolder(parent: ViewGroup, viewType: Int): BestDealsViewHolder {
        return BestDealsViewHolder(BestDealsRvItemBinding.inflate(
            LayoutInflater.from(parent.context)
        ))
    }

    override fun onBindViewHolder(holder: BestDealsViewHolder, position: Int) {
        val product = differ.currentList[position]
        holder.bind(product)
    }

    override fun getItemCount(): Int {
        return differ.currentList.size
    }
}
