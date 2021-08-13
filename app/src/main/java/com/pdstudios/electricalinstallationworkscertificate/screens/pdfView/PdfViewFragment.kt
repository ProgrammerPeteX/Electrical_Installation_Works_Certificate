package com.pdstudios.electricalinstallationworkscertificate.screens.pdfView

import android.os.Bundle
import android.view.*
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

        //Binding
        binding = DataBindingUtil.inflate(inflater, R.layout.fragment_pdf_view,container,false)

        //Database
        val application = requireNotNull(this.activity).application
        val database = MEiwsDatabase.getInstance(application).mEiwsFormDao

        //ViewModel
        val factory = PdfViewViewModelFactory(database, application)
        viewModel = ViewModelProvider(this, factory).get(PdfViewViewModel::class.java)
        binding.pdfViewViewModel = viewModel
        binding.lifecycleOwner = this



        //Menu - Share PDF
        setHasOptionsMenu(true)

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


    // Share
    private fun shareSuccess() {
        startActivity(viewModel.getShareIntent())
    }

    // Options menu
    override fun onCreateOptionsMenu(menu: Menu, inflater: MenuInflater) {
        super.onCreateOptionsMenu(menu, inflater)
        inflater.inflate(R.menu.share_menu, menu)
    }

    override fun onOptionsItemSelected(item: MenuItem): Boolean {
        when (item.itemId) {
            R.id.menuItem_sharePdf -> shareSuccess();
        }

        return super.onOptionsItemSelected(item)
    }
}

