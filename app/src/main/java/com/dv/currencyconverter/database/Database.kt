package com.dv.currencyconverter.database

import android.content.Context
import androidx.room.Room

class Database {

    companion object {

        private var mDatabase: AppDatabase? = null

        fun getInstance(context: Context): AppDatabase {
            if (mDatabase == null) {
                mDatabase = Room.databaseBuilder(
                    context.applicationContext,
                    AppDatabase::class.java, "database_currencies"
                ).build()
            }
            return mDatabase!!
        }

    }
}