package com.dv.currencyconverter.network

import com.dv.currencyconverter.ExchangeRates
import com.dv.currencyconverter.Rate
import com.google.gson.Gson
import okhttp3.ResponseBody
import org.json.JSONException
import org.json.JSONObject
import retrofit2.Converter
import java.io.IOException
import java.util.*

class ExchangeRatesConverter : Converter<ResponseBody, ExchangeRates> {
    @Throws(IOException::class)
    override fun convert(value: ResponseBody): ExchangeRates {
        val json = value.string()
        val gson = Gson()
        val exchangeRates = gson.fromJson(json, ExchangeRates::class.java)
        try {
            var jsonObject: JSONObject? = JSONObject(json)
            jsonObject = jsonObject?.optJSONObject("quotes")
            if (jsonObject != null) {
                val stringIterator = jsonObject.keys()
                val rateList: MutableList<Rate> = ArrayList()
                while (stringIterator.hasNext()) {
                    val currencyCode = stringIterator.next()
                    val rate = jsonObject.optDouble(currencyCode)
                    val rateObj = Rate(
                        exchangeRates.mSource!!, currencyCode.substring(3, 6), rate
                    )
                    rateList.add(rateObj)
                }
                exchangeRates.mRateList = rateList
            }
        } catch (e: JSONException) {
            e.printStackTrace()
        }
        return exchangeRates
    }
}