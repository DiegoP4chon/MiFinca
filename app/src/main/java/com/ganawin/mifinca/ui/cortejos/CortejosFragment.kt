package com.ganawin.mifinca.ui.cortejos

import android.os.Bundle
import android.util.Log
import androidx.fragment.app.Fragment
import android.view.View
import android.widget.Toast
import androidx.core.os.bundleOf
import androidx.fragment.app.viewModels
import androidx.navigation.fragment.findNavController
import com.ganawin.mifinca.R
import com.ganawin.mifinca.core.CurrentUser
import com.ganawin.mifinca.core.Resource
import com.ganawin.mifinca.data.remote.cortejos.CortejosDataSource
import com.ganawin.mifinca.databinding.FragmentCortejosBinding
import com.ganawin.mifinca.domain.cortejos.CortejosRepoImpl
import com.ganawin.mifinca.presentation.cortejos.CortejosScreenViewModel
import com.ganawin.mifinca.presentation.cortejos.CortejosScreenViewModelFactory
import com.ganawin.mifinca.ui.cortejos.adapter.CortejosScreenAdapter
import com.ganawin.mifinca.ui.cortejos.adapter.OnClickListenerCortejo
import com.google.android.material.dialog.MaterialAlertDialogBuilder
import com.google.firebase.storage.FirebaseStorage
import com.google.firebase.storage.StorageReference
import kotlin.math.log

class CortejosFragment : Fragment(R.layout.fragment_cortejos), OnClickListenerCortejo {

    private lateinit var binding: FragmentCortejosBinding
    private lateinit var userUIDColecction: String
    private lateinit var mStorageReference: StorageReference

    private val viewModel by viewModels<CortejosScreenViewModel> { CortejosScreenViewModelFactory(
            CortejosRepoImpl(CortejosDataSource())
    ) }

    override fun onViewCreated(view: View, savedInstanceState: Bundle?) {
        super.onViewCreated(view, savedInstanceState)

        binding = FragmentCortejosBinding.bind(view)
        userUid()
        getListCortejos()

        binding.fbtnAddCortejo.setOnClickListener { screenAddCortejo() }
    }

    private fun getListCortejos() {
        viewModel.getListCortejos(userUIDColecction).observe(viewLifecycleOwner, { result ->
            when(result){
                is Resource.Loading -> {}
                is Resource.Success -> {
                    binding.rvCortejos.adapter = CortejosScreenAdapter(result.data, this)
                }
                is Resource.Failure -> {
                    Toast.makeText(requireContext(), "Ha ocurrido un error", Toast.LENGTH_SHORT)
                            .show()
                }
            }
        })
    }

    override fun onClickItemCortejo(document: String, listIdPhotos: MutableList<String>) {
        val items = resources.getStringArray(R.array.array_options_item)

        MaterialAlertDialogBuilder(requireContext())
                .setTitle(getString(R.string.dialog_OnLongClick))
                .setItems(items) { dialogInterface, i ->
                    when(i){
                        0 -> deleteCortejo(document, listIdPhotos)
                        1 -> updateCortejo(document)
                    }
                }.show()
    }

    private fun deleteCortejo(document: String, listIdPhotos: MutableList<String>) {
        MaterialAlertDialogBuilder(requireContext())
                .setTitle(R.string.confirm_delete)
                .setPositiveButton(R.string.btn_delete) { dialogInterface, i ->
                    confirmDeleteCortejo(document, listIdPhotos)
                }
                .setNegativeButton(R.string.btn_cancelar_delete, null)
                .show()
    }

    private fun confirmDeleteCortejo(document: String, listIdPhotos: MutableList<String>) {
        viewModel.deleteCortejo(userUIDColecction, document).observe(viewLifecycleOwner, { result ->
            when(result){
                is Resource.Loading -> {}
                is Resource.Success -> {
                    Toast.makeText(requireContext(), result.data.toString(), Toast.LENGTH_SHORT)
                            .show()
                    deletePhotos(listIdPhotos)
                    getListCortejos()
                }
                is Resource.Failure -> {
                    Toast.makeText(requireContext(), "ha ocurrido un error", Toast.LENGTH_SHORT)
                            .show()
                }
            }
        })
    }

    private fun deletePhotos(listIdPhotos: MutableList<String>) {
        mStorageReference = FirebaseStorage.getInstance().reference
        for (idPhoto in listIdPhotos){
            if(idPhoto != ""){
                val storageReference = mStorageReference.child("FotosCortejos")
                        .child(userUIDColecction).child(idPhoto)
                storageReference.delete()
            }
        }
    }

    private fun updateCortejo(document: String) {
        findNavController().navigate(R.id.action_cortejosFragment_to_addCortejoFragment,
                bundleOf("document" to document , "UID" to userUIDColecction)
        )
    }

    private fun userUid() {
        userUIDColecction = "cortejos${CurrentUser().userUid()}"
    }

    private fun screenAddCortejo() {
        findNavController().navigate(R.id.action_cortejosFragment_to_addCortejoFragment,
            bundleOf("UID" to userUIDColecction))
    }
}