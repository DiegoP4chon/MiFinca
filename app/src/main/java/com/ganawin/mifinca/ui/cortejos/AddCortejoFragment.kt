package com.ganawin.mifinca.ui.cortejos

import android.app.Activity
import android.app.DatePickerDialog
import android.content.ActivityNotFoundException
import android.content.Intent
import android.net.Uri
import android.os.Bundle
import android.provider.MediaStore
import androidx.fragment.app.Fragment
import android.view.View
import android.widget.Toast
import androidx.fragment.app.viewModels
import com.ganawin.mifinca.R
import com.ganawin.mifinca.core.Resource
import com.ganawin.mifinca.data.remote.cortejos.CortejosDataSource
import com.ganawin.mifinca.databinding.FragmentAddCortejoBinding
import com.ganawin.mifinca.domain.cortejos.CortejosRepoImpl
import com.ganawin.mifinca.presentation.cortejos.CortejosScreenViewModel
import com.ganawin.mifinca.presentation.cortejos.CortejosScreenViewModelFactory
import com.google.android.material.textfield.TextInputLayout
import com.google.firebase.storage.FirebaseStorage
import com.google.firebase.storage.StorageReference
import java.util.*

class AddCortejoFragment : Fragment(R.layout.fragment_add_cortejo) {

    private lateinit var binding: FragmentAddCortejoBinding
    private lateinit var mStorageReference: StorageReference
    private var UserUid: String = ""
    private var idPhotoMacho = ""
    private var urlPhtoMacho = ""
    private var idPhotoHembra = ""
    private var urlPhotoHembra = ""

    private lateinit var calendar: Calendar
    private var year = 0
    private var month = 0
    private var day = 0

    private val REQUEST_IMAGE_MACHO = 4
    private val REQUEST_IMAGE_HEMBRA = 5

    private val viewModel by viewModels<CortejosScreenViewModel> { CortejosScreenViewModelFactory(
        CortejosRepoImpl(CortejosDataSource())
    ) }

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        arguments?.let { bundle ->
           UserUid = bundle.getString("UID", "")
        }
    }

    override fun onViewCreated(view: View, savedInstanceState: Bundle?) {
        super.onViewCreated(view, savedInstanceState)

        binding = FragmentAddCortejoBinding.bind(view)
        mStorageReference = FirebaseStorage.getInstance().reference

        binding.btnFechaCortejo.setOnClickListener { showDatePicker() }
        binding.btnRegistrarCortejo.setOnClickListener { validaCampos(
            binding.tilMachoCortejo, binding.tilMachoRaza,
            binding.tilHembraCortejo, binding.tilHembraRaza
        ) }
        binding.btnAddPhotoMacho.setOnClickListener { openGallery(REQUEST_IMAGE_MACHO) }
        binding.btnAddPhotoHembra.setOnClickListener { openGallery(REQUEST_IMAGE_HEMBRA) }
    }

    private fun validaCampos(vararg textFields: TextInputLayout) {
        for(textField in textFields){
            if(textField.editText?.text.toString().trim().isEmpty()){
                textField.error = getString(R.string.require)
            }else {
                textField.error = null
            }
        }
        validateFecha()
    }

    private fun validateFecha() {
        if(binding.btnFechaCortejo.text.toString() == "Seleccionar"){
            Toast.makeText(requireContext(), "Selecciona la fecha", Toast.LENGTH_SHORT).show()
        } else {
            insertarRegistro()
        }
    }

    private fun insertarRegistro() {
        val fecha = binding.btnFechaCortejo.text.toString()
        val caractMacho = binding.etMachoCortejo.text.toString().trim()
        val machoRaza = binding.etMachoRaza.text.toString().trim()
        val caractHembra = binding.etHembraCortejo.text.toString().trim()
        val hembraRaza = binding.etHembraRaza.text.toString().trim()

        val listaCortejo: MutableList<String> = mutableListOf(fecha, caractMacho, machoRaza, idPhotoMacho,
            urlPhtoMacho, caractHembra, hembraRaza, idPhotoHembra, urlPhotoHembra)

        viewModel.setNewCortejo(listaCortejo, UserUid).observe(viewLifecycleOwner, { result->
            when(result){
                is Resource.Loading -> {
                }
                is Resource.Success -> {
                    Toast.makeText(requireContext(), result.data, Toast.LENGTH_LONG).show()
                    activity?.onBackPressed()
                }
                is Resource.Failure -> {
                    Toast.makeText(requireContext(), "Error: ${result.exception}", Toast.LENGTH_SHORT)
                        .show()
                }
            }
        })
    }

    private fun openGallery(requestImage: Int) {
        val openPictureGalley = Intent(Intent.ACTION_PICK, MediaStore.Images.Media.EXTERNAL_CONTENT_URI)
        try {
            startActivityForResult(openPictureGalley, requestImage)
        } catch (e: ActivityNotFoundException){
            Toast.makeText(requireContext(), "No es posible abrir la galeria", Toast.LENGTH_SHORT)
                .show()
        }
    }

    override fun onActivityResult(requestCode: Int, resultCode: Int, data: Intent?) {
        super.onActivityResult(requestCode, resultCode, data)
        if (requestCode == REQUEST_IMAGE_MACHO && resultCode == Activity.RESULT_OK) {
            val imageSelectedUri: Uri = data?.data!!
            binding.imgUploadCortejoMacho.visibility = View.VISIBLE
            binding.imgUploadCortejoMacho.setImageURI(imageSelectedUri)
            //uploadPhoto(imageSelectedUri)
        }
        if (requestCode == REQUEST_IMAGE_HEMBRA && resultCode == Activity.RESULT_OK) {
            val imageSelectedUri: Uri = data?.data!!
            binding.imgUploadCortejoHembra.visibility = View.VISIBLE
            binding.imgUploadCortejoHembra.setImageURI(imageSelectedUri)
            //uploadPhoto(imageSelectedUri)
        }
    }


    private fun showDatePicker() {
        calendar = Calendar.getInstance()
        year = calendar.get(Calendar.YEAR)
        month = calendar.get(Calendar.MONTH)
        day = calendar.get(Calendar.DAY_OF_MONTH)

        val datePickerDialog = DatePickerDialog(requireContext(), {
                view, year, month, dayOfMonth ->
            val fechaCortejo = "$dayOfMonth/${month+1}/$year"
            binding.btnFechaCortejo.text = fechaCortejo
        }, year, month, day)

        datePickerDialog.show()
    }
}