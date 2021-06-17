package com.dv.currencyconverter.network

import com.dv.currencyconverter.Currency
import com.dv.currencyconverter.CurrencyList
import com.google.gson.Gson
import okhttp3.ResponseBody
import org.json.JSONException
import org.json.JSONObject
import retrofit2.Converter
import java.io.IOException
import java.util.*

class CurrencyListConverter : Converter<ResponseBody, CurrencyList> {
    @Throws(IOException::class)
    override fun convert(value: ResponseBody): CurrencyList {
        val json = value.string()
        val gson = Gson()
        val currencyList = gson.fromJson(json, CurrencyList::class.java)
        try {
            var jsonObject: JSONObject? = JSONObject(json)
            jsonObject = jsonObject?.optJSONObject("currencies")
            if (jsonObject != null) {
                val stringIterator = jsonObject.keys()
                val currencies: MutableList<Currency> = ArrayList()
                while (stringIterator.hasNext()) {
                    val currencyOcde = stringIterator.next()
                    currencies.add(Currency(currencyOcde, jsonObject.optString(currencyOcde)))
                }
                currencyList.mCurrencies = currencies
            }
        } catch (e: JSONException) {
            e.printStackTrace()
        }
        return currencyList
    }
}