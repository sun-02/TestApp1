package com.example.testapp1.search

import android.os.Bundle
import android.util.Log
import androidx.fragment.app.Fragment
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import androidx.appcompat.content.res.AppCompatResources
import androidx.fragment.app.viewModels
import androidx.lifecycle.lifecycleScope
import androidx.recyclerview.widget.DividerItemDecoration
import androidx.recyclerview.widget.GridLayoutManager
import androidx.recyclerview.widget.LinearLayoutManager
import androidx.recyclerview.widget.RecyclerView
import com.example.testapp1.BuildConfig
import com.example.testapp1.OnItemClickListener
import com.example.testapp1.R
import com.example.testapp1.databinding.FragmentSearchBinding
import com.google.android.material.snackbar.Snackbar

private const val SESSION_ID = "SessionId"
private const val TAG = "SearchFragment"

class SearchFragment : Fragment(), OnItemClickListener {

    private var _campaignsAdapter: CampaignsAdapter? = null
    private val campaignsAdapter get() = _campaignsAdapter!!

    private var _productsAdapter: ProductsAdapter? = null
    private val productsAdapter get() = _productsAdapter!!

    private var _binding: FragmentSearchBinding? = null
    private val binding get() = _binding!!

    private val viewModel by viewModels<SearchViewModel>()

    private lateinit var sessionId: String

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        arguments?.let {
            sessionId = it.getString(SESSION_ID)!!
        }
    }

    override fun onCreateView(
        inflater: LayoutInflater, container: ViewGroup?,
        savedInstanceState: Bundle?
    ): View {
        _binding = FragmentSearchBinding.inflate(inflater, container, false)
        return binding.root
    }

    override fun onViewCreated(view: View, savedInstanceState: Bundle?) {
        super.onViewCreated(view, savedInstanceState)

        _campaignsAdapter = CampaignsAdapter(this)
        _productsAdapter = ProductsAdapter(this)

        val rvCampaigns = binding.fsRvCampaigns
        val rvProducts = binding.fsRvProducts
        val listDivider = binding.fsRvDivider

        rvCampaigns.adapter = campaignsAdapter
        rvProducts.adapter = productsAdapter

        rvCampaigns.layoutManager =
            object : GridLayoutManager(requireContext(), 2){
                override fun canScrollVertically(): Boolean { return false }
            }

        val itemDivider = DividerItemDecoration(requireContext(), RecyclerView.VERTICAL)
        val divider = AppCompatResources.getDrawable(requireContext(), R.drawable.item_divider)!!
        itemDivider.setDrawable(divider)

        rvProducts.addItemDecoration(itemDivider)

        viewModel.searchResponseData.observe(viewLifecycleOwner) { responseData ->
            if (BuildConfig.DEBUG) Log.d(TAG, "Got responseData $responseData")

            val campaigns = responseData.campaigns
            val products = responseData.products

            if (campaigns.isEmpty()) {
                listDivider.visibility = View.GONE
                rvCampaigns.visibility = View.GONE
            } else {
                campaignsAdapter.submitList(campaigns)
                listDivider.visibility = View.VISIBLE
                rvCampaigns.visibility = View.VISIBLE
            }
            productsAdapter.submitList(products)
        }

        viewModel.queryForActions("aliexpress")
//        viewModel.queryForActions("iphone%12")
    }

    override fun onDestroy() {
        super.onDestroy()
        _campaignsAdapter = null
        _productsAdapter = null
        binding.fsRvCampaigns.adapter = null
        binding.fsRvProducts.adapter = null
        _binding = null
    }

    override fun onItemClick(v: View, position: Int) {
        Snackbar.make(v, "$position", Snackbar.LENGTH_LONG).show()
    }
}