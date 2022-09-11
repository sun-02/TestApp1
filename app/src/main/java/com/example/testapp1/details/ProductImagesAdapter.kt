package com.example.testapp1.details

import android.graphics.drawable.Drawable
import android.util.SparseArray
import android.view.LayoutInflater
import android.view.ViewGroup
import androidx.recyclerview.widget.DiffUtil
import androidx.recyclerview.widget.ListAdapter
import androidx.recyclerview.widget.RecyclerView
import com.example.testapp1.databinding.ItemProductImagesBinding
import com.example.testapp1.login.step.setImageFromUrl
import kotlinx.coroutines.CoroutineScope
import kotlinx.coroutines.Dispatchers
import kotlinx.coroutines.launch

class ProductImagesAdapter(
    private val scope: CoroutineScope
) : ListAdapter<String, ProductImagesAdapter.ViewHolder>(DiffCallback) {

    private val imageCache = SparseArray<Drawable>(itemCount)

    companion object DiffCallback : DiffUtil.ItemCallback<String>() {
        override fun areItemsTheSame(oldItem: String, newItem: String): Boolean {
            return oldItem == newItem
        }

        override fun areContentsTheSame(oldItem: String, newItem: String): Boolean {
            return oldItem == newItem
        }

    }

    class ViewHolder(
        private val binding: ItemProductImagesBinding,
        private val scope: CoroutineScope,
        private val imageCache: SparseArray<Drawable>
    ) : RecyclerView.ViewHolder(binding.root) {

        fun bind(urlString: String, position: Int) {
            if (imageCache[position] == null) {
                scope.launch(Dispatchers.IO) {
                    imageCache[position] = binding.ipiImage.setImageFromUrl(urlString)
                }
            }
        }
    }

    override fun onCreateViewHolder(parent: ViewGroup, viewType: Int): ViewHolder {
        return ViewHolder(
            ItemProductImagesBinding.inflate(
                LayoutInflater.from(parent.context),
                parent,
            false),
            scope,
            imageCache
        )
    }

    override fun onBindViewHolder(holder: ViewHolder, position: Int) {
        val urlString = getItem(position)
        holder.bind(urlString, position)
    }
}