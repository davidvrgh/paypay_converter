package com.dv.currencyconverter


import com.google.gson.annotations.SerializedName

data class CurrencyList(
    @SerializedName("success")
    var mSuccess: Boolean?,
    var mCurrencies: List<Currency>
) : BaseResponse()

data class Currency(var mCurrencyCode: String, var mCurrencyName: String)