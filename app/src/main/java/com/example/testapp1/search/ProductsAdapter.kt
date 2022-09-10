package com.example.testapp1.search

import android.graphics.drawable.Drawable
import android.util.Log
import android.util.SparseArray
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import androidx.fragment.app.Fragment
import androidx.lifecycle.lifecycleScope
import androidx.recyclerview.widget.DiffUtil
import androidx.recyclerview.widget.ListAdapter
import androidx.recyclerview.widget.RecyclerView
import com.example.testapp1.BuildConfig
import com.example.testapp1.OnItemClickListener
import com.example.testapp1.databinding.ItemProductBinding
import com.example.testapp1.login.step.setImageFromUrl
import kotlinx.coroutines.CoroutineScope
import kotlinx.coroutines.Dispatchers
import kotlinx.coroutines.launch

class ProductsAdapter(
    private val listener: OnItemClickListener
) : ListAdapter<Product, ProductsAdapter.ViewHolder>(DiffCallback) {

    private val imageCache = SparseArray<Drawable>(itemCount)
    private val campaignImageCache = SparseArray<Drawable>(itemCount)

    companion object DiffCallback : DiffUtil.ItemCallback<Product>() {
        override fun areItemsTheSame(oldItem: Product, newItem: Product): Boolean {
            return oldItem == newItem
        }

        override fun areContentsTheSame(oldItem: Product, newItem: Product): Boolean {
            return oldItem.id == newItem.id
        }

    }

    class ViewHolder(
        private var binding: ItemProductBinding,
        private val listener: OnItemClickListener,
        private val imageCache: SparseArray<Drawable>,
        private val campaignImageCache: SparseArray<Drawable>
    ) : RecyclerView.ViewHolder(binding.root), View.OnClickListener {

        fun bind(product: Product, position: Int) {
            val scope = (listener as Fragment).lifecycleScope
            product.let {
                with(binding) {
                    ipTvName.text = it.name
                    ipTvCashback.text = it.cashback
                    ipTvPrice.text = it.price
                    if (imageCache[position] == null) {
                        scope.launch(Dispatchers.IO) {
                            imageCache[position] = ipIvProduct.setImageFromUrl(it.imageUrls[1])
                        }
                    }
                    if (imageCache[position] == null) {
                        scope.launch(Dispatchers.IO) {
                            campaignImageCache[position] =
                                ipIvCampaign.setImageFromUrl(it.campaignImageUrl)
                        }
                    }
                }
            }
            itemView.setOnClickListener(this)
        }

        override fun onClick(v: View?) {
            listener.onItemClick(v!!, layoutPosition)
        }
    }

    override fun onCreateViewHolder(parent: ViewGroup, viewType: Int): ViewHolder {
        return ViewHolder(
            ItemProductBinding.inflate(LayoutInflater.from(parent.context)),
            listener,
            imageCache,
            campaignImageCache
        )
    }

    override fun onBindViewHolder(holder: ViewHolder, position: Int) {
        val product = getItem(position)
        holder.bind(product, position)
    }
}