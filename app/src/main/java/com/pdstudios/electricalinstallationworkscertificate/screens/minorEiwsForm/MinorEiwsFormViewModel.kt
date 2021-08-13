package com.pdstudios.electricalinstallationworkscertificate.screens.minorEiwsForm

import android.app.Application
import android.util.Log
import androidx.lifecycle.AndroidViewModel
import androidx.lifecycle.LiveData
import androidx.lifecycle.MutableLiveData
import androidx.lifecycle.viewModelScope
import com.itextpdf.forms.PdfAcroForm
import com.itextpdf.forms.fields.PdfFormField
import com.itextpdf.kernel.pdf.*
import com.pdstudios.electricalinstallationworkscertificate.FormField
import com.pdstudios.electricalinstallationworkscertificate.database.MEiwsFormDao
import kotlinx.coroutines.Dispatchers
import kotlinx.coroutines.launch
import kotlinx.coroutines.withContext
import java.io.File
import java.io.FileOutputStream
import java.io.InputStream

class MinorEiwsFormViewModel(
    private val dao: MEiwsFormDao,
    private val app: Application
): AndroidViewModel(app) {

    var formFieldList: MutableLiveData<MutableList<FormField>>
    = MutableLiveData(mutableListOf())

    private lateinit var file: File

    private var _saveForm = MutableLiveData<Boolean?>()
    val saveForm: LiveData<Boolean?>
        get() = _saveForm

    private var _finishedLoadingPdf = MutableLiveData<Boolean?>()
    val finishedLoadingPdf: LiveData<Boolean?>
        get() = _finishedLoadingPdf

    // STREAMS
    private lateinit var fileInputStream: InputStream
    private lateinit var fileOutputStream: FileOutputStream
    private lateinit var pdfOutputStream: PdfOutputStream
    private lateinit var pdfReader: PdfReader
    private lateinit var pdfWriter: PdfWriter
    private lateinit var pdfDocument: PdfDocument


    init {
        _saveForm.value = null
        _finishedLoadingPdf.value = null

    }

    fun onSave() {
        viewModelScope.launch {
            _saveForm.value = true
            //fill forms
            val form = fillPdfForms(pdfDocument)
            form.flattenFields()

            //close streams
            closeStreams()
        }
    }

    //PDF
    fun getTempPdf() {
        viewModelScope.launch(Dispatchers.Main) {

            //get file
            val filename = "minorEiwsFormTemp2.pdf"
            file = File(app.applicationContext.filesDir, filename)

            //get input and output streams
            fileInputStream = withContext(Dispatchers.IO){
                app.applicationContext.assets?.
                open("minor_electrical_installation_works_certificate.pdf")!!
            }
            fileOutputStream = withContext(Dispatchers.IO) { FileOutputStream(file) }

            pdfOutputStream = PdfOutputStream(fileOutputStream)

            //get pdf document
            pdfReader = withContext(Dispatchers.IO) { PdfReader(fileInputStream) }

            pdfWriter = PdfWriter(pdfOutputStream)
            pdfDocument = PdfDocument(pdfReader, pdfWriter)

            //get fieldList
            val form = PdfAcroForm.getAcroForm(pdfDocument, true)
            val fields = form.formFields
            fields?.forEach {
                val isEditText = it.value.formType == PdfName.Tx
                val isCheckBox = it.value.formType == PdfName.Btn
                val formField = FormField()
                formField.name = it.key
                formField.type = when {
                    isEditText -> "EditText"
                    isCheckBox -> "CheckBox"
                    else -> throw IllegalStateException("State is neither EditText nor CheckBox")
                }
                formFieldList.value!!.add(formField)
            }
            _finishedLoadingPdf.value = true
        }
    }

    private fun fillPdfForms(pdfDocument: PdfDocument): PdfAcroForm {
        val pdfAcroForm = PdfAcroForm.getAcroForm(pdfDocument, true)
        val fields = pdfAcroForm?.formFields

        formFieldList.value?.forEach { formField ->
            val isEditText = formField.type == "EditText"
            val isCheckBox = formField.type == "CheckBox"
            fields?.get(formField.name)?.let { field ->
                when {
                    isEditText ->  field.setValue(formField.value)
                    isCheckBox -> {
                        field.setCheckType(
                            if (formField.value == "true") PdfFormField.TYPE_CHECK
                            else PdfFormField.TYPE_CROSS)
                        field.setValue("Yes")
                    }
                    else -> null
                }
            }

            Log.i("test", "${formField.name}--${fields?.get(formField.name)?.valueAsString!!}")
        }
        return pdfAcroForm
    }

    private fun closeStreams() {
        pdfDocument.close()
        pdfReader.close()

        pdfWriter.close()
        fileInputStream.close()
        fileOutputStream.close()
        pdfOutputStream.close()
    }
}