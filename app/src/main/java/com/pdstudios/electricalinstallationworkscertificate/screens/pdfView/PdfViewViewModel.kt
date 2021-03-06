package com.pdstudios.electricalinstallationworkscertificate.screens.pdfView

import android.app.Application
import android.content.Intent
import android.graphics.Bitmap
import android.graphics.pdf.PdfRenderer
import android.os.ParcelFileDescriptor
import androidx.core.content.FileProvider
import androidx.lifecycle.AndroidViewModel
import androidx.lifecycle.LiveData
import androidx.lifecycle.MutableLiveData
import androidx.lifecycle.viewModelScope
import com.pdstudios.electricalinstallationworkscertificate.database.MEiwsFormDao
import com.pdstudios.electricalinstallationworkscertificate.getCurrentDateString
import kotlinx.coroutines.launch
import java.io.File

class PdfViewViewModel(
    val dao: MEiwsFormDao,
    val app: Application
    ): AndroidViewModel(app) {


    private lateinit var file: File
    private var page = 0
    private var pageCount = 0

    private var _pageNumberText = MutableLiveData<String>()
    val pageNumberText: LiveData<String>
        get() = _pageNumberText

    private var _navigateToMinorEiwsForm = MutableLiveData<Boolean>()
    val navigateToMinorEiwsForm: LiveData<Boolean>
        get() = _navigateToMinorEiwsForm

    private var _pdfBitmap = MutableLiveData<Bitmap?>()
    val pdfBitmap: LiveData<Bitmap?>
        get() = _pdfBitmap

    val fieldList = MutableLiveData<MutableList<String>>()

    init {
        fieldList.value = mutableListOf()
        _pageNumberText.value = "0/0"
        _pdfBitmap.value = null
        _navigateToMinorEiwsForm.value = false
    }

    fun onBack() {
        _navigateToMinorEiwsForm.value = true
    }

    fun getTempPdf() {
        viewModelScope.launch {
            //get file
            val filename = "minorEiwsFormTemp2.pdf"
            file = File(app.applicationContext.filesDir, filename)

            //set bitmap to imageView
            _pdfBitmap.value = getPdfBitmap()
        }
    }

    private fun getPdfBitmap(): Bitmap {
        val fileDescriptor = ParcelFileDescriptor.open(file, ParcelFileDescriptor.MODE_READ_ONLY)
        val pdfRenderer = PdfRenderer(fileDescriptor)
        pageCount = pdfRenderer.pageCount
        _pageNumberText.value = "${page+1}/${pageCount}"
        val currentPage = pdfRenderer.openPage(page)
        val width = 1000;
        val height: Int = (width*1.4142).toInt()
        val bitmap = Bitmap.createBitmap(width, height, Bitmap.Config.ARGB_8888)
        currentPage.render(bitmap, null, null, PdfRenderer.Page.RENDER_MODE_FOR_DISPLAY)
        return bitmap
    }

    //Page Navigation
    fun onPreviousPage() {
        if (page > 0) {
            page--
            _pdfBitmap.value = getPdfBitmap()
        }
    }

    fun onNextPage() {
        if (page < pageCount -1) {
            page++
            _pdfBitmap.value = getPdfBitmap()
        }
    }

    fun getShareIntent() : Intent {
        val pdfUri = FileProvider.getUriForFile(
            app.baseContext,
            app.applicationContext.packageName + ".provider",
            file)

        val currentDate = getCurrentDateString()

        val intent = Intent(Intent.ACTION_SEND)
        intent.type = app.contentResolver.getType(pdfUri)
        intent.putExtra(Intent.EXTRA_SUBJECT,
            "Minor_Electrical_Installation_Works_Certificate_$currentDate")
        intent.putExtra(Intent.EXTRA_STREAM, pdfUri)
        return intent
    }
}