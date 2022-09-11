package com.example.testapp1.search

import android.graphics.drawable.Drawable
import android.util.SparseArray
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import androidx.fragment.app.Fragment
import androidx.lifecycle.lifecycleScope
import androidx.recyclerview.widget.DiffUtil
import androidx.recyclerview.widget.ListAdapter
import androidx.recyclerview.widget.RecyclerView
import com.example.testapp1.databinding.ItemCampaignBinding
import com.example.testapp1.login.step.setImageFromUrl
import kotlinx.coroutines.Dispatchers
import kotlinx.coroutines.launch

class CampaignsAdapter(
    private val listener: OnCampaignItemClickListener
) : ListAdapter<Campaign, CampaignsAdapter.ViewHolder>(DiffCallback) {

    private val imageCache = SparseArray<Drawable>(itemCount)

    companion object DiffCallback : DiffUtil.ItemCallback<Campaign>() {
        override fun areItemsTheSame(oldItem: Campaign, newItem: Campaign): Boolean {
            return oldItem == newItem
        }

        override fun areContentsTheSame(oldItem: Campaign, newItem: Campaign): Boolean {
            return oldItem.id == newItem.id
        }

    }

    class ViewHolder(
        private val binding: ItemCampaignBinding,
        private val listener: OnCampaignItemClickListener,
        private val imageCache: SparseArray<Drawable>
        ) : RecyclerView.ViewHolder(binding.root), View.OnClickListener {

        fun bind(campaign: Campaign, position: Int) {
            val scope = (listener as Fragment).lifecycleScope
            campaign.let {
                with(binding) {
                    icTvName.text = it.name
                    icTvCashback.text = it.cashback
                    if (imageCache[position] == null) {
                        scope.launch(Dispatchers.IO) {
                            imageCache[position] = icIvCampaign.setImageFromUrl(it.imageUrl)
                        }
                    }
                }
            }
            itemView.setOnClickListener(this)
        }

        override fun onClick(v: View?) {
            listener.onCampaignItemClick(v!!, layoutPosition)
        }
    }

    override fun onCreateViewHolder(parent: ViewGroup, viewType: Int): ViewHolder {
        return ViewHolder(
            ItemCampaignBinding.inflate(LayoutInflater.from(parent.context)),
            listener,
            imageCache
        )
    }

    override fun onBindViewHolder(holder: ViewHolder, position: Int) {
        val campaign = getItem(position)
        holder.bind(campaign, position)
    }
}

interface OnCampaignItemClickListener {
    fun onCampaignItemClick(v: View, position: Int)
}
