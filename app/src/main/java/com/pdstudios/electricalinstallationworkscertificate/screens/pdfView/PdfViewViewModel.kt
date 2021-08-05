package com.pdstudios.electricalinstallationworkscertificate.screens.pdfView

import android.app.Application
import android.graphics.Bitmap
import android.graphics.pdf.PdfRenderer
import android.os.ParcelFileDescriptor
import androidx.lifecycle.AndroidViewModel
import androidx.lifecycle.LiveData
import androidx.lifecycle.MutableLiveData
import androidx.lifecycle.viewModelScope
import com.itextpdf.forms.PdfAcroForm
import com.itextpdf.forms.fields.PdfFormField
import com.itextpdf.kernel.pdf.PdfDocument
import com.itextpdf.kernel.pdf.PdfOutputStream
import com.itextpdf.kernel.pdf.PdfReader
import com.itextpdf.kernel.pdf.PdfWriter
import com.pdstudios.electricalinstallationworkscertificate.database.MEiwsForm
import com.pdstudios.electricalinstallationworkscertificate.database.MEiwsFormDao
import kotlinx.coroutines.Dispatchers
import kotlinx.coroutines.launch
import kotlinx.coroutines.withContext
import java.io.File
import java.io.FileOutputStream

class PdfViewViewModel(
    val key: Long,
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

    init {
        _pageNumberText.value = "0/0"
        _pdfBitmap.value = null
        _navigateToMinorEiwsForm.value = false
    }

    fun onBack() {
        _navigateToMinorEiwsForm.value = true
    }

    fun getTempPdf() {
        viewModelScope.launch {

            //get pdf form from database
            val mEiwsForm = loadForm()

            //get file
            val filename = "minorEiwsFormTemp2.pdf"
            file = File(app.applicationContext.filesDir, filename)

            //get input and output streams
            val pdfInputStream = withContext(Dispatchers.IO){
                app.applicationContext.assets?.open("minor_electrical_installation_works_certificate.pdf")
            }
            val fileOutputStream = withContext(Dispatchers.IO) {
                FileOutputStream(file)
            }

            val pdfOutputStream = PdfOutputStream(fileOutputStream)

            //get pdf document
            val pdfReader = withContext(Dispatchers.IO) {
                PdfReader(pdfInputStream!!)
            }

            val pdfWriter = PdfWriter(pdfOutputStream)
            val pdfDocument = PdfDocument(pdfReader, pdfWriter)

            //fill forms
            val form = fillPdfForms(pdfDocument, mEiwsForm)

            //flatten form
            form.flattenFields()

            //close all streams
            withContext(Dispatchers.IO) {
                pdfDocument.close()
                pdfReader.close()
                pdfWriter.close()
                pdfInputStream?.close()
                fileOutputStream.close()
                pdfOutputStream.close()
            }


            //set bitmap to imageView
            _pdfBitmap.value = getPdfBitmap(file)
        }
    }

    private fun fillPdfForms(pdfDocument: PdfDocument, mEiwsForm: MEiwsForm): PdfAcroForm {
        val form = PdfAcroForm.getAcroForm(pdfDocument, true)
        val fields = form?.formFields

        val temp = mEiwsForm.detailsOfClient
        fields?.let {
            it["detailsOfClient"]?.setValue(mEiwsForm.detailsOfClient)
            it["dateMinorWorksCompleted"]?.setValue(mEiwsForm.dateMinorWorksCompleted)
            it["installationAddress"]?.setValue(mEiwsForm.installationAddress)
            it["descriptionMinorWorks"]?.setValue(mEiwsForm.descriptionMinorWorks)
            it["detailsOfDepartures"]?.setValue(mEiwsForm.detailsOfDepartures)
            it["commentsOnExistingInstallations"]?.setValue(mEiwsForm.commentsExistingInstallation)
            val temp2 = it["riskAssessmentAttached"]?.appearanceStates
            val temp3 = it["riskAssessmentAttached"]?.fieldFlags
            it["riskAssessmentAttached"]?.setCheckType(PdfFormField.TYPE_CHECK)
            it["riskAssessmentAttached"]?.setValue("/No")
//            it["riskAssessmentAttached"]?.setValue(
//                if (mEiwsForm.riskAssessmentAttached) {
//                    "Yes"
//                } else {
//                    "Off"
//                })
        }
        return form
    }

    private suspend fun loadForm(): MEiwsForm {
        return withContext(Dispatchers.IO) {
            dao.get(key)
        }
    }

    private fun getPdfBitmap(file: File): Bitmap {
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
            _pdfBitmap.value = getPdfBitmap(file)
        }
    }

    fun onNextPage() {
        if (page < pageCount -1) {
            page++
            _pdfBitmap.value = getPdfBitmap(file)
        }
    }
}