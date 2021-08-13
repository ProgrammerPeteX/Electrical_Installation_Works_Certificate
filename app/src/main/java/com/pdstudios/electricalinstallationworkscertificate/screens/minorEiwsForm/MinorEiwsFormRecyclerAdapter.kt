package com.pdstudios.electricalinstallationworkscertificate.screens.minorEiwsForm

import android.text.Editable
import android.text.TextWatcher
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import androidx.lifecycle.MutableLiveData
import androidx.recyclerview.widget.RecyclerView
import com.pdstudios.electricalinstallationworkscertificate.FormField
import com.pdstudios.electricalinstallationworkscertificate.databinding.FormCardLayoutBinding

class MinorEiwsFormRecyclerAdapter(var formFieldList: MutableLiveData<MutableList<FormField>>)
    : RecyclerView.Adapter<MinorEiwsFormRecyclerAdapter.ViewHolder>(){

    private lateinit var binding: FormCardLayoutBinding


    override fun onCreateViewHolder(
        parent: ViewGroup,
        viewType: Int
    ): ViewHolder {
        val inflater = LayoutInflater.from(parent.context)
        binding = FormCardLayoutBinding.inflate(inflater,parent,false)
        return ViewHolder(binding.root)
    }

    override fun onBindViewHolder(holder: ViewHolder, position: Int) {
        val formField = formFieldList.value?.get(position)!!
        holder.info.text = formField.name

        val isEditText = formField.type == "EditText"
        val isCheckBox = formField.type == "CheckBox"

        when {
            isEditText -> {
                holder.checkbox.visibility = View.GONE
                holder.input.addTextChangedListener(object : TextWatcher {
                    override fun beforeTextChanged(p0: CharSequence?, p1: Int, p2: Int, p3: Int) {}

                    override fun onTextChanged(p0: CharSequence?, p1: Int, p2: Int, p3: Int) {}

                    override fun afterTextChanged(p0: Editable?) {
                        formFieldList.value?.get(position)!!.value = p0.toString()
                    }
                })
            }
            isCheckBox -> {
                holder.input.visibility = View.GONE
                holder.checkbox.setOnCheckedChangeListener { button, isChecked ->
                    if (isChecked) {
                        formFieldList.value?.get(position)!!.value = "true"
                    }
                }
            }
            else -> throw IllegalStateException("State is neither EditText nor CheckBox")
        }
    }

    override fun getItemCount(): Int {
        return formFieldList.value!!.size
    }

    inner class ViewHolder(itemView: View): RecyclerView.ViewHolder(itemView) {
        var info = binding.textViewInfo
        var input = binding.editTextInput
        var checkbox= binding.checkBox
    }


}
