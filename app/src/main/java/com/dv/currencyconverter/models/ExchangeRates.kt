package com.dv.currencyconverter


import com.google.gson.annotations.SerializedName

data class ExchangeRates(
    @SerializedName("source")
    var mSource: String?,
    @SerializedName("success")
    var mSuccess: Boolean?,
    @SerializedName("timestamp")
    var mTimestamp: Int?,
    var mRateList: List<Rate>
): BaseResponse()

data class Rate(var mFromCurrency: String, var toCurrency: String, var mRate: Double)
