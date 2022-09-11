package com.example.testapp1.details

import android.os.Bundle
import androidx.fragment.app.Fragment
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import com.example.testapp1.databinding.FragmentCampaignDetailsBinding
import com.example.testapp1.search.Campaign
import com.example.testapp1.search.CampaignsAdapter

private const val CAMPAIGN = "Campaign"
private const val TAG = "CampaignDetailsFragment"

class CampaignDetailsFragment : Fragment() {

    private var _campaign: Campaign? = null
    private val campaign get() = _campaign!!

//    private var _campaignDetailsAdapter: CampaignDetailsAdapter? = null
//    private val campaignDetailsAdapter get() = _campaignDetailsAdapter!!

    private var _binding: FragmentCampaignDetailsBinding? = null
    private val binding get() = _binding!!

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        arguments?.let {
            _campaign = it.getParcelable(CAMPAIGN)
        }
    }

    override fun onCreateView(
        inflater: LayoutInflater, container: ViewGroup?,
        savedInstanceState: Bundle?
    ): View {
        _binding = FragmentCampaignDetailsBinding.inflate(inflater, container, false)
        return binding.root
    }

    override fun onViewCreated(view: View, savedInstanceState: Bundle?) {
        super.onViewCreated(view, savedInstanceState)

        val str = """
            ${campaign.id}
            ${campaign.name}
            ${campaign.cashback}
            ${campaign.actions}
            ${campaign.imageUrl}
            ${campaign.paymentTime}
        """.trimIndent()

//        binding.tv.text = str
    }
}