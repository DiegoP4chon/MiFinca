package com.ganawin.mifinca.ui.terneros

import android.os.Bundle
import androidx.fragment.app.Fragment
import android.view.View
import android.widget.Toast
import androidx.core.os.bundleOf
import androidx.fragment.app.viewModels
import androidx.navigation.fragment.findNavController
import com.ganawin.mifinca.R
import com.ganawin.mifinca.core.CurrentUser
import com.ganawin.mifinca.core.Resource
import com.ganawin.mifinca.data.remote.terneros.TernerosDataSource
import com.ganawin.mifinca.databinding.FragmentTernerosBinding
import com.ganawin.mifinca.domain.terneros.TerneroRepoImpl
import com.ganawin.mifinca.presentation.ternero.TerneroScreenViewModel
import com.ganawin.mifinca.presentation.ternero.TerneroScreenViewModelFactory
import com.ganawin.mifinca.ui.terneros.adapter.OnClickListenerTernero
import com.ganawin.mifinca.ui.terneros.adapter.TerneroSreenAdapter
import com.google.android.material.dialog.MaterialAlertDialogBuilder
import com.google.firebase.storage.FirebaseStorage
import com.google.firebase.storage.StorageReference

@Suppress("UNUSED_ANONYMOUS_PARAMETER")
class TernerosFragment : Fragment(R.layout.fragment_terneros), OnClickListenerTernero {

    private lateinit var binding: FragmentTernerosBinding
    private lateinit var userUIDColecction: String
    private lateinit var mStorageReference: StorageReference

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
        viewModel.fetchListTerneros(userUIDColecction).observe(viewLifecycleOwner, { result ->
            when(result){
                is Resource.Loading -> {}
                is Resource.Success -> {
                    binding.rvTerneros.adapter = TerneroSreenAdapter(result.data, this)
                }
                is Resource.Failure -> {
                    Toast.makeText(requireContext(), getString(R.string.error_consulta), Toast.LENGTH_SHORT)
                            .show()
                }
            }
        })
    }

    override fun onLongClick(document: String, idPhoto: String) {
        val items = resources.getStringArray(R.array.array_options_item)

        MaterialAlertDialogBuilder(requireContext())
                .setTitle(getString(R.string.dialog_OnLongClick))
                .setItems(items) { dialogInterface, i ->
                    when(i){
                        0 -> deleteTernero(document, idPhoto)
                        1 -> updateTernero(document)
                    }
                }.show()

    }

    private fun updateTernero(document: String) {
        findNavController().navigate(R.id.action_ternerosFragment_to_addTerneroFragment,
                bundleOf("document" to document , "UID" to userUIDColecction))
    }

    private fun deleteTernero(document: String, idPhoto: String){
        MaterialAlertDialogBuilder(requireContext())
                .setTitle(R.string.confirm_delete)
                .setPositiveButton(R.string.btn_delete) { dialogInterface, i ->
                    confirmDeleteTernero(document, idPhoto)
                }
                .setNegativeButton(R.string.btn_cancelar_delete, null)
                .show()
    }

    private fun confirmDeleteTernero(document: String, idPhoto: String){
        viewModel.deleteItemTernero(userUIDColecction, document).observe(viewLifecycleOwner, { result ->
            when(result){
                is Resource.Loading -> {}
                is Resource.Success -> {
                    Toast.makeText(requireContext(), getString(R.string.confirm_delete_register), Toast.LENGTH_SHORT)
                            .show()
                    deletePhoto(idPhoto)
                    showTerneros()
                }
                is Resource.Failure -> {
                    Toast.makeText(requireContext(), getString(R.string.error_delete_register), Toast.LENGTH_SHORT)
                            .show()
                }
            }
        })
    }

    private fun deletePhoto(idPhoto: String) {
        mStorageReference = FirebaseStorage.getInstance().reference
        if(idPhoto != ""){
            val storageReference = mStorageReference.child("FotosTerneros")
                    .child(userUIDColecction).child(idPhoto)
            storageReference.delete()
        }
    }

    private fun addTerneroFragment() {
        findNavController().navigate(R.id.action_ternerosFragment_to_addTerneroFragment,
            bundleOf("UID" to userUIDColecction))
    }

    private fun userUid() {
        userUIDColecction = "terneros${CurrentUser().userUid()}"
    }
}