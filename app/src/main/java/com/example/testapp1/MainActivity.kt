package com.example.testapp1

import android.content.Context
import android.os.Bundle
import androidx.appcompat.app.AppCompatActivity
import androidx.fragment.app.commit
import com.example.testapp1.databinding.ActivityMainBinding
import com.example.testapp1.login.step.one.LoginStepOneFragment

class MainActivity : AppCompatActivity() {

    companion object {
        private const val SESSION_ID = "SessionID"
    }

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
        TODO("Not yet implemented")
    }

    private fun startLoginStepOneFragment() {
        supportFragmentManager.commit {
            add(R.id.fragment_container_view, LoginStepOneFragment())
            addToBackStack(null)
        }
    }

}