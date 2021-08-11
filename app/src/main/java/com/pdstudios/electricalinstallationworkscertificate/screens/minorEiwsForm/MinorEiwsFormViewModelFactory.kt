package com.pdstudios.electricalinstallationworkscertificate.screens.minorEiwsForm

import android.app.Application
import androidx.lifecycle.ViewModel
import androidx.lifecycle.ViewModelProvider
import com.pdstudios.electricalinstallationworkscertificate.database.MEiwsFormDao

class MinorEiwsFormViewModelFactory(
    private val dao: MEiwsFormDao,
    private val app: Application
): ViewModelProvider.Factory {
    override fun <T : ViewModel?> create(modelClass: Class<T>): T {
        if (modelClass.isAssignableFrom(MinorEiwsFormViewModel::class.java)) {
            return MinorEiwsFormViewModel(dao, app) as T
        }

        throw IllegalArgumentException("ViewModel not found!")
    }

}
