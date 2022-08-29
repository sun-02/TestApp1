package com.example.testapp1

import android.os.Bundle
import android.text.Editable
import android.text.TextWatcher
import androidx.fragment.app.Fragment
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import androidx.fragment.app.commit
import androidx.fragment.app.viewModels
import androidx.lifecycle.Transformations
import com.example.testapp1.databinding.FragmentLoginStepOneBinding

private const val PHONE_NUMBER = "PhoneNumber"

class LoginStepOneFragment : Fragment() {

    private var _binding: FragmentLoginStepOneBinding? = null
    private val binding get() = _binding!!

    private val viewModel by viewModels<LoginStepOneViewModel>()

    override fun onCreateView(
        inflater: LayoutInflater, container: ViewGroup?,
        savedInstanceState: Bundle?
    ): View? {
        _binding = FragmentLoginStepOneBinding.inflate(inflater, container, false)
        return binding.root
    }

    override fun onViewCreated(view: View, savedInstanceState: Bundle?) {
        super.onViewCreated(view, savedInstanceState)

        binding.lsoEtPhoneNumber.addTextChangedListener(viewModel.getTextWatcher())

        viewModel.phoneNumberIsCorrect.observe(viewLifecycleOwner) { isCorrect ->
            binding.lsoMbNext.isEnabled = isCorrect
        }

        binding.lsoMbNext.setOnClickListener {
            parentFragmentManager.commit {
                val bundle = Bundle()
                bundle.putString(PHONE_NUMBER, binding.lsoEtPhoneNumber.toString())
                replace(R.id.fragment_container_view, LoginStepTwoFragment::class.java, bundle)
                setReorderingAllowed(true)
            }
        }
    }



    override fun onDestroyView() {
        super.onDestroyView()
        _binding = null
    }
}