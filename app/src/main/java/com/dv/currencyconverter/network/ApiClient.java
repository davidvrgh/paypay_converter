package com.dv.currencyconverter.network;

import com.dv.currencyconverter.CurrencyList;
import com.dv.currencyconverter.ExchangeRates;
import com.dv.currencyconverter.annotations.AnnotCurrency;
import com.dv.currencyconverter.annotations.AnnotRates;

import retrofit2.Call;
import retrofit2.http.GET;
import retrofit2.http.Query;

public interface ApiClient {

    @GET("list?")
    @AnnotCurrency
    Call<CurrencyList> getCurrencyList(@Query("access_key") String accessKey);

    @GET("/live?")
    @AnnotRates
    Call<ExchangeRates> getExchangeRates(@Query("access_key") String accessKey, @Query("source") String source);

}
