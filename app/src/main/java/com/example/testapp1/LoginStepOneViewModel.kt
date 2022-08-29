package com.example.testapp1

import android.text.Editable
import android.text.TextWatcher
import androidx.lifecycle.LiveData
import androidx.lifecycle.MutableLiveData
import androidx.lifecycle.ViewModel

class LoginStepOneViewModel : ViewModel() {
    private var _phoneNumberIsCorrect: MutableLiveData<Boolean> = MutableLiveData(false)
    val phoneNumberIsCorrect: LiveData<Boolean> get() = _phoneNumberIsCorrect

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

    private fun checkPhoneNumber(phoneNumber: String) {
        _phoneNumberIsCorrect.value = phoneNumber.length == 12 && phoneNumber[0] == '+'
    }
}