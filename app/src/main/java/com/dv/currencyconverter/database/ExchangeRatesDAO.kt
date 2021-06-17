package com.dv.currencyconverter.database

import androidx.lifecycle.LiveData
import androidx.room.Dao
import androidx.room.Insert
import androidx.room.OnConflictStrategy
import androidx.room.Query
import com.dv.currencyconverter.database.entities.ExchangeRateEntity

@Dao
interface ExchangeRatesDAO {

    @Query("SELECT * from ExchangeRateEntity where from_currency_code = :sourceCurrency")
    fun getExchangeRates(sourceCurrency: String): LiveData<List<ExchangeRateEntity>>

    @Insert(onConflict = OnConflictStrategy.REPLACE)
    fun insertRates(rates: List<ExchangeRateEntity>)


}