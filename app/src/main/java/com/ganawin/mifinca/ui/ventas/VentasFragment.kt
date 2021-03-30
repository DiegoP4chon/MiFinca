package com.ganawin.mifinca.ui.ventas

import android.os.Bundle
import android.util.Log
import androidx.fragment.app.Fragment
import android.view.View
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

class VentasFragment : Fragment(R.layout.fragment_ventas), OnClickListenerVentas {

    private lateinit var binding: FragmentVentasBinding
    private lateinit var userUIDColecction: String

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
                    Log.d("datosVentas", "data: ${result.data}")
                }
                is Resource.Failure -> {
                    Log.d("datosVentas", "Error ${result.exception}")
                }
            }
        })
    }

    override fun onCLickItemVenta(document: String) {
        MaterialAlertDialogBuilder(requireContext())
                .setTitle(R.string.dialog_update)
                .setPositiveButton(R.string.btn_update) { dialogInterface, i ->
                    updateRegistroVenta(document)
                }
                .setNegativeButton(R.string.btn_cancelar_delete_ternero, null)
                .show()
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