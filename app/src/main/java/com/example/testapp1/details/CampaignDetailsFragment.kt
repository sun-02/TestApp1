package com.example.testapp1.details

import android.os.Bundle
import android.text.Spannable
import android.text.SpannableString
import android.text.style.ForegroundColorSpan
import android.util.TypedValue
import androidx.fragment.app.Fragment
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import android.widget.LinearLayout
import android.widget.TextView
import androidx.annotation.ColorInt
import androidx.lifecycle.lifecycleScope
import com.example.testapp1.databinding.FragmentCampaignDetailsBinding
import com.example.testapp1.login.step.setImageFromUrl
import com.example.testapp1.search.Campaign
import com.example.testapp1.search.CampaignsAdapter
import com.google.android.material.snackbar.Snackbar
import com.google.android.material.tabs.TabLayoutMediator
import kotlinx.coroutines.Dispatchers
import kotlinx.coroutines.launch

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

        with(binding) {
            lifecycleScope.launch(Dispatchers.IO) {
                fcdIvCampaign.setImageFromUrl(campaign.imageUrl)
            }
            fcdTvName.text = campaign.name
            fcdTvCashback.text = campaign.cashback
            fcdTvPaymentTime.text = campaign.paymentTime

            val typedValue = TypedValue()
            val theme = requireContext().theme
            theme.resolveAttribute(android.R.attr.colorAccent, typedValue, true)
            @ColorInt val colorAccent = typedValue.data

            val actionsSpanned = campaign.actions.map{ action ->
                SpannableString("${action.value} ${action.text}").apply {
                    setSpan(
                        ForegroundColorSpan(colorAccent),
                        0,
                        action.value.length,
                        Spannable.SPAN_EXCLUSIVE_EXCLUSIVE
                    )
                }
            }

            val layoutParams = LinearLayout.LayoutParams(
                LinearLayout.LayoutParams.MATCH_PARENT,
                LinearLayout.LayoutParams.WRAP_CONTENT
            )
            val marginMedium = requireActivity()
                .resources.getDimensionPixelSize(com.example.testapp1.R.dimen.margin_medium)
            val marginSmall = requireActivity()
                .resources.getDimensionPixelSize(com.example.testapp1.R.dimen.margin_small)
            layoutParams.setMargins(marginMedium, marginSmall, marginMedium, 0)

            for (act in actionsSpanned) {
                val textView = TextView(this@CampaignDetailsFragment.requireContext())
                textView.layoutParams = layoutParams
                textView.setTextAppearance(com.example.testapp1.R.style.TextAppearance_TestApp1_Body1_Bold)
                textView.text = act
                fcdLl.addView(textView)
            }

            fcdBtnBuy.setOnClickListener { v ->
                Snackbar.make(v, "Bought!", Snackbar.LENGTH_SHORT).show()
            }
        }
    }

    override fun onDestroyView() {
        super.onDestroyView()
        _binding = null
    }
}