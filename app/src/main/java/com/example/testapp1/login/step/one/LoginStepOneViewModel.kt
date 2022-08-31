package com.example.testapp1.login.step.one

import android.os.CountDownTimer
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

private const val TAG = "LoginStepOneViewModel"

class LoginStepOneViewModel : ViewModel() {

    private var _phoneNumberIsCorrect: MutableLiveData<Boolean> = MutableLiveData()
    val phoneNumberIsCorrect: LiveData<Boolean> get() = _phoneNumberIsCorrect

    private var _loginStepOneResponse: MutableLiveData<LoginStepOneResponse> = MutableLiveData()
    val loginStepOneResponse: LiveData<LoginStepOneResponse> get() = _loginStepOneResponse

    private var _timeLeft: MutableLiveData<Long> = MutableLiveData()
    val timeLeft: LiveData<Long> get() = _timeLeft

    private var _timerIsOn: MutableLiveData<Boolean> = MutableLiveData()
    val timerIsOn: LiveData<Boolean> get() = _timerIsOn

    private var _errorMsg: SingleLiveEvent<ServerResponse?> = SingleLiveEvent()
    val errorMsg: LiveData<ServerResponse?> get() = _errorMsg

    fun getTextWatcher(): TextWatcher =
        object : TextWatcher {
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
                checkPhoneNumber(s.toString())
            }

        }

    fun checkPhoneNumber(phoneNumber: String) {
        _phoneNumberIsCorrect.value = phoneNumber.length == 12 && phoneNumber[0] == '+'
    }

    fun sendCode(phoneNumber: String) {
        val phoneNumberCut = phoneNumber.substring(2)
        val url = URL("https://utcoin.one/loyality/login_step1?phone=$phoneNumberCut")
        val urlConnection: HttpURLConnection = url.openConnection() as HttpURLConnection
        try {
            val response = ServerResponse(urlConnection.responseCode, urlConnection.responseMessage)
            if (BuildConfig.DEBUG) Log.d(TAG, "Got response $response")
            if (response.code in 200..299) {
                _loginStepOneResponse.value = parseLoginStepOneResponse(response.body)
            } else {
                _errorMsg.value = response
            }
        } catch (e: IOException) {
            _errorMsg.value = ServerResponse(0, e.stackTraceToString())
        } finally {
            urlConnection.disconnect()
        }
    }

    private fun parseLoginStepOneResponse(json: String): LoginStepOneResponse {
        val root = JSONObject(json)
        return LoginStepOneResponse(
            successful = root.getBoolean("successful"),
            errorMessage = root.getString("errorMessage"),
            errorMessageCode = root.getString("errorMessageCode"),
            settings = null,
            normalizedPhone = root.optString("normalizedPhone"),
            explainMessage = root.optString("explainMessage")
        )
    }

    fun startTimer() {
        val timerInitialValue = 60000L
        val countDownInterval = 1000L

        _timeLeft.value = timerInitialValue
        _timerIsOn.value = true

        object : CountDownTimer(timerInitialValue, countDownInterval) {

            override fun onTick(millisUntilFinished: Long) {
                _timeLeft.value = millisUntilFinished / 1000
            }

            override fun onFinish() {
                _timerIsOn.value = false
            }
        }.start()
    }
}