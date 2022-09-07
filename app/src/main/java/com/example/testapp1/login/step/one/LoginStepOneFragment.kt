package com.example.testapp1.login.step.one

import android.os.Bundle
import android.text.Editable
import android.text.TextWatcher
import android.util.Log
import androidx.fragment.app.Fragment
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import androidx.fragment.app.commit
import androidx.fragment.app.viewModels
import com.example.testapp1.BuildConfig
import com.example.testapp1.login.step.two.LoginStepTwoFragment
import com.example.testapp1.R
import com.example.testapp1.databinding.FragmentLoginStepOneBinding
import com.google.android.material.snackbar.Snackbar

private const val PHONE_NUMBER = "PhoneNumber"
private const val EXPLAIN_MESSAGE = "ExplainMessage"
private const val TIME_LEFT_SECONDS = "TimeLeftSeconds"
private const val TAG = "LoginStepOneFragment"

class LoginStepOneFragment : Fragment() {

    private var _binding: FragmentLoginStepOneBinding? = null
    private val binding get() = _binding!!

    private val viewModel by viewModels<LoginStepOneViewModel>()

    override fun onCreateView(
        inflater: LayoutInflater, container: ViewGroup?,
        savedInstanceState: Bundle?
    ): View {
        _binding = FragmentLoginStepOneBinding.inflate(inflater, container, false)
        return binding.root
    }

    override fun onViewCreated(view: View, savedInstanceState: Bundle?) {
        super.onViewCreated(view, savedInstanceState)



        val textWatcher = object : TextWatcher {
            private var changedInternally = false

            override fun beforeTextChanged(s: CharSequence?, start: Int, count: Int, after: Int) {}

            override fun onTextChanged(s: CharSequence?, start: Int, before: Int, count: Int) {}

            override fun afterTextChanged(s: Editable?) {
                if (changedInternally) {
                    changedInternally = false
                    return
                }
                val str = s!!.toString()
                if (str.isNotBlank() && str[0] != '+') {
                    s.insert(0, "+")
                }
                viewModel.checkPhoneNumber(s.toString())
            }
        }
        binding.lsoEtPhoneNumber.addTextChangedListener(textWatcher)

        viewModel.phoneNumberIsCorrect.observe(viewLifecycleOwner) { isCorrect ->
            if (viewModel.timerIsOn.value == true) {
                binding.lsoMbNext.isEnabled = false
            } else {
                binding.lsoMbNext.isEnabled = isCorrect
            }
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
                viewModel.startTimer()
            }
        }

        binding.lsoMbNext.setOnClickListener {
            viewModel.requestCode(binding.lsoEtPhoneNumber.text.toString())
        }

        viewModel.loginStepOneResponseData.observe(viewLifecycleOwner) { responseData ->
            if (responseData.successful) {
                viewModel.clearResponse()
                parentFragmentManager.commit {
                    val bundle = Bundle()
                    bundle.putString(PHONE_NUMBER, responseData.normalizedPhone)
                    bundle.putString(EXPLAIN_MESSAGE, responseData.explainMessage)
                    val timeLeft = viewModel.timeLeftSeconds.value?.toInt()
                    if (timeLeft == null || timeLeft == 0) {
                        bundle.putInt(TIME_LEFT_SECONDS, 60)
                    } else {
                        bundle.putInt(TIME_LEFT_SECONDS, timeLeft)
                    }
                    replace(R.id.fragment_container_view, LoginStepTwoFragment::class.java, bundle)
                    setReorderingAllowed(true)
                    addToBackStack(null)
                }
            } else {
                Snackbar.make(binding.root, responseData.errorMessage, Snackbar.LENGTH_LONG).show()
                viewModel.startTimer()
            }
        }

        viewModel.timerIsOn.observe(viewLifecycleOwner) { timerIsOn ->
            if (timerIsOn) {
                binding.lsoTvTryAgainTimer.visibility = View.VISIBLE
                binding.lsoMbNext.isEnabled = false
            } else {
                binding.lsoTvTryAgainTimer.visibility = View.GONE
                binding.lsoMbNext.isEnabled = true
            }
        }

        viewModel.timeLeftSeconds.observe(viewLifecycleOwner) { timeLeft ->
            if (binding.lsoTvTryAgainTimer.visibility == View.VISIBLE) {
                binding.lsoTvTryAgainTimer.text = getString(R.string.lso_tv_try_again_timer, timeLeft)
            }
        }
    }

    override fun onDestroyView() {
        super.onDestroyView()
        _binding = null
    }
}