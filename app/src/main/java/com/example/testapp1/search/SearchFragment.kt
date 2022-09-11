package com.example.testapp1.search

import android.os.Bundle
import android.util.Log
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import androidx.appcompat.content.res.AppCompatResources
import androidx.fragment.app.Fragment
import androidx.fragment.app.commit
import androidx.fragment.app.viewModels
import androidx.recyclerview.widget.DividerItemDecoration
import androidx.recyclerview.widget.GridLayoutManager
import androidx.recyclerview.widget.RecyclerView
import com.example.testapp1.BuildConfig
import com.example.testapp1.R
import com.example.testapp1.databinding.FragmentSearchBinding
import com.example.testapp1.details.CampaignDetailsFragment
import com.example.testapp1.details.ProductDetailsFragment
import com.example.testapp1.login.step.one.LoginStepOneFragment
import com.google.android.material.snackbar.Snackbar

private const val SESSION_ID = "SessionId"
private const val TAG = "SearchFragment"
private const val CAMPAIGN = "Campaign"
private const val PRODUCT = "Product"
private const val ERROR_MESSAGE_CODE = "loyality.session.not.found"

class SearchFragment : Fragment(), OnCampaignItemClickListener, OnProductItemClickListener {

    private var _campaigns: List<Campaign>? = null
    private val campaigns get() = _campaigns!!

    private var _products: List<Product>? = null
    private val products get() = _products!!

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

            if (!responseData.successful && responseData.errorMessageCode == ERROR_MESSAGE_CODE) {
                startLoginStepOneFragment()
            }

            _campaigns = responseData.campaigns
            _products = responseData.products

            if (campaigns.isEmpty()) {
                rvCampaigns.visibility = View.GONE
                listDivider.visibility = View.GONE
                if (products.isEmpty()) {
                    Snackbar.make(
                        requireView(),
                        requireContext().getString(R.string.msg_found_nothing),
                        Snackbar.LENGTH_LONG
                    ).show()
                }
            } else {
                campaignsAdapter.submitList(campaigns)
                rvCampaigns.visibility = View.VISIBLE
                listDivider.visibility = View.VISIBLE
            }
            productsAdapter.submitList(products)
        }

        viewModel.errorMsg.observe(viewLifecycleOwner) { errMsg ->
            errMsg?.apply {
                val snackMsg = if (code == 0) {
                    if (BuildConfig.DEBUG) Log.d(TAG, "Connection error: $body")
                    getString(R.string.snack_msg_no_connection)
                } else {
                    if (BuildConfig.DEBUG) Log.d(TAG, "REST error: $code - $body")
                    getString(R.string.snack_msg_rest_error)
                }
                Snackbar.make(binding.root, snackMsg, Snackbar.LENGTH_LONG).show()
            }
        }

        binding.fsActionBar.setOnMenuItemClickListener { menuItem ->
            when (menuItem.itemId) {
                R.id.search -> {
                    val query = binding.fsEtSearch.text.toString()
                    viewModel.queryForActions(query, sessionId)
                    true
                }
                else -> false
            }
        }
    }

    override fun onCampaignItemClick(v: View, position: Int) {
        parentFragmentManager.commit {
            val bundle = Bundle()
            bundle.putParcelable(CAMPAIGN, campaigns[position])
            replace(R.id.fragment_container_view, CampaignDetailsFragment::class.java, bundle)
            setReorderingAllowed(true)
            addToBackStack(null)
        }
    }

    override fun onProductItemClick(v: View, position: Int) {
        parentFragmentManager.commit {
            val bundle = Bundle()
            bundle.putParcelable(PRODUCT, products[position])
            replace(R.id.fragment_container_view, ProductDetailsFragment::class.java, bundle)
            setReorderingAllowed(true)
            addToBackStack(null)
        }
    }

    private fun startLoginStepOneFragment() {
        parentFragmentManager.commit {
            replace(R.id.fragment_container_view, LoginStepOneFragment())
        }
    }

    override fun onDestroy() {
        super.onDestroy()
        _campaignsAdapter = null
        _productsAdapter = null
        binding.fsRvCampaigns.adapter = null
        binding.fsRvProducts.adapter = null
        _binding = null
        _campaigns = null
        _products = null
    }
}