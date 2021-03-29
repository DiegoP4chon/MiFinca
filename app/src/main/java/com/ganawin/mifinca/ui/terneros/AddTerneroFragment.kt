package com.ganawin.mifinca.ui.terneros

import android.app.Activity
import android.content.ActivityNotFoundException
import android.content.Intent
import android.graphics.Bitmap
import android.net.Uri
import android.os.Bundle
import android.provider.MediaStore
import android.util.Log
import androidx.fragment.app.Fragment
import android.view.View
import android.widget.CalendarView
import android.widget.Toast
import androidx.fragment.app.FragmentManager
import androidx.fragment.app.viewModels
import com.ganawin.mifinca.R
import com.ganawin.mifinca.core.Resource
import com.ganawin.mifinca.data.remote.terneros.TernerosDataSource
import com.ganawin.mifinca.databinding.FragmentAddTerneroBinding
import com.ganawin.mifinca.domain.terneros.TerneroRepoImpl
import com.ganawin.mifinca.presentation.ternero.TerneroScreenViewModel
import com.ganawin.mifinca.presentation.ternero.TerneroScreenViewModelFactory
import com.google.android.material.snackbar.Snackbar
import com.google.android.material.textfield.TextInputLayout
import com.google.firebase.storage.FirebaseStorage
import com.google.firebase.storage.StorageReference
import java.io.ByteArrayOutputStream
import java.util.*


class AddTerneroFragment : Fragment(R.layout.fragment_add_ternero) {

    private val REQUEST_IMAGE_CAPTURE = 1
    private val REQUEST_IMAGE_GALLERY = 2

    private var fecha: String =""
    private var sexo: String =""
    private var UserUid: String = ""
    private var URL: String = ""
    private var idPhoto: String = ""

    private lateinit var mStorageReference: StorageReference

    private lateinit var binding: FragmentAddTerneroBinding
    private val viewModel by viewModels<TerneroScreenViewModel> { TerneroScreenViewModelFactory(
        TerneroRepoImpl(TernerosDataSource())) }

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        arguments?.let { bundle ->
            UserUid = bundle.getString("UID", "")

        }
    }

    override fun onViewCreated(view: View, savedInstanceState: Bundle?) {
        super.onViewCreated(view, savedInstanceState)

        binding = FragmentAddTerneroBinding.bind(view)
        mStorageReference = FirebaseStorage.getInstance().reference

        binding.btnNewTernero.setOnClickListener { addNewTernero(binding.tilMadre,
            binding.tilPadre, binding.tilRaza) }

        binding.btntakePhoto.setOnClickListener { dispacthTakePictureIntent() }
        binding.btnUpload.setOnClickListener { openPictureGallery() }
        binding.tvNoPhoto.setOnClickListener { activeButtonNewTernero() }


        val calendar = binding.calendarNacimiento
        calendar.setOnDateChangeListener(CalendarView.OnDateChangeListener { view, year, month, dayOfMonth ->
            fecha = "$dayOfMonth/${month+1}/$year"
            Toast.makeText(requireContext(), fecha, Toast.LENGTH_LONG).show()
            binding.tvSelectDate.text = "Fecha seleccionada: $fecha"

        })
    }

    private fun activeButtonNewTernero() {
        binding.tvNoPhoto.visibility = View.GONE
        binding.btnNewTernero.visibility = View.VISIBLE
    }

    private fun openPictureGallery() {
        val openPictureGalley = Intent(Intent.ACTION_PICK, MediaStore.Images.Media.EXTERNAL_CONTENT_URI)
        try {
            startActivityForResult(openPictureGalley, REQUEST_IMAGE_GALLERY)
        } catch (e: ActivityNotFoundException){
            Toast.makeText(requireContext(), "No es posible abrir la galeria", Toast.LENGTH_SHORT)
                .show()
        }
    }

    private fun dispacthTakePictureIntent() {
        val takepictureIntent = Intent(MediaStore.ACTION_IMAGE_CAPTURE)
        try{
            startActivityForResult(takepictureIntent, REQUEST_IMAGE_CAPTURE)
        } catch (e: ActivityNotFoundException){
            Toast.makeText(requireContext(), "No es posible toma la foto", Toast.LENGTH_SHORT)
                .show()
        }
    }

    override fun onActivityResult(requestCode: Int, resultCode: Int, data: Intent?) {
        super.onActivityResult(requestCode, resultCode, data)
        if (requestCode == REQUEST_IMAGE_CAPTURE && resultCode == Activity.RESULT_OK) {
            val imageCapture: Bitmap = data?.extras?.get("data") as Bitmap
            val baos = ByteArrayOutputStream()
            imageCapture.compress(Bitmap.CompressFormat.PNG, 100, baos)
            try {
                val path = MediaStore.Images.Media.insertImage(requireContext().contentResolver,
                        imageCapture, "val", null)
                val imageUri = Uri.parse(path)
                binding.imgUpload.visibility = View.VISIBLE
                binding.imgUpload.setImageURI(imageUri)
                uploadPhoto(imageUri)
            }catch (e: Exception){
                Snackbar.make(binding.root, "Debe habilitar permiso de almacenamiento",
                    Snackbar.LENGTH_SHORT).show()
            }

        }
        if (requestCode == REQUEST_IMAGE_GALLERY && resultCode == Activity.RESULT_OK) {
            val imageSelectedUri: Uri = data?.data!!
            binding.imgUpload.visibility = View.VISIBLE
            binding.imgUpload.setImageURI(imageSelectedUri)
            uploadPhoto(imageSelectedUri)
        }
    }

    fun uploadPhoto(imageUpload: Uri) {

        mStorageReference = FirebaseStorage.getInstance().reference

        val storageReference = mStorageReference.child("FotosTerneros")
                .child(UserUid).child("${UUID.randomUUID()}")
        imageUpload.let {
            storageReference.putFile(imageUpload)
                    .addOnProgressListener {
                        binding.btnNewTernero.visibility = View.GONE
                        binding.pbUploadImage.visibility = View.VISIBLE
                    }
                    .addOnCompleteListener{
                        binding.pbUploadImage.visibility = View.GONE
                        binding.btnNewTernero.visibility = View.VISIBLE
                    }
                    .addOnSuccessListener {
                        Toast.makeText(requireContext(), "Foto sudida", Toast.LENGTH_SHORT).show()
                        it.storage.downloadUrl.addOnSuccessListener {
                            URL = it.toString()
                        }
                        idPhoto = it.storage.name
                        /*
                        val namePhoto = it.storage.name
                        Log.d("namePhotoUri", namePhoto)
                         */
                    }
                    .addOnFailureListener{
                        Toast.makeText(requireContext(), "Error al subir la foto", Toast.LENGTH_SHORT)
                                .show()
                    }
        }
    }

    private fun obtenerSexo(): Boolean {
        if(binding.cbMasc.isChecked && binding.cbHembra.isChecked){
            binding.cbMasc.isChecked = false
            binding.cbHembra.isChecked = false
            return false
        }else if (binding.cbHembra.isChecked) {
            sexo = "Hembra"
            return true
        }else if (binding.cbMasc.isChecked){
            sexo = "Macho"
            return true
        }else{
            return false
        }
    }

    private fun obtenerFecha(): Boolean {
        return fecha != ""
    }

    private fun addNewTernero(vararg textFields: TextInputLayout) {
        for(textField in textFields){
            if(textField.editText?.text.toString().trim().isEmpty()){
                textField.error = getString(R.string.require)
            }else {
                textField.error = null
            }
        }
        validate()
    }

    private fun validate() {
        if(obtenerSexo() && obtenerFecha()){
            insertRegistro()
        } else Toast.makeText(requireContext(), "Verifique fecha y sexo",
            Toast.LENGTH_LONG).show()
    }

    private fun insertRegistro() {
        val fecha_nacimiento = fecha
        val madre = binding.etMadre.text.toString().trim()
        val padre = binding.etPadre.text.toString().trim()
        val raza = binding.etRaza.text.toString().trim()

        val ternero: MutableList<String> = mutableListOf(fecha_nacimiento, sexo, madre, padre, raza,
                     URL, idPhoto)

        viewModel.setNewTernero(ternero, UserUid).observe(viewLifecycleOwner, { result->
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
}