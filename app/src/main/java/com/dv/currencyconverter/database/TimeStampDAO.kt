package com.dv.currencyconverter.database

import androidx.room.Dao
import androidx.room.Insert
import androidx.room.OnConflictStrategy
import androidx.room.Query
import com.dv.currencyconverter.database.entities.TimeStampEntity

@Dao
interface TimeStampDAO {

    @Query("select * from TimeStampEntity where api_endpoint_identifier = :apiId")
    fun getTimeStampForApiID(apiId: String): TimeStampEntity

    @Insert(onConflict = OnConflictStrategy.REPLACE)
    fun insert(entity: TimeStampEntity)
}