package com.ganawin.mifinca.ui.ventas

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
import com.ganawin.mifinca.data.remote.ventas.VentasDataSource
import com.ganawin.mifinca.databinding.FragmentAddVentaBinding
import com.ganawin.mifinca.domain.ventas.VentasRepoImpl
import com.ganawin.mifinca.presentation.ventas.VentasScreenViewModel
import com.ganawin.mifinca.presentation.ventas.VentasScreenViewModelFactory
import com.google.android.material.textfield.TextInputLayout
import com.google.firebase.storage.FirebaseStorage
import com.google.firebase.storage.StorageReference
import java.util.*

class AddVentaFragment : Fragment(R.layout.fragment_add_venta,) {

    private lateinit var binding: FragmentAddVentaBinding
    private lateinit var calendar: Calendar
    private lateinit var mStorageReference: StorageReference
    private var UserUid: String = ""
    private var urlPhoto = ""
    private var idPhoto = ""

    private var year = 0
    private var month = 0
    private var day = 0

    private val REQUEST_IMAGE_GALLERY = 1

    private val viewModel by viewModels<VentasScreenViewModel> { VentasScreenViewModelFactory(VentasRepoImpl(
        VentasDataSource()
    )) }

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        arguments?.let { bundle ->
            UserUid = bundle.getString("UID", "")

        }
    }

    override fun onViewCreated(view: View, savedInstanceState: Bundle?) {
        super.onViewCreated(view, savedInstanceState)

        binding = FragmentAddVentaBinding.bind(view)
        mStorageReference = FirebaseStorage.getInstance().reference

        binding.btnFechaVenta.setOnClickListener { showDatePicker() }
        binding.btnRegistrarVenta.setOnClickListener { validarCampos(binding.tilVenta,
            binding.tilComprador, binding.tilValorVenta) }
        binding.tvSubirFoto.setOnClickListener { openGallery() }
    }

    private fun validarCampos(vararg textFields: TextInputLayout) {
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
        if(binding.btnFechaVenta.text.toString() == "Seleccionar"){
            Toast.makeText(requireContext(), "Selecciona la fecha", Toast.LENGTH_SHORT).show()
        } else {
            insertarVenta()
        }
    }

    private fun insertarVenta() {

        val fecha = binding.btnFechaVenta.text.toString()
        val descripVenta = binding.etVenta.text.toString().trim()
        val comprador = binding.etComprador.text.toString().trim()
        val valorVenta = binding.etValorVenta.text.toString().trim()

        val listVenta: MutableList<String> = mutableListOf(fecha, descripVenta, comprador, valorVenta,
            idPhoto, urlPhoto)

        viewModel.setNewVenta(listVenta, UserUid).observe(viewLifecycleOwner, { result->
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

    private fun showDatePicker(){
        calendar = Calendar.getInstance()
        year = calendar.get(Calendar.YEAR)
        month = calendar.get(Calendar.MONTH)
        day = calendar.get(Calendar.DAY_OF_MONTH)

        val datePickerDialog = DatePickerDialog(requireContext(), {
                view, year, month, dayOfMonth ->
            val fechaVenta = "$dayOfMonth/${month+1}/$year"
            binding.btnFechaVenta.text = fechaVenta
        }, year, month, day)

        datePickerDialog.show()
    }

    private fun openGallery(){
        val openPictureGalley = Intent(Intent.ACTION_PICK, MediaStore.Images.Media.EXTERNAL_CONTENT_URI)
        try {
            startActivityForResult(openPictureGalley, REQUEST_IMAGE_GALLERY)
        } catch (e: ActivityNotFoundException){
            Toast.makeText(requireContext(), "No es posible abrir la galeria", Toast.LENGTH_SHORT)
                .show()
        }
    }

    override fun onActivityResult(requestCode: Int, resultCode: Int, data: Intent?) {
        super.onActivityResult(requestCode, resultCode, data)
        if (requestCode == REQUEST_IMAGE_GALLERY && resultCode == Activity.RESULT_OK) {
            val imageSelectedUri: Uri = data?.data!!
            binding.imgUploadVenta.visibility = View.VISIBLE
            binding.imgUploadVenta.setImageURI(imageSelectedUri)
            uploadPhoto(imageSelectedUri)
        }
    }

    private fun uploadPhoto(imageSelectedUri: Uri) {
        mStorageReference = FirebaseStorage.getInstance().reference

        val storageReference = mStorageReference.child("FotosVentas")
            .child(UserUid).child("${UUID.randomUUID()}")
        imageSelectedUri.let {
            storageReference.putFile(imageSelectedUri)
                .addOnProgressListener {
                    binding.btnRegistrarVenta.visibility = View.GONE
                    binding.pbVenta.visibility = View.VISIBLE
                }
                .addOnCompleteListener{
                    binding.pbVenta.visibility = View.GONE
                    binding.btnRegistrarVenta.visibility = View.VISIBLE
                }
                .addOnSuccessListener {
                    Toast.makeText(requireContext(), "Foto sudida", Toast.LENGTH_SHORT).show()
                    it.storage.downloadUrl.addOnSuccessListener {
                        urlPhoto = it.toString()
                    }
                    idPhoto = it.storage.name
                }
                .addOnFailureListener{
                    Toast.makeText(requireContext(), "Error al subir la foto", Toast.LENGTH_SHORT)
                        .show()
                }
        }
    }
}