package com.dv.currencyconverter.database.entities

import androidx.room.ColumnInfo
import androidx.room.Entity
import androidx.room.PrimaryKey

@Entity
data class CurrencyListEntity(
    @PrimaryKey
    @ColumnInfo(name = "currency_code") val mCurrencyCode: String,
    @ColumnInfo(name = "currency_name") val mCurrencyName: String
) {

    override fun toString(): String {
        return mCurrencyCode
    }

}
