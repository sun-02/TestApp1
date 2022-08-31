package com.example.testapp1.login.step.two

import android.text.Editable
import android.text.TextWatcher
import android.util.Log
import androidx.lifecycle.LiveData
import androidx.lifecycle.MutableLiveData
import androidx.lifecycle.ViewModel
import com.example.testapp1.BuildConfig
import com.example.testapp1.ServerResponse
import com.example.testapp1.SingleLiveEvent
import org.json.JSONObject
import java.io.IOException
import java.net.HttpURLConnection
import java.net.URL

private const val TAG = "LoginStepTwoViewModel"

class LoginStepTwoViewModel : ViewModel() {

    private var _loginStepTwoResponse: MutableLiveData<LoginStepTwoResponse> = MutableLiveData()
    val loginStepTwoResponse: LiveData<LoginStepTwoResponse> get() = _loginStepTwoResponse

    private var _errorMsg: SingleLiveEvent<ServerResponse?> = SingleLiveEvent()
    val errorMsg: LiveData<ServerResponse?> get() = _errorMsg

    fun getTextWatcher(phoneNumber: String): TextWatcher =
        object : TextWatcher {

            override fun beforeTextChanged(s: CharSequence?, start: Int, count: Int, after: Int) {}

            override fun onTextChanged(s: CharSequence?, start: Int, before: Int, count: Int) {}

            override fun afterTextChanged(s: Editable?) {
                if (s!!.length == 4) {
                    fetchSessionId(phoneNumber, s.toString())
                }
            }

        }

    fun fetchSessionId(phoneNumber: String, code: String) {
        val url = URL("https://utcoin.one/loyality/login_step2?phone=$phoneNumber&password=$code")
        val urlConnection: HttpURLConnection = url.openConnection() as HttpURLConnection
        try {
            val response = ServerResponse(urlConnection.responseCode, urlConnection.responseMessage)
            if (BuildConfig.DEBUG) Log.d(TAG, "Got response $response")
            if (response.code in 200..299) {
                _loginStepTwoResponse.value = parseLoginStepTwoResponse(response.body)
            } else {
                _errorMsg.value = response
            }
        } catch (e: IOException) {
            _errorMsg.value = ServerResponse(0, e.stackTraceToString())
        } finally {
            urlConnection.disconnect()
        }
    }

    private fun parseLoginStepTwoResponse(json: String): LoginStepTwoResponse {
        val root = JSONObject(json)
        val settings0 = root.getString("settings")
        val settings = if (settings0 != "null") {
            val sts = JSONObject(settings0)
            Settings(
                mainCurrencyDisplay = sts.getString("mainCurrencyDisplay"),
                moneyFraction = sts.getInt("moneyFraction"),
                tokenFraction = sts.getInt("tokenFraction")
            )
        } else {
            null
        }

        return LoginStepTwoResponse(
            successful = root.getBoolean("successful"),
            errorMessage = root.getString("errorMessage"),
            errorMessageCode = root.getString("errorMessageCode"),
            settings = settings,
            sessionId = root.optString("sessionId")
        )
    }


}
