package com.dv.currencyconverter.database

import androidx.room.Database
import androidx.room.RoomDatabase
import com.dv.currencyconverter.database.entities.CurrencyListEntity
import com.dv.currencyconverter.database.entities.ExchangeRateEntity
import com.dv.currencyconverter.database.entities.TimeStampEntity

@Database(
    version = 2,
    entities = [CurrencyListEntity::class, ExchangeRateEntity::class, TimeStampEntity::class],
)
abstract class AppDatabase : RoomDatabase() {

    abstract fun currencyListDAO(): CurrencyListDAO

    abstract fun exchangeRateDAO(): ExchangeRatesDAO

    abstract fun timeStampDAO(): TimeStampDAO

}