package com.ganawin.mifinca.ui.terneros

import android.app.Activity
import android.content.ActivityNotFoundException
import android.content.Intent
import android.graphics.Bitmap
import android.net.Uri
import android.os.Bundle
import android.provider.MediaStore
import androidx.fragment.app.Fragment
import android.view.View
import android.widget.Toast
import androidx.fragment.app.viewModels
import com.bumptech.glide.Glide
import com.ganawin.mifinca.R
import com.ganawin.mifinca.core.GenerateId
import com.ganawin.mifinca.core.Resource
import com.ganawin.mifinca.data.model.Ternero
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
import kotlin.collections.HashMap


@Suppress("UNUSED_ANONYMOUS_PARAMETER")
class AddTerneroFragment : Fragment(R.layout.fragment_add_ternero) {

    private val requestImageCapture = 1
    private val requestImageGalley = 2

    private var fecha: String =""
    private var sexo: String =""
    private var userUid: String = ""
    private var url: String = ""
    private var idPhoto: String = ""
    private var document: String = ""
    private var mapTernero: HashMap<String, Any> = hashMapOf()

    private lateinit var mStorageReference: StorageReference

    private lateinit var binding: FragmentAddTerneroBinding
    private val viewModel by viewModels<TerneroScreenViewModel> { TerneroScreenViewModelFactory(
        TerneroRepoImpl(TernerosDataSource())) }

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        arguments?.let { bundle ->
            userUid = bundle.getString("UID", "")
            document = bundle.getString("document", "")
        }
    }

    override fun onViewCreated(view: View, savedInstanceState: Bundle?) {
        super.onViewCreated(view, savedInstanceState)

        binding = FragmentAddTerneroBinding.bind(view)

        if(document != ""){
            cargarFormulario(document)
        }

        binding.btnNewTernero.setOnClickListener { addNewTernero(binding.tilMadre,
            binding.tilPadre, binding.tilRaza) }

        binding.btntakePhoto.setOnClickListener { dispacthTakePictureIntent() }
        binding.btnUpload.setOnClickListener { openPictureGallery() }
        binding.tvNoPhoto.setOnClickListener { activeButtonNewTernero() }


        val calendar = binding.calendarNacimiento
        calendar.setOnDateChangeListener { viewCalendar, year, month, dayOfMonth ->
            fecha = "$dayOfMonth/${month + 1}/$year"
            Toast.makeText(requireContext(), fecha, Toast.LENGTH_LONG).show()
            binding.tvSelectDate.text = fecha

        }
    }

    private fun cargarFormulario(document: String) {
        viewModel.fetchOneTernero(userUid, document).observe(viewLifecycleOwner, { result->
            when(result){
                is Resource.Loading -> {
                }
                is Resource.Success -> {
                    llenarCampos(result.data)
                }
                is Resource.Failure -> {
                    Toast.makeText(requireContext(), getString(R.string.error_consulta), Toast.LENGTH_SHORT)
                            .show()
                }
            }
        })
    }

    private fun llenarCampos(data: List<Ternero>) {
        idPhoto = data[0].idPhoto
        fecha = data[0].date_nacimiento
        binding.tvSelectDate.text = fecha
        sexo = data[0].sexo
        if(sexo == getString(R.string.animal_macho)){
            binding.cbMasc.isChecked = true
        } else {binding.cbHembra.isChecked = true}
        binding.etMadre.setText(data[0].madre)
        binding.etPadre.setText(data[0].padre)
        binding.etRaza.setText(data[0].raza)
        binding.btnUpload.text = getString(R.string.btn_change_photo)
        binding.tvNoPhoto.visibility = View.GONE
        binding.imgUpload.visibility = View.VISIBLE
        Glide.with(requireContext())
                .load(data[0].url_photo)
                .centerCrop()
                .into(binding.imgUpload)
        binding.btnNewTernero.visibility = View.VISIBLE
        binding.btnNewTernero.text = getString(R.string.modificar_ternero)
    }

    private fun activeButtonNewTernero() {
        binding.tvNoPhoto.visibility = View.GONE
        binding.btnNewTernero.visibility = View.VISIBLE
    }

    private fun openPictureGallery() {
        val openPictureGalley = Intent(Intent.ACTION_PICK, MediaStore.Images.Media.EXTERNAL_CONTENT_URI)
        try {
            startActivityForResult(openPictureGalley, requestImageGalley)
        } catch (e: ActivityNotFoundException){
            Toast.makeText(requireContext(), getString(R.string.error_open_gallery), Toast.LENGTH_SHORT)
                .show()
        }
    }

    private fun dispacthTakePictureIntent() {
        val takepictureIntent = Intent(MediaStore.ACTION_IMAGE_CAPTURE)
        try{
            startActivityForResult(takepictureIntent, requestImageCapture)
        } catch (e: ActivityNotFoundException){
            Toast.makeText(requireContext(), getString(R.string.error_take_picture), Toast.LENGTH_SHORT)
                .show()
        }
    }

    @Suppress("DEPRECATION")
    override fun onActivityResult(requestCode: Int, resultCode: Int, data: Intent?) {
        super.onActivityResult(requestCode, resultCode, data)
        if (requestCode == requestImageCapture && resultCode == Activity.RESULT_OK) {
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
                Snackbar.make(binding.root, getString(R.string.error_permission_storage),
                    Snackbar.LENGTH_SHORT).show()
            }

        }
        if (requestCode == requestImageGalley && resultCode == Activity.RESULT_OK) {
            val imageSelectedUri: Uri = data?.data!!
            binding.imgUpload.visibility = View.VISIBLE
            binding.imgUpload.setImageURI(imageSelectedUri)
            uploadPhoto(imageSelectedUri)
        }
    }

    private fun uploadPhoto(imageUpload: Uri) {

        mStorageReference = FirebaseStorage.getInstance().reference

        val storageReference: StorageReference = if(document != ""){
            if(idPhoto == ""){
                idPhoto = "${UUID.randomUUID()}"
            }
            mStorageReference.child("FotosTerneros")
                    .child(userUid).child(idPhoto)
        }else{
            mStorageReference.child("FotosTerneros")
                    .child(userUid).child("${UUID.randomUUID()}")
        }
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
                    .addOnSuccessListener { resSuccess ->
                        Toast.makeText(requireContext(), getString(R.string.confirm_upload_photo), Toast.LENGTH_SHORT).show()
                        resSuccess.storage.downloadUrl.addOnSuccessListener {
                            url = it.toString()
                            mapTernero["url_photo"] = it.toString()
                        }
                        idPhoto = resSuccess.storage.name
                        mapTernero["idPhoto"] = resSuccess.storage.name
                    }
                    .addOnFailureListener{
                        Toast.makeText(requireContext(), getString(R.string.failure_upload_photo), Toast.LENGTH_SHORT)
                                .show()
                    }
        }
    }

    private fun obtenerSexo(): Boolean {
        return if(binding.cbMasc.isChecked && binding.cbHembra.isChecked){
            binding.cbMasc.isChecked = false
            binding.cbHembra.isChecked = false
            false
        }else if (binding.cbHembra.isChecked) {
            sexo = getString(R.string.animal_hembra)
            true
        }else if (binding.cbMasc.isChecked){
            sexo = getString(R.string.animal_macho)
            true
        }else{
            false
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
            if(document != ""){
                modificarRegistro()
            } else {
                insertRegistro()
            }
        } else Toast.makeText(requireContext(), getString(R.string.alert_sexo_date),
            Toast.LENGTH_LONG).show()
    }

    private fun modificarRegistro() {
        mapTernero["id"] = GenerateId().generateID(fecha)
        mapTernero["date_nacimiento"] = fecha
        mapTernero["sexo"] = sexo
        mapTernero["madre"] = binding.etMadre.text.toString().trim()
        mapTernero["padre"] = binding.etPadre.text.toString().trim()
        mapTernero["raza"] = binding.etRaza.text.toString().trim()

        viewModel.updateItemTernero(userUid, document, mapTernero).observe(viewLifecycleOwner, { result->
            when(result){
                is Resource.Loading -> {
                }
                is Resource.Success -> {
                    Toast.makeText(requireContext(), getString(R.string.confirm_update_register), Toast.LENGTH_LONG).show()
                    activity?.onBackPressed()
                }
                is Resource.Failure -> {
                    Toast.makeText(requireContext(), getString(R.string.error_update_register), Toast.LENGTH_SHORT)
                            .show()
                }
            }
        })

    }

    private fun insertRegistro() {
        val madre = binding.etMadre.text.toString().trim()
        val padre = binding.etPadre.text.toString().trim()
        val raza = binding.etRaza.text.toString().trim()

        val ternero: MutableList<String> = mutableListOf(fecha, sexo, madre, padre, raza,
                     url, idPhoto)

        viewModel.setNewTernero(ternero, userUid).observe(viewLifecycleOwner, { result->
            when(result){
                is Resource.Loading -> {
                }
                is Resource.Success -> {
                    activity?.onBackPressed()
                }
                is Resource.Failure -> {
                    Toast.makeText(requireContext(), getString(R.string.error_add_register), Toast.LENGTH_SHORT)
                        .show()
                }
            }
        })

    }
}