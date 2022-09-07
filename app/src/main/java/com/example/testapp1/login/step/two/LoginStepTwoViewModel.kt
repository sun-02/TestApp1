package com.example.testapp1.login.step.two

import android.os.CountDownTimer
import android.util.Log
import androidx.lifecycle.LiveData
import androidx.lifecycle.MutableLiveData
import androidx.lifecycle.ViewModel
import androidx.lifecycle.viewModelScope
import com.example.testapp1.BuildConfig
import com.example.testapp1.ServerResponse
import com.example.testapp1.SingleLiveEvent
import com.example.testapp1.login.step.getResponse
import kotlinx.coroutines.Dispatchers
import kotlinx.coroutines.launch
import org.json.JSONObject
import java.net.URL

private const val TAG = "LoginStepTwoViewModel"

class LoginStepTwoViewModel : ViewModel() {

    private var _loginStepTwoResponseData: MutableLiveData<LoginStepTwoResponse> = MutableLiveData()
    val loginStepTwoResponseData: LiveData<LoginStepTwoResponse> get() = _loginStepTwoResponseData

    private var _errorMsg: SingleLiveEvent<ServerResponse?> = SingleLiveEvent()
    val errorMsg: LiveData<ServerResponse?> get() = _errorMsg

    private var _timeLeftSeconds: MutableLiveData<Int> = MutableLiveData()
    val timeLeftSeconds: LiveData<Int> get() = _timeLeftSeconds

    private var _timerIsOn: MutableLiveData<Boolean> = MutableLiveData()
    val timerIsOn: LiveData<Boolean> get() = _timerIsOn

    fun requestNewCode(phoneNumber: String) {
        val phoneNumberCut = phoneNumber.substring(2)
        val url = URL("https://utcoin.one/loyality/login_step1?phone=$phoneNumberCut")
        viewModelScope.launch(Dispatchers.IO) {
            val response = getResponse(url)
            if (BuildConfig.DEBUG) Log.d(TAG, "Got new code response $response")
        }
    }

    fun requestSessionId(phoneNumber: String, code: String) {
        val url = URL("https://utcoin.one/loyality/login_step2?phone=$phoneNumber&password=$code")
        viewModelScope.launch(Dispatchers.IO) {
            val response = getResponse(url)
            if (BuildConfig.DEBUG) Log.d(TAG, "Got session ID response $response")
            if (response.code in 200..299) {
                _loginStepTwoResponseData.postValue(parseLoginStepTwoResponse(response.body))
            } else {
                _errorMsg.postValue(response)
            }
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

    fun startTimer(timerInitialValue: Long) {
        val countDownInterval = 1000L

        _timeLeftSeconds.value = (timerInitialValue / 1000).toInt()
        _timerIsOn.value = true

        object : CountDownTimer(timerInitialValue, countDownInterval) {

            override fun onTick(millisUntilFinished: Long) {
                _timeLeftSeconds.value = (millisUntilFinished / 1000).toInt()
            }

            override fun onFinish() {
                _timerIsOn.value = false
            }
        }.start()
    }
}
