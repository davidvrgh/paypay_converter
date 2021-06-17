package com.dv.currencyconverter

import android.app.Application
import androidx.lifecycle.AndroidViewModel
import androidx.lifecycle.LiveData
import androidx.lifecycle.viewModelScope
import com.dv.currencyconverter.database.AppDatabase
import com.dv.currencyconverter.database.Database
import com.dv.currencyconverter.database.entities.CurrencyListEntity
import com.dv.currencyconverter.database.entities.ExchangeRateEntity
import com.dv.currencyconverter.database.entities.TimeStampEntity
import com.dv.currencyconverter.network.APIClientProvider
import com.dv.currencyconverter.network.ApiClient
import kotlinx.coroutines.Dispatchers
import kotlinx.coroutines.launch
import retrofit2.Call
import retrofit2.Callback
import retrofit2.Response
import java.util.ArrayList

class ConverterViewModel(application: Application) : AndroidViewModel(application) {

    private val DURATION_30_MINS = 1000 * 60 * 30

    private val mDatabase: AppDatabase = Database.getInstance(getApplication())
    private val mApiClient: ApiClient = APIClientProvider.getClient()
    var mSourceCurrency: String? = null


    fun getCurrencyList(listener: CurrencyListErrorListener): LiveData<List<CurrencyListEntity>> {
        fetchCurrencyListFromNetworkIfOutdated(listener)
        return mDatabase.currencyListDAO().getCurrencyLis()
    }

    private fun fetchCurrencyListFromNetworkIfOutdated(listener: CurrencyListErrorListener) {
        var timeStampEntity: TimeStampEntity? = null
        viewModelScope.launch(Dispatchers.Default) {
            timeStampEntity =
                mDatabase.timeStampDAO().getTimeStampForApiID(TimeStampEntity.API_ID_CURRENCY_LIST)
        }.invokeOnCompletion {
            val currentTime = System.currentTimeMillis()
            if (timeStampEntity == null || currentTime - timeStampEntity!!.mTimeStamp > DURATION_30_MINS) {
                requestCurrencyListAPI(listener)
            }
        }
    }

    private fun fetchExchangeRatesFromNetworkIfOutdated(
        source: String,
        listener: ExchangeListErrorListener
    ) {
        var timeStampEntity: TimeStampEntity? = null
        viewModelScope.launch(Dispatchers.Default) {
            timeStampEntity =
                mDatabase.timeStampDAO().getTimeStampForApiID(source)
        }.invokeOnCompletion {
            val currentTime = System.currentTimeMillis()
            if (timeStampEntity == null || currentTime - timeStampEntity!!.mTimeStamp > DURATION_30_MINS) {
                requestExchangeRatesAPI(source, listener)
            }
        }
    }

    fun getExchangeRates(
        source: String,
        listener: ExchangeListErrorListener
    ): LiveData<List<ExchangeRateEntity>> {
        fetchExchangeRatesFromNetworkIfOutdated(source, listener)
        return mDatabase.exchangeRateDAO().getExchangeRates(source)
    }

    private fun requestExchangeRatesAPI(
        source: String,
        listener: ExchangeListErrorListener
    ) {
        mApiClient.getExchangeRates(AccessKey.ACCESS_KEY, source).enqueue(object :
            Callback<ExchangeRates> {
            override fun onResponse(
                call: Call<ExchangeRates>?,
                response: Response<ExchangeRates>?
            ) {
                if (response?.body()?.mSuccess == true) {
                    viewModelScope.launch(Dispatchers.Default) {
                        val exchangeRateEntities: MutableList<ExchangeRateEntity> = ArrayList()
                        for (rate in response!!.body().mRateList) {
                            exchangeRateEntities.add(
                                ExchangeRateEntity(
                                    rate.mFromCurrency,
                                    rate.toCurrency,
                                    rate.mRate
                                )
                            )
                        }
                        mDatabase.exchangeRateDAO().insertRates(exchangeRateEntities)
                        mDatabase.timeStampDAO()
                            .insert(TimeStampEntity(source, System.currentTimeMillis()))
                    }
                } else {
                    propagateError(response?.body()?.mError?.mInfo)
                }
            }

            override fun onFailure(call: Call<ExchangeRates>?, t: Throwable?) {
                propagateError("Failed to fetch exchange rates")
            }

            fun propagateError(error: String?) {
                listener.onExchangeRateApiFailed(error)
            }
        })
    }

    private fun requestCurrencyListAPI(listener: CurrencyListErrorListener) {
        mApiClient.getCurrencyList(AccessKey.ACCESS_KEY).enqueue(object :
            Callback<CurrencyList> {
            override fun onResponse(
                call: Call<CurrencyList>,
                response: Response<CurrencyList>
            ) {
                var currencyList = response.body()
                if (currencyList.mSuccess == true) {
                    viewModelScope.launch(Dispatchers.Default) {
                        val currencyListEntities: MutableList<CurrencyListEntity> = ArrayList()
                        for (currency in currencyList.mCurrencies) {
                            currencyListEntities.add(
                                CurrencyListEntity(
                                    currency.mCurrencyCode,
                                    currency.mCurrencyName
                                )
                            )
                        }
                        mDatabase.currencyListDAO().insertCurrencies(currencyListEntities)
                        mDatabase.timeStampDAO().insert(
                            TimeStampEntity(
                                TimeStampEntity.API_ID_CURRENCY_LIST,
                                System.currentTimeMillis()
                            )
                        )
                    }
                } else {
                    propagateError(currencyList.mError?.mInfo)
                }

            }

            override fun onFailure(call: Call<CurrencyList>, t: Throwable) {
                propagateError("Failed to fetch currency list")
            }

            fun propagateError(error: String?) {
                listener.onCurrencyListApiFailed(error)
            }
        })
    }

}

interface CurrencyListErrorListener {

    fun onCurrencyListApiFailed(error: String?)

}

interface ExchangeListErrorListener {

    fun onExchangeRateApiFailed(error: String?)

}