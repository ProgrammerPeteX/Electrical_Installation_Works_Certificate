package com.pdstudios.electricalinstallationworkscertificate.screens.minorEiwsForm

import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import androidx.lifecycle.LifecycleOwner
import androidx.lifecycle.MutableLiveData
import androidx.recyclerview.widget.RecyclerView
import com.pdstudios.electricalinstallationworkscertificate.databinding.FormCardLayoutBinding

class PdfViewRecyclerAdapter(
    private val fieldList: MutableLiveData<MutableList<String>>,
    private val lifecycleOwner: LifecycleOwner
)
    : RecyclerView.Adapter<PdfViewRecyclerAdapter.ViewHolder>() {

    private lateinit var binding: FormCardLayoutBinding
    private val list: List<String>

    init {
        list = listOf("a", "b", "c", "c", "c", "c", "c", "c", "c", "c", "c", "c", "c", "c", "c")
    }

    override fun onCreateViewHolder(
        parent: ViewGroup,
        viewType: Int
    ): ViewHolder {
        val inflater = LayoutInflater.from(parent.context)
        binding = FormCardLayoutBinding.inflate(inflater,parent,false)
        return ViewHolder(binding.root)
    }

    override fun onBindViewHolder(holder: ViewHolder, position: Int) {
        holder.info.text = fieldList.value!![position]
    }

    override fun getItemCount(): Int {
        return fieldList.value!!.size
    }

    inner class ViewHolder(itemView: View): RecyclerView.ViewHolder(itemView) {
        var info = binding.textViewInfo
        var input = binding.editTextInput
        var checkbox= binding.checkBox
    }
}