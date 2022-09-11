package com.example.testapp1

import android.content.Context
import android.os.Bundle
import androidx.appcompat.app.AppCompatActivity
import androidx.fragment.app.commit
import com.example.testapp1.databinding.ActivityMainBinding
import com.example.testapp1.login.step.one.LoginStepOneFragment
import com.example.testapp1.search.SearchFragment

private const val SESSION_ID = "SessionId"

class MainActivity : AppCompatActivity() {

    private lateinit var binding: ActivityMainBinding

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        binding = ActivityMainBinding.inflate(layoutInflater)
        setContentView(binding.root)

        val sharedPref = getPreferences(Context.MODE_PRIVATE)
        val sessionId = sharedPref.getString(SESSION_ID, null)
        if (sessionId != null) {
            startSearchFragment(sessionId)
        } else {
            startLoginStepOneFragment()
        }
    }

    private fun startSearchFragment(sessionId: String) {
        val bundle = Bundle()
        bundle.putString(SESSION_ID, sessionId)
        supportFragmentManager.commit {
            add(R.id.fragment_container_view, SearchFragment::class.java, bundle)
        }
    }

    private fun startLoginStepOneFragment() {
        supportFragmentManager.commit {
            add(R.id.fragment_container_view, LoginStepOneFragment())
        }
    }

}