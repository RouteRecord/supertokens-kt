package com.supertokens.sdk.recipes.totp.models

data class TotpDevice(
    val name: String,
    val period: Long,
    val skew: Long,
    val verified: Boolean,
)
