package com.dv.currencyconverter.database.entities

import androidx.room.ColumnInfo
import androidx.room.Entity
import androidx.room.PrimaryKey

@Entity
data class TimeStampEntity(
    @PrimaryKey
    @ColumnInfo(name = "api_endpoint_identifier") val mApiId: String,
    @ColumnInfo(name = "last_request_timestamp") val mTimeStamp: Long
){
    companion object{
        const val API_ID_CURRENCY_LIST = "id_currency_list"
    }
}

