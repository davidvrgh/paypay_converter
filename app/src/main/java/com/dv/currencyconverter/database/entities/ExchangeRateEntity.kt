package com.dv.currencyconverter.database.entities

import androidx.room.ColumnInfo
import androidx.room.Entity

@Entity(primaryKeys = arrayOf("from_currency_code", "to_currency_code"))
data class ExchangeRateEntity(
    @ColumnInfo(name = "from_currency_code") val mFromCurrencyCode: String,
    @ColumnInfo(name = "to_currency_code") val mToCurrencyCodeL: String,
    @ColumnInfo(name = "exchange_rate") val mRate: Double
)
