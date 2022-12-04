package com.example.ecommerce.adapters

import android.view.LayoutInflater
import android.view.ViewGroup
import androidx.recyclerview.widget.AsyncListDiffer
import androidx.recyclerview.widget.DiffUtil
import androidx.recyclerview.widget.RecyclerView
import com.bumptech.glide.Glide
import com.example.ecommerce.data.Product
import com.example.ecommerce.databinding.SpecialRvItemBinding

class SpecialProductsAdapter : RecyclerView.Adapter<SpecialProductsAdapter.SpecialProductsViewHolder>() {

    inner class SpecialProductsViewHolder(private val binding : SpecialRvItemBinding) :
        RecyclerView.ViewHolder(binding.root){
            fun bind(product: Product){
                binding.apply {
                    Glide.with(itemView).load(product.images[0]).into(imgVSpecialRvItem)
                    txtVSpecialNameRvItem.text = product.name
                    txtVSpecialPriceRvItem.text = product.price.toString()
                }
            }
        }

    // diffCallBack solo recarga los elementos que han sido modificados -> esto ayuda a que -
    // el recyclerView sea mas eficiente
    private val diffCallBack = object : DiffUtil.ItemCallback<Product>(){

        // compara el id de los objetos
        override fun areItemsTheSame(oldItem: Product, newItem: Product): Boolean {
            return oldItem.id == newItem.id
        }

        // compara si todas las caracteristicas de los objetos son las mismas
        override fun areContentsTheSame(oldItem: Product, newItem: Product): Boolean {
            return oldItem == newItem
        }

    }

    val differ = AsyncListDiffer(this, diffCallBack)

    override fun onCreateViewHolder(parent: ViewGroup, viewType: Int): SpecialProductsViewHolder {
        return SpecialProductsViewHolder(
            SpecialRvItemBinding.inflate(
                LayoutInflater.from(parent.context),
                parent, false))
    }

    override fun onBindViewHolder(holder: SpecialProductsViewHolder, position: Int) {
        val product = differ.currentList[position]
        holder.bind(product)
    }

    override fun getItemCount(): Int {
        return differ.currentList.size
    }


}