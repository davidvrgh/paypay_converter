package com.dv.currencyconverter

import com.google.gson.annotations.SerializedName

open class BaseResponse {
    @SerializedName("privacy")
    var mPrivacy: String? = null

    @SerializedName("terms")
    var mTerms: String? = null

    @SerializedName("error")
    var mError: Error? = null
}

data class Error(
    @SerializedName("code")
    var mCode: Int?,
    @SerializedName("info")
    var mInfo: String
)
