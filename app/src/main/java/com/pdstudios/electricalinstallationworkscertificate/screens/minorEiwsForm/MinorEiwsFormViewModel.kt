package com.pdstudios.electricalinstallationworkscertificate.screens.minorEiwsForm

import android.app.Application
import androidx.lifecycle.AndroidViewModel
import androidx.lifecycle.LiveData
import androidx.lifecycle.MutableLiveData
import androidx.lifecycle.viewModelScope
import com.pdstudios.electricalinstallationworkscertificate.database.MEiwsDatabase
import com.pdstudios.electricalinstallationworkscertificate.database.MEiwsForm
import kotlinx.coroutines.Dispatchers
import kotlinx.coroutines.launch
import kotlinx.coroutines.withContext

class MinorEiwsFormViewModel(
    private val database: MEiwsDatabase,
    private val app: Application
): AndroidViewModel(app) {

    private var _saveForm = MutableLiveData<Boolean?>()
    val saveForm: LiveData<Boolean?>
        get() = _saveForm

    private var _eiwsKey = MutableLiveData<Long?>()
    val eiwsKey: LiveData<Long?>
        get() = _eiwsKey

    private var _mEiwsForm = MutableLiveData<MEiwsForm>()
    val mEiwsForm: LiveData<MEiwsForm>
        get() = _mEiwsForm

    init {
        _saveForm.value = null
        _mEiwsForm.value = MEiwsForm()
        _eiwsKey.value = null
    }

    fun onSave() {
        viewModelScope.launch {
            _saveForm.value = true
            saveToDatabase()
            _eiwsKey.value = getKey()
        }
    }

    private suspend fun saveToDatabase() {
        withContext(Dispatchers.IO) {
            database.mEiwsFormDao.insert(mEiwsForm.value!!)
        }
    }

    private suspend fun getKey(): Long {
        return withContext(Dispatchers.IO) {
            database.mEiwsFormDao.getLast().primaryKey
        }
    }
}