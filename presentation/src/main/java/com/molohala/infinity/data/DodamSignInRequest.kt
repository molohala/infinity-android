package com.molohala.infinity.data

data class DodamSignInRequest(
    public val id: String,
    public val pw: String,
    public val clientId: String,
    public val redirectUrl: String
)