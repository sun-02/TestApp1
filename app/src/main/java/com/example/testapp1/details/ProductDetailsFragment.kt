package com.example.testapp1.details

import android.os.Bundle
import android.text.Spannable
import android.text.SpannableString
import android.text.style.ForegroundColorSpan
import android.util.TypedValue
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import android.widget.*
import androidx.annotation.ColorInt
import androidx.fragment.app.Fragment
import androidx.lifecycle.lifecycleScope
import com.example.testapp1.databinding.FragmentProductDetailsBinding
import com.example.testapp1.login.step.setImageFromUrl
import com.example.testapp1.search.Product
import com.google.android.material.snackbar.Snackbar
import com.google.android.material.tabs.TabLayoutMediator
import kotlinx.coroutines.Dispatchers
import kotlinx.coroutines.launch


private const val PRODUCT = "Product"
private const val TAG = "ProductDetailsFragment"

class ProductDetailsFragment : Fragment() {

    private var _product: Product? = null
    private val product get() = _product!!

    private var _binding: FragmentProductDetailsBinding? = null
    private val binding get() = _binding!!

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        arguments?.let {
            _product = it.getParcelable(PRODUCT)
        }
    }

    override fun onCreateView(
        inflater: LayoutInflater, container: ViewGroup?,
        savedInstanceState: Bundle?
    ): View {
        _binding = FragmentProductDetailsBinding.inflate(inflater, container, false)
        return binding.root
    }

    override fun onViewCreated(view: View, savedInstanceState: Bundle?) {
        super.onViewCreated(view, savedInstanceState)

        with(binding) {
            val viewPager = fpdVpProductImages
            val adapter = ProductImagesAdapter(lifecycleScope)
            viewPager.adapter = adapter
            adapter.submitList(product.imageUrls)
            val tabLayout = fpdTlProductImagesDots
            TabLayoutMediator(tabLayout, viewPager) { _, _ -> }.attach()

            fpdTvName.text = product.name
            lifecycleScope.launch(Dispatchers.IO) {
                fpdIvCampaign.setImageFromUrl(product.campaignImageUrl)
            }
            fpdTvPrice.text = product.price
            fpdTvCashback.text = product.cashback
            fpdTvPaymentTime.text = product.paymentTime

            val typedValue = TypedValue()
            val theme = requireContext().theme
            theme.resolveAttribute(android.R.attr.colorAccent, typedValue, true)
            @ColorInt val colorAccent = typedValue.data

            val actionsSpanned = product.actions.map{ action ->
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
                val textView = TextView(this@ProductDetailsFragment.requireContext())
                textView.layoutParams = layoutParams
                textView.setTextAppearance(com.example.testapp1.R.style.TextAppearance_TestApp1_Body1_Bold)
                textView.text = act
                fpdLl.addView(textView)
            }

            fpdBtnBuy.setOnClickListener { v ->
                Snackbar.make(v, "Bought!", Snackbar.LENGTH_SHORT).show()
            }
        }
    }

    override fun onDestroyView() {
        super.onDestroyView()
        binding.fpdVpProductImages.adapter = null
        _binding = null
    }
}