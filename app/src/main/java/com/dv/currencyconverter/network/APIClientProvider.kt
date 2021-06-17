package com.dv.currencyconverter.network

import retrofit2.Retrofit


class APIClientProvider {


    companion object {

        private var mApiClient: ApiClient? = null

        fun getClient(): ApiClient {
            if (mApiClient == null) {
                val retrofit = Retrofit.Builder()
                    .baseUrl("http://api.currencylayer.com/")
                    .addConverterFactory(DynamicConverterFactory())
                    .build()
                mApiClient = retrofit.create(ApiClient::class.java)
            }
            return mApiClient!!
        }

    }
}