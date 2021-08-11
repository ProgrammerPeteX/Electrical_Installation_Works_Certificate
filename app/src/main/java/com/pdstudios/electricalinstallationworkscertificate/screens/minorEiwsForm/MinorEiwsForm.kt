package com.pdstudios.electricalinstallationworkscertificate.screens.minorEiwsForm

import android.os.Bundle
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import androidx.databinding.DataBindingUtil
import androidx.fragment.app.Fragment
import androidx.lifecycle.ViewModelProvider
import androidx.navigation.fragment.findNavController
import androidx.recyclerview.widget.LinearLayoutManager
import androidx.recyclerview.widget.RecyclerView
import com.pdstudios.electricalinstallationworkscertificate.R
import com.pdstudios.electricalinstallationworkscertificate.database.MEiwsDatabase
import com.pdstudios.electricalinstallationworkscertificate.databinding.FragmentMinorEiwsFormBinding

class MinorEiwsForm : Fragment() {

    private lateinit var binding: FragmentMinorEiwsFormBinding
    private lateinit var viewModel: MinorEiwsFormViewModel
    private var layoutManager: RecyclerView.LayoutManager? = null
    private var adapter: RecyclerView.Adapter<PdfViewRecyclerAdapter.ViewHolder>? = null

    override fun onCreateView(
        inflater: LayoutInflater,
        container: ViewGroup?,
        savedInstanceState: Bundle?
    ): View? {

        //Binding
        binding = DataBindingUtil.inflate(inflater, R.layout.fragment_minor_eiws_form,container,false)

        //Database
        val application = requireNotNull(this.activity).application
        val dao = MEiwsDatabase.getInstance(application).mEiwsFormDao

        //ViewModel
        val temp = 0 // DELETE
        val factory = MinorEiwsFormViewModelFactory(dao, application)
        viewModel = ViewModelProvider(this, factory).get(MinorEiwsFormViewModel::class.java)
        binding.minorEiwsFormViewModel = viewModel
        binding.lifecycleOwner = this


        //get Pdf
        viewModel.getTempPdf()

        //RecyclerView
        viewModel.finishedLoadingPdf.observe(viewLifecycleOwner) {
            it?.let {
                val something = viewModel.fieldList.value
                layoutManager = LinearLayoutManager(this.context)
                binding.recyclerView.layoutManager = layoutManager
                adapter = PdfViewRecyclerAdapter(viewModel.fieldList, viewLifecycleOwner)
                binding.recyclerView.adapter = adapter
            }

        }


        //Observers
        viewModel.saveForm.observe(viewLifecycleOwner) {
            it?.let {
                getDetails()
            }
        }

        viewModel.eiwsKey.observe(viewLifecycleOwner) { key ->
            key?.let {
//                navigate(key)
//                clearAll()
            }
        }
        return binding.root
    }

    private fun navigate(key: Long) {
        this.findNavController().navigate(MinorEiwsFormDirections.actionMinorEiwsFormToPdfViewFragment(key))
    }

    private fun getDetails() {
//        viewModel.mEiwsForm.value?.let {
//            it.detailsOfClient = binding.editTextDetailsClient.text.toString()
//            it.dateMinorWorksCompleted = binding.editTextDateMinorWorks.text.toString()
//            it.installationAddress = binding.editTextInstallationAddress.text.toString()
//            it.descriptionMinorWorks = binding.editTextDescriptionMinorWorks.text.toString()
//            it.detailsOfDepartures = binding.editTextDetailsDepartures.text.toString()
//            it.commentsExistingInstallation = binding.editTextCommentsExistingInstallation.text.toString()
//            it.riskAssessmentAttached = binding.checkBoxRiskAssessmentAttached.isChecked
//        }
    }

//    private fun clearAll() {
//        binding.editTextDetailsClient.setText("")
//        binding.editTextDateMinorWorks.setText("")
//        binding.editTextInstallationAddress.setText("")
//        binding.editTextDescriptionMinorWorks.setText("")
//        binding.editTextDetailsDepartures.setText("")
//        binding.editTextCommentsExistingInstallation.setText("")
//    }
}