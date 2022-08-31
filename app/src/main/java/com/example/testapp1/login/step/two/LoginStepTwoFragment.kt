package com.example.testapp1.login.step.two

import android.graphics.Typeface
import android.os.Bundle
import android.text.Spannable
import android.text.SpannableString
import android.text.style.StyleSpan
import android.util.Log
import androidx.fragment.app.Fragment
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import androidx.fragment.app.commit
import androidx.fragment.app.viewModels
import com.example.testapp1.BuildConfig
import com.example.testapp1.R
import com.example.testapp1.databinding.FragmentLoginStepTwoBinding
import com.google.android.material.snackbar.Snackbar

private const val PHONE_NUMBER = "PhoneNumber"
private const val EXPLAIN_MESSAGE = "ExplainMessage"
private const val SESSION_ID = "SessionId"
private const val TAG = "LoginStepOneFragment"

class LoginStepTwoFragment : Fragment() {

    private var _binding: FragmentLoginStepTwoBinding? = null
    private val binding get() = _binding!!

    private lateinit var phoneNumber: String
    private lateinit var explainMessage: String

    private val viewModel by viewModels<LoginStepTwoViewModel>()

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        requireArguments().let {
            phoneNumber = it.getString(PHONE_NUMBER)!!
            explainMessage = it.getString(EXPLAIN_MESSAGE)!!
        }
    }

    override fun onCreateView(
        inflater: LayoutInflater, container: ViewGroup?,
        savedInstanceState: Bundle?
    ): View {
        _binding = FragmentLoginStepTwoBinding.inflate(inflater, container, false)
        return binding.root
    }

    override fun onViewCreated(view: View, savedInstanceState: Bundle?) {
        super.onViewCreated(view, savedInstanceState)

        val explainMessageSpanned = SpannableString(explainMessage).apply {
            setSpan(
                StyleSpan(Typeface.BOLD),
                explainMessage.indexOf('+'), explainMessage.length,
                Spannable.SPAN_EXCLUSIVE_EXCLUSIVE)
        }
        binding.lstTvExplanation.text = explainMessageSpanned

        binding.lstEtCode.addTextChangedListener(viewModel.getTextWatcher(phoneNumber))

        viewModel.errorMsg.observe(viewLifecycleOwner) { errMsg ->
            errMsg?.apply {
                val snackMsg = if (code == 0) {
                    if (BuildConfig.DEBUG) Log.d(TAG, "Connection error: $body")
                    getString(R.string.snack_msg_no_connection)
                } else {
                    if (BuildConfig.DEBUG) Log.d(TAG, "REST error: $code - $body")
                    getString(R.string.snack_msg_rest_error)
                }
                Snackbar.make(binding.root, snackMsg, Snackbar.LENGTH_INDEFINITE)
                    .setAction(R.string.snack_btn_retry) {
                        viewModel.fetchSessionId(phoneNumber, binding.lstEtCode.text.toString())
                    }.show()
            }
        }

        viewModel.loginStepTwoResponse.observe(viewLifecycleOwner) { response ->
            Snackbar.make(
                binding.root,
                "Получен SessionId ${response.sessionId}",
                Snackbar.LENGTH_LONG
            ).show()
//            parentFragmentManager.commit {
//                val bundle = Bundle()
//                bundle.putString(SESSION_ID, response.sessionId)
//                replace(R.id.fragment_container_view, LoginStepTwoFragment::class.java, bundle)
//                setReorderingAllowed(true)
//            }
        }
    }
}