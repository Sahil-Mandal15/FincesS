package com.sahilm.fincess.adapter

import android.content.Context
import android.view.LayoutInflater
import android.view.ViewGroup
import androidx.recyclerview.widget.RecyclerView
import com.bumptech.glide.Glide
import com.sahilm.fincess.databinding.CategoryItemBinding
import com.sahilm.fincess.model.Category

class CategoryAdapter(
    private val context: Context,
    private val categories: List<Category>,
    private val categoryClickListener: CategoryClickListener
) : RecyclerView.Adapter<CategoryAdapter.CategoryViewHolder>() {

    interface CategoryClickListener {
        fun onCategoryClicked(category: Category)
    }

    inner class CategoryViewHolder(val binding: CategoryItemBinding) : RecyclerView.ViewHolder(binding.root)


    override fun onCreateViewHolder(parent: ViewGroup, viewType: Int): CategoryViewHolder {
        val binding = CategoryItemBinding.inflate(LayoutInflater.from(context), parent, false)
        return CategoryViewHolder(binding)
    }

    override fun getItemCount(): Int {
        return categories.size

    }

    override fun onBindViewHolder(holder: CategoryViewHolder, position: Int) {
        val category = categories[position]
        holder.binding.tvCategoryText.text = category.categoryName

        Glide.with(context)
            .load(category.categoryImage)
            .into(holder.binding.ivCategoryIcon)
        holder.binding.ivCategoryIcon.backgroundTintList = context.getColorStateList(category.categoryColor)

        holder.itemView.setOnClickListener {
            categoryClickListener.onCategoryClicked(category)
        }
    }

}