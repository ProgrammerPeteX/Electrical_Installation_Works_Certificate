package com.pdstudios.electricalinstallationworkscertificate.screens.minorEiwsForm

import android.app.Application
import android.graphics.Bitmap
import android.util.Log
import androidx.lifecycle.AndroidViewModel
import androidx.lifecycle.LiveData
import androidx.lifecycle.MutableLiveData
import androidx.lifecycle.viewModelScope
import com.itextpdf.forms.PdfAcroForm
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

class MinorEiwsFormViewModel(
    private val dao: MEiwsFormDao,
    private val app: Application
): AndroidViewModel(app) {

    private lateinit var file: File
    private lateinit var pdfBitmap: Bitmap

    private var _saveForm = MutableLiveData<Boolean?>()
    val saveForm: LiveData<Boolean?>
        get() = _saveForm

    private var _eiwsKey = MutableLiveData<Long?>()
    val eiwsKey: LiveData<Long?>
        get() = _eiwsKey

    private var _mEiwsForm = MutableLiveData<MEiwsForm>()
    val mEiwsForm: LiveData<MEiwsForm>
        get() = _mEiwsForm

    private var _finishedLoadingPdf = MutableLiveData<Boolean?>()
    val finishedLoadingPdf: LiveData<Boolean?>
        get() = _finishedLoadingPdf

    val fieldList = MutableLiveData<MutableList<String>>()

    init {
        fieldList.value = mutableListOf()
        _saveForm.value = null
        _mEiwsForm.value = MEiwsForm()
        _eiwsKey.value = null
        _finishedLoadingPdf.value = null
    }

    fun onSave() {
        viewModelScope.launch {
            _saveForm.value = true
            saveToDatabase()
            _eiwsKey.value = getKey()
            Log.i("","")
        }
    }

    private suspend fun saveToDatabase() {
        withContext(Dispatchers.IO) {
            dao.insert(mEiwsForm.value!!)
        }
    }

    private suspend fun getKey(): Long {
        return withContext(Dispatchers.IO) {
            dao.getLast().primaryKey
        }
    }

    //PDF
    fun getTempPdf() {
        viewModelScope.launch(Dispatchers.Main) {

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
//            val form = fillPdfForms(pdfDocument, mEiwsForm)
            val form = PdfAcroForm.getAcroForm(pdfDocument, true)
            val fields = form?.formFields
            fields?.forEach {
                fieldList.value?.add(it.value.fieldName.toString())
            }

            Log.i("","")

            //flatten form
//            form.flattenFields()

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
            _finishedLoadingPdf.value = true
        }
    }

//    private fun fillPdfForms(pdfDocument: PdfDocument, mEiwsForm: MEiwsForm): PdfAcroForm {
//        val form = PdfAcroForm.getAcroForm(pdfDocument, true)
//        val fields = form?.formFields
//        fields?.forEach { fieldList.value?.add(it.value.fieldName.toString()) }
//        Log.i("test", "${fieldList.value}")
//
//        fields?.let {
//            it["detailsOfClient"]?.setValue(mEiwsForm.detailsOfClient)
//            it["dateMinorWorksCompleted"]?.setValue(mEiwsForm.dateMinorWorksCompleted)
//            it["installationAddress"]?.setValue(mEiwsForm.installationAddress)
//            it["descriptionMinorWorks"]?.setValue(mEiwsForm.descriptionMinorWorks)
//            it["detailsOfDepartures"]?.setValue(mEiwsForm.detailsOfDepartures)
//            it["commentsOnExistingInstallations"]?.setValue(mEiwsForm.commentsExistingInstallation)
//            it["riskAssessmentAttached"]?.setCheckType(PdfFormField.TYPE_CHECK)
//            if(mEiwsForm.riskAssessmentAttached) {
//                it["riskAssessmentAttached"]?.setValue("Yes")
//            }
//        }
//        return form
//    }

//    private suspend fun loadForm(): MEiwsForm {
//        return withContext(Dispatchers.IO) {
//            dao.get(_eiwsKey.value!!)
//        }
//    }

}