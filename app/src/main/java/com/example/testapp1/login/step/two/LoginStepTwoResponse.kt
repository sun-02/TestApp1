package com.example.testapp1.login.step.two

class LoginStepTwoResponse(
    val successful: Boolean,
    val errorMessage: String,
    val errorMessageCode: String,
    val settings: Settings?,
    val sessionId: String
)

data class Settings(
    val mainCurrencyDisplay: String,
    val moneyFraction: Int,
    val tokenFraction: Int
)