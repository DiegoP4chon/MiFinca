package com.ganawin.mifinca.ui.cortejos

import android.app.Activity
import android.app.DatePickerDialog
import android.content.ActivityNotFoundException
import android.content.Intent
import android.net.Uri
import android.os.Bundle
import android.provider.MediaStore
import android.util.Log
import androidx.fragment.app.Fragment
import android.view.View
import android.widget.Toast
import androidx.fragment.app.viewModels
import com.bumptech.glide.Glide
import com.ganawin.mifinca.R
import com.ganawin.mifinca.core.GenerateId
import com.ganawin.mifinca.core.Resource
import com.ganawin.mifinca.data.model.Cortejo
import com.ganawin.mifinca.data.remote.cortejos.CortejosDataSource
import com.ganawin.mifinca.databinding.FragmentAddCortejoBinding
import com.ganawin.mifinca.domain.cortejos.CortejosRepoImpl
import com.ganawin.mifinca.presentation.cortejos.CortejosScreenViewModel
import com.ganawin.mifinca.presentation.cortejos.CortejosScreenViewModelFactory
import com.google.android.material.textfield.TextInputLayout
import com.google.firebase.storage.FirebaseStorage
import com.google.firebase.storage.StorageReference
import java.util.*
import kotlin.collections.HashMap

class AddCortejoFragment : Fragment(R.layout.fragment_add_cortejo) {

    private lateinit var binding: FragmentAddCortejoBinding
    private lateinit var mStorageReference: StorageReference
    private var UserUid: String = ""
    private var document: String = ""
    private var idPhotoMacho = ""
    private var urlPhotoMacho = ""
    private var idPhotoHembra = ""
    private var urlPhotoHembra = ""

    private var mapCortejo: HashMap<String, Any> = hashMapOf()

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
            document = bundle.getString("document", "")
        }
    }

    override fun onViewCreated(view: View, savedInstanceState: Bundle?) {
        super.onViewCreated(view, savedInstanceState)

        if(document != ""){
            cargarFormulario(document)
        }

        binding = FragmentAddCortejoBinding.bind(view)

        binding.btnFechaCortejo.setOnClickListener { showDatePicker() }
        binding.btnRegistrarCortejo.setOnClickListener { validaCampos(
            binding.tilMachoCortejo, binding.tilMachoRaza,
            binding.tilHembraCortejo, binding.tilHembraRaza
        ) }
        binding.btnAddPhotoMacho.setOnClickListener { openGallery(REQUEST_IMAGE_MACHO) }
        binding.btnAddPhotoHembra.setOnClickListener { openGallery(REQUEST_IMAGE_HEMBRA) }
    }

    private fun cargarFormulario(document: String) {
        viewModel.getOneCortejo(UserUid, document).observe(viewLifecycleOwner, { result->
            when(result){
                is Resource.Loading -> {
                }
                is Resource.Success -> {
                    llenarCampos(result.data)
                }
                is Resource.Failure -> {
                    Toast.makeText(requireContext(), "Error: ${result.exception}", Toast.LENGTH_SHORT)
                            .show()
                }
            }
        })
    }

    private fun llenarCampos(data: List<Cortejo>) {
        idPhotoMacho = data[0].idPhoto_macho
        idPhotoHembra = data[0].idPhoto_hembra
        binding.tvTitleAnadirCortejo.text = getString(R.string.modificar_cortejo)
        binding.btnFechaCortejo.text = data[0].fecha_cortejo
        binding.etMachoCortejo.setText(data[0].caract_macho)
        binding.etMachoRaza.setText(data[0].raza_macho)
        binding.btnAddPhotoMacho.text = getString(R.string.btn_change_photo)
        binding.imgUploadCortejoMacho.visibility = View.VISIBLE
        Glide.with(requireContext())
                .load(data[0].urlPhoto_macho)
                .centerCrop()
                .into(binding.imgUploadCortejoMacho)
        binding.etHembraCortejo.setText(data[0].caract_hembra)
        binding.etHembraRaza.setText(data[0].raza_hembra)
        binding.btnAddPhotoHembra.text = getString(R.string.btn_change_photo)
        binding.imgUploadCortejoHembra.visibility = View.VISIBLE
        Glide.with(requireContext())
                .load(data[0].urlPhoto_hembra)
                .centerCrop()
                .into(binding.imgUploadCortejoHembra)
        binding.btnRegistrarCortejo.text = getString(R.string.update)
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
            if(document != ""){
                modificarRegistro()
            } else {
                insertarRegistro()
            }
        }
    }

    private fun modificarRegistro() {
        mapCortejo["id"] = GenerateId().generateID(binding.btnFechaCortejo.text.toString())
        mapCortejo["fecha_cortejo"] = binding.btnFechaCortejo.text.toString()
        mapCortejo["caract_macho"] = binding.etMachoCortejo.text.toString().trim()
        mapCortejo["raza_macho"] = binding.etMachoRaza.text.toString().trim()
        mapCortejo["caract_hembra"] = binding.etHembraCortejo.text.toString().trim()
        mapCortejo["raza_hembra"] = binding.etHembraRaza.text.toString().trim()

        viewModel.updateCortejo(UserUid, document, mapCortejo).observe(viewLifecycleOwner, { result->
            when(result){
                is Resource.Loading -> {
                }
                is Resource.Success -> {
                    Toast.makeText(requireContext(), "$result.data", Toast.LENGTH_LONG).show()
                    activity?.onBackPressed()
                }
                is Resource.Failure -> {
                    Toast.makeText(requireContext(), "Error: ${result.exception}", Toast.LENGTH_SHORT)
                            .show()
                }
            }
        })
    }

    private fun insertarRegistro() {
        val fecha = binding.btnFechaCortejo.text.toString()
        val caractMacho = binding.etMachoCortejo.text.toString().trim()
        val machoRaza = binding.etMachoRaza.text.toString().trim()
        val caractHembra = binding.etHembraCortejo.text.toString().trim()
        val hembraRaza = binding.etHembraRaza.text.toString().trim()

        val listaCortejo: MutableList<String> = mutableListOf(fecha, caractMacho, machoRaza, idPhotoMacho,
            urlPhotoMacho, caractHembra, hembraRaza, idPhotoHembra, urlPhotoHembra)

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
            uploadPhoto(imageSelectedUri, -1)
        }
        if (requestCode == REQUEST_IMAGE_HEMBRA && resultCode == Activity.RESULT_OK) {
            val imageSelectedUri: Uri = data?.data!!
            binding.imgUploadCortejoHembra.visibility = View.VISIBLE
            binding.imgUploadCortejoHembra.setImageURI(imageSelectedUri)
            uploadPhoto(imageSelectedUri, 1)
        }
    }

    private fun uploadPhoto(imageSelectedUri: Uri, identificador: Int) {
        mStorageReference = FirebaseStorage.getInstance().reference

        val storageReference: StorageReference = if(document != ""){
            var idPhotoUpload = ""
            idPhotoUpload = if(identificador < 0){
                if(idPhotoMacho == ""){
                    "${UUID.randomUUID()}"
                } else {
                    idPhotoMacho
                }
            } else {
                if(idPhotoHembra == ""){
                    "${UUID.randomUUID()}"
                } else {
                    idPhotoHembra
                }
            }
            mStorageReference.child("FotosCortejos")
                    .child(UserUid).child(idPhotoUpload)

        }else{
            mStorageReference.child("FotosCortejos")
                    .child(UserUid).child("${UUID.randomUUID()}")
        }
        imageSelectedUri.let {
            storageReference.putFile(imageSelectedUri)
                    .addOnProgressListener {
                        binding.btnRegistrarCortejo.visibility = View.GONE
                        binding.pbCortejo.visibility = View.VISIBLE
                    }
                    .addOnCompleteListener{
                        binding.pbCortejo.visibility = View.GONE
                        binding.btnRegistrarCortejo.visibility = View.VISIBLE
                    }
                    .addOnSuccessListener {
                        Toast.makeText(requireContext(), "Foto sudida", Toast.LENGTH_SHORT).show()
                        it.storage.downloadUrl.addOnSuccessListener {
                            if(identificador < 0){
                                urlPhotoMacho = it.toString()
                                mapCortejo["urlPhoto_macho"] = it.toString()
                            } else {
                                urlPhotoHembra = it.toString()
                                mapCortejo["urlPhoto_hembra"] = it.toString()
                            }
                        }
                        if(identificador < 0){
                            idPhotoMacho = it.storage.name
                            mapCortejo["idPhoto_macho"] = it.storage.name
                        } else {
                            idPhotoHembra = it.storage.name
                            mapCortejo["idPhoto_hembra"] = it.storage.name
                        }
                    }
                    .addOnFailureListener{
                        Toast.makeText(requireContext(), "Error al subir la foto", Toast.LENGTH_SHORT)
                                .show()
                    }
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