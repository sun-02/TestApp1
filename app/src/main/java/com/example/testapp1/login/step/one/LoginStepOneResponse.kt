package com.example.testapp1.login.step.one

data class LoginStepOneResponse(
    val successful: Boolean,
    val errorMessage: String,
    val errorMessageCode: String,
    val settings: Any?,
    val normalizedPhone: String,
    val explainMessage: String
)