package com.example.testapp1.search

import android.os.Bundle
import android.util.Log
import androidx.fragment.app.Fragment
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import androidx.fragment.app.viewModels
import com.example.testapp1.BuildConfig
import com.example.testapp1.databinding.FragmentSearchBinding

private const val SESSION_ID = "SessionId"
private const val TAG = "SearchFragment"

class SearchFragment : Fragment() {

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

        viewModel.searchResponseData.observe(viewLifecycleOwner) { response ->
            if (BuildConfig.DEBUG) Log.d(TAG, "Got actions response $response")
        }

        viewModel.queryForActions("aliexpress")
//        viewModel.queryForActions("iphone%12")
    }

    override fun onDestroy() {
        super.onDestroy()
        _binding = null
    }
}