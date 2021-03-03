package com.ganawin.mifinca.ui.sreensapp

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
import androidx.core.os.bundleOf
import androidx.fragment.app.viewModels
import androidx.lifecycle.Observer
import androidx.navigation.fragment.findNavController
import com.ganawin.mifinca.R
import com.ganawin.mifinca.core.Resource
import com.ganawin.mifinca.data.remote.terneros.TernerosDataSource
import com.ganawin.mifinca.databinding.FragmentTernerosBinding
import com.ganawin.mifinca.domain.terneros.TerneroRepoImpl
import com.ganawin.mifinca.presentation.ternero.TerneroScreenViewModel
import com.ganawin.mifinca.presentation.ternero.TerneroScreenViewModelFactory
import com.ganawin.mifinca.ui.sreensapp.adapter.OnClickListener
import com.ganawin.mifinca.ui.sreensapp.adapter.TerneroSreenAdapter
import com.google.android.material.dialog.MaterialAlertDialogBuilder
import com.google.firebase.auth.FirebaseAuth
import com.google.firebase.storage.FirebaseStorage
import com.google.firebase.storage.StorageReference
import java.util.*
import kotlin.collections.HashMap

class TernerosFragment : Fragment(R.layout.fragment_terneros), OnClickListener {

    private lateinit var binding: FragmentTernerosBinding
    private val firebaseAuth by lazy { FirebaseAuth.getInstance() }
    private lateinit var userUIDColecction: String
    private lateinit var mStorageReference: StorageReference
    private val mapTernero: HashMap<String, Any> = hashMapOf()

    private val viewModel by viewModels<TerneroScreenViewModel> { TerneroScreenViewModelFactory(
        TerneroRepoImpl(TernerosDataSource())
    ) }


    override fun onViewCreated(view: View, savedInstanceState: Bundle?) {
        super.onViewCreated(view, savedInstanceState)

        userUid()
        binding = FragmentTernerosBinding.bind(view)
        binding.fbtnAddTernero.setOnClickListener { addTerneroFragment() }
        showTerneros()
    }


    private fun showTerneros() {
        viewModel.fetchListTerneros(userUIDColecction).observe(viewLifecycleOwner, Observer { result ->
            when(result){
                is Resource.Loading -> {}
                is Resource.Success -> {
                    binding.rvTerneros.adapter = TerneroSreenAdapter(result.data, this)
                    Log.d("FireTerneroError", "data: ${result.data}")
                }
                is Resource.Failure -> {
                    Log.d("FireTerneroError", "Error ${result.exception}")
                }
            }
        })
    }

    override fun onLongClick(document: String, itemsTernero: MutableList<String>) {
        binding.cvEditTernero.visibility = View.GONE
        binding.fbtnAddTernero.visibility = View.VISIBLE
        val items = resources.getStringArray(R.array.array_options_item)

        MaterialAlertDialogBuilder(requireContext())
                .setTitle(getString(R.string.dialog_OnLongClick))
                .setItems(items) { dialogInterface, i ->
                    when(i){
                        0 -> deleteTernero(document)
                        1 -> updateTernero(document, itemsTernero)
                    }
                }.show()

    }

    private fun updateTernero(document: String, itemsTernero: MutableList<String>) {
        binding.fbtnAddTernero.visibility = View.GONE
        binding.btnCancelUpdate.setOnClickListener {
            binding.cvEditTernero.visibility = View.GONE
            binding.fbtnAddTernero.visibility = View.VISIBLE
        }

        binding.btnDatePicker.setOnClickListener { showDatePickerDialog() }

        binding.cvEditTernero.visibility = View.VISIBLE
        binding.tvUpdateDate.text = itemsTernero[0]
        binding.etUpdateSexo.setText(itemsTernero[1])
        binding.etUpdateMadre.setText(itemsTernero[2])
        binding.etUpdatePadre.setText(itemsTernero[3])
        binding.etUpdateRaza.setText(itemsTernero[4])

        binding.btnChangePhoto.setOnClickListener { selectNewPhoto() }

        binding.btnConfirmChanges.setOnClickListener {

            val fechaNacimeinto = binding.tvUpdateDate.text.toString()
            val sexo = binding.etUpdateSexo.text.toString().trim()
            val madre = binding.etUpdateMadre.text.toString().trim()
            val padre = binding.etUpdatePadre.text.toString().trim()
            val raza = binding.etUpdateRaza.text.toString().trim()

            mapTernero["date_nacimiento"] = fechaNacimeinto
            mapTernero["sexo"] = sexo
            mapTernero["madre"] = madre
            mapTernero["padre"] = padre
            mapTernero["raza"] = raza

            confirmUpdateTernero(document, mapTernero)
        }

    }

    private fun selectNewPhoto() {
        val openPictureGalley = Intent(Intent.ACTION_PICK, MediaStore.Images.Media.EXTERNAL_CONTENT_URI)
        try {
            startActivityForResult(openPictureGalley, 3)
        } catch (e: ActivityNotFoundException){
            Toast.makeText(requireContext(), "No es posible abrir la galeria", Toast.LENGTH_SHORT)
                    .show()
        }
    }

    override fun onActivityResult(requestCode: Int, resultCode: Int, data: Intent?) {
        super.onActivityResult(requestCode, resultCode, data)
        if (requestCode == 3 && resultCode == Activity.RESULT_OK) {
            val imageSelectedUri: Uri = data?.data!!
            uploadNewPhoto(imageSelectedUri)
        }
    }

    private fun uploadNewPhoto(imageSelectedUri: Uri) {
        mStorageReference = FirebaseStorage.getInstance().reference

        val storageReference = mStorageReference.child("FotosTerneros")
                .child(userUIDColecction).child("${UUID.randomUUID()}")
        imageSelectedUri.let {
            storageReference.putFile(imageSelectedUri)
                    .addOnProgressListener {
                        binding.btnConfirmChanges.visibility = View.GONE
                        binding.pbEditTernero.visibility = View.VISIBLE
                    }
                    .addOnCompleteListener{
                        binding.pbEditTernero.visibility = View.GONE
                        binding.btnConfirmChanges.visibility = View.VISIBLE
                    }
                    .addOnSuccessListener {
                        Toast.makeText(requireContext(), "Foto sudida", Toast.LENGTH_SHORT).show()
                        it.storage.downloadUrl.addOnSuccessListener {
                            mapTernero["url_photo"] = it.toString()
                            //URL = it.toString()
                        }
                    }
                    .addOnFailureListener{
                        Toast.makeText(requireContext(), "Error al subir la foto", Toast.LENGTH_SHORT)
                                .show()
                    }
        }
    }

    private fun showDatePickerDialog() {
        val calendar = Calendar.getInstance()
        val year = calendar.get(Calendar.YEAR)
        val month = calendar.get(Calendar.MONTH)
        val day = calendar.get(Calendar.DAY_OF_MONTH)

        val datePickerDialog = DatePickerDialog(requireContext(), DatePickerDialog.OnDateSetListener{
            view, year, month, dayOfMonth ->
            val newFecha = "$dayOfMonth/${month+1}/$year"
            binding.tvUpdateDate.text = newFecha
        }, year, month, day)

        datePickerDialog.show()
    }

    fun deleteTernero(document: String){
        MaterialAlertDialogBuilder(requireContext())
                .setTitle(R.string.confirm_delete)
                .setPositiveButton(R.string.btn_delete) { dialogInterface, i ->
                    confirmDeleteTernero(document)
                }
                .setNegativeButton(R.string.btn_cancelar_delete_ternero, null)
                .show()
    }

    fun confirmUpdateTernero(document: String, mapTernero: HashMap<String, Any>){
        viewModel.updateItemTernero(userUIDColecction, document, mapTernero)
                .observe(viewLifecycleOwner, Observer { result ->
                    when(result){
                        is Resource.Loading -> {}
                        is Resource.Success -> {
                            Toast.makeText(requireContext(), result.data, Toast.LENGTH_SHORT)
                                    .show()
                            binding.cvEditTernero.visibility = View.GONE
                            binding.fbtnAddTernero.visibility = View.VISIBLE
                            //Log.d("ModificandoRegistros", "resultado: ${result.data}")
                            showTerneros()
                        }
                        is Resource.Failure -> {
                            Toast.makeText(requireContext(), "ha ocurrido un error", Toast.LENGTH_SHORT)
                                    .show()
                            //Log.d("ModificandoRegistros", "Error ${result.exception}")
                        }
                    }
                })
    }

    fun confirmDeleteTernero(document: String){
        viewModel.deleteItemTernero(userUIDColecction, document).observe(viewLifecycleOwner, Observer { result ->
            when(result){
                is Resource.Loading -> {}
                is Resource.Success -> {
                    Toast.makeText(requireContext(), result.data, Toast.LENGTH_SHORT)
                            .show()
                    //Log.d("EliminandoRegistros", "resultado: ${result.data}")
                    showTerneros()
                }
                is Resource.Failure -> {
                    Toast.makeText(requireContext(), "ha ocurrido un error", Toast.LENGTH_SHORT)
                            .show()
                    //Log.d("EliminandoRegistros", "Error ${result.exception}")
                }
            }
        })
    }

    private fun addTerneroFragment() {
        findNavController().navigate(R.id.action_ternerosFragment_to_addTerneroFragment,
            bundleOf("UID" to userUIDColecction))
    }

    private fun userUid() {
        firebaseAuth.currentUser?.let {
            userUIDColecction = "terneros${firebaseAuth.currentUser!!.uid}"
        }
    }
}