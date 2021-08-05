package com.pdstudios.electricalinstallationworkscertificate.screens.pdfView

import android.os.Bundle
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import androidx.databinding.DataBindingUtil
import androidx.fragment.app.Fragment
import androidx.lifecycle.ViewModelProvider
import androidx.navigation.fragment.findNavController
import com.pdstudios.electricalinstallationworkscertificate.R
import com.pdstudios.electricalinstallationworkscertificate.database.MEiwsDatabase
import com.pdstudios.electricalinstallationworkscertificate.databinding.FragmentPdfViewBinding

class PdfViewFragment: Fragment() {
    private lateinit var binding: FragmentPdfViewBinding
    private lateinit var viewModel: PdfViewViewModel

    override fun onCreateView(
        inflater: LayoutInflater,
        container: ViewGroup?,
        savedInstanceState: Bundle?
    ): View? {
        //Args
        val args = PdfViewFragmentArgs.fromBundle(requireArguments())

        //Binding
        binding = DataBindingUtil.inflate(inflater, R.layout.fragment_pdf_view,container,false)

        //Database
        val application = requireNotNull(this.activity).application
        val database = MEiwsDatabase.getInstance(application).mEiwsFormDao

        //ViewModel
        val factory = PdfViewViewModelFactory(args.key, database, application)
        viewModel = ViewModelProvider(this, factory).get(PdfViewViewModel::class.java)
        binding.pdfViewViewModel = viewModel
        binding.lifecycleOwner = this

        //GetTempPdf
        viewModel.getTempPdf()

        //Observers
        viewModel.navigateToMinorEiwsForm.observe(viewLifecycleOwner) { navigate ->
            if (navigate) {
                this.findNavController().navigate(
                    PdfViewFragmentDirections.actionPdfViewFragmentToMinorEiwsForm())
            }
        }

        viewModel.pdfBitmap.observe(viewLifecycleOwner) {bitmap ->
            bitmap?.let {
                binding.imageViewPdf.setImageBitmap(it)
            }
        }

        return binding.root
    }
}
