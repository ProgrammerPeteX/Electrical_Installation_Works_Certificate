package com.pdstudios.electricalinstallationworkscertificate.database

import androidx.lifecycle.LiveData
import androidx.room.Dao
import androidx.room.Insert
import androidx.room.Query
import androidx.room.Update

@Dao
interface MEiwsFormDao {

    @Insert
    fun insert(mEiwsForm: MEiwsForm)

    @Update
    fun update(mEiwsForm: MEiwsForm)

    @Query("SELECT * FROM minor_eiws_form_table WHERE primaryKey =:key")
    fun get(key: Long): MEiwsForm

    @Query("SELECT * FROM minor_eiws_form_table ORDER BY primaryKey DESC")
    fun getAll(): LiveData<MEiwsForm>

    @Query("SELECT * FROM minor_eiws_form_table ORDER BY primaryKey DESC LIMIT 1")
    fun getLast(): MEiwsForm

    @Query("DELETE FROM minor_eiws_form_table")
    fun clearAll()

    @Query("DELETE FROM minor_eiws_form_table WHERE primaryKey=:key")
    fun delete(key: Long)
}