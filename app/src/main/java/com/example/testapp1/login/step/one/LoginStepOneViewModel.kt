package com.example.testapp1.login.step.one

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

private const val TAG = "LoginStepOneViewModel"

class LoginStepOneViewModel : ViewModel() {

    private var _phoneNumberIsCorrect: MutableLiveData<Boolean> = MutableLiveData()
    val phoneNumberIsCorrect: LiveData<Boolean> get() = _phoneNumberIsCorrect

    private var _loginStepOneResponseData: MutableLiveData<LoginStepOneResponse> = MutableLiveData()
    val loginStepOneResponseData: LiveData<LoginStepOneResponse> get() = _loginStepOneResponseData

    private var _timeLeftSeconds: MutableLiveData<Int> = MutableLiveData()
    val timeLeftSeconds: LiveData<Int> get() = _timeLeftSeconds

    private var _timerIsOn: MutableLiveData<Boolean> = MutableLiveData()
    val timerIsOn: LiveData<Boolean> get() = _timerIsOn

    private var _errorMsg: SingleLiveEvent<ServerResponse?> = SingleLiveEvent()
    val errorMsg: LiveData<ServerResponse?> get() = _errorMsg

    fun checkPhoneNumber(phoneNumber: String) {
        _phoneNumberIsCorrect.value = phoneNumber.length == 12 && phoneNumber[0] == '+'
    }

    fun requestCode(phoneNumber: String) {
        val phoneNumberCut = phoneNumber.substring(2)
        val url = URL("https://utcoin.one/loyality/login_step1?phone=$phoneNumberCut")
        viewModelScope.launch(Dispatchers.IO) {
            val response = getResponse(url)
            if (BuildConfig.DEBUG) Log.d(TAG, "Got access code response $response")
            if (response.code in 200..299) {
                _loginStepOneResponseData.postValue(parseLoginStepOneResponse(response.body))
            } else {
                _errorMsg.postValue(response)
            }
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

    fun clearResponse() {
        _loginStepOneResponseData = MutableLiveData()
    }
}