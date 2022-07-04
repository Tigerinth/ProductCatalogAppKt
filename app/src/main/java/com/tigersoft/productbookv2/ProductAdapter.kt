package com.tigersoft.productbookv2

import android.content.Intent
import android.view.LayoutInflater
import android.view.ViewGroup
import androidx.recyclerview.widget.RecyclerView
import com.tigersoft.productbookv2.databinding.RecyclerRowBinding

class ProductAdapter(val productList : ArrayList<Product>) : RecyclerView.Adapter<ProductAdapter.ProductHolder>() {
    class ProductHolder(val binding: RecyclerRowBinding) : RecyclerView.ViewHolder(binding.root) {
    }

    override fun onCreateViewHolder(parent: ViewGroup, viewType: Int): ProductHolder {
        val binding = RecyclerRowBinding.inflate(LayoutInflater.from(parent.context),parent,false)
        return ProductHolder(binding)
    }

    override fun onBindViewHolder(holder: ProductHolder, position: Int) {
        holder.binding.rvTextView.text = productList.get(position).name
        holder.itemView.setOnClickListener {
            val intent = Intent(holder.itemView.context,ProductInfoActivity::class.java)
            intent.putExtra("info","old")
            intent.putExtra("id",productList[position].id)
            holder.itemView.context.startActivity(intent)
        }
    }

    override fun getItemCount(): Int {
        return productList.size
    }

}