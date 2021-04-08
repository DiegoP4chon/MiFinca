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
import com.bumptech.glide.Glide
import com.ganawin.mifinca.R
import com.ganawin.mifinca.core.GenerateId
import com.ganawin.mifinca.core.Resource
import com.ganawin.mifinca.data.model.Venta
import com.ganawin.mifinca.data.remote.ventas.VentasDataSource
import com.ganawin.mifinca.databinding.FragmentAddVentaBinding
import com.ganawin.mifinca.domain.ventas.VentasRepoImpl
import com.ganawin.mifinca.presentation.ventas.VentasScreenViewModel
import com.ganawin.mifinca.presentation.ventas.VentasScreenViewModelFactory
import com.google.android.material.textfield.TextInputLayout
import com.google.firebase.storage.FirebaseStorage
import com.google.firebase.storage.StorageReference
import java.util.*
import kotlin.collections.HashMap

@Suppress("UNUSED_ANONYMOUS_PARAMETER")
class AddVentaFragment : Fragment(R.layout.fragment_add_venta) {

    private lateinit var binding: FragmentAddVentaBinding
    private lateinit var calendar: Calendar
    private lateinit var mStorageReference: StorageReference
    private var userUid: String = ""
    private var document = ""
    private var urlPhoto = ""
    private var idPhoto = ""

    private var mapVenta: HashMap<String, Any> = hashMapOf()

    private var year = 0
    private var month = 0
    private var day = 0

    private val requestImageGallery = 1

    private val viewModel by viewModels<VentasScreenViewModel> { VentasScreenViewModelFactory(VentasRepoImpl(
        VentasDataSource()
    )) }

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        arguments?.let { bundle ->
            userUid = bundle.getString("UID", "")
            document = bundle.getString("document", "")
        }
    }

    override fun onViewCreated(view: View, savedInstanceState: Bundle?) {
        super.onViewCreated(view, savedInstanceState)

        if(document != ""){
            cargarFormulario(document)
        }

        binding = FragmentAddVentaBinding.bind(view)
        binding.btnFechaVenta.setOnClickListener { showDatePicker() }
        binding.btnRegistrarVenta.setOnClickListener { validarCampos(binding.tilVenta,
            binding.tilComprador, binding.tilValorVenta) }
        binding.tvSubirFoto.setOnClickListener { openGallery() }
    }

    private fun cargarFormulario(document: String) {
        viewModel.getOneVenta(userUid, document).observe(viewLifecycleOwner, { result->
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

    private fun llenarCampos(data: List<Venta>) {
        idPhoto = data[0].idPhoto
        binding.tvTitleAnadirVenta.text = getString(R.string.modificar_venta)
        binding.btnFechaVenta.text = data[0].fecha_venta
        binding.etVenta.setText(data[0].descripVenta)
        binding.etComprador.setText(data[0].comprador)
        binding.etValorVenta.setText(data[0].valorVenta.toString())
        binding.tvSubirFoto.text = getString(R.string.text_cambiar_foto)
        binding.imgUploadVenta.visibility = View.VISIBLE
        binding.btnRegistrarVenta.text = getString(R.string.btn_update)

        Glide.with(requireContext())
                .load(data[0].url_Photo)
                .centerCrop()
                .into(binding.imgUploadVenta)
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
        if(binding.btnFechaVenta.text.toString() == getString(R.string.seleccione_fecha)){
            Toast.makeText(requireContext(), getString(R.string.petition_select_date), Toast.LENGTH_SHORT).show()
        } else {
            if(document != "") {
                modificarVenta()
            } else {
                insertarVenta()
            }
        }
    }

    private fun modificarVenta() {
        mapVenta["id"] = GenerateId().generateID(binding.btnFechaVenta.text.toString())
        mapVenta["descripVenta"] = binding.etVenta.text.toString().trim()
        mapVenta["fecha_venta"] = binding.btnFechaVenta.text.toString()
        mapVenta["comprador"] = binding.etComprador.text.toString().trim()
        mapVenta["valorVenta"] = binding.etValorVenta.text.toString().trim().toLong()

        viewModel.updateVenta(userUid, document, mapVenta).observe(viewLifecycleOwner, { result->
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

    private fun insertarVenta() {

        val fecha = binding.btnFechaVenta.text.toString()
        val descripVenta = binding.etVenta.text.toString().trim()
        val comprador = binding.etComprador.text.toString().trim()
        val valorVenta = binding.etValorVenta.text.toString().trim()

        val listVenta: MutableList<String> = mutableListOf(fecha, descripVenta, comprador, valorVenta,
            idPhoto, urlPhoto)

        viewModel.setNewVenta(listVenta, userUid).observe(viewLifecycleOwner, { result->
            when(result){
                is Resource.Loading -> {
                }
                is Resource.Success -> {
                    Toast.makeText(requireContext(), getString(R.string.confirm_add_register), Toast.LENGTH_LONG).show()
                    activity?.onBackPressed()
                }
                is Resource.Failure -> {
                    Toast.makeText(requireContext(), getString(R.string.error_add_register), Toast.LENGTH_SHORT)
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
            startActivityForResult(openPictureGalley, requestImageGallery)
        } catch (e: ActivityNotFoundException){
            Toast.makeText(requireContext(), getString(R.string.error_open_gallery), Toast.LENGTH_SHORT)
                .show()
        }
    }

    override fun onActivityResult(requestCode: Int, resultCode: Int, data: Intent?) {
        super.onActivityResult(requestCode, resultCode, data)
        if (requestCode == requestImageGallery && resultCode == Activity.RESULT_OK) {
            val imageSelectedUri: Uri = data?.data!!
            binding.imgUploadVenta.visibility = View.VISIBLE
            binding.imgUploadVenta.setImageURI(imageSelectedUri)
            uploadPhoto(imageSelectedUri)
        }
    }

    private fun uploadPhoto(imageSelectedUri: Uri) {
        mStorageReference = FirebaseStorage.getInstance().reference

        val storageReference: StorageReference = if(document != ""){
            if(idPhoto == ""){
                idPhoto = "${UUID.randomUUID()}"
            }
            mStorageReference.child("FotosVentas")
                    .child(userUid).child(idPhoto)
        }else{
            mStorageReference.child("FotosVentas")
                    .child(userUid).child("${UUID.randomUUID()}")
        }
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
                .addOnSuccessListener { resSuccess ->
                    Toast.makeText(requireContext(), getString(R.string.confirm_upload_photo), Toast.LENGTH_SHORT).show()
                    resSuccess.storage.downloadUrl.addOnSuccessListener {
                        urlPhoto = it.toString()
                        mapVenta["url_Photo"] = it.toString()
                    }
                    idPhoto = resSuccess.storage.name
                    mapVenta["idPhoto"] = resSuccess.storage.name
                }
                .addOnFailureListener{
                    Toast.makeText(requireContext(), getString(R.string.failure_upload_photo), Toast.LENGTH_SHORT)
                        .show()
                }
        }
    }
}