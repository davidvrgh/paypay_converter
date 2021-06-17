package com.dv.currencyconverter.database

import androidx.lifecycle.LiveData
import androidx.room.Dao
import androidx.room.Insert
import androidx.room.OnConflictStrategy
import androidx.room.Query
import com.dv.currencyconverter.database.entities.CurrencyListEntity

@Dao
interface CurrencyListDAO {

    @Query("SELECT * from CurrencyListEntity")
    fun getCurrencyLis(): LiveData<List<CurrencyListEntity>>

    @Insert(onConflict = OnConflictStrategy.REPLACE)
    fun insertCurrencies(currencies: List<CurrencyListEntity>)

}