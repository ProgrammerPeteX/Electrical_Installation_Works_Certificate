package com.pdstudios.electricalinstallationworkscertificate.screens.pdfView

import android.app.Application
import androidx.lifecycle.ViewModel
import androidx.lifecycle.ViewModelProvider
import com.pdstudios.electricalinstallationworkscertificate.database.MEiwsFormDao

class PdfViewViewModelFactory(
    private val key: Long,
    private val database: MEiwsFormDao,
    private val application: Application
): ViewModelProvider.Factory {
    override fun <T : ViewModel?> create(modelClass: Class<T>): T {
        if (modelClass.isAssignableFrom(PdfViewViewModel::class.java)) {
            return PdfViewViewModel(key, database, application) as T
        }
        throw IllegalArgumentException("ViewModel not found")
    }

}
