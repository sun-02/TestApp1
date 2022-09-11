package com.example.testapp1.login.step.two

import android.content.Context
import android.graphics.Typeface
import android.os.Bundle
import android.text.*
import android.text.method.LinkMovementMethod
import android.text.style.ClickableSpan
import android.text.style.StyleSpan
import android.util.Log
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import androidx.core.content.edit
import androidx.fragment.app.Fragment
import androidx.fragment.app.FragmentManager
import androidx.fragment.app.commit
import androidx.fragment.app.viewModels
import com.example.testapp1.BuildConfig
import com.example.testapp1.R
import com.example.testapp1.databinding.FragmentLoginStepTwoBinding
import com.example.testapp1.search.SearchFragment
import com.google.android.material.snackbar.Snackbar

private const val PHONE_NUMBER = "PhoneNumber"
private const val EXPLAIN_MESSAGE = "ExplainMessage"
private const val TIME_LEFT_SECONDS = "TimeLeftSeconds"
private const val SESSION_ID = "SessionId"
private const val TAG = "LoginStepOneFragment"

class LoginStepTwoFragment : Fragment() {

    private var _binding: FragmentLoginStepTwoBinding? = null
    private val binding get() = _binding!!

    private lateinit var phoneNumber: String
    private lateinit var explainMessage: String
    private var timeLeftSeconds: Int = 0

    private val viewModel by viewModels<LoginStepTwoViewModel>()

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        requireArguments().let {
            phoneNumber = it.getString(PHONE_NUMBER)!!
            explainMessage = it.getString(EXPLAIN_MESSAGE)!!
            timeLeftSeconds = it.getInt(TIME_LEFT_SECONDS)
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
                explainMessage.indexOf('+'),
                explainMessage.length,
                Spannable.SPAN_EXCLUSIVE_EXCLUSIVE)
        }
        binding.lstTvExplanation.text = explainMessageSpanned

        val textWatcher = object : TextWatcher {

            override fun beforeTextChanged(s: CharSequence?, start: Int, count: Int, after: Int) {}

            override fun onTextChanged(s: CharSequence?, start: Int, before: Int, count: Int) {}

            override fun afterTextChanged(s: Editable?) {
                if (s!!.length == 4) {
                    viewModel.requestSessionId(phoneNumber, s.toString())
                }
            }
        }
        binding.lstEtCode.addTextChangedListener(textWatcher)

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
                        viewModel.requestSessionId(phoneNumber, binding.lstEtCode.text.toString())
                    }.show()
            }
        }

        viewModel.startTimer(timeLeftSeconds * 1000L)

        viewModel.timerIsOn.observe(viewLifecycleOwner) { timerIsOn ->
            if (timerIsOn) {
                setRequestAgainTvWithTimer(viewModel.timeLeftSeconds.value!!)
            } else {
                setRequestAgainTvClickable()
            }
        }

        viewModel.timeLeftSeconds.observe(viewLifecycleOwner) { timeLeft ->
            setRequestAgainTvWithTimer(timeLeft)
        }

        viewModel.loginStepTwoResponseData.observe(viewLifecycleOwner) { responseData ->
            val sharedPref = requireActivity().getPreferences(Context.MODE_PRIVATE)
            sharedPref.edit {
                putString(SESSION_ID, responseData.sessionId)
            }

            clearBackStack()
            parentFragmentManager.commit {
                val bundle = Bundle()
                bundle.putString(SESSION_ID, responseData.sessionId)
                replace(R.id.fragment_container_view, SearchFragment::class.java, bundle)
                setReorderingAllowed(true)
            }
        }

        binding.lstActionBar.setNavigationOnClickListener {
            parentFragmentManager.popBackStack()
        }
    }

    private fun clearBackStack() {
        val fm = parentFragmentManager
        if (fm.backStackEntryCount > 0) {
            val entry = fm.getBackStackEntryAt(0)
            fm.popBackStack(entry.id, FragmentManager.POP_BACK_STACK_INCLUSIVE)
        }
    }

    private fun setRequestAgainTvWithTimer(timeLeft: Int) {
        binding.lstTvRequestAgain.text = getString(R.string.lso_tv_try_again_timer, timeLeft)
    }

    private fun setRequestAgainTvClickable() {
        val str = getString(R.string.lst_tv_request_again)
        val spannableString = SpannableString(str)
        val clickableSpan: ClickableSpan = object : ClickableSpan() {
            override fun onClick(widget: View) {
                viewModel.requestNewCode(phoneNumber)
            }

            override fun updateDrawState(ds: TextPaint) {
                super.updateDrawState(ds)
                ds.isUnderlineText = false
            }
        }
        spannableString.setSpan(clickableSpan, 0, str.length, Spanned.SPAN_EXCLUSIVE_EXCLUSIVE)
        binding.lstTvRequestAgain.text = spannableString
        if (binding.lstTvRequestAgain.movementMethod == null) {
            binding.lstTvRequestAgain.movementMethod = LinkMovementMethod.getInstance()
        }
        binding.lstEtCode.setText("")
    }
}