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
    private var adapter: RecyclerView.Adapter<MinorEiwsFormRecyclerAdapter.ViewHolder>? = null

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
        val factory = MinorEiwsFormViewModelFactory(dao, application)
        viewModel = ViewModelProvider(this, factory).get(MinorEiwsFormViewModel::class.java)
        binding.minorEiwsFormViewModel = viewModel
        binding.lifecycleOwner = this


        //get Pdf
        viewModel.getTempPdf()

        //Observers
            //RecyclerView
        viewModel.finishedLoadingPdf.observe(viewLifecycleOwner) {
            it?.let {
                layoutManager = LinearLayoutManager(this.context)
                binding.recyclerView.layoutManager = layoutManager
                adapter = MinorEiwsFormRecyclerAdapter(viewModel.formFieldList)
                binding.recyclerView.adapter = adapter
            }
        }

        viewModel.saveForm.observe(viewLifecycleOwner) {
            it?.let {
                navigate()
            }
        }
        return binding.root
    }

    private fun navigate() {
        this.findNavController().navigate(MinorEiwsFormDirections.actionMinorEiwsFormToPdfViewFragment())
    }

}