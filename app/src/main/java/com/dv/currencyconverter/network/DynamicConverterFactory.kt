package com.dv.currencyconverter.network

import com.dv.currencyconverter.annotations.AnnotCurrency
import com.dv.currencyconverter.annotations.AnnotRates
import okhttp3.ResponseBody
import retrofit2.Converter
import retrofit2.Retrofit
import java.lang.reflect.Type

class DynamicConverterFactory : Converter.Factory() {

    override fun responseBodyConverter(
        type: Type,
        annotations: Array<Annotation>,
        retrofit: Retrofit
    ): Converter<ResponseBody, *>? {
        annotations.forEach { annotation ->

            return when (annotation.annotationClass) {
                AnnotCurrency::class -> CurrencyListConverter()
                AnnotRates::class -> ExchangeRatesConverter()
                else -> null
            }
        }
        return null
    }

}
