package com.pdstudios.electricalinstallationworkscertificate.database

import android.content.Context
import androidx.room.Database
import androidx.room.Room
import androidx.room.RoomDatabase

@Database(entities = [MEiwsForm::class], version = 1, exportSchema = false)
abstract class MEiwsDatabase: RoomDatabase() {

    abstract val mEiwsFormDao: MEiwsFormDao

    companion object {
        private var INSTANCE: MEiwsDatabase? = null

        fun getInstance(context: Context): MEiwsDatabase {
            synchronized(this) {
                var instance = INSTANCE

                if (instance == null) {
                    instance = Room.databaseBuilder(
                        context.applicationContext,
                        MEiwsDatabase::class.java,
                        MEiwsForm.TABLE_NAME)
                        .fallbackToDestructiveMigration()
                        .build()

                    INSTANCE = instance
                }

                return instance
            }
        }
    }
}