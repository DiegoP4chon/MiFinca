package com.ganawin.mifinca.ui.ventas

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
import com.ganawin.mifinca.data.remote.ventas.VentasDataSource
import com.ganawin.mifinca.databinding.FragmentVentasBinding
import com.ganawin.mifinca.domain.ventas.VentasRepoImpl
import com.ganawin.mifinca.presentation.ventas.VentasScreenViewModel
import com.ganawin.mifinca.presentation.ventas.VentasScreenViewModelFactory
import com.ganawin.mifinca.ui.ventas.adapter.OnClickListenerVentas
import com.ganawin.mifinca.ui.ventas.adapter.VentasSreenAdapter
import com.google.android.material.dialog.MaterialAlertDialogBuilder
import com.google.firebase.storage.FirebaseStorage
import com.google.firebase.storage.StorageReference

@Suppress("UNUSED_ANONYMOUS_PARAMETER")
class VentasFragment : Fragment(R.layout.fragment_ventas), OnClickListenerVentas {

    private lateinit var binding: FragmentVentasBinding
    private lateinit var userUIDColecction: String
    private lateinit var mStorageReference: StorageReference

    private val viewModel by viewModels<VentasScreenViewModel> { VentasScreenViewModelFactory(
        VentasRepoImpl(VentasDataSource())
    ) }

    override fun onViewCreated(view: View, savedInstanceState: Bundle?) {
        super.onViewCreated(view, savedInstanceState)

        binding = FragmentVentasBinding.bind(view)
        userUid()
        getListVentas()
        binding.fbtnAddVenta.setOnClickListener { screenAddVenta() }
    }

    private fun getListVentas() {
        viewModel.getListVenta(userUIDColecction).observe(viewLifecycleOwner, { result ->
            when(result){
                is Resource.Loading -> {}
                is Resource.Success -> {
                    binding.rvVentas.adapter = VentasSreenAdapter(result.data, this)
                }
                is Resource.Failure -> {
                    Toast.makeText(requireContext(), getString(R.string.error_consulta), Toast
                            .LENGTH_SHORT).show()
                }
            }
        })
    }

    override fun onCLickItemVenta(document: String, idPhoto: String) {
        val items = resources.getStringArray(R.array.array_options_item)

        MaterialAlertDialogBuilder(requireContext())
                .setTitle(getString(R.string.dialog_OnLongClick))
                .setItems(items) { dialogInterface, i ->
                    when(i){
                        0 -> deleteVenta(document, idPhoto)
                        1 -> updateRegistroVenta(document)
                    }
                }.show()
    }

    private fun deleteVenta(document: String, idPhoto: String) {
        MaterialAlertDialogBuilder(requireContext())
                .setTitle(R.string.confirm_delete)
                .setPositiveButton(R.string.btn_delete) { dialogInterface, i ->
                    confirmDeleteVenta(document, idPhoto)
                }
                .setNegativeButton(R.string.btn_cancelar_delete, null)
                .show()
    }

    private fun confirmDeleteVenta(document: String, idPhoto: String) {
        viewModel.deleteVenta(userUIDColecction, document).observe(viewLifecycleOwner, { result ->
            when(result){
                is Resource.Loading -> {}
                is Resource.Success -> {
                    Toast.makeText(requireContext(), getString(R.string.confirm_delete_register), Toast.LENGTH_SHORT)
                            .show()
                    deletePhoto(idPhoto)
                    getListVentas()
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
            val storageReference = mStorageReference.child("FotosVentas")
                    .child(userUIDColecction).child(idPhoto)
            storageReference.delete()
        }
    }

    private fun updateRegistroVenta(document: String) {
        findNavController().navigate(R.id.action_ventasFragment_to_addVentaFragment,
                bundleOf("document" to document , "UID" to userUIDColecction)
        )
    }

    private fun screenAddVenta() {
        findNavController().navigate(R.id.action_ventasFragment_to_addVentaFragment,
            bundleOf("UID" to userUIDColecction)
        )
    }

    private fun userUid() {
        userUIDColecction = "ventas${CurrentUser().userUid()}"
    }
}